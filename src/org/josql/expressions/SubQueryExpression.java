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
package org.josql.expressions;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.gentlyweb.utils.Getter;

import org.josql.Query;
import org.josql.QueryResults;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import org.josql.events.*;

public class SubQueryExpression extends ValueExpression implements BindVariableChangedListener,
						   	           SaveValueChangedListener
{

    private Query q = null;
    private boolean inited = false;
    private String acc = null;
    private Getter get = null;
    private boolean nullQuery = false;

    public SubQueryExpression (Query q)
    {

	this.q = q;

	this.q.addBindVariableChangedListener (this);
	this.q.addSaveValueChangedListener (this);

    }

    public void bindVariableChanged (BindVariableChangedEvent ev)
    {

	if (this.q.getFrom () instanceof BindVariable)
	{

	    BindVariable bv = (BindVariable) this.q.getFrom ();

	    if (bv.getName ().equalsIgnoreCase (ev.getName ()))
	    {

		this.inited = false;

	    }

	}

    }

    public void saveValueChanged (SaveValueChangedEvent ev)
    {

	if (this.q.getFrom () instanceof SaveValue)
	{

	    SaveValue sv = (SaveValue) this.q.getFrom ();

	    if (sv.getName ().equalsIgnoreCase (ev.getName ()))
	    {

		this.inited = false;

	    }

	}

    }

    public Getter getGetter ()
    {

	return this.get;

    }

    public void setAccessor (String acc)
    {

	this.acc = acc;

    }

    public String getAccessor ()
    {

	return this.acc;

    }

    public Query getQuery ()
    {

	return this.q;

    }

    public boolean hasFixedResult (Query q)
    {

	return false;

    }

    public Class getExpectedReturnType (Query  q)
	                                throws QueryParseException
    {

	if (this.get != null)
	{

	    return this.get.getType ();

	}

	return List.class;

    }

    public void init (Query  q)
	              throws QueryParseException
    {

	// Now see if we have an accessor for the function.
	if (this.acc != null)
	{

	    try
	    {

		this.get = new Getter (this.acc,
				       ArrayList.class);
		    
	    } catch (Exception e) {

		throw new QueryParseException ("Sub-query: " + 
					       this +
					       " has accessor: " +
					       this.acc +
					       " however no valid accessor has been found in return type: " +
					       ArrayList.class.getName (),
					       e);

	    }

	}

    }

    public boolean isTrue (Object o,
			   Query  q)
	                   throws QueryExecutionException
    {

	List l = (List) this.getValue (o,
				       q);

	return l.size () > 0;

    }

    private Object innerInitFromFunction (Expression from,
                                          Object     o,
                                          Query      q)
                                          throws     QueryExecutionException
    {
        
        try
        {

            from.init (q);

        } catch (Exception e) {

            throw new QueryExecutionException ("Unable to init FROM clause: " +
                                               from +
                                               " for sub-query: " +
                                               q,
                                               e);

        }

        // Need to pass the parent query here to ensure that if we are using any
        // special bind variables they are gained from the correct place.
        return this.q.getFrom ().getValue (o,
                                           q);
        
    }

    private Object innerInitFromConstantExpression (Expression from,
                                                    Object     o,
                                                    Query      q)
                                                    throws     QueryExecutionException
    {
        
        Object obj = null;
        
        // Assume this is a getter.
        String g = (String) from.getValue (null,
                                           this.q);

        // See if it's the "special" null.
        if (!g.equalsIgnoreCase ("null"))
        {

            Accessor acc = new Accessor ();

            acc.setAccessor (g);

            this.q.setFrom (acc);
        
            try
            {

                // Init the accessor (from) but with our parent.
                acc.init (q);

            } catch (Exception e) {
                
                throw new QueryExecutionException ("Unable to init accessor: " +
                                                   g +
                                                   " from class: " +
                                                   o.getClass () +
                                                   " for FROM clause, for sub-query: \"" +
                                                   this.q + 
                                                   "\"",
                                                   e);
                
            }

            // Get the value, this will constitute the class for our query.
            obj = this.q.getFrom ().getValue (o,
                                              this.q);

        } else {

            // This is a "null" query.
            List l = new ArrayList ();
            l.add (new Object ());

            obj = l;

            this.nullQuery = true;

        }
        
        return obj;
        
    }

    private Object innerInitFromBindVariable (Expression from,
                                              Object     o,
                                              Query      q)
                                              throws     QueryExecutionException
    {

        // Need to init the bind variable.
        try
        {

            from.init (q.getTopLevelQuery ());

        } catch (Exception e) {

            throw new QueryExecutionException ("Unable to init FROM clause: " +
                                               from +
                                               " for sub-query: " +
                                               this.q,
                                               e);

        }

        return from.getValue (o,
                             q.getTopLevelQuery ());

    }

    private Object innerInitFromAccessor (Expression from,
                                          Object     o,
                                          Query      q)
                                          throws     QueryExecutionException
    {
        
        try
        {

            from.init (q);

        } catch (Exception e) {

            throw new QueryExecutionException ("Unable to init FROM clause: " +
                                               from +
                                               " for sub-query: " +
                                               this.q,
                                               e);

        }

        // Get the value, this will constitute the class for our query.
        return from.getValue (o,
                              this.q);	    
        
    }

    private void innerInit (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	Object obj = null;

	Expression from = this.q.getFrom ();

	if (from instanceof ConstantExpression)
	{

            obj = this.innerInitFromConstantExpression (from,
                                                        o,
                                                        q);

	}

	if (from instanceof Accessor)
	{

            obj = this.innerInitFromAccessor (from,
                                              o,
                                              q);

	}

	if (from instanceof Function)
	{

            obj = this.innerInitFromFunction (from,
                                              o,
                                              q);

	}

	if (from instanceof SaveValue)
	{

	    obj = from.getValue (o,
				 q.getTopLevelQuery ());

	}

	if (from instanceof BindVariable)
	{

            obj = this.innerInitFromBindVariable (from,
                                                  o,
                                                  q);
	    
	}

	if (obj == null)
	{

	    return;

	}

	// Need to ensure that obj is an instance of Collection.
	if (!(obj instanceof Collection))
	{

	    throw new QueryExecutionException ("Expected FROM clause: " +
					       this.q.getFrom () +
					       " for sub-query: " +
					       this.q +
					       " to evaluate to an instance of: " +
					       Collection.class.getName () +
					       ", instead evaluates to: " +
					       obj.getClass ().getName () +
					       " using from expression: " +
					       from);

	}

	Collection col = (Collection) obj;
	
	// Now peek at the top of the collection.
	Iterator iter = col.iterator ();
	
	if (!iter.hasNext ())
	{

	    return;

	}

	Object io = iter.next ();
	
	// Get the class...
	if (io == null)
	{

	    // Crapola!  Now need to iterate down the collection until we find
	    // an item that is not null.
	    while (iter.hasNext ())
	    {

		io = iter.next ();

		if (io != null)
		{

		    break;

		}

	    }

	}

	if (io == null)
	{

	    // Just return, no elements.
	    return;

	}

	this.q.setFromObjectClass (io.getClass ());

	try
	{

	    this.q.init ();

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to init sub-query: " + 
					       this.q +
					       " with class: " +
					       io.getClass ().getName (),
					       e);

	}

	this.inited = true;

    }

    private List innerGetValue (Object o)
	                        throws QueryExecutionException
    {

	if (this.nullQuery)
	{

	    return Query.nullQueryList;

	}

	Object obj = null;

	try
	{

	    obj = this.q.getFrom ().getValue (o,
					      this.q.getTopLevelQuery ());

	} catch (Exception e) {
	    
	    throw new QueryExecutionException ("Unable to evaluate FROM clause accessor: " +
					       this.q.getFrom () + 
					       " for sub-query: " + 
					       this.q,
					       e);

	}

	if (obj == null)
	{

	    return new ArrayList ();

	}

	if (!(obj instanceof Collection))
	{

	    throw new QueryExecutionException ("Evaluation of FROM clause for sub-query: " +
					       this.q +
					       " returns an instance of: " +
					       obj.getClass ().getName () + 
					       " however only sub-classes of: " +
					       Collection.class.getName () + 
					       " are supported.");

	}

	List l = null;

	// Now, co-erce the collection to a List.
	if (obj instanceof List)
	{

	    l = (List) obj;
	    
	} else {

	    l = new ArrayList ((Collection) obj);

	}

	return l;

    }

    public Object evaluate (Object o,
			    Query  q)
	                    throws QueryExecutionException
    {

	this.q.setParent (q);

	if (!this.inited)
	{

	    this.innerInit (o,
			    q);

	} 

	if (this.inited)
	{

	    List l = this.innerGetValue (o);

	    QueryResults qr = this.q.execute (l);

	    if (this.get != null)
	    {

		try
		{

		    return this.get.getValue (qr.getResults ());

		} catch (Exception e) {

		    throw new QueryExecutionException ("Unable to get value for accessor: " +
						       this.acc +
						       " from return type: " +
						       ArrayList.class.getName () + 
						       " after execution of sub-query: " +
						       this,
						       e);

		}

	    } 

	    return qr.getResults ();

	} 

	return new ArrayList ();

    }

    public String toString ()
    {

	return "(" + this.q.toString () + ")"; 
 
    }

}
