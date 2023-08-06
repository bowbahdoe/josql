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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Date;

public class GeneralFilter 
{

    public static final int EQUALS = 0;
    public static final int NOT_EQUALS = 9;
    public static final int GREATER_THAN = 1;
    public static final int LESS_THAN = 2;
    public static final int CONTAINS = 3;
    public static final int NOT_CONTAINS = 10;
    private static final int IN_RANGE = 4;
    public static final int STARTS_WITH = 5;
    public static final int ENDS_WITH = 6;
    public static final int KEYS = 7;
    public static final int VALUES = 8;

    private List fields = new ArrayList ();

    private Class clazz = null;

    private boolean nullAcceptPolicy = false;

    public GeneralFilter (Class c)
    {

	this.clazz = c;

    }
    
    /**
     * Specify what the policy should be when a null value is returned from an
     * accesor chain, in other words, when the comparison is made should
     * a <code>null</code> value return <code>true</code> or <code>false</code>.
     *
     * @param policy The policy to use.
     */
    public void setNullAcceptPolicy (boolean policy)
    {

	this.nullAcceptPolicy = policy;

    }

    /**
     * Get the null accept policy, this governs what happens when a null is
     * observed from the result of an accessor chain call, <code>true</code>
     * indicates that the value is accepted, <code>false</code> indicates
     * that it is rejected.
     * 
     * @return The policy.
     */
    public boolean getNullAcceptPolicy ()
    {

	return this.nullAcceptPolicy;

    }

    /**
     * Add a new field to check for the date to be in the specified range.
     *
     * @param field The field spec.
     * @param max The upper date.
     * @param min The lower date.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.
     */
    public void addField (String field,
			  Date   max,
			  Date   min)
                          throws IllegalArgumentException
    {

	this.fields.add (new DateFilterField (field,
					      max,
					      min,
					      null,
					      GeneralFilter.IN_RANGE,
					      this.clazz));

    }			  

    /**
     * Set new date range values for the specified date filter field.
     *
     * @param field The field spec.
     * @param max The upper date.
     * @param min The lower date.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       Date   max,
			       Date   min)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof DateFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Date.class.getName ());

	}

	DateFilterField f = (DateFilterField) ff;

	f.max = max;
	f.min = min;

    }

    /**
     * Add a new field for a Date comparison.
     * Note: the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS, GeneralFilter.LESS_THAN,
     * GeneralFilter.GREATER_THAN.
     *
     * @param field The field spec.
     * @param value The Date to check against.
     * @param type The type, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method.
     */ 
    public void addField (String field,
			  Date   value,
			  int    type)
                          throws IllegalArgumentException
    {

	this.fields.add (new DateFilterField (field,
					      null,
					      null,
					      value,
					      type,
					      this.clazz));

    }

    /**
     * Set a new date value for the specified date filter field.
     *
     * @param field The field spec.
     * @param value The new value.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       Date   value)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof DateFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Date.class.getName ());

	}

	DateFilterField f = (DateFilterField) ff;

	f.value = value;

    }

    /**
     * Add a new field for a boolean comparison.
     * Note: the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS.
     *
     * @param field The field spec.
     * @param value The Date to check against.
     * @param type The type, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method.
     */ 
    public void addField (String  field,
			  boolean value,
			  int     type)
                          throws  IllegalArgumentException
    {

	this.fields.add (new BooleanFilterField (field,
						 value,
						 type,
						 this.clazz));

    }

    /**
     * Set a new value for the specified boolean filter field.
     *
     * @param field The field spec.
     * @param value The new value.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String  field,
			       boolean value)
	                       throws  IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof BooleanFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Boolean.class.getName ());

	}

	BooleanFilterField f = (BooleanFilterField) ff;

	f.value = value;

    }

    /**
     * Add a new field for a number comparison.  Even though you should pass in 
     * a double <b>ANY</b> number can be checked for, regardless of primitive
     * type.
     * <br /><br />
     * <b>Note:</b> the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS, GeneralFilter.LESS_THAN,
     * GeneralFilter.GREATER_THAN.
     * <br /><br />
     * <b>Warning:</b> it is valid to pass in a value of <b>4</b> for the
     * type, which maps to a range check for the values however this is NOT
     * recommended since in this method we set the maximum and minimum values
     * to 0, so unless the value you are looking for is 0 you would get
     * <code>false</code> returned from the checking of the field in the filter,
     * probably not what you want...  If you want to check a range then
     * use {@link #addField(String,double,double)}.
     *
     * @param field The field spec.
     * @param value The value to check against.
     * @param type The type, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method.
     */ 
    public void addField (String field,
			  double value,
			  int    type)
                          throws IllegalArgumentException
    {

	this.fields.add (new NumberFilterField (field,
						0,
						0,
						value,
						type,
						this.clazz));

    }

    /**
     * Set new maximum and minimum values for the specified number filter field.
     *
     * @param field The field spec.
     * @param value The new value.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       double value)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof NumberFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Number.class.getName ());

	}

	NumberFilterField f = (NumberFilterField) ff;

	f.val = value;

    }

    /**
     * Add a new field for a number range comparison.  Even though you should pass in 
     * a double <b>ANY</b> number can be checked for, regardless of primitive
     * type.  The checking is such that if the value equals one of the values (max or min) then
     * we find a match.
     *
     * @param field The field spec.
     * @param max The maximum value to check against.
     * @param min The minimum value to check against.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  
     */ 
    public void addField (String field,
			  double max,
			  double min)
    {

	this.fields.add (new NumberFilterField (field,
						max,
						min,
						0,
						GeneralFilter.IN_RANGE,
						this.clazz));

    }

