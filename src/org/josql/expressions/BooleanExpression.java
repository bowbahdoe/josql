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
 * This class represents a "boolean" expression, either <code>true</code> or <code>false</code>.
 */
public class BooleanExpression extends ValueExpression
{

    private Boolean value = null;

    public BooleanExpression ()
    {

    }

    /**
     * Always returns <code>true</code> since it represents a constant.
     *
     * @param q The Query object.
     * @return <code>true</code> always.
     */
    public boolean hasFixedResult (Query q)
    {

	return true;

    }

    /**
     * Returns a string version of this expression.
     * Basically returns: true | false.
     *
     * @return A string version of this expression.
     */
    public String toString ()
    {

	if (this.isBracketed ())
	{

	    return "(" + this.value.toString () + ")";

	}

	return this.value.toString ();

    }

    /**
     * Get the expected return type.
     *
     * @param q The Query object.
     * @return Always returns: <code>java.lang.Boolean.TYPE</code>.
     */
    public Class getExpectedReturnType (Query q)
    {

	return Boolean.TYPE;

    }

    /**
     * Init this expression.  Actually does nothing since it is a constant.
     *
     * @param q The Query object.
     */
    public void init (Query  q)
    {

	// Nothing to do...

    }

    public void setValue (Boolean b)
    {

	this.value = b;

    }

    /**
     * Returns whether this expression is <code>true</code> or <code>false</code>.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object, not used in this method.
     * @return The value of this expression.
     */
    public boolean isTrue (Object o,
			   Query  q)
    {

	return this.value.booleanValue ();

    }

    /**
     * Get the value of this boolean.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object, not used in this method.
     * @return The value of this expression.
     */
    public Object getValue (Object o,
			    Query  q)
    {

	return this.value;

    }

    /**
     * Get the value of this boolean.
     *
     * @param o The current object, not used in this method.
     * @param q The Query object, not used in this method.
     * @return The value of this expression.
     */
    public Object evaluate (Object o,
			    Query  q)
    {

	return this.value;

    }

}
