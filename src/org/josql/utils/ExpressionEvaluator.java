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
package org.josql.utils;

import java.util.List;
import java.util.ArrayList;

import org.josql.Query;
import org.josql.QueryParseException;
import org.josql.QueryExecutionException;

import org.josql.expressions.Expression;

/**
 * This class can be used as a convenient way of evaluating an expression
 * without having to use the Query object itself.
 * <p>
 * In this way you can easily evaluate JoSQL expressions against objects.
 * <p>
 * Usage:
 * <p>
 * Use the static methods to evaluate the expression in one call, for instance to
 * find out details about a file:
 * <p>
 * String exp = "path + ', size: ' + formatNumber(length) + ', last modified: ' + formatDate(lastModified)<br />
 * String details = ExpressionEvaluator.getValue (exp, new File ('/home/me/myfile.txt'));
 */
public class ExpressionEvaluator
{
    
    private Expression where = null;
    private Query q = null;
    
    /**
     * Create a new expression evaluator.
     *
     * @param exp The expression to be evaluated.
     * @param cl The class of the object(s) that the expression will be
     *           evaluated against.
     * @throws QueryParseException If the expression cannot be parsed.
     */
    public ExpressionEvaluator (String exp,
                                Class  cl)
                                throws QueryParseException
    {
        
        this (exp,
              cl,
              null);
        
    }

    /**
     * Create a new expression evaluator.
     *
     * @param exp The expression to be evaluated.
     * @param cl The class of the object(s) that the expression will be
     *           evaluated against.
     * @param fhs A list of function handlers that contain functions that will
     *            be used by the expression, can be null.
     * @throws QueryParseException If the expression cannot be parsed.
     */
    public ExpressionEvaluator (String exp,
                                Class  cl,
                                List   fhs)
                                throws QueryParseException
    {
        
        Query q = new Query ();

        if (fhs != null)
        {
            
            for (int i = 0; i < fhs.size (); i++)
            {
                
                q.addFunctionHandler (fhs.get (i));
                
            }
            
        }
        
        q.parse ("SELECT * FROM " + cl.getName () + " WHERE " + exp);
        
        this.q = q;
        
        this.where = q.getWhereClause ();
        
    }

    /**
     * Get the query associated with the expression, use this to setup
     * bind variables, function handlers and so on, which of course must
     * be setup prior to evaluating the expression.
     *
     * @return The Query object.
     */
    public Query getQuery ()
    {
        
        return this.q;
        
    }

    /**
     * Evaluate the expression against the object passed in.
     *
     * @param o The object to evaluate the expression against.
     * @return The value of calling Expression.isTrue (Query, Object).
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public boolean isTrue (Object o)
                           throws QueryExecutionException
    {
       
       if (o == null)
       {
        
            throw new NullPointerException ("Object passed in is null.");
        
       }
              
       return this.where.isTrue (o,
                                 this.q);
        
    }

    /**
     * Evaluate the expression against the list of objects passed in and
     * return the value.
     *
     * @param l The list of objects to evaluate the expression against.
     * @return The values gained when evaluating the expression against all
     *         the objects in the list.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public List getValues (List   l)
                           throws QueryExecutionException
    {
        
        if (l == null)
        {
            
            throw new NullPointerException ("List is null");
            
        }

        int s = l.size ();
        
        List ret = new ArrayList (s);
                
        for (int i = s - 1; i > -1; i--)
        {
            
            ret.set (i,
                     this.getValue (l.get (i)));
            
        }
                
        return ret;
        
    }

    /**
     * Evaluate the expression against the object passed in and return the
     * value.
     *
     * @param o The object to evaluate the expression against.
     * @return The value gained when evaluating the expression against 
     *         the object.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public Object getValue (Object o)
                            throws QueryExecutionException
    {
        
        return this.where.getValue (o,
                                    this.q);
        
    }
    
    /**
     * Evaluate the expression against the object passed in.
     *
     * @param exp A string representation of the expression to evaluate.
     * @param o The object to evaluate the expression against.
     * @return The value of calling Expression.isTrue (Query, Object).
     * @throws QueryParseException If the expression cannot be parsed.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public static boolean isTrue (String exp,
                                  Object o)
                                  throws QueryParseException,
                                         QueryExecutionException
    {
       
       if (o == null)
       {
        
            throw new NullPointerException ("Object passed in is null.");
        
       }
       
       ExpressionEvaluator ee = new ExpressionEvaluator (exp,
                                                         o.getClass ());
       
       return ee.isTrue (o);
        
    }
    
    /**
     * Evaluate the expression against the list of objects passed in and
     * return the value.
     *
     * @param exp A string representation of the expression to evaluate.
     * @param l The list of objects to evaluate the expression against.
     * @return The values gained when evaluating the expression against all
     *         the objects in the list.
     * @throws QueryParseException If the expression cannot be parsed.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public static List getValues (String exp,
                                  List   l)
                                  throws QueryParseException,
                                         QueryExecutionException
    {
        
        if (l == null)
        {
            
            throw new NullPointerException ("List is null");
            
        }
        
        if (l.size () == 0)
        {
            
            return new ArrayList ();
            
        }
        
        Class c = null;
        
        for (int i = 0; i < l.size (); i++)
        {
            
            Object o = l.get (i);
            
            if (o != null)
            {
                
                c = o.getClass ();
                
                if (c != null)
                {
                    
                    break;
                    
                }
                
            }
            
        }
        
        if (c == null)
        {
            
            throw new NullPointerException ("All objects in the list are null");
            
        }
        
        ExpressionEvaluator ee = new ExpressionEvaluator (exp,
                                                          c);
        
        return ee.getValues (l);
        
    }
    
    /**
     * Evaluate the expression against the object passed in and return the
     * value.
     *
     * @param exp A string representation of the expression to evaluate.
     * @param o The object to evaluate the expression against.
     * @return The value gained when evaluating the expression against 
     *         the object.
     * @throws QueryParseException If the expression cannot be parsed.
     * @throws QueryExecutionException If the expression cannot be executed.
     */
    public static Object getValue (String exp,
                                   Object o)
                                   throws QueryParseException,
                                          QueryExecutionException
    {

       if (o == null)
       {
        
            throw new NullPointerException ("Object passed in is null.");
        
       }
        
        ExpressionEvaluator ee = new ExpressionEvaluator (exp,
                                                          o.getClass ());
        
        return ee.getValue (o);
        
    }
    
}