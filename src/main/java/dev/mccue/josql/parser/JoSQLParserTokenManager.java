/* Generated By:JavaCC: Do not edit this line. JoSQLParserTokenManager.java */
package dev.mccue.josql.parser;

public class JoSQLParserTokenManager implements JoSQLParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x1fffffffe0L) != 0L)
         {
            jjmatchedKind = 38;
            return 27;
         }
         if ((active1 & 0x100L) != 0L)
            return 19;
         if ((active0 & 0x2000000000L) != 0L || (active1 & 0x80L) != 0L)
            return 13;
         if ((active0 & 0x8000000000000L) != 0L)
            return 9;
         return -1;
      case 1:
         if ((active0 & 0x1fff7bb800L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 38;
               jjmatchedPos = 1;
            }
            return 27;
         }
         if ((active0 & 0x8447e0L) != 0L)
            return 27;
         return -1;
      case 2:
         if ((active0 & 0x1010007800L) != 0L)
            return 27;
         if ((active0 & 0xfefff8000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 2;
            return 27;
         }
         return -1;
      case 3:
         if ((active0 & 0xfcff40000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 3;
            return 27;
         }
         if ((active0 & 0x200b8000L) != 0L)
            return 27;
         return -1;
      case 4:
         if ((active0 & 0xa40f00000L) != 0L)
            return 27;
         if ((active0 & 0x58f040000L) != 0L)
         {
            if (jjmatchedPos != 4)
            {
               jjmatchedKind = 38;
               jjmatchedPos = 4;
            }
            return 27;
         }
         return -1;
      case 5:
         if ((active0 & 0xa00000000L) != 0L)
         {
            if (jjmatchedPos != 5)
            {
               jjmatchedKind = 38;
               jjmatchedPos = 5;
            }
            return 1;
         }
         if ((active0 & 0x403040000L) != 0L)
            return 27;
         if ((active0 & 0x18c000000L) != 0L)
         {
            if (jjmatchedPos != 5)
            {
               jjmatchedKind = 38;
               jjmatchedPos = 5;
            }
            return 27;
         }
         return -1;
      case 6:
         if ((active0 & 0x8000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 6;
            return 27;
         }
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 6;
            return 1;
         }
         if ((active0 & 0x184000000L) != 0L)
            return 27;
         return -1;
      case 7:
         if ((active0 & 0x8000000L) != 0L)
            return 27;
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 7;
            return 1;
         }
         return -1;
      case 8:
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 8;
            return 1;
         }
         return -1;
      case 9:
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 9;
            return 1;
         }
         return -1;
      case 10:
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 10;
            return 1;
         }
         return -1;
      case 11:
         if ((active0 & 0xe00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 11;
            return 1;
         }
         return -1;
      case 12:
         if ((active0 & 0x200000000L) != 0L)
            return 1;
         if ((active0 & 0xc00000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 12;
            return 1;
         }
         return -1;
      case 13:
         if ((active0 & 0x400000000L) != 0L)
            return 1;
         if ((active0 & 0x800000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 13;
            return 1;
         }
         return -1;
      case 14:
         if ((active0 & 0x800000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 14;
            return 1;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0, long active1)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x20L);
      case 36:
         return jjStopAtPos(0, 62);
      case 37:
         return jjStopAtPos(0, 73);
      case 40:
         return jjStopAtPos(0, 58);
      case 41:
         return jjStopAtPos(0, 59);
      case 42:
         return jjStopAtPos(0, 55);
      case 43:
         return jjStopAtPos(0, 70);
      case 44:
         return jjStopAtPos(0, 54);
      case 45:
         jjmatchedKind = 71;
         return jjMoveStringLiteralDfa1_0(0x2000000000L, 0x0L);
      case 46:
         return jjStartNfaWithStates_0(0, 51, 9);
      case 47:
         return jjStartNfaWithStates_0(0, 72, 19);
      case 58:
         jjmatchedKind = 48;
         return jjMoveStringLiteralDfa1_0(0x2000000000000L, 0x0L);
      case 59:
         return jjStopAtPos(0, 53);
      case 60:
         jjmatchedKind = 64;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x18L);
      case 61:
         return jjStopAtPos(0, 65);
      case 62:
         jjmatchedKind = 63;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x4L);
      case 63:
         return jjStopAtPos(0, 50);
      case 64:
         return jjStopAtPos(0, 52);
      case 91:
         return jjStopAtPos(0, 56);
      case 93:
         return jjStopAtPos(0, 57);
      case 65:
      case 97:
         return jjMoveStringLiteralDfa1_0(0x5820L, 0x0L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa1_0(0x4000040L, 0x0L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa1_0(0x8008000L, 0x0L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa1_0(0x80000000L, 0x0L);
      case 70:
      case 102:
         return jjMoveStringLiteralDfa1_0(0x40080000L, 0x0L);
      case 71:
      case 103:
         return jjMoveStringLiteralDfa1_0(0x800200000L, 0x0L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa1_0(0x402000000L, 0x0L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa1_0(0x40180L, 0x0L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa1_0(0x420000L, 0x0L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa1_0(0x1000012000L, 0x0L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa1_0(0x800600L, 0x0L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa1_0(0x100000000L, 0x0L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa1_0(0x1000000L, 0x0L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa1_0(0x20000000L, 0x0L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa1_0(0x10000000L, 0x0L);
      case 87:
      case 119:
         return jjMoveStringLiteralDfa1_0(0x200100000L, 0x0L);
      case 123:
         return jjStopAtPos(0, 60);
      case 125:
         return jjStopAtPos(0, 61);
      default :
         return jjMoveNfa_0(2, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0, long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0, active1);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active1 & 0x4L) != 0L)
            return jjStopAtPos(1, 66);
         else if ((active1 & 0x8L) != 0L)
            return jjStopAtPos(1, 67);
         else if ((active1 & 0x20L) != 0L)
            return jjStopAtPos(1, 69);
         break;
      case 62:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(1, 37);
         else if ((active1 & 0x10L) != 0L)
            return jjStopAtPos(1, 68);
         break;
      case 95:
         if ((active0 & 0x2000000000000L) != 0L)
            return jjStopAtPos(1, 49);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x442000000L, active1, 0L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x1105008000L, active1, 0L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x200100000L, active1, 0L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x8420000L, active1, 0L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x800L, active1, 0L);
      case 78:
      case 110:
         if ((active0 & 0x100L) != 0L)
         {
            jjmatchedKind = 8;
            jjmatchedPos = 1;
         }
         else if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(1, 10, 27);
         return jjMoveStringLiteralDfa2_0(active0, 0x41000L, active1, 0L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000L, active1, 0L);
      case 82:
      case 114:
         if ((active0 & 0x200L) != 0L)
         {
            jjmatchedKind = 9;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x820a80000L, active1, 0L);
      case 83:
      case 115:
         if ((active0 & 0x20L) != 0L)
         {
            jjmatchedKind = 5;
            jjmatchedPos = 1;
         }
         else if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(1, 7, 27);
         return jjMoveStringLiteralDfa2_0(active0, 0x10004000L, active1, 0L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000L, active1, 0L);
      case 88:
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000L, active1, 0L);
      case 89:
      case 121:
         if ((active0 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(1, 6, 27);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0, active1);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(0, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0, 0L);
      return 2;
   }
   switch(curChar)
   {
      case 67:
      case 99:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(2, 14, 27);
         break;
      case 68:
      case 100:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(2, 12, 27);
         return jjMoveStringLiteralDfa3_0(active0, 0x800000L);
      case 69:
      case 101:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(2, 28, 27);
         return jjMoveStringLiteralDfa3_0(active0, 0x280100000L);
      case 75:
      case 107:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000L);
      case 76:
      case 108:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(2, 11, 27);
         return jjMoveStringLiteralDfa3_0(active0, 0x41050000L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x800280000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x108008000L);
      case 84:
      case 116:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(2, 13, 27);
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
      case 86:
      case 118:
         return jjMoveStringLiteralDfa3_0(active0, 0x402000000L);
      case 87:
      case 119:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 36, 27);
         break;
      default :
         break;
   }
   return jjStartNfa_0(1, active0, 0L);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0, 0L);
      return 3;
   }
   switch(curChar)
   {
      case 67:
      case 99:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(3, 15, 27);
         return jjMoveStringLiteralDfa4_0(active0, 0x80000000L);
      case 69:
      case 101:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(3, 17, 27);
         else if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(3, 29, 27);
         return jjMoveStringLiteralDfa4_0(active0, 0x1800000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x402440000L);
      case 76:
      case 108:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(3, 16, 27);
         break;
      case 77:
      case 109:
         if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(3, 19, 27);
         break;
      case 82:
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x200100000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x40000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x900200000L);
      case 87:
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0, 0L);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0, 0L);
      return 4;
   }
   switch(curChar)
   {
      case 67:
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000L);
      case 69:
      case 101:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 4;
         }
         else if ((active0 & 0x40000000L) != 0L)
            return jjStartNfaWithStates_0(4, 30, 27);
         return jjMoveStringLiteralDfa5_0(active0, 0x204000000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
      case 75:
      case 107:
         return jjMoveStringLiteralDfa5_0(active0, 0x40000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000000L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x402000000L);
      case 80:
      case 112:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 21;
            jjmatchedPos = 4;
         }
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000L);
      case 82:
      case 114:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(4, 23, 27);
         break;
      case 84:
      case 116:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(4, 22, 27);
         break;
      case 85:
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x80000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0, 0L);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0, 0L);
      return 5;
   }
   switch(curChar)
   {
      case 95:
         return jjMoveStringLiteralDfa6_0(active0, 0xa00000000L);
      case 69:
      case 101:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(5, 18, 27);
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000L);
      case 71:
      case 103:
         if ((active0 & 0x2000000L) != 0L)
         {
            jjmatchedKind = 25;
            jjmatchedPos = 5;
         }
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000000L);
      case 84:
      case 116:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(5, 24, 27);
         return jjMoveStringLiteralDfa6_0(active0, 0x180000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0, 0L);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0, 0L);
      return 6;
   }
   switch(curChar)
   {
      case 95:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa7_0(active0, 0x800000000L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000000L);
      case 69:
      case 101:
         if ((active0 & 0x80000000L) != 0L)
            return jjStartNfaWithStates_0(6, 31, 27);
         break;
      case 78:
      case 110:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(6, 26, 27);
         break;
      case 82:
      case 114:
         return jjMoveStringLiteralDfa7_0(active0, 0x200000000L);
      case 83:
      case 115:
         if ((active0 & 0x100000000L) != 0L)
            return jjStartNfaWithStates_0(6, 32, 27);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0, 0L);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0, 0L);
      return 7;
   }
   switch(curChar)
   {
      case 69:
      case 101:
         return jjMoveStringLiteralDfa8_0(active0, 0x200000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000000L);
      case 84:
      case 116:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(7, 27, 27);
         break;
      case 89:
      case 121:
         return jjMoveStringLiteralDfa8_0(active0, 0x800000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0, 0L);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0, 0L);
      return 8;
   }
   switch(curChar)
   {
      case 95:
         return jjMoveStringLiteralDfa9_0(active0, 0x800000000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x400000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa9_0(active0, 0x200000000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0, 0L);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0, 0L);
      return 9;
   }
   switch(curChar)
   {
      case 82:
      case 114:
         return jjMoveStringLiteralDfa10_0(active0, 0x800000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa10_0(active0, 0x400000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa10_0(active0, 0x200000000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0, 0L);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0, 0L);
      return 10;
   }
   switch(curChar)
   {
      case 69:
      case 101:
         return jjMoveStringLiteralDfa11_0(active0, 0x800000000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa11_0(active0, 0x200000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa11_0(active0, 0x400000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0, 0L);
}
private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0, 0L);
      return 11;
   }
   switch(curChar)
   {
      case 76:
      case 108:
         return jjMoveStringLiteralDfa12_0(active0, 0x400000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa12_0(active0, 0x800000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa12_0(active0, 0x200000000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0, 0L);
}
private final int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0, 0L);
      return 12;
   }
   switch(curChar)
   {
      case 83:
      case 115:
         if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(12, 33, 1);
         break;
      case 84:
      case 116:
         return jjMoveStringLiteralDfa13_0(active0, 0x400000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa13_0(active0, 0x800000000L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0, 0L);
}
private final int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0, 0L);
      return 13;
   }
   switch(curChar)
   {
      case 76:
      case 108:
         return jjMoveStringLiteralDfa14_0(active0, 0x800000000L);
      case 83:
      case 115:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(13, 34, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(12, active0, 0L);
}
private final int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0, 0L);
      return 14;
   }
   switch(curChar)
   {
      case 84:
      case 116:
         return jjMoveStringLiteralDfa15_0(active0, 0x800000000L);
      default :
         break;
   }
   return jjStartNfa_0(13, active0, 0L);
}
private final int jjMoveStringLiteralDfa15_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0, 0L);
      return 15;
   }
   switch(curChar)
   {
      case 83:
      case 115:
         if ((active0 & 0x800000000L) != 0L)
            return jjStartNfaWithStates_0(15, 35, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(14, active0, 0L);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 27;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 27:
               case 1:
                  if ((0x3ff001000000000L & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjCheckNAdd(1);
                  break;
               case 19:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(22, 23);
                  else if (curChar == 47)
                  {
                     if (kind > 46)
                        kind = 46;
                     jjCheckNAdd(20);
                  }
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 44)
                        kind = 44;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 47)
                     jjAddStates(3, 4);
                  else if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 13;
                  else if (curChar == 46)
                     jjCheckNAdd(9);
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(6, 7);
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 3:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 4:
                  if (curChar == 39 && kind > 41)
                     kind = 41;
                  break;
               case 5:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 6:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if (curChar == 34 && kind > 42)
                     kind = 42;
                  break;
               case 8:
                  if (curChar == 46)
                     jjCheckNAdd(9);
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 43)
                     kind = 43;
                  jjCheckNAddTwoStates(9, 10);
                  break;
               case 11:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 43)
                     kind = 43;
                  jjCheckNAdd(12);
                  break;
               case 13:
                  if (curChar == 45 && kind > 46)
                     kind = 46;
                  break;
               case 14:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 44)
                     kind = 44;
                  jjCheckNAddStates(0, 2);
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(16, 8);
                  break;
               case 17:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 44)
                     kind = 44;
                  jjCheckNAdd(17);
                  break;
               case 18:
                  if (curChar == 47)
                     jjAddStates(3, 4);
                  break;
               case 20:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 46)
                     kind = 46;
                  jjCheckNAdd(20);
                  break;
               case 21:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(22, 23);
                  break;
               case 22:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(22, 23);
                  break;
               case 23:
                  if (curChar == 42)
                     jjCheckNAddStates(5, 7);
                  break;
               case 24:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(25, 23);
                  break;
               case 25:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(25, 23);
                  break;
               case 26:
                  if (curChar == 47 && kind > 47)
                     kind = 47;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 27:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 38)
                        kind = 38;
                     jjCheckNAdd(1);
                  }
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 38)
                        kind = 38;
                     jjCheckNAddTwoStates(0, 1);
                  }
                  break;
               case 2:
               case 0:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjCheckNAddTwoStates(0, 1);
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjCheckNAdd(1);
                  break;
               case 3:
                  jjAddStates(8, 9);
                  break;
               case 6:
                  jjAddStates(10, 11);
                  break;
               case 10:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(12, 13);
                  break;
               case 20:
                  if (kind > 46)
                     kind = 46;
                  jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 22:
                  jjCheckNAddTwoStates(22, 23);
                  break;
               case 24:
               case 25:
                  jjCheckNAddTwoStates(25, 23);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 3:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(8, 9);
                  break;
               case 6:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(10, 11);
                  break;
               case 20:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 46)
                     kind = 46;
                  jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 22:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(22, 23);
                  break;
               case 24:
               case 25:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(25, 23);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 27 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   16, 8, 17, 19, 21, 23, 24, 26, 3, 4, 6, 7, 11, 12, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, "\55\76", null, null, 
null, null, null, null, null, null, null, null, "\72", "\72\137", "\77", "\56", 
"\100", "\73", "\54", "\52", "\133", "\135", "\50", "\51", "\173", "\175", "\44", 
"\76", "\74", "\75", "\76\75", "\74\75", "\74\76", "\41\75", "\53", "\55", "\57", 
"\45", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xffff1e7fffffffe1L, 0x3ffL, 
};
static final long[] jjtoSkip = {
   0xc0000000001eL, 0x0L, 
};
static final long[] jjtoSpecial = {
   0xc00000000000L, 0x0L, 
};
protected JavaCharStream input_stream;
private final int[] jjrounds = new int[27];
private final int[] jjstateSet = new int[54];
protected char curChar;
public JoSQLParserTokenManager(JavaCharStream stream){
   if (JavaCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public JoSQLParserTokenManager(JavaCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(JavaCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 27; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(JavaCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         matchedToken.specialToken = specialToken;
         return matchedToken;
      }
      else
      {
         if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
         {
            matchedToken = jjFillToken();
            if (specialToken == null)
               specialToken = matchedToken;
            else
            {
               matchedToken.specialToken = specialToken;
               specialToken = (specialToken.next = matchedToken);
            }
         }
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}