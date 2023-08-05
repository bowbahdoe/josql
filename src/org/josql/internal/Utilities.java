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
package org.josql.internal;

import java.util.*;

import java.lang.reflect.Method;

import com.gentlyweb.utils.StringUtils;

public class Utilities
{

    public static LikePatternSymbol F = LikePatternSymbol.getSymbol (LikePatternSymbol.SYMBOL_F);
    public static LikePatternSymbol A = LikePatternSymbol.getSymbol (LikePatternSymbol.SYMBOL_A);        
    public static LikePatternSymbol E = LikePatternSymbol.getSymbol (LikePatternSymbol.SYMBOL_E);
    public static LikePatternSymbol N = LikePatternSymbol.getSymbol (LikePatternSymbol.SYMBOL_N); 

    public static final int GT = 0;
    public static final int GTE = 1;
    public static final int LT = 2;
    public static final int LTE = 3;
    public static final int EQ = 4;

    private static Map pCNames = new HashMap ();
    private static Map primNames = new HashMap ();
    private static Map primCls = new HashMap ();

    private static Comparator objComp = null;

    static
    {

	Utilities.pCNames.put ("double",
			       "");
	Utilities.pCNames.put (Double.class.getName (),
			       "");
	Utilities.pCNames.put ("int",
			       "");
	Utilities.pCNames.put (Integer.class.getName (),
			       "");
	Utilities.pCNames.put ("float",
			       "");
	Utilities.pCNames.put (Float.class.getName (),
			       "");
	Utilities.pCNames.put ("long",
			       "");
	Utilities.pCNames.put (Long.class.getName (),
			       "");
	Utilities.pCNames.put ("short",
			       "");
	Utilities.pCNames.put (Short.class.getName (),
			       "");
	Utilities.pCNames.put ("byte",
			       "");
	Utilities.pCNames.put (Byte.class.getName (),
			       "");
	Utilities.pCNames.put (Number.class.getName (),
			       "");

	Utilities.primNames.put (Double.TYPE.getName (),
				 Double.class.getName ());
	Utilities.primNames.put (Double.class.getName (),
				 Double.TYPE.getName ());
	Utilities.primNames.put (Integer.TYPE.getName (),
				 Integer.class.getName ());
	Utilities.primNames.put (Integer.class.getName (),
				 Integer.TYPE.getName ());
	Utilities.primNames.put (Float.TYPE.getName (),
				 Float.class.getName ());
	Utilities.primNames.put (Float.class.getName (),
				 Float.TYPE.getName ());
	Utilities.primNames.put (Long.TYPE.getName (),
				 Long.class.getName ());
	Utilities.primNames.put (Long.class.getName (),
				 Long.TYPE.getName ());
	Utilities.primNames.put (Short.TYPE.getName (),
				 Short.class.getName ());
	Utilities.primNames.put (Short.class.getName (),
				 Short.TYPE.getName ());
	Utilities.primNames.put (Byte.TYPE.getName (),
				 Byte.class.getName ());
	Utilities.primNames.put (Byte.class.getName (),
				 Byte.TYPE.getName ());
	Utilities.primNames.put (Character.TYPE.getName (),
				 Character.class.getName ());
	Utilities.primNames.put (Character.class.getName (),
				 Character.TYPE.getName ());
	Utilities.primNames.put (Boolean.TYPE.getName (),
				 Boolean.class.getName ());
	Utilities.primNames.put (Boolean.class.getName (),
				 Boolean.TYPE.getName ());

	Utilities.primCls.put (Double.TYPE.getName (),
			       Double.TYPE);
	Utilities.primCls.put (Double.class.getName (),
			       Double.TYPE);

	Utilities.primCls.put (Integer.TYPE.getName (),
			       Integer.TYPE);
	Utilities.primCls.put (Integer.class.getName (),
			       Integer.TYPE);

	Utilities.primCls.put (Float.TYPE.getName (),
			       Float.TYPE);
	Utilities.primCls.put (Float.class.getName (),
			       Float.TYPE);

	Utilities.primCls.put (Long.TYPE.getName (),
			       Long.TYPE);
	Utilities.primCls.put (Long.class.getName (),
			       Long.TYPE);

	Utilities.primCls.put (Short.TYPE.getName (),
			       Short.TYPE);
	Utilities.primCls.put (Short.class.getName (),
			       Short.TYPE);

	Utilities.primCls.put (Byte.TYPE.getName (),
			       Byte.TYPE);
	Utilities.primCls.put (Byte.class.getName (),
			       Byte.TYPE);

	Utilities.primCls.put (Character.TYPE.getName (),
			       Character.TYPE);
	Utilities.primCls.put (Character.class.getName (),
			       Character.TYPE);

	Utilities.primCls.put (Boolean.TYPE.getName (),
			       Boolean.TYPE);
	Utilities.primCls.put (Boolean.class.getName (),
			       Boolean.TYPE);

    }

