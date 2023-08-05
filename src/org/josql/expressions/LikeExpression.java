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

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

/**
 * Represents a LHS [ NOT ] [ $ ] LIKE RHS expression.
 * It should be noted that unlike "normal" SQL the "." character is not supported since in
 * practice it tends to be redundant.
 * <p>
 * It is possible to use: $ in front of the "LIKE" to indicate that a case insensitive 
 * comparison should be performed, for example:
 * <pre>
 *   SELECT *
 *   FROM   java.lang.String
 *   WHERE  toString $LIKE '%value'
 * </pre>
 * <p>
 * Also, the LHS or RHS can be built up using the "+" operator to concatenate a string, thus:
 * <pre>
 *   SELECT *
 *   FROM   java.lang.String
 *   WHREE  toString $LIKE '%' + :myValue
 * </pre>
 * <p>
 * In this way (using the {@link BindVariable named bind variable}) you don't have to provide
 * the wildcard.
 * <p>
 * It is also possible to specify your own "wildcard" character in the Query object using:
 * {@link Query#setWildcardCharacter(char)}.
 * <p>
 * Note: the implementation is a modified version of that provided by: Kevin Stannard 
 * (http://www.jzoo.com/java/wildcardfilter/).
 */
public class LikeExpression extends BinaryExpression
{

    private boolean not = false;
    private boolean ignoreCase = false;
    private List pattern = null;

    public boolean isIgnoreCase ()
    {

	return this.ignoreCase;

    }

    /**
     * Init the expression, we over-ride here so that if the RHS is fixed we can 
     * init the pattern that will be used to match the expression.
     * 
     * @param q The Query object.
     * @throws QueryParseException If the LHS and/or RHS cannot be inited.
     */
    public void init (Query  q)
	              throws QueryParseException
    {

	// Call our parent first.
	super.init (q);

	if (this.right.hasFixedResult (q))
	{

	    // It does have a fixed result so get the value and init the pattern.
	    Object r = null;

	    try
	    {

		r = this.right.getValue (null,
					 q);

	    } catch (Exception e) {

		throw new QueryParseException ("Unable to get RHS value from: \"" +
					       this.right +
					       "\", expected to RHS to have fixed result.",
					       e);

	    }

	    if (r == null)
	    {

		// Return since we can't do anything useful now.
		return;

	    }

	    String rs = r.toString ();

	    if (this.ignoreCase)
	    {

		rs = rs.toLowerCase ();

	    }

	    char wc = q.getWildcardCharacter ();

	    this.pattern = Utilities.getLikePattern (rs,
						     String.valueOf (wc));

	}

    }    

    public void setIgnoreCase (boolean v)
    {

	this.ignoreCase = v;

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
     * Returns whether the LHS is "LIKE" the RHS.
     * A special case here is that if the LHS and RHS are both <code>null</code> then <code>true</code>
     * is returned.  Also, if either the LHS or RHS is not null and one is null then <code>false</code>
     * is returned.
     *
     * @param o The object to evaluate the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the LHS is "LIKE" the RHS, <code>false</code> if not.  And using
     *         "NOT" will invert the result.
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	// Get the left...
	Object l = this.left.getValue (o,
				       q);

	if (this.pattern != null)
	{

	    return Utilities.matchLikePattern (this.pattern,
					       l,
					       this.not,
					       this.ignoreCase);

	}

	Object r = this.right.getValue (o,
					q);

	if ((l == null)
	    &&
	    (r == null)
	   )
	{

	    // Special case...
	    if (this.not)
	    {

		return false;

	    }

	    return true;

	}

	if ((l == null)
	    ||
	    (r == null)
	   )
	{

	    if (this.not)
	    {

		return true;

	    }

	    return false;

	}

	// Convert RHS to a string.
	String rs = r.toString ();

	if (this.ignoreCase)
	{

	    rs = rs.toLowerCase ();

	}

	// Now see if rs contains the wildcard character.
	char wc = q.getWildcardCharacter ();

	List pat = Utilities.getLikePattern (rs,
					     String.valueOf (wc));

	return Utilities.matchLikePattern (pat,
					   l,
					   this.not,
					   this.ignoreCase);

    }

    /**
     * Returns a string version of the expression.
     * Returns in the form:
     * <pre>
     *   {@link Expression#toString() Expression} [ NOT ] [ $ ]LIKE {@link Expression#toString() Expression}
     * </pre>
     *
     * @return A string representation of the expression.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer (this.left.toString ());

	if (this.isNot ())
	{

	    buf.append (" NOT");

	}

	buf.append (" ");

	if (this.ignoreCase)
	{

	    buf.append ("$");

	}

	buf.append ("LIKE ");

	buf.append (this.right);

	if (this.isBracketed ())
	{

	    buf.insert (0,
			"(");
	    buf.append (")");

	}

	return buf.toString ();

    }

}
