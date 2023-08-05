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

import com.gentlyweb.utils.Getter;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

/**
 * Represents an "accessor" into an object.  An accessor is basically a dot separated list
 * of method names, such as: <code>myObj.id.name</code>.
 * <p>
 * All of the methods referenced must have no arguments and be "public" in the referring class.
 * You can use either the actual method name or the JavaBean naming convention.
 * Thus: <code>myObj.id.name</code> might also be represented as: <code>getMyObj.getId.getName</code>.
 */
public class Accessor extends ValueExpression
{

    private String acc = null;
    private Getter get = null;

    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	return this.get.getType ();

    }

    public void init (Query  q)
	              throws QueryParseException
    {

	// Now init the getter.
	try
	{

	    this.get = new Getter (this.acc,
				   q.getFromObjectClass ());

	} catch (Exception e) {

	    throw new QueryParseException ("Unable to create getter: " + 
					   this.acc,
					   e);

	}

    }

    public String getAccessor ()
    {

	return this.acc;

    }

    public void setAccessor (String a)
    {

	this.acc = a;

    }

    public Getter getGetter ()
    {

	return this.get;

    }

    public void setName (String name)
    {

	this.acc = name;

    }

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

    public boolean hasFixedResult (Query q)
    {

	// Well duh...
	return false;

    }

    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	try
	{

	    return this.get.getValue (o);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to get value from: " +
					       this + 
					       " passed in object type: " +
					       o.getClass ().getName () +
					       " expecting: " +
					       this.get.getType ().getName (),
					       e);

	}

    }

    public boolean equals (Object o)
    {

	if (o == null)
	{

	    return false;

	}

	if (!(o instanceof Accessor))
	{

	    return false;

	}

	Accessor a = (Accessor) o;

	return this.acc.equals (a.getAccessor ());

    }

    public String toString ()
    {

	if (this.isBracketed ())
	{

	    return "(" + this.acc + ")";

	}

	return this.acc + "[detail: " + this.get + "]";

    }

}
