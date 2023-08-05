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
package org.josql.functions;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.List;
import java.util.ArrayList;

import org.josql.functions.regexp.RegExpFactory;
import org.josql.functions.regexp.RegExp;

import org.josql.QueryExecutionException;

/**
 * This class holds functions that operate on strings in some way.
 */
public class StringFunctions extends AbstractFunctionHandler
{

    public static final String HANDLER_ID = "_internal_string";

    private RegExpFactory regExpF = null;

    private void initRegExpFactory ()
    {
        
        this.regExpF = new RegExpFactory (this.q);
        
    }

    /**
     * Match a regular expression against the object passed in.
     *
     * @param o The object to match against, <code>toString</code> is called on the object.
     * @param re The regular expression to match.
     * @return <code>true</code> if the expression matches.
     * @throws QueryExecutionException If the match cannot be performed, or if there is no suitable
     *                                 regular expression library available to the {@link RegExpFactory}.
     */
    public boolean regexp (Object o,
			   String re)
	                   throws QueryExecutionException
    {

        if (this.regExpF == null)
        {
            
            this.initRegExpFactory ();
            
        }

	RegExp regexp = this.regExpF.getDefaultInstance (this.q);

	if (regexp == null)
	{

	    throw new QueryExecutionException ("No default regular expression library available for: " +
					       this.regExpF.getDefaultInstanceName ());

	}

	if (o == null)
	{

	    return false;

	}

	String v = o.toString ();

	return regexp.match (re,
			     v);

    }

    /**
     * Match a regular expression against the object passed in using the specified regular expression
     * library, pre-defined library names can be found in: {@link RegExpFactory}.
     *
     * @param o The object to match against, <code>toString</code> is called on the object.
     * @param re The regular expression to match.
     * @param instName The name of the regular expression library to use.
     * @return <code>true</code> if the expression matches.
     * @throws QueryExecutionException If the match cannot be performed, or if the <b>instName</b> 
     *                                 regular expression library is not available to the {@link RegExpFactory}.
     */
    public boolean regexp (Object o,
			   String re,
			   String instName)
	                   throws QueryExecutionException
    {

        if (this.regExpF == null)
        {
            
            this.initRegExpFactory ();
            
        }

	RegExp regexp = this.regExpF.getInstance (instName,
                                                  this.q);

	if (regexp == null)
	{

	    throw new QueryExecutionException ("No regular expression library available for: " +
					       instName);

	}

	if (o == null)
	{

	    return false;

	}

	String v = o.toString ();

	return regexp.match (re,
			     v);

    }

    /**
     * <a target="_blank" href="http://www.gnu.org/software/grep/grep.html">grep</a> 
     * through a file, line by line, and determine what matches there are to the nominated
     * String.  Return a List of {@link FileMatch} objects.
     *
     * @param f The File to match against.
     * @param s The string to match.
     * @param ignoreCase If set to <code>true</code> then the case of the line and string to 
     *                   match are ignored.
     * @return The List of {@link FileMatch} objects.
     */
    public List grep (File    f,
		      String  s,
		      boolean ignoreCase)
	              throws  QueryExecutionException
    {

	if ((f == null)
	    ||
	    (!f.exists ())
	    ||
	    (f.isDirectory ())
	    ||
	    (!f.canRead ())
	   )
	{

	    return null;

	}

	List retData = new ArrayList ();

	try
	{

	    BufferedReader br = new BufferedReader (new FileReader (f));
	
	    String l = br.readLine ();

	    int lc = 1;
	    
	    String ss = s;

	    if (ignoreCase)
	    {

		ss = s.toLowerCase ();

	    }

	    while (l != null)
	    {

		int ind = -1;

		if (ignoreCase)
		{

		    ind = l.toLowerCase ().indexOf (ss);

		} else {

		    ind = l.indexOf (ss);
		
		}

		if (ind != -1)
		{

		    retData.add (new FileMatch (f,
						lc,
						ind,
						s,
						l));
		    
		}
		
		l = br.readLine ();
		
		lc++;

	    }

	    br.close ();

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to read from file: " +
					       f,
					       e);

	}

