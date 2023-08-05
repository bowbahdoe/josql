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
package org.josql;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class holds all the "result" information about the execution of a particular
 * Query.  It should be noted that this class holds no reference to the Query object
 * so that a query can be executed, the results "processed" in some way and then the
 * results can be cleaned up by the GC. 
 * <p>
 * @see org.josql.Query#execute(List)
 */
public class QueryResults
{

    // Execution data.
    Map saveValues = new HashMap ();
    Map timings = null;
    List results = null;
    List whereResults = null;
    List havingResults = null;
    Map groupByResults = null;

    Map groupBySaveValues = null;

    public QueryResults ()
    {

    }

    public Map getGroupBySaveValues (List k)
    {

	if (this.groupBySaveValues == null)
	{

	    return null;

	}

	return (Map) this.groupBySaveValues.get (k);

    }

    /**
     * Get the save values.
     *
     * @return The save values.
     */
    public Map getSaveValues ()
    {

	return this.saveValues;

    }

    /**
     * Get a particular save value for the passed in key.
     *
     * @param id The key of the save value.
     * @return The value it maps to.
     */
    public Object getSaveValue (Object id)
    {

	if (this.saveValues == null)
	{

	    return null;

	}

	if (id instanceof String)
	{

	    id = ((String) id).toLowerCase ();

	}

	return this.saveValues.get (id);

    }

    /**
     * Get the results of executing the query, this is the "final" results, i.e.
     * of executing ALL of the query.
     *
     * @return The results.
     */
    public List getResults ()
    {

	return this.results;

    }

    /**
     * Get the timing information, is a Map of string to double values.
     *
     * @return The timings.
     */
    public Map getTimings ()
    {

	return this.timings;

    }

    /**
     * Get the group by results.
     *
     * @return The group by results.
     */
    public Map getGroupByResults ()
    {

	return this.groupByResults;

    }    

    /**
     * Get the having results.
     *
     * @return The having results.
     */
    public List getHavingResults ()
    {

	return this.havingResults;

    }

    /**
     * Get the where results.
     *
     * @return The where results.
     */
    public List getWhereResults ()
    {

	return this.whereResults;

    }

}
