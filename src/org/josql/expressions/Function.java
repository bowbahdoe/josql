/*
 * Copyright 2004-2007 Gary Bentley 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.josql.expressions;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.gentlyweb.utils.Getter;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

import org.josql.functions.NotFixedResults;

/**
 * This class represents a Function that can be "called" in JoSQL.  
 */
public class Function extends ValueExpression
{

    private String name = null;
    private List params = null;
    private Method function = null;
    private Object handler = null;
    private boolean fixedResult = true;
    private Object fixedValue = null;
    private String acc = null;
    private Getter get = null;

    public Getter getGetter ()
    {

	return this.get;

    }

    public String getAccessor ()
    {

	return this.acc;

    }

    public void setAccessor (String acc)
    {

	this.acc = acc;

    }

    /**
     * Get the expected return type from the function.  The exact class returned is 
     * dependent upon the function (Java method) that is being called.
     * 
     * @param q The Query object.
     * @return The class of the expected return type.
     */
    public Class getExpectedReturnType (Query  q)
    {

	if (this.get != null)
	{

	    return this.get.getType ();

	}

	return this.function.getReturnType ();

    }

    /**
     * This is a complex method that will initialise the function.
     * Firstly all of the "arguments" to the function are inited and then
     * their expected return types gained from calling: {@link Function#getExpectedReturnType(Query)}.
     * Then the function handlers, user-defined and then built-in are searched until they
     * find a match for the function name, ensure that it's a public method and that all the
     * arguments match, widening the match where necessary.
     *
     * @param q The Query object.
     * @throws QueryParseException If something goes wrong whilst initing the arguments to the 
     *                             function or if the function cannot be found.
     */
    public void init (Query  q)
                      throws QueryParseException
    {

	// Need to init the params (if present) first because the expected type may not be
	// present otherwise.
	if (this.params != null)
	{

	    int s = this.params.size ();

	    for (int i = 0; i < s; i++)
	    {

		Expression exp = (Expression) this.params.get (i);

		exp.init (q);

	    }

	}

	// Try and find the relevant method, first in the user-defined function handler (if present)
	// or the built-in handlers.
        this.findMethod (q);

	if (this.function == null)
	{

            Class[] ps = null;
    
            if (this.params != null)
            {
    
                int s = params.size (); 
    
                ps = new Class[s];
    
                for (int i = 0; i < s; i++)
                {
    
                    // Need to get the expected return type.
                    Expression exp = (Expression) params.get (i);
    
                    ps[i] = exp.getExpectedReturnType (q);
    
                }
    
            }

	    // Can't find the function.
	    throw new QueryParseException ("Unable to find function (method): \"" +
					   Utilities.formatSignature (this.name,
								      ps) + 
					   "\" in any user-defined function handlers or the default function handler");

	}

	// Now see if we have an accessor for the function.
	if (this.acc != null)
	{

            this.initAccessor ();

	}

	// A function has/can have a fixed result if all it's arguments
	// also have a fixed result, if there aren't any args then assume
	// it won't have a fixed result.
	if ((this.params != null)
            &&
            (!NotFixedResults.class.isAssignableFrom (this.function.getDeclaringClass ()))
           )
	{

	    for (int i = 0; i < this.params.size (); i++)
	    {

		Expression exp = (Expression) this.params.get (i);

		if (!exp.hasFixedResult (q))
		{

		    this.fixedResult = false;
		    break;

		}

	    }

	} else {

	    this.fixedResult = false;

	}

    }

    private void initAccessor ()
                               throws QueryParseException
    {

        // We have an accessor, see what the functions return type is.
        Class retType = this.function.getReturnType ();

        // Ensure that the function DOES have a return type.
        if (Void.TYPE.isAssignableFrom (retType))
        {

            // The return type is void, barf!
            throw new QueryParseException ("Function: " + 
                                           this + 
                                           " maps to method: " +
                                           this.function +
                                           " however methods return type is \"void\" and an accessor: " +
                                           this.acc +
                                           " has been defined.");

        }

        // See if the return type is an object.  If it is then defer trying to
        // get the accessor until run-time.  Have to do it this way since
        // type comparisons are a little pointless.
        if (!retType.getName ().equals (Object.class.getName ()))
        {

            // The return type is NOT java.lang.Object, so now try and get the 
            // accessor.
            try
            {

                this.get = new Getter (this.acc,
                                       retType);
                
            } catch (Exception e) {

                throw new QueryParseException ("Function: " +
                                               this +
                                               " maps to method: " +
                                               this.function +
                                               " and has accessor: " +
                                               this.acc +
                                               " however no valid accessor has been found in return type: " +
                                               retType.getName (),
                                               e);

            }

        }
        
    }