	return retData;

    }

    /**
     * <a target="_blank" href="http://www.gnu.org/software/grep/grep.html">grep</a> 
     * through a file, line by line, and determine what matches there are to the nominated
     * regular expression using the specified regular expression implementation.  
     * Return a List of {@link FileMatch} objects.
     *
     * @param f The File to match against.
     * @param regexp The regular expression to match against each line.  This will use the
     *               default regular expression library.  In this case the location of the match
     *               (i.e. {@link FileMatch#getColumn()}) will be -1 since the regular expression
     *               handling does not support location matching.  Also, {@link FileMatch#getString()}
     *               will contain the regular expression used.
     * @param instName The instance name to use.
     * @return The List of {@link FileMatch} objects.
     * @throws QueryExecutionException If the default regular expression implementation is not 
     *                                 available or if the file cannot be read.
     */
    public List rgrep (File    f,
		       String  regexp,
		       String  instName)
	               throws  QueryExecutionException
    {

        if (this.regExpF == null)
        {
            
            this.initRegExpFactory ();
            
        }

	RegExp reImpl = this.regExpF.getInstance (instName,
                                                  this.q);

	if (reImpl == null)
	{

	    throw new QueryExecutionException ("No default regular expression library available for: " +
					       instName);

	}

	return this.rgrep (f,
			   regexp,
			   reImpl);

    }

    /**
     * <a target="_blank" href="http://www.gnu.org/software/grep/grep.html">grep</a> 
     * through a file, line by line, and determine what matches there are to the nominated
     * regular expression.  Return a List of {@link FileMatch} objects.
     *
     * @param f The File to match against.
     * @param regexp The regular expression to match against each line.  This will use the
     *               default regular expression library.  In this case the location of the match
     *               (i.e. {@link FileMatch#getColumn()}) will be -1 since the regular expression
     *               handling does not support location matching.  Also, {@link FileMatch#getString()}
     *               will contain the regular expression used.
     * @return The List of {@link FileMatch} objects.
     * @throws QueryExecutionException If the default regular expression implementation is not 
     *                                 available or if the file cannot be read.
     */
    public List rgrep (File    f,
		       String  regexp)
	               throws  QueryExecutionException
    {

        if (this.regExpF == null)
        {
            
            this.initRegExpFactory ();
            
        }

	RegExp reImpl = this.regExpF.getDefaultInstance (this.q);

	if (reImpl == null)
	{

	    throw new QueryExecutionException ("No default regular expression library available for: " +
					       this.regExpF.getDefaultInstanceName ());

	}

	return this.rgrep (f,
			   regexp,
			   reImpl);

    }

    private List rgrep (File    f,
			String  regexp,
			RegExp  reImpl)
	                throws  QueryExecutionException
    {

	if ((f == null)
	    ||
	    (!f.exists ())
	    ||
	    (f.isDirectory ())
	    ||
	    (!f.canRead ())
	   )
	{

	    return null;

	}

	List retData = new ArrayList ();

	try
	{

	    BufferedReader br = new BufferedReader (new FileReader (f));
	
	    String l = br.readLine ();

	    int lc = 1;
	    
	    while (l != null)
	    {

		if (reImpl.match (regexp,
				  l))
		{

		    retData.add (new FileMatch (f,
						lc,
						-1,
						regexp,
						l));
		    
		}
		
		l = br.readLine ();
		
		lc++;

	    }

	    br.close ();

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to read from file: " +
					       f,
					       e);

	}

	return retData;

    }

    /**
     * <a target="_blank" href="http://www.gnu.org/software/grep/grep.html">grep</a>
     * through a file, line by line, and determine what matches there are to the nominated
     * String.  Return a List of {@link FileMatch} objects.
     *
     * @param f The File to match against.
     * @param s The string to match.
     * @return The List of {@link FileMatch} objects.
     */
    public List grep (File   f,
		      String s)
	              throws QueryExecutionException
    {

	return this.grep (f,
			  s,
			  false);

    }

    /**
     * Given a string trim the passed in string from the front and end, set <b>v</b> to <code>null</code>
     * to have just whitespace trimmed.  Both are converted to strings first.
     *
     * @param o The string to trim.
     * @param v The string to trim from the front and end.  Set to <code>null</code> to just trim
     *          whitespace.
     * @return The trimmed string.
     */
    public String trim (Object o,
			Object v)
    {

	if (o == null)
	{

	    return null;

	}

	String os = o.toString ();

	if (v == null)
	{

	    return os.trim ();

	}

	String vs = v.toString ();

	if (os.endsWith (vs))
	{

	    os = os.substring (0,
			       vs.length ());

	}

	if (os.startsWith (vs))
	{

	    os = os.substring (vs.length ());

	}

	return os;

    }

    /**
     * A thinly veiled wrapper around the {@link String#lastIndexOf(String)} method.
     * Both <b>o</b> and <b>i</b> are converted to Strings and then the "lastIndexoOf" method
     * is called on <b>o</b> with <b>i</b> as the argument.  
     *
     * @param o The string to search.
     * @param i The string to match.
     * @return The last index of <b>i</b> within <b>o</b>.  If <b>o</b> is <code>null</code> then
     *         -1 is returned.  If <b>i</b> is <code>null</code> then -1 is returned.
     */
    public double lastIndexOf (Object o,
			       Object i)
    {

	if (o == null)
	{

	    return -1;

	}

	if (i == null)
	{

	    return -1;

	}

	String os = o.toString ();
	String is = i.toString ();

	return os.lastIndexOf (is);

    }  

    /**
     * Return a substring of the passed in object (in a string form).  See {@link #subStr(Object,double,double)}
     * for the full details since this is just a thin-wrapper around that method with the <b>t</b>
     * parameter set to -1.
     *
     * @param o The object to convert to a string and return the substring.
     * @param f The start index.  If this is set to 0 then the entire string is returned.  
     * @return The substring.
     */
    public String subStr (Object o,
			  double f)
    {

	return this.subStr (o,
			    f,
			    -1);

    }

    /**
     * A function to return a substring of a String.  If the passed in object isn't 
     * a string then it is converted to a string before processing.
     *
     * @param o The object to convert to a string and return the substring.
     * @param f The start index.  If it's < 0 then "" is returned.  If the start is out of 
     *          range of the string then "" is returned.
     * @param t The end index.  If it's > f then it is reset to -1.  If it's -1 then
     *          it's ignored and the substring from the start is used.  If the end is greater
     *          than the length of the string then it is ignored.
     * @return The substring.
     */
    public String subStr (Object o,
			  double f,
			  double t)
    {

	if (o == null)
	{

	    return null;

	}

	int fi = (int) f;
	int ti = (int) t;

	String s = o.toString ();

	if ((fi < 0)
	    ||
	    (fi > s.length ())
	   )
	{

	    return "";

	}

	if ((ti < fi)
	    ||
	    (ti > s.length ())
	   )
	{

	    ti = -1;

	}

	if (ti == -1)
	{

	    return s.substring (fi);

	}

	return s.substring (fi,
			    ti);

    }

    public double length (Object o)
    {

	if (o == null)
	{

	    return 0;

	}

	if (o instanceof String)
	{

	    return ((String) o).length ();

	}

	return o.toString ().length ();

    }

}
