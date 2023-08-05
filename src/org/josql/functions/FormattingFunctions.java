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

import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import com.gentlyweb.utils.TimeDuration;
import com.gentlyweb.utils.Timing;
import com.gentlyweb.utils.Getter;

import org.josql.Query;
import org.josql.QueryExecutionException;

public class FormattingFunctions extends AbstractFunctionHandler
{

    public static final String HANDLER_ID = "_internal_formatting";

    public static String DEFAULT_DATE_FORMAT_SPEC = "dd/MMM/yyyy";
    public static String DEFAULT_DATE_TIME_FORMAT_SPEC = FormattingFunctions.DEFAULT_DATE_FORMAT_SPEC +
                    	                                 ", hh:mm:ss";
    public static String DEFAULT_DECIMAL_FORMAT_SPEC = "###,###,###.##";
    
    private SimpleDateFormat defSDF = new SimpleDateFormat (FormattingFunctions.DEFAULT_DATE_FORMAT_SPEC);
    private SimpleDateFormat defSDTF = new SimpleDateFormat (FormattingFunctions.DEFAULT_DATE_TIME_FORMAT_SPEC);

    public String formatTimeDuration (Object o)
	                              throws QueryExecutionException
    {

	if (o instanceof Number)
	{

	    return TimeDuration.getInstance (((Number) o).longValue ()).format ();

	}

	if (o instanceof Date)
	{

	    return TimeDuration.getInstance ((Date) o).format ();

	}

	if (o instanceof TimeDuration)
	{

	    return TimeDuration.getInstance ((TimeDuration) o).format ();

	}

	if (o instanceof Timing)
	{

	    return TimeDuration.getInstance ((Timing) o).format ();

	}

	throw new QueryExecutionException ("Type: " + 
					   o.getClass ().getName () + 
					   " not supported.");

    }

    public void setDefaultDateFormatSpec (String spec)
    {

	this.defSDF = new SimpleDateFormat (spec);

    }

    public String formatDate (Object o)
	                      throws QueryExecutionException
    {

	if (o == null)
	{

	    throw new QueryExecutionException ("Cannot format a null date.");

	}

	Date d = null;

	if (o instanceof Date)
	{

	    d = (Date) o;

	}

	if (o instanceof Number)
	{

	    d = new Date (((Number) o).longValue ());

	}

	// If this is a string try and parse the string first, basically convert
	// from one format to another.
	if (o instanceof String)
	{

	    d = ((ConversionFunctions) this.q.getFunctionHandler (ConversionFunctions.HANDLER_ID)).toDate ((String) o);

	}

	if (d == null)
	{

	    throw new QueryExecutionException ("Type: " + 
					       o.getClass ().getName () + 
					       " not supported.");

	}

	return this.defSDF.format (d);

    }

    public String formatDateTime (Object o)
	                          throws QueryExecutionException
    {

	if (o == null)
	{

	    throw new QueryExecutionException ("Cannot format a null date.");

	}

	Date d = null;

	if (o instanceof Date)
	{

	    d = (Date) o;

	}

	if (o instanceof Number)
	{

	    d = new Date (((Number) o).longValue ());

	}

	// If this is a string try and parse the string first, basically convert
	// from one format to another.
	if (o instanceof String)
	{

	    d = ((ConversionFunctions) this.q.getFunctionHandler (ConversionFunctions.HANDLER_ID)).toDate ((String) o);

	}

	if (d == null)
	{

	    throw new QueryExecutionException ("Type: " + 
					       o.getClass ().getName () + 
					       " not supported.");

	}

	return this.defSDTF.format (d);

    }

    public String formatDate (Query  q,
			      Object o,
                              Getter g,
   			      String spec,
			      String saveValueName)
	                      throws QueryExecutionException
    {

	if (g != null)
	{

	    try
	    {

		o = g.getValue (o);

	    } catch (Exception e) {

		throw new QueryExecutionException ("Unable to get value from accessor: " + 
						   g,
						   e);

	    }

	}

	if (o == null)
	{

	    return null + "";

	}

	Date d = null;

	if (o instanceof Date)
	{

	    d = (Date) o;

	}

	if (o instanceof Long)
	{

	    d = new Date (((Long) o).longValue ());

	}

	Object so = null;

	if (saveValueName != null)
	{

	    so = q.getSaveValue (saveValueName);

	}

	SimpleDateFormat df = null;
	
	if (so != null)
	{

	    df = (SimpleDateFormat) so;

	} else {
	    
	    if (spec == null)
	    {

		spec = FormattingFunctions.DEFAULT_DATE_FORMAT_SPEC;

	    }

	    df = new SimpleDateFormat (spec);
	    
	}

	return df.format (d);

    }

    public String formatNumber (Object n)
	                        throws QueryExecutionException
    {

	return this.formatNumber (this.q,
				  n,
				  null,
				  null);

    }

    public String formatNumber (Query  q,
				Object o,
				String spec,
				String saveValueName)
	                        throws QueryExecutionException
    {

	if (!(o instanceof Number))
	{

	    if (o == null)
	    {

		return "NaN (null)";

	    }

	    return "NaN (" + o.getClass ().getName () + ")";

	}

	if (o == null)
	{

	    return "0";

	}

	Object so = null;

	if (saveValueName != null)
	{

	    so = q.getSaveValue (saveValueName);

	}

	Number n = (Number) o;

	DecimalFormat df = null;
	
	if (so != null)
	{

	    if (!(so instanceof DecimalFormat))
	    {

		throw new QueryExecutionException ("Expected save value: \"" + 
						   saveValueName +
						   "\" object to be of type: " +
						   DecimalFormat.class.getName () + 
						   ", is: " + 
						   so.getClass ().getName ());

	    }

	    df = (DecimalFormat) so;

	} else {
	    
	    if (spec == null)
	    {

		spec = FormattingFunctions.DEFAULT_DECIMAL_FORMAT_SPEC;

	    }

	    df = new DecimalFormat (spec);
	    
	}

	return df.format (n.doubleValue ());

    }

    public String formatNumber (Query  q,
				Object o,
				Getter g,
				String spec,
			        String saveValueName)
	                        throws QueryExecutionException
    {

	try
	{

	    o = g.getValue (o);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to get value from accessor: " + 
					       g,
					       e);

	}

	return this.formatNumber (q,
				  o,
				  spec,
				  saveValueName);
	
    }

}