    public static void setObjectComparator (Comparator c)
    {
        
        Utilities.objComp = c;
        
    }

    public static Class getObjectClass (Class c)
    {

	if (c.isPrimitive ())
	{

	    String n = c.getName ();

	    if (n.equals (Boolean.TYPE.getName ()))
	    {

		return Boolean.class;

	    }

	    if (n.equals (Long.TYPE.getName ()))
	    {

		return Long.class;

	    }

	    if (n.equals (Short.TYPE.getName ()))
	    {

		return Short.class;

	    }

	    if (n.equals (Integer.TYPE.getName ()))
	    {

		return Integer.class;

	    }

	    if (n.equals (Double.TYPE.getName ()))
	    {

		return Double.class;

	    }

	    if (n.equals (Character.TYPE.getName ()))
	    {

		return Character.class;

	    }

	    if (n.equals (Float.TYPE.getName ()))
	    {

		return Float.class;

	    }

	    if (n.equals (Byte.TYPE.getName ()))
	    {

		return Byte.class;

	    }

	}

	return c;

    }

    public static Class getPrimitiveClass (Class c)
    {

	return (Class) Utilities.primCls.get (c.getName ());

    }

    public static boolean isPrimitiveClass (Class c)
    {

	return Utilities.primNames.containsKey (c.getName ());

    }

    public static boolean getResult (boolean v,
				     boolean n)
    {

	if (v)
	{

	    if (n)
	    {

		return false;

	    }

	    return true;

	}

	if (n)
	{

	    return true;

	}

	return false;

    }

