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

import org.josql.expressions.Expression;

public class OrderBy
{

    public static final int ASC = 0;
    public static final int DESC = 1;

    private int type = -1;
    private int ci = -1;
    private Expression exp = null;

    public void setExpression (Expression exp)
    {

	this.exp = exp;

    }

    public int getIndex ()
    {

	return this.ci;

    }

    public Expression getExpression ()
    {

	return this.exp;

    }

    public void setIndex (int ci)
    {

	this.ci = ci;

    }

    public int getType ()
    {

	return this.type;

    }

    public void setType (int t)
    {

	this.type = t;

    }

    public String toString ()
    {

	StringBuffer b = new StringBuffer ();

	if (this.ci > -1)
	{

	    b.append (this.ci);

	} else {

	    b.append (this.exp);

	}

	if (this.type == OrderBy.ASC)
	{

	    b.append (" ASC");

	} 

	if (this.type == OrderBy.DESC)
	{

	    b.append (" DESC");

	}

	return b.toString ();

    }

}
