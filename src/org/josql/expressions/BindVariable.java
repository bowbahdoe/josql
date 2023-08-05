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
import java.util.Map;
import java.util.HashMap;

import com.gentlyweb.utils.Getter;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

/**
 * This class represents a "bind variable" used within a SQL statement.
 * A bind variable can be either:
 * <ul>
 *   <li>Named - a named bind variable is prefixed with ":", the value of the 
 *       variable is set in the {@link Query} object via: {@link Query#setVariable(String,Object)}.
 *       The parser used (javacc generated) is a little picky about "reserved keywords" and as
 *       such you cannot use the following as the names of bind variables: select, from, limit, 
 *       execute, on, all, results, where, having, order, by, group.  If you <b>must</b> use one of
 *       reserved names then suffix it with "$", i.e. "execute$", "limit$" and so on.</li>
 *   <li>Anonymous - an anonymous bind variable is represented as "?".  The value of the
 *       variable is set in the {@link Query} object via: {@link Query#setVariable(int,Object)}.
 *       Anonymous variables are assigned an internal integer index starting at 1 and they increase
 *       by 1.</li>
 *   <li>Special - there are 3 types of "special" bind variable:
 *     <ul>
 *       <li><b>:_currobj</b> - Indicates the current object within the execution scope.</li>
 *       <li><b>:_allobjs</b> - Indicates the current set of objects within the execution scope.</li>
 *       <li><b>:_query</b> - Indicates the Query object.</li>
 *       <li><b>:_grpby</b> - Indicates the Group By object.  If there are more than 1 group by 
 *           objects then :_grpby1 ... :_grpby2 ... :_grpbyX can be used to identify which
 *           group by object you mean.  Remember that the group by object only has meaning
 *           if there is a group by statement.</li>
 *       <li><b>:_grpbys</b> - Indicates the set of group bys, this will be a list of lists, one
 *           list for each group by key, and each group by column will be in that list.
 *       <li><b>:_groupby</b> - A synonym for <b>:_grpby</b>.</li> 
 *       <li><b>:_groupbys</b> - A synonym for <b>:_grpbys</b>.</li> 
 *     </ul>
 *     <p>
 *     Special bind variables are mainly used in function calls however they can also be used
 *     just about anywhere.
 *   </li>
 * </ul>
 * <h3>Examples</h3>
 * <pre>
 *   SELECT :_query,
 *          :_allobjs,
 *          :_currobj
 *   FROM java.lang.Object
 *
 *   SELECT name
 *   FROM   java.io.File
 *   WHERE  length(:_currobj, name) > :length
 *   AND    length > avg(:_query,:_allobjs,length)
 *   AND    path LIKE '%' + ?
 * </pre>
 * <h3>Accessors</h3>
 * <p>
 * It is also possible for bind variables (including the special variables) to have accessors.
 * For example:
 * <pre>
 *   SELECT :_query.variables
 *   FROM   java.lang.Object
 * </pre>
 * <p>
 * Would cause all the bind variables in the query to be returned.
 * Also, if the ? in the next query is an instance of <code>java.lang.String</code>.
 * <pre>
 *   SELECT ?.length
 *   FROM   java.lang.Object
 * </pre>
 */
public class BindVariable extends ValueExpression
{

    public static final String SPECIAL_NAME_PREFIX = "_";
    private static Map SPECIAL_VAR_NAMES;

    static
    {

	BindVariable.SPECIAL_VAR_NAMES = new HashMap ();
	BindVariable.SPECIAL_VAR_NAMES.put (Query.QUERY_BIND_VAR_NAME,
					    "");
	BindVariable.SPECIAL_VAR_NAMES.put (Query.CURR_OBJ_VAR_NAME,
					    "");
	BindVariable.SPECIAL_VAR_NAMES.put (Query.ALL_OBJS_VAR_NAME,
					    "");
	BindVariable.SPECIAL_VAR_NAMES.put (Query.GRPBY_OBJ_VAR_NAME,
					    "");
	BindVariable.SPECIAL_VAR_NAMES.put (Query.GRPBY_OBJ_VAR_NAME_SYNONYM,
					    "");
	BindVariable.SPECIAL_VAR_NAMES.put (Query.PARENT_BIND_VAR_NAME,
					    "");

    }

    private String name = null;
    private Object val = null;
    private boolean anon = false;
    private String acc = null;
    private Getter get = null;
    private boolean groupByVar = false;
    private int groupByInd = 0;

