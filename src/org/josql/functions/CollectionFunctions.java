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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;

import com.gentlyweb.utils.GeneralComparator;

import org.josql.Query;
import org.josql.QueryExecutionException;

import org.josql.expressions.Expression;

import org.josql.internal.Utilities;

/**
 * Defines a set of functions that operate on "collections" of objects in some way.
 */
public class CollectionFunctions extends AbstractFunctionHandler
{

    private Map foreachQueryCache = null;

    /**
     * The id that can be used to get the "CollectionFunctions" handler object from
     * the Query object.
     */
    public static final String HANDLER_ID = "_internal_collection";

    /**
     * Sort a list according to it's "natural" ordering (see {@link Collections#sort(List)}).
     *
     * @param objs The list of objects to sort.
     * @return The sorted list, according to their natural ordering.
     */
    public List sort (List objs)
    {

	Collections.sort (objs);

	return objs;

    }

    /**
     * Sort a Map by the keys in ascending order (for more optionality in the sort and ordering
     * see: {@link #sort(Map,String,String)}).
     *
     * @param m The map to sort.
     * @return A List sorted according to the key in ascending order.
     */
    public List sort (Map m)
    {

	return this.sort (m,
			  "key",
			  GeneralComparator.ASC);

    }

    /**
     * Sort a Map by it's keys or values in ascending order (for more optionality in the sort and ordering
     * see: {@link #sort(Map,String,String)}).
     *
     * @param m The map to sort.
     * @param type Should be either: "key" or "value" to indicate which item to sort on.
     *             Use <code>null</code> for key.
     * @return A List sorted according to the key in ascending order.
     */
    public List sort (Map    m,
		      String type)
    {

	return this.sort (m,
			  type,
			  GeneralComparator.ASC);

    }

    /**
     * Sort a Map by either it's key or value.
     *
     * @param m The map to sort.
     * @param type Should be either: "key" or "value" to indicate which item to sort on.
     *             Use <code>null</code> for key.
     * @param dir The direction you want to sort on, either "asc" or "desc".  Use <code>null</code>
     *            for "asc".
     * @return A List sorted according to the key or value.
     */
    public List sort (Map    m,
		      String type,
		      String dir)
    {

	String acc = "key";

	if ((type != null)
	    &&
	    (type.equalsIgnoreCase ("value"))
	   )
	{

	    acc = "value";

	}

	String d = GeneralComparator.ASC;

	if (dir != null)
	{

	    dir = dir.toUpperCase ();

	    if (dir.equals (GeneralComparator.DESC))
	    {

		d = GeneralComparator.DESC;

	    }

	}

	GeneralComparator gc = new GeneralComparator (Map.Entry.class);

	gc.addField (acc,
		     d);

	List l = new ArrayList (m.entrySet ());

	Collections.sort (l,
			  gc);

	return l;

    }

    /**
     * Get a value from the specified Map.
     *
     * @param m The map of objects.
     * @param exp The expression is evaluated (in the context of the current object) and the 
     *            value returned used as the key to the Map, the value it maps to 
     *            (which may be null) is returned.
     * @return The value that the <b>exp</b> value maps to, may be null.
     */
    public Object get (Map        m,
		       Expression exp)
	               throws     QueryExecutionException
    {

	// Evaluate the expression.
	// Get the current object.
	return m.get (exp.getValue (this.q.getCurrentObject (),
				    this.q));

    }

    /**
     * Get a value from the specified List.
     *
     * @param l The list of objects.
     * @param n The index, indices start at 0.
     * @return The value of the <b>i</b>th element from the list of objects.  Return <code>null</code>
     *         if <b>n</b> is out of range.
     */
    public Object get (List   l,
		       Number n)
    {

	int i = n.intValue ();

	if ((i > l.size ())
	    ||
	    (i < 0)
	   )
	{

	    return null;

	}

	return l.get (i);

    }

