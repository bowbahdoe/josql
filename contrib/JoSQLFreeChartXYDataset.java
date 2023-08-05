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
import java.util.HashMap;

import org.jfree.data.xy.XYDataset;

import org.jfree.data.DomainOrder;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetChangeEvent;

import org.josql.Query;
import org.josql.QueryResults;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.internal.Utilities;

import org.josql.expressions.SelectItemExpression;

public class JoSQLFreeChartXYDataset extends Query implements XYDataset
{

    private List results = null;
    private Map series = new HashMap ();
    private List listeners = new ArrayList ();
    private DatasetGroup group = null;

    public JoSQLFreeChartXYDataset ()
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

    public int indexOf (Comparable c)
    {

	// Must be an integer.
	return ((Integer) c).intValue ();

    }

    public Comparable getSeriesKey (int series)
    {

	return Integer.valueOf (series);

    }

    public int getSeriesCount ()
    {

	return this.series.size ();

    }

    public void removeSeries (int series)
    {

	this.series.remove (Integer.valueOf (series));

    }

    public void addSeries (int    series,
			   int    xCol,
			   int    yCol)
	                   throws IllegalArgumentException,
	                          IllegalStateException,
	                          QueryParseException
    {

	if (!this.parsed ())
	{

	    throw new IllegalStateException ("Cannot add a series until a query has been specified and parsed.");

	}

	if (xCol < 1)
	{

	    throw new IllegalArgumentException ("X column index must be a minimum of 1.");

	}

	if (yCol < 1)
	{

	    throw new IllegalArgumentException ("Y column index must be a minimum of 1.");

	}

	List cols = this.getColumns ();

	if (xCol > cols.size ())
	{

	    throw new IllegalArgumentException ("X column index must be a minimum of " + 
						cols.size () + 
						".");

	}

	if (yCol > cols.size ())
	{

	    throw new IllegalArgumentException ("Y column index must be a minimum of " + 
						cols.size () + 
						".");

	}

	SelectItemExpression xexp = (SelectItemExpression) cols.get (xCol - 1);

	Class xc = xexp.getExpectedReturnType (this);

	if (!Utilities.isNumber (xc))
	{

	    throw new IllegalArgumentException ("X column: " +
						xexp +
						" will evaluate to an instance of type: " +
						xc.getName () +
						", but only columns that return numbers are allowed.");	    

	}

	SelectItemExpression yexp = (SelectItemExpression) cols.get (yCol - 1);

	Class yc = yexp.getExpectedReturnType (this);

	if (!Utilities.isNumber (yc))
	{

	    throw new IllegalArgumentException ("Y column: " +
						yexp +
						" will evaluate to an instance of type: " +
						yc.getName () +
						", but only columns that return numbers are allowed.");
	    
	}

	this.series.put (Integer.valueOf (series),
			 new Series (xCol,
				     yCol));

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

	QueryResults qr = super.execute (l);

	this.results = qr.getResults ();

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

    public List getResults ()
    {

	return this.results;

    }

    public void clearResults ()
    {

	this.results = null;

    }    

    public int getItemCount (int series)
    {

	if (this.results == null)
	{

	    return 0;

	}

	return this.results.size ();

    }

    public double getXValue (int series,
			     int item)
    {

	// Bad man!
	return ((Double) this.getX (series,
				    item)).doubleValue ();

    }

    public double getYValue (int series,
			     int item)
    {

	// Bad man!
	return ((Double) this.getY (series,
				    item)).doubleValue ();

    }

    public Number getX (int series,
			int item)
    {

	if (this.results == null)
	{

	    return new Double (0);

	}

	List l = (List) this.results.get (item);

	Series s = (Series) this.series.get (Integer.valueOf (series));

	if (s == null)
	{

	    return new Double (0);

	}

	Number n = (Number) l.get (s.xCol - 1);

	if (n instanceof Double)
	{

	    return n;

	} else {

	    return new Double (n.doubleValue ());

	}

    }

    public Number getY (int series,
			int item)
    {

	if (this.results == null)
	{

	    return new Double (0);

	}

	List l = (List) this.results.get (item);

	Series s = (Series) this.series.get (Integer.valueOf (series));

	if (s == null)
	{

	    return new Double (0);

	}

	Number n = (Number) l.get (s.yCol - 1);

	if (n instanceof Double)
	{

	    return n;

	} else {

	    return new Double (n.doubleValue ());

	}

    }

    public DomainOrder getDomainOrder ()
    {

	return DomainOrder.ASCENDING;

    }

    private class Series
    {

	public int xCol = 0;
	public int yCol = 0;

	public Series (int x,
		       int y)
	{

	    this.xCol = x;
	    this.yCol = y;

	}

    }

}