    private void getMethodFromHandlers (Query q,
                                        List  handlers)
                                        throws QueryParseException
    {

	int s = handlers.size ();

	TreeMap ms = new TreeMap ();

	for (int i = 0; i < s; i++)
	{

	    Object fh = handlers.get (i);

	    this.getMethods (fh.getClass (),
			     q,
			     ms);
		
	}

	// Get the one with the highest score.
	if (ms.size () > 0)
	{

	    this.function = (Method) ms.get (ms.lastKey ());
		
	    Class c = this.function.getDeclaringClass ();

	    // What a pain!
	    for (int i = 0; i < s; i++)
	    {

		Object o = handlers.get (i);

		if (o.getClass ().isAssignableFrom (c))
	        {

		   this.handler = o;

		}

	    }
		
        }
        
    }

    private void findMethod (Query  q)
                             throws QueryParseException
    {

        List fhs = q.getFunctionHandlers ();
        
        if (fhs != null)
        {
        
            this.getMethodFromHandlers (q,
                                        fhs);

        }
        
        if (this.function == null)
        {
            
            List dfhs = q.getDefaultFunctionHandlers ();
            
            if (dfhs != null)
            {
                
                this.getMethodFromHandlers (q,
                                            dfhs);
                
            }
            
        }
        
    }

    /**
     * Return the List of {@link Expression} objects that constitute the arguments
     * to the function, no guarantee is made here as to whether they have been inited.
     *
     * @return The List of {@link Expression} objects.
     */
    public List getParameters ()
    {

	return this.params;

    }

    public void setParameters (List ps)
    {

	this.params = ps;

    }

    public void setName (String name)
    {


	this.name = name;

    }

    public String getName ()
    {

	return this.name;

    }

    /**
     * Evaluate this function on the current object.  It should be noted that not
     * all functions will use the current object in their execution, functions from the
     * {@link org.josql.functions.GroupingFunctions} class are notable exceptions.
     *
     * @param o The current object.
     * @param q The Query object.
     * @return The result of evaluating the function.
     * @throws QueryExecutionException If something goes wrong during execution of the
     *                                 function or gaining the values to be used as arguments.
     */
    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	// See if we have a fixed result.
	if (this.fixedResult)
	{

	    // Evaluated once...
	    if (this.fixedValue != null)
	    {

		return this.fixedValue;

	    }

	}

	// Get the values for the parameters... if any...
	Object[] ps = null;

	if (this.params != null)
	{

	    int s = this.params.size ();

	    ps = new Object[s];

	    for (int i = 0; i < s; i++)
	    {

		Expression exp = (Expression) this.params.get (i);

	        if (Expression.class.isAssignableFrom (this.function.getParameterTypes ()[i]))
		{

		    // Leave this one alone.
		    ps[i] = exp;

		} else {

		    // Eval this expression.
		    try
		    {

			ps[i] = exp.getValue (o,
					      q);

		    } catch (Exception e) {
			
			throw new QueryExecutionException ("Unable to get parameter: " + 
							   i + 
							   " (\"" +
							   exp.toString () + 
							   "\") for function: " +
							   this.name,
							   e);
			
		    }

		}

	    }

	}

	Object v = null;