    /**
     * This method encapsulates our "matching" mechanism.  It handles collections correctly
     * and will match according to the combination of <b>igoreCase</b>, <b>type</b> and 
     * <b>not</b>.
     *
     * Note: this method deliberately throws no exceptions, and it tries hard to ensure that
     * ClassCastExceptions and NullPointerExceptions are also NOT thrown, in other words in
     * theory it should be possible to compare ANY object against ANY other in safety.  However
     * if the objects DO NOT implement comparable (and are type compatible, i.e. can <b>r</b>
     * be assigned to <b>l</b>) then a string comparison is performed so you may be at the mercy
     * of the {@link String#toString()} method of each object.  In general this is not a problem
     * but beware of potential gotchas such as:
     * <code>
     *   SELECT *
     *   FROM   MyObject
     *   WHERE  20 >= (SELECT value
     *                 FROM   myList)
     * </code>
     * <pre>
     * It's tempting to think here that the query will return the correct result, however this is
     * NOT true because the sub-query will return a List of Lists (with "value" as the single
     * item in each list of the sub-query results).  To make the query above work as expected you
     * should use:
     * <code>
     *   SELECT *
     *   FROM   MyObject
     *   // The value will be returned instead of an enclosing list.
     *   WHERE  20 >= (SELECT [*] value 
     *                 FROM   myList)
     * </code>
     *
     * @param l The LHS object.
     * @param r The RHS object.
     * @param ignoreCase Whether to ignore the case or not, note: setting this to <code>true</code>
     *                   will force a string comparison (the object to compare against will be
     *                   converted to a string via: {@link String#toString()} and then "lowered".
     * @param type The type of comparison to make, should be one of:
     *             {@link Utilities#GT}, {@link Utilities#GTE}, {@link Utilities#LT}
     *             {@link Utilities#LTE}, {@link Utilities#EQ}.
     * @param not Whether the result should be reversed.
     * @return <code>true</code> if <b>l</b> matches <b>r</b> given the rules defined by the 
     *         other parms, <code>false</code> otherwise.
     */
    public static boolean matches (Object  l,
				   Object  r,
				   boolean ignoreCase,
				   int     type,
				   boolean not)
    {

	if (l instanceof Collection)
	{

	    if (r instanceof Collection)
	    {

		Collection cl = (Collection) l;
		Collection cr = (Collection) r;

		boolean lisra = (cl instanceof List);
		boolean risra = (cr instanceof List);

		List rl = null;

		if (risra)
		{

		    rl = (List) cr;

		}

		int rs = cr.size () - 1;

		// Need to now ensure that each item in the left is >, <, >=, <=
		// than every item in the right... sigh... ;)
		if (lisra)
		{

		    List ll = (List) cl;

		    // Can go backwards for slightly faster access.
		    int s = ll.size () - 1;

		    for (int i = s; i > -1; i--)
		    {

		        l = Utilities.lowerValue (ll.get (i),
						  ignoreCase);

			if (risra)
			{

			    for (int j = rs; j > -1; j--)
			    {

				r = Utilities.lowerValue (rl.get (j),
							  ignoreCase);

				if (!Utilities.compare2 (l,
							 r,
							 type,
							 not))
				{

				    return false;

				}				

			    }

			} else {

			    Iterator it = cr.iterator ();

			    while (it.hasNext ())
			    {

				r = Utilities.lowerValue (it.next (),
							  ignoreCase);

				if (!Utilities.compare2 (l,
							 r,
							 type,
							 not))
				{

				    return false;

				}

			    }		    

			}

		    }

		} else {

		    Iterator it = cl.iterator ();

		    while (it.hasNext ())
		    {

			l = Utilities.lowerValue (it.next (),
						  ignoreCase);

			if (risra)
			{

			    for (int j = rs; j > -1; j--)
			    {

				r = Utilities.lowerValue (rl.get (j),
							  ignoreCase);

				if (!Utilities.compare2 (l,
							 r,
							 type,
							 not))
				{

				    return false;

				}

			    }

			} else {

			    Iterator itr = cr.iterator ();

			    while (itr.hasNext ())
			    {

				r = Utilities.lowerValue (itr.next (),
							  ignoreCase);

				if (!Utilities.compare2 (l,
							 r,
							 type,
							 not))
				{

				    return false;

				}		

			    }

			}    

		    }

		}

		// If we are here then it means that ALL values in the RHS
		// collection match the condition with the LHS values
		// so we return true.
		return true;

	    } else {

		// Here we need to check to ensure that ALL values in the LHS
		// collection match the condition for the single RHS value.
		Collection cl = (Collection) l;

		r = Utilities.lowerValue (r,
					  ignoreCase);

		if (cl instanceof List)
		{

		    List ll = (List) cl;

		    int ls = cl.size () - 1;

		    for (int i = ls; i > -1; i--)
		    {

			l = Utilities.lowerValue (ll.get (i),
						  ignoreCase);

			if (!Utilities.compare2 (l,
						 r,
						 type,
						 not))
			{

			    return false;

			}		

		    }

		} else {

		    Iterator cli = cl.iterator ();

		    while (cli.hasNext ())
		    {

			l = Utilities.lowerValue (cli.next (),
						  ignoreCase);

			if (!Utilities.compare2 (l,
						 r,
						 type,
						 not))
			{

			    return false;

			}		

		    }    

		}

		// All values in the LHS match the condition for the RHS
		// value.
		return true;

	    }

	} else {

	    // See if the RHS is a collection.
	    if (r instanceof Collection)
	    {

		l = Utilities.lowerValue (l,
					  ignoreCase);

		Collection cr = (Collection) r;

		if (cr instanceof List)
		{

		    List rl = (List) cr;

		    int rs = rl.size () - 1;

		    for (int i = rs; i > -1; i--)
		    {

			r = Utilities.lowerValue (rl.get (i),
						  ignoreCase);

			if (!Utilities.compare2 (l,
						 r,
						 type,
						 not))
			{

			    return false;

			}		    

		    }

		} else {

		    Iterator cri = cr.iterator ();

		    while (cri.hasNext ())
		    {

			r = Utilities.lowerValue (cri.next (),
						  ignoreCase);

			if (!Utilities.compare2 (l,
						 r,
						 type,
						 not))
			{

			    return false;

			}		

		    }    

		}

		// All values in the RHS match the condition for the LHS
		// value.
		return true;		

	    }

	}

	// Just vanilla objects, so compare.
	// Note: we perform the "lower" at this point because (from above)
	// we want to ensure that the lower is only ever performed once and
	// it's not until now that we can be sure that the objects are not collections.
	return Utilities.compare2 (Utilities.lowerValue (l,
							 ignoreCase),
				   Utilities.lowerValue (r,
							 ignoreCase),
				   type,
				   not);

    }

