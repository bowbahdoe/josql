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

public class ColumnReference
{

    private int index = -1;
    private String name = null;

    public void setName (String n)
    {

	this.name = n;

    }

    public String getName ()
    {

	return this.name;

    }

    public int getIndex ()
    {

	return this.index;

    }

    public void setIndex (int ind)
    {

	this.index = ind;

    }

}