    /**
     * Set new maximum and minimum values for the specified number filter field.
     *
     * @param field The field spec.
     * @param max The new maximum to check against.
     * @param min The new minimum to check against.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       double max,
			       double min)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof NumberFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Number.class.getName ());

	}

	NumberFilterField f = (NumberFilterField) ff;

	f.max = max;
	f.min = min;

    }

    /**
     * Add a new field for a String comparison.  
     * <br /><br />
     * <b>Note:</b> the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS, GeneralFilter.CONTAINS,
     * GeneralFilter.NOT_CONTAINS, GeneralFilter.STARTS_WITH, GeneralFilter.ENDS_WITH.
     * <br /><br />
     * <b>Warning:</b> using this method will cause the <b>toString ()</b> method
     * to be called on the value we get from the field regardless of type, if you
     * need to compare objects then use the method {@link #addField(String,Object,Comparator,int)}
     * or {@link #addField(String,Object,int)}.
     *
     * @param field The field spec.
     * @param value The value to compare against.
     * @param type The type of comparison, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method.
     */
    public void addField (String field,
			  String value,
			  int    type)
                          throws IllegalArgumentException
    {

	this.fields.add (new StringFilterField (field,
						value,
						type,
						this.clazz));

    }

    /**
     * Set the value for the specified String filter field.
     *
     * @param field The field spec.
     * @param value The value to compare against.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       String value)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof StringFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						String.class.getName ());

	}

	StringFilterField f = (StringFilterField) ff;


	f.val = value;

    }

    /**
     * Add a new field for an Object comparison.  The passed in Object <b>MUST</b>
     * implement the Comparable interface otherwise an exception if thrown.  We compare
     * the object gained from the field spec to the object passed in by calling
     * <code>compareTo([Object returned from field spec call])</code> passing the object
     * returned to the compareTo method of the object passed in here.  It is your responsibility
     * to ensure that the object returned from the field spec call will <b>NOT</b> cause
     * a ClassCastException to be thrown in the compareTo method.
     * <br /><br />
     * <b>Note:</b> the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS, GeneralFilter.LESS_THAN,
     * GeneralFilter.GREATER_THAN.
     * If you pass in GeneralFilter.NOT_EQUALS then if the compareTo method returns something
     * other than 0 then we accept the object in the filter.
     *
     * @param field The field spec.
     * @param value The value to compare against.
     * @param type The type of comparison, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method, or if the passed in object does not implement
     *         the Comparable interface.
     */
    public void addField (String field,
			  Object object,
			  int    type)
                          throws IllegalArgumentException
    {

	if (object.getClass ().isAssignableFrom (Comparable.class))
	{

	    throw new IllegalArgumentException ("Object does implement the: " + 
						Comparable.class.getName () + 
						" interface.");

	}

	this.fields.add (new ObjectFilterField (field,
						object,
						null,
						type,
						this.clazz));

    }

    /**
     * Set the value for the specified field.
     *
     * @param field The field spec.
     * @param value The value to compare against.
     * @throws IllegalArgumentException If we can't find the specified field.
     */
    public void setFieldValue (String field,
			       Object value)
	                       throws IllegalArgumentException
    {

	// Get the field.
	FilterField ff = this.getField (field);

	if (ff == null)
	{

	    throw new IllegalArgumentException ("Field: " +
						field +
						" not found.");

	}

	
	if (!(ff instanceof ObjectFilterField))
	{

	    throw new IllegalArgumentException ("Field: " +
						field + 
						" filters on: " +
						ff.c.getName () +
						" but expected to filter on: " +
						Object.class.getName ());

	}

	ObjectFilterField f = (ObjectFilterField) ff;


	f.obj = value;

    }

    /**
     * Add a new field for an Object comparison.  We use the passed in
     * Comparator to compare the objects, in the call to the <code>compare</code>
     * method we pass the object passed into this method as the first argument
     * and the object returned from the field spec call as the second argument.
     * <br /><br />
     * It is your responsibility
     * to ensure that the object passed in and returned from the field spec call will <b>NOT</b> cause
     * a ClassCastException to be thrown in the compare method.
     * <br /><br />
     * <b>Note:</b> the only supported types for ths method are: 
     * GeneralFilter.EQUALS, GeneralFilter.NOT_EQUALS, GeneralFilter.LESS_THAN,
     * GeneralFilter.GREATER_THAN.
     * If you pass in GeneralFilter.NOT_EQUALS then if the compare method returns something
     * other than 0 then we accept the object in the filter.
     *
     * @param field The field spec.
     * @param value The value to compare against.
     * @param type The type of comparison, see above for valid values.
     * @throws IllegalArgumentException If the field spec is not valid for the
     *         class set in the constructor.  Or if you pass in an invalid
     *         type for this method, or if the passed in object does not implement
     *         the Comparable interface.
     */
    public void addField (String     field,
			  Object     object,
			  Comparator comp,
			  int        type)
                          throws     IllegalArgumentException
    {

	this.fields.add (new ObjectFilterField (field,
						object,
						comp,
						type,
						this.clazz));

    }

    /**
     * Get the named field, we get the field from our list of fields.
     *
     * @param field The field spec.
     * @return The FilterField (well the sub-class) or null if we can't
     *         find the field.
     */
    private FilterField getField (String field)
    {

	for (int i = 0; i < this.fields.size (); i++)
	{

	    FilterField f = (FilterField) this.fields.get (i);

	    if (f.field.equals (field))
	    {

		return f;

	    }

	}

	return null;

    }

    /**
     * Get the class that we are filtering.
     *
     * @return The Class.
     */
    public Class getFilterClass ()
    {

	return this.clazz;

    }

