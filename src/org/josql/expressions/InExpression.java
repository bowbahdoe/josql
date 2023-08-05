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
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

/**
 * This class represents in [ NOT ] IN [ LIKE ] [ ALL ] expression.
 * If any of the values listed are Maps or Collections then they are iterated over to see if a match
 * is found.  For Maps only the key values are checked.  If you pass a list then it is iterated over
 * using a <code>for</code> construct rather than an Iterator since it's much faster.
 * <p>
 * Note: due to the way that the expression is designed it is POSSIBLE to have a binary expression
 * in the in list, however at this time that is not supported since it can lead to an ambiguous result,
 * for example: <code>true IN (true, false)</code> has no sensible meaning.
 */
public class InExpression extends BinaryExpression
{

    private List items = new ArrayList ();
    private boolean not = false;
    private boolean doLike = false;
    private boolean all = false;
    private boolean ignoreCase = false;

    /**
     * Initialise the IN expression.  Init the LHS and then all of the values in the brackets.
     * 
     * @param q The Query object.
     * @throws QueryParseException If something goes wrong with the init.
     */
    public void init (Query  q)
	              throws QueryParseException
    {

	this.left.init (q);

	int s = this.items.size ();

	for (int i = 0; i < s; i++)
	{

	    Expression exp = (Expression) this.items.get (i);

	    exp.init (q);

	}

    }

    public void setIgnoreCase (boolean v)
    {

	this.ignoreCase = v;

    }

    public boolean isIgnoreCase ()
    {

	return this.ignoreCase;

    }

    public boolean isAll ()
    {

	return this.all;

    }

    public void setAll (boolean v)
    {

	this.all = v;

    }

    public boolean isDoLike ()
    {

	return this.doLike;

    }

    public void setDoLike (boolean d)
    {

	this.doLike = d;

    }

    public void setItems (List items)
    {

	this.items = items;

    }

    public List getItems ()
    {

	return this.items;

    }

