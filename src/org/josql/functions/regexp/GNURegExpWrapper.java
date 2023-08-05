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
import java.lang.reflect.Constructor;

/**
 * The wrapper implementation for the GNU implementation of regular expression matching.
 * See: <a href="http://www.cacas.org/java/gnu/regexp/">http://www.cacas.org/java/gnu/regexp/</a> for details.
 */
public class GNURegExpWrapper extends AbstractRegExpWrapper implements RegExp
{

    public static final String SUPPORTED_VERSION = "1.1.4";

    private final String reClassName = "gnu.regexp.RE";
    private final String isMatchMethName = "isMatch";

    private Map patterns = new HashMap ();
    
    private Constructor cons = null;
    private Method isMatchMeth = null;

    public GNURegExpWrapper ()
    {

    }

    public String getSupportedVersion ()
    {

	return GNURegExpWrapper.SUPPORTED_VERSION;

    }

    public boolean isAvailable (Query q)
    {

	try
	{

	    q.loadClass (this.reClassName);

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

	    Object o = this.patterns.get (pattern);
	    
	    if (o == null)
	    {

		Object args[] = {pattern};
		
		o = this.cons.newInstance (args);
		
		this.patterns.put (pattern,
				   o);
		
	    }
	    
	    Object args[] = {val};
	    
	    return ((Boolean) this.isMatchMeth.invoke (o,
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

	    // Bit easier this one!
	    Class reClass = q.loadClass (reClassName);
	    
	    Class argTypes[] = {Object.class};
	    
	    this.cons = reClass.getConstructor (argTypes);
	    
	    this.isMatchMeth = reClass.getMethod (this.isMatchMethName,
						  argTypes);
	    
	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to init",
					       e);
	    
	}
	
    }
    
}
