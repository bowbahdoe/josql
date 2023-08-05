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

import org.josql.Query;
import org.josql.QueryParseException;

/**
 * This class just provides a base-abstract implementation that allow "Filters" to be
 * built on top of it.
 */  
public abstract class AbstractJoSQLFilter
{

    protected Query q = null;
    protected Exception exp = null;
    protected boolean badQuery = false;

    /**
     * Protected constructor to allow sub-classes to init the query when they are ready.
     */
    protected AbstractJoSQLFilter ()
    {

    }

    /**
     * Init this filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not equal to the expected class.
     */
    public AbstractJoSQLFilter (String q)
	                        throws QueryParseException
    {

	this.setQuery (q);

    }

    /**
     * Should sub-classes should return the type that they expect to be present in 
     * a Query.
     * 
     * @return The class that should be used in the Query for the filter.
     */
    public abstract Class getExpectedClass ();

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not as expected.
     */
    public AbstractJoSQLFilter (Query  q)
	                        throws IllegalStateException,
	                               QueryParseException
    {

	this.setQuery (q);

    }

    private void checkFrom ()
	                    throws QueryParseException
    {

	if (!this.getExpectedClass ().isAssignableFrom (this.q.getFromObjectClass ()))
	{

	    throw new QueryParseException ("Query FROM class is: " + 
					   this.q.getFromObjectClass ().getName () +
					   ", however only: " +
					   this.getExpectedClass ().getName () + 
					   " or sub-classes are supported.");

	}

    }

    /*
     * Optional operation that allows a "generic" filter to be created.  Sub-classes that do not
     * wish to support this operation should throw an instance of: {@link UnsupportedOperationException}.
     *
     * @param o The object to evaluate the WHERE clause against.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code> for the specified
     *         object.
     */
    public abstract boolean accept (Object o)
	                            throws UnsupportedOperationException;

    /**
     * Clear any exception stored.
     */
    public void clearException ()
    {

	this.exp = null;
	this.badQuery = false;

    }

    /**
     * Most "filter accept" methods do not allow for any exceptions to be thrown however since
     * the execution of the WHERE clause on the object can cause the throwing of a
     * {@link QueryParseException} it should be captured.  If the exception is thrown then
     * this method will return it.  
     *
     * @return The exception thrown by the execution of the WHERE clause in {@link #accept(Object)}
     *         or by sub-class/interface specific methods, this may be null if no exception was thrown.
     */
    public Exception getException ()
    {

	return this.exp;

    }

    /**
     * Set a new Query (string form) for use in this filter.
     *
     * @param q The Query to use.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not as expected.
     */
    public void setQuery (String q)
	                  throws QueryParseException
    {

	this.q = new Query ();
	this.q.parse (q);

	this.badQuery = false;
	this.exp = null;

	this.checkFrom ();

    }

    /**
     * Set a new Query object for use in this filter.
     *
     * @param q The Query to use.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not as expected.
     */
    public void setQuery (Query  q)
	                  throws IllegalStateException,
	                         QueryParseException
    {

	if (!q.parsed ())
	{

	    throw new IllegalStateException ("Query has not yet been parsed.");

	}

	this.q = q;

	this.checkFrom ();

	this.badQuery = false;
	this.exp = null;

    }

    /**
     * Get the Query we are using to process objects.
     *
     * @return The Query.
     */
    public Query getQuery ()
    {

	return this.q;

    }

}