    public boolean equals (Object o)
    {

	if (o == null)
	{

	    return false;

	}

	if (!(o instanceof BindVariable))
	{

	    return false;

	}

	BindVariable b = (BindVariable) o;

	if ((b.getName () != null)
	    &&
	    (this.name != null)
	    &&
	    (b.getName ().equals (this.name))
	   )
	{

	    if ((this.acc != null)
		&&
		(b.getAccessor () != null)
		&&
		(b.getAccessor ().equals (this.acc))
	       )
	    {

		return true;

	    }

	}

	return false;

    }

    public String getAccessor ()
    {

	return this.acc;

    }

    public void setAccessor (String a)
    {

	this.acc = a;

    }

    /**
     * Get the expected return type.
     * The exact class returned here is dependent (obviously) on what the bind variable
     * represents.  Wherever possible it attempts to get the most specific class for the
     * variable.  It is generally better to set the variables prior to executing the:
     * {@link Query#parse(String)} method to ensure that the correct class is returned here.
     *
     * @param q The Query object.
     * @return The return type class or <code>java.lang.Object.class</code> if the class
     *         cannot be determined.
     * @throws QueryParseException If the type cannot be determined.
     */
    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	if (this.get != null)
	{

	    return this.get.getType ();

	}

	if (this.val != null)
	{

	    return this.val.getClass ();

	}

