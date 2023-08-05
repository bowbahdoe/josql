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

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import java.text.SimpleDateFormat;

import org.josql.QueryExecutionException;

/**
 * Note: creating new instances of SimpleDateFormat objects are VERY costly over
 * large(ish) numbers of objects therefore a cache of objects is provided.
 */
public class ConversionFunctions extends AbstractFunctionHandler
{

    public static final String HANDLER_ID = "_internal_conversion";

    /**
     * Represents the {@link Calendar#MINUTE} field, is: <b>mi</b>.
     */
    public static final String MINUTE = "mi";

    /**
     * Represents the {@link Calendar#DATE} field, is: <b>d</b>.
     */
    public static final String DAY = "d";

    /**
     * Represents the {@link Calendar#YEAR} field, is: <b>y</b>.
     */
    public static final String YEAR = "y";

    /**
     * Represents the {@link Calendar#SECOND} field, is: <b>s</b>.
     */
    public static final String SECOND = "s";

    /**
     * Represents the {@link Calendar#HOUR_OF_DAY} field, is: <b>h</b>.
     */
    public static final String HOUR = "h";

    /**
     * Represents the {@link Calendar#MONTH} field, is: <b>m</b>.
     */
    public static final String MONTH = "m";

    /**
     * Represents the {@link Calendar#WEEK_OF_YEAR} field, is: <b>w</b>.
     */
    public static final String WEEK = "w";

    public static String DEFAULT_DATE_FORMAT_SPEC = "dd/MMM/yyyy";
    public static String DEFAULT_DATE_FORMAT_SPEC_2 = "dd-MMM-yyyy";
    public static String DEFAULT_DATE_FORMAT_SPEC_3 = "dd MMM yyyy";

    private Map sdfs = new HashMap ();
    private Calendar cal = Calendar.getInstance ();

    public ConversionFunctions ()
    {
        
	this.sdfs.put (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC,
		       new SimpleDateFormat (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC));
	this.sdfs.put (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC_2,
		       new SimpleDateFormat (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC_2));
	this.sdfs.put (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC_3,
		       new SimpleDateFormat (ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC_3));

    }

    /**
     * This method (function) will return the associated field from a
     * {@link Calendar} instance.  The <b>type</b> parm should be one of the 
     * constants from this class.  The default {@link java.util.TimeZone} is used.
     *
     * @param d If the type is a long value then it is first converted to a Date.
     *          Or a {@link Date} should be used.
     * @param type The type of field to get.
     * @return The field from {@link Calendar}.
     * @throws QueryExecutionException If the <b>d</b> parm isn't an instance of 
     *                                 {@link Long} or {@link Date}.
     */
    public int timeField (Object d,
		          String type)
	                  throws QueryExecutionException
    {

	if ((!(d instanceof Date))
	    &&
	    (!(d instanceof Long))
	   )
	{

	    throw new QueryExecutionException ("Value passed in is of type: " +
					       d.getClass ().getName () +
					       " only: " +
					       Long.class.getName () + 
					       " or: " +
					       Date.class.getName () + 
					       " are supported.");

	}

	Date date = null;

	if (d instanceof Long)
	{

	    date = new Date (((Long) d).longValue ());

	}

	if (d instanceof Date)
	{

	    date = (Date) d;

	}

	this.cal.setTime (date);

	type = type.toLowerCase ();

	if (type.equals (ConversionFunctions.SECOND))
	{

	    return this.cal.get (Calendar.SECOND);

	}

	if (type.equals (ConversionFunctions.MINUTE))
	{

	    return this.cal.get (Calendar.MINUTE);

	}

	if (type.equals (ConversionFunctions.HOUR))
	{

	    return this.cal.get (Calendar.HOUR_OF_DAY);

	}

	if (type.equals (ConversionFunctions.DAY))
	{

	    return this.cal.get (Calendar.DATE);

	}

	if (type.equals (ConversionFunctions.WEEK))
	{

	    return this.cal.get (Calendar.WEEK_OF_YEAR);

	}

	if (type.equals (ConversionFunctions.MONTH))
	{

	    return this.cal.get (Calendar.MONTH);

	}

	if (type.equals (ConversionFunctions.YEAR))
	{

	    return this.cal.get (Calendar.YEAR);

	}

	// None of the above...
	return -1;

    }

