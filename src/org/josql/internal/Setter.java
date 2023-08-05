package org.josql.internal;

import java.util.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.StringTokenizer;

import com.gentlyweb.utils.Getter;

/**
 * This class is used to perform access into a Java object using a 
 * String value with a specific notation.
 * <p>
 * The Accessor uses a dot notation such as <b>field1.method1.method2</b> to
 * perform the access on an object.  Each value in the notation refers to
 * a field or method (a no argument method) of the type of the previous
 * value.
 * For instance if you have the following class structure:
 * </p>
 * <pre>
 * public class A 
 * {
 *    public B = new B ();
 * }
 * 
 * public class B
 * {
 *    public C = new C ();
 * }
 * 
 * public class C
 * {
 *    String d = "";
 * }
 * </pre>
 * <p>
 * You would then use the notation: <b>B.C.d</b> to get access to 
 * field <b>d</b> in Class C.
 * <br /><br />
 * The Accessor also supports a <b>[ ]</b> notation for accessing
 * into Lists/Maps and Arrays.  If the value between the <b>[ ]</b>
 * is an integer then we look for the associated type to be either
 * an array or a List, we then index into it with the integer.  If
 * the value is <b>NOT</b> an integer then we use assume the
 * type is a Map and use it as a key into the Map.
 * <br /><br />
 * For instance changing the example above:
 * </p>
 * <pre>
 * public class A 
 * {
 *    public List vals = new ArrayList ();
 * }
 * </pre>
 * <p>	Method[] methods = c.getMethods ();

	for (int i = 0; i < methods.length; i++)
	{

	    if ((methods[i].getName ().equals (getMethod.toString ()))
		&&
		(methods[i].getParameterTypes ().length == 0)
	       )
	    {

		// This is the one...
		return methods[i];

	    }

	}

	return null;

 * Now we could use: <b>vals[X]</b> where <b>X</b> is a positive integer.
 * Or changing again:
 * </p>
 * <pre>
 * public class A 
 * {
 *    public Map vals = new HashMap ();
 * }
 * </pre>
 * <p>
 * We could use: <b>vals[VALUE]</b> where <b>VALUE</b> would then be
 * used as a Key into the vals HashMap.
 * <br /><br />
 * Note: The Accessor is <b>NOT</b> designed to be an all purpose
 * method of gaining access to a class.  It has specific uses and for
 * most will be of no use at all.  It should be used for general purpose
 * applications where you want to access specific fields of an object
 * without having to know the exact type.  One such application is in
 * the <code>GeneralComparator</code>, in that case arbitrary Objects can 
 * be sorted without having to write complex Comparators or implementing
 * the Comparable interface AND it gives the flexibility that sorting
 * can be changed ad-hoc.
 * <br /><br />
 * The Accessor looks for in the following order:
 * <ul>
 *   <li>Public fields with the specified name.</li>
 *   <li>If no field is found then the name is converted to a "JavaBeans" 
 *       <b>get</b> method, so a field name of <b>value</b> would be converted
 *       to <b>getValue</b> and that method is looked for.  The method must take
 *       no arguments.</li>
 *   <li>If we don't find the <b>get*</b> method then we look for a method with 
 *       the specified name.  So a field name of <b>value</b> would mean that
 *       a method (that again takes no arguments) is looked for.</li>
 * </ul>
 * <p>
 * Note: we have had to add the 3rd type to allow for methods that don't follow
 * JavaBeans conventions (there are loads in the standard Java APIs which makes 
 * accessing impossible otherwise).
 */
public class Setter
{

    private Getter getter = null;
    private Object setter = null;
    private Class clazz = null;

