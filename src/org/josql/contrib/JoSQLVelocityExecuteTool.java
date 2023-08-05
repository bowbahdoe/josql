/*
 * Copyright 2004-2008 Gary Bentley 
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
package org.josql.contrib;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;

//import org.apache.velocity.tools.config.ValidScope; 
//import org.apache.velocity.tools.config.DefaultKey; 

import org.josql.Query;

// Annotations to tell the Tools framework that the default key should be "josql" and that the tool
// should be in the "request" scope.
//@DefaultKey(value="josql")
//@ValidScope(value="request")

/**
 * A custom tool for use with Velocity.
 * <p>
 * Usage:
 * <p>
 * A typical usage may be:
 * <p>
 * <pre>
 *   #foreach ($v in $josql.filter ("name LIKE '/opt/%' order by dir, name", $files))
 * </pre>
 */
public class JoSQLVelocityExecuteTool 
{
    
    private Exception excep = null;
    
    public JoSQLVelocityExecuteTool ()
    {

    }

    public Exception getException ()
    {
        
        return this.excep;
        
    }

    public void clearException ()
    {
        
        this.excep = null;
        
    }

    public List filter (String     st,
                        Collection items)
    {
        
        // Build up our statement.
        Iterator iter = items.iterator ();
                   
        Object o = null;
            
        // Get the object, determine the class, build the query.
        while (iter.hasNext ())
        {
                
            o = iter.next ();
                
            if (o != null)
            {
                    
                break;
                    
            }

        }
        
        // Default to object.
        Class c = Object.class;
        
        if (o != null)
        {
            
            c = o.getClass ();
            
        }

        String t = "SELECT * FROM " + c.getName ();

        String sst = st.trim ().toLowerCase ();

        if ((!sst.startsWith ("order by"))
            &&
            (!sst.startsWith ("limit"))
           )
        {
            
            t = " WHERE ";
            
        }
        
        t = t + st;
        
        Query q = new Query ();
        
        try
        {
        
            q.parse (t);
            
        } catch (Exception e) {
            
            this.excep = e;
            
            // As per the tool contract.
            return null;
            
        }
                
        try
        {
                
            return q.execute (items).getResults ();
        
        } catch (Exception e) {
            
            this.excep = e;
            
            // As per the tool contract.
            return null;
            
        }
                                        
    }

}
