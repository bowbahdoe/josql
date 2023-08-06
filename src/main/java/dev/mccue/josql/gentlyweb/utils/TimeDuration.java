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

import java.util.Date;

public class TimeDuration
{

    public static final String DEFAULT_FORMAT_SPEC = "DDDd HHh mmm sss SSSms";

    private static final String DDD = "DDD";
    private static final String SSS = "SSS";
    private static final String ss = "ss";
    private static final String HH = "HH";
    private static final String mm = "mm";

    private static final int milli = 1;
    private static final int sec = TimeDuration.milli * 1000;
    private static final int min = TimeDuration.sec * 60;
    private static final int hour = TimeDuration.min * 60;
    private static final int day = TimeDuration.hour * 24;

    private int days = 0;
    private int hours = 0;
    private int mins = 0;
    private int secs = 0;
    private int millis = 0;

    public TimeDuration (int days,
			 int hours,
			 int mins,
			 int secs)
    {

	this.days = days;
	this.hours = hours;
	this.mins = mins;
	this.secs = secs;

	this.sanitize ();

    }

    public TimeDuration (Date from,
			 Date to)
    {

	long f = 0;
	long t = 0;

	if (from != null)
	{

	    f = from.getTime ();

	}

	if (to != null)
	{

	    t = to.getTime ();

	}

	this.init (t - f);

    }

    public TimeDuration (Date d)
    {

	this.init (d.getTime ());

    }

    public void init (long millis)
    {

	this.days = (int) (millis / TimeDuration.day);

	long rem = millis - (this.days * TimeDuration.day);

	this.hours = (int) (rem / TimeDuration.hour);
	rem = rem - (this.hours * TimeDuration.hour);

	this.mins = (int) (rem / TimeDuration.min);
	rem = rem - (this.mins * TimeDuration.min);

	this.secs = (int) (rem / TimeDuration.sec);
	this.millis = (int) (rem - (this.secs * TimeDuration.sec));

	this.sanitize ();

    }

    public TimeDuration (long millis)
    {

	this.init (millis);

    }

    public TimeDuration (TimeDuration t)
    {

	this.init (t);

    }

    public TimeDuration (int days,
			 int hours,
			 int mins,
			 int secs,
			 int millis)
    {

	this.days = days;
	this.hours = hours;
	this.mins = mins;
	this.secs = secs;
	this.millis = millis;

	this.sanitize ();

    }

    private void sanitize ()
    {

	if (this.days < 0)
	{

	    this.days = 0;

	}

	if (this.hours < 0)
	{

	    this.hours = 0;

	}

	if (this.hours > 23)
	{

	    this.hours = 23;

	}

	if (this.mins < 0)
	{

	    this.mins = 0;

	}

	if (this.mins > 59)
	{

	    this.mins = 59;

	}

	if (this.secs < 0)
	{

	    this.secs = 0;

	}

	if (this.secs > 59)
	{

	    this.secs = 59;

	}

	if (this.millis < 0)
	{

	    this.millis = 0;

	}

	if (this.millis > 999)
	{

	    this.millis = 999;

	}

    }

    public void init (TimeDuration t)
    {

	this.days = t.getDays ();

	this.hours = t.getHours ();

	this.mins = t.getMins ();

	this.secs = t.getSecs ();

	this.millis = t.getMillis ();

	this.sanitize ();

    }

    public void subtract (TimeDuration t)
    {

	// Bit of a cheat this but it's the easiest way!
	TimeDuration tt = new TimeDuration (t.rollUpToMillis () -
					    this.rollUpToMillis ());

	this.init (tt);

    }

    public void add (TimeDuration t)
    {

	// Bit of a cheat this but it's the easiest way!
	TimeDuration tt = new TimeDuration (t.rollUpToMillis () + 
					    this.rollUpToMillis ());

	this.init (tt);

    }

    public static TimeDuration getInstance (TimeDuration t)
    {

	return new TimeDuration (t);

    }

    public static TimeDuration getInstance (Timing t)
    {

	return new TimeDuration (t.getDuration ());

    }

    public static TimeDuration getInstance (Date d)
    {

	return TimeDuration.getInstance (d.getTime ());

    }

    public static TimeDuration getInstance (long millis)
    {

	return new TimeDuration (millis);

    }

    public Date getAsDate ()
    {

	return new Date (this.rollUpToMillis ());

    }

    public long rollUpToMillis ()
    {

	return ((long) this.days * (long) TimeDuration.day)
	    +
	    (this.hours * TimeDuration.hour)
	    +
	    (this.mins * TimeDuration.min)
	    +
	    (this.secs * TimeDuration.sec)
	    +
	    (this.millis * TimeDuration.milli);

    }

    public void setMillis (int m)
    {

	this.millis = m;

	this.sanitize ();

    }

    public int getMillis ()
    {

	return this.millis;

    }

    public void setSecs (int s)
    {

	this.secs = s;

	this.sanitize ();

    }

    public int getSecs ()
    {

	return this.secs;

    }

    public void setMins (int m)
    {

	this.mins = m;

	this.sanitize ();

    }

    public int getMins ()
    {

	return this.mins;

    }

    public void setHours (int h)
    {

	this.hours = h;

	this.sanitize ();

    }

    public int getHours ()
    {

	return this.hours;

    }

    public void setDays (int d)
    {

	this.days = d;

    }

    public int getDays ()
    {

	return this.days;

    }

    public String format ()
    {

	return this.format (TimeDuration.DEFAULT_FORMAT_SPEC);

    }

    public String format (String spec)
    {

	String s = spec;

	if (spec.indexOf (TimeDuration.DDD) != -1)
	{

	    s = StringUtils.replaceString (s,
					   TimeDuration.DDD,
					   String.valueOf (this.days));

	}

	if (spec.indexOf (TimeDuration.SSS) != -1)
	{

	    s = StringUtils.replaceString (s,
					   TimeDuration.SSS,
					   String.valueOf (this.millis));

	}

	if (spec.indexOf (TimeDuration.ss) != -1)
	{

	    s = StringUtils.replaceString (s,
					   TimeDuration.ss,
					   String.valueOf (this.secs));

	}

	if (spec.indexOf (TimeDuration.HH) != -1)
	{

	    s = StringUtils.replaceString (s,
					   TimeDuration.HH,
					   String.valueOf (this.hours));

	}

	if (spec.indexOf (TimeDuration.mm) != -1)
	{

	    s = StringUtils.replaceString (s,
					   TimeDuration.mm,
					   String.valueOf (this.mins));

	}

	return s;

    }

}
