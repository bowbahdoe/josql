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
package org.josql.internal;

import java.util.List;
import java.util.ArrayList;

import org.josql.expressions.ValueExpression;

import org.josql.Query;
import org.josql.QueryParseException;
import org.josql.QueryExecutionException;

public class Limit
{

    private ValueExpression start = null;
    private ValueExpression rowsCount = null;

    public Limit ()
    {

    }

    public void init (Query  q)
	              throws QueryParseException
    {

	// Init the value expressions...
	if (this.start != null)
	{

	    this.start.init (q);

	    // Should probably check to ensure that accessors aren't used... since
	    // we don't have a "current object" to work on!.
	    Class c = this.start.getExpectedReturnType (q);

	    if (!Utilities.isNumber (c))
	    {

		throw new QueryParseException ("The expected return type of the start expression: \"" +
					       this.start +
					       "\" of the LIMIT clause is: " +
					       c.getName () + 
					       " however the expression when evaluated must return a numeric result.");

	    }

	}

	this.rowsCount.init (q);

	// Should probably check to ensure that accessors aren't used... since
	// we don't have a "current object" to work on!.

	// Try and determine what the expected type is that we will return.
	Class c = this.rowsCount.getExpectedReturnType (q);

	if (!Utilities.isNumber (c))
	{

	    throw new QueryParseException ("The expected return type of the rows count expression: \"" +
					   this.rowsCount +
					   "\" of the LIMIT clause is: " +
					   c.getName () + 
					   " however the expression when evaluated must return a numeric result.");

	}

    }

    public List getSubList (List   objs,
			    Query  q)
	                    throws QueryExecutionException
    {

	// Get the row count.
	Object o = this.rowsCount.evaluate (null,
					    q);

	int rows = -1;

	// Ensure that it is a number.
	if ((o != null)
	    &&
	    (!(o instanceof Number))
	   )
	{

	    throw new QueryExecutionException ("Return value of rows count expression: \"" +
					       this.rowsCount +
					       "\" for the LIMIT clause is of type: " +
					       o.getClass ().getName () + 
					       " expected it to return a numeric value.");

	}

	if (o != null)
	{

	    // There are rounding issues here, but if the user provides a float/double value...
	    rows = ((Number) o).intValue ();

	}

	int start = 0;

	// Now get the start value...
	if (this.start != null)
	{

	    Object s = this.start.evaluate (null,
					    q);

	    // Ensure that it is a number.
	    if ((s != null)
		&&
		(!(s instanceof Number))
	       )
	    {

		throw new QueryExecutionException ("Return value of the start expression: \"" +
						   this.start +
						   "\" for the LIMIT clause is of type: " +
						   s.getClass ().getName () + 
						   " expected it to return a numeric value.");

	    }

	    if (s != null)
	    {

		// There are rounding issues here, but if the user provides a float/double value...
		start = ((Number) s).intValue ();

		// Whilst for the user rows start at 1, for us they start at 0...
		start--;

	    }

	}

	int ls = objs.size ();

	// Now get our sub-list.
	if (start > (ls - 1))
	{

	    // Return nothing, outside of the range.
	    return new ArrayList ();

	}

	if (rows > 0)
	{

	    if ((start + rows) > (ls - 1))
	    {

		    // Just return the rows starting at start...
		    // We return a new list to prevent issues with modifications...
		    return new ArrayList (objs.subList (start,
							ls));

	    }

	    // Here we return start + rows.
	    return new ArrayList (objs.subList (start,
						start + rows));

	} else {

	    // Just ignore the rows...
	    return new ArrayList (objs.subList (start,
						ls));

	}

    }

    public void setStart (ValueExpression v)
    {

	this.start = v;

    }

    public void setRowsCount (ValueExpression v)
    {

	this.rowsCount = v;

    }

}
