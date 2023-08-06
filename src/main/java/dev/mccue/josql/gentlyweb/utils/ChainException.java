/*
 * Copyright 2006 - Gary Bentley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.mccue.josql.gentlyweb.utils;

import java.io.PrintWriter;
import java.io.IOException;

public class ChainException extends Exception
{

    private Throwable exp = null;

    public ChainException (String message)
    {

	super (message);

    }

    public ChainException (String    message,
		           Throwable exp)
    {

	super (message,
	       exp);

	this.exp = exp;

    }

    public Throwable getException ()
    {

	return this.exp;

    }

    public void printInnerExceptionChain (PrintWriter out)
	                                  throws      IOException
    {

	// Print out the inner exceptions if they exist...
	if (this.exp != null)
	{

	    out.println ("Next exception in chain:");
	    exp.printStackTrace (out);
	    out.println ();

	    if (exp instanceof ChainException) 
	    {

		ChainException ee = (ChainException) exp;

		ee.printInnerExceptionChain (out);

	    } else {

		exp.printStackTrace (out);

	    }

	}

    }

}
