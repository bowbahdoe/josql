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

import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JRField;

import org.josql.QueryExecutionException;
import org.josql.Query;
import org.josql.QueryResults;

/**
 * A data source suitable for use with <a href="http://jasperreports.sourceforge.net/">JasperReports</a>.  
 * This is basically just an extension to {@link Query} that allows the
 * results to be iterated over, thereby providing the ability for objects to be reported on
 * that are held in memory.
 * <p>
 * One limitation here is that the SQL query must return columns rather than the objects
 * since the values need to be mapped by JasperReports.  For example:
 * <pre>
 *   SELECT lastModified,
 *          name
 *   FROM   java.io.File
 *   WHERE  name LIKE '%.html'
 * </pre>
 * <p>
 * This query would work but it should be noted that the select "columns" (since they do not have 
 * aliases assigned) will be labeled 1, 2, X and so on.
 * You can assign aliases to the "columns" and then use them in the report definition file.
 * <p>
 * Please note: due to my bewilderment (and the fact that I can't get the examples to work ;)
 * I haven't been able to adequately test this implementation, in the rudementary tests I 
 * performed it seemed to work.  If it doesn't please send me an example so that I can try
 * it!
 */  
public class JoSQLJRDataSource extends Query implements JRRewindableDataSource
{

    private int row = 0;
    private List results = null;

    public JoSQLJRDataSource ()
    {

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

    public Object getFieldValue (JRField field)
    {

	// Get the row of objects.
	List res = (List) this.results.get (this.row);

	Integer ind = (Integer) this.getAliases ().get (field.getName ());

	int i = -1;

	if (ind != null)
	{

	    i = ind.intValue ();

	    if (i > (res.size () - 1))
	    {

		return null;

	    }

	}

	// Get the index for the field name.
	return res.get (i);

    }

    public boolean next ()
    {

	if (this.row < (this.results.size () - 1))
	{

	    this.row++;

	    return true;

	}

	return false;

    }

    public void moveFirst ()
    {

	this.row = 0;

    }

}
