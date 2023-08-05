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

import org.josql.Query;

/**
 * Defines a basic function handler.
 * A function handler object does NOT have to extend this class, this is here purely
 * as an easy way to have the required {@link Query} object be available for 
 * sub-classes.
 */
public abstract class AbstractFunctionHandler implements FunctionHandler
{

    protected Query q = null;

    /**
     * Set the Query object that the function handler should use.
     *
     * @param q The Query object.
     */
    public void setQuery (Query q)
    {

	this.q = q;

    }

}
