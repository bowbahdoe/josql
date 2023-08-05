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

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import javax.servlet.jsp.JspException;

import org.josql.Query;
import org.josql.QueryResults;

/**
 * Allows a JoSQL Query to be used in a JSP.
 * <p>
 * Example:
 * <pre>
 *   &lt;%@ page import="java.util.Arrays" %>
 *   &lt;%@ page import="java.io.File" %>
 *
 *   &lt;%@ tablib prefix="josql" uri="josqltaglib" %>
 *
 *   &lt;josql:query inputList='&lt;%= Arrays.asList (new File ("/home/me/").listFiles ()) %>'
 *                results="queryResults">
 *     SELECT *
 *     FROM   java.io.File
 *     WHERE  name = '.bashrc'
 *   &lt;/josql:query>
 * </pre>
 * <p>
 * The body of the tag is taken as the statement to execute.
 * <p>
 * Note: this class deliberately does NOT extend {@link Query} since doing so would then
 * prevent the query from being released via the {@link #release()} method.
 * <p>
 * The following <b>attributes</b> are supported:
 * <ul>
 *   <li><b>inputList</b> - specifies the name of an attribute that holds the List of objects
 *       to execute the JoSQL statement against, or is the List of objects.  The attribute
 *       allows request-time expressions to be used, i.e. the "rtexprvalue" in the .tld file
 *       is set to <code>true</code>.</li>
 *   <li><b>results</b> - specifies the name of the attribute to hold the results (instance of:
 *       {@link QueryResults}).</li>
 * </ul>
 */
public class JoSQLJSPQueryTag extends BodyTagSupport
{

    private Object inputList = null;
    private String results = null;
    private Query query = null;

    /**
     * Set the name of the attribute that should hold the results.
     *
     * @param r The name.
     */
    public void setResults (String r)
    {

	this.results = r;

    }

    /**
     * Set the input list, i.e. the list of objects to execute the JoSQL
     * statement against.  Can be either a "java.lang.String" which will
     * indicate the name of an attribute to use, or a "java.util.List"
     * which will be the list of objects of use.
     * The list of objects is not gained until the {@link #doEndTag()} method
     * is called.
     *
     * @param l The name or list of objects to execute the statement against.
     */
    public void setInputList (Object l)
    {

	this.inputList = l;

    }

    /**
     * When called will parse the tag body into a JoSQL statement.
     * 
     * @return {@link Tag#SKIP_BODY} is returned.
     * @throws JspException If the tag body cannot be parsed into a JoSQL statement.
     */
    public int doAfterBody ()
	                    throws JspException
    {

	this.query = new Query ();

	String st = this.getBodyContent ().getString ();

	try
	{

	    this.query.parse (st);

	} catch (Exception e) {

	    throw new JspException ("Unable to parse statement: " +
				    st,
				    e);

	}

	return Tag.SKIP_BODY;

    }

    /**
     * Execute the statement parsed in {@link #doAfterBody()}.
     * The list of objects to execute the statement against is first retrieved
     * using the value specified in {@link #setInputList(Object)}.  The list must
     * not be null and must be a list otherwise an exception is thrown.
     * <p>
     * Once executed the results are set as an attribute specified by: 
     * {@link #setResults(String)}.
     * 
     * @return {@link Tag#EVAL_PAGE} always.
     * @throws JspException If the list is null, not a list or the statement cannot
     *                      be executed against the list of objects.
     */
    public int doEndTag ()
	                 throws JspException
    {

	List l = null;

	if (this.inputList instanceof String)
	{

	    String ln = (String) this.inputList;

	    // Get the list.
	    Object o = this.pageContext.findAttribute (ln);

	    if (o == null)
	    {

		throw new JspException ("No list with name: " +
					ln +
					" can be found");

	    }

	    if (!(o instanceof List))
	    {

		throw new JspException ("Attribute: " +
					ln +
					" is not an instance of: " +
					List.class.getName () + 
					", is: " + 
					o.getClass ().getName ());

	    }

	    l = (List) o;

	}

	if (this.inputList instanceof List)
	{

	    l = (List) this.inputList;

	}

	if (l == null)
	{

	    throw new JspException ("No input list specified.");

	}

	try
	{

	    QueryResults qr = this.query.execute (l);

	    // Set the query results as an attribute.
	    this.pageContext.setAttribute (this.results,
					   qr);

	} catch (Exception e) {

	    throw new JspException ("Unable to execute query with list: " +
				    this.inputList,
				    e);

	}

	return Tag.EVAL_PAGE;

    }

    /**
     * Release the objects we have references to and then call: super.release ().
     */
    public void release ()
    {

	this.inputList = null;
	this.results = null;
	this.query = null;
	
	super.release ();

    }

}
