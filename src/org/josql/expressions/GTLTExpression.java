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

import org.josql.internal.Utilities;

/**
 * This class represents one of the following:
 * <ul>
 *   <li><b>></b> - Greater than</li>
 *   <li><b><</b> - Less than</li>
 *   <li><b>>=</b> - Greater than or equal to</li>
 *   <li><b><=</b> - Less than or equal to</li>
 * </ul>
 * <p>
 * You can also force a "string" comparison by prefixing with "$".  This will force
 * both sides to be strings via the <code>toString</code> method.
 */
public class GTLTExpression extends BinaryExpression
{

    private int type = -1;
    private boolean ignoreCase = false;

    public int getType ()
    {

	return this.type;

    }

    /**
     * Type is an integer here for speed purposes, however one of the constants
     * should be used.
     *
     * @param t The type of expression.
     */
    public void setType (int t)
    {

	this.type = t;

    }

    public void setIgnoreCase (boolean v)
    {

	this.ignoreCase = v;

    }

    public boolean isIgnoreCase ()
    {

	return this.ignoreCase;

    }

    /**
     * Return whether this expression evaluates to true.  The actual comparison
     * is performed by: {@link Utilities#compare(Object,Object)} which copes with
     * the object types.
     *
     * @param o The current object to evaluate the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the expression evaluates to <code>true</code>.
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	// Get the lhs.
	Object l = this.left.getValue (o,
				       q);

	Object r = this.right.getValue (o,
					q);

	if ((l == null)
	    &&
	    (r == null)
	   )
	{

	    if ((this.type == 1)
		||
		(this.type == 3)
	       )
	    {

		return true;

	    }

	}

	if ((l == null)
	    ||
	    (r == null)
	   )
	{

	    return false;
	    
	}

	return Utilities.matches (l,
				  r,
				  this.ignoreCase,
				  this.type,
				  false);

    }

    /**
     * Return a string version of the expression.
     * In the form: {@link Expression#toString() Expression} [ $ ] <|> [ = ] {@link Expression#toString() Expression}
     *
     * @return A string version of the expression.
     */
    public String toString ()
    {

	String pred = "<";

	if (this.type == Utilities.GT)
	{

	    pred = ">";

	}

	if (this.type == Utilities.GTE)
	{

	    pred = ">=";

	}

	if (this.type == Utilities.LTE)
	{

	    pred = "<=";

	}

	if (this.ignoreCase)
	{

	    pred = "$" + pred;

	}
	
	if (this.isBracketed ())
	{

	    return "(" + this.left.toString () + " " + pred + " " + this.right.toString () + ")";

	}

	return this.left.toString () + " " + pred + " " + this.right.toString ();

    }

}