    private static Object lowerValue (Object  o,
				      boolean ignoreCase)
    {

	if (ignoreCase)
	{

	    o = o.toString ();
	    
	    if (o != null)
	    {

		o = ((String) o).toLowerCase ();
		
	    }

	}

	return o;

    }

    private static boolean compare2 (Object  l,
				     Object  r,
				     int     type,
				     boolean not)
    {

	int c = Utilities.compare (l,
				   r);
	
	// Use the direct values here for speed.
	
	// Check for >
	if ((type == 0)
	    &&
	    (c < 1)
	   )
	{

	    // The LHS value is equal to or less than
	    // the RHS value, but we expect >, so can safely 
	    // return false.
	    if (not)
	    {

		return true;

	    }

	    return false;
	    
	}
	
	// Check for >= or =
	if (((type == 1)
	     ||
	     (type == 4)
	    )
	    &&
	    (c < 0)
	   )
	{

	    // The RHS value is less than the LHS value
	    // but we expect >=, so can safely return false.
	    if (not)
	    {

		return true;

	    }

	    return false;
	    
	}

	// Check for <
	if ((type == 2)
	    &&
	    (c > -1)
	   )
	{

	    // The RHS value is greater than or equal to
	    // the LHS value but we expect <, so can safely return
	    // false.
	    if (not)
	    {

		return true;

	    }

	    return false;

	}

	// Check for <=
	if (((type == 3)
	     ||
	     (type == 4)
	    )
	    &&
	    (c > 0)
	   )
	{

	    // The RHS value is greater than the LHS value
	    // but we expect <=, so can safely return false.
	    if (not)
	    {

		return true;

	    }

	    return false;
	    
	}	

	if (not)
	{

	    return false;

	}

	return true;

    }

    public static int compare (Object o1,
			       Object o2)
    {

        if (Utilities.objComp != null)
        {
            
            return Utilities.objComp.compare (o1,
                                              o2);
            
        }

	if ((o1 == null)
	    &&
	    (o2 == null)
	   )
	{

	    return 0;

	}

        if ((o1 == null)
            &&
            (o2 != null)
           )
        {
            
            return 1;
            
        }

        if ((o1 != null)
            &&
            (o2 == null)
           )
        {
            
            return -1;
            
        }

	if ((o1 instanceof Number)
	    &&
	    (o2 instanceof Number)
	   )
	{

	    return Utilities.getDoubleObject (o1).compareTo (Utilities.getDoubleObject (o2));

	}

	if ((o1 instanceof Comparable)
	    &&
	    (o2 instanceof Comparable)
	    &&
	    (o1.getClass ().isAssignableFrom (o2.getClass ()))
	   )
	{

	    return ((Comparable) o1).compareTo ((Comparable) o2);

	}

	// Force a string comparison.
	String s1 = o1.toString ();
	String s2 = o2.toString ();

	return s1.compareTo (s2);

    }

