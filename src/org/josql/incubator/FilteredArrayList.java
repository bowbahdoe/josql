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
package org.josql.incubator;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.expressions.Expression;

public class FilteredArrayList extends ArrayList
{

    private Expression where = null;
    private Comparator orderByComp = null;
    private Query q = null;
    private Exception ex = null;
    private boolean noThrow = false;

    public FilteredArrayList (String q)
	                      throws QueryParseException
    {

	this (q,
	      10);

    }

    public FilteredArrayList (String q,
			      int    cap)
	                      throws QueryParseException
    {

	super (cap);

	this.q = new Query ();
	this.q.parse (q);
	this.where = this.q.getWhereClause ();

    }

    public FilteredArrayList (String     q,
			      Collection c)
	                      throws     QueryParseException
    {

	this (q);

	this.addAll (c);

    }

    public FilteredArrayList (Query q)
    {

	this.q = q;

    }

    public FilteredArrayList (Query      q,
			      Collection c)
    {

	this (q);

	this.addAll (c);

    }

    public boolean isNoThrowOnWhereFalse ()
    {

	return this.noThrow;

    }

    public void setNoThrowOnWhereFalse (boolean v)
    {

	this.noThrow = v;

    }

    public Exception getException ()
    {

	return this.ex;

    }

    public Query getQuery ()
    {

	return this.q;

    }

    public void resort ()
    {

	if (this.orderByComp == null)
	{

	    this.orderByComp = this.q.getOrderByComparator ();

	}

	if (this.orderByComp != null)
	{

	    Collections.sort (this,
			      this.orderByComp);

	    return;

	}

	Collections.sort (this);

    }

    private boolean check (Object o)
	                   throws IllegalArgumentException
    {

	this.ex = null;

	if (this.where == null)
	{

	    return true;

	}

	try
	{

	    if (!this.where.isTrue (o,
				    this.q))
	    {

		if (!this.noThrow)
		{

		    throw new IllegalArgumentException ("Where clause: " +
							this.where +
							" evaluates to false for object cannot be added");
		
		}

		return false;

	    }

	    return true;

	} catch (QueryExecutionException e) {

	    this.ex = e;

	    throw new IllegalArgumentException ("Where clause: " +
						this.where +
						" throws exception during execution, use: getException for details.");

	}

    }

    public boolean addAll (Collection c)
	                   throws     IllegalArgumentException
    {

	int s = this.size () - 1;

	if (s < 0)
	{

	    s = 0;

	}

	return this.addAll (s,
			    c);

    }

    public boolean addAll (int        index,
			   Collection c)
	                   throws     IllegalArgumentException
    {

	this.ex = null;

	if (c == null)
	{

	    throw new NullPointerException ("Expected collection to be non-null.");

	}

	boolean change = false;

	int st = index;

	if (c instanceof List)
	{

	    List l = (List) c;

	    int s = l.size (); 

	    for (int i = 0; i < s; i++)
	    {

		Object o = l.get (i);

		try
		{

		    if (this.where.isTrue (o,
					   this.q))
		    {

			super.add (st,
				   o);

			st++;
			change = true;

		    } else {

			if (!this.noThrow)
			{

			    throw new IllegalArgumentException ("Where clause: " +
								this.where +
								" evaluates to false for object cannot be added");

			}

		    }

		} catch (QueryExecutionException e) {
		    
		    this.ex = e;
		    
		    throw new IllegalArgumentException ("Where clause: " +
							this.where +
							" throws exception during execution, use: getException for details.");

		}

	    }

	} else {

	    Iterator iter = c.iterator ();

	    while (iter.hasNext ())
	    {

		Object o = iter.next ();

		try
		{

		    if (this.where.isTrue (o,
					   this.q))
		    {

			super.add (st,
				   o);

			st++;
			change = true;

		    } else {

			if (!this.noThrow)
			{

			    throw new IllegalArgumentException ("Where clause: " +
								this.where +
								" evaluates to false for object cannot be added");

			}

		    }

		} catch (QueryExecutionException e) {
		    
		    this.ex = e;

		    throw new IllegalArgumentException ("Where clause: " +
							this.where +
							" throws exception during execution, use: getException for details.");

		}

	    }

	}

	return change;

    }

    public void add (int    index,
		     Object o)
	             throws IllegalArgumentException
    {

	if (!this.check (o))
	{

	    return;

	}

	super.add (index,
		   o);

    }

    public Object set (int    index,
		       Object o)
	               throws IllegalArgumentException
    {

	Object oo = this.get (index);

	if (!this.check (o))
	{

	    return oo;

	}

	super.set (index,
		   o);

	return oo;

    }

    public boolean add (Object o)
	                throws IllegalArgumentException
    {

	if (!this.check (o))
	{

	    return false;

	}

	return super.add (o);

    }

    public boolean canAdd (Object o)
	                   throws QueryExecutionException
    {

	return this.where.isTrue (o,
				  this.q);

    }

    public Object clone ()
    {

	FilteredArrayList l = new FilteredArrayList (this.q,
						     this);

	return l;

    }

    public List cloneList (Query q)
    {

	return new FilteredArrayList (q,
				      this);

    }

    public List cloneList ()
    {

	return new FilteredArrayList (this.q,
				      this);

    }

    public FilteredArrayList cloneSelf ()
    {

	return (FilteredArrayList) this.cloneList ();

    }

    public FilteredArrayList cloneSelf (Query q)
    {

	return (FilteredArrayList) this.cloneList (q);

    }

}
