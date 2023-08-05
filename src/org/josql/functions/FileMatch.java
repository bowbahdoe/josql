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

import java.io.File;

/**
 * This class represents the match of a String in a file.
 */ 
public class FileMatch
{

    private File f = null;
    private int line = 0;
    private int col = 0;
    private String str = null;
    private String oLine = null;

    public FileMatch (File   f,
		      int    line,
		      int    col,
		      String str,
		      String oLine)
    {

	this.f = f;
	this.line = line;
	this.col = col;
	this.str = str;
	this.oLine = oLine;

    }

    public String toString ()
    {

	return this.f.getPath () + "[" + this.line + "," + this.col + "] \"" + this.str + "\" " + this.oLine;

    }

    public String getOriginalLine ()
    {

	return this.oLine;

    }

    public String getString ()
    {

	return this.str;

    }

    public int getColumn ()
    {

	return this.col;

    }

    public int getLine ()
    {

	return this.line;

    }

    public File getFile ()
    {

	return this.f;

    }

}
