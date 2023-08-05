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
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;

import com.gentlyweb.utils.Getter;

import org.josql.QueryExecutionException;

import org.josql.internal.Utilities;

import org.josql.expressions.Expression;

import org.josql.Query;

public class GroupingFunctions extends AbstractFunctionHandler
{

    public static final String VALUE = "value";

    public static final String HANDLER_ID = "_internal_grouping";

    public Object least (List       allobjs,
			 Expression exp,
			 String     saveValueName)
                         throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return o;

	    }

	}

	if (allobjs.size () == 0)
	{

	    return null;

	}

	Object currObj = this.q.getCurrentObject ();
        Comparator uc = this.q.getObjectComparator ();

	Object g = null;

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);

		throw new QueryExecutionException ("Unable to get value from expression: " + 
						   exp +
						   " for maximum value" +
						   e);

	    }	    

	    if (g == null)
	    {

		g = v;

	    } else {

                int c = 0;
                                
                if (uc != null)
                {

                    c = uc.compare (v,
                                    g);
                    
                } else {

                    c = Utilities.compare (v,
					   g);

                }

		if (c < 0)
		{

		    g = v;

		}

	    }

	}

	if ((saveValueName != null)
	    &&
	    (q != null)
	   )
	{

	    q.setSaveValue (saveValueName,
			    g);

	}

	this.q.setCurrentObject (currObj);

	return g;

    }

    public Object minObject (Expression exp)
	                     throws     QueryExecutionException
    {

	return this.minObject ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			       exp);

    }    

    public Object minObject (List       allobjs,
			     Expression exp)
                             throws     QueryExecutionException
    {

	return this.leastObject (allobjs,
				 exp);

    }

    public Object leastObject (List       allobjs,
			       Expression exp)
                               throws     QueryExecutionException
    {

	if (allobjs.size () == 0)
	{

	    return null;

	}

	Object currObj = this.q.getCurrentObject ();

	Object l = null;
	Object lo = null;

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);

		throw new QueryExecutionException ("Unable to get value from expression: " + 
						   exp +
						   " for maximum value" +
						   e);

	    }	    

	    if (l == null)
	    {

		l = v;
		lo = o;

	    } else {

                int c = 0;
                
                Comparator uc = this.q.getObjectComparator ();
                
                if (uc != null)
                {

                    c = uc.compare (v,
                                    l);
                    
                } else {

                    c = Utilities.compare (v,
					   l);

                }

		if (c < 0)
		{

		    l = v;
		    lo = o;

		}

	    }

	}

	this.q.setCurrentObject (currObj);

	return lo;

    }

    public Object maxObject (List       allobjs,
			     Expression exp)
                             throws     QueryExecutionException
    {

	return this.greatestObject (allobjs,
				    exp);

    }

    public Object maxObject (Expression exp)
	                     throws     QueryExecutionException
    {

	return this.maxObject ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			       exp);

    }    

    public Object greatestObject (List       allobjs,
				  Expression exp)
                                  throws     QueryExecutionException
    {

	if (allobjs.size () == 0)
	{

	    return null;

	}

	Object currObj = this.q.getCurrentObject ();

	Object g = null;
	Object go = null;

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

            this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);

		throw new QueryExecutionException ("Unable to get value from expression: " + 
						   exp +
						   " for maximum value" +
						   e);

	    }	    

	    if (g == null)
	    {

		g = v;
		go = o;

	    } else {

                int c = 0;
                
                Comparator uc = this.q.getObjectComparator ();
                
                if (uc != null)
                {

                    c = uc.compare (v,
                                    g);
                    
                } else {

                    c = Utilities.compare (v,
					   g);

                }

		if (c > 0)
		{

		    g = v;
		    go = o;

		}

	    }

	}

	this.q.setCurrentObject (currObj);

	return go;

    }

    public Object least (List       allobjs,
			 Expression exp)
                         throws     QueryExecutionException
    {

	return this.least (allobjs,
			   exp,
			   null);

    }

    public Object min (Expression exp)
	               throws     QueryExecutionException
    {

	return this.min ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			 exp);

    }    

    public Object min (List       allobjs,
		       Expression exp)
                       throws     QueryExecutionException
    {

	return this.least (allobjs,
			   exp,
			   null);

    }

    public Object min (List       allobjs,
		       Expression exp,
		       String     saveValueName)
                       throws     QueryExecutionException
    {

	return this.least (allobjs,
			   exp,
			   saveValueName);

    }

    public Map.Entry maxEntry (Map    m,
			       String type)
    {

	int t = 0;

	if (type.equals (GroupingFunctions.VALUE))
	{

	    t = 1;

	}

	Map.Entry r = null;
	Map.Entry le = null;

	Iterator iter = m.entrySet ().iterator ();

	while (iter.hasNext ())
	{

	    r = (Map.Entry) iter.next ();

	    if (le != null)
	    {

		if (t == 0)
		{

		    if (Utilities.isGTEquals (r.getKey (),
					      le.getKey ()))
		    {

			le = r;

		    }

		} else {

		    if (Utilities.isGTEquals (r.getValue (),
					      le.getValue ()))
		    {

			le = r;

		    }

		}

	    } else {

		le = r;

	    }

	}

	return le;

    }

    public Map.Entry minEntry (Object m,
			       String type)
	                       throws QueryExecutionException
    {

	if (!(m instanceof Map))
	{

	    throw new QueryExecutionException ("Only instances of: " +
					       Map.class.getName () + 
					       " are supported, passed: " +
					       m.getClass ().getName ());

	}

	return this.minEntry ((Map) m,
			      type);

    }

    public Map.Entry minEntry (Map    m,
			       String type)
    {

	int t = 0;

	if (type.equals (GroupingFunctions.VALUE))
	{

	    t = 1;

	}

	Map.Entry r = null;
	Map.Entry le = null;

	Iterator iter = m.entrySet ().iterator ();

	while (iter.hasNext ())
	{

	    r = (Map.Entry) iter.next ();

	    if (le != null)
	    {

		if (t == 0)
		{

		    if (Utilities.isLTEquals (r.getKey (),
					      le.getKey ()))
		    {

			le = r;

		    }

		} else {

		    if (Utilities.isLTEquals (r.getValue (),
					      le.getValue ()))
		    {

			le = r;

		    }

		}

	    } else {

		le = r;

	    }

	}

	return le;

    }

    public Object max (List       allobjs,
		       Expression exp,
		       String     saveValueName)
                       throws     QueryExecutionException
    {

	return this.greatest (allobjs,
			      exp,
			      saveValueName);

    }

    public Object greatest (List       allobjs,
			    Expression exp,
			    String     saveValueName)
                            throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return (Double) o;

	    }

	}

	if (allobjs.size () == 0)
	{

	    return null;

	}

	Object currObj = this.q.getCurrentObject ();

	Object g = null;

	int s = allobjs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);

		throw new QueryExecutionException ("Unable to get value from expression: " + 
						   exp +
						   " for maximum value" +
						   e);

	    }	    

	    if (g == null)
	    {

		g = v;

	    } else {

                int c = 0;
                
                Comparator uc = this.q.getObjectComparator ();
                
                if (uc != null)
                {

                    c = uc.compare (v,
                                    g);
                    
                } else {

                    c = Utilities.compare (v,
					   g);

                }

		if (c > 0)
		{

		    g = v;

		}

	    }

	}

	if (saveValueName != null)
	{

	    this.q.setSaveValue (saveValueName,
				 g);

	}

	this.q.setCurrentObject (currObj);

	return g;

    }

    public Object greatest (List       allobjs,
			    Expression exp)
                            throws     QueryExecutionException
    {

	return this.greatest (allobjs,
			      exp,
			      null);

    }

    public Object max (Expression exp)
	               throws     QueryExecutionException
    {

	return this.max ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			 exp);

    }    

    public Object max (List       allobjs,
		       Expression exp)
                       throws     QueryExecutionException
    {

	return this.greatest (allobjs,
 			      exp,
			      null);

    }

    private double getTotal (List       allobjs,
			     Expression exp)
	                     throws     QueryExecutionException
    {

	Object currObj = this.q.getCurrentObject ();

	double total = 0;

	int size = allobjs.size ();

	for (int i = 0; i < size; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    Number n = null;

	    try
	    {

		n = (Number) exp.getValue (o,
					   this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);

		throw new QueryExecutionException ("Unable to get value from expression: " +
						   exp + 
						   " for item: " + 
						   i +
						   " from the list of objects.",
						   e);

	    }

	    total += n.doubleValue ();

	}

	this.q.setCurrentObject (currObj);

	return total;

    }

    public void checkType (Object     o,
			   Class      expected,
			   Expression exp)
                           throws   QueryExecutionException
    {

	if (!expected.isInstance (o))
	{

	    throw new QueryExecutionException ("Expression: " + 
					       exp +
					       " returns type: " +
					       o.getClass ().getName () + 
					       " however must return instance of: " +
					       expected.getName ());

	}

    }

    public Double sum (List       allobjs,
                       Expression exp,
                       String     saveValueName)
                       throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return (Double) o;

	    }

	}

	if ((allobjs == null)
	    ||
	    (allobjs.size () == 0)
	   )
	{

	    return new Double (0);

	}

	double total = this.getTotal (allobjs,
				      exp);

	Double d = new Double (total);

	if ((saveValueName != null)
	    &&
	    (q != null)
	   )
	{

	    this.q.setSaveValue (saveValueName,
				 d);

	}

	return d;

    }

    public Double sum (Expression exp)
	               throws     QueryExecutionException
    {

	return this.sum ((List) this.q.getAllObjects (),
			 exp);

    }    

    public Double sum (List       objs,
		       Expression exp)
	               throws     QueryExecutionException
    {

	Class c = null;

	try
	{

	    c = exp.getExpectedReturnType (this.q);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to determine expected return type for expression: " +
					       exp,
					       e);

	}

	boolean dyn = false;

	if (!c.getName ().equals (Object.class.getName ()))
	{

	    // Should return a number...
	    if (!Utilities.isNumber (c))
	    {

		throw new QueryExecutionException ("This function expects the expression: " +
						   exp +
						   " to return a number (sub-class of: " +
						   Number.class.getName () + 
						   ") but evaluation of the expression will return an instance of: " +
						   c.getName ());

	    }

	} else {

	    dyn = true;

	}

	Object co = this.q.getCurrentObject ();
	List allobjs = this.q.getAllObjects ();

        this.q.setAllObjects (objs);

	int s = objs.size () - 1;

	double d = 0;

	for (int i = s; i > -1; i--)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (co);
                this.q.setAllObjects (allobjs);

		throw new QueryExecutionException ("Unable to evaluate expression: " + 
						   exp + 
						   " on item: " + 
						   i,
						   e);

	    }

	    if (v == null)
	    {

		// Skip... i.e. assume it's zero.
		continue;

	    }

	    if (dyn)
	    {

		if (!(Utilities.isNumber (v)))
		{

                    this.q.setCurrentObject (co);
                    this.q.setAllObjects (allobjs);

		    throw new QueryExecutionException ("Expected expression: " +
						       exp +
						       " to return a number (sub-class of: " +
						       Number.class.getName () + 
						       ") but returns instance of: " +
						       o.getClass ().getName () + 
						       " for item: " +
						       i + 
						       " (class: " +
						       v.getClass ().getName () +
						       ")");

		}

	    }

	    d += ((Number) v).doubleValue ();

	}

	this.q.setCurrentObject (co);
        this.q.setAllObjects (allobjs);

	return new Double (d);

    }

    /**
     * This function allows you to specify your own accessor as a string that will
     * be used to access the relevant value for each of the objects in the <b>objs</b>
     * List.  
     *
     * @param objs The List of objects you wish to sum over.
     * @param acc The accessor to create for accessing the value in each of the objects in <b>objs</b>.
     * @return The summed value.
     * @throws QueryExecutionException If the accessor is not valid for the objects in the list or
     *                                 if the accessor throws an exception.
     */
    public Double sum (List   objs,
		       String acc)
	               throws QueryExecutionException
    {

	if ((objs == null)
	    ||
	    (objs.size () == 0)
	   )
	{

	    return new Double (0);

	}

	// Get the first object.
	Object o = objs.get (0);

	Getter get = null;

	try
	{

	    get = new Getter (acc,
			      o.getClass ());

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to create accessor for: " +
					       acc +
					       " with class: " +
					       o.getClass ().getName (),
					       e);

	}

	if (!get.getType ().getName ().equals (Object.class.getName ()))
	{

	    // Should return a number...
	    if (!Utilities.isNumber (get.getType ()))
	    {

		throw new QueryExecutionException ("This function expects the accessor (second parm): " +
						   acc +
						   " to return a number (sub-class of: " +
						   Number.class.getName () + 
						   ") but evaluation of the accessor will return an instance of: " +
						   get.getType ().getName ());

	    }

	}

	int s = objs.size () - 1;

        Object currobj = this.q.getCurrentObject ();
        List allobjs = this.q.getAllObjects ();

        this.q.setAllObjects (objs);

	double d = 0;

	for (int i = s; i > -1; i--)
	{

	    o = objs.get (i);

            this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = get.getValue (o);

	    } catch (Exception e) {

                this.q.setCurrentObject (currobj);
                this.q.setAllObjects (allobjs);

		throw new QueryExecutionException ("Unable to evaluate accessor: " + 
						   acc + 
						   " on item: " + 
						   i,
						   e);

	    }

	    if (v == null)
	    {

		// Skip... i.e. assume it's zero.
		continue;

	    }

	    d += ((Number) v).doubleValue ();

	}

        this.q.setCurrentObject (currobj);
        this.q.setAllObjects (allobjs);

	return new Double (d);

    }

    public String concat (List     allobjs,
			  Expression exp,
			  String     sep,
			  String     saveValueName)
                          throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return (String) o;

	    }

	}

	StringBuffer buf = new StringBuffer ();

	int size = allobjs.size ();
	int size1 = size - 1;

	Object currObj = this.q.getCurrentObject ();
        List currall = this.q.getAllObjects ();
        
        this.q.setAllObjects (allobjs);

	for (int i = 0; i < size; i++)
	{

	    Object o = allobjs.get (i);

	    this.q.setCurrentObject (o);

	    Object v = null;

	    try
	    {

		v = exp.getValue (o,
				  this.q);

	    } catch (Exception e) {

                this.q.setCurrentObject (currObj);
                this.q.setAllObjects (currall);

		throw new QueryExecutionException ("Unable to get value from expression: " +
						   exp + 
						   " for item: " + 
						   i +
						   " from the list of objects.",
						   e);

	    }

	    buf.append (v);

	    if ((sep != null)
		&&
		(i < size1)
	       )
	    {

		buf.append (sep);

	    }

	}

	String r = buf.toString ();

	if ((saveValueName != null)
	    &&
	    (q != null)
	   )
	{

	    q.setSaveValue (saveValueName,
			    r);

	}

	this.q.setCurrentObject (currObj);
        this.q.setAllObjects (currall);

	return r;

    }

    public String concat (List       allobjs,
                          Expression exp,
			  String     sep)
                          throws     QueryExecutionException
    {

	return this.concat (allobjs,
			    exp,
			    sep,
			    null);

    }

    public String concat (Expression exp)
	                  throws     QueryExecutionException
    {

	return this.concat ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			    exp);

    }    

    public String concat (List       allobjs,
                          Expression exp)
                          throws     QueryExecutionException
    {

	return this.concat (allobjs,
			    exp,
			    null,
			    null);

    }

    public Double avg (List       allobjs,
                       Expression exp,
                       String     saveValueName)
                       throws     QueryExecutionException
    {

	if (saveValueName != null)
	{

	    Object o = this.q.getSaveValue (saveValueName);

	    if (o != null)
	    {

		return (Double) o;

	    }

	}

	if ((allobjs == null)
	    ||
	    (allobjs.size () == 0)
	   )
	{

	    return new Double (0);

	}

	double total = this.getTotal (allobjs,
				      exp);

	double avg = total / allobjs.size ();

	Double d = new Double (avg);

	if (saveValueName != null)
	{

	    q.setSaveValue (saveValueName,
			    d);

	}

	return d;

    }

    public Double avg (Expression exp)
	               throws     QueryExecutionException
    {

	return this.avg ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
			 exp);

    }    

    public Double avg (List       allobjs,
                       Expression exp)
                       throws     QueryExecutionException
    {

	return this.avg (allobjs,
			 exp,
			 null);

    }

    /**
     * A function that will take each item from the passed in List and
     * determine a "count" for each item, i.e. how many times each item appears.
     *
     * @param objs The List of objects to operate on.
     * @return A Map of object to a count of the number of times the object appears in the list.
     * @throws QueryExecutionException Won't happen in this method.
     */
    public Map occurrence (List   objs)
	                   throws QueryExecutionException
    {

	return this.occurrence (objs,
				null);

    }

    /*
    public Map occurrence (Expression exp)
	                   throws     QueryExecutionException
    {

	return this.occurrence ((List) this.q.getVariable (Query.ALL_OBJS_VAR_NAME),
				exp);

    }    
    */

    /**
     * A function that will take each item from the passed in List and
     * determine a "count" for each item, i.e. how many times each item appears.
     *
     * @param objs The List of objects to operate on.
     * @param exp An optional expression that should be performed on each object
     *            and the value returned used instead.
     * @return A Map of object to a count of the number of times the object appears in the list.
     * @throws QueryExecutionException If the expression cannot be evaluated.
     */
    public Map occurrence (List       objs,
			   Expression exp)
	                   throws     QueryExecutionException
    {

	Map occs = new HashMap ();

	if (objs == null)
	{

	    return occs;

	}

	Object currObj = this.q.getCurrentObject ();
        List currAll = this.q.getAllObjects ();
        
        this.q.setAllObjects (objs);

	int s = objs.size ();

	for (int i = 0; i < s; i++)
	{

	    Object o = objs.get (i);

	    this.q.setCurrentObject (o);

	    if (exp != null)
	    {

		try
		{

		    o = exp.getValue (o,
				      this.q);

		} catch (Exception e) {

                    this.q.setCurrentObject (currObj);
                    this.q.setAllObjects (currAll);

		    throw new QueryExecutionException ("Unable to get value for expression: " +
						       exp + 
						       " for object: " +
						       i + 
						       " from the list of objects.",
						       e);

		}

	    }

	    Integer c = (Integer) occs.get (o);

	    int co = 1;

	    if (c != null)
	    {

		co = c.intValue ();

		co++;

	    }

	    occs.put (o,
		      Integer.valueOf (co));

	}

	this.q.setCurrentObject (currObj);
        this.q.setAllObjects (currAll);

	return occs;

    }

    /**
     * This is the same as {@link #occurrence(List,Expression)} except that the
     * second expression should evaluate to a number that will be used to limit the
     * results, the occurrence count must be greater than or equal to the value from
     * the expression.
     *
     * @param objs The List of objects to operate on.
     * @param exp An optional expression that should be performed on each object
     *            and the value returned used instead.
     * @param limitExp An expression that when evaluated should return a number, this
     *                 will then be used to limit the results returned to those that have an
     *                 occurrence count >= that number.  
     * @return A Map of object to a count of the number of times the object appears in the list.
     * @throws QueryExecutionException If the expression cannot be evaluated or the <b>limitExp</b>
     *                                 arg does not evaulate to a number.
     */
    public Map occurrence (List       objs,
			   Expression exp,
			   Expression limitExp)
	                   throws     QueryExecutionException
    {

	Map rs = this.occurrence (objs,
				  exp);

	// Evaluate the limit expression.
	Object o = limitExp.getValue (this.q.getCurrentObject (),
				      this.q);

	if (!(o instanceof Number))
	{

	    throw new QueryExecutionException ("Limit expression: " + 
					       limitExp +
					       " does not evaluate to a number");

	}

	int i = ((Number) o).intValue ();

	Map ret = new HashMap ();

	Iterator iter = rs.entrySet ().iterator ();

	while (iter.hasNext ())
	{

            Map.Entry item = (Map.Entry) iter.next ();

	    Object k = item.getKey ();

	    Integer c = (Integer) item.getValue ();

	    if (c.intValue () >= i)
	    {

		ret.put (k,
			 c);

	    }

	}

	return ret;

    }

}
