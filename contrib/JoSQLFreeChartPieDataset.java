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
package org.josql.contrib;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.jfree.data.general.PieDataset;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetChangeEvent;

import org.josql.Query;
import org.josql.QueryResults;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

import org.josql.expressions.SelectItemExpression;

public class JoSQLFreeChartPieDataset extends Query implements PieDataset
{

    private Map values = new LinkedHashMap ();
    private int key = 0;
    private int value = 0;
    private List listeners = new ArrayList ();
    private DatasetGroup group = null;

    public JoSQLFreeChartPieDataset ()
    {

    }

    public void addChangeListener (DatasetChangeListener l)
    {

	this.listeners.add (l);

    }

    public void removeChangeListener (DatasetChangeListener l)
    {

	this.listeners.remove (l);

    }

    public DatasetGroup getGroup ()
    {

	return this.group;

    }

    public void setGroup (DatasetGroup g)
    {

	this.group = g;

    }

    public void setKeyValue (int    keyCol,
			     int    valueCol)
	                     throws IllegalArgumentException,
	                            IllegalStateException,
	                            QueryParseException
    {

	if (!this.parsed ())
	{

	    throw new IllegalStateException ("Cannot specify the key and value columns until a query has been specified and parsed.");

	}

	if (keyCol < 1)
	{

	    throw new IllegalArgumentException ("Key column index must be a minimum of 1.");

	}

	if (valueCol < 1)
	{

	    throw new IllegalArgumentException ("Value column index must be a minimum of 1.");

	}

	List cols = this.getColumns ();

	if (keyCol > cols.size ())
	{

	    throw new IllegalArgumentException ("Key column index must be a minimum of " + 
						cols.size () + 
						".");

	}

	if (valueCol > cols.size ())
	{

	    throw new IllegalArgumentException ("Value column index must be a minimum of " + 
						cols.size () + 
						".");

	}

	SelectItemExpression vexp = (SelectItemExpression) cols.get (valueCol - 1);

	Class vc = vexp.getExpectedReturnType (this);

	if (!Utilities.isNumber (vc))
	{

	    throw new IllegalArgumentException ("Value column: " +
						valueCol +
						" will evaluate to an instance of type: " +
						vc.getName () +
						", but only columns that return numbers are allowed.");
	    
	}

	this.key = keyCol;
	this.value = valueCol;

    }

    /**
     * Exectute the query and return the results.  A reference to the results is also held to 
     * allow them to be iterated over.  If you plan on re-using this data source then 
     * you should call: {@link #clearResults()} to free up the references to the results.
     *
     * @param l The List of objects to execute the query on.
     * @return The results.
     * @throws QueryExecutionException If the query cannot be executed, or if the query
     *                                 is set to return objects rather than "columns".
     */
    public QueryResults executeQuery (List   l)
	                              throws QueryExecutionException
    {

	if (this.isWantObjects ())
	{

	    throw new QueryExecutionException ("Only SQL statements that return columns (not the objects passed in) can be used.");

	}

	if ((this.key == 0)
	    ||
	    (this.value == 0)
	   )
	{

	    throw new IllegalStateException ("Key and/or value columns not specified.");

	}

	QueryResults qr = super.execute (l);

	List res = qr.getResults ();

	Map nValues = new LinkedHashMap ();

	for (int i = 0; i < res.size (); i++)
	{

	    List resR = (List) res.get (i);

	    // Get the key.
	    Object k = resR.get (this.key - 1);
	    
	    // Get the value.
	    Object v = resR.get (this.value - 1);

	    String kv = null + "";

	    if (k == null)
	    {

		nValues.put (kv,
			     v);

	    } 

	    // See if the key is "comparable".
	    if (k instanceof Comparable)
	    {

		nValues.put (k,
			     v);

	    } else {

		kv = k.toString ();

		nValues.put (kv,
			     v);

	    }

	}

	// Just switch them, this way enables any client of the data to
	// still iterate over them without fear of a ConcurrentModificationException
	// occuring.
	this.values = nValues;

	// Notify our listeners.
	DatasetChangeEvent dce = new DatasetChangeEvent (this,
							 this);

	for (int i = 0; i < this.listeners.size (); i++)
	{

	    DatasetChangeListener d = (DatasetChangeListener) this.listeners.get (i);

	    d.datasetChanged (dce);

	}

	return qr;

    }

    public int getItemCount ()
    {

	return this.values.size ();

    }

    public Number getValue (int index)
    {

	int c = 0;

	Iterator iter = this.values.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Object k = iter.next ();

	    if (index == c)
	    {

		return (Number) this.values.get (k);

	    }

	    c++;

	}

	return new Double (-1);

    }

    public int getIndex (Comparable k)
    {

	int c = 0;

	Iterator iter = this.values.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Comparable ko = (Comparable) iter.next ();

	    if (ko.compareTo (k) == 0)
	    {

		return c;

	    }

	    c++;

	}

	return -1;	

    }

    public Comparable getKey (int index)
    {

	int c = 0;

	Iterator iter = this.values.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Object k = iter.next ();

	    if (index == c)
	    {

		return (Comparable) k;

	    }

	    c++;

	}

	return null;

    }

    public Number getValue (Comparable key)
    {

	return (Number) this.values.get (key);

    }

    public List getKeys ()
    {

	return new ArrayList (this.values.keySet ());

    }

}