    /**
     * Cycle over all our fields and check to see if this object
     * matches.  We return at the first field that does not match.
     * 
     * @param o The object to check the fields against.
     * @return <code>true</code> if all our fields match, <code>false</code>
     *         otherwise.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.
     */
    public boolean accept (Object o)
                           throws IllegalAccessException,
                                  InvocationTargetException,
	                          FilterException
    {

	// Cycle over our filters and do it...
	for (int i = 0; i < this.fields.size (); i++)
	{
	    
	    FilterField f = (FilterField) this.fields.get (i);
	    
	    if (!f.accept (o))
	    {
		
		return false;
		
	    }

	}

	return true;

    }

    /**
     * Iterate over the Set and filter the objects it contains.
     * Any objects that match, via the {@link #accept(Object)}
     * method will then be added to the <b>newSet</b> parameter.  Since we use
     * an <b>Iterator</b> to cycle over the Set the ordering of the values added
     * to the <b>newSet</b> is dependent on the ordering of the <b>newSet</b> Set.
     * <br /><br />
     * The Set <b>set</b> is left unchanged.
     *
     * @param set The Set to filter.
     * @param newSet The Set to add successfully filtered objects to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (Set    set,
			Set    newSet)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	Iterator iter = set.iterator ();

	while (iter.hasNext ())
	{

	    Object o = iter.next ();
	    
	    if (this.accept (o))
	    {

		newSet.add (o);

	    }

	}

    }

    /**
     * Iterate over the Set and filter the objects it contains.
     * Any objects that match, via the {@link #accept(Object)}
     * method will then be added to the <b>newList</b> parameter.  Since we use
     * an <b>Iterator</b> to cycle over the Set the ordering of the values added
     * to the <b>newList</b> is dependent on the ordering of the <b>set</b> Set.
     * <br /><br />
     * The Set <b>set</b> is left unchanged.
     *
     * @param set The Set to filter.
     * @param newList The List to add successfully filtered objects to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (Set    set,
			List   newList)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	Iterator iter = set.iterator ();

	while (iter.hasNext ())
	{

	    Object o = iter.next ();
	    
	    if (this.accept (o))
	    {

		newList.add (o);

	    }

	}

    }

    /**
     * Iterate over the List and filter the objects it contains.
     * Any objects that match, via the {@link #accept(Object)}
     * method will then be added to the <b>newSet</b> parameter.  
     * <br /><br />
     * The List <b>list</b> is left unchanged.
     *
     * @param list The List to filter.
     * @param newSet The Set to add successfully filtered objects to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (List   list,
			Set    newSet)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	int size = list.size ();

	for (int i = 0; i < size; i++)
	{

	    Object o = list.get (i);

	    if (this.accept (o))
	    {

		newSet.add (o);

	    }

	}

    }

    /**
     * Iterate over the Map and filter either the Keys or Values in the Map
     * given our fields.  You specify whether you want the Keys or Values to
     * be filtered using the <b>type</b> parameter, pass either GeneralFilter.KEYS
     * or GeneralFilter.VALUES.  Any values that match, via the {@link #accept(Object)}
     * method will then be added to the <b>newMap</b> parameter.  Since we use
     * an <b>Iterator</b> to cycle over the Map the ordering of the values added
     * to the <b>newMap</b> is dependent on the ordering of the <b>newMap</b> Map.
     * <br /><br />
     * The Map <b>map</b> is left unchanged.
     * <br /><br />
     * <b>Note:</b> if the <b>type</b> parm is <b>NOT</b> GeneralFilter.KEYS
     * or GeneralFilter.VALUES then it's assumed you want to filter on the values.
     *
     * @param map The Map to filter.
     * @param type The type to filter on, either keys or values.
     * @param newMap The map to add successfully filtered keys/values to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (Map map,
			int type,
			Map newMap)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	if ((type != GeneralFilter.KEYS)
	    &&
	    (type != GeneralFilter.VALUES)
	   )
	{

	    type = GeneralFilter.VALUES;

	}

	Iterator iter = map.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Object key = iter.next ();
	    Object value = map.get (key);

	    if (type == GeneralFilter.KEYS)
	    {

		if (this.accept (key))
		{

		    newMap.put (key,
				value);

		}

	    }

	    if (type == GeneralFilter.VALUES)
	    {

		if (this.accept (value))
		{
		
		    newMap.put (key,
				value);

		}

	    }

	}

    }

    /**
     * Iterate over the Collection and filter given our fields.  
     * <br /><br />
     * The Collection <b>collection</b> is left unchanged.
     * <br /><br />
     * <b>Note:</b> since we use an Iterator to iterate over the Collection
     * it is <b>SLOWER</b> than if you have a List and use the {@link #filter(List,List)}
     * method since that uses List.size and it has been shown that using the <b>get</b>
     * method can be an order of magnitude faster than using the Iterator.  This
     * method is really here to allow filtering of things like <b>Sets</b>.
     * It would be interesting to determine whether performing the following would
     * be more efficient (i.e. faster...) than using the Iterator:
     * <pre>
     *   // Get as an Object array.
     *   Object[] objs = collection.toArray ();
     *
     *   int size = objs.length;
     *   for (int i = 0; i < size; i++)
     *   {
     *
     *       if (this.accept (objs[i]))
     *       {
     *
     *          newCollection.add (objs[i]);
     *
     *       }
     *
     *   } 
     * </pre>
     * <p>
     * If you find this to be the case, please contact <b>code-monkey@gentlyweb.com</b>.
     * <br /><br />
     * The bottom line is, if you have a List to filter then use the {@link #filter(List,List)}
     * method rather than this one.
     * </p>
     *
     * @param collection The Collection to filter.
     * @param newCollection The Collection to add successfully filtered objects to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (Collection collection,
			Collection newCollection)
                        throws     IllegalAccessException,
                                   InvocationTargetException,
                                   FilterException
    {

	Iterator iter = collection.iterator ();

	while (iter.hasNext ())
	{

	    Object val = iter.next ();

	    if (this.accept (val))
	    {

		newCollection.add (val);

	    }

	}

    }

    /**
     * Cycle over the List and filter given our fields.  The filtered
     * objects are added to the <b>newList</b> in the order they are gained
     * from <b>list</b>.
     * <br /><br />
     * The List <b>list</b> is left unchanged.
     * <br /><br />
     * This method will be <b>much</b> quicker than using the {@link #filter(Collection,Collection)}
     * method since it uses the <b>get</b> method of List rather than an Iterator.  However if
     * you <b>need</b> to <b>iterate</b> over the List rather than use direct access then
     * cast as a Collection and call {@link #filter(Collection,Collection)} instead.
     *
     * @param list The List to filter.
     * @param newList The List to add successfully filtered objects to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filter (List   list,
			List   newList)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	int size = list.size ();

	for (int i = 0; i < size; i++)
	{

	    Object o = list.get (i);

	    if (this.accept (o))
	    {

		newList.add (o);

	    }

	}

    }

    /**
     * Filter an array of objects and return a new array of the filtered 
     * objects.
     * <br /><br />
     * The object array is left unchanged.
     * <br /><br />
     * It should be noted that we perform a bit of a cheat here, we use
     * an intermediate <b>ArrayList</b> to add the new objects into and then
     * call <b>toArray</b> to get the objects.  This may have efficiency
     * considerations but we're pretty sure that the implementation of
     * ArrayList is gonna be as fast we could write!
     *
     * @param objects The objects to filter.
     * @return A new Object array of the filtered objects.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public Object[] filter (Object[] objects)
                            throws   IllegalAccessException,
                                     InvocationTargetException,
                                     FilterException
    {

	List l = new ArrayList ();

	int length = objects.length;

	for (int i = 0; i < length; i++)
	{

	    if (this.accept (objects[i]))
	    {

		l.add (objects[i]);

	    }

	}

	return l.toArray ();

    }

    /**
     * Iterate over the Map and filter either the Keys or Values in the Map
     * given our fields.  You specify whether you want the Keys or Values to
     * be filtered using the <b>type</b> parameter, pass either GeneralFilter.KEYS
     * or GeneralFilter.VALUES.  Any values that match, via the {@link #accept(Object)}
     * method will then be added to the new Map.  
     * <br /><br />
     * The Map is left unchanged.
     * <br /><br />
     * <b>Note:</b> if the <b>type</b> parm is <b>NOT</b> GeneralFilter.KEYS
     * or GeneralFilter.VALUES then it's assumed you want to filter on the values.
     * <br /><br />
     * We try and create a new instance of the same type as the Map passed in.
     * So if the Map passed in is actually a HashMap then we create a new
     * HashMap and then add to that.  If the passed in Map actually is a 
     * SortedMap then we call {@link #filter(SortedMap,int)} instead.  There is 
     * a potential problem here in that we can only call the default no argument
     * constructor for the new Map, if you are using a HashMap and have tuned
     * it with a load factor and capacity then this method will ruin that and
     * we recommend that you use: {@link #filter(Map,int,Map)} instead.
     *
     * @param map The Map to filter.
     * @param type The type to filter on, either keys or values.
     * @return A new Map to add successfully filtered keys/values to.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  It is also thrown
     *                         if we cannot create the new instance of Map
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure.  
     */
    public Map filter (Map    map,
		       int    type)
                       throws IllegalAccessException,
                              InvocationTargetException,
                              FilterException
    {

	// Get the class of the passed in Map.
	// See if it's really a SortedMap.
	Class c = map.getClass ();

	if (c.isAssignableFrom (SortedMap.class))
	{

	    return this.filter ((SortedMap) map,
				type);

	}

	// Create a new instance of the Map...
	Map nMap = null;

	try
	{
	    
	    nMap = (Map) c.newInstance ();

	} catch (Exception e) {

	    throw new FilterException ("Unable to create new instance of: " +
				       map.getClass ().getName () + 
				       ", root cause: " +
				       e.getMessage (),
				       e);

	}

	this.filter (map,
		     type,
		     nMap);

	return nMap;

    }

