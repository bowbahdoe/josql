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
 * Represents the arithmetic expressions: *, +, /, - and %.
 * It should be noted that ALL numbers in JoSQL are represented as <b>double</b> values, this
 * allows for easy arithmetic operations without the fear of losing precision or casting
 * issues.
 */
public class ArithmeticExpression extends ValueExpression
{

    public static final int MULTIPLY = 0;
    public static final int ADDITION = 1;
    public static final int SUBTRACT = 2;
    public static final int DIVIDE = 3;
    public static final int MODULUS = 4;

    private int type = -1;

    private ValueExpression left = null;
    private ValueExpression right = null;

    private boolean fixedResult = false;

    /**
     * Return the expected return type.  This just returns the result of calling this
     * method on the LHS.
     *
     * @param q The Query object.
     * @return The expected return type class.
     * @throws QueryParseException If something goes wrong in determining the return type.
     */
    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	return this.left.getExpectedReturnType (q);

    }

    /**
     * Determine whether this arithmetic expression evaluates to true.
     * Whilst this seems a little bizarre this method is needed to allow
     * artithmetic expressions to be used in the columns part of the SELECT.
     * <p>
     * Thus the following could be performed:
     * <pre>
     *   SELECT 10 + 20
     *   FROM   java.lang.Object
     * </pre>
     *
     * The rules are as follows:
     * <ul>
     *   <li>If {@link #evaluate(Object,Query)} returns <code>null</code> then <code>false</code>
     *       is returned.</li>
     *   <li>If {@link #evaluate(Object,Query)} returns a number and it is greater than <b>0</b> then
     *       <code>true</code> is returned.  If it is <b>0</b> or less than <b>0</b> 
     *       then <code>false</code> is returned.
     *   <li>If it is anything else then <code>true</code> is returned.
     * </ul>
     *
     * @param o The object to perform the expression on.
     * @param q The Query object.
     * @return As according to the rules above.
     * @throws QueryExecutionException If something goes wrong during the evaluation of the 
     *                                 expression.
     */
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

	if (o instanceof Number)
	{

	    return ((Number) o).doubleValue () > 0;

	}

	return true;

    }

    /**
     * Return whether this expression has a fixed result. 
     * 
     * @param q The Query object.
     * @return {@link Expression#hasFixedResult(Query) LHS.hasFixedResult(Query)} 
     *         && 
     *         {@link Expression#hasFixedResult(Query) RHS.hasFixedResult(Query)}
     */
    public boolean hasFixedResult (Query q)
    {

	return this.fixedResult;

    }

    public void init (Query  q)
	              throws QueryParseException
    {

	this.left.init (q);
	this.right.init (q);

	this.fixedResult = this.left.hasFixedResult (q) && this.right.hasFixedResult (q);

    }

    /**
     * Get the RHS value expression.
     *
     * @return The RHS.
     */
    public ValueExpression getRight ()
    {

	return this.right;

    }

    /**
     * Get the LHS value expression.
     *
     * @return The LHS.
     */
    public ValueExpression getLeft ()
    {

	return this.left;

    }

    public void setLeft (ValueExpression exp)
    {

	this.left = exp;

    }

    public void setRight (ValueExpression exp)
    {

	this.right = exp;

    }

    public int getType ()
    {

	return this.type;

    }

    public void setType (int t)
    {

	this.type = t;

    }

    /**
     * Evaulate this expression.  Apart from the special cases the LHS and RHS must evaluate
     * to an instance of <code>java.lang.Number</code> otherwise a QueryExecutionException is
     * thrown.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If the type of the expression is + and either LHS or RHS are NOT numbers 
     *       then the String concatentation of them both are returned.  
     *       This works the same as in Java.</li>
     *   <li>If the type of the expression is / and the RHS is 0 then 0 is returned.</li>
     * </ul>
     *
     * @param o The object to perform the expression on.
     * @param q The Query object.
     * @return The result of the expression, see the "special cases" for the exceptions to what would
     *         be the intuitive result.
     * @throws QueryExecutionException If an error occurs during processing.
     */
    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	Object l = this.left.getValue (o,
				       q);

	Object r = this.right.getValue (o,
					q);

	// Special case for addition.
	if ((this.type == ArithmeticExpression.ADDITION)
	    &&
	    (!(l instanceof Number)
	     ||
	     !(r instanceof Number)
	    )
	   )
	{

	    StringBuffer b = new StringBuffer ();

	    b.append (l);

            b.append (r);

	    return b.toString ();

	}

	if (l == null)
	{

	    l = new Double (0);

	}

	if (r == null)
	{

	    r = new Double (0);

	}

	double ld = Utilities.getDouble (l);
	double rd = Utilities.getDouble (r);

	if (this.type == ArithmeticExpression.ADDITION)
	{

	    return new Double (ld + rd);

	}

	if (this.type == ArithmeticExpression.SUBTRACT)
	{

	    return new Double (ld - rd);

	}

	if (this.type == ArithmeticExpression.MULTIPLY)
	{

	    return new Double (ld * rd);

	}

	if (this.type == ArithmeticExpression.MODULUS)
	{

	    return new Double (ld % rd);

	}

	if (this.type == ArithmeticExpression.DIVIDE)
	{

	    if (rd == 0)
	    {

		return new Double (0);

	    }

	    return new Double (ld / rd);

	}

	return null;

    }

    public String toString ()
    {

	String pred = "+";
	
	if (this.type == ArithmeticExpression.MULTIPLY)
	{

	    pred = "*";

	}

	if (this.type == ArithmeticExpression.SUBTRACT)
	{

	    pred = "-";

	}

	if (this.type == ArithmeticExpression.MODULUS)
	{

	    pred = "%";

	}

	if (this.type == ArithmeticExpression.DIVIDE)
	{

	    pred = "/";

	}

	String exp = this.left.toString () + " " + pred + " " + this.right.toString ();

	if (this.isBracketed ())
	{

	    exp = "(" + exp + ")";

	}

	return exp;

    }

}
