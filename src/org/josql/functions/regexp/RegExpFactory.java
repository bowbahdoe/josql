package org.josql.functions.regexp;

import java.util.Map;
import java.util.HashMap;

import org.josql.*;

public class RegExpFactory
{

    /**
     * The instance name to use for the Java 1.4 (java.util.regex) regular expression library.
     * Whilst this is the default it will not be available on version of Java < 1.4.
     */
    public static final String JAVA_INST = "java";

    /**
     * The instance name to use for the ORO Apache regular expression library.
     */
    public static final String ORO_INST = "oro";

    /**
     * The instance name to use for the GNU regular expression library.
     */
    public static final String GNU_INST = "gnu";

    /**
     * The instance name to use for the Apache RegExp regular expression library.
     */
    public static final String APACHE_REGEXP_INST = "apache.regexp";

    private String defInst = RegExpFactory.JAVA_INST;

    private Map mappings = new HashMap ();
    private Map versions = new HashMap ();

    public RegExpFactory (Query q)
    {

	StandardJavaRegExpWrapper j = new StandardJavaRegExpWrapper ();

	if (j.isAvailable (q))
	{

	    this.mappings.put (RegExpFactory.JAVA_INST,
			       StandardJavaRegExpWrapper.class);
	    this.versions.put (RegExpFactory.JAVA_INST,
			       j.getSupportedVersion ());

	}

	OroApacheRegExpWrapper o = new OroApacheRegExpWrapper ();

	if (o.isAvailable (q))
	{

	    this.mappings.put (RegExpFactory.ORO_INST,
			       OroApacheRegExpWrapper.class);
	    this.versions.put (RegExpFactory.ORO_INST,
			       o.getSupportedVersion ());

	}

	GNURegExpWrapper g = new GNURegExpWrapper ();

	if (g.isAvailable (q))
	{

	    this.mappings.put (RegExpFactory.GNU_INST,
			       GNURegExpWrapper.class);
 	    this.versions.put (RegExpFactory.GNU_INST,
			       g.getSupportedVersion ());

	}

	ApacheRegExpWrapper a = new ApacheRegExpWrapper ();

	if (a.isAvailable (q))
	{

	    this.mappings.put (RegExpFactory.APACHE_REGEXP_INST,
			       ApacheRegExpWrapper.class);
 	    this.versions.put (RegExpFactory.APACHE_REGEXP_INST,
                               a.getSupportedVersion ());

	}

    }

    public String getSupportedVersion (String instName)
    {

	return (String) this.versions.get (instName);

    }

    public String getDefaultInstanceName ()
    {

	return this.defInst;

    }

    public void addInstance (String  name,
			     RegExp  re,
			     boolean def)
    {

	this.mappings.put (name,
			   re);

	if (def)
	{
	    
	    this.defInst = name;

	}

    }

    public void setDefaultInstanceName (String n)
    {

	if (!this.mappings.containsKey (n))
	{

	    throw new IllegalArgumentException ("No appropriate wrapper class found for instance name: " +
						n);

	}

	this.defInst = n;

    }

    public RegExp getDefaultInstance (Query  q)
	                              throws QueryExecutionException
    {

	return this.getInstance (this.defInst,
                                 q);

    }

    public RegExp getInstance (String type,
                               Query  q)
	                       throws QueryExecutionException
    {

	Object o = this.mappings.get (type);

	if (o == null)
	{

	    return null;

	}

	if (o instanceof RegExp)
	{

	    // Already inited...
	    return (RegExp) o;

	}

	Class c = (Class) o;

	try
	{

	    RegExp re = (RegExp) c.newInstance ();

	    re.init (q);

	    this.mappings.put (type,
			       re);

	    return re;

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to init RegExp instance: " + 
					       c.getName (),
					       e);

	}

    }

}
