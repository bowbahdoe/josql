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

import java.io.File;

import org.apache.tools.ant.types.selectors.ExtendFileSelector;

import org.apache.tools.ant.types.Parameter;

import org.apache.tools.ant.BuildException;

import org.josql.Query;

/**
 * A custom file selector for use with Ant.
 * <p>
 * See: <a target="_blank" href="http://ant.apache.org/manual/CoreTypes/selectors.html#customselect">Custom Ant Selectors</a> for more details.
 * <p>
 * Allows a JoSQL <a href="http://josql.sourceforge.net/manual/3-5.html" target="_blank">WHERE clause</a>
 * to be applied to each file passed to the selector {@link #isSelected(File,String,File)}.
 * <p>
 * An obvious question to ask here is "why do I need this when Ant has lots of custom file selectors".
 * Well, in short, you don't "need" this selector, but I've found that trying to remember all the
 * custom elements and their attributes can be painful and doesn't give me all the power needed to
 * select the files I want.  This custom selector however does.
 * <p>
 * The selector supports the following "param"s.
 * <ul>
 *   <li><b>where</b> - multiple <b>where</b> params are supported, the value of each param is concatenated
 *       with the others to form the full WHERE clause.  It is up to you to decide how you split up
 *       your where clause.</li>
 *   <li><b>debug</b> - when set to <b>on</b> (case-insensitive) debug information about the WHERE clause
 *       actually used and for each file passed to {@link #isSelected(File,String,File)} what the
 *       WHERE clause evaluated to, either <code>true</code> or <code>false</code>.</li>
 * </ul>
 * <p>
 * Usage:
 * <p>
 * A typical usage may be:
 * <p>
 * <pre>
 *   &lt;fileset dir="myDir" 
 *            includes="**">
 *     &lt;custom classpath="[PATH_TO_JOSQL]/JoSQL-1.0.jar:[PATH_TO_JOSQL]/3rd-party-jars/gentlyWEB-utils-1.1.jar"
 *             classname="org.josql.contrib.JoSQLAntFileSelector">
 *       &lt;param name="debug" value="on" />
 *       &lt;param name="where" value="toDate (lastModified) > toDate ('22/Sep/2005')" />
 *       &lt;param name="where" value="AND length > 10000" />
 *     &lt;/custom>
 *   &lt;/fileset>
 * </pre>
 * <p>
 * This will create a file set containing all the files modified after 22/Sep/2005 and have a length greater
 * than 10000 bytes.
 * <p>
 * Compare this to how it would be "normally" be done with standard Ant fileset selectors:
 * <pre>
 *   &lt;fileset dir="myDir" 
 *            includes="**">
 *     &lt;date datetime="22/Sep/2005"
 *           pattern="dd/MMM/yyyy"
 *           when="after" />
 *     &lt;size value="10000"
 *           when="more" />
 *   &lt;/fileset>
 * </pre>
 * <p>
 * Of course it is perfectly possible to mix and match this selector and other custom selectors or
 * the built-in selectors.
 */
public class JoSQLAntFileSelector extends Query implements ExtendFileSelector
{

    private static String sqlPrefix = "SELECT * FROM java.io.File WHERE ";
    
    public static final String WHERE = "where";
    public static final String DEBUG = "debug";

    private Exception parseExp = null;
    private boolean configured = false;
    private String where = null;
    private boolean debug = false;
    private boolean shownWhere = false;
    
    public JoSQLAntFileSelector ()
    {

    }

    public void setParameters (Parameter[] parms)
    {

	if (parms != null)
	{

	    StringBuffer buf = new StringBuffer ();

	    for (int i = 0; i < parms.length; i++)
	    {

		Parameter p = parms[i];

		if (p.getName ().toLowerCase ().equals (JoSQLAntFileSelector.DEBUG))
		{

		    if (p.getValue ().toLowerCase ().equals ("on"))
		    {

			this.debug = true;

		    }

		}

		if (p.getName ().toLowerCase ().equals (JoSQLAntFileSelector.WHERE))
		{

		    if (buf.length () > 0)
		    {

			buf.append (" ");

		    }

		    buf.append (p.getValue ().trim ());

		}

	    }

	    if (buf.length () > 0)
	    {

		try
		{

		    this.where = buf.toString ();

		    this.parse (JoSQLAntFileSelector.sqlPrefix + buf.toString ());

		    this.configured = true;

		} catch (Exception e) {

		    this.parseExp = e;

		}

	    }

	}

    }

    public boolean isSelected (File   basedir, 
			       String filename,
			       File   file)
                               throws BuildException
    {

	if (!this.configured)
	{

	    if (this.parseExp != null)
	    {

		throw new BuildException ("Unable to init query with where clause: " + 
					  this.where +
					  ", reason: " + this.parseExp.getMessage (),
					  this.parseExp);

	    }

	    throw new BuildException ("Selector is not configured, expected to find a parameter with name: " +
				      JoSQLAntFileSelector.WHERE +
				      " that provides the where clause to evaluate against each file.");

	}

	if (this.debug)
	{

	    if (!this.shownWhere)
	    {

		System.out.println ("Using WHERE clause: " + this.where);

		this.shownWhere = true;

	    }

	}

	try
	{

	    boolean v = this.getWhereClause ().isTrue (file,
						       this);

	    if (this.debug)
	    {

		System.out.println ("WHERE = " + 
				    v +
				    " for file: " +
				    file);

	    }

	    return v;

	} catch (Exception e) {

	    //e.printStackTrace ();

	    throw new BuildException ("Unable to execute where clause: " +
				      this.getWhereClause () +
				      " on file: " +
				      file +
				      ", reason: " + e.getMessage (),
				      e);

	}

    }

}
