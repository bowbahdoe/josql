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
package org.josql.internal;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

import org.josql.expressions.Expression;

import org.josql.Query;
import org.josql.QueryResults;

public class GroupByExpressionComparator implements Comparator
{

    private List items = new ArrayList ();
    private Query q = null;
    private int size = 0;
    private int count = 0;
    private Exception exp = null;
    private Object nullObj = new Object ();

    private Map cache = new HashMap ();
    private boolean caching = false;
    private Comparator uc = null;

    public GroupByExpressionComparator (Query      q,
                                        Comparator userComparator,
					boolean    caching)
    {

	this.q = q;
	this.caching = caching;
        this.uc = userComparator;

    }

    public int getCount ()
    {

	return this.count;

    }

    public boolean equals (Object o)
    {

	throw new UnsupportedOperationException ("Not supported for instances of: " +
						 this.getClass ().getName ());

    } 

    public boolean isCaching ()
    {

	return this.caching;

    }

    public void setCaching (boolean b)
    {

	this.caching = b;

    }

    public void clearCache ()
    {

	this.cache.clear ();

    }

    public int ci (Object o1,
		   Object o2)
	           throws Exception
    {

	List lo1 = (List) o1;
	List lo2 = (List) o2;
	
	QueryResults qr = this.q.getQueryResults ();

	for (int i = 0; i < this.size; i++)
	{

	    Item it = (Item) this.items.get (i);

	    // The "current object" here will be a list, it is also the "key"
	    // to the group by results.
	    this.q.setAllObjects ((List) qr.getGroupByResults ().get (lo1));

	    this.q.setCurrentGroupByObjects (this.q.getAllObjects ());

	    // Also, setup the save values.
	    this.q.setSaveValues ((Map) qr.getGroupBySaveValues (lo1));

	    this.q.setCurrentObject (o1);

	    Object eo1 = null;

	    if (it.exp != null)
	    {

		eo1 = it.exp.getValue (o1,
				       this.q);

	    } else {

		eo1 = lo1.get (it.ind);

	    }

	    this.q.setAllObjects ((List) qr.getGroupByResults ().get (lo2));

	    // Also, setup the save values.
	    this.q.setSaveValues ((Map) qr.getGroupBySaveValues (lo2));

	    this.q.setCurrentObject (o2);
	    
	    Object eo2 = null;

	    if (it.exp != null)
	    {

		eo2 = it.exp.getValue (o2,
				       this.q);

	    } else {

		eo2 = lo2.get (it.ind);

	    }

	    // Compare them...
	    int c = 0;

            if (this.uc != null)
            {
                
                c = this.uc.compare (eo1,
                                     eo2);
                
            } else {
            
                c = Utilities.compare (eo1,
				       eo2);

            }

	    if (c == 0)
	    {

		// Go to the next...
		continue;

	    }

	    // For speed reasons, 1 is used here rather than the constant.
	    if (it.dir == 1)
	    {

		c = -1 * c;

	    }

	    return c;

	}

	return 0;

    }

    public int cic (Object o1,
		    Object o2)
	            throws Exception
    {

	this.count++;

	Map co = null;
	boolean get = true;
	Item it = null;
	Object eo1 = null;
	Object eo2 = null;

	QueryResults qr = this.q.getQueryResults ();
	List lo1 = (List) o1;
	List lo2 = (List) o2;

	for (int i = 0; i < this.size; i++)
	{

	    it = (Item) this.items.get (i);

	    eo1 = null;

	    get = true;

	    co = (Map) cache.get (o1);

	    if (co == null)
	    {

		co = new HashMap (this.size);

		cache.put (o1,
			   co);

		get = false;

	    } 

	    if (get)
	    {

		eo1 = co.get (it);

		if (eo1 == this.nullObj)
		{

		    eo1 = null;

		}

	    }
		
	    if (eo1 == null)
	    {

		this.q.setAllObjects ((List) qr.getGroupByResults ().get (lo1));

		// Also, setup the save values.
		this.q.setSaveValues ((Map) qr.getGroupBySaveValues (lo1));

		this.q.setCurrentObject (o1);

		if (it.exp != null)
		{

		    eo1 = it.exp.getValue (o1,
					   this.q);

		} else {

		    eo1 = lo1.get (it.ind - 1);

		}

		co.put (it,
			eo1);

	    }

	    get = true;

	    eo2 = null;

	    co = (Map) cache.get (o2);

	    if (co == null)
	    {

		co = new HashMap (this.size);

		cache.put (o2,
			   co);

		get = false;

	    }

	    if (get)
	    {

		eo2 = co.get (it);

	    }

	    if (eo2 == null)
	    {

		this.q.setAllObjects ((List) qr.getGroupByResults ().get (lo2));

		// Also, setup the save values.
		this.q.setSaveValues ((Map) qr.getGroupBySaveValues (lo2));

		this.q.setCurrentObject (o2);

		if (it.exp != null)
		{

		    eo2 = it.exp.getValue (o2,
					   this.q);

		} else {

		    eo2 = lo2.get (it.ind - 1);

		}
		
		co.put (it,
			eo2);

	    }
	    
	    // Compare them...
	    int c = 0;
            
            if (this.uc != null)
            {
 
                c = this.uc.compare (eo1,
                                     eo2);
                
            } else {
                
                c = Utilities.compare (eo1,
				       eo2);

            }

	    if (c == 0)
	    {

		// Go to the next...
		continue;

	    }

	    // For speed reasons, 1 is used here rather than the constant.
	    if (it.dir == 1)
	    {

		c = -1 * c;

	    }

	    return c;

	}

	return 0;

    }

    public int compare (Object o1,
			Object o2)
    {

	try
	{

	    if (this.caching)
	    {

		return this.cic (o1,
				 o2);

	    } else {

		return this.ci (o1,
				o2);

	    }

	} catch (Exception e) {

	    this.exp = e;

	    return 0;

	}

    }

    public Exception getException ()
    {

	return this.exp;

    }

    public void addSortItem (Expression exp,
			     int        ind,
			     int        dir)
    {

	Item it = new Item ();
	it.dir = dir;
	it.ind = ind;
	it.exp = exp;

	this.items.add (it);

	this.size = this.items.size ();

    }

    private class Item
    {

	public int dir = 0;
	public int ind = 0;
	public Expression exp = null;

    }

}
