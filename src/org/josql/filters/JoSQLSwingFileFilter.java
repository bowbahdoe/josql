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

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.josql.Query;
import org.josql.QueryParseException;

/**
 * A {@link FileFilter} that uses a JoSQL statement to provide the filtering.
 * The value returned by the {@link #accept(File)} method is determined by executing the
 * WHERE clause of a JoSQL statement on each File passed in.  This just uses an instance of:
 * {@link JoSQLFileFilter} to do it's job, see that class for details of usage.
 */  
public class JoSQLSwingFileFilter extends FileFilter
{

    // Like I'm gonna re-invent the wheel!
    private JoSQLFileFilter ff = null;

    private String d = null;

    /**
     * Init this file filter with the query.
     * 
     * @param q The query.
     * @throws QueryParseException If there is an issue with the parsing of the query, 
     *                             or if the FROM class is not {@link File}.
     */
    public JoSQLSwingFileFilter (String q)
	                         throws QueryParseException
    {

	this.ff = new JoSQLFileFilter (q);

    }

    /**
     * Init this file filter with the query already built and parsed.
     * 
     * @param q The query.
     * @throws IllegalStateException If the Query object has not been parsed.
     * @throws QueryParseException If the FROM class is not {@link File}.
     */
    public JoSQLSwingFileFilter (Query  q)
	                         throws IllegalStateException,
	                                QueryParseException
    {

	this.ff = new JoSQLFileFilter (q);

    }

    /**
     * Set the JoSQLFileFilter that should be used to handle the {@link #accept(File)} method.
     *
     * @param ff The file filter.
     */
    public void setJoSQLFileFilter (JoSQLFileFilter ff)
    {

	this.ff = ff;

    }

    /**
     * Set the description that should be used.
     *
     * @param d The description.
     */
    public void setDescription (String d)
    {

	this.d = d;

    }

    /**
     * Return the description for the filter.
     *
     * @return The description.
     */
    public String getDescription ()
    {

	return this.d;

    }

    /**
     * Get the file filter being used "under the hoodie" in the {@link #accept(File)} method.
     * You should also check that file filter to see if an exception has occurred.
     *
     * @return The JoSQLFileFilter that is being used to perform the match.
     */
    public JoSQLFileFilter getJoSQLFileFilter ()
    {

	return this.ff;

    }

    /**
     * Apply the WHERE clause of the statement to the {@link File} passed in.
     * This is just a wrapper call to: {@link JoSQLFileFilter#accept(File)}.
     *
     * @param f The file to evaluate the WHERE on.
     * @return <code>true</code> if the WHERE clause evaluates to <code>true</code>.
     */
    public boolean accept (File f)
    {
	
	return this.ff.accept (f);

    }

}