    /**
     * For each of the objects in the <b>objs</b> List get the value from each one
     * using the <b>accessor</b> and compare it to the <b>value</b> parameter.  The value
     * param is converted to a string and then to a Boolean value using: {@link Boolean#valueOf(String)}.
     *
     * @param objs The list of objects to iterate over.
     * @param exp The expression to use to get the value from the object in the List.
     * @param value The value to compare the result of the accessor against.  If the parm is <code>null</code>
     *              then it defaults to {@link Boolean#FALSE}.
     * @return A count of how many times the accessor evaluated to the same value of the 
     *         <b>value</b> parm.
     * @throws QueryExecutionException If the value from the accessor cannot be gained or if
     *                                 the compare cannot be performed.
     */
    public int count (List       objs,
		      Expression exp,
		      Object     value)
	              throws     QueryExecutionException
    {

	Boolean b = Boolean.FALSE;

	if (value != null)
	{

	    b = Boolean.valueOf (value.toString ());

	}

	int count = 0;

	Object currobj = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();
        Comparator uc = this.q.getObjectComparator ();
        
        this.q.setAllObjects (objs);
        
	int size = objs.size ();

	for (int i = 0; i < size; i++)
	{

	    Object o = objs.get (i);

            this.q.setCurrentObject (o);

	    try
	    {

                int c = 0;
                
                if (uc != null)
                {
                    
                    c = uc.compare (exp.getValue (o,
                                                  this.q),
                                    b);
                    
                } else {

                    c = Utilities.compare (exp.getValue (o,
                                                         this.q),
                                           b);
                    
                }
                
		if (c == 0)
		{

		    count++;

		}

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
        
		throw new QueryExecutionException ("Unable to get value from expression: " +
						   exp + 
						   " for item: " + 
						   i +
						   " from the list of objects.",
						   e);

	    }

	}	

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);
        