    /**
     * Iterate over the SortedMap and filter either the Keys or Values in the SortedMap
     * given our fields.  You specify whether you want the Keys or Values to
     * be filtered using the <b>type</b> parameter, pass either GeneralFilter.KEYS
     * or GeneralFilter.VALUES.  Any values that match, via the {@link #accept(Object)}
     * method will then be added to the new SortedMap.  
     * <br /><br />
     * The Map is left unchanged.
     * <br /><br />
     * <b>Note:</b> if the <b>type</b> parm is <b>NOT</b> GeneralFilter.KEYS
     * or GeneralFilter.VALUES then it's assume you want to filter on the values.
     * <br /><br />
     * We try and create a new instance of the same type as the SortedMap passed in.
     * And then get the Comparator from the old SortedMap and use it in the
     * constructor of the new SortedMap.  If your SortedMap doesn't use a 
     * Comparator then it doesn't matter since if your SortedMap follows the
     * general contract for a SortedMap then it should ignore the Comparator
     * value if it is null.  If the SortedMap passed in doesn't have 
     * a constructor with a single Comparator argument then we try and create
     * a new version via <b>Class.newInstance ()</b>, i.e. via a blank
     * constructor, if that isn't present or not accessible then we 
     * throw an exception.
     *
     * @param map The SortedMap to filter.
     * @param type The type to filter on, either keys or values.
     * @return A new SortedMap with the successfully filtered keys/values added to it.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date. It is also thrown
     *                         if we cannot create the new instance of SortedMap
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure.  
     */
    public SortedMap filter (SortedMap map,
		             int       type)
                             throws    IllegalAccessException,
                                       InvocationTargetException,
                                       FilterException
    {

	// Get the constructor that has a single Comparator argument.
	SortedMap nMap = null;

	try
	{

	    Class[] types = {Comparator.class};

	    Constructor con = map.getClass ().getConstructor (types);

	    // Invoke it...
	    try
	    {

		Object[] parms = {map.comparator ()};

		nMap = (SortedMap) con.newInstance (parms);

	    } catch (Exception e) {

		throw new FilterException ("Unable to create new instance of: " +
					   map.getClass ().getName () +
					   " using the constructor that takes a single: " +
					   Comparator.class.getName () +
					   " argument, root cause: " + 
					   e.getMessage (),
					   e);

	    }

	} catch (Exception e) {

	    // Try a new instance...
	    try
	    {

		nMap = (SortedMap) map.getClass ().newInstance ();

	    } catch (Exception ee) {

		throw new FilterException ("Unable to create a new instance of: " + 
					   map.getClass ().getName () + 
					   ", cannot find no argument constructor or constructor that takes java.util.Comparator as it's only argument, root cause: " + ee.getMessage (),
					   ee);

	    }

	}

	this.filter (map,
		     type,
		     nMap);

	return nMap;

    }

