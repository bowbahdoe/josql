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
package org.josql.functions.regexp;

import java.util.Map;
import java.util.HashMap;

import org.josql.*;

import java.lang.reflect.Method;

/**
 * The wrapper implementation for the Apache ORO implementation of regular expression matching.
 * See: <a href="http://jakarta.apache.org/oro/">http://jakarta.apache.org/oro/</a> for details.
 */
public class OroApacheRegExpWrapper extends AbstractRegExpWrapper implements RegExp
{

    public static final String SUPPORTED_VERSION = "2.0.8";

    private final String compilerClassName = "org.apache.oro.text.regex.Perl5Compiler";
    private final String matcherClassName = "org.apache.oro.text.regex.Perl5Matcher";
    private final String patternClassName = "org.apache.oro.text.regex.Pattern";

    private final String matchesMethName = "matches";
    private final String compileMethName = "compile";

    private Method compileMeth = null;
    private Method matchesMeth = null;
    
    private Object compiler = null;
    private Object matcher = null;
    
    private Map patterns = new HashMap ();

    public OroApacheRegExpWrapper ()
    {

    }

    public String getSupportedVersion ()
    {

	return OroApacheRegExpWrapper.SUPPORTED_VERSION;

    }

    public boolean isAvailable (Query q)
    {

	try
	{

	    q.loadClass (this.patternClassName);

	    return true;

	} catch (Exception e) {

	    return false;

	}

    }

    public boolean match (String pattern,
			  String val)
                          throws QueryExecutionException
    {

	try
	{

	    // See if we already have the pattern.
	    Object o = this.patterns.get (pattern);
	    
	    if (o == null)
	    {

		Object args[] = {pattern};
		
		// Create a new Pattern.
		o = this.compileMeth.invoke (this.compiler,
					     args);
		
		this.patterns.put (pattern,
				   o);
		
	    }
	    
	    Object args[] = {val, o};
	    
	    // Match them!
	    return ((Boolean) this.matchesMeth.invoke (this.matcher,
						       args)).booleanValue ();

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to match value: " + 
					       val + 
					       " against pattern: " + 
					       pattern,
					       e);
	    
	}
	
    }
    
    public void init (Query  q)
                      throws QueryExecutionException
    {

	try
	{

	    Class compClass = q.loadClass (this.compilerClassName);
	    
	    // Create a new Compiler instance.
	    this.compiler = compClass.newInstance ();
	    
	    // Get the compile(String) method.
	    Class argTypes[] = {String.class};
		
	    this.compileMeth = compClass.getMethod (this.compileMethName,
						    argTypes);
	    
	    Class matchClass = q.loadClass (this.matcherClassName);
	    
	    // Create a new matcher.
	    this.matcher = matchClass.newInstance ();
	    
	    Class patternClass = q.loadClass (this.patternClassName);
	    
	    Class argTypes2[] = {String.class, patternClass};
	    
	    // Get the matches(String,Pattern) method...
	    this.matchesMeth = matchClass.getMethod (this.matchesMethName,
						     argTypes2);
	    
	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to init",
					       e);
	    
	}
	
    }
    
}
