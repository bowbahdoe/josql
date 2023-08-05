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

import java.util.*;

import java.lang.reflect.Constructor;

import org.josql.internal.Setter;
import org.josql.internal.Utilities;

import org.josql.Query;
import org.josql.QueryParseException;
import org.josql.QueryExecutionException;

public class NewObjectExpression extends ValueExpression
{

    private String cn = null;
    private Class clazz = null;
    private List constructorArgs = null;
    private Map intoExps = null;
    private Constructor constructor = null;
    private Object[] conParms = null;
    private int argsSize = -1;

    public void addIntoExpression (Expression exp,
				   String     setter)
    {

	if (this.intoExps == null)
	{

	    this.intoExps = new LinkedHashMap ();

	}

	this.intoExps.put (exp,
			   setter);

    }

    public void setConstructorArgs (List exps)
    {

	this.constructorArgs = exps;

	this.argsSize = exps.size () - 1;

    }

    public void setClassName (String c)
    {

	this.cn = c;

    }

    public boolean hasFixedResult (Query q)
    {

	return false;

    }

    public Class getExpectedReturnType (Query q)
	                                throws QueryParseException
    {

	return this.clazz;

    }

    public void init (Query q)
	              throws QueryParseException
    {

	// Load the class.
	try
	{

	    // Load via the custom (if present) classloader.
	    this.clazz = q.getClassLoader ().loadClass (this.cn);

	} catch (Exception e) {

	    throw new QueryParseException ("Unable to load class: " +
					   this.cn,
					   e);

	}

	Class[] conTypes = null;

	// Init all the constructor arg expressions, if provided.
	if (this.constructorArgs != null)
	{

	    conTypes = new Class [this.constructorArgs.size ()];

	    this.conParms = new Object[this.constructorArgs.size ()];

	    for (int i = 0; i < this.constructorArgs.size (); i++)
	    {

		Expression exp = (Expression) this.constructorArgs.get (i);

		exp.init (q);

		conTypes[i] = exp.getExpectedReturnType (q);

	    }

	}

	TreeMap tm = new TreeMap ();

	Constructor[] cons = this.clazz.getConstructors ();

	for (int i = 0; i < cons.length; i++)
	{

	    int score = Utilities.matchMethodArgs (cons[i].getParameterTypes (),
						   conTypes);

	    if (score > 0)
	    {

		tm.put (Integer.valueOf (score),
			cons[i]);

	    }

	}

	if (tm.size () > 0)
	{

	    this.constructor = (Constructor) tm.get (tm.lastKey ());

	}

	// Now try and find the constructor.
	if (this.constructor == null)
	{

	    throw new QueryParseException ("Unable to find constructor: " +
					   Utilities.formatSignature (this.clazz.getName (),
								      conTypes));

	}

	// Now init any setters.
	if (this.intoExps != null)
	{

	    Iterator iter = this.intoExps.keySet ().iterator ();

	    Class[] pts = new Class[1];

	    while (iter.hasNext ())
	    {

		Expression exp = (Expression) iter.next ();

		String setName = (String) this.intoExps.get (exp);

		exp.init (q);

		pts[0] = exp.getExpectedReturnType (q);

		// Create the Setter.
		Setter set = null;

		try
		{

		    set = new Setter (setName,
				      this.clazz,
				      pts);

		} catch (Exception e) {

		    throw new QueryParseException ("Unable to create setter for: " +
						   setName,
						   e);

		}

		this.intoExps.put (exp,
				   set);

	    }

	}

    }

    /**
     * Always return <code>true</code> because a new object is being created and thus
     * will be unequal to null.
     *
     * @param o The object currently in "scope".
     * @param q The Query object.
     * @return <code>true</code> always.
     */
    public boolean isTrue (Object o,
			   Query  q)
    {

	return true;

    }

    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.getValue (o,
			      q);

    }

    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	// Need to create a new object.
	if (this.constructorArgs != null)
	{

	    for (int i = this.argsSize; i > -1; i--)
	    {

		Expression exp = (Expression) this.constructorArgs.get (i);

		try
		{

		    this.conParms[i] = exp.getValue (o,
						     q);

		} catch (Exception e) {

		    throw new QueryExecutionException ("Unable to evaluate constructor argument expression: " +
						       exp,
						       e);

		}

	    }

	}

	// Execute the constructor.
	Object obj = null;

	try
	{

	    obj = this.constructor.newInstance (Utilities.convertArgs (this.conParms,
								       this.constructor.getParameterTypes ()));

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to create new instance of: " +
					       this.clazz.getName () +
					       " using constructor: " +
					       this.constructor +
					       ", passing parameters: " +
					       Arrays.toString (this.conParms),
					       e);

	}

	// See if we have any setters.
	if (this.intoExps != null)
	{

	    Iterator iter = this.intoExps.keySet ().iterator ();

	    while (iter.hasNext ())
	    {

		Expression exp = (Expression) iter.next ();

		Setter set = (Setter) this.intoExps.get (exp);

		Object so = null;

		// Eval the expression.
		try
		{

		    so = exp.getValue (o,
				       q);

		} catch (Exception e) {

		    throw new QueryExecutionException ("Unable to evaluate expression: " +
						       exp + 
						       " for setter: " +
						       set +
						       " on class: " +
						       this.clazz.getName (),
						       e);

		}

		try
		{

		    set.setValue (obj,
				  so);

		} catch (Exception e) {

		    String cn = null + "";

		    if (so != null)
		    {

			cn = so.getClass ().getName ();

		    }

		    throw new QueryExecutionException ("Unable to set value of type: " +
						       cn +
						       " in object of type: " +
						       this.clazz.getName () + 
						       " using setter: " +
						       set +
						       " (value is result of expression: " +
						       exp +
						       ")",
						       e);

		}

	    }

	}

	return obj;

    }

    public String toString ()
    {

	StringBuffer b = new StringBuffer ("new ");
	b.append (this.clazz.getName ());
	b.append (" (");
	
	if (this.constructorArgs != null)
	{

	    for (int i = 0; i < this.constructorArgs.size (); i++)
	    {

		Expression exp = (Expression) this.constructorArgs.get (i);

		b.append (exp);

		if (i < this.constructorArgs.size () - 1)
		{

		    b.append (", ");

		}

	    }

	}

	b.append (")");

	if (this.intoExps != null)
	{

	    b.append (" {");

	    Iterator iter = this.intoExps.keySet ().iterator ();

	    while (iter.hasNext ())
	    {

		Expression exp = (Expression) iter.next ();

		b.append (exp);

		b.append (" -> ");

		Object obj = this.intoExps.get (exp);

		if (obj instanceof Setter)
		{

		    Setter set = (Setter) obj;

		    b.append (set);

		}

		if (obj instanceof String)
		{

		    b.append ((String) obj);

		}

		if (iter.hasNext ())
		{

		    b.append (", ");

		}

	    }

	    b.append ("}");

	}

	return b.toString ();

    }

}
