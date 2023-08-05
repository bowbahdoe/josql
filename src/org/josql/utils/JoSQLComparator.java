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
package org.josql.utils;

import java.util.List;
import java.util.Comparator;

import org.josql.Query;
import org.josql.QueryParseException;
import org.josql.QueryExecutionException;

import org.josql.internal.ListExpressionComparator;

/**
 * This class allows the ORDER BY clause of a JoSQL SQL clause to be used
 * as a Comparator.  It should be noted that is the same as performing: {@link Query#execute(List)}
 * but there are times when having a separate comparator is desirable.
 * The EXECUTE ON ALL clause is supported but you must call: {@link #doExecuteOn(List)}
 * first to ensure that they are executed.
 * <p>
 * This class is basically just a thin wrapper around using the comparator gained by
 * calling: {@link Query#getOrderByComparator()}.
 * <p>
 * A note on performance, for small numbers of objects (around 1000) this comparator
 * has (for vanilla accessors, no function calls) pretty comparable performance against a 
 * hand-coded Java Comparator that performs the same function.  However start to scale the
 * numbers of objects and performance degrades, in testing for ~34000 FileWrapper objects
 * to order by: <code>path DESC, lastModified, name, length</code> took around: 1300ms.
 * The hand-coded Java Comparator took around: 180ms!  The upshot is, if you need flexibility
 * and do not need to order large numbers of objects then use this kind of Comparator, if
 * performance and numbers of objects is an issue then hand-rolling your own Comparator
 * is probably best.  As a side-note, to perform the following order by: 
 * <code>lower(path) DESC, lastModified, name, length</code> using a JoSQLComparator took:
 * about: 1400ms.  However modifying the hand-coded Comparator to use: 
 * {@link String#compareToIgnoreCase(String)} then took about 860ms!  And if you using: 
 * {@link String#toLowerCase()} for each string instead, it then takes about: 1800ms!
 * (Meaning that in certain circumstances JoSQL can be faster!)
 * <p>
 * <h3>Caching</h3>
 * <p>
 * It is not uncommon for a Comparator (even using the effecient merge-sort implementation of
 * {@link java.util.Collections#sort(List,Comparator)}) to perform thousands (even millions!) 
 * of comparisons.<br /><br />
 * However since JoSQL does not automatically cache the results of calls to functions and
 * results of accessor accesses the performance of this kind of "dynamic" Comparator can 
 * quickly degrade.  To mitigate this it is possible to turn "caching" on whereby the
 * Comparator will "remember" the results of the functions on a per object basis and use those
 * values instead of calling them again.  This is not without it's downside however.  
 * Firstly since a reference to the object will be held it is important (if caching is used
 * that you call: {@link #clearCache()} once the Comparator has been used to free up those
 * references (it was considered using a {@link java.util.WeakHashMap} but that doesn't provide 
 * exactly what's needed here).<br /><br />
 * It is recommended that caching is turned on when the Comparator is to be used in a sort
 * operation , i.e. calling: {@link java.util.Collections#sort(List,Comparator)} or similar 
 * (however careful consideration needs to be given to the amount of memory that this 
 * may consume, i.e. 4 bytes = 1 object reference, plus 1 List, plus 4 bytes per order 
 * by "column" it soon adds up)<br /><br />
 * If the comparator is to be used in a {@link java.util.TreeMap} or {@link java.util.TreeSet} 
 * then caching should not be used since the values may (and perhaps should) change over time 
 * but due to caching the order won't change.
 */  
public class JoSQLComparator implements Comparator
{

    private Query q = null;
    private Exception exp = null;
    private ListExpressionComparator c = null;

    /**
     * Execute the EXECUTE ON ALL expressions.  
     *
     * @param l The list to execute the expressions on.
     */
    public void doExecuteOn (List   l)
	                     throws QueryExecutionException
    {

	this.q.doExecuteOn (l,
			    Query.ALL);

    }

    /**
     * Clear the cache, it is VITAL that you call this method before you use
     * the comparator (if it has been used before) otherwise data objects will
     * be "left around" and preventing the GC from cleaning them up.
     */
    public void clearCache ()
    {

	if (this.q != null)
	{

	    this.c.clearCache ();

	}

    }

    /**
     * Return whether this comparator uses caching to improve performance.
     *
     * @return <code>true</code> if caching is on.
     * @throws IllegalStateException If the query has not yet been parsed or set.
     */
    public boolean isCaching ()
	                     throws  IllegalStateException
    {

	if ((this.q == null)
	    ||
	    (!this.q.parsed ())
	   )
	{

	    throw new IllegalStateException ("Query has not yet been parsed.");

	}

	return this.c.isCaching ();

    }

    /**
     * Set whether the comparator should use caching to improve performance.
     *
     * @param b Set to <code>true</code> to turn caching on.
     * @throws IllegalStateException If the query has not yet been parsed or set.
     */
    public void setCaching (boolean b)
	                    throws  IllegalStateException
    {

	if ((this.q == null)
	    ||
	    (!this.q.parsed ())
	   )
	{

	    throw new IllegalStateException ("Query has not yet been parsed.");

	}

	this.c.setCaching (b);

    }

    /**
     * Init this filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query.
     */
    public JoSQLComparator (String  q)
	                    throws  QueryParseException
    {

	this.setQuery (q);

    }

    /**
     * Compares the objects as according to the ORDER BY clause.
     *
     * @param o1 The first object.
     * @param o2 The second object.
     */
    public int compare (Object o1,
			Object o2)
    {

	try
	{

	    return c.ci (o1,
			 o2);

	} catch (Exception e) {

	    this.exp = e;

	    return 0;

	}

    }

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not as expected.
     */
    public JoSQLComparator (Query   q)
	                    throws  IllegalStateException,
	                            QueryParseException
    {

	this.setQuery (q);

    }

    /**
     * The {@link Comparator#compare(Object,Object)} method does not allow for 
     * any exceptions to be thrown however since the execution of the ORDER BY clause 
     * on the objects can cause the throwing of a {@link QueryParseException} it should 
     * be captured.  If the exception is thrown then this method will return it.  
     *
     * @return The exception thrown by the execution of the ORDER BY clause in {@link #compare(Object,Object)}
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
    public void setQuery (String  q)
	                  throws  QueryParseException
    {

	this.q = new Query ();
	this.q.parse (q);

	this.c = (ListExpressionComparator) this.q.getOrderByComparator ();

	this.exp = null;

    }

    /**
     * Set a new Query object for use in this filter.
     *
     * @param q The Query to use.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not as expected.
     */
    public void setQuery (Query   q)
	                  throws  IllegalStateException,
	                          QueryParseException
    {

	if (!q.parsed ())
	{

	    throw new IllegalStateException ("Query has not yet been parsed.");

	}

	this.q = q;

	this.c = (ListExpressionComparator) this.q.getOrderByComparator ();

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
