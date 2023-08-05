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
package org.josql.functions;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import com.gentlyweb.utils.Getter;

import org.josql.QueryExecutionException;

import org.josql.expressions.Expression;

public class MiscellaneousFunctions extends AbstractFunctionHandler implements NotFixedResults
{

    public static final String HANDLER_ID = "_internal_misc";

    private Map accessorCache = new HashMap ();
    private Map getMethodCache = new HashMap ();

    private Random rand = new Random ();

    /**
     * Return the current date.
     * 
     * @param zeroTime If set to <code>true</code> then the date returned will have it's time fields
     *                 set to zero.
     * @return A date object.
     */
    public Date now (boolean zeroTime)
    {

	Date d = null;

	if (zeroTime)
	{

	    Calendar c = Calendar.getInstance ();

	    c.set (Calendar.HOUR_OF_DAY,
		   0);
	    c.set (Calendar.MINUTE,
		   0);
	    c.set (Calendar.SECOND,
		   0);
	    c.set (Calendar.MILLISECOND,
		   0);

	    d = c.getTime ();

	} else {

	    d = new Date ();

	}

	return d;

    }

    public void cache (List   allobjs,
                       Getter get)
	               throws QueryExecutionException
    {

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    try
	    {

		this.q.setSaveValue (o,
				     get.getValue (o));

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value from accessor: " + 
						   get +
						   " from object: " + 
						   i,
						   e);

	    }

	}

    }

    public void cache (List       allobjs,
                       Expression exp)
	               throws     QueryExecutionException
    {

	Object co = this.q.getCurrentObject ();

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    try
	    {

		this.q.setSaveValue (o,
				     exp.getValue (o,
						   q));

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value from expression: " + 
						   exp +
						   " from object: " + 
						   i,
						   e);

	    }

	}

	this.q.setCurrentObject (co);

    }

    public double abs (Number d)
    {

	return Math.abs (d.doubleValue ());

    }

    public int random ()
    {

	return this.rand.nextInt ();

    }

    public int random (Number n)
    {

	return this.rand.nextInt (n.intValue ());

    }

    public double randomDouble ()
    {

	return this.rand.nextDouble ();

    }

    public Object saveValue (Object saveValueName)
    {

	return this.q.getSaveValue (saveValueName);

    }

    public Object savevalue (Object saveValueName)
    {

	return this.q.getSaveValue (saveValueName);

    }

    public Object save_value (Object saveValueName)
    {

	return this.q.getSaveValue (saveValueName);

    }

    public String fileExtension (Object f)
    {

	if (f == null)
	{

	    return null;

	}

	String n = null;

	if (f instanceof String)
	{

	    n = (String) f;

	}

	if (f instanceof File)
	{

	    File fi = (File) f;

	    if (fi.isDirectory ())
	    {

		return null;

	    }

	    n = ((File) f).getName ();

	}

	return n.substring (n.lastIndexOf ('.') + 1);

    }

    /**
     * Call the specified accessor on the object. 
     *
     * @param oExp The expression to use to evaluate to get the object.
     * @param accExp The expression that is evaluated to get the accessor.
     * @return The value returned from the accessor.
     * @throws Exception If there is something wrong.
     */
    public Object accessor (Expression oExp,
			    Expression accExp)
	                    throws     Exception
    {

	// Get the value for the object.
	Object o = null;

	try
	{

	    o = oExp.getValue (this.q.getCurrentObject (),
			       this.q);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to evaluate expression: " +
					       oExp +
					       " to get object.");

	}

	Object a = null;

	try
	{

	    a = accExp.getValue (this.q.getCurrentObject (),
				 this.q);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to evaluate expression: " +
					       accExp +
					       " to get accessor.");

	}

	if (a == null)
	{

	    throw new QueryExecutionException ("Accessor expression: " +
					       accExp + 
					       " evaluates to null for object: " +
					       o +
					       " returned from expression: " +
					       oExp);

	}

	// Co-erce to a string.
	return this.accessor (o,
			      a.toString ());

    }    

    /**
     * Get a property from the current object using the "get" method, if one exists, the <b>name</b> value
     * will be used as the property name.
     *
     * @param o The object to call the "get(String)" method on.
     * @param name The name of the property to retrieve.
     * @return The value of the parameter.
     * @throws Exception If there is something wrong.
     */
    public Object get (String name)
                       throws Exception
    {
        
        return this.get (this.q.getCurrentObject (),
                         name);
        
    }

    /**
     * Get a property from the object using the "get" method, if one exists, the <b>name</b> value
     * will be used as the property name.
     *
     * @param o The object to call the "get(String)" method on.
     * @param name The name of the property to retrieve.
     * @return The value of the parameter.
     * @throws Exception If there is something wrong.
     */
    public Object get (Object o,
                       String name)
                       throws Exception
    {
        
	if (o == null)
	{

	    return null;

	}

        String cn = o.getClass ().getName ();

	// See if we already have the getter.
	Method m = (Method) this.accessorCache.get (cn);

	if (m == null)
	{

            Class[] argTypes = { String.class };

            try
            {
                m = o.getClass ().getMethod ("get",
                                             argTypes);
            
            } catch (Exception e) {
                
                throw new QueryExecutionException ("No get method taking a single string parameter found in: " +
                                                   cn);
            
            }

            this.getMethodCache.put (cn,
                                     m);

	}

        Object parms[] = { name };

        return m.invoke (o,
                         parms);
        
    }

    /**
     * Call the specified accessor on the object. 
     *
     * @param o The object to call the accessor on.
     * @param acc The accessor.
     * @return The value returned from the accessor.
     * @throws Exception If there is something wrong.
     */
    public Object accessor (Object o,
			    String acc)
	                    throws Exception
    {

	if (o == null)
	{

	    return null;

	}

	// See if we already have the getter.
	Getter g = (Getter) this.accessorCache.get (o.getClass ().getName () + acc);

	if (g == null)
	{

	    // Create a new Getter for the class of the object.
	    g = new Getter (acc,
			    o.getClass ());

	    this.accessorCache.put (o.getClass ().getName () + acc,
				    g);

	}

	return g.getValue (o);

    }

    public Object ifThen (Expression ifcond,
			  Expression thenVal)
	                  throws     QueryExecutionException
    {

	if (ifcond.isTrue (this.q.getCurrentObject (),
			   this.q))
	{

	    return thenVal.getValue (this.q.getCurrentObject (),
				     this.q);

	}

	return null;

    }

    public Object ifThenElse (Expression ifcond,
			      Expression thenVal,
			      Expression elseVal)
	                      throws     QueryExecutionException
    {

	Object i = this.ifThen (ifcond,
				thenVal);

	if (i == null)
	{

	    return elseVal.getValue (this.q.getCurrentObject (),
				     this.q);

	}

	return i;

    }

    public Object eval (Expression exp)
	                throws QueryExecutionException
    {

	return exp.getValue (this.q.getCurrentObject (),
			     this.q);

    }

    /**
     * Evaluates the <b>type</b> expression to produce a object whose type
     * should be compared against the class gained from evaluation of the 
     * <b>clazz</b> expression.
     * In effect the following is performed:
     * <pre>
     *   obj.getValue (q.getCurrentObject (), q)
     *     instanceof clazz.getValue (q.getCurrentObject (), q).getClass ()
     * </pre>
     * <p>
     * This is really just a thin wrapper around {@link Class#isInstance(Object)}.
     *
     * @param obj The expression that represents the object to
     *            against.
     * @param clazz The expression that represents the class of the type
     *              to compare against.
     * @throws QueryExecutionException If either of the expressions can't
     *         be evaluated.
     */
    public Boolean instanceOf (Expression obj,
			       Expression clazz)
	                       throws     QueryExecutionException
    {

	return Boolean.valueOf (clazz.getValue (this.q.getCurrentObject (),
						q).getClass ().isInstance (obj.getValue (this.q.getCurrentObject (),
											 q)));

    }

}
