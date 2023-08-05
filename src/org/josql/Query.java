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

import java.io.StringReader;
import java.io.BufferedReader;

import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Collection;

import org.josql.parser.JoSQLParser;

import org.josql.expressions.*;

import org.josql.functions.*;

import org.josql.internal.*;

import org.josql.events.*;

/** 
 * This class provides the ability for a developer to apply an arbitrary SQL statement
 * (using suitable syntax) to a collection of Java objects.
 * <p>
 * Basic usage:
 * <pre>
 *   Query q = new Query ();
 *   q.parse (myStatement);
 *   List results = q.execute (myObjects);
 * </pre>
 * <p>
 * An example statement would look like:
 * <pre>
 *   SELECT lastModified,
 *          name
 *   FROM   java.io.File
 *   WHERE  name LIKE '%.html'
 * </pre>
 * <p>
 * The JoSQL functionality is large and complex, whilst basic queries like the one above are
 * perfectly possible, very complex queries are also possible, for example:
 * <pre>
 *   SELECT name,
 *          formatDate(lastModified),
 *          formatNumber(length),
 *          formatNumber(length - @avg_length),
 *          formatTimeDuration(@max_last_modified - lastModified)
 *   FROM   java.io.File
 *   WHERE  lastModified > @avg_last_modified
 *   AND    length > @avg_length
 *   AND    lower(name) LIKE '%.html'
 *   GROUP BY path
 *   ORDER BY name, lastModified DESC
 *   EXECUTE ON ALL avg (:_allobjs, length) avg_length,
 *                  avg (:_allobjs, lastModified) avg_last_modified,
 *                  max (:_allobjs, lastModified) max_last_modified
 * </pre>
 * <p>
 * Note: the "EXECUTE ON ALL" syntax is an extension used by JoSQL because it has no notion
 * of "aggregate functions".
 * <p>
 * For full details of how a query works and what is possible, see the 
 * <a href="http://josql.sourceforge.net/manual/index.html">JoSQL User Manual</a>.
 * <p>
 * Please note that the package structure for JoSQL is deliberate, JoSQL is designed to be a 
 * black box and lightweight thus only the <code>Query</code> object and associated exceptions
 * are exposed in the main package.  Also, the class structure for JoSQL is not designed to 
 * exactly represent the SQL statement passed to it, rather the classes are optimised for
 * ease of execution of the statement.  If you wish to have a completely accurate Java object
 * view of ANY SQL statement then please see: 
 *   <a href="http://sourceforge.net/projects/jsqlparser">JSqlParser</a>
 * which will provide what you need.
 */
public class Query
{

    public static String QUERY_BIND_VAR_NAME = "_query";
    public static String PARENT_BIND_VAR_NAME = "_parent";
    public static String CURR_OBJ_VAR_NAME = "_currobj";
    public static String ALL_OBJS_VAR_NAME = "_allobjs";
    public static String GRPBY_OBJ_VAR_NAME = "_grpby";
    public static String GRPBY_OBJ_VAR_NAME_SYNONYM = "_groupby";
    public static final String INT_BIND_VAR_PREFIX = "^^^";

    public static final String ALL = "ALL";
    public static final String RESULTS = "RESULTS";
    public static final String GROUP_BY_RESULTS = "GROUP_BY_RESULTS";
    public static final String WHERE_RESULTS = "WHERE_RESULTS";
    public static final String HAVING_RESULTS = "HAVING_RESULTS";

    public static final String ORDER_BY_ASC = "ASC";
    public static final String ORDER_BY_DESC = "DESC";

    public static final List nullQueryList = new ArrayList ();

    static
    {

	Query.nullQueryList.add (new Object ());

    }

    private List bfhs = new ArrayList ();
    private Map bfhsMap = new HashMap ();

    private char wildcardChar = '%';

    private Map aliases = new HashMap ();
    private List groupBys = null;
    private Comparator orderByComp = null;
    private Comparator groupOrderByComp = null;
    private Grouper grouper = null;
    private List orderBys = null;
    private List groupOrderBys = null;
    private List cols = null;
    private boolean retObjs = false;
    private Expression where = null;
    private Expression having = null;
    private Map bindVars = null;
    private String query = null;
    private boolean wantTimings = false;
    private List functionHandlers = null;
    private int anonVarIndex = 1;
    private Expression from = null;
    private Class objClass = null;
    private Limit limit = null;
    private Limit groupByLimit = null;
    private Map executeOn = null;
    private boolean isParsed = false;
    private boolean distinctResults = false;
    private ClassLoader classLoader = null;
    private Query parent = null;
    private Map listeners = new HashMap ();
    private Comparator userComparator = null;

    // Execution data.
    private transient Object currentObject = null;
    private transient List allObjects = null;
    private transient List currGroupBys = null;
    
    private QueryResults qd = null;

    /**
     * Return the WHERE clause expression.
     *
     * @return The WHERE clause as an expression.
     */
    public Expression getWhereClause ()
    {

	return this.where;

    }

    /**
     * Return the HAVING clause expression.
     *
     * @return The HAVING clause as an expression.
     */
    public Expression getHavingClause ()
    {

	return this.having;

    }

    /**
     * Return the {@link Comparator} we will use to do the ordering of the results, may be null.
     *
     * @return The Comparator.
     */
    public Comparator getOrderByComparator ()
    {

	return this.orderByComp;

    }

    public FunctionHandler getFunctionHandler (String id)
    {

	if (this.parent != null)
	{

	    return this.parent.getFunctionHandler (id);

	}

	return (FunctionHandler) this.bfhsMap.get (id);

    }

