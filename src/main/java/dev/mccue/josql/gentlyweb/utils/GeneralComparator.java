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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringTokenizer;


//import com.gentlyweb.xml.JDOMUtils;
//import com.gentlyweb.xml.JDOMXmlOutputter;

/**
 * A general purpose comparator designed to compare any pair of 
 * heterogeneous objects.
 * <p>
 * <a name="top" href="#bottom">Skip to bottom of description.</a>
 * </p>
 * <h4>Description Contents:</h4>
 * <ul>
 *   <li><a href="#1">Basic Operation</a></li>
 *   <li><a href="#2">Ascending/Descending Comparison</a></li>
 *   <li><a href="#3">Comparing values</a></li>
 *   <li><a href="#4">Multi field sorting</a></li>
 *   <li><a href="#5">Thread safety and reuse</a></li>
 *   <li><a href="#6">Efficiency</a></li>
 *   <li><a href="#7">Examples</a></li>
 * </ul>
 * <h4><a name="1">Basic Operation</a></h4>
 * <p>
 * This class works by you specify a number of <b>fields</b> that are then
 * used to access into each of the objects.  The term field is a bit mis-leading
 * since you can specify either a field name within a class or a zero argument
 * method name.
 * <br /><br />
 * When specifying a field you can use dot notation to acess into return types/
 * field types.
 * <br /><br />
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
 * Then to perform comparison between between 2 objects of type <b>A</b> on 
 * the <b>d</b> field in Class C you would specify a field of: <b>B.C.d</b>.
 * Notice that you don't specify the current class, all fields are taken relative
 * to the current type.
 * <br /><br />
 * Similarly, if class C actually looked like:
 * </p>
 * <pre>
 * public class C
 * {
 *    private String d = "";
 *
 *    public String getD ()
 *    {
 *       return d;
 *    }   
 * } 
 * </pre>
 * <p>
 * Then you would could use the same field since if a public field with the
 * specified name cannot be found then it is converted to a method name instead,
 * following JavaBeans conventions.  So the <b>d</b> is converted to <b>getD</b>
 * and a public method is looked for (with no arguments) with that name.  Finally if a 
 * <b>get*</b> method cannot be found then a method with just the name, in this case <b>d</b>,
 * is searched for that takes no arguments.  (Note: this has been added because there are a 
 * number of cases where the standard java.*.* classes DO NOT follow the JavaBeans conventions
 * but you would want to use a GeneralComparator).
 * <br /><br />
 * You can override the method name conversion by using the actual method name
 * (in case your method doesn't follow the JavaBeans convention), so if C 
 * looked like:
 * </p>
 * <pre>
 * public class C
 * {
 *    private d = "";
 *    
 *    public String getMyDField ()
 *    {
 *       return d;
 *    }
 * }
 * </pre>
 * <p>
 * You would then use a field of: <b>B.C.getMyDField</b> in which case the
 * method <b>getMyDField</b> would be looked for instead of looking for
 * field.
 * </p>
 * <h4><a name="2">Ascending/Descending Comparison</a></h4>
 * <p>
 * When you add a field you specify whether you want the comparison to
 * occur in ascending or descending order.  Setting to have a 
 * descending search just means that the result gained from 
 * <a href="#3">comparing values</a> is reversed.
 * </p>
 * <h4><a name="3">Comparing values</a></h4>
 * <p>
 * If a field value equates to a public Java field in the object then
 * we call: <code>java.lang.reflect.Field.get(Object)</code> on each
 * of the objects passed in and then if the type of the field implements
 * <code>Comparable</code> then we call: <code>Comparable.compareTo(Object,Object)</code>
 * to get the appropriate value.  If the type of the field does <b>NOT</b>
 * implement <code>Comparable</code> then we call <b>toString</b> on the object
 * returned and then call <code>String.compareTo(Object,Object)</code> for 
 * the value to return instead.
 * <br /><br />
 * Similarly we do the same when the field value equates to a public Java method
 * of the object.  We call <code>java.lang.reflect.Method.invoke(Object,Object[])</code>
 * with a zero-length argument list and then if the return type of the method 
 * implements <code>Comparable</code> we call <code>Comparable.compareTo(Object,Object)</code>
 * with the result of the method calls to get our value.  Otherwise we call
 * <b>toString</b> on the returned values and then call <code>String.compareTo(Object,Object)</code>
 * to get the value.
 * <br /><br />
 * The upshot of all this is that you can sort a list of objects on values
 * returned from method calls or by fields that may be X levels deep within 
 * an Object <b>WITHOUT</b> having to implement a complex version of the Comparable
 * interface yourself.
 * </p>
 * <h4><a name="4">Multi field sorting</a></h4>
 * <p>
 * You can specify as many fields as you want to compare on, when the comparison
 * is made (via the {@ #compare(Object,Object)} method) we only compare on
 * as many fields as we need to, so if the result of the first field comparison
 * indicates that the values are different we just return since there is no
 * point doing further comparing, other fields will have no effect.  Only
 * if the values are equal do we move onto "lower" fields.
 * </p>
 * <h4><a name="#5">Thread safety and reuse</a></h4>
 * <p>
 * This class is <b>NOT</b> Thread safe and never will be (or should be...)
 * since you would not want other threads modifying the fields you compare on.
 * Once configured however the comparator is perfectly reusable since the
 * only data it holds relates to the fields/methods that are accessed.  If you
 * do plan to use across multiple Threads then you need some kind of external
 * synchronization, for example:
 * </p>
 * <pre>
 * public synchronized void sortObjects (List objs)
 * {
 *
 *     Collections.sort (objs,
 *                       myGeneralComparator);
 *
 * }
 * </pre>
 * </p>
 * <h4><a name="#6">Efficiency</a></h4>
 * <p>
 * To try and access into the Object structure we use a {@link Getter}
 * which is basically a List of Field and Methods that should be 
 * accessed/invoked when trying to find the value we require.  Since we
 * are reliant on reflection for this the key factor is the cost of
 * traversing down the accessor chain (so the size of it will be important).
 * The accessor chain is gained when you call one of the <b>add***</b> methods
 * so the finding of the method/field is a one-off.
 * </p>
 * <h4><a name="#6.1">Warning on Getters</a></h4>
 * <p>
 * The Getter class supports the <b>[ ]</b> notation for accessing 
 * Maps and Lists however in terms of comparisons and sorting this means
 * little and should <b>NOT</b> be used!!!  This is not checked because
 * there may be situations where you would want to do it, however you would
 * need to ensure that your Lists/Maps contain heterogeneous Objects.
 * </p>
 * <h4><a name="#7">JDOM Support</a></h4>
 * <p>
 * This class is capable of initing itself from a <b>JDOM Element</b>
 * and also to save it's current state into a JDOM element.  This is
 * primarily to support the {@link ConfigList} and {@link ConfigMap}
 * objects, however it can be used in other places when you may want
 * to keep the state of the comparator.
 * </p>
 * <h4><a name="#7.1">Format</a></h4>
 * <p>
 * When {@link #getAsJDOMElement()} is called it will return a JDOM Element
 * in the form given below (conversely, the constructor {@link #GeneralComparator(Element)}
 * expects the passed in JDOM element to have the same format):
 * <pre>
 *   &#lt;comparator class="[[NAME OF CLASS THAT IS BEING COMPARED]]">
 *    &#lt;field id="[[ACCESSOR VALUE INTO THE OBJECT]]"
 *               type="either ASC or DESC" />
 *    &#lt;!-- 
 *     There can be X number of fields, the minimum is 1.
 *    -->
 *   &#lt;/>
 * </pre>
 * </p>
 * <h4><a name="#7">Examples</a></h4>
 * <p>
 * Say we wanted to sort a number of {@link Property} objects.
 * </p>
 * <pre>
 * // Create a new GeneralComparator.
 * GeneralComparator g = new GeneralComparator ({@link Property}.class);
 * 
 * // Now we want to sort them on type first, then id then description, then
 * // value.
 * g.{@link addField(String,String) addField} ("type",
 *             GeneralComparator.ASC);
 * g.{@link addField(String,String) addField} ("getID",
 *             GeneralComparator.ASC);
 * g.{@link addField(String,String) addField} ("description",
 *             GeneralComparator.ASC);
 * g.{@link addField(String,String) addField} ("value",
 *             GeneralComparator.ASC);
 *
 * // Then (by magic...) get our collection of Properties that can
 * // be sorted.
 * List properties = Helper.getProperties ();
 *
 * // Now sort them using our comparator.
 * Collections.sort (properties,
 *                   g);
 * </pre>
 * <p>
 * We could always set any of the fields to be a descending sort.  Notice
 * here that we use "getID" since that method name is not using the
 * correct JavaBeans convention.  Also, {@link Property} implements the
 * <code>Comparable</code> interface but using the comparator will
 * override it.
 * <br /><br />
 * Or if we wanted to sort a slice of messages from a {@link Logger}:
 * </p>
 * <pre>
 * GeneralComparator g = new GeneralComparator (Logger.Message.class);
 * 
 * // Sort on the time, but this time sort on the day of the Date
 * // this is a deprecated method but it's not a biggie.  We want
 * // them in reverse order.
 * g.addField ("time.day",
 *             GeneralComparator.DESC);
 *
 * // Get our slice...
 * long time = System.currentTimeMillis ();
 * Date now = new Date (time);
 * Date oneDayAgo = new Date (time - (24*60*60*1000));
 * int types = Logger.Message.INFORMATION || Logger.Message.ERROR;
 * List messages = myLogger.getMessages (now,
 *                                       oneDayAgo,
 *                                       types);
 * 
 * Collections.sort (messages,
 *                   g);
 *
 * // We could perform another sort but this time, sorting first
 * // on the type, this should be in ascending order, i.e. ERROR first...
 * g.addFieldBefore ("type",
 *                   GeneralComparator.ASC,
 *                   "time.day");
 *
 * Collections.sort (messages,
 *                   g);
 * </pre>
 * <p>
 * A quite common need is to sort the entries in a Map rather than the keys but
 * still maintain the key/value relationship.  To do so use the code below:
 * <pre>
 * // Get all the entries in the Map as a List.
 * List l = new ArrayList ();
 * l.addAll (myMap.entrySet ());
 *
 * // Create a GeneralComparator.  It is also possible to use the specific
 * // implementation of the Map.Entry interface for the specific Map but
 * // this way keeps it generic.
 * GeneralCompartor gc = new GeneralComparator (Map.Entry.class);
 *
 * // Specify the "value" (i.e. getValue method).
 * gc.addField ("value",
 *              GeneralComparator.DESC);
 *  
 * // Sort.
 * Collections.sort (l,
 *                   gc);
 * </pre>
 * <p>
 * <a href="#top" name="bottom">Back to top of description.</a>
 * </p>
 */
