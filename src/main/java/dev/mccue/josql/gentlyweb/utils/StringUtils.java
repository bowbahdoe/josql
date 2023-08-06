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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import java.text.DecimalFormat;

public class StringUtils
{

    /**
     * Convert a string to a set of character entities.  
     *
     * @param str The string to convert.
     * @return The converted string.
     */
    public static String convertStringToCharEntities (String str)
    {

	DecimalFormat df = new DecimalFormat ("000");

	StringBuffer buf = new StringBuffer ();

	char chars[] = str.toCharArray ();

	for (int i = 0; i < chars.length; i++)
	{

	    buf.append ("&#");
	    buf.append (df.format ((int) chars[i]));
	    buf.append (';');

	}

	return buf.toString ();

    }

    /**
     * Given a chunk of text format it so that you return a new String
     * with the line length equal to that passed in. 
     *
     * @param text The text to format.
     * @param lineLength The line length to format the text to.
     * @return The formatted text.
     */
    public static String getTextAsFormattedLines (String text,
						  int    lineLength)
    {

	StringBuffer retdata = new StringBuffer ();

	List lines = new ArrayList ();

	String t = new String (text);

	while (t.length () > lineLength)
	{

	    // Chop up the description into lines close to lineLength...
	    // Chop off the first lineLength characters...
	    String s = t.substring (0, lineLength);

	    String rest = t.substring (lineLength);
		    
	    // Find the last instance of " ".
	    int lastInd = s.lastIndexOf (' ');
		    
	    // See if the last index if (lineLength - 1), if so then
	    // we have a clean break...
	    if (lastInd == (lineLength))
	    {
		
		retdata.append (s);
		retdata.append ('\n');

	    } else {
			
		// The last index is less so now
		// we need to check the rest to see
		// if that starts with a char other 
		// than " ", if so it's part of the
		// previous word...
		if (rest.charAt (0) != ' ')
		{
		    
		    // It is!
		    // Now get the rest of the word and append it
		    // to s...
		    int indexOfFirst = rest.indexOf (' ');
		    
		    String halfWord = rest.substring (0,
						      indexOfFirst);

		    // Substring...
		    s = s + halfWord;
		    
		    // Add s to the list of lines.
		    retdata.append (s);
		    retdata.append ('\n');
		    
		    rest = rest.substring (indexOfFirst + 1);
		    
		}
		
		rest = rest.trim ();

	    }

	    t = rest;

	}

	lines.add (t.trim ());
	retdata.append (t.trim ());
	retdata.append ('\n');

	return retdata.toString ();

    }

    /**
     * Indicate whether the String value has at least one
     * character in the range specified.
     *
     * @param value The value to check.
     * @param start The starting character of the range.
     * @param end The end character of the range.
     * @return Return <code>true</code> if it has at least
     *         one character in the range, <code>false</code>
     *         otherwise.
     */
    public static boolean hasCharInRange (String value,
					  char   start,
					  char   end)
    {

	if (value == null)
	{

	    return false;

	}

	char[] chars = value.toCharArray ();

	boolean found = false;

	for (int i = 0; i < chars.length; i++)
	{

	    if ((chars[i] >= start)
		&&
		(chars[i] <= end)
	       )
	    {

		found = true;

	    }

	}

	return found;

    }

    /**
     * A method to chop up a String on another multi-character String and
     * then return the results in a List.  If you have a single character 
     * String then you should use java.util.StringTokenizer.
     *
     * @param str The String to chop up.
     * @param token The token to look for.  This should be just a plain
     *              String, this method doesn't support regular expressions.
     * @return A List of all the Strings found, if token is not present then
     *         return a single sized List with the str at 0.
     */
    public static List tokenizeString (String str,
				       String token)
    {

	List retData = new ArrayList ();

	String s = new String (str);

	int start = s.indexOf (token);

	if (start > -1)
	{

	    while (start > -1)
	    {

		String tok = s.substring (0,
					  start);

		int end = start + token.length ();

		s = s.substring (end);

		if (!tok.equals (""))
		{
		
		    retData.add (tok);

		}

		start = s.indexOf (token);

	    }

	    if (!s.equals (""))
	    {

		retData.add (s);

	    }

	} else {

	    retData.add (s);

	}

	return retData;

    }

    /**
     * Determine whether the specified string contains a value, where
     * having a value means:
     * <code>
     *   (string != null) && (!string.equals (""))
     * </code>
     *
     * @return <code>true</code> if the string has a value, <code>false</code>
     *         otherwise.
     */
    public static boolean hasValue (String v)
    {

	if (v == null)
	{

	    return false;

	}

	if (v.equals (""))
	{

	    return false;

	}

	return true;

    }

    /**
     * Given a Map of String/String (or Object/Object, they will be converted to Strings before
     * replacement) replace all instances of each one in the specified text.
     * <br /><br />
     * There is no guarantee about the order in which the replacements will occur, if the
     * ordering is important then you should impose the order by using a sorted map.
     * <br /><br />
     * The original text will not be affected.
     *
     * @param text The text to perform the replacements on.
     * @param map The map of String to find in the text and replace with the value of the map (String).
     * @param caseSensitive Indicate whether we should consider case when searching for the strings.
     * @return A new String representing the text with all the replacements made.  If any of the keys/values in
     *         the map are null then we return "".
     */
    public static String replaceAllStrings (String  text,
					    Map     map,
					    boolean caseSensitive)
    {

	String newText = new String (text);

	Iterator iter = map.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Object k = iter.next ();
	    String t = k.toString ();
	    String v = (String) map.get (k);

	    newText = StringUtils.replaceString (newText,
						 t,
						 v,
						 caseSensitive);

	}