    public void addItem (Expression e)
    {

	this.items.add (e);

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
     * Return whether this expression evaulates to <code>true</code>.  If any of the values on RHS are Maps (keys 
     * only) or Collections then they are iterated over and checked against the LHS.
     *
     * @param o The current object to perform the expression on.
     * @param q The Query object.
     * @return <code>true</code> if the LHS is "equal" (as determined by: {@link Utilities#isEquals(Object,Object)})
     *         to any of the values in the brackets.  If this is a NOT expression then the result is negated.
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	// Get the LHS.
	Object l = this.left.getValue (o,
				       q);

	String v = null;
	String wc = String.valueOf (q.getWildcardCharacter ());

	if (this.doLike)
	{

	    if (l != null)
	    {

		v = l.toString ();

		if (this.ignoreCase)
		{

		    v = v.toLowerCase ();

		}

	    }

	}

	int count = 0;

	// Cycle over our items.
	int s = this.items.size ();

	for (int i = 0; i < s; i++)
	{

	    Expression exp = (Expression) this.items.get (i);

	    // Evaluate it.
	    Object eo = exp.getValue (o,
				      q);

	    boolean eq = false;
	    boolean proc = false;

	    if (eo instanceof Collection)
	    {

		Collection col = (Collection) eo;

		eq = this.compareCollection (l,
					     col,
					     v,
					     wc);
		proc = true;

	    }

	    if (eo instanceof Map)
	    {

		eq = this.compareMap (l,
				      (Map) eo,
				      v,
				      wc);
		proc = true;

	    }

	    if (!proc)
	    {

		// See if the objects are equivalent.
		eq = this.compareItem (l,
				       eo,
				       v,
				       wc);

	    }

	    if (eq)
	    {

		count++;

		if (this.not)
		{

		    return false;

		}

		if (!this.all)
		{

		    return true;

		}

	    } 

	}

	if ((this.all)
	    &&
	    (!this.not)
	    &&
	    (count == s)
	   )
	{

	    return true;

	}

	if ((this.all)
	    &&
	    (this.not)
	    &&
	    (count == 0)
	   )
	{

	    return true;

	}

	if (this.not)
	{

	    return true;

	}

	return false;

    }

    private boolean compareCollection (Object     o,
				       Collection c,
				       String     v,
				       String     wc)
    {

	if (c instanceof List)
	{

	    return this.compareList (o,
				     (List) c,
				     v,
				     wc);

	}

	Iterator i = c.iterator ();

	int count = 0;

	while (i.hasNext ())
	{

	    Object n = i.next ();

	    if (this.compareItem (o,
				  n,
				  v,
				  wc))
	    {

		count++;

		if (!this.all)
		{

		    return true;

		}

	    }

	}

	if ((this.all)
	    &&
	    (!this.not)
	    &&
	    (count == c.size ())
	   )
	{

	    return true;

	}

	if ((this.all)
	    &&
	    (this.not)
	    &&
	    (count == 0)
	   )
	{

	    return true;

	}

	return false;

    }

    private boolean compareList (Object o,
				 List   l,
				 String v,
				 String wc)
    {

	int s = l.size ();

	int count = 0;

	for (int i = 0; i < s; i++) 
	{

	    Object n = l.get (i);

	    if (this.compareItem (o,
				  n,
				  v,
				  wc))
	    {

		count++;

		if (!this.all)
		{

		    return true;

		}

	    }

	}

	if ((this.all)
	    &&
	    (!this.not)
	    &&
	    (count == s)
	   )
	{

	    return true;

	}

	if ((this.all)
	    &&
	    (this.not)
	    &&
	    (count == 0)
	   )
	{

	    return true;

	}

	return false;

    }

    private boolean compareItem (Object o,
				 Object n,
				 String v,
				 String wc)
    {

	boolean eq = true;

	if (this.doLike)
	{

	    if ((v == null)
		&&
		(n == null)
	       )
	    {

		return true;

	    }

	    if (n == null)
	    {

		return false;

	    }

	    String vn = n.toString ();

	    if (this.ignoreCase)
	    {

		vn = vn.toLowerCase ();

	    }

	    List pat = Utilities.getLikePattern (vn,
						 wc);

	    eq = Utilities.matchLikePattern (pat,
					     v);

	} else {

	    if (this.ignoreCase)
	    {

                if (o == null)
                {
                    
                    return (n == null);
                    
                } else {
                    
                    if (n == null)
                    {
                        
                        return false;
                        
                    }
                    
                }

		return o.toString ().equalsIgnoreCase (n.toString ());

	    }

	    eq = Utilities.isEquals (o,
				     n);

	}

	return eq;

    }

    private boolean compareMap (Object o,
				Map    m,
				String v,
				String wc)
    {

	Iterator i = m.keySet ().iterator ();

	int count = 0;
	
	while (i.hasNext ())
	{

	    Object n = i.next ();

	    if (this.compareItem (o,
				  n,
				  v,
				  wc))
	    {

		count++;

		if (!this.all)
		{

		    return true;

		}

	    }

	}

	if ((this.all)
	    &&
	    (!this.not)
	    &&
	    (count == m.size ())
	   )
	{

	    return true;

	}

	if ((this.all)
	    &&
	    (this.not)
	    &&
	    (count == 0)
	   )
	{

	    return true;

	}

	return false;

    }

    /**
     * Return a string representation of this expression.
     * In the form: {@link Expression#toString() Expression} [ NOT ] [$]IN [ LIKE ] [ ALL ]
     * ( {@link Expression#toString() Expression} [ , {@link Expression#toString() Expression}* ] )
     *
     * @return A string representation of this expression.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer (this.left.toString ());

	buf.append (" ");

	if (this.isNot ())
	{

	    buf.append ("NOT ");

	}

	if (this.ignoreCase)
	{

	    buf.append ("$");

	}

	buf.append ("IN ");

	if (this.doLike)
	{

	    buf.append ("LIKE ");

	}

	if (this.all)
	{

	    buf.append ("ALL ");

	}

	buf.append ("(");

	for (int i = 0; i < this.items.size (); i++)
	{

	    buf.append (this.items.get (i));

	    if (i < (this.items.size () - 1))
	    {

		buf.append (",");

	    }

	}

	buf.append (")");

	if (this.isBracketed ())
	{

	    buf.insert (0,
			"(");
	    buf.append (")");

	}

	return buf.toString ();

    }

}