    public static boolean isGTEquals (Object o1,
				      Object o2)
    {

	return Utilities.matches (o1,
				  o2,
				  false,
				  Utilities.GTE,
				  false);

    }

    public static boolean isLTEquals (Object o1,
				      Object o2)
    {

	return Utilities.matches (o1,
				  o2,
				  false,
				  Utilities.LTE,
				  false);

    }

    public static boolean isEquals (Object o1,
				    Object o2)
    {

	return Utilities.compare (o1,
				  o2) == 0;

    }

    public static Double getDoubleObject (Object o)
    {

	return new Double (Utilities.getDouble (o));

    }

    public static double getDouble (Object o)
    {

	return ((Number) o).doubleValue ();

    }

    public static boolean isNumber (Object o)
    {

	if (o == null)
	{

	    return false;

	}

	return Utilities.pCNames.containsKey (o.getClass ().getName ());

    }

    public static boolean isNumber (Class c)
    {

	return Utilities.pCNames.containsKey (c.getName ());

    }

    public static String formatSignature (String  name,
					  Class[] ps)
    {

	StringBuffer buf = new StringBuffer (name);
	buf.append ("(");
	
	if (ps != null)
	{

	    for (int i = 0; i < ps.length; i++)
	    {
		    
		buf.append (ps[i].getName ());
		
		if (i < (ps.length - 1))
		{

		    buf.append (",");
		    
		}
		
	    }
	    
	}
	
	buf.append (")");
	
	return buf.toString ();

    }

    public static boolean matchLikePattern (List    p,
					    Object  lhs,
					    boolean not,
					    boolean ignoreCase)
    {

	if (lhs instanceof Collection)
	{

	    return Utilities.matchLikePattern (p,
					       (Collection) lhs,
					       not,
					       ignoreCase);

	}

	boolean v = Utilities.matchLikePattern (p,
						lhs,
						ignoreCase);

	if ((!v)
	    &&
	    (not)
	   )
	{

	    return true;

	}

	if ((v)
	    &&
	    (not)
	   )
	{

	    return false;

	}

	return v;

    }

    public static boolean matchLikePattern (List       p,
					    Collection lhs,
					    boolean    not,
					    boolean    ignoreCase)
    {

	if (lhs instanceof List)
	{

	    int s = lhs.size () - 1;

	    List l = (List) lhs;

	    for (int i = s; i > -1; i--)
	    {

		Object o = l.get (i);

		if (!Utilities.matchLikePattern (p,
						 o,
						 ignoreCase))
		{

		    if (not)
		    {

			return true;

		    }

		    return false;

		}

	    }

	    if (not)
	    {

		return false;

	    }

	    return true;

	}

	Iterator iter = lhs.iterator ();

	while (iter.hasNext ())
	{

	    Object o = iter.next ();

	    if (!Utilities.matchLikePattern (p,
					     o,
					     ignoreCase))
	    {

		if (not)
		{

		    return true;

		}

		return false;

	    }

	}
	
	if (not)
	{

	    return false;

	}

	return true;

    }

    public static boolean matchLikePattern (List    p,
					    Object  o,
					    boolean ignoreCase)
    {

	if (o == null)
	{

	    return false;

	}

	String st = o.toString ();
	
	if (ignoreCase)
	{

	    st = st.toLowerCase ();

	}

	return Utilities.matchLikePattern (p,
					   st);

    }

    public static int getLastMatch (String value,
                                     String search,
                                     int    start)
    {

        int last = -1;

        while ((start = value.indexOf (search, start)) != -1)
        {

            last = start;
            start += search.length ();

        };

        return last;
        
    }