    /**
     * Iterate over the Collection and filter given our fields, return the filtered
     * objects in a new Collection.
     * <br /><br />
     * The Collection passed in is left unchanged.
     * <br /><br />
     * Effectively this method is just a wrapper for {@link #filter(Collection,Collection)}.
     * <br /><br />
     * The bottom line is, if you have a List to filter then use the {@link #filter(List,List)}
     * method rather than this one.
     * <br /><br />
     * We try and create a new instance of the same type as the Collection passed in.
     * So if the Collection passed in is actually a ArrayList then we create a new
     * ArrayList and then add to that.  If the passed in Collection actually is a 
     * SortedSet then we call {@link #filter(SortedSet,int)} instead, this is to
     * preserve any Comparator that may be used in sorting the Collection.
     * </p>
     *
     * @param collection The Collection to filter.
     * @return A new Collection with the successfully filtered objects added.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.   It is also thrown
     *                         if we cannot create the new instance of Collection
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure. 
     */
    public Collection filter (Collection collection)
                              throws     IllegalAccessException,
                                         InvocationTargetException,
                                         FilterException
    {

	// Get the class of the passed in Collection.
	// See if it's really a SortedSet.
	Class c = collection.getClass ();

	if (c.isAssignableFrom (SortedSet.class))
	{

	    return this.filter ((SortedSet) collection);

	}

	// Create a new instance of the Collection...
	Collection nCol = null;

	try
	{

	    nCol = (Collection) c.newInstance ();

	} catch (Exception e) {

	    throw new FilterException ("Unable to create new instance of: " +
				       collection.getClass ().getName () + 
				       ", root cause: " + 
				       e.getMessage (),
				       e);

	}

	this.filter (collection,
		     nCol);

	return nCol;	

    }

    /**
     * Iterate over the Set and filter given our fields, return the filtered
     * objects in a new Set.
     * <br /><br />
     * The Set passed in is left unchanged.
     * <br /><br />
     * Effectively this method is just a wrapper for {@link #filter(Set,Set)}.
     * <br /><br />
     * We try and create a new instance of the same type as the Set passed in.
     * So if the Set passed in is actually a HashSet then we create a new
     * HashSet and then add to that.  If the passed in Set actually is a 
     * SortedSet then we call {@link #filter(SortedSet,int)} instead, this is to
     * preserve any Comparator that may be used in sorting the Set.
     * </p>
     *
     * @param set The Set to filter.
     * @return A new Set with the successfully filtered objects added.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  It is also thrown
     *                         if we cannot create the new instance of Set
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure.  
     */
    public Set filter (Set    set)
                       throws IllegalAccessException,
                              InvocationTargetException,
                              FilterException
    {

	// Get the class of the passed in Collection.
	// See if it's really a SortedSet.
	Class c = set.getClass ();

	if (c.isAssignableFrom (SortedSet.class))
	{

	    return this.filter ((SortedSet) set);

	}

	// Create a new instance of the Set...
	Set nSet = null;

	try
	{

	    nSet = (Set) c.newInstance ();

	} catch (Exception e) {

	    throw new FilterException ("Unable to create new instance of: " +
				       set.getClass ().getName () + 
				       ", root cause: " + 
				       e.getMessage (),
				       e);

	}

	this.filter (set,
		     nSet);

	return nSet;	

    }

    /**
     * Iterate over the SortedSet and filter the objects it contains.
     * Any values that match, via the {@link #accept(Object)}
     * method will then be added to the new SortedMap.  
     * <br /><br />
     * The SortedSet is left unchanged.
     * <br /><br />
     * We try and create a new instance of the same type as the SortedSet passed in.
     * And then get the Comparator from the old SortedMet and use it in the
     * constructor of the new SortedMet.  If your SortedMet doesn't use a 
     * Comparator then it doesn't matter since if your SortedMet follows the
     * general contract for a SortedMet then it should ignore the Comparator
     * value if it is null.  If the SortedMet passed in doesn't have 
     * a constructor with a single Comparator argument then we try and create
     * a new version via <b>Class.newInstance ()</b>, i.e. via a blank
     * constructor, if that isn't present or not accessible then we 
     * throw an exception.
     *
     * @param set The SortedMet to filter.
     * @return A new SortedMet with successfully filtered objects added to it.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date. It is also thrown
     *                         if we cannot create the new instance of SortedSet
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure.  
     */
    public SortedSet filter (SortedSet set)
                             throws    IllegalAccessException,
                                       InvocationTargetException,
                                       FilterException
    {

	// Get the constructor that has a single Comparator argument.
	SortedSet nSet = null;

	try
	{

	    Class[] types = {Comparator.class};

	    Constructor con = set.getClass ().getConstructor (types);

	    // Invoke it...
	    try
	    {

		Object[] parms = {set.comparator ()};

		nSet = (SortedSet) con.newInstance (parms);

	    } catch (Exception e) {

		throw new FilterException ("Unable to create new instance of: " +
					   set.getClass ().getName () +
					   " using the constructor that takes a single: " +
					   Comparator.class.getName () +
					   " argument, root cause: " + 
					   e.getMessage (),
					   e);

	    }

	} catch (Exception e) {

	    // Try a new instance...
	    try
	    {

		nSet = (SortedSet) set.getClass ().newInstance ();

	    } catch (Exception ee) {

		throw new FilterException ("Unable to create a new instance of: " + 
					   set.getClass ().getName () + 
					   ", cannot find no argument constructor or constructor that takes java.util.Comparator as it's only argument, root cause: " + ee.getMessage (),
					   ee);

	    }

	}

	this.filter (set,
		     nSet);

	return nSet;

    }

