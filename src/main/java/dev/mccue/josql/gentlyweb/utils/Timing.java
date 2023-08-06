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

public class Timing
{

    private long start = 0;
    private long end = 0;

    public Timing ()
    {

	this.start = System.currentTimeMillis ();

    }

    public Timing (Date s)
    {

	this.start = s.getTime ();

    }

    public Timing (long s)
    {

	this.start = s;

    }

    public TimeDuration getDuration ()
    {

	return new TimeDuration (this.end - this.start);

    }

    public void restart (Date d)
    {

	this.start = d.getTime ();
	this.end = 0;

    }

    public void restart ()
    {

	this.start = System.currentTimeMillis ();
	this.end = 0;

    }

    public void stop (long s)
    {

	this.end = s;

    }

    public void stop (Date d)
    {

	this.end = d.getTime ();

    }

    public void stop ()
    {

	this.end = System.currentTimeMillis ();

    }

}
