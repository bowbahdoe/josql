package org.josql.functions.regexp;

import java.util.Map;
import java.util.HashMap;

import org.josql.*;

import java.lang.reflect.Method;

public class ApacheRegExpWrapper extends AbstractRegExpWrapper implements RegExp
{

    public static final String SUPPORTED_VERSION = "1.3";

    private final String compilerClassName = "org.apache.regexp.RECompiler";
    private final String matcherClassName = "org.apache.regexp.RE";
    private final String programClassName = "org.apache.regexp.REProgram";
    private final String compileMethName = "compile";
    private final String setProgramMethName = "setProgram";
    private final String matchMethName = "match";
    
    private Method compileMeth = null;
    private Method setProgramMeth = null;
    private Method matchMeth = null;
    private Class compilerClass = null;
    private Class matcherClass = null;

    private Map patterns = new HashMap ();

    public ApacheRegExpWrapper ()
    {

    }

    public String getSupportedVersion ()
    {

	return ApacheRegExpWrapper.SUPPORTED_VERSION;

    }

    public boolean isAvailable (Query q)
    {

	try
	{

            q.loadClass (this.compilerClassName);

	    return true;

	} catch (Exception e) {

	    return false;

	}

    }

    public void init (Query  q)
	              throws QueryExecutionException
    {

	try
	{

	    this.compilerClass = q.loadClass (this.compilerClassName);
	    
	    Class argTypes[] = {String.class};
	    
	    this.compileMeth = this.compilerClass.getMethod (this.compileMethName,
							     argTypes);
	    
	    this.matcherClass = q.loadClass (this.matcherClassName);
	    
	    Class pc = q.loadClass (this.programClassName);
	    
	    Class argTypes2[] = {pc};
	    
	    this.setProgramMeth = this.matcherClass.getMethod (this.setProgramMethName,
							       argTypes2);
	    
	    this.matchMeth = this.matcherClass.getMethod (this.matchMethName,
							  argTypes);
	    
	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to init",
					       e);
	    
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

		// Create a new compiler.
		Object c = this.compilerClass.newInstance ();
		
		Object args[] = {pattern};
		
		// Compile the pattern.
		Object program = this.compileMeth.invoke (c,
							  args);
		
		// Create a new RE.
		Object re = this.matcherClass.newInstance ();
		
		Object args2[] = {program};
		
		// Apply the program.
		this.setProgramMeth.invoke (re,
					    args2);
		
		this.patterns.put (pattern,
				   re);
		
		o = re;
		
	    }
	    
	    Object args[] = {val};
	    
	    // Now try and match the value.
	    return ((Boolean) this.matchMeth.invoke (o,
						     args)).booleanValue ();
	    
	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to match value: " + 
					       val +
					       " against pattern: " +
					       pattern,
						   e);
	    
	}
		
    }
    
}
