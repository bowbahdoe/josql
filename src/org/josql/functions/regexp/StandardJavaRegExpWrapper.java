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
 * The wrapper implementation for the Java 1.4 regular expression matching (java.util.regex).
 * See: {@link java.util.regex}.
 */
public class StandardJavaRegExpWrapper extends AbstractRegExpWrapper implements RegExp
{
    
    public static final String SUPPORTED_VERSION = "1.4";

    private final String compileMethName = "compile";
    private final String matcherMethName = "matcher";
    private final String matchesMethName = "matches";
    private final String matcherClassName = "java.util.regex.Matcher";
    private final String patternClassName = "java.util.regex.Pattern";
    
    private Method compileMeth = null;
    private Method matcherMeth = null;
    private Method matchesMeth = null;
    
    private Map patterns = new HashMap ();
    
    public StandardJavaRegExpWrapper ()
    {
	
    }

    public String getSupportedVersion ()
    {

	return StandardJavaRegExpWrapper.SUPPORTED_VERSION;

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

	    // See if we already have a Pattern for this pattern.
	    Object o = this.patterns.get (pattern);
	    
	    if (o == null)
	    {

		Object args[] = {pattern};
		
		// Create a new one.  Static method.
		o = this.compileMeth.invoke (null,
					     args);
		
		this.patterns.put (pattern,
				   o);
		
	    }
	    
	    Object args[] = {val};
	    
	    // Now create the matcher.
	    Object matcher = this.matcherMeth.invoke (o,
						      args);
	    
	    // Now invoke the "matches" method.
	    return ((Boolean) this.matchesMeth.invoke (matcher,
						       null)).booleanValue ();
	    
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

	    Class pc = q.loadClass (this.patternClassName);
	    
	    Class argTypes[] = {String.class};
	    
	    // Get the "compile" method.
	    this.compileMeth = pc.getMethod (this.compileMethName,
					     argTypes);
	    
	    Class argTypes2[] = {CharSequence.class};
	    
	    // Get the "matcher" method.
	    this.matcherMeth = pc.getMethod (this.matcherMethName,
					     argTypes2);
	    
	    Class mc = q.loadClass (this.matcherClassName);
	    
	    // Get the matches method.
	    this.matchesMeth = mc.getMethod (this.matchesMethName,
					     null);
	    
	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to init",
					       e);

	}
	
    }

}
