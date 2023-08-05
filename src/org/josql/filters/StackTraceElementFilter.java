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

import java.util.List;
import java.util.ArrayList;

import org.josql.Query;
import org.josql.QueryParseException;
import org.josql.QueryExecutionException;

/**
 * This class allows you to filter the stack trace of a Throwable, so cutting down
 * on the (sometimes) pointless long stack traces.
 * <p>
 * Usage:
 * <pre>
 *    SELECT *
 *    FROM   java.lang.StackTraceElement
 *    WHERE  className LIKE 'org.josql.%'
 * </pre>
 * And then to use in code:
 * <pre>
 *    StackTraceElementFilter f = new StackTraceElementFilter (sql);
 *    f.filter (new Throwable ());
 * </pre>
 */  
public class StackTraceElementFilter extends AbstractJoSQLFilter
{

    /**
     * Init this filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not equal to the expected class.
     */
    public StackTraceElementFilter (String q)
	                            throws QueryParseException
    {

	super (q);

    }

    /**
     * Get the expected class.
     * 
     * @return {@link StackTraceElement}.
     */
    public Class getExpectedClass ()
    {

	return StackTraceElement.class;

    }

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not as expected.
     */
    public StackTraceElementFilter (Query  q)
	                            throws IllegalStateException,
	                                   QueryParseException
    {

	super (q);

    }

    /*
     * Returns <code>true</code> if the where clause evaluates to true for the 
     * passed in StackTraceElement.
     *
     * @param o The object to evaluate the WHERE clause against.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code> for the specified
     *         object.
     */
    public boolean accept (Object o)
    {

	try
	{

	    return this.accept ((StackTraceElement) o);

	} catch (Exception e) {

	    this.exp = e;

	    return false;

	}

    }

    /**
     * Returns <code>true</code> if the where clause evaluates to true for the 
     * passed in StackTraceElement.
     *
     * @param s The object to evaluate the WHERE clause against.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code> for the specified
     *         object.
     */
    public boolean accept (StackTraceElement s)
	                   throws            QueryExecutionException
    {

	return this.q.getWhereClause ().isTrue (s,
						this.q);

    }
    
    /**
     * Filter the specified stack trace and return the new stack trace
     * that can then be set in the throwable.
     *
     * @param s The stack trace.
     * @return The new stack trace, filtered.
     * @throws QueryExecutionException If the where clause cannot be
     *         executed against a particular element.
     */
    public StackTraceElement[] filterStackTrace (StackTraceElement[] s)
	                                         throws              QueryExecutionException
    {

	if (s == null)
	{

	    return s;

	}

	List l = new ArrayList ();

	for (int i = 0; i < s.length; i++)
	{

	    if (this.accept (s[i]))
	    {

		l.add (s[i]);

	    }

	}

	// Can't believe I have to fricking do this... can't just cast to
	// StackTraceElement[]... grr...
	StackTraceElement[] a = new StackTraceElement[l.size ()];

	for (int i = 0; i < l.size (); i++)
	{

	    a[i] = (StackTraceElement) l.get (i);

	}

	return a;

    }

    /**
     * Filter the throwable, but will also filter any causes (right up the cause chain)
     * as well.  Equivalent to calling: {@link #filter(Throwable,boolean)} with the 
     * boolean set to <code>true</code>.
     *
     * @param t The throwable instance to filter.
     * @throws QueryExecutionException If the where clause cannot be executed.
     */
    public void filter (Throwable t)
	                throws    QueryExecutionException
    {

	this.filter (t,
		     true);

    }

    /**
     * Filter the throwable, but will also filter any causes (right up the cause chain)
     * as well if the <b>filterCause</b> boolean is set to <code>true</code>.
     *
     * @param t The throwable instance to filter.
     * @param filterCause When set to <code>true</code> will also filter the cause 
     *                    chain as well.
     * @throws QueryExecutionException If the where clause cannot be executed.
     */
    public void filter (Throwable t,
			boolean   filterCause)
	                throws    QueryExecutionException
    {

	if (filterCause)
	{

	    Throwable c = t.getCause ();

	    if (c != null)
	    {

		this.filter (c,
			     filterCause);

	    }

	}

	t.setStackTrace (this.filterStackTrace (t.getStackTrace ()));

    }


}
