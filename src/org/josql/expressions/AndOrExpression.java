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
 * Represents either an <code>AND</code> expression or a <code>OR</code> expression.
 * Lazy evaluation is employed here such if the expression is: <code>LHS OR RHS</code>
 * and LHS = true then the RHS is NOT evaluated, if the expression is: <code>LHS AND RHS</code>
 * and LHS = false then the RHS is NOT evaluated (see {@link #isTrue(Object,Query)}).  This is important to note if you expect
 * side-effects to occur in the RHS (bad practice anyway so don't do it!).
 */
public class AndOrExpression extends BinaryExpression
{

    private boolean and = false;

    public boolean isAnd ()
    {

	return this.and;

    }

    public void setAnd (boolean v)
    {

	this.and = v;

    }

    /**
     * Evaulates the expression and returns true if the expression evaulates to <code>true</code>.
     * <p>
     * <table border="1" cellpadding="3" cellspacing="0">
     *   <tr>
     *     <th>Type</th>
     *     <th>LHS</th>
     *     <th>RHS</th>
     *     <th>Result</th>
     *     <th>Notes</th>
     *   </tr>
     *   <tr>
     *     <td>AND</td>
     *     <td>true</td>
     *     <td>true</td>
     *     <td>true</td>
     *     <td>Both LHS and RHS are evaulated.</td>
     *   </tr>
     *   <tr>
     *     <td>AND</td>
     *     <td>true</td>
     *     <td>false</td>
     *     <td>false</td>
     *     <td>Both LHS and RHS are evaulated.</td>
     *   </tr>
     *   <tr>
     *     <td>AND</td>
     *     <td>false</td>
     *     <td>unknown or false</td>
     *     <td>false</td>
     *     <td>Only the LHS is evaulated.</td>
     *   </tr>
     *   <tr>
     *     <td>OR</td>
     *     <td>true</td>
     *     <td>unknown</td>
     *     <td>true</td>
     *     <td>Only the LHS is evaulated.</td>
     *   </tr>
     *   <tr>
     *     <td>OR</td>
     *     <td>false</td>
     *     <td>true</td>
     *     <td>true</td>
     *     <td>Both the LHS and RHS are evaulated.</td>
     *   </tr>
     *   <tr>
     *     <td>OR</td>
     *     <td>false</td>
     *     <td>false</td>
     *     <td>false</td>
     *     <td>Both the LHS and RHS are evaulated.</td>
     *   </tr>
     * </table>
     * <p>
     * In general what this means is that you should "left-weight" your expressions so that
     * the expression that returns <code>true</code> most often (or more likely to return 
     * <code>true</code>) should be on the LHS.
     *
     * @param o The current object to perform the expression on. 
     * @param q The query object.
     * @return <code>true</code> if the expression evaulates to <code>true</code>, <code>false</code>
     *         otherwise.
     * @throws QueryExecutionException If the expression cannot be evaulated.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	// Execute left first.
	boolean l = this.left.isTrue (o,
				      q);

	// See what our predicate is...
	if (this.and)
	{

            if (!l)
            {

               return false;

            }

	    boolean r = this.right.isTrue (o,
					   q);

	    return l && r;

	}

	if (l)
	{

	    return true;

	}

	boolean r = this.right.isTrue (o,
				       q);
	
	return r;

    }

    /**
     * Return a string version of this expression.
     * Note: any formatting of the statement (such as line breaks) will be removed.
     *
     * @return A string version of the expression.  
     */
    public String toString ()
    {

	String pred = " OR ";
	
	if (this.and)
	{

	    pred = " AND ";

	}

	if (this.isBracketed ())
	{

	    return "(" + this.left.toString () + pred + this.right.toString () + ")";

	}

	return this.left.toString () + pred + this.right.toString ();

    }

}
