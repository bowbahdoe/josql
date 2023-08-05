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
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

/**
 * Represents an expression that also has an alias.  SELECT columns may have aliases
 * as may the functions in the "EXECUTE ON" clause.
 */
public class AliasedExpression extends Expression
{

    private String alias = null;
    protected Expression exp = null;
    private boolean fixedResult = false;

    /**
     * Return whether this expression has a fixed result.
     * See: {@link Expression#hasFixedResult(Query)} for more details.
     * 
     * @param q The Query object.
     * @return <code>true</code> if the expression returns a fixed result, <code>false</code> otherwise.
     */
    public boolean hasFixedResult (Query q)
    {

	return this.fixedResult;

    }

    /**
     * Get the expected return type for the expression.
     *
     * @param q The Query object.
     * @return The class of the return type.
     * @throws QueryParseException If an error occurs whilst trying to determine the
     *                             return type.
     */
    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	return this.exp.getExpectedReturnType (q);

    }

    /**
     * Init this expression.  All that occurs here is that the aliased expression is
     * inited via: {@link Expression#init(Query)}.
     *
     * @param q The Query object.
     * @throws QueryParseException If an error occurs during the initialisation of the
     *                             expression.
    */
    public void init (Query  q)
	              throws QueryParseException
    {

	this.exp.init (q);

	this.fixedResult = this.exp.hasFixedResult (q);

    }

    /**
     * Get the alias for the expression.
     *
     * @return The alias.
     */
    public String getAlias ()
    {

	return this.alias;

    }

    public void setAlias (String a)
    {

	this.alias = Utilities.stripQuotes (a);

    }

    /**
     * Get the expression being aliased.
     *
     * @return The expression.
     */
    public Expression getExpression ()
    {

	return this.exp;

    }

    public void setExpression (Expression exp)
    {

	this.exp = exp;

    }

    /**
     * Indicate whether the expression evaluates to <code>true</code>.
     *
     * @param o The object to perform the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the expression evaulates to <code>true</code>, <code>false</code>
     *         otherwise.
     * @throws QueryExecutionException If something goes wrong during execution of the:
     *                                 {@link Expression#isTrue(Object,Query)} method.
     * @see Expression#isTrue(Object,Query)
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	return this.exp.isTrue (o,
				q);

    }

    /**
     * Get the value for this expression.
     *
     * @param o The object to perform the expression on.
     * @param q The Query object.
     * @return The result of calling: {@link Expression#getValue(Object,Query)}.
     * @throws QueryExecutionException If something goes wrong with the execution of the
     *                                 expression.
     */
    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.exp.getValue (o,
				  q);

    }

    /**
     * Return a string representation of the aliased expression.
     * Returns in the form: <b>Expression AS Alias</b>.
     * 
     * @return The result of calling: {@link Expression#toString()} + AS + {@link #getAlias()}.
     */
    public String toString ()
    {

	return this.exp.toString () + " AS " + this.alias;

    }

}
