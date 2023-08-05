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
package org.josql.internal;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.josql.Query;
import org.josql.QueryExecutionException;

import org.josql.expressions.Expression;

public class Grouper 
{

    private List cols = new ArrayList ();
    private Query q = null;
    private int cs = -1;

    public Grouper (Query q)
    {

	this.q = q;

    }

    public List getExpressions ()
    {

	return this.cols;

    }

    public void addExpression (Expression e) 
    {

	this.cols.add (e);
	this.cs = cols.size ();

    }

    public Map group (List   objs)
	              throws QueryExecutionException
    {

	Map retVals = new HashMap ();

	int s = objs.size (); 

	List l = null;

	for (int j = 0; j < s; j++)
	{

	    Object o = objs.get (j);

	    this.q.setCurrentObject (o);

	    l = new ArrayList ();

	    // Get the values...
	    for (int i = 0; i < this.cs; i++)
	    {

		Expression exp = (Expression) this.cols.get (i);

		try
		{

		    l.add (exp.getValue (o,
					 this.q));

		} catch (Exception e) {

		    throw new QueryExecutionException ("Unable to get group by value for expression: " +
						       exp,
						       e);

		}

	    }

	    List v = (List) retVals.get (l);

	    if (v == null)
	    {

		v = new ArrayList ();

		retVals.put (l,
			     v);

	    }

	    v.add (o);

	}

	return retVals;

    }

}
