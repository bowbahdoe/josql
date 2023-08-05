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

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.josql.Query;
import org.josql.QueryParseException;

/**
 * A {@link Filter} that uses a JoSQL statement to provide the filtering.
 * The value returned by the {@link #isLoggable(LogRecord)} method is determined by executing the
 * WHERE clause of a JoSQL statement on each {@link LogRecord} passed in.
 * <p>
 * Since this uses a sub-set of the JoSQL functionality certain restrictions apply:
 * <ul>
 *   <li>The SELECT clause is ignored.</li>
 *   <li>The object defined in the FROM clause <b>must</b> be java.util.logging.LogRecord</li>
 *   <li>EXECUTE ON functions are not supported here since there is no way to get the entire set
 *       of objects to work on.</li>
 *   <li>ORDER BY, GROUP BY, HAVING and LIMIT clauses are ignored.</li>
 * </ul>  
 * Examples:
 * <p>
 * <pre>
 *   SELECT *
 *   FROM   {@link LogRecord java.tuil.logging.LogRecord}
 *   WHERE  name $LIKE '%.html'
 *   AND    millis > {@link org.josql.functions.ConversionFunctions#toDateMillis(String,String) toDateMillis}('12-04-2004')
 *   AND    sequenceNumber BETWEEN (10000 AND 20000)
 *   AND    message $LIKE '%internal%'
 *   AND    loggerName = 'internal_logger'
 *   AND    level.name IN ('SEVERE', 'WARNING')
 * </pre>
 * <p>
 * If you are using a custom log record then you can always just extend this class and override
 * the {@link #getExpectedClass()} method to return your specific sub-class.
 */  
public class JoSQLLogRecordFilter extends DefaultObjectFilter 
{

    protected Class expected = LogRecord.class;

    /**
     * Init this filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not {@link LogRecord}.
     */
    public JoSQLLogRecordFilter (String q)
	                         throws QueryParseException
    {

	super (q,
               LogRecord.class);

    }

    /**
     * Init this filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not {@link LogRecord}.
     */
    public JoSQLLogRecordFilter (Query  q)
	                         throws IllegalStateException,
	                                QueryParseException
    {

	super (q);

    }

    public boolean accept (Object o)
    {

	if (!(o instanceof LogRecord))
	{

	    throw new IllegalArgumentException ("Expected object to be of type: " + 
						LogRecord.class.getName () +
						", got: " +
						o.getClass ().getName ());

	}

	return super.accept ((LogRecord) o);

    }

    /**
     * Apply the WHERE clause of the statement to the {@link LogRecord} passed in.
     * If an exception is thrown by the execution of the WHERE clause the Query 
     * is marked as "dirty" and the where clause is no longer executed on the passed in 
     * files (since it is likely that the WHERE clause will fail for all LogRecord objects).  
     * You can get access to exception by using: {@link #getException()}.
     *
     * @param l The log record to evaluate the WHERE on.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code>.
     */
    public boolean isLoggable (LogRecord l)
    {
	
	if (this.badQuery)
	{

	    return false;

	}

	try
	{

	    return this.q.getWhereClause ().isTrue (l,
						    this.q);

	} catch (Exception e) {

	    this.badQuery = true;

	    this.exp = e;

	}

	return false;

    }

    /**
     * Always returns {@link LogRecord}.
     *
     * @return The log record class.
     */
    public Class getExpectedClass ()
    {

	return LogRecord.class;

    }

}
