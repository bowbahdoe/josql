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
package org.josql.filters;

import java.io.FileFilter;
import java.io.File;

import org.josql.Query;
import org.josql.QueryParseException;

/**
 * A {@link FileFilter} that uses a JoSQL statement to provide the filtering.
 * The value returned by the {@link #accept(File)} method is determined by executing the
 * WHERE clause of a JoSQL statement on each File passed in.
 * <p>
 * Since this uses a sub-set of the JoSQL functionality certain restrictions apply:
 * <ul>
 *   <li>The SELECT clause is ignored.</li>
 *   <li>The object defined in the FROM clause <b>must</b> be java.io.File</li>
 *   <li>EXECUTE ON functions are not supported here since there is no way to get the entire set
 *       of objects to work on.</li>
 *   <li>ORDER BY, GROUP BY, HAVING and LIMIT clauses are ignored.</li>
 * </ul>  
 * Examples:
 * <p>
 * <pre>
 *   SELECT *
 *   FROM   {@link File java.io.File}
 *   WHERE  name $LIKE '%.html'
 *   AND    lastModified > {@link org.josql.functions.ConversionFunctions#toDateMillis(String,String) toDateMillis}('12-04-2004')
 *   AND    file
 * </pre>
 */  
public class JoSQLFileFilter extends AbstractJoSQLFilter implements FileFilter
{

    /**
     * Init this file filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not {@link File}.
     */
    public JoSQLFileFilter (String q)
	                    throws QueryParseException
    {

	super (q);

    }

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not {@link File}.
     */
    public JoSQLFileFilter (Query  q)
	                    throws IllegalStateException,
	                           QueryParseException
    {

	super (q);

    }

    public boolean accept (Object o)
    {

	if (!(o instanceof File))
	{

	    throw new IllegalArgumentException ("Expected object to be of type: " + 
						File.class.getName () +
						", got: " +
						o.getClass ().getName ());

	}

	return this.accept ((File) o);

    }

    /**
     * Apply the WHERE clause of the statement to the {@link File} passed in.
     * If an exception is thrown by the execution of the WHERE clause the Query 
     * is marked as "dirty" and the where clause is no longer executed on the passed in 
     * files (since it is likely that the WHERE clause will fail for all File objects).  
     * You can get access to exception by using: {@link #getException()}.
     *
     * @param f The file to evaluate the WHERE on.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code>.
     */
    public boolean accept (File f)
    {
	
	if (this.badQuery)
	{

	    return false;

	}

	try
	{

	    return this.q.getWhereClause ().isTrue (f,
						    this.q);

	} catch (Exception e) {

	    this.badQuery = true;

	    this.exp = e;

	}

	return false;

    }

    /**
     * Always returns {@link File}.
     *
     * @return The file class.
     */
    public Class getExpectedClass ()
    {

	return File.class;

    }

}
