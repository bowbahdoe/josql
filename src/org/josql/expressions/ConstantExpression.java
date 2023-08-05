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

/**
 * This class represents a constant String or number.
 * ALL numbers in JoSQL are represented by <b>double</b>.
 */
public class ConstantExpression extends ValueExpression
{

    private Object val = null;

    /**
     * Get the expected return type.
     * Will be either: <code>java.lang.String.class</code>
     * or: <code>java.lang.Double.class</code>.
     *
     * @param q The Query object.
     * @return The expected class.
     */
    public Class getExpectedReturnType (Query  q)
    {

	if (this.val == null)
	{

            return Object.class;

	}

	return this.val.getClass ();

    }

    /**
     * Inits the expression, in reality does nothing here, can't init a constant!
     *
     * @param q The Query object.
     */
    public void init (Query  q)
    {

	// Nothing to do...

    }

    public void setValue (Object v)
    {

	this.val = v;

    }

    /**
     * Returns whether the value of this constant represents a <code>true</code>
     * value.  See: {@link ArithmeticExpression#isTrue(Object,Query)} for details of how
     * the return value is determined.
     *
     * @param o The current object.  Not used in this method.
     * @param q The Query object.
     * @return <code>true</code> if the constant evaluates to <code>true</code>.
     */
    public boolean isTrue (Object o,
			   Query  q)
    {

	if (this.val == null)
	{

	    return false;

	}

	if (this.val instanceof Boolean)
	{

	    return ((Boolean) this.val).booleanValue ();

	}

	if (this.val instanceof Number)
	{

	    return ((Number) this.val).doubleValue () > 0;

	}

	return true;

    }

    /**
     * Get the value of this constant.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object, not used in this method.
     * @return The constant value.
     */
    public Object getValue (Object o,
			    Query  q)
    {

	return this.val;

    }

    /**
     * Get the value of this constant.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object, not used in this method.
     * @return The constant value.
     */
    public Object evaluate (Object o,
			    Query  q)
    {

	return this.val;

    }

    /**
     * Always returns <code>true</code>, well duh!
     *
     * @param q The Query object.
     * @return <code>true</code> always.
     */
    public boolean hasFixedResult (Query q)
    {

	// Well duh...
	return true;

    }

    /**
     * Returns a string representation of this constant.
     * If the constant is a String then it should be noted that the original 
     * quoting character is not kept in this class and the output of this method
     * will contain the character "'" instead.  
     * If the constant is a number then "toString" is called on it, so any ","
     * or other number formatting will have been lost.
     *
     * @return A string representation of this constant.
     */
    public String toString ()
    {

	String v = null;

	if (this.val == null)
	{

	    v = val + "";

	} else {

	    if (this.val instanceof String)
	    {

		v = "'" + this.val.toString () + "'";

	    } else {

		v = this.val.toString ();

	    }

	}

	if (this.isBracketed ())
	{

	    v = "(" + v + ")";

	}

	return v;

    }

}