    /**
     * Cycle over the List and filter given our fields.  The filtered
     * objects are added to a new List and then returned.
     * <br /><br />
     * The List <b>list</b> is left unchanged.
     * <br /><br />
     * This method will be <b>much</b> quicker than using the {@link #filter(Collection,Collection)}
     * method since it uses the <b>get</b> method of List rather than an Iterator.  However if
     * you <b>need</b> to <b>iterate</b> over the List rather than use direct access then
     * cast as a Collection and call {@link #filter(Collection,Collection)} instead and then
     * cast the return as a List.
     * <br /><br />
     * We try and create a new instance of the same type as the List passed in.
     * So if the List passed in is actually an ArrayList then we create a new
     * ArrayList and then add to that.  
     * </p>
     *
     * @param list The List to filter.
     * @return A new List with the successfully filtered objects added to it.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  It is also thrown
     *                         if we cannot create the new instance of List
     *                         it will then contain a nested exception with the "real"
     *                         reason for the failure.  
     */
    public List filter (List   list)
                        throws IllegalAccessException,
                               InvocationTargetException,
                               FilterException
    {

	// Get the class of the passed in List.
	Class c = list.getClass ();

	// Create a new instance of the List.
	List nList = null;

	try
	{

	    nList = (List) c.newInstance ();

	} catch (Exception e) {

	    throw new FilterException ("Unable to create new instance of: " +
				       list.getClass ().getName () + 
				       ", root cause: " + 
				       e.getMessage (),
				       e);

	}

	this.filter (list,
		     nList);

	return nList;	

    }

    /**
     * Cycle over the List and filter given our fields directly from the passed in
     * List.  The filtered objects are removed from the passed List.  
     * This method will <b>probably</b> be slower than doing:
     * <pre>
     *   GeneralFilter gf = new GeneralFilter (MyObjectClass);
     *
     *   // ... configure the filter ...
     * 
     *   List myList = gf.filter (myList);
     * </pre>
     * <p>
     * This is because we have to here use an Iterator to strip out the unwanted 
     * objects rather than using the <b>get</b> method which is what {@link #filter(List)}
     * uses.
     *
     * @param list The List to filter.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filterAndRemove (List   list)
                                 throws IllegalAccessException,
                                        InvocationTargetException,
                                        FilterException
    {
	
	this.filterAndRemove ((Collection) list);

    }

    /**
     * Cycle over the Collection and filter given our fields directly from the passed in
     * Set.  The filtered objects are removed from the passed Collection.  
     *
     * @param col The Collection to filter.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filterAndRemove (Collection col)
                                 throws     IllegalAccessException,
                                            InvocationTargetException,
                                            FilterException
    {
	
	Iterator iter = col.iterator ();

	while (iter.hasNext ())
	{

	    if (!this.accept (iter.next ()))
	    {

		iter.remove ();

	    }

	}

    }

    /**
     * Cycle over the Set and filter given our fields directly from the passed in
     * Set.  The filtered objects are removed from the passed Set.  
     *
     * @param set The Set to filter.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filterAndRemove (Set    set)
                                 throws IllegalAccessException,
                                        InvocationTargetException,
                                        FilterException
    {
	
	this.filterAndRemove ((Collection) set);

    }

    /**
     * Iterate over the Map and filter given our fields directly from the passed in
     * Map.  The filtered objects are removed from the passed Map.  
     * <br /><br /
     * <b>Note:</b> if the <b>type</b> parm is <b>NOT</b> GeneralFilter.KEYS
     * or GeneralFilter.VALUES then it's assumed you want to filter on the values.
     *
     * @param set The Set to filter.
     * @throws InvocationTargetException If we cannot execute the associated
     *                                   {@link Accessor} chain and get the
     *                                   value.
     * @throws IllegalAccessException If we cannot execute the associated
     *                                {@link Accessor} chain because of a 
     *                                security violation.
     * @throws FilterException Thrown if the return type is not what is
     *                         expected from the field type, for example if
     *                         we are checking a java.utli.Date but the returned type
     *                         is NOT of type java.util.Date.  
     */
    public void filterAndRemove (Map    map,
				 int    type)
                                 throws IllegalAccessException,
                                        InvocationTargetException,
                                        FilterException
    {

	if ((type != GeneralFilter.KEYS)
	    &&
	    (type != GeneralFilter.VALUES)
	   )
	{

	    type = GeneralFilter.VALUES;

	}

	Iterator iter = map.keySet ().iterator ();
	
	while (iter.hasNext ())
	{

	    Object key = iter.next ();

	    if (type == GeneralFilter.KEYS)
	    {

		if (!this.accept (key))
		{

		    iter.remove ();

		}

	    }

	    if (type == GeneralFilter.VALUES)
	    {

		if (!this.accept (map.get (key)))
		{

		    iter.remove ();

		}

	    }

	}

    }