	try
	{

	    v = this.function.invoke (this.handler,
				      ps);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to execute function: " + 
					       this.name + 
					       " (\"" +
					       this.toString () + 
					       "\") with values: " +
					       Arrays.asList (ps),
					       e);

	}

	if (v != null)
	{

	    // See if we have an accessor.
	    if (this.acc != null)
	    {

		// See if we have a Getter.
		if (this.get == null)
		{

		    // No getter, so now try and init it.  This assumes that the return
		    // type won't ever change!
		    try
		    {

			this.get = new Getter (this.acc,
					       v.getClass ());
			
		    } catch (Exception e) {
			
			throw new QueryExecutionException ("Unable to create accessor for: " +
							   this.acc +
							   " from return type: " +
							   v.getClass ().getName () + 
							   " after execution of function: " +
							   this,
							   e);

		    }

		}

		try
		{

		    v = this.get.getValue (v);
		    
		} catch (Exception e) {
		    
		    throw new QueryExecutionException ("Unable to get value for accessor: " +
						       this.acc +
						       " from return type: " +
						       v.getClass ().getName () + 
						       " after execution of function: " +
						       this,
						       e);
		    
		}

	    }

	}

	if (this.fixedResult)
	{

	    this.fixedValue = v;

	}

	return v;

    }

    /**
     * Return whether the evaluation of this function (see: {@link #evaluate(Object,Query)})
     * will result in a <code>true</code> value.
     * See: {@link ArithmeticExpression#isTrue(Object,Query)} for details of how this is 
     * determined.
     *
     * @param o The current object.
     * @param q The Query object.
     * @return <code>true</code> if the function return value evaluates to <code>true</code>.
     * @throws QueryExecutionException If something goes wrong with evaluating the function.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	o = this.evaluate (o,
			   q);

	if (o == null)
	{
	    
	    return false;

	}

	if (Utilities.isNumber (o))
	{

	    return Utilities.getDouble (o) > 0;

	}

	if (o instanceof Boolean)
	{

	    return ((Boolean) o).booleanValue ();

	}

	// Not null so return true...
	return true;	

    }

    /**
     * Return a string representation of the function.
     * In the form: Name ( {@link Expression} [ , {@link Expression} ] )
     * 
     * @return A string representation of the function.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ();

	buf.append (this.name);
	buf.append ("(");

	if (this.params != null)
	{

	    for (int i = 0; i < this.params.size (); i++)
	    {

		Expression p = (Expression) this.params.get (i);

		buf.append (p);

		if (i < (this.params.size () - 1))
		{

		    buf.append (",");

		}

	    }

	}

	buf.append (")");

	if (this.acc != null)
	{

	    buf.append (".");
	    buf.append (this.acc);

	}

	if (this.isBracketed ())
	{

	    buf.insert (0,
			"(");

	    buf.append (")");

	}

	return buf.toString ();

    }

    /**
     * Return whether the function will return a fixed result, this only
     * occurs iff all the arguments to the function also return a fixed result.
     *
     * @param q The Query object.
     */
    public boolean hasFixedResult (Query q)
    {

	return this.fixedResult;

    }

    private int matchMethodArgs (Class[] methArgs,
				 Query   q)
                                 throws  QueryParseException
    {

	// The score here helps in argument resolution, a more specific argument
	// match (that is NOT expression in the method args) will score higher and
	// thus is a better match.
	int score = 0;

	for (int i = 0; i < methArgs.length; i++)
	{

	    Class c = methArgs[i];
	    
	    Expression exp = (Expression) this.params.get (i);

	    // See if the arg is object, which means "I can accept any type".
	    if (c.getClass ().getName ().equals (Object.class.getName ()))
	    {

		score += 1;

		continue;

	    }

	    // Now try and get the expected return type...
	    Class expC = exp.getExpectedReturnType (q);

	    if (expC == null)
	    {

		// Can't match this arg.
		continue;

	    } else {

		if (c.isAssignableFrom (expC))
		{

		    score += 2;

		    continue;

		}

	    }

	    if (Expression.class.isAssignableFrom (c))
	    {

		score += 1;

		// This is a match... i.e. the arg is an expression and thus supported
		// in a "special" way.
		continue;

	    }

	    if ((Utilities.isNumber (expC))
		&&
		((Utilities.isNumber (c))
		 ||
		 (c.getName ().equals (Object.class.getName ()))
		)
	       )
	    {

		score += 1;

		// This matches...
		continue;

	    }

	    if ((Utilities.isPrimitiveClass (c))
		&&
		(Utilities.isPrimitiveClass (expC))
	       )
	    {

		// It is a primitive class as well, so now see if they are compatible.
		if (Utilities.getPrimitiveClass (c).isAssignableFrom (Utilities.getPrimitiveClass (expC)))
		{

		    score += 1;

		    // They are assignable...
		    continue;

		}

	    }

	    // See if the expression return type is an object... this "may" mean
	    // that we can match and it may not, it will be determined at runtime.
	    if (expC.getName ().equals (Object.class.getName ()))
	    {

		score += 1;

		continue;

	    }

	    // If we are here then we can't match this arg type...
	    // No point checking any further...
	    return 0;

	}

	// All args can be matched.
	return score;

    } 

    private void getMethods (Class  c,
			     Query  q,
			     Map    matches)
                             throws QueryParseException
    {

	Method[] meths = c.getMethods ();

	for (int i = 0; i < meths.length; i++)
	{

	    Method m = meths[i];

	    if (!m.getName ().equals (this.name))
	    {

		continue;

	    }

	    // Make sure it's public...
	    if (!Modifier.isPublic (m.getModifiers ()))
	    {

		continue;

	    }

	    // Now check the args... sigh...
	    Class[] mpt = m.getParameterTypes ();

	    int ps = 0;
	    int fps = 0;

	    if (mpt != null)
	    {

		ps = mpt.length;

	    }

	    if (this.params != null)
	    {

		fps = this.params.size ();

	    }

	    if (ps != fps)
	    {

		continue;

	    }

            if (ps == 0)
            {
                
                matches.put (Integer.valueOf (0),
                             m);
                
                return;
                
            }

	    int score = this.matchMethodArgs (mpt,
					      q);

	    if (score > 0)
	    {

		matches.put (Integer.valueOf (score),
			     m);

	    }

	}

    }

}
