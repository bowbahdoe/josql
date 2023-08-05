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
 * The base class for all expressions.
 */
public abstract class Expression 
{

    private boolean bracketed = false;

    /**
     * This method allows ANY expression (including those that extend {@link ValueExpression})
     * to be used in the WHERE and HAVING clauses but ensuring that a boolean value is
     * available for every expression.
     *
     * @param o The current object to evaluate the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the expression evaluates to <code>true</code> (well duh...).
     * @throws QueryExecutionException If there is a problem with the execution of the 
     *                                 expression.
     */
    public abstract boolean isTrue (Object o,
				    Query  q)
	                            throws QueryExecutionException;

    /**
     * Return whether the expression will evaluate to a fixed/constant result.
     * This allows certain optimisations to be performed when an expression is 
     * evaluated.  A "fixed result" is basically one in which multiple calls to
     * either the: {@link #isTrue(Object,Query)} or {@link #getValue(Object,Query)}
     * methods will return the same object (or that o1.equals (o2) == true) 
     * regardless of the object passed to the method.
     *
     * @param q The Query object.
     * @return <code>true</code> if the expression evaluates to a fixed/constant result.
     */
    public abstract boolean hasFixedResult (Query q);

    public void setBracketed (boolean v)
    {

	this.bracketed = v;

    }

    public boolean isBracketed ()
    {

	return this.bracketed;

    }

    /**
     * Return the class of the object that "should" be returned from a call to the:
     * {@link #getValue(Object,Query)} method.  It may be that repeated executions
     * of a query will return different classes from this method.  In general
     * sub-classes should take this variance into account.
     *
     * @param q The Query object.
     * @return The expected type that will be returned from the {@link #getValue(Object,Query)}
     *         method.
     * @throws QueryParseException If something goes wrong with determining the type.
     */
    public abstract Class getExpectedReturnType (Query  q)
	                                         throws QueryParseException;

    /**
     * Perform the necessary initialisation for this expression.
     * The exact operations performed are defined by the sub-class.
     * 
     * @param q The Query object.
     * @throws QueryParseException If something goes wrong with the initialisation.
     */
    public abstract void init (Query  q)
	                       throws QueryParseException;

    /**
     * Get the value for this expression based upon the object passed in.  In general
     * sub-classes should perform some operation on the object to generate their result.
     * The Query object is provided so that sub-classes can gain access to the 
     * bind variables (if required), save values and so on.
     * Whilst it may seem better to have the Query object as a member of this class
     * this would then prevent the expression from being used separately from the Query 
     * (a design goal of JoSQL, i.e. independent processing).
     *
     * @param o The current object that the expression should be evaluated on.
     * @param q The Query object.
     * @return The value of the expression.
     * @throws QueryExecutionException If something goes wrong with gaining the value.
     */
    public abstract Object getValue (Object o,
				     Query  q)
	                             throws QueryExecutionException;

    /**
     * Return a string representation of the expression, making this abstract forces
     * sub-classes to provide an implementation.
     *
     * @return A string representation of the expression.
     */
    public abstract String toString ();

}