    /**
     * Output the filter fields as a String suitable for debugging.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ();
	buf.append ("Class: ");
	buf.append (this.clazz.getName ());
	buf.append ('\n');
	buf.append ("  Fields (filter object class/type/field/value[/extras]:\n");

	for (int i = 0; i < this.fields.size (); i++)
	{

	    buf.append ("    ");
	    buf.append (this.fields.get (i).toString ());
	    buf.append ('\n');

	}

	return buf.toString ();

    }

    private class ObjectFilterField extends FilterField
    {

	private Object obj = null;
	private Comparator comp = null;
	private int type = GeneralFilter.EQUALS;

	private ObjectFilterField (String     field,
				   Object     obj,
				   Comparator comp,
				   int        type,
				   Class      c)
	                           throws     IllegalArgumentException
	{

	    super (field,
		   c);
	    
	    if ((type != GeneralFilter.EQUALS)
		&&
		(type != GeneralFilter.NOT_EQUALS)
		&&
		(type != GeneralFilter.LESS_THAN)
		&&
		(type != GeneralFilter.GREATER_THAN)
	       )
	    {

		throw new IllegalArgumentException (type + " is not supported for an Object comparison.");

	    }

	    this.obj = obj;
	    this.comp = comp;
	    this.type = type;

	}

	public String toString ()
	{

	    return Object.class.getName () + "/" + this.type + "/" + this.getField () + "/" + this.obj.toString () + "/" + this.comp.toString ();

	}

	protected boolean accept (Object o)
                                  throws IllegalAccessException,
	                                 InvocationTargetException,
	                                 FilterException
	{

	    Object v = this.getValue (o);

	    if (v == null)
	    {

		return getNullAcceptPolicy ();

	    }

	    int res = 0;

	    // See if we are using the comparator or the comparable interface...
	    if (this.comp != null)
	    {

		res = this.comp.compare (this.obj,
					 v);

	    } else {

		// Using the comparable interface...
		Comparable compObj = (Comparable) this.obj;

		// Do the compare...
		res = compObj.compareTo (o);

	    }

	    if (this.type == GeneralFilter.GREATER_THAN)
	    {
		
		if (res > 0)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.LESS_THAN)
	    {
		
		if (res < 0)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.EQUALS)
	    {
		
		if (res == 0)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.NOT_EQUALS)
	    {
		
		if (res != 0)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    return false;

	}

    }

    private class BooleanFilterField extends FilterField
    {

	private boolean value = false;
	private int type = GeneralFilter.EQUALS;

	private BooleanFilterField (String  field,
				    boolean value,
				    int     type,
				    Class   c)
                                    throws IllegalArgumentException
	{

	    super (field,
		   c);

	    if ((type != GeneralFilter.EQUALS)
		&&
		(type != GeneralFilter.NOT_EQUALS)
	       )
	    {

		throw new IllegalArgumentException (type + " is not supported for a Boolean comparison.");

	    }

	    this.value = value;
	    this.type = type;

	}

	public String toString ()
	{

	    return Boolean.class.getName () + "/" + this.type + "/" + this.getField () + "/" + this.value;

	}

	protected boolean accept (Object o)
                                  throws IllegalAccessException,
                                         InvocationTargetException,
	                                 FilterException
	{

	    Object v = this.getValue (o);

	    if (v == null)
	    {

		return getNullAcceptPolicy ();

	    }

	    // Get the type, if it is a java.lang.Boolean then grand...
	    if (!v.getClass ().isAssignableFrom (Boolean.class))
	    {

		throw new FilterException ("Type of value returned from getter: " + 
					   this.getter.getType ().getName () + 
					   " is NOT of type: " +
					   Boolean.class.getName ());

	    }

	    boolean b = ((Boolean) v).booleanValue ();

	    if (this.type == GeneralFilter.EQUALS)
	    {

		if (b == this.value)
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.NOT_EQUALS)
	    {

		if (b != this.value)
		{

		    return true;

		}

	    }

	    return false;

	}

    }

    private class DateFilterField extends FilterField
    {

	private Date max = null;
	private Date min = null;
	private Date value = null;

	private int type = GeneralFilter.EQUALS;

	private DateFilterField (String field,
				 Date   max,
				 Date   min,
				 Date   value,
				 int    type,
				 Class  c)
                                 throws IllegalArgumentException
	{

	    super (field,
		   c);

	    if ((type != GeneralFilter.EQUALS)
		&&
		(type != GeneralFilter.NOT_EQUALS)
		&&
		(type != GeneralFilter.IN_RANGE)
		&&
		(type != GeneralFilter.LESS_THAN)	
		&&
		(type != GeneralFilter.GREATER_THAN)	
	       )
	    {
		
		throw new IllegalArgumentException (type + " is not supported for a Date comparison.");

	    }

	    this.max = max;
	    this.min = min;
	    this.value = value;
	    this.type = type;

	}

	public String toString ()
	{

	    return Date.class.getName () + "/" + this.type + "/" + this.getField () + "/" + this.value + "/" + "max:" + this.max + "/" + "min:" + this.min;

	}

	protected boolean accept (Object o)
                                  throws IllegalAccessException,
	                                 InvocationTargetException,
	                                 FilterException
	{

	    Object v = this.getValue (o);

	    if (v == null)
	    {

		return getNullAcceptPolicy ();

	    }

	    // Get the type, if it is a java.util.Date then grand...
	    if (!v.getClass ().isAssignableFrom (Date.class))
	    {

		throw new FilterException ("Type of value returned from getter: " + 
					   this.getter.getType ().getClass () + 
					   " is NOT of type: " +
					   Date.class.getName ());

	    }
	    
	    Date d = (Date) v;

	    if (this.type == GeneralFilter.EQUALS)
	    {
		
		if (d.equals (this.value))
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.NOT_EQUALS)
	    {

		if (!d.equals (this.value))
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.IN_RANGE)
	    {
		
		if ((d.equals (this.max))
		    ||
		    (d.equals (this.min))
		   )
		{

		    return true;

		}

		if ((d.before (this.max))
		    &&
		    (d.after (this.min))
		   )
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.LESS_THAN)
	    {
		
		if (d.before (this.value))
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.GREATER_THAN)
	    {
		
		if (d.after (this.value))
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    return false;	    

	}

    }

    private class NumberFilterField extends FilterField
    {

	private double max = 0;
	private double min = 0;
	private double val = 0;

	private int type = GeneralFilter.EQUALS;

	private NumberFilterField (String field,
				   double max,
				   double min,
				   double value,
				   int    type,
				   Class  c)
                                   throws IllegalArgumentException
	{

	    super (field,
		   c);

	    if ((type != GeneralFilter.EQUALS)
		&&
		(type != GeneralFilter.NOT_EQUALS)
		&&
		(type != GeneralFilter.IN_RANGE)
		&&
		(type != GeneralFilter.LESS_THAN)	
		&&
		(type != GeneralFilter.GREATER_THAN)	
	       )
	    {

		throw new IllegalArgumentException (type + " is not supported for a Number comparison.");

	    }

	    this.max = max;
	    this.min = min;
	    this.val = value;
	    this.type = type;

	}

	public String toString ()
	{

	    return Number.class.getName () + "/" + this.type + "/" + this.getField () + "/" + this.val + "/" + "max:" + this.max + "/" + "min:" + this.min;

	}

	protected boolean accept (Object o)
                                  throws IllegalAccessException,
	                                 InvocationTargetException,
	                                 FilterException
	{

	    Object v = this.getValue (o);

	    if (v == null)
	    {

		return getNullAcceptPolicy ();

	    }

	    // Get the type, if it is a java.lang.Number then grand...
	    if (!v.getClass ().isAssignableFrom (Number.class))
	    {

		throw new FilterException ("Type of value returned from getter: " + 
					   this.getter.getType ().getName () + 
					   " is NOT of type: " +
					   Number.class.getName ());

	    }

	    // It is a number...
	    // Good now get it as a double.
	    double oVal = ((Number) v).doubleValue ();
	    
	    if (this.type == GeneralFilter.EQUALS)
	    {
		
		if (oVal == this.val)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.NOT_EQUALS)
	    {

		if (oVal != this.val)
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.IN_RANGE)
	    {
		
		if ((oVal <= this.max)
		    &&
		    (oVal >= this.min)
		   )
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.LESS_THAN)
	    {
		
		if (oVal < this.val)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    if (this.type == GeneralFilter.GREATER_THAN)
	    {
		
		if (oVal > this.val)
		{
		    
		    return true;
		    
		}
		
	    }
	    
	    return false;

	}

    }

    private class StringFilterField extends FilterField
    {

	private String val = "";
	private int type = GeneralFilter.EQUALS;

	private StringFilterField (String field,
				   String value,
				   int    type,
				   Class  c)
                                   throws IllegalArgumentException
	{

	    super (field,
		   c);

	    if ((type != GeneralFilter.EQUALS)
		&&
		(type != GeneralFilter.NOT_EQUALS)
		&&
		(type != GeneralFilter.CONTAINS)
		&&
		(type != GeneralFilter.NOT_CONTAINS)
		&&
		(type != GeneralFilter.STARTS_WITH)
		&&
		(type != GeneralFilter.ENDS_WITH)
	       )
	    {

		throw new IllegalArgumentException (type + " is not supported for a String comparison.");

	    }

	    this.type = type;
	    this.val = value;

	}

	public String toString ()
	{

	    return String.class.getName () + "/" + this.type + "/" + this.getField () + "/" + this.val;

	}

	protected boolean accept (Object o)
                                  throws IllegalAccessException,
	                                 InvocationTargetException,
	                                 FilterException
	{

	    Object ro = this.getValue (o);

	    if (ro == null)
	    {

		return getNullAcceptPolicy ();

	    }

	    String v = ro.toString ();

	    if (this.type == GeneralFilter.EQUALS)
	    {

		if (v.equals (this.val))
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.NOT_EQUALS)
	    {

		if (!v.equals (this.val))
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.NOT_CONTAINS)
            {

               if (v.indexOf (this.val) == -1)
               {

                  return true;

               }

            }

	    if (this.type == GeneralFilter.CONTAINS)
	    {

		if (v.indexOf (this.val) != -1)
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.ENDS_WITH)
	    {

		if (v.endsWith (this.val))
		{

		    return true;

		}

	    }

	    if (this.type == GeneralFilter.STARTS_WITH)
	    {

		if (v.startsWith (this.val))
		{

		    return true;

		}

	    }

	    return false;

	}

    }

    private abstract class FilterField 
    {

	protected Getter getter = null;
	private Class c = null;
	private String field = null;

	private FilterField (String field,
			     Class  c)
                             throws IllegalArgumentException
	{

	    this.field = field;
	    this.c = c;
	    this.getter = new Getter (field,
				      c);

	}

	protected Object getValue (Object o)
	                           throws IllegalAccessException,
					  InvocationTargetException
	{

	    return this.getter.getValue (o);

	}

	protected String getField ()
	{

	    return this.field;

	}

	protected abstract boolean accept (Object o)
                                           throws IllegalAccessException,
	                                          InvocationTargetException,
	                                          FilterException;

    }

}
