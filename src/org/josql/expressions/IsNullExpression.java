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

import org.josql.Query;
import org.josql.QueryExecutionException;

/**
 * Represents an "IS NULL" (or "IS NOT NULL") expression.
 */
public class IsNullExpression extends BinaryExpression
{

    private boolean not = false;

    public boolean isNot ()
    {

	return this.not;

    }

    public void setNot (boolean v)
    {

	this.not = v;

    }

    /**
     * Return a string representation of this expression.
     * In the form: {@link Expression#toString()} IS [ NOT ] NULL
     *
     * @return A string representation of this expression.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ("IS ");
	
	if (this.not)
	{

	    buf.append ("NOT ");

	}

	buf.append (this.left.toString ());

	if (this.isBracketed ())
	{

	    buf.insert (0,
			"(");
	    buf.append (")");

	}

	return buf.toString ();

    }

    /**
     * Determine whether the LHS of this expression is or is not null.
     * Note that this is equivalent to: <code>LHS = null</code> or:
     * <code>LHS != null</code>.
     *
     * @param o The current object to perform the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the LHS is null (or not null is specified).
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	// Get the left...
	o = this.left.getValue (o,
				q);

	if (o == null)
	{

	    if (this.not)
	    {

		return false;

	    }

	    return true;

	}

	if (this.not)
	{

	    return true;

	}

	return false;

    }

}