public class GeneralComparator implements Comparator, 
                                          Serializable//,
                                          //JDOMXmlOutputter
{

    public class XMLConstants
    {

	public static final String root = "comparator";
	public static final String clazz = "class";
	public static final String field = "field";
	public static final String id = "id";
	public static final String type = "type";

    }

    public int count = 0;

    /**
     * Use to indicate that a field should be sorted in ascending order.
     */
    public static final String ASC = "ASC";

    /**
     * Use to indicate that a field should be sorted in descending order.
     */
    public static final String DESC = "DESC";

    private List fields = new ArrayList ();

    private Class clazz = null;

    /**
     * Create a new GeneralComparator using the data held in the JDOM
     * element.
     *
     * @param root The root JDOM element.
     * @throws JDOMException If the format is incorrect.
     * @throws ChainException If we can't load the class that we need.
     * @throws IllegalArgumentException If the field is invalid.
     */
    /*
    public GeneralComparator (Element root)
	                      throws  JDOMException,
	                              ChainException,
                                      IllegalArgumentException
    {

	JDOMUtils.checkName (root,
			     GeneralComparator.XMLConstants.root,
			     true);

	// Get the class.
	String clazz = JDOMUtils.getAttributeValue (root,
						    GeneralComparator.XMLConstants.clazz);

	try
	{

	    this.clazz = Class.forName (clazz);

	} catch (Exception e) {

	    throw new ChainException ("Unable to load class: " +
				      clazz,
				      e);

	}

	// Get all the fields, there should be at least 1.
	List fEls = JDOMUtils.getChildElements (root,
						GeneralComparator.XMLConstants.field,
						true);

	for (int i = 0; i < fEls.size (); i++)
	{

	    Element e = (Element) fEls.get (i);

	    // Get the id attribute.
	    String id = JDOMUtils.getAttributeValue (e,
						     GeneralComparator.XMLConstants.id);

	    // Get the type attribute.
	    String type = JDOMUtils.getAttributeValue (e,
						       GeneralComparator.XMLConstants.type);

	    if ((!type.equals (GeneralComparator.ASC))
		&&
		(!type.equals (GeneralComparator.DESC))
	       )
	    {

		Attribute attr = JDOMUtils.getAttribute (e,
							 GeneralComparator.XMLConstants.type,
							 true);

		throw new JDOMException ("Type (path: " + 
					 JDOMUtils.getPath (attr) +
					 ") must be either: " +
					 GeneralComparator.ASC +
					 " or: " +
					 GeneralComparator.DESC);

	    }

	    // Add the field.
	    this.addField (id,
			   type);

	}

    }
    */

    public GeneralComparator (Class c)
    {

	this.clazz = c;

    }

    public Class getCompareClass ()
    {

	return this.clazz;

    }

    public void addField (Getter field,
			  String type)
                          throws IllegalArgumentException
    {

	// Get the class that the getter relates to, they MUST be the same class AND the
	// same object... classes loaded via other classloaders are not compatible.
	if (field.getBaseClass ().hashCode () != this.clazz.hashCode ())
	{

	    throw new IllegalArgumentException ("Class in Getter is: " + 
						field.getBaseClass ().getName () +
						" with hashCode: " +
						field.getBaseClass ().hashCode () +
						" which is incompatible with comparator class: " +
						this.clazz.getName () + 
						" with hashCode: " +
						this.clazz.hashCode ());

	}

	if (this.fields.size () == 0)
	{

	    this.fields.add (0,
			     new SortField (field,
					    type,
					    this.clazz));

	    return;

	}

	this.fields.add (this.fields.size (),
			 new SortField (field,
					type,
					this.clazz));
	

    }

    /**
     * Add a new field in at the specified index.  Remember that indices
     * start at 0 and proceed in asceding order.  If the index specified
     * is <0 then we add the field in at 0, moving everything else down
     * by 1.  If the index specified is >(fields.length - 1) then we just 
     * add to the end of the fields.
     *
     * @param field The field to add.
     * @param type The type, either GeneralComparator.ASC or GeneralComparator.DESC.
     * @param index The index to add at.
     * @throws IllegalArgumentException If we can't find the field in the
     *                                  class/class chain passed into the constructor.
     */
    public void addFieldAtIndex (String field,
				 String type,
				 int    index)
                                 throws IllegalArgumentException
    {

	if (index < 0)
	{

	    this.fields.add (0,
			     new SortField (field,
					    type,
					    this.clazz));

	    return;

	}

	if (index > (this.fields.size () - 1))
	{

	    this.fields.add (new SortField (field,
					    type,
					    this.clazz));

	    return;

	}

	this.fields.add (index,
			 new SortField (field,
					type,
					this.clazz));

    }

    /** 
     * Add a new field in BEFORE the named field, if we don't have the 
     * named field then we just call {@link #addField(String,String)} which
     * will add the field in after all the others.
     *
     * @param field The field to add.
     * @param type Sort either ascending or descending, should be either
     *             GeneralComparator.ASC or GeneralComparator.DESC.
     * @param ref The reference field.
     * @throws IllegalArgumentException If we can't find the field in the
     *                                  class/class chain passed into the constructor.
     */
    public void addFieldBefore (String field,
				String type,
				String ref)
                                throws IllegalArgumentException
    {

	// Get the field index...
	int fi = this.getFieldIndex (ref);

	if (fi != -1)
	{

	    this.addFieldAtIndex (field,
				  type,
				  --fi);

	} else {

	    this.addField (field,
			   type);

	}

    }

    /** 
     * Add a new field in AFTER the named field, if we don't have the 
     * named field then we just call {@link #addField(String,String)} which
     * will add the field in after all the others.
     *
     * @param field The field to add.
     * @param type Sort either ascending or descending, should be either
     *             GeneralComparator.ASC or GeneralComparator.DESC.
     * @param ref The reference field.
     * @throws IllegalArgumentException If we can't find the field in the
     *                                  class/class chain passed into the constructor.
     */
    public void addFieldAfter (String field,
			       String type,
			       String ref)
                               throws IllegalArgumentException
    {

	// Get the field index...
	int fi = this.getFieldIndex (ref);

	if (fi != -1)
	{

	    this.addFieldAtIndex (field,
				  type,
				  ++fi);

	} else {

	    this.addField (field,
			   type);

	}

    }

    /**
     * Remove a field that we sort on.  If we don't have the field then
     * we do nothing.
     *
     * @param field The field to remove.
     */
    public void removeField (String field)
    {

	SortField f = this.getField (field);

	if (f != null)
	{

	    this.fields.remove (f);

	}

    }

    /**
     * Add a field that we sort on, if you readd the same field then
     * the type is just updated.  The order in which you add the fields
     * provides the order in which the objects are sorted.
     *
     * @param field The field to sort on.
     * @param type The type either GeneralComparator.ASC or GeneralComparator.DESC.
     * @throws IllegalArgumentException If we can't find the field in the
     *                                  class/class chain passed into the constructor.
     */
    public void addField (String field,
			  String type)
                          throws IllegalArgumentException
    {

	// Find the field...
	SortField f = this.getField (field);

	if (f != null)
	{

	    f.type = type;
	    return;

	}

	this.addFieldAtIndex (field,
			      type,
			      (this.fields.size () + 1));

    }

    /**
     * Get the index of this field.
     *
     * @param field The field to look for.
     * @return The index (0 or greater) or -1 if we don't have the field.
     */
    private int getFieldIndex (String field)
    {

	for (int i = 0; i < this.fields.size (); i++)
	{

	    SortField f = (SortField) this.fields.get (i);

	    if (f.field.equals (field))
	    {

		return i;

	    }

	}

	return -1;	

    }

    /**
     * Get a field given a field name.
     *
     * @param field The name of the field.
     * @return The SortField object or null if we don't have it.
     */
    private SortField getField (String field)
    {

	for (int i = 0; i < this.fields.size (); i++)
	{

	    SortField f = (SortField) this.fields.get (i);

	    if (f.field.equals (field))
	    {

		return f;

	    }

	}

	return null;

    }

    /**
     * Implement the {@link Comparator.compare(Object,Object)} method.
     * Here we check each field in turn, we only check subsequent fields if
     * the "higher" up fields are equal.  So if fields 0 and 1 are both equal
     * then we check field 2 and so on...  Note: it is possible that we have
     * an exception thrown here, however the compare method doesn't allow
     * for exceptions to be thrown, so we just consume them and return 0, we
     * only catch IllegalAccessException and InvocationTargetException. 
     *
     * @param obj1 The first object.
     * @param obj2 The second object.
     * @return A value according to the rules laid out in {@link Comparator.compare(Object,Object)},
     *         if either object is <code>null</code> then we return 0 or we return 0 if
     *         either object returned from the accessor chain "get" call is null.
     */
    public int compare (Object obj1,
			Object obj2)
    {

	this.count++;

	if ((obj1 == null)
	    ||
	    (obj2 == null)
	   )
	{

	    return 0;

	}

	if ((!this.clazz.isAssignableFrom (obj1.getClass ()))
	    ||
	    (!this.clazz.isAssignableFrom (obj2.getClass ()))
	   )
	{

	    throw new IllegalArgumentException ("Expected objects of type: " +
						this.clazz.getName () +
						", got: " +
						obj1.getClass ().getName () + 
						", and: " +
						obj2.getClass ().getName ());

	}

	try
	{

	    for (int i = 0; i < this.fields.size (); i++)
	    {
		
		SortField f = (SortField) this.fields.get (i);

		Object val1 = f.getValue (obj1);
		Object val2 = f.getValue (obj2);

		// Can't compare what's not there, return 0.
		if ((val1 == null)
		    ||
		    (val2 == null)
		   )
		{

		    return 0;

		}

		int v = 0;
		    
		if (val1 instanceof Comparable)
		{
			
		    // We can use a simple compareTo.
		    Comparable comp = (Comparable) val1;
		    
		    v = comp.compareTo (val2);
			
		} else {
			
		    v = val1.toString ().compareTo (val2.toString ());
			
		}
		    
		if (v != 0)
		{
		    
		    if (f.getType ().equals (GeneralComparator.DESC))
		    {
			
			return -1 * v;
			
		    }
		    
		    // This is an ascending sort...
		    return v;
		    
		}
		
		// They are equal, so need to go to the next field...
		continue;
		
	    }
		
	} catch (IllegalAccessException e) {

	} catch (InvocationTargetException e) {

	}

	return 0;

    }

    /**
     * Implement the {@link Comparator.equals(Object)} method.
     * We just look through our fields and then compare the fields.
     * 
     * @param obj Another GeneralComparator.
     * @return <code>true</code> if all our fields match those in the passed in
     *         GeneralComparator AND that they are in the same order AND that they
     *         have the same type, <code>false</code> otherwise.
     */
    public boolean equals (Object obj)
    {

	GeneralComparator gc = (GeneralComparator) obj;

	List oFields = gc.getFields ();

	if (oFields.size () != this.fields.size ())
	{

	    return false;

	}

	for (int i = 0; i < this.fields.size (); i++)
	{

	    SortField f = (SortField) this.fields.get (i);

	    SortField of = (SortField) oFields.get (i);

	    if (f.compareTo (of) != 0)
	    {

		return false;

	    }

	}

	return true;

    }

    /**
     * Return a List of GeneralComparator.SortField objects, this is used in 
     * the {@link #equals(Object)} method.
     *
     * @return A List of GeneralComparator.SortField objects.
     */
    protected List getFields ()
    {

	return this.fields;

    }

    public int getCount ()
    {

	return this.count;

    }

    /**
     * Get the comparator data as a JDOM element.
     * 
     * @return The built element.
     */
    /*
    public Element getAsJDOMElement ()
    {

	Element root = new Element (GeneralComparator.XMLConstants.root);

	// Add the class.
	root.setAttribute (GeneralComparator.XMLConstants.clazz,
			   this.clazz.getName ());

	// Add all the fields.
	for (int i = 0; i < this.fields.size (); i++)
	{

	    SortField sf = (SortField) this.fields.get (i);

	    Element e = new Element (GeneralComparator.XMLConstants.field);

	    e.setAttribute (GeneralComparator.XMLConstants.id,
			    sf.getField ());
	    e.setAttribute (GeneralComparator.XMLConstants.type,
			    sf.getType ());

	    root.addContent (e);

	}

	return root;
	
    }
    */

    private class SortField implements Comparable
    {

	private String field = "";
	private String type = GeneralComparator.ASC;

	private Getter get = null;

	public SortField (String field,
			  String type,
			  Class  c)
                          throws IllegalArgumentException
	{

	    this (new Getter (field,
			      c),
		  type,
		  c);

	    this.field = field;

	}

	public SortField (Getter field,
			  String type,
			  Class  c)
                          throws IllegalArgumentException
	{

	    // Get the field or method...
	    this.get = field;

	    //this.field = field;

	    if ((!type.equals (GeneralComparator.ASC))
		&&
		(!type.equals (GeneralComparator.DESC))
	       )
	    {

		throw new IllegalArgumentException ("Type must be either: " +
						    GeneralComparator.ASC +
						    " or: " + 
						    GeneralComparator.DESC);

	    }	    

	    this.type = type;

	}

	public Object getValue (Object obj)
	                        throws IllegalAccessException,
	                               InvocationTargetException
	{

	    return this.get.getValue (obj);

	}

	protected String getField ()
	{

	    return this.field;

	}

	protected String getType ()
	{

	    return this.type;

	}

	public int compareTo (Object o)
	{

	    SortField f = (SortField) o;

	    if ((this.field.equals (f.getField ()))
		&&
		(this.type.equals (f.getType ()))
	       )
	    {

		return 0;

	    }

	    if (this.field.equals (f.getField ()))
	    {

		return this.type.compareTo (f.getType ());

	    }

	    return this.field.compareTo (f.getField ());

	}

    }

}
