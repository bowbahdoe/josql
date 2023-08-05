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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.gentlyweb.utils.GeneralFilter;
import com.gentlyweb.utils.FilterException;

public class MethodFilter 
{

    private Class c = null;
    private List ps = null;
    private String name = null;
    private int type = -1;
    private List modifiers = null;
    
    public MethodFilter ()
    {

    }

    public MethodFilter (Class c)
    {

	this.c = c;

    }

    public void setClass (Class c)
    {

	this.c = c;

    }
    
    public void addModifier (int m)
    {
	
	if (this.modifiers == null)
	{

	    this.modifiers = new ArrayList ();
	    
	}
	
	this.modifiers.add (Integer.valueOf (m));
	
    }
    
    public List filter ()
	                throws IllegalAccessException,
	                       InvocationTargetException,
	                       FilterException
    {

	Method[] ms = this.c.getMethods ();
	
	List res = new ArrayList ();
	
	if (ms.length == 0)
	{

	    return res;
	    
	}
	
	GeneralFilter gf = null;
	
	if (this.name != null)
	{

	    gf = new GeneralFilter (Method.class);
	    gf.addField ("name",
			 name,
			 this.type);
	    
	}
	
	for (int i = 0; i < ms.length; i++)
	{

	    Method m = ms[i];
	    
	    if (gf != null)
	    {

		if (!gf.accept (m))
		{

		    continue;
		    
		}
		
	    }
	    
	    // Now check that it has the correct modifiers...
	    if (!this.hasModifiers (m))
	    {

		continue;
		
	    }
	    
	    // Now check the parm types.
	    if (!this.hasParameters (m))
	    {

		continue;
		
	    }
	    
	    res.add (m);
	    
	}
	
	return res;
	
    }
    
    private boolean hasParameters (Method m)
    {

	if (this.ps == null)
	{

	    return true;
	    
	}
	
	// Allowing for widening of types here.
	Class[] mpt = m.getParameterTypes ();
	
	if (mpt.length != this.ps.size ())
	{

	    return false;

	}
	    
	for (int i = 0; i < mpt.length; i++)
	{

	    Class c = mpt[i];
	    
	    Class pc = (Class) this.ps.get (i);
	    
	    if (pc == null)
	    {

		// Skip this one because we can't compare...
		continue;
		
	    }
	    
	    if (!c.isAssignableFrom (pc))
	    {

		return false;
		
	    }
	    
	}
	
	return true;
	
    }

    private boolean hasModifiers (Method m)
    {

	if (this.modifiers != null)
	{

	    int mmods = m.getModifiers ();
	    
	    for (int i = 0; i < this.modifiers.size (); i++)
	    {

		int in = ((Integer) this.modifiers.get (i)).intValue ();
		
		if ((mmods & in) == 0)
		{

		    return false;
		    
		}
		
	    }
	    
	}
	
	return true;
	
    }
    
    public void setParameterTypes (List pt)
    {

	this.ps = pt;
	
    }
    
    public void setName (String n,
			 int    type)
    {
	
	this.name = n;
	this.type = type;
	
    }
    
}
