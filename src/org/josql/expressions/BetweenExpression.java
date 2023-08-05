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
 * Represents a "BETWEEN x AND y" expression.
 */
public class BetweenExpression extends BinaryExpression
{

    private ValueExpression start = null;
    private ValueExpression end = null;
    private boolean not = false;
    private boolean leftFR = false;
    private boolean startFR = false;
    private boolean endFR = false;
    private Object leftFRVal = null;
    private Object startFRVal = null;
    private Object endFRVal = null;

    /**
     * Inits the expression.
     * 
     * @param q The Query object.
     * @throws QueryParseException If the LHS, start or end cannot be inited.
     */
    public void init (Query q)
	              throws QueryParseException
    {

	this.left.init (q);

	this.start.init (q);
	this.end.init (q);

	this.leftFR = this.left.hasFixedResult (q);
	this.startFR = this.start.hasFixedResult (q);
	this.endFR = this.end.hasFixedResult (q);

    }

    /**
     * Get the start expression.
     *
     * @return The start expression, is an instance of {@link ValueExpression}.
     */
    public Expression getStart ()
    {

	return this.start;

    }

    /**
     * Get the end expression.
     *
     * @return The end expression, is an instance of {@link ValueExpression}.
     */
    public Expression getEnd ()
    {

	return this.end;

    }

    public void setEnd (ValueExpression e)
    {

	this.end = e;

    }

    public void setStart (ValueExpression s)
    {

	this.start = s;

    }

    public boolean isNot ()
    {

	return this.not;

    }

    public void setNot (boolean v)
    {

	this.not = v;

    }

    /**
     * Return whether this expression evaluates to <code>true</code>.
     *
     * Is equivalent to: LHS >= START AND LHS <= END.  And of course if the 
     * expression is NOTed then it returns !(LHS >= START AND LHS <= END).
     *
     * @param o The object to perform the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the LHS is between the start and end values.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	Object l = null;

	if (this.leftFR)
	{

	    if (this.leftFRVal == null)
	    {

		l = this.left.getValue (o,
					q);

		this.leftFRVal = l;

	    } else {

		l = this.leftFRVal;

	    }

	} else {

	    l = this.left.getValue (o,
				    q);

	    if (this.leftFR)
	    {

		this.leftFRVal = l;
		
	    }

	}

	Object s = null;

	if (this.startFR)
	{

	    if (this.startFRVal == null)
	    {

		s = this.start.getValue (o,
					 q);

		this.startFRVal = s;

	    } else {

		s = this.startFRVal;

	    }

	} else {

	    s = this.start.getValue (o,
				     q);

	    if (this.startFR)
	    {

		this.startFRVal = s;
		
	    }

	}

	Object e = null;

	if (this.endFR)
	{

	    if (this.endFRVal == null)
	    {

		e = this.end.getValue (o,
				       q);

		this.endFRVal = e;

	    } else {

		e = this.endFRVal;

	    }

	} else {

	    e = this.end.getValue (o,
				   q);

	    if (this.endFR)
	    {

		this.endFRVal = e;

	    }

	}

	// See if the LHS is >= s.
	boolean sb = Utilities.isGTEquals (l,
					   s);

	boolean eb = Utilities.isLTEquals (l,
					   e);

	if (!this.not
	    &&
	    sb
	    &&
	    eb
	   )
	{

	    return true;

	}

	if (this.not
	    &&
	    (!sb
	     ||
	     !eb
	    )
	   )
	{

	    return true;

	}

	return false;

    }

    /**
     * Returns a string version of this expression.
     * Will return the form:
     * <p>
     * <pre>
     *   {@link Expression#toString() Expression} [ NOT ] BETWEEN {@link Expression#toString() Expression} AND {@link Expression#toString() Expression}
     * </pre>
     *
     * @return A string version of the expression.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer (this.left.toString ());

	if (this.not)
	{

	    buf.append (" NOT");

	}

	buf.append (" BETWEEN ");

	buf.append (this.start);
	buf.append (" AND ");
	buf.append (this.end);

	if (this.isBracketed ())
	{

	    buf.insert (0,
			"(");

	    buf.append (")");

	}

	return buf.toString ();

    }

}
