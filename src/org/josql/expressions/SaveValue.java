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

public class SaveValue extends ValueExpression
{

    private String name = null;
    private String acc = null;
    private Getter get = null;

    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	// See if the save value is already present.
	Object sv = q.getSaveValue (this.name);

	if (sv != null)
	{

	    if (this.acc != null)
	    {

		// Init the getter.
		try
		{

		    this.get = new Getter (this.acc,
					   sv.getClass ());

		} catch (Exception e) {
		    
		    throw new QueryParseException ("Unable to create dynamic getter from instance of type: " +
						   sv.getClass ().getName () + 
						   " for save value: " +
						   this.name +
						   " using accessor: " +
						   this.acc,
						   e);

		}

		return this.get.getType ();

	    }

	    return sv.getClass ();

	}

	// No idea what it could be...
	return Object.class;

    }

    public void init (Query  q)
    {

	// Nothing to do...

    }

    public String getName ()
    {

	return this.name;

    }

    public void setName (String name)
    {

	this.name = name;

    }

    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	Object v = q.getSaveValue (this.name);

	if (v == null)
	{

	    return v;

	}

	// See if we have an accessor...

	if ((this.acc != null)
	    &&
	    (this.get == null)
	   )
	{

	    try
	    {

		this.get = new Getter (this.acc,
				       v.getClass ());

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to create dynamic getter from instance of type: " +
						   v.getClass ().getName () + 
						   " for save value: " +
						   this.name +
						   " using accessor: " +
						   this.acc,
						   e);

	    }

	}

	if (this.get != null)
	{

	    try
	    {

		v = this.get.getValue (v);

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value from instance of type: " + 
						   v.getClass ().getName () +
						   " for save value: " + 
						   this.name + 
						   " using accessor: " + 
						   this.acc,
						   e);

	    }

	}

	return v;

    }

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

    public String getAccessor ()
    {

	return this.acc;

    }

    public void setAccessor (String acc)
    {

	this.acc = acc;

    }

    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.getValue (o,
			      q);

    }

    public String toString ()
    {

	StringBuffer buf = new StringBuffer ();

	buf.append ("@");
	buf.append (this.name);

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

    public boolean hasFixedResult (Query q)
    {

	// A save value cannot have a fixed result.
	return false;

    }

}
