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

import java.util.Map;
import java.util.Collection;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

public class SelectItemExpression extends Expression
{

    public static final String KEY = "key";
    public static final String VALUE = "value";

    private Expression exp = null;
    private boolean fixedResult = false;
    private Class addItemsType = null;
    private String addItemsMapType = null;
    private String alias = null;

    public String getAlias ()
    {

	return this.alias;

    }

    public void setAlias (String a)
    {

	this.alias = Utilities.stripQuotes (a);

    }

    public boolean hasFixedResult (Query q)
    {

	return this.fixedResult;

    }

    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	return this.exp.getExpectedReturnType (q);

    }

    public void init (Query  q)
	              throws QueryParseException
    {

	this.exp.init (q);

	this.fixedResult = this.exp.hasFixedResult (q);

	/*
	if (this.isAddItemsFromCollectionOrMap ())
	{

	    // Get the expected return type.
	    Class expC = this.getExpectedReturnType (q);

	    if (expC.getName ().equals (Object.class.getName ()))
	    {

		// Defer until later.
		return;

	    }

	    if (!this.addItemsType.isAssignableFrom (expC))
	    {

		throw new QueryParseException ("Expected return type from select clause column will be: " +
					       expC.getName () +
					       ", however expecting to add items to results from type: " +
					       this.addItemsType.getName ());

	    }

	}
	*/

    }

    public void setAddMapType (String t)
    {

	this.addItemsMapType = t;

    }

    public Collection getAddItems (Object v)
    {

	if (Map.class.isAssignableFrom (this.addItemsType))
	{

	    boolean k = this.addItemsMapType.equals (SelectItemExpression.KEY);

	    Map m = (Map) v;

	    if (k)
	    {

		return m.keySet ();

	    }

	    return m.values ();

	}

	if (v instanceof Collection)
	{

	    return (Collection) v;

	}

	// This is a nasty hack but is "ok" for now, fix in a later version...
	java.util.List l = new java.util.ArrayList ();
	l.add (v);

	return l;

    }

    public boolean isAddItemsFromCollectionOrMap ()
    {

	return this.addItemsType != null;

    }

    public void setAddItemsType (Class c)
    {

	this.addItemsType = c;

    }

    public Expression getExpression ()
    {

	return this.exp;

    }

    public void setExpression (Expression exp)
    {

	this.exp = exp;

    }

    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	return this.exp.isTrue (o,
				q);

    }

    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.exp.getValue (o,
				  q);

    }

    public String toString ()
    {

	StringBuffer b = new StringBuffer ();

	if (this.isAddItemsFromCollectionOrMap ())
	{

	    b.append ("[*");

	    if (this.addItemsMapType != null)
	    {

		b.append (", ");
		b.append (this.addItemsMapType);

	    }

	    b.append ("] ");

	}

	b.append (this.exp);

	if (this.alias != null)
	{

	    b.append (" ");
	    b.append (this.alias);

	}

	return b.toString ();

    }

}
