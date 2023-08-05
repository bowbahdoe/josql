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

import com.gentlyweb.utils.StringUtils;

import org.josql.Query;
import org.josql.QueryParseException;

/**
 * A general purpose object filter that uses a JoSQL statement to provide the filtering.
 * The value returned by the {@link #accept(Object)} method is determined by executing the
 * JoSQL WHERE clause passed in.  A "wrapper" is created around the WHERE clause to make it
 * a fully-formed JoSQL statement.
 */  
public class DefaultObjectFilter extends AbstractJoSQLFilter
{

    private static String qWrapper = "SELECT * FROM [[CLASS]] WHERE [[WHERE]]";
    private static String CLASS_TAG = "[[CLASS]]";
    private static String WHERE_TAG = "[[WHERE]]";

    private Class c = null;

    /**
     * Init this filter with the where clause, note the class specified will be used in the
     * FROM clause.
     * 
     * @param w The where clause.
     * @param c The class of the objects to filter.
     * @throws QueryParseException If there is an issue with the parsing of the query.
     */
    public DefaultObjectFilter (String w,
				Class  c)
	                        throws QueryParseException
    {

	if (c == null)
	{

	    throw new QueryParseException ("Class must be specified");

	}

	this.c = c;

	String q = StringUtils.replaceString (qWrapper,
					      DefaultObjectFilter.CLASS_TAG,
					      c.getName ());

	w = w.trim ();

	if (w.toLowerCase ().startsWith ("where"))
	{

	    w = w.substring (5);

	}

	q = StringUtils.replaceString (q,
				       DefaultObjectFilter.WHERE_TAG,
				       w);

	this.setQuery (q);

    }

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not {@link File}.
     */
    public DefaultObjectFilter (Query  q)
	                        throws IllegalStateException,
	                               QueryParseException
    {

	this.c = q.getFromObjectClass ();

	this.setQuery (q);

    }

    /**
     * Apply the WHERE clause of the statement to the object passed in.
     * If an exception is thrown by the execution of the WHERE clause the Query 
     * is marked as "dirty" and the where clause is no longer executed on the passed in 
     * objects (since it is likely that the WHERE clause will fail for all objects).  
     * You can get access to exception by using: {@link #getException()}.
     *
     * @param o The object to evaluate the WHERE on.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code>.
     */
    public boolean accept (Object o)
    {
	
	if (this.badQuery)
	{

	    return false;

	}

	try
	{

	    return this.q.getWhereClause ().isTrue (o,
						    this.q);

	} catch (Exception e) {

	    this.badQuery = true;

	    this.exp = e;

	}

	return false;

    }

    /**
     * Get the class the Query expects to operate on.
     *
     * @return The class.
     */
    public Class getExpectedClass ()
    {

	return this.c;

    }

}
