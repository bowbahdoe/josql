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
package dev.mccue.josql.functions;

import dev.mccue.josql.Query;

/**
 * Defines an interface that custom objects can use to indicate that they can 
 * store a reference to the Query object.
 * A function handler object does NOT have to implement this class, this is here purely
 * as a convenience for developers so that they can easily get a reference to the Query
 * object, since the Query object will call the {@link #setQuery(Query)} method.
 */
public interface FunctionHandler
{

    /**
     * Set the Query object that the function handler should use.
     *
     * @param q The Query object.
     */
    public void setQuery (Query q);

}