	return q.getVariableClass (this.name);

    }

    private void initForGroupByName (String n,
                                     Query  q)
                                     throws QueryParseException
    {
        
        List grpBys = q.getGroupByColumns ();

        // This is a group by... check for a group by clause.
        if (grpBys == null)
        {

            throw new QueryParseException ("Use of special group by object bind variable: " +
                                           name + 
                                           " is not valid when there are no GROUP BY clauses defined."); 

        }

        String rest = "";

        // Trim it down to see if there is a number.
        if (n.startsWith (Query.GRPBY_OBJ_VAR_NAME))
        {

            rest = n.substring (Query.GRPBY_OBJ_VAR_NAME.length ());

        }

        if (n.startsWith (Query.GRPBY_OBJ_VAR_NAME_SYNONYM))
        {
        
            rest = n.substring (Query.GRPBY_OBJ_VAR_NAME_SYNONYM.length ());

        }

        int grpbyind = 1;

        if (rest.length () > 0)
        {

            // Should be a number.
            try
            {

                grpbyind = Integer.parseInt (rest);

            } catch (Exception e) {

                throw new QueryParseException ("Special bind variable name: " + 
                                               this.name + 
                                               " is not valid, expected an integer number at end of name for indexing into GROUP BYs.");

            }

            if (grpbyind < 1)
            {

                throw new QueryParseException ("Special bind variable name: " + 
                                               this.name + 
                                               " is not valid, integer to index GROUP BYs must be a minimum of 1.");		    

            }

            if (grpbyind > (grpBys.size ()))
            {

                throw new QueryParseException ("Special bind variable name: " + 
                                               this.name + 
                                               " is not valid, integer references GROUP BY: " + 
                                               grpbyind + 
                                               " however there are only: " + 
                                               grpBys.size () + 
                                               " GROUP BYs defined.");

            }

        }

        this.groupByVar = true;
        this.groupByInd = grpbyind;
        
    }

    /**
     * Initialises this bind variable.
     * If the bind variable is "anonymous" then a name is gained for it from the
     * Query object.  If there is a value then it is gained from the Query object and
     * cached.  Also, if there is an accessor defined then it is inited where possible.
     *
     * @param q The Query object.
     * @throws QueryParseException If the bind variable cannot be inited.
     */
    public void init (Query  q)
	              throws QueryParseException
    {

	if (this.anon)
	{

	    this.name = q.getAnonymousBindVariableName ();

	}

	String n = this.name.toLowerCase ();

	if ((n.startsWith (Query.GRPBY_OBJ_VAR_NAME))
	    ||
	    (n.startsWith (Query.GRPBY_OBJ_VAR_NAME_SYNONYM))
	   )
	{

            this.initForGroupByName (n,
                                     q);

	} else {

	    if (n.startsWith (BindVariable.SPECIAL_NAME_PREFIX))
	    {

		// Make sure it's valid.
		if (!BindVariable.SPECIAL_VAR_NAMES.containsKey (n))
		{

		    throw new QueryParseException ("Bind variable name: " +
						   name +
						   " is not valid, bind variable names starting with: " +
						   BindVariable.SPECIAL_NAME_PREFIX + 
						   " are reserved, and must be one of: " +
						   BindVariable.SPECIAL_VAR_NAMES.keySet ());

		}

	    }

	}

	// See if we already have this bind variable set...
	this.val = q.getVariable (this.name);

	// See if we have a "trailing" accessor.
	if ((this.val != null)
	    &&
	    (this.acc != null)
	   )
	{

	    this.initGetter (this.val);

	    try
	    {

		this.val = this.get.getValue (this.val);

	    } catch (Exception e) {

		throw new QueryParseException ("Unable to get value from accessor: " +
					       this.acc +
					       " and class: " + 
					       this.val.getClass ().getName () + 
					       " from bind variable: " + 
					       this.name,
					       e);

	    }

	}

	// See if we can init the getter... there are times when it 
	// is possible even if the bind variable isn't available yet.
	if ((this.acc != null)
	    &&
	    (this.get == null)
	   )
	{

	    // Not over keen on this method but it will do for now...
	    // It precludes the init occurring if we are working on java.lang.Object
	    // objects... but how many times will that happen?
	    Class c = q.getVariableClass (this.name);

	    if (!c.isInstance (new Object ()))
	    {

		// Init the getter.
		this.initGetter (c);

	    }

	}

    }

    public String getName ()
    {

	return this.name;

    }

    public boolean isAnonymous ()
    {

	return this.anon;

    }

    public void setAnonymous (boolean v)
    {

	this.anon = v;

    }

    public void setName (String name)
    {

	this.name = name;

    }

    private void initGetter (Object o)
    {

	// Get the class for the value.
	Class c = o.getClass ();

	this.initGetter (c);
	
    }

    private void initGetter (Class c)
    {

	this.get = new Getter (this.acc,
			       c);
	
    }

    /**
     * Gets the value of this bind variable.  
     *
     * @param o The current object.  Note that this variable isn't used in this method.
     * @param q The Query object.
     * @return The value.
     * @throws QueryExecutionException If something goes wrong during the accessing
     *                                 of the value.
     */
    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	if (this.groupByVar)
	{

	    o = q.getGroupByVariable (this.groupByInd);

	} else {

	    o = q.getVariable (this.name);

	}

	if ((this.acc != null)
	    &&
	    (this.get == null)
	    &&
	    (o != null)
	   )
	{

	    // Unable to get the accessor...
	    this.initGetter (o);

	} 

	if (this.get != null)
	{

	    try
	    {

		o = this.get.getValue (o);

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value for accessor: " +
						   this.acc + 
						   ", class: " + 
						   this.get.getBaseClass ().getName () + 
						   " from bind variable: " + 
						   this.name,
						   e);

	    }

	}

	return o;

    }

    /**
     * Returns whether the value of this bind variable represents a <code>true</code>
     * value.  See: {@link ArithmeticExpression#isTrue(Object,Query)} for details of how
     * the return value is determined.
     *
     * @param o The current object.  Not used in this method.
     * @param q The Query object.
     * @return <code>true</code> if the bind variable evaluates to <code>true</code>.
     * @throws QueryExecutionException If a problem occurs during evaluation.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	o = this.getValue (o,
			   q);

	if (o == null)
	{
	    
	    return false;

	}

	if (Utilities.isNumber (o))
	{

	    return Utilities.getDouble (o) > 0;

	}

	// Not null so return true...
	return true;

    }

    /**
     * Evaluates the value of this bind variable.  This is just a thin-wrapper around:
     * {@link #getValue(Object,Query)}.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object.
     * @return The value of this bind variable.
     * @throws QueryExecutionException If there is a problem getting the value.
     */
    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.getValue (o,
			      q);

    }

    /**
     * Returns a string version of this bind variable.
     * Returns in the form: ? | ":" [ "_" ] Name
     *
     * @return A string version of the bind variable.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ();

	if (this.anon)
	{

	    buf.append ("?");

	} else {

	    buf.append (":");
	    buf.append (this.name);

	}

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
     * Will always return false since a bind variable cannot be fixed.
     *
     * @param q The Query object.
     * @return <code>false</code> always.
     */
    public boolean hasFixedResult (Query q)
    {

	// A bind variable cannot have a fixed result.
	return false;

    }

}
