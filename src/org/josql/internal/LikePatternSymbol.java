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

public class LikePatternSymbol 
{
    
    public static final int SYMBOL_F = 0;
    public static final int SYMBOL_E = 1;
    public static final int SYMBOL_A = 2;
    public static final int SYMBOL_N = 3;       
    
    public String part = null;
    public int code = -1;
            
    public static LikePatternSymbol getSymbol (int code)
    {
        
        return new LikePatternSymbol (code);
        
    }

    public static LikePatternSymbol getSymbol (String part)
    {
        
        return new LikePatternSymbol (part);
        
    }

    public String toString ()
    {
        
        return "Part: " + this.part + ", code: " + this.code;
        
    }
            
    public LikePatternSymbol (int code)
    {
        
        this.code = code;
        
    }

    public LikePatternSymbol (String part)
    {
        
        this.part = part;
        
    }
    
    public boolean hashCode (Object o)
    {
        
        return ((LikePatternSymbol) o).code == this.code;
        
    }
    
}
