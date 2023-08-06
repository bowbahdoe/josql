/*
 * Copyright 2006 - Gary Bentley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.mccue.josql.gentlyweb.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

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
 * <p>
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
 * the {@link GeneralComparator}, in that case arbitrary Objects can 
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
public class Accessor
{

    private Object accessor = null;
    private String index = "";

    public String getIndex ()
    {

	return this.index;

    }

    public void setIndex (String index)
    {

	this.index = index;

    }

    public void setAccessor (Field f)
    {

	this.accessor = f;

    }

    public void setAccessor (Method m)
    {

	this.accessor = m;

    }

    public boolean isIndexCorrectForType (Class c)
    {
	
	// See if the index is a number...in which case
	// the class should be either an Array or a List.
	if (this.index.equals (""))
	{

	    return true;

	}

	try
	{
	    
	    Integer.parseInt (this.index);
	    
	    if ((c.isArray ())
		||
		(c.isAssignableFrom (List.class))
	       )
	    {
		
		return true;
		
	    }
	    
	} catch (NumberFormatException e) {
	    
	    if (c.isAssignableFrom (Map.class))
	    {
		
		return true;
		
	    }
	    
	}
	
	return false;

    }

    public static Object getValueFromAccessorChain (Object obj,
						    List   chain)
	                                            throws IllegalAccessException,
                                                           InvocationTargetException
    {
	
	// For our accessor chain, use the Field and Methods
	// to get the actual value.
	Object retdata = obj;
	
	for (int i = 0; i < chain.size (); i++)
	{

	    Accessor a = (Accessor) chain.get (i);
	    
	    // See what type the accessor is...
	    if (a.accessor instanceof Method)
	    {
		
		Method m = (Method) a.accessor;
		
		Object[] parms = {};
		
		// Invoke the method...
		retdata = m.invoke (retdata,
				    parms);
		
		if (retdata == null)
		{
		    
		    return null;
		    
		}
				
	    }
	    
	    if (a.accessor instanceof Field)
	    {
		
		// It's a field...so...
		Field f = (Field) a.accessor;
		
		// Now get the value...
		retdata = f.get (retdata);
		
	    }

	    // See if we have an index...
	    if (!a.getIndex ().equals (""))
	    {
		
		retdata = Accessor.getValueForIndex (retdata,
						     a.getIndex ());
		
		if (retdata == null)
		{
		    
		    return null;
		    
		}
		
	    }
		
	}
	
	return retdata;
	
    }

    public static Object getValueForIndex (Object data,
					   String index)
    {

	try
	{
	    
	    int in = Integer.parseInt (index);
	    
	    // See if we have an Array or 
	    // List.
	    if (data.getClass ().isArray ())
	    {
		
		if (in < Array.getLength (data))
		{
		    
		    // It's an array.
		    return Array.get (data,
				      in);
		    
		} 
		
	    } else {
		
		if (data instanceof List)
		{
		    
		    List l = (List) data;
		    
		    if (in < l.size ())
		    {
			
			return l.get (in);
			
		    } 
		    
		}
		
	    }
	    
	} catch (NumberFormatException e) {
	    
	    // It's not a number so assume that
	    // we have a Map.
	    if (data instanceof Map)
	    {
		
		Map map = (Map) data;
		
		return map.get (index);
		
	    } 
	    
	}

	return null;

    }

    /**
     * Get the Java field associated with the named field.  Return
     * null if there isn't one, or if we can't access it.
     *
     * @param field The name of the field.
     * @param clazz The Class to get the field from.
     * @return A List of Accessor objects used for delving into the
     *         classes to be sorted.
     */
    public static List getAccessorChain (String accessorRef,
					 Class  clazz)
	                                 throws IllegalArgumentException
    {

	StringTokenizer t = new StringTokenizer (accessorRef,
						 ".");

	Class c = clazz;

	List retdata = new ArrayList ();

	while (t.hasMoreTokens ())
	{

	    String tok = t.nextToken ();

	    Accessor a = new Accessor ();
	    
	    String index = "";

	    if (tok.endsWith ("]"))
	    {
		
		// It does, so now find the [
		index = tok.substring ((tok.indexOf ("[") + 1),
				       tok.length () - 1);

		a.setIndex (index);
		
		tok = tok.substring (0,
				     tok.indexOf ("["));

	    }

	    // Get the Fields.
	    Field[] fields = c.getFields ();
	    
	    Field f = null;

	    // See if the token matches...
	    for (int i = 0; i < fields.length; i++)
	    {
		
		if (fields[i].getName ().equals (tok))
		{
		    
		    // Found it...
		    f = fields[i];
		
		    break;

		}

	    }

	    if (f != null)
	    {

		c = f.getType ();

		if (!a.isIndexCorrectForType (c))
		{
		    
		    throw new IllegalArgumentException ("Field: " + 
							tok +
							" cannot be accessed with index: " +
							index +
							" since field type is: " +
							c.getName ());
			
		}

		a.accessor = f;

		retdata.add (a);

	    } else {

		// Now convert it to a method name and use the
		// JavaBeans convention...
		StringBuffer name = new StringBuffer (tok);

		name.setCharAt (0,
				Character.toUpperCase (tok.charAt (0)));

		name.insert (0,
			     "get");

		// Now get the method...
		Method m = Accessor.getNoParmJavaMethod (name.toString (),
							 c);

		if (m != null)
		{

		    c = m.getReturnType ();
		    
		    if (!a.isIndexCorrectForType (c))
		    {
			
			throw new IllegalArgumentException ("Method: " + 
							    tok +
							    " cannot be called with index: " +
							    index +
							    " since method return type is: " +
							    c.getName ());
			
		    }
		    
		    if (Void.class.isAssignableFrom (c))
		    {
			
			throw new IllegalArgumentException ("Method: " + 
							    tok +
							    " cannot be called on class: " +
							    c.getName () + 
							    " since return type is void");
			
		    }
		    
		    a.accessor = m;
		    
		    retdata.add (a);
		    
		    continue;

		} else {

		    // The field is not either a standard JavaBeans method so now just
		    // look for the method with the specified name.
		    m = Accessor.getNoParmJavaMethod (tok,
						      c);

		    if (m == null)
		    {
		    
			throw new IllegalArgumentException ("Cannot find method with name: " +
							    tok +
							    " in class: " +
							    c.getName ());

		    }

		    c = m.getReturnType ();
		    
		    if (!a.isIndexCorrectForType (c))
		    {
			
			throw new IllegalArgumentException ("Method: " + 
							    tok +
							    " cannot be called with index: " +
							    index +
							    " since method return type is: " +
							    c.getName ());
			
		    }
		    
		    if (Void.class.isAssignableFrom (c))
		    {
			
			throw new IllegalArgumentException ("Method: " + 
							    tok +
							    " cannot be called on class: " +
							    c.getName () + 
							    " since return type is void");
			
		    }
		    
		    a.accessor = m;
		    
		    retdata.add (a);
		    
		    continue;

		}

	    }

	}

	return retdata;

    }

    private static Method getNoParmJavaMethod (String method,
					       Class  c)
    {

	Method[] methods = c.getMethods ();

	for (int i = 0; i < methods.length; i++)
	{

	    if ((methods[i].getName ().equals (method))
		&&
		(methods[i].getParameterTypes ().length == 0)
	       )
	    {

		// This is the one...
		return methods[i];

	    }

	}

	return null;

    }

}