    public static boolean matchLikePattern (List    p,
					    String  value)
    {

	if (value == null)
	{

	    return false;

	}

        boolean accept = true;

        LikePatternSymbol c = null;
        LikePatternSymbol pm = null;        

        int currPos = 0;
        int cmdPos = 0;

	int s = p.size ();

        while (cmdPos < s)
        {

            c = (LikePatternSymbol) p.get (cmdPos);
            pm = (LikePatternSymbol) p.get (cmdPos + 1);

            if (c.equals (Utilities.F))
            {

                // if we are to find 'anything'
                // then we are done
                if (pm.equals (Utilities.A))
                {

                    break;

                }

                // otherwise search for the param
                // from the curr pos
                
                int nextPos = Utilities.getLastMatch (value,
                                                      pm.part,
                                                      currPos);
                                                   
/*
                int nextPos = value.indexOf (pm.part,
					     currPos);
*/        
                if (nextPos >= 0)
                {

                    // found it
                    currPos = nextPos + pm.part.length ();

                } else {

                    accept = false;
		    break;

                }

            } else {

                if (c.equals (Utilities.E))
                {

                    // if we are to expect 'nothing'
                    // then we MUST be at the end of the string
                    if (pm.equals (Utilities.N))
                    {

                        if (currPos != value.length ())
                        {

                            accept = false;

                        }

                        // since we expect nothing else,
                        // we must finish here
                        break;

                    } else { 

                        // otherwise, check if the expected string
                        // is at our current position
                        
                        int nextPos = Utilities.getLastMatch (value,
                                                              pm.part,
                                                              currPos);
                        
/*
                        int nextPos = value.lastIndexOf (pm.part,
						     currPos);
*/
                        if (nextPos != currPos)
                        {

                            accept = false;
                            break;

                        }

                        // if we've made it this far, then we've
                        // found what we're looking for
                        currPos += pm.part.length ();

                    }

                }

            }

            cmdPos += 2;
        }

	return accept;

    }

    public static boolean matchLikePattern (List    p,
					    String  value,
					    boolean not)
    {

	boolean accept = Utilities.matchLikePattern (p,
						     value);

	if (not)
	{

	    return !accept;

	}

	return accept;

    }

    public static List getLikePattern (String value,
				       String wildcard)
    {

	List p = new ArrayList ();

        StringTokenizer t = new StringTokenizer (value, 
						 wildcard, 
						 true);

        String tok = null;

        while (t.hasMoreTokens ())
        {

            tok = t.nextToken ();

            if (tok.equals (wildcard))
            {

                p.add (Utilities.F);
    
                if (t.hasMoreTokens ())
                {

                    tok = t.nextToken ();
                                        
                    p.add (LikePatternSymbol.getSymbol (tok));

                } else {

                    p.add (Utilities.A);

                }

            } else {

                p.add (Utilities.E);
                p.add (LikePatternSymbol.getSymbol (tok));

            }

        }

        if ((tok == null)
	    ||
	    (!tok.equals (wildcard))
	   )	    
        {

            p.add (Utilities.E);
            p.add (Utilities.N);

        }

	return p;

    }

    public static String stripQuotes (String s)
    {

	if (s == null)
	{

	    return s;

	}

	if (((s.charAt (0) == '\'')
	     &&
	     (s.charAt (s.length () - 1) == '\'')
	    )
	    ||
	    ((s.charAt (0) == '"')
	     &&
	     (s.charAt (s.length () - 1) == '"')
	    )
	   )
	{ 

	    return s.substring (1,
				s.length () - 1);

	}

	return s;

    }

    public static void getMethods (Class  c,
				   String name,
				   int    mods,
				   List   ms)
    {

	if (c == null)
	{

	    return;

	}

	Method[] meths = c.getDeclaredMethods ();

	for (int i = 0; i < meths.length; i++)
	{

	    Method m = meths[i];

	    if ((m.getName ().equals (name))
		&&
		((m.getModifiers () & mods) == mods)
	       )
	    {

		if (!ms.contains (m))
		{

		    // This is one.
		    ms.add (m);

		}

	    }

	}	

	// Now get all the super-classes.
	Class sup = c.getSuperclass ();

	if (sup != null)
	{

	    Utilities.getMethods (sup,
				  name,
				  mods,
				  ms);

	}

	// Now work up through the super-classes/interfaces.
	Class[] ints = c.getInterfaces ();

	for (int i = 0; i < ints.length; i++)
	{

	    Class in = ints[i];

	    Utilities.getMethods (in,
				  name,
				  mods,
				  ms);

	}

    }