    private void initFunctionHandlers ()
    {

	FunctionHandler o = new CollectionFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (CollectionFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

	o = new StringFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (StringFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

	o = new ConversionFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (ConversionFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

	o = new FormattingFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (FormattingFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

	o = new GroupingFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (GroupingFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

	o = new MiscellaneousFunctions ();
	o.setQuery (this);

	this.bfhsMap.put (MiscellaneousFunctions.HANDLER_ID,
			  o);

	this.bfhs.add (o);

    }

    public Map getExecuteOnFunctions ()
    {

	return this.executeOn;

    }

    public void setExecuteOnFunctions (Map ex)
    {

	this.executeOn = ex;

    }

    public String getAnonymousBindVariableName ()
    {

	if (this.parent != null)
	{

	    return this.parent.getAnonymousBindVariableName ();

	}

	String n = Query.INT_BIND_VAR_PREFIX + this.anonVarIndex;

	this.anonVarIndex++;

	return n;

    }

    public List getDefaultFunctionHandlers ()
    {

	if (this.parent != null)
	{

	    return this.parent.getDefaultFunctionHandlers ();

	}

	return new ArrayList (this.bfhs);

    }

    public List getFunctionHandlers ()
    {

	if (this.parent != null)
	{

	    return this.parent.getFunctionHandlers ();

	}

	return this.functionHandlers;

    }

    public void addFunctionHandler (Object o)
    {

	if (this.parent != null)
	{

	    this.parent.addFunctionHandler (o);

	}

	if (this.functionHandlers == null)
	{

	    this.functionHandlers = new ArrayList ();

	}

	if (o instanceof FunctionHandler)
	{

	    FunctionHandler fh = (FunctionHandler) o;

	    fh.setQuery (this);

	}

	this.functionHandlers.add (o);

    }

    public void setFrom (Expression exp)
    {

	this.from = exp;

    }

    public Expression getFrom ()
    {

	return this.from;

    }

    public void setClassName (String n)
    {

	ConstantExpression ce = new ConstantExpression ();
	this.from = ce;
	ce.setValue (n);

    }

    public void setOrderByColumns (List cols)
    {

	this.orderBys = cols;

    }

    public void setGroupByLimit (Limit g)
    {

	this.groupByLimit = g;

    }

    public void setGroupByOrderColumns (List cols)
    {

	this.groupOrderBys = cols;

    }

    public List getGroupByColumns ()
    {

	return this.groupBys;

    }

    public void setGroupByColumns (List cols)
    {

	this.groupBys = cols;

    }

    public List getColumns ()
    {

	return this.cols;

    }

    public void setColumns (List cols)
    {

	this.cols = cols;

    }

    /**
     * Set the expression for the HAVING clause.
     * Caution: do NOT use this method unless you are sure about what you are doing!
     *
     * @param be The expression.
     */
    public void setHaving (Expression be)
    {

	this.having = be;

    }

    /**
     * Set the expression for the WHERE clause.
     * Caution: do NOT use this method unless you are sure about what you are doing!
     *
     * @param be The expression.
     */
    public void setWhere (Expression be)
    {

	this.where = be;

    }

    /**
     * Create a new blank Query object.
     */
    public Query ()
    {

	this.initFunctionHandlers ();

    }

    public void setWantTimings (boolean v)
    {

	this.wantTimings = v;

    }

    protected void addTiming (String id,
			      double time)
    {

	if (this.wantTimings)
	{

	    if (this.qd == null)
	    {

		return;

	    }

	    if (this.qd.timings == null)
	    {

		this.qd.timings = new LinkedHashMap ();

	    }

	    this.qd.timings.put (id,
				 new Double (time));

	}

    }

    /**
     * Get the value of an indexed bind variable.
     *
     * @param index The index.
     * @return The value.
     */
    public Object getVariable (int index)
    {

	if (this.parent != null)
	{

	    return this.parent.getVariable (index);

	}

	return this.getVariable (Query.INT_BIND_VAR_PREFIX + index);

    }

    /**
     * Get the class that the named variable has.
     *
     * @param name The name of the variable.
     * @return The Class.
     */
    public Class getVariableClass (String name)
    {

	String n = name.toLowerCase ();

	if (n.equals (Query.QUERY_BIND_VAR_NAME))
	{

	    // Return the query itself!
	    return Query.class;

	}

	if (n.equals (Query.PARENT_BIND_VAR_NAME))
	{

	    // Return the query itself!
	    return Query.class;

	}

	if (n.equals (Query.CURR_OBJ_VAR_NAME))
	{

	    // May be null if we aren't processing a while/having expression.
	    return this.objClass;

	}

	if (n.equals (Query.ALL_OBJS_VAR_NAME))
	{

	    // May change depending upon when it is called.
	    return List.class;

	}

	if (this.parent != null)
	{

	    return this.parent.getVariableClass (n);

	}

	if (this.bindVars == null)
	{

	    return Object.class;

	}

	Object v = (Object) this.bindVars.get (n);

	if (v == null)
	{

	    return Object.class;

	}

	return v.getClass ();

    }

    /**
     * Get the value of a group by variable from the current group bys.
     *
     * @param ind The variable index.
     * @return The value.
     */
    public Object getGroupByVariable (int ind)
    {

	// Get the current group bys.
	if (this.currGroupBys != null)
	{

	    return this.currGroupBys.get (ind - 1);

	}

	return null;

    }

    /**
     * Get the value of a named bind variable.
     *
     * @param name The name of the bind variable.
     * @return The value.
     */
    public Object getVariable (String name)
    {

	String n = name.toLowerCase ();

        if (n.startsWith (":"))
        {
            
            n = n.substring (1);
            
        }

	if (n.equals (Query.QUERY_BIND_VAR_NAME))
	{

	    // Return the query itself!
	    return this;

	}

	if (n.equals (Query.PARENT_BIND_VAR_NAME))
	{

	    // Return the parent query.
	    return this.parent;

	}

	if (n.equals (Query.CURR_OBJ_VAR_NAME))
	{

	    // May be null if we aren't processing a while/having expression.
	    return this.currentObject;

	}

	if (n.equals (Query.ALL_OBJS_VAR_NAME))
	{

	    // May change depending upon when it is called.
	    return this.allObjects;

	}

	if (this.parent != null)
	{

	    return this.parent.getVariable (name);

	}

	if (this.bindVars == null)
	{

	    return null;

	}

	return this.bindVars.get (n);

    }    

    /**
     * Set the value of a named bind variable.
     *
     * @param name The name.
     * @param v The value.
     */
    public void setVariable (String name,
			     Object v)
    {

	if (this.parent != null)
	{

	    this.parent.setVariable (name,
				     v);

	    return;

	}

	if (this.bindVars == null)
	{

	    this.bindVars = new HashMap ();

	}

        if (name.startsWith (":"))
        {
            
            name = name.substring (1);
            
        }

	this.bindVars.put (name.toLowerCase (),
			   v);

    }

    /**
     * Set the value of an indexed bind variable.
     *
     * @param index The index.
     * @param v The value.
     */
    public void setVariable (int    index,
			     Object v)
    {

	if (this.parent != null)
	{

	    this.parent.setVariable (index,
				     v);

	    return;

	}

	this.setVariable (Query.INT_BIND_VAR_PREFIX + index,
			  v);

    }

    /**
     * Get all the bind variables as a Map.
     *
     * @return The name/value mappings of the bind variables.
     */
    public Map getVariables ()
    {

	if (this.parent != null)
	{

	    return this.parent.getVariables ();

	}

	return this.bindVars;

    }

    /**
     * A helper method that will evaluate the WHERE clause for the object passed in.
     *
     * @param o The object to evaluate the WHERE clause against.
     * @return The result of calling: Expression.isTrue(Object,Query) for the WHERE clause.
     */
    public boolean isWhereTrue (Object o)
                                throws QueryExecutionException 
    {

	if (this.where == null)
	{

	    // A null where means yes!
	    return true;

	}

	return this.where.isTrue (o,
				  this);

    }

    /**
     * Set the bind variables in one go.
     *
     * @param bVars The bind variable name/value mappings.
     */
    public void setVariables (Map bVars)
    {

	if (this.parent != null)
	{

	    this.parent.setVariables (bVars);

	    return;

	}

        Iterator iter = bVars.entrySet ().iterator ();
        
        while (iter.hasNext ())
        {
            
            Map.Entry item = (Map.Entry) iter.next ();
            
            Object k = item.getKey ();
            Object v = item.getValue ();
            
            if (k instanceof Number)
            {
                
                this.setVariable (((Number) k).intValue (),
                                  v);
                
            } else {
            
                this.setVariable (k.toString (),
                                  v);
                
            }
            
        }

    }

    /**
     * Execute all the expressions for the specified type, either: {@link #ALL} or:
     * {@link #RESULTS}.  If the expressions are aliased then the results will be
     * available in the save results upon completion.
     *
     * @param l The List of objects to execute the functions on.
     * @param t The type of expressions to execute.
     * @throws QueryExecutionException If there is an issue with executing one of the
     *                                 expressions or if the Query hasn't been inited yet.
     */
    public void doExecuteOn (List   l,
			     String t)
	                     throws QueryExecutionException
    {

        if (this.executeOn == null)
        {
            
            // Do nothing.
            return;
            
        }

	if (!this.isParsed)
	{

	    throw new QueryExecutionException ("Query has not been initialised.");

	}

	if (this.executeOn != null)
	{

	    // Set the "all objects".
	    this.allObjects = l;

	    long s = System.currentTimeMillis ();

	    List fs = (List) this.executeOn.get (t);

	    if (fs != null)
	    {

		// Execute each one in turn.
		int si = fs.size (); 

		for (int i = 0; i < si; i++)
		{

		    AliasedExpression f = (AliasedExpression) fs.get (i);

		    Object o = f.getValue (null,
					   this);
		    
		    String af = f.getAlias ();

		    if (af != null)
		    {

			this.setSaveValue (af,
					   o);
			
		    }
		    
		}

		this.addTiming ("Total time to execute: " + si + " expression(s) on " + t + " objects",
				System.currentTimeMillis () - s);	

	    }

	}

    }

    /**
     * This method will be called at the end of a query execution to clean up the
     * transient objects used throughout execution.
     */
    private void clearResults ()
    {

	this.qd = null;

	this.currentObject = null;
	this.allObjects = null;
        this.currGroupBys = null;

    }

    /**
     * Execute this query on the specified objects provided by the iterator.  It should be noted that the iterator
     * is first traversed and the objects it returns converted to a List and then passed to the {@link #execute(List)} method for execution.
     *
     * @param iter The iterator to use to get the objects.
     * @return The list of objects that match the query.
     * @throws QueryExecutionException If the query cannot be executed.
     */
    public QueryResults execute (Iterator iter)
	                         throws   QueryExecutionException
    {

	if ((iter == null)
	    &&
	    (this.objClass != null)
	   )
	{

	    throw new QueryExecutionException ("Iterator must be non-null when an object class is specified.");

	}
		
        List l = new ArrayList ();
        
        while (iter.hasNext ())
        {
            
            l.add (iter.next ());
            
        }
                
	return this.execute (l);

    }

    /**
     * Execute this query on the specified objects.  It should be noted that the collection
     * is first converted to a List and then passed to the {@link #execute(List)} method for execution.
     *
     * @param objs The collection of objects to execute the query on.
     * @return The list of objects that match the query.
     * @throws QueryExecutionException If the query cannot be executed.
     */
    public QueryResults execute (Collection objs)
	                         throws     QueryExecutionException
    {

	if ((objs == null)
	    &&
	    (this.objClass != null)
	   )
	{

	    throw new QueryExecutionException ("Collection of objects must be non-null when an object class is specified.");

	}
		
        List l = new ArrayList (objs.size ());
        l.addAll (objs);
                
	return this.execute (l);

    }

    /**
     * Execute this query on the specified objects.
     *
     * @param objs The list of objects to execute the query on.
     * @return The list of objects that match the query.
     * @throws QueryExecutionException If the query cannot be executed.
     */
    public QueryResults execute (List   objs)
	                         throws QueryExecutionException
    {

	if ((objs == null)
	    &&
	    (this.objClass != null)
	   )
	{

	    throw new QueryExecutionException ("List of objects must be non-null when an object class is specified.");

	}

	this.qd = new QueryResults ();

	if ((this.objClass == null)
	    &&
	    (objs == null)
	   )
	{

	    objs = Query.nullQueryList;

	}

	this.allObjects = objs;

	// See if we have any expressions that are to be executed on 
	// the complete set.
	this.doExecuteOn (objs,
		          Query.ALL);

        this.evalWhereClause ();

	// See if we have any functions that are to be executed on 
	// the results...
        this.doExecuteOn (this.qd.results,
                          Query.RESULTS);

	// If we have a "having" clause execute it here...
        this.evalHavingClause ();

	// Now perform the group by operation.
        if (this.grouper != null)
        {
            
            this.evalGroupByClause ();
            
            return this.qd;
        
        }

	// Now perform the order by.
        this.evalOrderByClause ();

	// Finally, if we have a limit clause, restrict the set of objects returned...
        this.evalLimitClause ();

        this.evalSelectClause ();

	try
	{

	    return this.qd;

	} finally {

	    // Clean up ;)
	    this.clearResults ();

	}

    }

    private void evalSelectClause ()
                                   throws QueryExecutionException
    {
        
	boolean retNewObjs = false;

	// See if we are a single column of new objects.
	if (!this.retObjs)
	{

	    if (this.cols.size () == 1)
	    {

		SelectItemExpression sei = (SelectItemExpression) this.cols.get (0);
		
		if (sei.getExpression () instanceof NewObjectExpression)
		{

		    retNewObjs = true;
		    
		}

	    }

	}

	long s = System.currentTimeMillis ();

	// Now get the columns if necessary, we do this here to get the minimum
	// set of objects required.
	if ((!this.retObjs)
	    &&
	    (!retNewObjs)
	   )
	{

	    Collection resC = null;

	    if (!this.distinctResults)
	    {

		resC = new ArrayList (this.qd.results.size ());

	    } else {

		resC = new LinkedHashSet (this.qd.results.size ());

	    }

	    // Get the column values.
	    this.getColumnValues (this.qd.results,
				  resC);

	    if (this.distinctResults)
	    {

		this.qd.results = new ArrayList (resC);

	    } else {

		this.qd.results = (List) resC;

	    }

	    this.addTiming ("Collection of results took",
			    (double) (System.currentTimeMillis () - s));

	} else {

	    if (this.retObjs)
	    {

		if (this.distinctResults)
		{

		    s = System.currentTimeMillis ();
		    
		    this.qd.results = ((CollectionFunctions) this.getFunctionHandler (CollectionFunctions.HANDLER_ID)).unique (this.qd.results);
		    
		    this.addTiming ("Collecting unique results took",
				    (double) (System.currentTimeMillis () - s));

		}

	    }

	    // If we want a single column of new objects...
	    if (retNewObjs)
	    {

		this.qd.results = this.getNewObjectSingleColumnValues (this.qd.results);

	    }

	}        
        
    }

    private void evalOrderByClause ()
                                    throws QueryExecutionException
    {
        
	if ((this.qd.results.size () > 1)
	    &&
	    (this.orderByComp != null)
	   )
	{

	    long s = System.currentTimeMillis ();

	    // It should be noted here that the comparator will set the
	    // "current object" so that it can be used in the order by
	    // clause.
	    Collections.sort (this.qd.results,
			      this.orderByComp);

	    this.addTiming ("Total time to order results",
			    System.currentTimeMillis () - s);	

	}

	if (this.orderByComp != null)
	{

	    ListExpressionComparator lec = (ListExpressionComparator) this.orderByComp;

	    if (lec.getException () != null)
	    {

		throw new QueryExecutionException ("Unable to order results",
						   lec.getException ());

	    }

	    lec.clearCache ();

	}
        
    }

    private void evalGroupByClause ()
                                    throws QueryExecutionException
    {
     
        long s = System.currentTimeMillis ();
     
        // Need to handle the fact that this will return a Map of Lists...
        try
        {

            s = System.currentTimeMillis ();

            // Group the objects.
            Map mres = this.grouper.group (this.qd.results);

            this.qd.groupByResults = mres;

            List grpBys = new ArrayList (mres.keySet ());

            // Convert the keys in the group by to a List.
            Map origSvs = this.qd.saveValues;

            Map nres = new LinkedHashMap ();

            int gs = grpBys.size ();

            // Now for each "group by" list, do:
            // 1. Execute the functions for the GROUP_BY_RESULTS type.
            // 2. Sort the group by results according to the ORDER BY clause.
            // 3. Limit the group by results according to the LIMIT clause.
            for (int i = 0; i < gs; i++)
            {

                List l = (List) grpBys.get (i);

                List lr = (List) mres.get (l);

                this.allObjects = lr;
                this.currGroupBys = l;

                // Now set the save values for the group bys.
                if (this.qd.groupBySaveValues == null)
                {

                    this.qd.groupBySaveValues = new HashMap ();

                }

                this.qd.saveValues = new HashMap ();

                if (origSvs != null)
                {

                    this.qd.saveValues.putAll (origSvs);
                    
                }

                this.qd.groupBySaveValues.put (l,
                                               this.qd.saveValues);

                // Now execute all (any) group by results functions.
                this.doExecuteOn (lr,
                                  Query.GROUP_BY_RESULTS);

                // Now sort these according to the order by (if any).
                if ((lr.size () > 1)
                    &&
                    (this.orderByComp != null)
                   )
                {

                    Collections.sort (lr,
                                      this.orderByComp);

                    ListExpressionComparator lec = (ListExpressionComparator) this.orderByComp;

                    if (lec.getException () != null)
                    {

                        throw new QueryExecutionException ("Unable to order group by results",
                                                           lec.getException ());

                    }

                    lec.clearCache ();

                }

                if (!this.retObjs)
                {

                    // Now collect the values...
                    Collection res = null;

                    if (!this.distinctResults)
                    {

                        res = new ArrayList ();

                    } else {

                        res = new LinkedHashSet ();

                    }

                    this.getColumnValues (lr,
                                          res);

                    if (this.distinctResults)
                    {

                        lr = new ArrayList (res);

                    } else {

                        lr = (List) res;

                    }

                } else {

                    if (this.distinctResults)
                    {

                        this.qd.results = ((CollectionFunctions) this.getFunctionHandler (CollectionFunctions.HANDLER_ID)).unique (this.qd.results);

                    }

                }

                nres.put (l,
                          lr);

            }

            // Restore the save values.
            this.qd.saveValues = origSvs;

            // Set the group by results.
            this.qd.groupByResults = nres;

            long t = System.currentTimeMillis ();

            this.addTiming ("Group column collection and sort took",
                            (double) (t - s));
            
            s = t;
            
            // Now order the group bys, if present.
            if (this.groupOrderByComp != null)
            {

                origSvs = this.qd.saveValues;

                Collections.sort (grpBys,
                                  this.groupOrderByComp);

                // "Restore" the save values.
                this.qd.saveValues = origSvs;

                GroupByExpressionComparator lec = (GroupByExpressionComparator) this.groupOrderByComp;

                if (lec.getException () != null)
                {

                    throw new QueryExecutionException ("Unable to order group bys, remember that the current object here is a java.util.List, not the class defined in the FROM clause, you may need to use the org.josq.functions.CollectionFunctions.get(java.util.List,Number) function to get access to the relevant value from the List.",
                                                       lec.getException ());

                }

                lec.clearCache ();

            }

            // Now limit the group bys, if required.
            if (this.groupByLimit != null)
            {

                s = System.currentTimeMillis ();
                
                List oGrpBys = grpBys;
                
                grpBys = this.groupByLimit.getSubList (grpBys,
                                                       this);
                
                // Now trim out from the group by results any list that isn't in the current grpbys.
                for (int i = 0; i < oGrpBys.size (); i++)
                {

                    List l = (List) oGrpBys.get (i);

                    if (!grpBys.contains (l))
                    {
                        
                        // Remove.
                        this.qd.groupByResults.remove (l);
                        
                    }
                    
                }
                
                this.addTiming ("Total time to limit group by results size",
                                System.currentTimeMillis () - s);	

            }

            this.addTiming ("Group operation took",
                            (double) (System.currentTimeMillis () - s));

            // "Restore" the save values.
            this.qd.saveValues = origSvs;

            this.qd.results = grpBys;
            
            // NOW limit the group by results to a certain size, this needs
            // to be done last so that the group by limit clause can make use of the size of the
            // results.
            if (this.limit != null)
            {
                
                for (int i = 0; i < this.qd.results.size (); i++)
                {

                    List l = (List) this.qd.results.get (i);

                    List lr = (List) this.qd.groupByResults.get (l);

                    this.allObjects = lr;
                    this.currGroupBys = l;
            
                    this.qd.saveValues = (Map) this.qd.groupBySaveValues.get (l);
                        
                    this.qd.groupByResults.put (l,
                                                this.limit.getSubList (lr,
                                                                       this));
                
                }            

            }

            this.qd.saveValues = origSvs;

        } catch (Exception e) {

            throw new QueryExecutionException ("Unable to perform group by operation",
                                               e);

        }
                                   
    }                                    

    private void evalHavingClause ()
                                   throws QueryExecutionException
    {
    
    	if (this.having != null)
	{

	    int si = this.qd.results.size (); 

	    this.qd.havingResults = new ArrayList (si);

	    for (int i = 0; i < si; i++)
	    {

		Object o = this.qd.results.get (i);

		this.currentObject = o;

		if (this.having.isTrue (o,
					this))
		{

		    this.qd.havingResults.add (o);

		}

	    }	    

	    this.qd.results = this.qd.havingResults;

	    // Future proofing...
	    this.allObjects = this.qd.results;

	}
        
    }

    private void evalLimitClause ()
                                  throws QueryExecutionException
    {
        
        if (this.limit != null)
	{

	    long s = System.currentTimeMillis ();

	    this.qd.results = this.limit.getSubList (this.qd.results,
						     this);

	    this.addTiming ("Total time to limit results size",
			    System.currentTimeMillis () - s);	

	}
    }

    private void evalWhereClause ()
                                  throws QueryExecutionException
    {
        
        long s = System.currentTimeMillis ();
        
        int si = this.allObjects.size ();
        
        if (this.where != null)
	{

	    // Create the where results with "about" half the size of the input collection.
	    // Further optimizations may be possible here if some statistics are collected
	    // about how many objects match/fail the where clause and then increase the
	    // capacity of the where results list as required, i.e. to cut down on the number
	    // of array copy and allocation operations performed.  For now though half will do ;)
	    this.qd.whereResults = new ArrayList (si / 2);

	    for (int i = 0; i < si; i++)
	    {

		Object o = this.allObjects.get (i);

		this.currentObject = o;

		boolean res = this.where.isTrue (o,
						 this);

		if (res)
		{

		    this.qd.whereResults.add (o);

		}

	    }

	} else {

	    // No limiting where clause so what's passed in is what comes out.
	    this.qd.whereResults = this.allObjects;

	}

	double wet = (double) System.currentTimeMillis () - (double) s;

	this.addTiming ("Total time to execute Where clause on all objects",
			wet);
	this.addTiming ("Where took average over: " + si + " objects",
			wet / (double) si);

	this.allObjects = this.qd.whereResults;

	// The results here are the result of executing the where clause, if present.
	this.qd.results = this.qd.whereResults;

    }

    public void setCurrentGroupByObjects (List objs)
    {

	this.currGroupBys = objs;

    }

    /**
     * Get the current list of objects in context (value of the :_allobjs special bind variable).
     * Note: the value of the :_allobjs bind variable will change depending upon where the query execution
     * is up to.
     *
     * @return The list of objects in context.
     */
    public List getAllObjects ()
    {

	return this.allObjects;

    }

    public void setAllObjects (List objs)
    {

	this.allObjects = objs;

    }

    public void setCurrentObject (Object o)
    {

	this.currentObject = o;

    }

    /**
     * Get the current object (value of the :_currobj special bind variable).  Note: the value
     * of the :_currobj bind variable will change depending upon where the query execution is up to.
     *
     * @return The current object in context.
     */
    public Object getCurrentObject ()
    {

	return this.currentObject;

    }

    private void getColumnValues (List       res,
				  Collection rs)
	                          throws     QueryExecutionException
    {

	int s = res.size ();

	int cs = this.cols.size ();

	boolean addItems = false;

	for (int i = 0; i < s; i++)
	{

	    Object o = res.get (i);

	    this.currentObject = o;

	    List sRes = new ArrayList (cs);

	    for (int j = 0; j < cs; j++)
	    {

		SelectItemExpression v = (SelectItemExpression) this.cols.get (j);

		try
		{

		    if (v.isAddItemsFromCollectionOrMap ())
		    {

			addItems = true;
			    
		    }
			
		    // Get the value from the object...
		    Object ov = v.getValue (o,
					    this);
		    
		    if (addItems)
		    {
			    
			rs.addAll (v.getAddItems (ov));
			
		    } else {
			
			sRes.add (ov);
			
		    }
		    
		    // Now since the expression can set the current object, put it
		    // back to rights after the call...
		    this.currentObject = o;

		} catch (Exception e) {

		    throw new QueryExecutionException ("Unable to get value for column: " +
						       j + 
						       " for: " +
						       v.toString () + 
						       " from result: " +
						       i + 
						       " (" +
						       o + 
						       ")",
						       e);
							   
		}
		
	    }

	    if (!addItems)
	    {

		rs.add (sRes);

	    }

	}

    }

    private List getNewObjectSingleColumnValues (List   rows)
	                                         throws QueryExecutionException
    {

	int s = rows.size ();

	SelectItemExpression nsei = (SelectItemExpression) this.cols.get (0);

	List res = new ArrayList (s);

	for (int i = 0; i < s; i++)
	{

	    Object o = rows.get (i);

	    this.currentObject = o;

	    try
	    {

		res.add (nsei.getValue (o,
					this));

		// Now since the expression can set the current object, put it
		// back to rights after the call...
		this.currentObject = o;

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value for column: " +
						   1 + 
						   " for: " +
						   nsei.toString () + 
						   " from result: " +
						   i + 
						   " (" +
						   o + 
						   ")",
						   e);
							   
	    }

	}

	return res;

    }

    public void setSaveValues (Map s)
    {

	if (this.parent != null)
	{

	    this.parent.qd.saveValues.putAll (s);

	    return;

	}

	this.qd.saveValues = s;

    }
    
    public void setSaveValue (Object id,
			      Object value)
    {

	if (this.parent != null)
	{

	    this.parent.setSaveValue (id,
				      value);

	    return;

	}

	if (this.qd == null)
	{

	    return;

	}

	if (id instanceof String)
	{

	    id = ((String) id).toLowerCase ();

	}

	Object old = this.qd.saveValues.get (id);

	this.qd.saveValues.put (id,
				value);

	if (old != null)
	{

	    this.fireSaveValueChangedEvent (id,
					    old,
					    value);

	}

    }

    protected void fireSaveValueChangedEvent (Object id,
					      Object from,
					      Object to)
    {

	List l = (List) this.listeners.get ("svs");

	if ((l == null)
	    ||
	    (l.size () == 0)
	   )
	{

	    return;

	}

	SaveValueChangedEvent svce = new SaveValueChangedEvent (this,
								id.toString ().toLowerCase (),
								from,
								to);

	for (int i = 0; i < l.size (); i++)
	{

	    SaveValueChangedListener svcl = (SaveValueChangedListener) l.get (i);

	    svcl.saveValueChanged (svce);

	}

    }

    protected void fireBindVariableChangedEvent (String name,
						 Object from,
						 Object to)
    {

	List l = (List) this.listeners.get ("bvs");

	if ((l == null)
	    ||
	    (l.size () == 0)
	   )
	{

	    return;

	}

	BindVariableChangedEvent bvce = new BindVariableChangedEvent (this,
								      name,
								      from,
								      to);

	for (int i = 0; i < l.size (); i++)
	{

	    BindVariableChangedListener bvcl = (BindVariableChangedListener) l.get (i);

	    bvcl.bindVariableChanged (bvce);

	}

    }

    /**
     * Get the save value for a particular key and group by list.
     *
     * @param id The id of the save value.
     * @param gbs The group by list key.
     * @return The object the key maps to.
     */
    public Object getGroupBySaveValue (Object id,
				       List   gbs)
    {

	if (this.parent != null)
	{

	    return this.parent.getGroupBySaveValue (id,
                                                    gbs);

	}

	Map m = this.getGroupBySaveValues (gbs);

	if (m == null)
	{

	    return null;

	}

	return m.get (id);

    }

    /**
     * Get the save values for the specified group bys.
     *
     * @param gbs The group bys.
     * @return The save values (name/value pairs).
     */
    public Map getGroupBySaveValues (List gbs)
    {

	if (this.parent != null)
	{

	    return this.parent.getGroupBySaveValues (gbs);

	}

	if ((this.qd == null)
	    ||
	    (this.qd.groupBySaveValues == null)
	   )
	{

	    return null;

	}

	return (Map) this.qd.groupBySaveValues.get (gbs);

    }

    /**
     * Get the save values for a particular key.
     *
     * @return The object the key maps to.
     */
    public Object getSaveValue (Object id)
    {

	if (this.parent != null)
	{

	    return this.parent.getSaveValue (id);

	}

	if ((this.qd == null)
	    ||
	    (this.qd.saveValues == null)
	   )
	{

	    return null;

	}

	if (id instanceof String)
	{

	    id = ((String) id).toLowerCase ();

	}

	return this.qd.saveValues.get (id);

    }

    /**
     * Get the query string that this Query object represents.
     *
     * @return The query string.
     */
    public String getQuery ()
    {

	return this.query;

    }

    /**
     * Sets the custom comparator to use to perform per object comparisons.
     *
     * @param c The comparator.
     */
    public void setObjectComparator (Comparator c)
    {
        
        this.userComparator = c;
        
    }

    public Comparator getObjectComparator ()
    {
        
        return this.userComparator;
        
    }

    /**
     * Will cause the order by comparator used to order the results
     * to be initialized.  This is generally only useful if you are specifying the
     * the order bys yourself via: {@link #setOrderByColumns(List)}.  Usage of
     * this method is <b>NOT</b> supported, so don't use unless you really know what 
     * you are doing!
     */
    public void initOrderByComparator ()
	                               throws QueryParseException
    {

	if (this.orderBys != null)
	{
	    
	    // No caching, this may need to change in the future.
	    this.orderByComp = new ListExpressionComparator (this,
                                                             this.userComparator,
							     false);

	    ListExpressionComparator lec = (ListExpressionComparator) this.orderByComp;

	    // Need to check the type of each order by, if we have
	    // any "column" indexes check to see if they are an accessor...
	    int si = this.orderBys.size (); 

	    for (int i = 0; i < si; i++)
	    {

		OrderBy ob = (OrderBy) this.orderBys.get (i);

		// Get the expression...
		Expression e = (Expression) ob.getExpression ();

		if (e == null)
		{

		    // Now expect an integer that refers to a column
		    // in the select...
		    int ci = ob.getIndex ();

		    if (ci == 0)
		    {

			throw new QueryParseException ("Order by column indices should start at 1.");

		    }

		    if (this.retObjs)
		    {

			throw new QueryParseException ("Cannot sort on a select column index when the objects are to be returned.");

		    }

		    if (ci > this.cols.size ())
		    {

			throw new QueryParseException ("Invalid order by column index: " + 
						       ci + 
						       ", only: " +
						       this.cols.size () + 
						       " columns are selected to be returned.");

		    }

		    // Get the SelectItemExpression.
		    SelectItemExpression sei = (SelectItemExpression) this.cols.get (ci - 1);

		    // Get the expression...
		    e = sei.getExpression ();

		} else {

		    // Init the expression...
		    e.init (this);

		}

		// Check to see if the expression returns a fixed result, if so
		// there's no point adding it.
		if (!e.hasFixedResult (this))
		{

		    lec.addSortItem (e,
				     ob.getType ());

		}

	    }

	}

    }

    /**
     * Re-order the objects according to the columns supplied in the <b>dirs</b> Map.
     * The Map should be keyed on an Integer and map to a String value, the String value should
     * be either: {@link #ORDER_BY_ASC} for the column to be in ascending order or: 
     * {@link #ORDER_BY_DESC} for the column to be in descending order.  The Integer refers
     * to a column in the SELECT part of the statement.
     * <p>
     * For example:
     * <p>
     * <pre>
     *   SELECT name,
     *          directory,
     *          file
     *          length
     *   FROM   java.io.File
     * </pre>
     * Can be (re)ordered via the following code:
     * <pre>
     *   Query q = new Query ();
     *   q.parse (sql);
     *   
     *   Map reorderBys = new TreeMap ();
     *   reorderBys.put (new Integer (2), Query.ORDER_BY_ASC);
     *   reorderBys.put (new Integer (3), Query.ORDER_BY_DESC);
     *   reorderBys.put (new Integer (1), Query.ORDER_BY_ASC);
     *   reorderBys.put (new Integer (4), Query.ORDER_BY_DESC);
     *
     *   // Note: this call will cause the entire statement to be executed.
     *   q.reorder (myFiles,
     *              reorderBys);
     * </pre>
     *
     * @param objs The objects you wish to reorder.
     * @param dirs The order bys.
     * @return The QueryResults.
     * @throws QueryParseException If the statement can be parsed, i.e. if any of the order by
     *                             columns is out of range.
     * @throws QueryExecutionException If the call to: {@link #execute(List)} fails.
     * @see #reorder(List,String)
     */
    public QueryResults reorder (List      objs,
				 SortedMap dirs)
	                         throws QueryExecutionException,
					QueryParseException
    {

	if (this.isWantObjects ())
	{

	    throw new QueryParseException ("Only SQL statements that return columns (not the objects passed in) can be re-ordered.");

	}

	List obs = new ArrayList ();

	Iterator iter = dirs.entrySet ().iterator ();

	while (iter.hasNext ())
	{

            Map.Entry item = (Map.Entry) iter.next ();

	    Integer in = (Integer) item.getKey ();
	    
	    // See if we have a column for it.
	    if (in.intValue () > this.cols.size ())
	    {

		throw new QueryParseException ("Cannot reorder: " +
					       dirs.size () + 
					       " columns, only: " +
					       this.cols.size () + 
					       " are present in the SQL statement.");

	    }

	    String dir = (String) item.getValue ();
	    
	    int d = OrderBy.ASC;
	    
	    if (dir.equals (Query.ORDER_BY_DESC))
	    {

		d = OrderBy.DESC;
		
	    }
	    
	    OrderBy ob = new OrderBy ();
	    ob.setIndex (in.intValue ());
	    ob.setType (d);
	    
	    obs.add (ob);
	    
	}

	this.orderBys = obs;
	
	this.initOrderByComparator ();

	// Execute the query.
	return this.execute (objs);	

    }

    /**
     * Allows the re-ordering of the results via a textual representation of the order bys.
     * This is effectively like providing a new ORDER BY clause to the sql.
     * <p>
     * For example:
     * <p>
     * <pre>
     *   SELECT name,
     *          directory,
     *          file
     *          length
     *   FROM   java.io.File
     * </pre>
     * Can be (re)ordered via the following code:
     * <pre>
     *   Query q = new Query ();
     *   q.parse (sql);
     *   
     *   // Note: this call will cause the entire statement to be executed.
     *   q.reorder (myFiles,
     *              "name DESC, 3 ASC, length, 1 DESC");
     * </pre>
     * 
     * @param objs The objects you wish to re-order.
     * @param orderBys The order bys.
     * @return The execution results.
     * @throws QueryParseException If the statement can be parsed, i.e. if any of the order by
     *                             columns is out of range or the order bys cannot be parsed.
     * @throws QueryExecutionException If the call to: {@link #execute(List)} fails.
     * @see #reorder(List,SortedMap)
     */
    public QueryResults reorder (List   objs,
				 String orderBys)
	                         throws QueryParseException,
					QueryExecutionException
    {

	String sql = "";

	if (!orderBys.toLowerCase ().startsWith ("order by"))
	{

	    sql = sql + " ORDER BY ";

	}
	    
	sql = sql + orderBys;

	BufferedReader sr = new BufferedReader (new StringReader (sql));

	JoSQLParser parser = new JoSQLParser (sr);

	List ors = null;

	try
	{

	     ors = parser.OrderBys ();

	} catch (Exception e) {

	    throw new QueryParseException ("Unable to parse order bys: " + 
					   orderBys,
					   e);

	}	

	this.orderBys = ors;

	this.initOrderByComparator ();

	// Execute the query.
	return this.execute (objs);

    }

    public void setClassLoader (ClassLoader cl)
    {

	this.classLoader = cl;

    }

    public ClassLoader getClassLoader ()
    {

	if (this.classLoader == null)
	{

	    // No custom classloader specified, use the one that loaded
	    // this class.
            this.classLoader = Thread.currentThread ().getContextClassLoader ();

            if (this.classLoader == null)
            {
                
                this.classLoader = this.getClass ().getClassLoader ();
                
            }

	}

	return this.classLoader;

    }

    public Class loadClass (String name)
	                    throws Exception
    {

	return this.getClassLoader ().loadClass (name);

    }

    /**
     * Parse the JoSQL query.
     *
     * @param q The query string.
     * @throws QueryParseException If the query cannot be parsed and/or {@link #init() inited}.
     */
    public void parse (String q)
	               throws QueryParseException
    {

	this.query = q;

	BufferedReader sr = new BufferedReader (new StringReader (q));

	long s = System.currentTimeMillis ();

	JoSQLParser parser = new JoSQLParser (sr);

	this.addTiming ("Time to init josql parser object",
			System.currentTimeMillis () - s);

	s = System.currentTimeMillis ();

	try
	{

	    parser.parseQuery (this);

	} catch (Exception e) {

	    throw new QueryParseException ("Unable to parse query: " + 
					   q,
					   e);

	}

	this.isParsed = true;

	this.addTiming ("Time to parse query into object form",
			System.currentTimeMillis () - s);

	// Init the query.
	this.init ();

    }

    private void initFromObjectClass ()
                                      throws QueryParseException
    {

        if (this.parent == null)
	{

	    if (!(this.from instanceof ConstantExpression))
	    {

		throw new QueryParseException ("The FROM clause of the outer-most Query must be a string that denotes a fully-qualified class name, expression: " +
					       this.from + 
					       " is not valid.");

	    }

	    // See if the class name is the special "null".
	    String cn = null;

	    try
	    {

		// Should be safe to use a null value here (especially since we know
		// how ConstantExpression works ;)
		cn = (String) this.from.getValue (null,
						  this);

	    } catch (Exception e) {
		
		throw new QueryParseException ("Unable to determine FROM clause of the outer-most Query from expression: " +
					       this.from +
					       ", note: this exception shouldn't be able to happen, so something has gone SERIOUSLY wrong!",
					       e);
		
	    }

	    if (!cn.equalsIgnoreCase ("null"))
	    {

		// Load the class that we are dealing with...
		try
		{
		    
		    this.objClass = this.loadClass (cn);

		} catch (Exception e) {
		    
		    throw new QueryParseException ("Unable to load FROM class: " + 
						   cn,
						   e);
		    
		}

	    }

	} 
        
    }

    public void init ()
                      throws QueryParseException
    {

	long s = System.currentTimeMillis ();

	// If we don't have a parent, then there must be an explicit class name.
	this.initFromObjectClass ();

	// Now if we have any columns, init those as well...
        this.initSelect ();

	// Now init the where clause (where possible)...
	if (this.where != null)
	{

	    this.where.init (this);

	}

	// Now init the having clause (where possible)...
	if (this.having != null)
	{

	    this.having.init (this);

	}

	// See if we have order by columns, if so init the comparator.
	this.initOrderByComparator ();

	// See if we have order by columns, if so init the comparator.
	if (this.groupBys != null)
	{

            this.initGroupBys ();

	}

        this.initGroupOrderBys ();

	if (this.groupByLimit != null)
	{

	    this.groupByLimit.init (this);

	}

	if (this.limit != null)
	{

	    this.limit.init (this);

	}

        this.initExecuteOn ();

	this.addTiming ("Time to init Query objects",
			System.currentTimeMillis () - s);

    }

    private void initSelect ()
                             throws QueryParseException
    {
        
        if (this.retObjs)
	{
            
            // Nothing to do.
            return;
            
        }
        
        int aic = 0;

        int si = this.cols.size ();

        this.aliases = new HashMap ();

        for (int i = 0; i < si; i++)
        {

            SelectItemExpression exp = (SelectItemExpression) this.cols.get (i);

            exp.init (this);

            if (exp.isAddItemsFromCollectionOrMap ())
            {

                aic++;

            }

            String alias = exp.getAlias ();

            if (alias != null)
            {

                this.aliases.put (alias,
                                  Integer.valueOf (i + 1));

            } 

            this.aliases.put ((i + 1) + "",
                              Integer.valueOf (i + 1));

        }

        if ((aic > 0)
            &&
            (aic != si)
           )
        {

            throw new QueryParseException ("If one or more SELECT clause columns is set to add the items returned from a: " +
                                           Map.class.getName () + 
                                           " or: " +
                                           java.util.Collection.class.getName () + 
                                           " then ALL columns must be marked to return the items as well.");

        }
        
    }

    private void initGroupBys ()
                               throws QueryParseException
    {
        
        this.grouper = new Grouper (this);

        int si = this.groupBys.size (); 

        for (int i = 0; i < si; i++)
        {

            OrderBy ob = (OrderBy) this.groupBys.get (i);

            // Get the expression...
            Expression e = (Expression) ob.getExpression ();

            if (e == null)
            {

                // Now expect an integer that refers to a column
                // in the select...
                int ci = ob.getIndex ();

                if (ci == 0)
                {

                    throw new QueryParseException ("Order by column indices should start at 1.");

                }

                if (this.retObjs)
                {

                    throw new QueryParseException ("Cannot sort on a select column index when the objects are to be returned.");

                }

                if (ci > this.cols.size ())
                {

                    throw new QueryParseException ("Invalid order by column index: " + 
                                                   ci + 
                                                   ", only: " +
                                                   this.cols.size () + 
                                                   " columns are selected to be returned.");

                }

                // Get the SelectItemExpression.
                SelectItemExpression sei = (SelectItemExpression) this.cols.get (ci - 1);

                // Get the expression...
                e = sei.getExpression ();

            } else {

                // Init the expression...
                e.init (this);

            }

            this.grouper.addExpression (e);

        }
        
    }

    private void initExecuteOn ()
                                throws QueryParseException
    {
    
    	if (this.executeOn == null)
	{
            
            return;
            
        }
    
        // Get the supported types.
        List allF = (List) this.executeOn.get (Query.ALL);

        if (allF != null)
        {

            // We have some, so init them...
            int si = allF.size ();

            for (int i = 0; i < si; i++)
            {

                AliasedExpression f = (AliasedExpression) allF.get (i);

                f.init (this);

            }

        }

        List resultsF = (List) this.executeOn.get (Query.RESULTS);

        if (resultsF != null)
        {

            // We have some, so init them...
            int si = resultsF.size ();

            for (int i = 0; i < si; i++)
            {

                AliasedExpression f = (AliasedExpression) resultsF.get (i);

                f.init (this);

            }

        }

        resultsF = (List) this.executeOn.get (Query.GROUP_BY_RESULTS);

        if (resultsF != null)
        {

            // We have some, so init them...
            int si = resultsF.size ();

            for (int i = 0; i < si; i++)
            {

                AliasedExpression f = (AliasedExpression) resultsF.get (i);

                f.init (this);

            }

        }
    
    }

    private void initGroupOrderBys ()
                                    throws QueryParseException
    {
        
        if (this.groupOrderBys == null)
	{
            
            // Nothing to do.
            return;
            
        }
        
        if (this.grouper == null)
        {

            throw new QueryParseException ("Group Order Bys are only valid if 1 or more Group By columns have been specified.");

        }

        // Here we "override" the from class because when dealing with the order bys the
        // current object will be a List, NOT the class defined in the FROM clause.
        Class c = this.objClass;

        this.objClass = List.class;

        // No caching, this may need to change in the future.
        this.groupOrderByComp = new GroupByExpressionComparator (this,
                                                                 this.userComparator,
                                                                 false);

        GroupByExpressionComparator lec = (GroupByExpressionComparator) this.groupOrderByComp;

        List grouperExps = this.grouper.getExpressions ();

        // Need to check the type of each order by, if we have
        // any "column" indexes check to see if they are an accessor...
        int si = this.groupOrderBys.size (); 

        for (int i = 0; i < si; i++)
        {

            OrderBy ob = (OrderBy) this.groupOrderBys.get (i);

            if (ob.getIndex () > -1)
            {

                int ci = ob.getIndex ();

                if (ci == 0)
                {

                    throw new QueryParseException ("Group Order by column indices should start at 1.");
                    
                }

                if (ci > grouperExps.size ())
                {

                    throw new QueryParseException ("Invalid Group Order By column index: " + 
                                                   ci + 
                                                   ", only: " +
                                                   grouperExps.size () + 
                                                   " Group By columns are selected to be returned.");
                    
                }

                lec.addSortItem (null,
                                 // Remember the -1!  Column indices start at 1 but
                                 // List indices start at 0 ;)
                                 ci - 1,
                                 ob.getType ());

                continue;

            }

            // Get the expression...
            Expression e = (Expression) ob.getExpression ();

            // See if the expression is a "direct" match for any of the
            // group by columns.
            boolean cont = true;

            for (int j = 0; j < grouperExps.size (); j++)
            {

                Expression exp = (Expression) grouperExps.get (j);
                
                if (e.equals (exp))
                {

                    // This is a match, add to the comparator.
                    lec.addSortItem (null,
                                     j,
                                     ob.getType ());
                    
                    cont = false;

                }

            }

            if (!cont)
            {

                continue;

            }

            if ((e instanceof Function)
                ||
                (e instanceof BindVariable)
                ||
                (e instanceof SaveValue)
               )
            {

                e.init (this);

                lec.addSortItem (e,
                                 -1,
                                 ob.getType ());

                continue;

            }

            // If we are here then we haven't been able to deal with the 
            // order by... so barf.
            throw new QueryParseException ("If the Group Order By: " +
                                           ob + 
                                           " is not a function, a bind variable or a save value then it must be present in the Group By list.");

        }

        // Restore the FROM object class.
        this.objClass = c;
        
    }

    /**
     * Set the "FROM" object class.  It is advised that you NEVER call this method, do so
     * at your own risk, dragons will swoop from the sky and crisp your innards if you do so!!!
     * Seriously though ;), this method should ONLY be called by those who know what they
     * are doing, whatever you think you know about how this method operates is irrelevant
     * which is why the dangers of calling this method are not documented...  
     * <p>
     * YOU HAVE BEEN WARNED!!!  NO BUGS WILL BE ACCEPTED THAT ARISE FROM THE CALLING OF
     * THIS METHOD!!!
     *
     * @param c The FROM class.
     */
    public void setFromObjectClass (Class c)
    {

	this.objClass = c;

    }

    public Class getFromObjectClass ()
    {

	return this.objClass;

    }

    public void removeBindVariableChangedListener (BindVariableChangedListener bvl)
    {

	List l = (List) this.listeners.get ("bvs");

	if (l == null)
	{

	    return;

	}

	l.remove (bvl);

    }

    public void addBindVariableChangedListener (BindVariableChangedListener bvl)
    {

	List l = (List) this.listeners.get ("bvs");

	if (l == null)
	{

	    l = new ArrayList ();

	    this.listeners.put ("bvs",
				l);

	}

	if (!l.contains (bvl))
	{

	    l.add (bvl);

	}

    }

    public void removeSaveValueChangedListener (SaveValueChangedListener svl)
    {

	List l = (List) this.listeners.get ("svs");

	if (l == null)
	{

	    return;

	}

	l.remove (svl);

    }

    public void addSaveValueChangedListener (SaveValueChangedListener svl)
    {

	List l = (List) this.listeners.get ("svs");

	if (l == null)
	{

	    l = new ArrayList ();

	    this.listeners.put ("svs",
				l);

	}

	if (!l.contains (svl))
	{

	    l.add (svl);

	}

    }

    public Map getAliases ()
    {

	return this.aliases;

    }

    /**
     * Return whether the query should return objects.
     *
     * @return <code>true</code> if the query should return objects.
     */
    public boolean isWantObjects ()
    {

	return this.retObjs;

    }

    /**
     * Set whether the query should return objects (use <code>true</code>).
     * Caution: Do NOT use unless you are sure about what you are doing!
     *
     * @param v Set to <code>true</code> to indicate that the query should return objects.
     */
    public void setWantObjects (boolean v)
    {

	this.retObjs = v;

    }

    /**
     * Get the character that represents a wildcard in LIKE searches.
     *
     * @return The char.
     */
    public char getWildcardCharacter ()
    {

	return this.wildcardChar;

    }

    /**
     * Set the character that represents a wildcard in LIKE searches.
     *
     * @param c The char.
     */
    public void setWildcardCharacter (char c)
    {

	this.wildcardChar = c;

    }

    /**
     * Set the object that represents the <a href="http://josql.sourceforge.net/limit-clause.html" target="_blank">limit clause</a>.
     * Caution: Do NOT use unless you are sure about what you are doing!
     *
     * @param l The object.
     */
    public void setLimit (Limit l)
    {

	this.limit = l;

    }

    /**
     * Get the object that represents the <a href="http://josql.sourceforge.net/limit-clause.html" target="_blank">limit clause</a>.
     * 
     * @return The object.
     */
    public Limit getLimit ()
    {

	return this.limit;

    }

    /**
     * Return whether this Query object has had a statement applied to it
     * and has been parsed.
     *
     * @return Whether the query is associated with a statement.
     */
    public boolean parsed ()
    {

	return this.isParsed;

    }

    /**
     * Indicate whether "distinct" results are required.
     *
     * @param v Set to <code>true</code> to make the results distinct.
     */
    public void setWantDistinctResults (boolean v)
    {

	this.distinctResults = v;

    }

    /**
     * Get the results of {@link #execute(java.util.List) executing} this query.
     *
     * @return The query results.
     */
    public QueryResults getQueryResults ()
    {

	return this.qd;

    }

    /**
     * Get the "order bys".  This will return a List of {@link OrderBy} objects.
     * This is generally only useful when you want to {@link #reorder(List,String)} 
     * the search and wish to get access to the textual representation of the order bys.
     * <p>
     * It is therefore possible to modify the orderbys in place, perhaps by using a different
     * expression or changing the direction (since the objects are not cloned before being
     * returned).  However do so at <b>YOUR OWN RISK</b>.  If you do so, then ensure you
     * call: {@link #setOrderByColumns(List)}, then: {@link #initOrderByComparator()}
     * before re-executing the statement, otherwise nothing will happen!
     *
     * @return The order bys.
     */
    public List getOrderByColumns ()
    {

	return new ArrayList (this.orderBys);

    }

    /**
     * Set the parent query.
     * Caution: Do NOT use unless you are sure about what you are doing!
     *
     * @param q The parent query.
     */
    public void setParent (Query q)
    {

	this.parent = q;

    }

    /**
     * Get the parent query.
     *
     * @return The query, will be <code>null</code> if there is no parent.
     */
    public Query getParent ()
    {

	return this.parent;

    }

    /**
     * Get the top level query if "this" is a sub-query, the query chain is traversed until
     * the top level query is found, i.e. when {@link #getParent()} returns null.
     *
     * @return The top level query, will be <code>null</code> if there is no parent.
     */
    public Query getTopLevelQuery ()
    {

	Query q = this;
	Query par = null;

	while (true)
	{

	    par = q.getParent ();

	    if (par == null)
	    {

		break;

	    }

	    q = par;

	}

	return q;

    }

    /**
     * Get a string version of this query suitable for debugging.  This will reconstruct the query
     * based on the objects it holds that represent the various clauses.
     *
     * @return The reconstructed query.
     */
    public String toString ()
    {

	StringBuffer buf = new StringBuffer ("SELECT ");
	
	if (this.distinctResults)
	{

	    buf.append ("DISTINCT ");

	}

	if (this.retObjs)
	{

	    buf.append ("*");

	} else {

	    for (int i = 0; i < this.cols.size (); i++)
	    {

		buf.append (" ");
		buf.append (this.cols.get (i));

		if (i < (this.cols.size () - 1))
		{

		    buf.append (",");

		}
	    
	    }

	}

	buf.append (" FROM ");	
	buf.append (this.from);

	if (this.where != null)
	{

	    buf.append (" WHERE ");
	
	    buf.append (this.where);

	}

	return buf.toString ();

    }

    public static QueryResults parseAndExec (String query,
                                             List   objs)
                                             throws QueryParseException,
                                                    QueryExecutionException
    {
        
        Query q = new Query ();
        q.parse (query);
        
        return q.execute (objs);
        
    }

}