	return newText;

    }

    /**
     * Given a Map of String/String (or Object/Object, they will be converted to Strings before
     * replacement) replace all instances of each one in the specified text.
     * <br /><br />
     * There is no guarantee about the order in which the replacements will occur, if the
     * ordering is important then you should impose the order by using a sorted map.
     * <br /><br />
     * The original text will not be affected.
     *
     * @param text The text to perform the replacements on.
     * @param map The map of String to find in the text and replace with the value of the map (String).
     * @return A new String representing the text with all the replacements made.  If any of the keys/values in
     *         the map are null then we return "".
     */
    public static String replaceAllStrings (String  text,
					    Map     map)
    {

	String newText = text;

	Iterator iter = map.keySet ().iterator ();

	while (iter.hasNext ())
	{

	    Object k = iter.next ();
	    String t = (String) k;
	    String v = (String) map.get (k);

	    newText = StringUtils.replaceString (newText,
						 t,
						 v);

	}

	return newText;

    }

    /**
     * Replace all instances of the specified string in the text given.
     * This will not recursively replace, if it finds something to replace
     * it replaces and then goes from the place it found last.
     * <p>
     * It should be noted that we take copies of text and str prior 
     * to performing the replacements.
     * </p>
     *
     * @param text The text to perform the replacement on.
     * @param str The string to find in the text.
     * @param replace The string to replace "str" with.
     * @param caseSensitive Indicate whether we should consider case
     *                      when searching for the string.
     * @return A new String representing the replaced text (if any 
     *         were made).  
     * @throws NullPointerExceptio If any of the arguments are null indicating which one is at fault.
     */
    public static String replaceString (String  text,
					String  str,
					String  replace,
					boolean caseSensitive)
	                                throws  NullPointerException
    {

	if (text == null)
	{

	    throw new NullPointerException ("text parm.");

	}

	if (str == null)
	{

	    throw new NullPointerException ("str parm.");

	}

	if (replace == null)
	{

	    throw new NullPointerException ("replace parm.");

	}

	// First convert text to a StringBuffer.
	StringBuffer buf = new StringBuffer (text);

	String newText = new String (text);

	String s = new String (str);

	if (!caseSensitive)
	{

	    s = s.toLowerCase ();
	    newText = newText.toLowerCase ();

	}

	int index = newText.indexOf (s);

	while (index != -1)
	{

	    buf.replace (index,
			 index + s.length (),
			 replace);

	    newText = buf.toString ();

	    if (!caseSensitive)
	    {

		newText = newText.toLowerCase ();

	    }

	    index = newText.indexOf (s,
				     index + replace.length ());

	}

	return buf.toString ();

    }    

    /**
     * Replace all instances of the specified string in the text given.
     * This will not recursively replace, if it finds something to replace
     * it replaces and then goes from the place it found last.
     *
     * @param text The text to perform the replacement on.
     * @param str The string to find in the text.
     * @param replace The string to replace "str" with.
     * @return A new String representing the replaced text (if any 
     *         were made).  
     * @throws NullPointerException If any of the arguments are null indicating which one is at fault.
     */
    public static String replaceString (String  text,
					String  str,
					String  replace)
	                                throws  NullPointerException
    {

	if (text == null)
	{

	    throw new NullPointerException ("text parm (arg 1).");

	}

	if (str == null)
	{

	    throw new NullPointerException ("str parm (arg 2).");

	}

	if (replace == null)
	{

	    throw new NullPointerException ("replace parm (arg3).");

	}

	// First convert text to a StringBuffer.
	StringBuffer buf = new StringBuffer (text);

	int index = buf.indexOf (str);

	while (index != -1)
	{

	    buf.replace (index,
			 index + str.length (),
			 replace);

	    index = buf.indexOf (str,
				 index + replace.length ());

	}

	return buf.toString ();

    }    

    /**
     * Indicate whether the String value has ALL of it's characters
     * in the specified range of characters.  Make sure that 
     * <b>end</b> is "greater" than <b>start</b>.  If value is null return false.
     * <p>
     * Note: if you set <b>start</b> == <b>end</b> then you check to see if
     * a String is made up of the same character, not useful all the time but
     * it is sometimes.
     * </p>
     *
     * @param value The value to check.
     * @param start The starting character of the range.
     * @param end The end character of the range.
     * @return Whether all the characters are in the range.
     *         Return <code>true</code> if they are, <code>false</code>
     *         otherwise.
     */
    public static boolean areAllCharsInRange (String value,
					      char   start,
					      char   end)
    {

	if (start > end)
	{

	    return false;

	}

	if (value == null)
	{

	    return false;

	}

	char[] chars = value.toCharArray ();

	for (int i = 0; i < chars.length; i++)
	{

	    if ((chars[i] < start)
		||
		(chars[i] > end)
	       )
	    {

		return false;

	    }

	}

	return true;

    }

    /**
     * Given a particular String, remove any instances of the given
     * character from it and return a new String.
     *
     * @param str The String to search for the removals.
     * @param c The character to remove.
     * @return A String of the new String.
     */
    public static String removeChar (String str,
				     char   c)
    {

	if (str == null)
	{

	    return str;

	}

	StringBuffer buf = new StringBuffer ();

	char[] chars = str.toCharArray ();

	for (int i = 0; i < chars.length; i++)
	{

	    if (chars[i] != c)
	    {

		buf.append (chars[i]);

	    }

	}

	return buf.toString ();

    }

}
