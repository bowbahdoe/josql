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

import java.util.List;
import java.util.ArrayList;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

/**
 * This class represents a list of expressions used within a SQL statement.
 * <p>
 * Expressions lists are created using pairs of "[]" brackets, i.e.
 * <pre>
 *  [toString, 10, true]
 * </pre>
 * Would create a list of expressions that should be treated as a unit.
 * <p>
 * Since expression lists are also expressions it is possible to nest them to the nth degree, if desired.
 * i.e.
 * <pre>
 *  [toString, [1, 2, 3, 4], [true, true]]
 * </pre>
 */
public class ExpressionList extends ValueExpression
{

    private List expressions = null;

    /**
     * Get the expected return type, which is {@link List}.
     *
     * @param q The Query object.
     * @return {@link List}.
     */
    public Class getExpectedReturnType (Query  q)
    {
    
        return List.class;

    }

    /**
     * Initialises this expression list, each expression in the list is inited.
     *
     * @param q The Query object.
     * @throws QueryParseException If one of the expressions cannot be inited.
     */
    public void init (Query  q)
	              throws QueryParseException
    {

        for (int i = 0; i < this.expressions.size (); i++)
        {
            
            Expression exp = (Expression) this.expressions.get (i);
            
            exp.init (q);
            
        }

    }

    /**
     * Returns the expressions, a list of {@link Expression} objects.
     *
     * @return The expressions.
     */
    public List getExpressions ()
    {
       
       return this.expressions;
        
    }

    /**
     * Add an expression to the list.
     *
     * @param expr The expression.
     */
    public void addExpression (Expression expr)
    {
        
        if (this.expressions == null)
        {
            
            this.expressions = new ArrayList ();
            
        }
        
        this.expressions.add (expr);
        
    }

    /**
     * Set the expressions.
     *
     * @param exprs The expressions.
     */
    public void setExpressions (List exprs)
    {
        
        this.expressions = exprs;
        
    }

    /**
     * Gets the value of the expressions, this will return a list of the values for
     * each of the expressions in the list.  In essence {@link Expression#getValue(Object,Query)}
     * is called on each of the expressions.
     *
     * @param o The current object.  
     * @param q The Query object.
     * @return A list of the values, if there are no expressions in the list then an empty list is returned.
     * @throws QueryExecutionException If something goes wrong the acquisition of the values.
     */
    public Object getValue (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

        List ret = new ArrayList ();

        for (int i = 0; i < this.expressions.size (); i++)
        {
            
            Expression exp = (Expression) this.expressions.get (i);
            
            ret.add (exp.getValue (o,
                                   q));
            
        }

        return ret;

    }

    /**
     * Returns <code>true</code> if one of the expression values is non-null.
     * Note: for efficiency this method calls {@link Expression#getValue(Object,Query)}
     * on each expression directly and returns <code>true</code> for the first non-null
     * value found, as such if any of your expressions triggers side-effects then this
     * method should not be used.
     *
     * @param o The current object.  
     * @param q The Query object.
     * @return <code>true</code> if one of the expression values is non-null.
     * @throws QueryExecutionException If a problem occurs during evaluation.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

        for (int i = 0; i < this.expressions.size (); i++)
        {
            
            Expression exp = (Expression) this.expressions.get (i);
            
            if (exp.getValue (o,
                              q) != null)
            {
                
                return true;
                
            }
            
        }

        return false;

    }

    /**
     * Evaluates the value of this expression list.  This is just a thin-wrapper around:
     * {@link #getValue(Object,Query)}.
     *
     * @param o The current object.
     * @param q The Query object.
     * @return The value of this expression list.
     * @throws QueryExecutionException If there is a problem getting the values.
     */
    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	return this.getValue (o,
			      q);

    }

    /**
     * Returns a string version of this expression list.
     * Returns in the form: "[" Expression [ , Expression ]* "]".
     *
     * @return A string version of the expression list.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ("[");

        for (int i = 0; i < this.expressions.size (); i++)
        {
            
            buf.append (this.expressions.get (i));
            
            if (i < this.expressions.size () - 1)
            {
                
                buf.append (", ");
                
            }
            
        }

        buf.append ("]");

	return buf.toString ();

    }

    /**
     * Returns <code>true</code> if this expression list is empty (no expressions) or
     * if ALL of the expressions have a fixed result.
     *
     * @param q The Query object.
     * @return See description.
     */
    public boolean hasFixedResult (Query q)
    {

        if (this.expressions.size () == 0)
        {
            
            return true;
            
        }

        for (int i = 0; i < this.expressions.size (); i++)
        {
            
            Expression exp = (Expression) this.expressions.get (i);
            
            if (!exp.hasFixedResult (q))
            {
                
                return false;
                
            }
            
        }

        return true;

    }

}