    /**
     * @param ref The reference for the setter.
     * @param clazz The Class to get the field from.
     */
    public Setter (String  ref,
		   Class   clazz,
		   Class[] parmTypes)
	           throws  IllegalArgumentException,
	                   NoSuchMethodException
    {

	this.clazz = clazz;

	StringTokenizer t = new StringTokenizer (ref,
						 ".");

	StringBuffer getRef = new StringBuffer ();

	if (t.countTokens () > 1)
	{

	    // Get everything up to the last part.
	    while (t.hasMoreTokens ())
	    {
		
		getRef.append (t.nextToken ());

		if (t.countTokens () > 1)
		{

		    getRef.append ('.');

		}

		if (t.countTokens () == 1)
		{

		    // Now get the Getter.
		    this.getter = new Getter (getRef.toString (),
					      clazz);

		    // Get the return type from the getter.
		    clazz = this.getter.getType ();

		    break;

		}

	    }

	}

	// Now for the final part this is the setter.
	String set = t.nextToken ();

	// Get the Fields.
	Field[] fields = clazz.getFields ();
	
	Field f = null;
	
	// See if the token matches...
	for (int i = 0; i < fields.length; i++)
	{
	    
	    if (fields[i].getName ().equals (set))
	    {
		
		// Found it...
		f = fields[i];
		
		this.setter = f;
		
		return;
		
	    }
	    
	}
	
	// If we are here then it's not a public field.
	
	// Now convert it to a method name and use the
	// JavaBeans convention...
	
	// Now get the method...
	StringBuffer name = new StringBuffer (set);

	name.setCharAt (0,
			Character.toUpperCase (name.charAt (0)));

	name.insert (0,
		     "set");

	String nName = name.toString ();

	List meths = new ArrayList ();

	Utilities.getMethods (clazz,
			      nName,
			      Modifier.PUBLIC,
			      meths);

	TreeMap sm = new TreeMap ();

	// Now compare the parm types.
	for (int i = 0; i < meths.size (); i++)
	{

	    Method m = (Method) meths.get (i);

	    Class[] mpts = m.getParameterTypes ();

	    int score = Utilities.matchMethodArgs (mpts,
						   parmTypes);

	    if (score > 0)
	    {

		sm.put (Integer.valueOf (score),
			m);

	    }

	}

	// Get the last key
	if (sm.size () > 0)
	{

	    this.setter = (Method) sm.get (sm.lastKey ());

	}

	if (this.setter == null)
	{

	    meths = new ArrayList ();

	    Utilities.getMethods (clazz,
				  set,
				  Modifier.PUBLIC,
				  meths);

	    sm = new TreeMap ();

	    // Now compare the parm types.
	    for (int i = 0; i < meths.size (); i++)
	    {

		Method m = (Method) meths.get (i);

		Class[] mpts = m.getParameterTypes ();
		
		int score = Utilities.matchMethodArgs (mpts,
						       parmTypes);

		if (score > 0)
		{

		    sm.put (Integer.valueOf (score),
			    m);

		}

	    }

	    // Get the last key
	    if (sm.size () > 0)
	    {

		this.setter = (Method) sm.get (sm.lastKey ());

	    }

	}

	if (this.setter == null)
	{

	    throw new IllegalArgumentException ("Unable to find required method: " +
						nName + 
						" or: " + 
						set +
						" in class: " +
						clazz.getName () +
						" taking parms: " +
						Arrays.toString (parmTypes));

	}
	
    }

    public Class getBaseClass ()
    {

	return this.clazz;

    }

    public void setValue (Object target,
			  Object value)
                          throws IllegalAccessException,
	                         InvocationTargetException,
                                 IllegalArgumentException
    {

	Object[] vals = {value};
	
	this.setValue (target,
		       vals);

    }

    public void setValue (Object   target,
			  Object[] values)
                          throws   IllegalAccessException,
	                           InvocationTargetException,
                                   IllegalArgumentException
    {

	// Get the object to set on from the getter.
	if (this.getter != null)
	{

	    target = this.getter.getValue (target);

	}

	// Now call our accessor on the obj and set the value.
	if (this.setter instanceof Field)
	{

	    Field f = (Field) this.setter;

	    f.set (target,
		   values[0]);

	    return;

	}
	
	if (this.setter instanceof Method)
	{

	    Method m = (Method) this.setter;

	    m.invoke (target,
		      Utilities.convertArgs (values,
					     m.getParameterTypes ()));

	}

    }

    /**
     * Get the class of the type of object we expect in the {@link #setValue(Object,Object)}
     * method.
     *
     * @return The class.
     */
    public Class getType ()
    {

	// See what type the accessor is...
	if (this.setter instanceof Method)
	{
		
	    Method m = (Method) this.setter;
		
	    Class[] parms = m.getParameterTypes ();

	    return parms[0];

	}
	    
	if (this.setter instanceof Field)
	{
		
	    // It's a field...so...
	    Field f = (Field) this.setter;

	    return f.getType ();

	}

	return null;

    }

}
