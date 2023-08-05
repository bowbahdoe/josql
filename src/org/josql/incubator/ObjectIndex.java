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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import com.gentlyweb.utils.Getter;

/**
 * This is an experimental class aimed at producing an index across a 
 * collection of homogeneous objects.  It should be noted here that it is the "sorting" 
 * of the elements in the List of objects that takes nearly all the time here.
 * Use the {@link #sort()} method yourself to only perform sort once.  Everytime you
 * add an object however the sort status of the List of objects will be invalidated and
 * have to be re-sorted before you have retrieve the objects again.
 */
public class ObjectIndex implements Comparator
{

    private List objs = new ArrayList ();
    private List indices = new ArrayList ();
    private Class c = null;
    private int size = -1;
    private boolean dirty = false;
    private boolean syncOnAdd = false;

    public ObjectIndex (Class c)
    {

	this.c = c;

    }

    public boolean isSyncOnAdd ()
    {

	return this.syncOnAdd;

    }

    public void setSyncOnAdd (boolean v)
    {

	this.syncOnAdd = v;

    }

    public List getObjects (List keys)
    {

	if (this.dirty)
	{

	    Collections.sort (this.objs,
			      this);

	    this.dirty = false;

	}

	List res = new ArrayList ();

	int low = 0;
	int os1 = this.objs.size () - 1;

	int high = os1;

	while (low <= high)
	{

	    int mid = (low + high) / 2;

	    Object o = this.objs.get (mid);

	    int r = this.compare (o,
				  keys);

	    if (r != 0)
	    {

		// They are not equal, so now determine whether we go "up" or "down".
		if (r < 0)
		{

		    low = mid + 1;

		} else {

		    high = mid - 1;

		}

	    } else {

		// Add it to the list...
		res.add (o);

		int oi = mid;

		// Now we need to check either side for others that may
		// match.
		while (mid < os1)
		{

		    mid++;

		    o = this.objs.get (mid);
		    
		    r = this.compare (o,
				      keys);
		    
		    if (r == 0)
		    {

			res.add (o);
			    
		    } else {

			// Not equal...
			break;

		    }

		}

		mid = oi;

		while (mid > -1)
		{

		    mid--;

		    o = this.objs.get (mid);
		    
		    r = this.compare (o,
				      keys);
		    
		    if (r == 0)
		    {

			res.add (o);
			    
		    } else {

			// Not equal...
			break;

		    }

		}

		// If we are here then we've got 'em all.
		break;

	    }

	}

	return res;

    }

    public int size ()
    {

	return this.size;

    }

    public void sort ()
    {

	Collections.sort (this.objs,
			  this);

	this.dirty = false;

    }

    public void add (String name)
    {

	if (this.syncOnAdd)
	{

	    Collections.sort (this.objs,
			      this);

	    this.dirty = false;

	} else {

	    this.dirty = true;

	}

	this.indices.add (new Getter (name,
				      this.c));

    }

    public void removeObject (Object o)
    {

	this.objs.remove (o);

    }

    public void addObject (Object o)
    {

	if (this.objs.contains (o))
	{

	    return;

	}

	if (this.syncOnAdd)
	{

	    Collections.sort (this.objs,
			      this);

	    this.dirty = false;

	} else {

	    this.dirty = true;

	}

	this.objs.add (o);

	this.size = this.objs.size ();

    }

    public int compare (Object o,
			List   keys)
    {

	try
	{

	    int ks = keys.size ();

	    for (int i = 0; i < ks; i++)
	    {

		Getter g = (Getter) this.indices.get (i);
		
		Object eo1 = g.getValue (o);

		Object kso = keys.get (i);

		// Can't compare what's not there, return 0.
		if ((eo1 == null)
		    ||
		    (kso == null)
		   )
		{

		    return 0;

		}

		int v = 0;
		    
		if (eo1 instanceof Comparable)
		{
			
		    // We can use a simple compareTo.
		    Comparable comp = (Comparable) eo1;
		    
		    v = comp.compareTo (kso);
			
		} else {
			
		    v = eo1.toString ().compareTo (kso.toString ());
			
		}
		    
		if (v != 0)
		{
		    
		    return v;
		    
		}
		
		// They are equal, so need to go to the next field...
		continue;

	    }

	} catch (Exception e) {
	    
	}

	// All equal.
	return 0;

    }

    public int compare (Object o1,
			Object o2)
    {

	try
	{

	    for (int i = 0; i < this.size; i++)
	    {

		Getter g = (Getter) this.indices.get (i);
		
		Object eo1 = g.getValue (o1);

		Object eo2 = g.getValue (o2);

		// Can't compare what's not there, return 0.
		if ((eo1 == null)
		    ||
		    (eo2 == null)
		   )
		{

		    return 0;

		}

		int v = 0;
		    
		if (eo1 instanceof Comparable)
		{
			
		    // We can use a simple compareTo.
		    Comparable comp = (Comparable) eo1;
		    
		    v = comp.compareTo (eo2);
			
		} else {
			
		    v = eo1.toString ().compareTo (eo2.toString ());
			
		}
		    
		if (v != 0)
		{
		    
		    return v;
		    
		}
		
		// They are equal, so need to go to the next field...
		continue;

	    }

	} catch (Exception e) {
	    
	}

	// All equal.
	return 0;

    }    

}
