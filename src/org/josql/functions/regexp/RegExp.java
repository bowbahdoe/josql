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
package org.josql.functions.regexp;

import org.josql.*;

/**
 * Defines a regular expression, use the {@link RegExpFactory#getDefaultInstance()} to 
 * get the "default" instance, and then just call: {@link #match(String,String)}.
 */
public interface RegExp
{

    public boolean match (String pattern,
			  String val)
	                  throws QueryExecutionException;

    public void init (Query  q)
	              throws QueryExecutionException;

}
