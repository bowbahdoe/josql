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
import java.util.Arrays;

import org.jfree.data.category.CategoryDataset;

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

public class JoSQLFreeChartCategoryDataset extends Query implements CategoryDataset
{

    private QueryResults results = null;
    private int xCol = 0;
    private List yCols = null;
    private List listeners = new ArrayList ();
    private DatasetGroup group = null;

    public JoSQLFreeChartCategoryDataset ()
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

    /**
     * Get any results, will be null unless {@link #execute(List)} has been called.
     *
     * @return The results.
     */
    public QueryResults getResults ()
    {

	return this.results;

    }

    /**
     * Clear any results.
     */
    public void clearResults ()
    {

	this.results = null;

    }

    public void setGroup (DatasetGroup g)
    {

	this.group = g;

    }

    public void define (int      xCol,
			Object[] yCols)
                        throws   IllegalArgumentException,
	                         IllegalStateException,
	                         QueryParseException
    {

	this.define (xCol,
		     Arrays.asList (yCols));

    }

    public void define (int    xCol,
			int[]  yCols)
                        throws IllegalArgumentException,
	                       IllegalStateException,
	                       QueryParseException
    {

	List l = new ArrayList (yCols.length);

	for (int i = 0; i < yCols.length; i++)
	{

	    l.add (Integer.valueOf (yCols[i]));

	}

	this.define (xCol,
		     l);

    }    

    public void define (int    xCol,
			List   yCols)
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

	for (int i = 0; i < yCols.size (); i++)
	{

	    Object o = yCols.get (i);

	    if (o instanceof String)
	    {

		try
		{

		    yCols.set (i,
			       Integer.valueOf ((String) o));

		    continue;

		} catch (Exception e) {

		    throw new IllegalArgumentException ("Unable to convert y column indicator: " + 
							o +
							" to an integer.");

		}

	    }

	    if (o instanceof Number)
	    {

		yCols.set (i,
			   Integer.valueOf (((Number) o).intValue ()));

		continue;

	    }

	    if (!(o instanceof Integer))
	    {

		throw new IllegalArgumentException ("Expected y column indicator: " +
						    o +
						    " to be either a number or a string representing a number.");
		
	    }

	}

	List cols = this.getColumns ();

	for (int i = 0; i < yCols.size (); i++)
	{

	    Integer in = (Integer) yCols.get (i);

	    if (in.intValue () < 1)
	    {

		throw new IllegalArgumentException ("Y column index must be a minimum of 1.");

	    }

	    if (in.intValue () > cols.size ())
	    {

		throw new IllegalArgumentException ("Y column index must be a maximum of " + 
						    cols.size () + 
						    ".");

	    }

	    SelectItemExpression yexp = (SelectItemExpression) cols.get (in.intValue () - 1);

	    if (yexp.getAlias () == null)
	    {

		throw new IllegalArgumentException ("Y column: " +
						    yexp +
						    " must have an alias.");

	    }

	    Class yc = yexp.getExpectedReturnType (this);

	    if (!Utilities.isNumber (yc))
	    {

		throw new IllegalArgumentException ("Y column: " +
						    yexp +
						    " will evaluate to an instance of type: " +
						    yc.getName () +
						    ", but only columns that return numbers are allowed.");
	    
	    }

	}

	if (xCol > cols.size ())
	{

	    throw new IllegalArgumentException ("X column index must be a maximum of " + 
						cols.size () + 
						".");

	}

	SelectItemExpression xexp = (SelectItemExpression) cols.get (xCol - 1);

	Class xc = xexp.getExpectedReturnType (this);

	xc = Utilities.getObjectClass (xc);

	if (!(Comparable.class.isAssignableFrom (xc)))
	{

	    throw new IllegalArgumentException ("X column: " +
						xexp +
						" will evaluate to an instance of type: " +
						xc.getName () +
						", but only columns that implement: " +
						Comparable.class.getName () +
						" can be used.");	    

	}

	this.yCols = yCols;

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

	this.results = super.execute (l);

	// Notify our listeners.
	DatasetChangeEvent dce = new DatasetChangeEvent (this,
							 this);

	for (int i = 0; i < this.listeners.size (); i++)
	{

	    DatasetChangeListener d = (DatasetChangeListener) this.listeners.get (i);

	    d.datasetChanged (dce);

	}

	return this.results;

    }

    public DomainOrder getDomainOrder ()
    {

	return DomainOrder.ASCENDING;

    }

    public int getRowCount ()
    {

	return this.results.getResults ().size ();

    }

    public int getColumnCount ()
    {

	return this.yCols.size ();

    }

    public Number getValue (int row,
			    int col)
    {

	List l = (List) this.results.getResults ().get (row);

	return (Number) l.get (col);

    }

    public List getRowKeys ()
    {

	List ks = new ArrayList ();

	for (int i = 0; i < this.results.getResults ().size (); i++)
	{

	    List l = (List) this.results.getResults ().get (i);

	    ks.add (l.get (this.xCol));

	}

	return ks;

    }

    public Number getValue (Comparable row,
			    Comparable col)
    {

	int rk = this.getRowIndex (row);
	int ck = this.getColumnIndex (col);

	List l = (List) this.results.getResults ().get (rk);

	return (Number) l.get (ck);

    }

    public Comparable getColumnKey (int c)
    {

	return (Comparable) this.getColumnKeys ().get (c);

    }

    public List getColumnKeys ()
    {

	List ks = new ArrayList ();

	for (int i = 0; i < this.yCols.size (); i++)
	{

	    Integer in = (Integer) this.yCols.get (i);

	    SelectItemExpression sei = (SelectItemExpression) this.getColumns ().get (in.intValue () - 1);

	    ks.add (sei.getAlias ());

	}

	return ks;

    }

    public int getColumnIndex (Comparable c)
    {

	List ck = this.getColumnKeys ();

	for (int i = 0; i < ck.size (); i++)
	{

	    if (((Comparable) ck.get (i)).compareTo (c) == 0)
	    {

		return i;

	    }

	}

	return -1;

    }

    public int getRowIndex (Comparable c)
    {

	List rk = this.getRowKeys ();

	for (int i = 0; i < rk.size (); i++)
	{

	    if (((Comparable) rk.get (i)).compareTo (c) == 0)
	    {

		return i;

	    }

	}

	return -1;

    }

    public Comparable getRowKey (int k)
    {

	return (Comparable) this.getRowKeys ().get (k);

    }

}