    public static int matchMethodArgs (Class[] args,
				       Class[] compArgs)
    {

	if ((compArgs == null)
	    &&
	    (args == null)
	   )
	{

	    return 2;

	}

	if ((compArgs == null)
	    &&
	    (args.length == 0)
	   )
	{

	    return 2;

	}

	if ((compArgs == null)
	    &&
	    (args.length > 0)
	   )
	{

	    return 0;

	}

	if (args.length != compArgs.length)
	{

	    return 0;

	}

	// The score here helps in argument resolution, a more specific argument
	// match (that is NOT expression in the method args) will score higher and
	// thus is a better match.
	int score = 0;

	for (int i = 0; i < args.length; i++)
	{

	    Class c = args[i];

	    // See if the arg is object, which means "I can accept any type".
	    if (c.getClass ().getName ().equals (Object.class.getName ()))
	    {

		score += 1;

		continue;

	    }

	    Class cc = compArgs[i];

	    if (cc == null)
	    {

		// Can't match this arg.
		continue;

	    } else {

		if (c.isAssignableFrom (cc))
		{

		    score += 2;

		    continue;

		}

	    }

	    if ((Utilities.isNumber (cc))
		&&
		(Utilities.isNumber (c))
	       )
	    {

		score += 1;

		// This matches...
		continue;

	    }

	    if ((Utilities.isPrimitiveClass (c))
		&&
		(Utilities.isPrimitiveClass (cc))
	       )
	    {

		// It is a primitive class as well, so now see if they are compatible.
		if (Utilities.getPrimitiveClass (c).isAssignableFrom (Utilities.getPrimitiveClass (cc)))
		{

		    score += 1;

		    // They are assignable...
		    continue;

		}

	    }

	    // See if the type is an object... this "may" mean
	    // that we can match and it may not, it will be determined at runtime.
	    if (cc.getName ().equals (Object.class.getName ()))
	    {

		score += 1;

		continue;

	    }

	    // If we are here then we can't match this arg type...
	    // No point checking any further...
	    return 0;

	}

	// All args can be matched.
	return score;

    } 

    public static Object[] convertArgs (Object[] args,
					Class[]  argTypes)
    {

	if (args == null)
	{

	    return args;

	}

	Object[] nargs = new Object [args.length];

	for (int i = 0; i < argTypes.length; i++)
	{

	    if (Utilities.isNumber (argTypes[i]))
	    {

		Class c = Utilities.getObjectClass (argTypes[i]);

		// This arg is a number, need to now convert to the type in the args.
		Number arg = (Number) args[i];

		if (Double.class.isAssignableFrom (c))
		{

		    nargs[i] = arg;

		    continue;

		}

		if (Short.class.isAssignableFrom (c))
		{

		    nargs[i] = new Short (arg.shortValue ());

		    continue;

		}

		if (Integer.class.isAssignableFrom (c))
		{

		    nargs[i] = Integer.valueOf (arg.intValue ());

		    continue;

		}

		if (Long.class.isAssignableFrom (c))
		{

		    nargs[i] = new Long (arg.longValue ());

		    continue;

		}

		if (Float.class.isAssignableFrom (c))
		{

		    nargs[i] = new Float (arg.floatValue ());

		    continue;

		}

		if (Byte.class.isAssignableFrom (c))
		{

		    nargs[i] = new Byte (arg.byteValue ());

		    continue;

		}

	    } else {

		nargs[i] = args[i];

	    }

	}

	return nargs;

    }

    public static String unescapeString (String v)
    {
        
        v = StringUtils.replaceString (v,
                                 "\\\\",
                                 "\\");
        v = StringUtils.replaceString (v,
                                 "\\\"",
                                 "\"");
        v = StringUtils.replaceString (v,
                                 "\\'",
                                 "'");
                                 
        return v;
        
    }

}