    public Date addTime (Date   d,
			 Double amount,
			 String type)
    {

	int a = amount.intValue ();

	long v = d.getTime ();

	if (type.equals (ConversionFunctions.SECOND))
	{

	    v += (a * 1000L);

	    return new Date (v);

	}

	if (type.equals (ConversionFunctions.MINUTE))
	{

	    v += (a * 60000L);

	    return new Date (v);

	}

	if (type.equals (ConversionFunctions.HOUR))
	{

	    v += (a * 3600000L);

	    return new Date (v);

	}

	if (type.equals (ConversionFunctions.DAY))
	{

	    v += (a * 24L * 3600000L);

	    return new Date (v);

	}

	if (type.equals (ConversionFunctions.WEEK))
	{

	    v += (a * 7L * 24L * 3600000L);

	    return new Date (v);

	}

	if (type.equals (ConversionFunctions.MONTH))
	{

	    // Need something a bit more sophisticated now...
	    GregorianCalendar gc = new GregorianCalendar ();
	    gc.setTime (d);
	    
	    gc.add (Calendar.MONTH,
		    a);

	    return gc.getTime ();

	}

	if (type.equals (ConversionFunctions.YEAR))
	{

	    // Need something a bit more sophisticated now...
	    GregorianCalendar gc = new GregorianCalendar ();
	    gc.setTime (d);
	    
	    gc.add (Calendar.YEAR,
		    a);

	    return gc.getTime ();

	}

	// None of the above...
	return d;

    }

    public Date toDate (Object value)
	                throws QueryExecutionException
    {

	if (value == null)
	{

	    return null;

	}

	if (value instanceof Number)
	{

	    return new Date (((Number) value).longValue ());

	} 

	if (value instanceof String) 
	{

	    return this.toDate ((String) value,
				null);

	} 

	if (value instanceof Date) 
	{

	    return (Date) value;

	}

	throw new QueryExecutionException ("Type: " + value.getClass ().getName () + " is not supported.");

    }

    public Date to_date (Object value)
	                 throws QueryExecutionException
    {

	return this.toDate (value);

    }

    public Date to_date (String value,
			 String spec)
	                 throws QueryExecutionException
    {

	return this.toDate (value,
			    spec);

    }

    public Date toDate (String value,
			String spec)
	                throws QueryExecutionException
    {

	if (spec == null)
	{

	    spec = ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC;

	}

	SimpleDateFormat df = (SimpleDateFormat) this.sdfs.get (spec);

	if (df == null)
	{

	    df = new SimpleDateFormat (spec);

	    this.sdfs.put (spec,
					  df);

	}

	try
	{

	    return df.parse (value);

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to parse date value: " + 
					       value + 
					       " using spec: " + 
					       spec,
					       e);

	}

    }

    public Long toMillis (Date d)
    {

	return new Long (d.getTime ());

    }

    public Long toDateMillis (String value,
			      String spec)
	                      throws QueryExecutionException
    {

	if (spec == null)
	{

	    spec = ConversionFunctions.DEFAULT_DATE_FORMAT_SPEC;

	}

	SimpleDateFormat df = (SimpleDateFormat) this.sdfs.get (spec);

	if (df == null)
	{

	    df = new SimpleDateFormat (spec);

	    this.sdfs.put (spec,
			   df);

	}

	try
	{

	    Date d = df.parse (value);

	    return new Long (d.getTime ());

	} catch (Exception e) {

	    throw new QueryExecutionException ("Unable to parse date value: " + 
					       value + 
					       " using spec: " + 
					       spec,
					       e);

	}

    }

    public String upper (Object o)
    {

	if (o == null)
	{

	    return null;

	}

	return o.toString ().toUpperCase ();

    }

    public String lower (Object o)
    {

	if (o == null)
	{

	    return null;

	}

	return o.toString ().toLowerCase ();

    }

    public String to_string (Object o)
    {
	
	return this.toString (o);

    }

    public String toString (Object o)
    {

	return o + "";

    }

    public Number to_number (Object o)
    {

	return this.toNumber (o);

    }

    public Number toNumber (Object o)
    {

	if (o == null)
	{

	    return null;

	}

	if (o instanceof String)
	{

	    // Try and parse as a double.
	    try
	    {

		return new Double ((String) o);

	    } catch (Exception e) {

		// Ignore?  Maybe have an option...

	    }

	}

	if (o instanceof Date)
	{

	    return new Double (((Date) o).getTime ());

	}

	if (!(o instanceof Number))
	{

	    return null;

	}

	return (Number) o;

    }

}
