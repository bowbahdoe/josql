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
package dev.mccue.josql;

/**
 * The exception that is thrown during the <b>parse</b> and <b>init</b>
 * parts of the Query execution.
 */
public class QueryParseException extends Exception 
{

    public QueryParseException (String    message,
				Throwable cause)
    {

	super (message,
	       cause);

    }

    public QueryParseException (String message)
    {

	super (message);

    }

}
