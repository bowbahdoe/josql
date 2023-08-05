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

/**
 * Super-class of Expressions that return a binary result.
 * <p>
 * A binary expression must always have a LHS.  The RHS is optional.
 */
public abstract class BinaryExpression extends Expression
{

    protected Expression left = null;
    protected Expression right = null;

    /**
     * Return whether this expression, and more specifically the left and right parts of
     * the expression return a fixed result.
     * Sub-classes may override this method for more tailored results, especially if the
     * binary expression does not demand a RHS.
     *
     * @param q The Query object.
     * @return <code>true<code> if the expression has a fixed result.
     */
    public boolean hasFixedResult (Query q)
    {

	boolean fr = true;

	if (this.right != null)
	{

	    fr = this.right.hasFixedResult (q);

	}

	return this.left.hasFixedResult (q) && fr;

    }

    /**
     * Return the expected return type from this expression.
     * 
     * @param q The Query object.
     * @return The class of the return type, this method ALWAYS returns <code>Boolean.class</code>.
     */
    public Class getExpectedReturnType (Query  q)
    {

	return Boolean.class;

    }

    /**
     * Init the expression.  Sub-classes will often override this method.
     * This method just calls: {@link Expression#init(Query)} on the LHS and RHS (if present) of the
     * expression.  
     * 
     * @param q The Query object.
     * @throws QueryParseException If the LHS and/or RHS cannot be inited.
     */
    public void init (Query  q)
	              throws QueryParseException
    {

	this.left.init (q);

	// There isn't always a RHS, for example IN expressions.
	if (this.right != null)
	{

	    this.right.init (q);

	}

    }

    /**
     * Get the value of this expression.  This will always return an instance of: 
     * <code>java.lang.Boolean</code> created as the result of a call to:
     * {@link Expression#getValue(Object,Query)}.
     *
     * @param o The object to evaluate the expression on.
     * @param q The Query object.
     * @return An instance of Boolean.
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return Boolean.valueOf (this.isTrue (o,
					     q));

    }

    /**
     * Get the RHS.
     *
     * @return The RHS of the expression.
     */
    public Expression getRight ()
    {

	return this.right;

    }

    /**
     * Get the LHS.
     *
     * @return The LHS of the expression.
     */
    public Expression getLeft ()
    {

	return this.left;

    }

    public void setLeft (Expression exp)
    {

	this.left = exp;

    }

    public void setRight (Expression exp)
    {

	this.right = exp;

    }

}