	return count;

    }

    public int count (Expression exp)
	              throws     QueryExecutionException
    {

	return this.count ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			   exp);

    }

    public int count (List       allobjs,
		      Expression exp)
	              throws     QueryExecutionException
    {

	int count = 0;

	Object currobj = this.q.getCurrentObject ();
	List currall = this.q.getAllObjects ();
        
        this.q.setAllObjects (allobjs);
        
	int size = allobjs.size ();

	for (int i = 0; i < size; i++)
	{

	    Object o = allobjs.get (i);

            this.q.setCurrentObject (o);

	    try
	    {

		if (exp.isTrue (o,
				this.q))
		{

		    count++;

		}

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (currall);

		throw new QueryExecutionException ("Unable to determine whether expression: \"" +
						   exp + 
						   "\" is true for object at index: " + 
						   i +
						   " from the list of objects.",
						   e);

	    }

	}	

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (currall);
        
	return count;

    }

    public List toList (List       allobjs,
			Expression exp,
			String     saveValueName)
                        throws     QueryExecutionException
    {

	return this.collect (allobjs,
			     exp,
			     saveValueName);

    }

    public List unique (List objs)
    {

	/**
	   Strangely the method below is consistently slower than the method employed!
	return new ArrayList (new java.util.LinkedHashSet (objs));
	*/

	Map m = new LinkedHashMap ();

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    m.put (objs.get (i),
		   null);

	}

	return new ArrayList (m.keySet ());

    }

    public List unique (Expression exp)
	                throws     QueryExecutionException
    {

	return this.unique ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			    exp);

    }    

    public List unique (List       objs,
			Expression exp)
	                throws     QueryExecutionException
    {

	/**
	   Strangely the method below is consistently slower than the method employed!
	return new ArrayList (new java.util.LinkedHashSet (objs));
	*/

	Map m = new HashMap ();

	Object currobj = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();
        
        this.q.setAllObjects (objs);
        
	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

            this.q.setCurrentObject (o);

	    o = exp.getValue (o,
			      this.q);

	    m.put (o,
		   null);

	}

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);
        
	return new ArrayList (m.keySet ());

    }

    public List collect (List       objs,
			 Expression exp,
			 String     saveValueName)
                         throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return (List) o;

	    }

	}

	List retVals = new ArrayList ();

	int s = objs.size ();

	List allobjs = this.q.getAllObjects ();
	Object co = this.q.getCurrentObject ();

        this.q.setAllObjects (objs);

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    // Execute the function.
	    try
	    {

		retVals.add (exp.getValue (o,
					   this.q));

	    } catch (Exception e) {

                // Reset the current object.
                this.q.setCurrentObject (co);
                this.q.setAllObjects (allobjs);
        
		throw new QueryExecutionException ("Unable to execute expression: \"" +
						   exp +
						   " on object at index: " +
						   i +
						   " from the list of objects.",
						   e);

	    }

	}

	if (saveValueName != null)
	{

	    this.q.setSaveValue (saveValueName,
				 retVals);

	}

	// Reset the current object.
	this.q.setCurrentObject (co);
        this.q.setAllObjects (allobjs);

	return retVals;

    }

    public List collect (Expression exp)
	                 throws     QueryExecutionException
    {

	return this.collect ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			    exp);

    }    

    public List collect (List       allobjs,
			 Expression exp)
                         throws     QueryExecutionException
    {

	return this.collect (allobjs,
			     exp,
			     null);

    }

    public List toList (Expression exp)
	                throws     QueryExecutionException
    {

	return this.toList ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			    exp);

    }    

    public List toList (List       allobjs,
			Expression exp)
                        throws     QueryExecutionException
    {

	return this.collect (allobjs,
			     exp,
			     null);

    }

    public List foreach (Expression exp)
	                 throws     QueryExecutionException
    {

	return this.foreach ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			     exp);

    }    

    public List foreach (List       allobjs,
			 Expression exp)
	                 throws     QueryExecutionException
    {

	if (allobjs == null)
	{

	    return null;

	}

	Object currobj = this.q.getCurrentObject ();

        this.q.setAllObjects (allobjs);
        
	List res = new ArrayList ();

	int s = allobjs.size (); 

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    res.add (exp.getValue (o,
				   this.q));

	}

	// Reset the current object.
	this.q.setCurrentObject (currobj);
        this.q.setAllObjects (allobjs);
        
	return res;

    }

    /**
     * Given a list of objects, execute the expression against each one and return
     * those objects that return a <code>true</code> value for the expression.
     * In effect this is equivalent to executing the WHERE clause of a JoSQL statement
     * against each object (which in fact is what happens internally).  The class
     * for the objects if found by examining the list passed in.
     *
     * @param objs The list of objects.
     * @param exp The expression (basically a where clause, it is ok for the expression
     *            to start with "WHERE", case-insensitive) to execute for each of the
     *            objects.
     * @return The list of matching objects.
     */
    public List foreach (List   objs,
			 String exp)
	                 throws QueryExecutionException
    {

	List l = new ArrayList ();

	if ((objs == null)
	    ||
	    (objs.size () == 0)
	   )
	{

	    return l;

	}

	Query q = null;

	// See if we have the expression in our cache.
	if (this.foreachQueryCache != null)
	{

	    q = (Query) this.foreachQueryCache.get (exp);

	}

	if (q == null)
	{

	    // Init our query.
	    Class c = null;

	    Object o = objs.get (0);

	    if (o == null)
	    {

		int s = objs.size () - 1;

		// Bugger now need to cycle until we get a class.
		for (int i = s; s > -1; i--)
		{

		    o = objs.get (i);

		    if (o != null)
		    {

			c = o.getClass ();
			break;

		    }

		}

	    } else {
		    
		c = o.getClass ();
		    
	    }

	    if (exp.toLowerCase ().trim ().startsWith ("where"))
	    {

		exp = exp.trim ().substring (5);

	    }

	    String query = "SELECT * FROM " + c.getName () + " WHERE " + exp;

	    q = new Query ();

	    try
	    {

		q.parse (query);

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to create statement using WHERE clause: " +
						   exp +
						   " and class: " +
						   c.getName () + 
						   " (gained from objects in list passed in)",
						   e);

	    }

	    // Cache it.
	    if (this.foreachQueryCache == null)
	    {

		this.foreachQueryCache = new HashMap ();

	    }

	    this.foreachQueryCache.put (exp,
					q);

	}

	return q.execute (objs).getResults ();

    }

    public List foreach (Expression listFunction,
			 Expression exp)
	                 throws     QueryExecutionException
    {

	// Execute the list function.
	Object o = listFunction.getValue (this.q.getCurrentObject (),
					  this.q);

	if (!(o instanceof List))
	{

	    throw new QueryExecutionException ("Expected expression: " + 
					       listFunction + 
					       " to return instance of: " +
					       List.class.getName () + 
					       " but returned instance of: " +
					       o.getClass ().getName ());

	}

	List l = (List) o;

	return this.foreach (l,
			     exp);

    }    

    /**
     * Find objects from the List based upon the expression passed in.  If
     * the expression evaluates to <code>true</code> then the object will
     * be returned.
     * Note: in accordance with the general operating methodology for the Query
     *       object, the ":_allobjs" special bind variable will be set to the 
     *       the List passed in and the "_currobj" will be set to the relevant
     *       object in the List.
     *
     * @param objs The List of objects to search.
     * @param exp The expression to evaulate against each object in the List.
     * @return The List of matching objects, if none match then an empty list is returned.
     * @throws QueryExecutionException If the expression cannot be evaulated against each
     *                                 object.
     */
    public List find (List       objs,
		      Expression exp)
	              throws     QueryExecutionException
    {

	// Get the current object, it's important that we leave the Query in the
	// same state at the end of this function as when we started!
	Object currobj = this.q.getCurrentObject ();
        List allobjs = this.q.getAllObjects ();

	this.q.setAllObjects (objs);

	List r = new ArrayList ();

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    try
	    {

		if (exp.isTrue (o,
				this.q))
		{

		    r.add (o);

		}

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
        
		throw new QueryExecutionException ("Unable to evaulate expression: " + 
						   exp + 
						   " against object: " +
						   i + 
						   " (class: " + 
						   o.getClass ().getName () + 
						   ")",
						   e);

	    }

	}

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);

	return r;

    }

    /**
     * Group objects from the List based upon the expression passed in.  The expression
     * is evaulated for each object, by calling: {@link Expression#getValue(Object,Query)}
     * and the return value used as the key to the Map.  All objects with that value are
     * added to a List held against the key.  To maintain the ordering of the keys (if
     * desirable) a {@link LinkedHashMap} is used as the return Map.
     *
     * Note: in accordance with the general operating methodology for the Query
     *       object, the ":_allobjs" special bind variable will be set to the 
     *       the List passed in and the "_currobj" will be set to the relevant
     *       object in the List.
     *
     * @param objs The List of objects to search.
     * @param exp The expression to evaulate against each object in the List.
     * @return The LinkedHashMap of matching objects, grouped according to the return value
     *         of executing the expression against each object in the input List.
     * @throws QueryExecutionException If the expression cannot be evaulated against each
     *                                 object.
     */
    public Map grp (List       objs,
		    Expression exp)
	            throws     QueryExecutionException
    {
    
	// Get the current object, it's important that we leave the Query in the
	// same state at the end of this function as when we started!
	Object currobj = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();

	this.q.setAllObjects (objs);

	Map r = new LinkedHashMap ();

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    try
	    {

		Object v = exp.getValue (o,
					 this.q);

		List vs = (List) r.get (v);

		if (vs == null)
		{

		    vs = new ArrayList ();

		    r.put (v,
			   vs);

		}

		vs.add (v);

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
                
		throw new QueryExecutionException ("Unable to evaulate expression: " + 
						   exp + 
						   " against object: " +
						   i + 
						   " (class: " + 
						   o.getClass ().getName () + 
						   ")",
						   e);

	    }

	}

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);

	return r;	

    }

    /**
     * Create a map of the objects passed in, the key will be the object in the list and
     * the value will be the result of calling the expression on the object.
     *
     * The expression is evaulated for each object, by calling: {@link Expression#getValue(Object,Query)}
     * and the return value used as the value to the Map.  To maintain the ordering of the keys (if
     * desirable) a {@link LinkedHashMap} is used as the return Map.
     *
     * Note: in accordance with the general operating methodology for the Query
     *       object, the ":_allobjs" special bind variable will be set to the 
     *       the List passed in and the "_currobj" will be set to the relevant
     *       object in the List.
     *
     * @param objs The List of objects to map.
     * @param exp The expression to evaulate against each object in the List.
     * @return The LinkedHashMap of mapped objects.
     * @throws QueryExecutionException If the expression cannot be evaulated against each
     *                                 object.
     */
    public Map map (List       objs,
                    Expression exp)
                    throws     QueryExecutionException
    {
        
	// Get the current object, it's important that we leave the Query in the
	// same state at the end of this function as when we started!
	Object currobj = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();

	this.q.setAllObjects (objs);

	Map r = new LinkedHashMap ();

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    try
	    {

		Object v = exp.getValue (o,
					 this.q);

                r.put (o,
                       v);

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
                
		throw new QueryExecutionException ("Unable to evaulate expression: " + 
						   exp + 
						   " against object: " +
						   i + 
						   " (class: " + 
						   o.getClass ().getName () + 
						   ")",
						   e);

	    }

	}

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);

	return r;	        
        
    }

    /**
     * Create a map of the objects passed in, the key will be the result of calling the <b>keyExp</b> expression
     * on the object in the list and the value will be the result of calling the <b>valExp</b> expression on the object.
     *
     * The expression is evaulated for each object, by calling: {@link Expression#getValue(Object,Query)}
     * and the return value used as the value to the Map.  To maintain the ordering of the keys (if
     * desirable) a {@link LinkedHashMap} is used as the return Map.
     *
     * Note: in accordance with the general operating methodology for the Query
     *       object, the ":_allobjs" special bind variable will be set to the 
     *       the List passed in and the "_currobj" will be set to the relevant
     *       object in the List.
     *
     * @param objs The List of objects to map.
     * @param exp The expression to evaulate against each object in the List.
     * @return The LinkedHashMap of mapped objects.
     * @throws QueryExecutionException If the expression cannot be evaulated against each
     *                                 object.
     */
    public Map map (List       objs,
                    Expression keyExp,
                    Expression valExp)
                    throws     QueryExecutionException
    {
        
	// Get the current object, it's important that we leave the Query in the
	// same state at the end of this function as when we started!
	Object currobj = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();

	this.q.setAllObjects (objs);

	Map r = new LinkedHashMap ();

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

            Object k = null;
            Object v = null;

	    try
	    {

		k = keyExp.getValue (o,
				     this.q);

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
                
		throw new QueryExecutionException ("Unable to evaulate key expression: " + 
						   keyExp + 
						   " against object: " +
						   i + 
						   " (class: " + 
						   o.getClass ().getName () + 
						   ")",
						   e);

	    }

	    try
	    {

		v = valExp.getValue (o,
				     this.q);

	    } catch (Exception e) {

                // Restore the currobj and allobjs.
                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);
                
		throw new QueryExecutionException ("Unable to evaulate value expression: " + 
						   valExp + 
						   " against object: " +
						   i + 
						   " (class: " + 
						   o.getClass ().getName () + 
						   ")",
						   e);

	    }

            r.put (k,
                   v);
            
	}

	// Restore the currobj and allobjs.
	this.q.setCurrentObject (currobj);
	this.q.setAllObjects (allobjs);

	return r;	        
        
    }

}
