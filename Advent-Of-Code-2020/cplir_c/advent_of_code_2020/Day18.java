package cplir_c.advent_of_code_2020;

import java.util.Arrays;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;


public final class Day18 {
    private static final boolean DEBUG = true;
    static int                   MODE  = 1;

    public static void main(String[] args) {
        for (var i = 0; i < EXAMPLES.length; ++i) {
            var result = sumExpressions(EXAMPLES[i]);
            var answer = EXAMPLES_I[i];
            System.out.println("result " + result + " answer " + answer);
        }
        // sumExpressions(INPUT);
        System.out.println(
            "after passing the examples but still failing part 2, I used python operation swapped operation overrides"
        );
        for (var i = 0; i < EXAMPLES.length; ++i) {
            var result = sumAdvanced(EXAMPLES[i]);
            var answer = EXAMPLES_II[i];
            System.out.println("adv rslt: " + result + " answer " + answer);
        }
        var result = sumAdvanced(INPUT);
        System.out.println("adv rslt: " + result + ", no answer known.");
    }
    static long sumAdvanced(String string) {
        string = string.replace(" ", "");
        System.out.println(string);
        var it = ByteIterators.wrap(string.getBytes());
        // \n: sum operation priority -1
        // *: multiplication operation priority 0
        // +: sum operation priority 1
        // (): group operation, priority 2
        if (DEBUG) {
            System.out.println("calling iHW on " + string);
        }
        return interpretHW(it, (byte) -2); // abort when encountering a priority <= -2
    }

    static long interpretHW(ByteListIterator it, byte stopPriority) {
        if (DEBUG) {
            System.out.println("entered iHW at it[" + it.nextIndex() + "], stopPriority=" + stopPriority);
        }
        var value = readArgument(it);
        if (DEBUG) {
            System.out.println("iHW read a value argument of " + value);
            System.out.println(
                "calling iHWwVal on it[" + it.nextIndex() + "], stopPriority=" + stopPriority + ", value=" + value
            );
        }
        return interpretHWWithValue(it, stopPriority, value);
    }
    static long interpretHWWithValue(ByteListIterator it, byte stopPriority, long value) {
        if (DEBUG) {
            System.out.println(
                "entered iHWwVal with it[" + it.nextIndex() + "], stopPriority=" + stopPriority + ", value=" + value
            );
        }
        while (it.hasNext()) {
            if (DEBUG) {
                System.out.println("iHWwVal: next byte available at pos " + it.nextIndex() + ", looping");
            }
            value = interpretTokens(value, it, stopPriority);
            if (DEBUG) {
                System.out.println(
                    "iHWwVal value changed: it[" + it.nextIndex() + "], stopPriority=" + stopPriority + ", value=" + value
                );
            }
            if (value < 0) {
                if (DEBUG) {
                    System.out.println("iHWwVal: value negative, returning " + (~value));
                }
                return ~value;
            }
        }
        if (DEBUG) {
            System.out.println("iHWwVal: no more bytes at pos " + it.nextIndex() + ", returning " + value);
        }
        return value;
    }
    static long interpretTokens(long value, ByteListIterator it, byte stopPriority) {
        if (DEBUG) {
            System.out.println(
                "entered intTokns with value=" + value + ", it[" + it.nextIndex() + "], stopPriority=" + stopPriority
            );
        }
        var opChar = it.nextByte();
        if (DEBUG) {
            System.out.println("intTokns read an operator byte: " + ((char) opChar));
        }
        switch (opChar) {
            case '\n':
                if (DEBUG) {
                    System.out.println("intTokns read a newline");
                }
                if (stopPriority >= -1) {
                    if (DEBUG) {
                        System.out.println(
                            "intTokns: stopPriority indicates newlines are not edible, returning value " + value + " ~value "
                                    + (~value)
                        );
                    }
                    it.previousByte();
                    if (DEBUG) {
                        System.out.println("intTokns: iterator backed up to " + it.nextIndex());
                    }
                    return ~value;
                }
                opChar = -1;
                if (DEBUG) {
                    System.out.println("intTokns: priority indicates newlines are edible,");
                    System.out.println("intTokns: opChar set to a priority of -1, leaving switch");
                }
                break;

            case '*':
                if (DEBUG) {
                    System.out.println("intTokns read an asterisk");
                }
                if (stopPriority >= 0) {
                    if (DEBUG) {
                        System.out.println(
                            "intTokns: stopPriority indicates asterisks are not edible, returning value " + value + " ~value "
                                    + (~value)
                        );
                    }
                    it.previousByte();
                    if (DEBUG) {
                        System.out.println("intTokns: iterator backed up to " + it.nextIndex());
                    }
                    return ~value;
                }
                opChar = 0;
                if (DEBUG) {
                    System.out.println("intTokns: priority indicates asterisks are edible,");
                    System.out.println("intTokns: opChar set to a priority of 0, leaving switch");
                }
                break;

            case '+':
                if (DEBUG) {
                    System.out.println("intTokns read a plus sign");
                }
                if (stopPriority >= 1) {
                    if (DEBUG) {
                        System.out.println(
                            "intTokns: stopPriority indicates plus signs are not edible, returning value " + value + " ~value "
                                    + (~value)
                        );
                    }
                    it.previousByte();
                    if (DEBUG) {
                        System.out.println("intTokns: iterator backed up to " + it.nextIndex());
                    }
                    return ~value;
                }
                opChar = 1;
                if (DEBUG) {
                    System.out.println("intTokns: priority indicates plus signs are edible,");
                    System.out.println("intTokns: opChar set to a priority of 1, leaving switch");
                }
                break;

            case ')':
                if (DEBUG) {
                    System.out.println("intTokns read a right parenthesis");
                }
                if (stopPriority >= -1) {
                    if (DEBUG) {
                        System.out.println(
                            "intTokns: priority indicates right parentheses are not edible, backing up iterator"
                        );
                    }
                    it.previousByte();
                    if (DEBUG) {
                        System.out.println("intTokns: iterator backed up to " + it.nextIndex());
                        // only eat the paren if part of a paren-level call
                    }
                } else if (DEBUG) {
                    // ate a parenthesis
                    System.out.println("intTokns: priority indicates parentheses are edible, parenthesis munched");
                }
                if (DEBUG) {
                    System.out.println("intTokns: returning value=" + value + ", ~value=" + (~value));
                }
                return ~value;

            case '(':
            default :
                if (DEBUG) {
                    System.out.println("intTokns: found an invalid operation byte " + ((char) opChar) + ", throwing");
                    System.out.println(
                        "intTokns state: value=" + value + ", it[" + it.nextIndex() + "], stopPriority=" + stopPriority
                    );
                }
                throw new AssertionError("invalid op: " + ((char) opChar));
        }
        if (DEBUG) {
            System.out.println(
                "calling intTokWop with value=" + value + ", opChar=" + (opChar) + ", it[" + it.nextIndex()
                        + "], stopPriority=" + stopPriority
            );
        }
        return interpretTokensWithOp(value, opChar, it, stopPriority);
    }
    static long interpretTokensWithOp(long value, byte opChar, ByteListIterator it, byte stopPriority) {
        if (DEBUG) {
            System.out.println(
                "entered intTokWop with value=" + value + ", opChar=" + (opChar) + ", it[" + it.nextIndex()
                        + "], stopPriority=" + stopPriority
            );
        }
        var nextValue = readArgument(it);
        if (DEBUG) {
            System.out.println("intTokWop read a next value of " + nextValue + "; iterator now at pos " + it.nextIndex());
        }
        if (opChar == 1 || !it.hasNext()) {
            if (DEBUG) {
                if (opChar == 1) {
                    System.out.println(
                        "intTokWop: opChar is a plus sign, which is max priority, so no need to check for argument stealing"
                    );
                } else {
                    System.out.println(
                        "intTokWop: iterator is out of bytes at " + it.nextIndex() + ", no more arguments available"
                    );
                }
            }

            // max priority, interpret the tokens
            if (opChar == 0) {
                value *= nextValue;
            } else {
                value += nextValue;
            }
            if (DEBUG) {
                System.out.println("intTokWop: returning " + value + " early.");
            }
            return value;
        }
        // if the operation following this one has greater priority,
        // feed it the new argument and use its result as this op's argument
        var nextOpChar = it.nextByte();
        if (DEBUG) {
            System.out.println("intTokWop read a nextOpChar of " + ((char) nextOpChar));
        }
        switch (nextOpChar) {
            case '\n':
                nextOpChar = -1;
                break;

            case '*':
                nextOpChar = 0;
                break;

            case '+':
                nextOpChar = 1;
                break;

            case ')':
                if (stopPriority >= -1) {
                    it.previousByte();
                    // only eat the paren if part of a paren-level call
                }
                if (opChar == 0) {
                    value *= nextValue;
                } else {
                    value += nextValue;
                }
                return ~value;

            case '(':
            default :
                throw new AssertionError("invalid op: " + ((char) opChar));
        }
        // put the operation back
        it.previousByte();
        if (stopPriority >= nextOpChar || nextOpChar < opChar) {
            // the next operation has less priority than this operation, so eat this op's arguments and return the result
            if (opChar == 0) {
                value *= nextValue;
            } else {
                value += nextValue;
            }
            return ~value;
        } else if (nextOpChar == opChar) {
            // the next operation has equal priority, so eat this op's arguments and return the result,
            // allowing the loop to continue
            // now interpret this operation
            if (opChar == 0) {
                value *= nextValue;
            } else {
                value += nextValue;
            }
            return value;
        } else {
            // the next operation has more priority than this operation, so feed it our argument and use the result
            // get the new argument
            nextValue = interpretHWWithValue(it, (byte) (nextOpChar - 1), nextValue);
            // now interpret this operation
            if (opChar == 0) {
                value *= nextValue;
            } else {
                value += nextValue;
            }
            return value;
        }
    }
    static long readArgument(ByteListIterator it) throws AssertionError {
        long value = -1;
        if (!it.hasNext()) {
            throw new AssertionError();
        }
        var chr = it.nextByte();
        switch (chr) {
            case '(':
                value = interpretHW(it, (byte) -2); // only stop for right paren
                break;

            case ')':
            case '+':
            case '*':
            case '\n':
                throw new IllegalArgumentException("" + (char) chr);

            default :
                value = chr - (byte) '0';
        }
        return value;
    }
    static long sumExpressions(String input) {
        long result;
        System.out.println("input " + input);
        input = input.replace("\n", ")+(");
        input = String.format("(%s)", input);
        System.out.println(input);
        if (MODE <= 0) {
            result = opstacksSum(input);
        } else {
            var hwArray = input.getBytes();
            result = sumHwArray(ByteIterators.wrap(hwArray));
        }
        System.out.println();
        System.out.println("HW summed to " + result);
        return result;
    }
    static long sumHwArray(ByteListIterator it) {
        byte stopByte;
        if (it.nextIndex() == 0) {
            stopByte = -1;
        } else {
            stopByte = (byte) ')';
        }
        long result = -1;
        while (it.hasNext()) {
            var current = it.nextByte();
            if (current == stopByte) {
                // System.out.println("stopped parens, returning " + result);
                break;
            }
            // System.out.println("current " + ((char) current) + " result " + result);
            switch (current) {
                case (byte) ' ':
                    continue;

                case (byte) '(':
                    result = sumHwArray(it);
                    break;

                case (byte) '+':
                    result = sumAddition(it, result);
                    break;

                case (byte) '*':
                    result = sumMultiplication(it, result);
                    break;

                default :
                    result = current - (long) '0';
                    break;
            }
        }
        return result;
    }
    static long sumAddition(ByteListIterator it, long result) {
        var current = it.nextByte();
        var nextArg = getNextArg(it, current);
        if (result == -1) {
            result = nextArg;
        } else {
            result += nextArg;
        }
        return result;
    }
    static long sumMultiplication(ByteListIterator it, long result) {
        var current = it.nextByte();
        var nextArg = getNextArg(it, current);
        if (result == -1) {
            result = nextArg;
        } else {
            result *= nextArg;
        }
        return result;
    }
    private static long getNextArg(ByteListIterator it, byte current) {
        long nextArg;
        if (current == (byte) ' ') {
            current = it.nextByte();
        }
        if (current == (byte) '(') {
            nextArg = sumHwArray(it);
        } else {
            nextArg = current - (byte) '0';
        }
        return nextArg;
    }
    private static long opstacksSum(String input) throws AssertionError {
        long result;
        var  operands  = new LongArrayList();
        var  operators = new ByteArrayList();
        for (char chr : input.toCharArray()) {
            if (chr == ' ') {
                continue;
            }
            switch (chr) {
                case '(':
                case '-':
                case '+':
                case '*':
                    operators.add((byte) chr);
                    // System.out.println("pushed op " + chr);
                    break;

                case ')':
                    simplifyStack(operands, operators);
                    chr = (char) operators.removeByte(operators.size() - 1);
                    // System.out.println("popped (: " + chr);
                    if (chr != '(') {
                        throw new AssertionError();
                    }
                    break;

                default :
                    var val = Character.getNumericValue(chr);
                    operands.add(val);
                    // System.out.println("pushed arg " + val);
                    break;
            }
        }
        simplifyStack(operands, operators);
        if (!operators.isEmpty() || operands.size() != 1) {
            System.out.println(operators);
            System.out.println(operands);
            throw new AssertionError();
        }
        result = operands.getLong(0);
        return result;
    }
    private static void simplifyStack(LongList operands, ByteList operators) throws AssertionError {
        if (operators.isEmpty()) {
            return;
        }
        // inclusive
        var argumentIndex = operands.size() - 1;
        var operatorIndex = operators.size() - 1;
        while (operatorIndex >= 0 && operators.getByte(operatorIndex) != '(') {
            --argumentIndex;
            --operatorIndex;
        }
        // inclusive
        ++operatorIndex;
        if (argumentIndex >= operands.size() - 1) {
            return;
        }
        System.out.println(
            "simplifying " + (operators.size() - operatorIndex) + " operators and " + (operands.size() - argumentIndex)
                    + " operands"
        );
        var operatorCount = operatorIndex;
        var argumentCount = argumentIndex;
        var left          = operands.getLong(argumentIndex);
        System.out.print("L" + left + " ");
        ++argumentIndex;
        while (operatorIndex < operators.size()) {
            var operator = operators.getByte(operatorIndex);
            System.out.print((char) operator);
            // System.out.println("read op " + ((char) operator));
            ++operatorIndex;
            var right = operands.getLong(argumentIndex);
            System.out.print(" R" + right + " ");
            ++argumentIndex;
            if (operator == (byte) '+') {
                left += right;
            } else if (operator == (byte) '*') {
                left *= right;
            } else {
                throw new AssertionError((char) operator);
            }
        }
        operators.size(operatorCount);
        operands.size(argumentCount);
        // System.out.println("chopped off read args and ops");
        operands.add(left);
        System.out.println("= " + left);
        printOperators(operators);
        System.out.println("args " + operands);
    }

    private static void printOperators(ByteList operators) {
        var ops  = operators.toByteArray();
        var chrs = new char[ops.length];
        for (var i = 0; i < ops.length; ++i) {
            chrs[i] = (char) ops[i];
        }
        System.out.println("ops " + Arrays.toString(chrs));
    }

    static final String   EXAMPLE     = "1+(2*3)+(4*(5+6))";
    static final String[] EXAMPLES    = {EXAMPLE, "1+2*3+4*5+6", "2*3+(4*5)", "5+(8*3+9+3*4*3)",
                                         "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", "((2+4*9)*(6+9*8+6)+6)+2+4*2"};
    static final int      EXAMPLE_I   = 51;
    static final int[]    EXAMPLES_I  = {EXAMPLE_I, 71, 26, 437, 12240, 13632};
    static final int[]    EXAMPLES_II = {51, 231, 46, 1445, 669060, 23340};
    static final String   INPUT       = "8+((9*7)+2)+(4*(9*3*9+3+8)+6+5+8)\n" + "7+5*3+(9*9*3)\n"
            + "4*(4*8+3+4*2+(8+2*6+6))+(6+9+8)*8+(6+6*(2*9)*3+(9*3*5*4)*7)\n"
            + "((4*3)*2*2)*4*5*5*7*((3*3+6*8)*7*3*9+(7*6+9)*(4*3))\n" + "8+6+9+((8*2+7+9*7)*6*9+(8*2)*5*2)+9\n"
            + "(9+8+4*9*9)*9+8*(2*(7*2)+8)*2\n" + "5*9+6*3+(5*7*5+4*8+4)\n" + "((7*9*3*8*6*6)*(9*2+4*3)+9*3)*2\n"
            + "2+((8+3+6*6+5+9)*4*7+6*4+(6+8*5*9+3))+9+7*7\n" + "((7*9*5)+9*(8+2))+2\n" + "3*6*8*(9+5*8+4*8)+8\n"
            + "9+8*(3+9+(3*7)*3*4)\n" + "((6+8+5*3+7)+(5+6)*8)+9+2\n" + "(5*(3*2*4+9+5))*8+5\n"
            + "(8*2*5*4*3)+5+(3*(6+8*8))*3\n" + "4*5+(9*3)+(7*9*3*7*6)+4\n" + "6+2*2+8+(3+4+(4+2+7+5)*(3+8+7*9*2))*2\n"
            + "(3*5+3+(6*4+3)+(4*4*9)+7)*3+8+(5+2*7+2)*3*7\n" + "6*7+(2*7+9*2+(3+6+6+9)+(7+8))+6*8+6\n" + "8*(6*8+3)*8\n"
            + "(6*4+(8*8)+5*6)*5*8*5\n" + "8*(3*6*(7+5+8*6+5))*2+4\n" + "(7+(8*3)*5+8)*8*((5*9*3*8)*7*(3+2+6+9)*2*3*9)+2\n"
            + "(4+5+5)*(6+(6*5+3)*(8*6*6+8+6))*2+4+3*8\n" + "5+6\n" + "6+5*(6+3+5+5*2)*5+3*9\n" + "3*7+(8*2*4*2)*4*3+9\n"
            + "(2+(5*5*6*8+7)+8*5+9)+(8+4*4+9*4*6)+8*6\n" + "(3*3+5)*6*6*(7*(4*3+4))\n"
            + "7+(9*4)+((3*2*7+8+4+2)*4*3+(9+5+6*5+5)*4)*(8+(7+9+3*2+8)*8*6*3)\n"
            + "9+7+((5+3*4+9)*4+7*9*(7+7))*6+6*(6+6+(6*7+7+4)+(6+2*6+3*5)*6*8)\n" + "5+(9+5+(9*8*5)+3*5)*7+7\n"
            + "(9*5+3*(9*4)+7)*2+9*9*8\n" + "9*5*(6*6*7+(5+4+3+8+3)+(7*9))*(4*7*4+4*(2+2*8*6+9+4)*(8+8*6))+9*4\n"
            + "7+3+((7*9+9*3+3+2)+6+7)*6\n" + "7+7*(9*(2*4*7)*2+(2+5+4+7+5))*(5+7+4*(3*4)+7)\n" + "4+(9+3+(5+4+6+4)+3+3)\n"
            + "(5*(6*6*8+8*9*8)*5+2)*3+5+(6+3+5)\n" + "(5*3*6)+(6+(9+3+9+6*2*4)+6+3*9)*2*8*(3*2*2+6+3+8)\n"
            + "8+6+8+6+((8*4*7+8*4)*4*8+5)\n"
            + "(8*8+(9*2*6*3*5+7)+6+9*8)*5*8*2*(8+8*9*9)+(9*(6*8+9*3*4*5)+8+(8+5+4*6)*(8*8))\n"
            + "(8*(4+3*8*2+6+9)*5*7)+4*(2+4)+5+7+9\n" + "3*4*9+7*(2*(7*6+5+2+5+8)*(6+8))+(7*(5+8+2*7)+4+(7*4+5+4*3)+7*7)\n"
            + "((9+6*7+8+5*3)*8+3*7)+8+3+4*5*((6*7+2+4*4)*9+6*4+(9+8)*(6*4*2))\n"
            + "(6+6*3*4)+2*(5*(8*8)*8*(8+6*6+3*9)*3)+((8+5)*7+6+9+6)+8\n" + "(3+(5+7*2*5+3)+2+(5*4*2+9*3)*4)+9*2\n"
            + "(3*8*2)+6\n" + "7+3*(8+8+9*(6+7)*7)\n" + "5+2*2+(4+4*2+2+2+4)*4+6\n" + "7*((6+7+6*8+4+3)*6+9*7+(5+4*7*7))*9\n"
            + "((9*7)*9)*7+9*3+3+2\n" + "2+9*(5*4*6*(5*4+4*2*6*3)*(2+5+5*9))+5\n"
            + "8*3*8*((7*3+8)*6*8*9+(3+9+9*2*3)*(7*2*5+9*3))\n" + "6*(6*5+6+5*9*7)+2*(7+3*3*7+4)+3+8\n"
            + "4+(8*5*7*(7+8+6)*6)+4+(2*6*5*4)+((8+6+7+3)+(3*3*8*5)+(3+3+2))\n" + "2*5+4+7*(7+8*5+9*3*7)*6\n"
            + "3*6+((7*7*9*9*6*6)+2*(8*6+5+2+5)+7*(7*8*2+6+2+8)+(9*3+2+8+9))*4\n" + "3*2*8*(7*4+(6+9+3+7*2))\n"
            + "(7*6*(2*4)*(2*5)*8*4)+6*9+7+5+4\n" + "((8*6+2+9*4*7)*2*6*(6*5*7))*5*(3*(5*3+4)+6*5+8)+6\n" + "5+7+2+7+(5*3)\n"
            + "((3+2*9*5+9)+9+(3+8*6+8)+8)+6+8+4*8*((2+3+9+6+4*7)*6*8*6)\n" + "9*5*7+8\n" + "9*9+2+7+(8+9*9+3)+2\n"
            + "9+((3*7*4+8)+(7+2+2*7+2+4)+7*7+6)*(3*(6*7*3+8)+4+(7+4+4+9)+(3+8*4)+7)*2+9\n"
            + "3+(5+(9*4+5+4+3)*4*(7*6)+6)*5*9+9\n" + "7+(9*(2+8*8+5)*2+7)*6+2+4+2\n" + "(6+(9*4+7+8+5))+3\n" + "8*8*9\n"
            + "6+5*((2*4)*(6*6*9*6)*4+2*3*2)*2+8\n" + "8+5*(9+6)*(2*(6+3+7*2+7)*7)\n" + "4*(7*5+6*4)*2\n"
            + "(4+(8+3*5*6)*2*4)+4\n" + "9*6*2+5\n" + "2+(6*9+6+(8+4*6)*6*3)*9*9*3\n" + "7*((9+8+9)*8+2)+5*4*6\n"
            + "8*((2*9*9*4)+9*9*5+9)+7\n" + "((9+5*7+2*4+8)+4)+6+9+8*5\n" + "(2*9)*7+(3*7*6+6)*2*2+(4+8+(2+8))\n"
            + "((4*2)*2+2+6)+6\n" + "(6*9*8+8*6)+8*(8+8*4+4)\n"
            + "((2*2*2)+6*6+8*2+3)*2*(9+3+(3*9*5+2*6+6)+(3*2*8+9+6+7))*((5*2*2+6+4*2)+(8+8*5*7)+8)+9\n"
            + "9+(8*4*9*(9*6*8*9))*9+3*5\n" + "8+4+2*((8+5*8+2)+9*(7*7+7+2*6)*(6+6)*2*8)\n"
            + "(6+(8*4)*7+3*(4*5+8*5)+5)+7*(7+9*(8*5*6*9+6*7)+9*4)\n" + "6*3*3+(3*6*9*(3*9*7)*(5*4*4)+5)+((2*9)*3)+5\n"
            + "(4+8)+3+4+3*7+4\n" + "(6*3+(5*3+9+4+5))+(7+8+2*(4*7*6*7*8+7))*5\n" + "(7+(8+4)+4*(2+8))+4*5+2*9*(5+9+6+4+6+8)\n"
            + "((6*4+6+7)*5*2*2+2)+(2+5*7+2)+6+4*7\n" + "(6+2+2+5)*6+3*7+(8+9*(7+7*5)+3)+8\n"
            + "(3+(7*3*6*3+7)+2+2+8+(7+2*6+6+4))*5+5*3*6+6\n" + "6*(6+(4+2+6*3)+2+(6+6)+8)*3\n"
            + "(9+9+6*(6+7*2)+(2*2*8*3)*3)+(5+(2*6+9*4*4*7)*5*7+9)+6\n"
            + "((7+5+9)+9+3)*(2+9+8+4*2*6)*((5+2+8)*(8+8*7)+7+(4+6)*(9*3*8+9)+8)*9\n" + "5*4+5+((5+4+7*8*4)+6+4+2*6)\n"
            + "5+7+7*2+7\n" + "(5*4)*2*(4+3*(2+6)+3*(8*6*7))*4\n" + "4*(4*8*(4*6+4*5+2)*(2+5)*7*8)*7+8\n" + "9+9\n"
            + "9+8*3*8+((7*3*3)*2)+6\n" + "4*(3*5*3*(2*4))+3+((8+5)*3*9*4)*7+2\n" + "(7*2)*2*7\n" + "4*8*2+2+9+2\n"
            + "5*(6+8+7*3+7*(9+2+3+6+6*6))*8*((7+4*8*5+7)+2*5)\n" + "7+(3+(7+2*9)*3*2+7*7)+5\n" + "5*((7*8*2)+2*5+9*8)*3\n"
            + "(7*5+9)*4*2*7*8*3\n" + "9*7+(6*3*4*(4+7*5*4+9)+7)+3*3\n" + "(3*5*6*5)+2+3*(8*5*8*2)+4\n"
            + "4+2*((4*2+3+6+4+6)*8)\n" + "7+(5*8)*4*(7+(7+4)*6)\n" + "4+5+3+(3+4+5*3)*9\n" + "((3+4*4*9)*7+4)*7+8\n"
            + "7+5*2+(7*5)*5\n" + "2+8*5*4*9+(5*8+4+8*2)\n" + "(4*9*7*9)*((5+4*7+8)+9+7*7+9+8)+9*7*5\n"
            + "(8+(3*7+8+6+5)+2+8*8)+9\n" + "2+(6*9)*(2+6*2*5*9)+4\n" + "4+(4*2+4+8*8*4)*6*4*5*(4*5)\n"
            + "(2*5+5*9)*7*4*(6+2*5+2+(5*2*7)+(6+9))*9+4\n" + "4*9*(2+(5+2+6*6)+4+4*(2+9*8))\n" + "8+2+5+8*(3*(7*3+2)*9)+3\n"
            + "7*6*6*5+4*6\n" + "2+9*(4*3*2+5)+2+(4+5)*4\n" + "((5+4*4)*7+7)*3+6+5*2+8\n"
            + "9*((5+8+5*6+5)+5+3*(3+5)*2)+(2+6+4*(5+4+5+5+5))*(8*9)\n" + "6+4+7*8*4+3\n"
            + "((5*9*4+9+6)+5+8+2+6*5)*8*9*4+8+4\n" + "(8+9+(9+6+2*2))+5\n" + "(8+3+5*(8+4))*(4+6*4*(3*4)*5)*4*8*7\n"
            + "9+2+(6*7*(3*9+7+9+8*9)+7+2)+2*5\n" + "3+(2+6+(3+3*2+8*7)+5)*3\n" + "((4*4*6*2*2)+9*6)*2\n" + "(4+6+2+7)+8\n"
            + "(4+7*(9*6*8)+3+4*6)*2+3+2*7*6\n" + "(4*6+2+(9*2+5*7)*9)+8+(7*(5+8*8+4))*(5+4+3+3)+8\n"
            + "5+2+6*8*(5+3)*(9+7+3+(2*5*4*7+7*6)+8)\n" + "7+3+4*(9*(6+4*7*5*3)+2*6)\n" + "5+3*4+3+(7+(2*5+8*6+9)+(2+8+6+8))\n"
            + "2+(2+7*6*8)*7*5+3+8\n" + "9*9*5*((4+9+2)*7)*(2+3+4)\n" + "7*(3+(5+4+4*2)*9+2+2)+9*(2*9*5*7+9+7)+9\n"
            + "9*(6+(6+9*8)+6+2*(3*8*8+9+6*8))*8*8*3*9\n" + "4*(5*8*3*3)\n" + "(2*(2*4+6*7+2)+8+8)+4*6+(2*6+6+6+2*4)+6\n"
            + "7+(2+7+2*7*4)\n" + "(9+(2*2))+7*8\n" + "3+(4+8*9*(7+4+3*5+4*7)*4)*4+6+7+9\n"
            + "2*(3*9+5*(7+4*2+4*6)*6+4)*7+(2+(3+8))+3*2\n" + "((6+8)+6+6)+2*3+(8+(2*3)*(8+9))\n" + "8*9+(5+3)*4+4+9\n"
            + "7*9*(7*8+8*2+(9+8*9)*8)*4+2+6\n" + "6*4*9*3+2*8\n" + "4*4*8*(7*7*6)*2*4\n" + "5+7+9*(6+2+9*(8+7+9*9)*4)+4+6\n"
            + "(6+5)*2+9+3\n" + "9+6+(5*8)+4+(8+7*5)\n" + "4*8\n" + "8*3*9*(2+6*(6+7)*5+(2*9+9*8+7*8))*8*3\n"
            + "9*5+4+7*((4*4*6*7+9*2)+7*3)*6\n" + "7*(7*5*3*5*5)+4*8*6*3\n"
            + "8*(8+7*(3+8+2*8*8+2)*2+(6*5+9*5*6+7)+(8+2))*4+3*(2*7*9)+8\n" + "3+2\n" + "6+6+4*4+(9*9*(3+5*3*4)+6+3)\n"
            + "6+(4*2*8+7+8)+8*((9+6*3)*2+5)*4*9\n" + "8+(2*9*3*5+2+3)*((2+9*9+9*8)*6*2*5)\n"
            + "2+3+(8*7*7*(5+3)*(2+4+6+4*5+7))\n" + "8+(5+4+4)*((7*6*6)*(3*7+5+2)+5+6)\n" + "7*(8*2+5+(7*6))*2+2+8\n"
            + "8+((6*6*8+9*3)+(8+2+9*2*6)+2*8*2+4)+4+9+((5*6*5+4)+6+5*(4+5+7*9*3+6)+4+(8*7+8+7))+(8+8+9*9)\n"
            + "3+((7*6+5)+2+(4+8+7+4+7*7))*2+4\n" + "6*(5+(7*4)+4*7)*6*9*4*2\n" + "5+8*2*7+(9*8+7)\n"
            + "8*(3*9*2*(5*3+6*6*9)*6)\n" + "(4+9*2+(7*7*8+9)*2)+2+6\n" + "(8+9*(4+9+9*6+2+7)+5*(7*6+5)*3)+6\n"
            + "4+(9*(3*6+9+3*6)+7+3+4+5)*6*9\n" + "4+7*(6+(2+7*8*9+5*4))+(3*2+2*9*8*8)\n"
            + "(5*9*3*2*5)*3+(7*9)+5*(5+(3*9)+2+8+3)\n" + "(4*(6+2+3+7*4*9))*8*5+4+9*8\n"
            + "3+4+8*((2+3+8)+(2*9+7*7)+2+3+6+4)+8\n" + "2*4+7*(6*2)\n" + "7+(8+7*9*9)*4*8+8\n"
            + "3*(3+4+5+7+(7*3+6+6+2+3)*(9*7*5))*9*4*2\n" + "3*3+8\n" + "3+2*(8*5*9+3*9)*8+8*(4*2+4)\n"
            + "(6*6*7+4)+(5+3+9+7*7)\n" + "9*4*4*9+8+(8*5*3+8)\n" + "(7*4*7+4)*(4*9*3*2)+2+6+6+6\n"
            + "5+7*2*((6*9*6+8+5)*6+8*2*3)+6*6\n" + "6+(3*(2*5*3)+5*3)*4+5*2\n" + "5*(5*2*5*(7*6+2))\n"
            + "9+((7*5+7)*6+(9*6))*4*7*7+7\n" + "7+4*2\n" + "4+((9+4)*6)*5*4\n" + "7*(6*6+4*5+5+5)\n" + "6+(9*6*8*9+7)*9*3*6\n"
            + "9+7*5*(7*5*9+8+4*4)\n" + "8*6*(2+(7*6*7+9+9)+(5+8*8+9*9*8)+(6*8+7+4)*9*2)*(9+3*3+5)+9\n" + "4+(2*9*2)+4+9+2+7\n"
            + "6+(3*6*(9+3*6+2)*4+(9*7*4)+8)+8*6+(2*7)\n" + "7*(2*2)*9\n" + "8+6*4+6*2+7\n"
            + "3*3*8+3*(8+(4*4*6*4*6)+6*(7+5+4*6))+4\n" + "(7+(8+7))+(3*(6*7+6+7+6+3)+2)*2*4*9*8\n"
            + "7+8+9+(5+9*(2+9*7+2)+7*6+(3*5+5*8*5))+9\n"
            + "(6*(9*7+3*8*3+2)+9+2*2)*8*5+(4*(8+6*9*6*6)+7+(9*9*3*3+2+8)*2)*3+4\n" + "((7*2*7*9*2+5)*7)*8\n"
            + "(2+(8*3+6)+8*6+3*7)*8\n" + "(6*2*2*4*3)+5+7\n" + "(8+7*(7*6+8)*4)*(6*7*(5*9*5+7+2*6))\n"
            + "5*(8+(8+3+8*5+2)*(8+3+7)+7)+(4+7)*5+8\n" + "(9+6+7)+(9+7+7+(6+8*4+4+6*4)+8)*((6+4+5)*4*4+7*6)+3+3*8\n"
            + "(6*5*(2*8+2))*8+5+4+(9*8+8+4+8)\n" + "2*7*((4*6)+8*8+(7*3*9+2+6*3)+9)+3\n" + "2*7*(2+2*6)*9\n"
            + "5*((9*6*5)*5*3)*2\n" + "9*(3*2+(7+5*7+4+8+7)*8+8+4)+4+(2*4*4+4+(8+2+8))+8+3\n" + "(5*8*4)+3*4+8\n"
            + "7+7*9*6+(2+8+(4*8+3))\n" + "(5*6*5*5*3+7)*4+9\n" + "(3+4*7+6+(7+9*6+4))*6\n"
            + "9+(9*2+(2+9+6+3*4+6)+(4+8+2+6*2+7))*9\n" + "4+(5*8)\n" + "4*3+7*(8*8+4)*5\n"
            + "4*6*4+(2+6+4*(9+8)*(6*3)*(3*3))+9+5\n" + "5*5+6*(9*(9+3*7*6*6*3))*4*8\n" + "2+8+3*7+(3+6*(3+9)+(9+5*4+6+8*3))\n"
            + "7+3*3+((3*2+2+2*5+8)+(8*8*5+5)*7*4+2)+7\n" + "4*5+7+7*(5+5*9)*4\n" + "9+6+6*((2+2*8)+8+3+3*9)*4\n"
            + "9+((3*2)*3)*9+8+4\n" + "(6+(4*9*4+2+3)*(9+5+2)+5)+6*4+5*6+8\n" + "3+8*9+3+5\n"
            + "(6+(3*9*3+2*7*9)*(5+7+2*7*8))+2+8*2+7+2\n" + "6*(5*9+8+5)*(5+9*7+7*9*(2+6*2))*5\n"
            + "6*((6*3+6*2+8)+8+(3+2*5*7*4)+(8+7)+4)\n" + "5+((2*4+4)+9)+(2*6+5+8*2)+5*4*6\n"
            + "(8*6*6+4)*7+5*(7+8+(4*6+7*3*6*7)*(9+6)*7)+7\n" + "3*(4*3*9+3*6)*(6*5)+4+2*9\n" + "(9*(4+2*5))*7*9+4\n"
            + "(9*(9*2*3*5+6*6)+6+(6*9*2))*6*7\n" + "(8+(3+9*6*3))*(3+(2+2+5)*2+3)+8*4+5*2\n" + "3*6+(4*8)\n"
            + "(5*4+2*(2+8*7*8*7)*7)*(7*7)\n" + "6*9*2+2+6*((6+6+9*5)+2)\n" + "6+8*(5+(9*3+3)*(9*9+3+6*3+2)*5+4*4)\n"
            + "(5*3*5)*(3*4)\n" + "6+(5*2*3+6)+5*3\n" + "7*(7+2+5)\n" + "(3+3)+2+(2*4+3)+5+(8+4+4*2*4)\n"
            + "5+4+(9*3+(7*9+6+3))*2*(3+9*7+9)+9\n" + "(6*8*6*2*3+9)*3+2+7+9\n" + "(5*3)*(4+8+3*5)\n"
            + "(4*(3*3*9*6*9)+6+7)*4*2\n" + "6+(3+7+7+9+5*6)*(3+(6*6*2+7*9)+(2+8*2+2)+(7+7)+3)*(2+2)+8\n"
            + "4*9*7*7+((4*3*9)*6+(7*8)*3*(5+9+9*8+6)+8)\n" + "8+6*4\n" + "(3*6)*9\n" + "((6*3*8+2*3+7)*4)*2+7*3*8+(5*6+4)\n"
            + "8+4+(4*(6*7*9+7+3)*(2+7+7+5*6)+6+7)*3*3\n" + "(6*3*9*6*8)*8*(6*7)+6\n" + "(8+2+2)*7*4+9*(3+7+7+(2*9*4*9*5)*4)\n"
            + "6+7+(2*3*5+7+2+6)+2\n" + "5+(8+3)+(6*2+9+9+3)*(9+7*2+7*7*7)\n"
            + "3*((2+6+2*2*4)+4+(3+3+6)*5+4)+9+3*8*(7+8+9+7+7)\n" + "(6+9*2+3*2)+4*3*6*8*6\n" + "7*((3*8+3+3)+8)*4*9*9\n"
            + "4+4+(8*4*9+(2*9*6*3)+6)+2*7*5\n" + "(4+5+9+9*2*5)+5*6+8+(4*2*5+9)\n" + "8+(5+2+4)+7+(8+2+(2*3*7*6*5*8)*4+4*6)\n"
            + "5*8+3*(5*6*(4+7*2*5+5*6)+7)\n" + "6*((3+8*3*9)*4*8)*4+8*4+3\n" + "4+(9*8*9*4+5*(5*2+2))+8*2\n"
            + "6+3+9+(4*4+3*6)\n" + "9*3+(6*6)+6+6\n" + "((2+3*3*4+4*4)+8+(8*2*5)*(7*3*2+4*7*9)*(3*6))*9*6\n"
            + "9*(6*5+3*6+8+7)+5*2\n" + "9*((6*7*8+2+7*5)+4*9+8*3+3)+(7*6*4+8*9+8)\n" + "5+((3+2*3*3+4*3)*9*4+5)\n"
            + "4+7+(9*5)+5+(5*4+9*9)\n" + "9*6+2+(3+7*(9*4+7+2)+7+(7*9*5*6*6))*6*8\n" + "(6*2+7*2)*6*9+3\n"
            + "2+2+2*(6*6+3*9)+(5*3+7*4+8*8)*7\n" + "(5+7+7*(8+5*5)*8*3)+((6*9)+5)+8+3*9*(2*5*2)\n"
            + "(3+(5*5*2*2+4)+7)+(8*6*8*(7+9+3+8+3))+(8*7)*(8*3+3+2*(8*7+8+5)+3)*8\n"
            + "6+((3+9+9+4)*(2+3+6*8*8+3)+4+7*3+2)+8\n" + "(2*3)+4+7+(8+8)\n" + "3+6*(6+(5*9+3*8*6)+5)\n" + "(8*5+7)+2*4\n"
            + "4*6*7*4*(4*(8+6+8)+2*2*(3+6*4+5*9+7)+5)\n" + "2+((5+4*4+3+5)*(3*9+7*2*3*6)*(9*7+2*2+5*3)+6+2*9)*3+9*3*9\n"
            + "6*3*(7*8*7)\n" + "7*3*((6*2+9)*3*3*(9+2)+(7+5*5+6+5)+6)\n"
            + "(5+(9*7+9*6+3*5)*7*9)*5+8+(6+3)+(6+9*(6*7*5*3*5))\n" + "(3+(6+9+7+6*8+7)+3*5*(7*3+8))*2+2*3*5\n"
            + "6+7*7+(9*3+(5*8*9*9)+3)*(7+2)\n" + "2+((2+6+9+3)*(6+3)*3)*6*3\n" + "(8+3*2*3+(4*7+9*5*9*5))+3+6*8\n"
            + "6*(3+2*9)*6+4\n" + "(5+6*8*8+4)*4+(7+5)+9+2\n" + "(9*(6*9*9*3*2+4)*9+2*2*5)*3+8*(2*9*9*7+5)+7\n"
            + "8+7*((4*5*6+4+9)*5)+6*((8+2*4*9*8)*6*(4+2*8+2)*9+3*2)\n" + "5*7+(4*(8+6+9*4+4+6)*6)\n"
            + "(7*8*(3+3)*4*6)*6+9*8*3\n" + "5+((7+6+2)*7+(2*7+7*9+8))*2*8\n" + "((2+3+7*8)*3*(9*2+9))+(6*9*3*7+5)*7*9\n"
            + "(5*(8*2)*3*(4*2)+8)+3*(5*3)+(8+4*8*(5*6))+3*6\n" + "2+((7*9+2*5*9+8)+(6*4*5*7+3+7)*8+7*(9*3+9*3*2))\n"
            + "5+6*6*(5+9+(5+6*4)+8)\n" + "(7*8*(8+2)+(3+9+4+8))+(6*(2*5*3+6*6)*7+5*8*(9+4*5+8+6*4))\n" + "(8*6+8*5)*7*6\n"
            + "9+8+(2*6*4*(6+5*5)*3+9)+4\n" + "7+6+(2*(7+9+6)*7+5)\n"
            + "(2*(9*2+2*8+6)*6*4)*3*3+((7*9+9+2+6+8)*(9*5*9+6)+(2*9)*9+4+(9*4))\n" + "6*(9*5*4+7*6)*4*7\n"
            + "((8+6)*5*2)*9+(7*8)+9+((8*3*7)*7*2+3*7)*2\n" + "5+(7+(7*3+2*5+6)+7)*4*5+2+7\n"
            + "4*7*((8+2*3+2*9*8)+(2*9)*(2*8+2)*8+6*4)+(2*(8*8)*4+3)\n" + "9+5*(4+7*3+3*2)\n" + "6*9+3+(6+6*(4+6+8*7*5))+6\n"
            + "(5*4+4+(4+6)+(5+7))+7+6*3*(8+5)+(5+5+(5+5*4*9+4*4)+4+5*8)\n" + "9+(4+(3*7*7*3))*4*(8*5*9*3+5+9)*2\n"
            + "2+6*9*5*4\n" + "7*(5*4)+3*8+8\n" + "(8+3+(3*4*4+9*5)+7)*(3*2+9+7+9)*6+6\n" + "2*3*2+4\n"
            + "(4*(9*3+9+3+8+4))*7+2*8\n" + "7*(2+5+9*6+5)+4*(6+2*2)\n" + "(6*7*5+5*2)+9\n" + "(2+4+4)*4+9+2*8\n"
            + "6*9*(9+(9*4+2+7*5+3)*6)*5+4+7\n" + "6*8*(2*(7+6*3*8)*(6*8*3))\n" + "9*8*4+5+((7+4+6+9+6)*5*2)+(9*2+(7+9*6)+6)\n"
            + "7+8*(5*5)*9\n" + "7*2*9+6+9+(3*9+8+8*9*5)\n" + "6+3*(6*5*8*8)*7\n" + "4*(4+7+4*(9*2+3*9*8*7))*(7*6*8)+7+4*6\n"
            + "5*7*6\n" + "6*4\n" + "8*((8*8+4*3)*9+6*7+4+(4*8))*9\n" + "((3+3*2*8)*9)+7+4+8\n" + "3*7+9*(3+(5*3+2))\n"
            + "7+9+4*2\n" + "2*(6*6+(2*8+4*5)+4+5*6)*2*8\n" + "(5*2*(8*9+5*4+9*4)+7)*9+6\n" + "4*5+9+7*8\n" + "2*6*5*6\n"
            + "2+9*(6*4+6+(7+9*7*5+7)*6+(8*2*9*7*9*4))\n" + "5*2+4+5*9\n" + "9+6+5+(2*(7+3)+(3*8)*8*8*4)+9\n"
            + "3*(2+8)*2+3*9*2\n" + "9+((9+2*4+7)*4+8)+3+2+2+(5*(5*9*4*8*8*9)*2*5)\n" + "((9*8*2*2*2+7)+7+7)+2+5*2\n"
            + "7+(7+6*9*5+9)*6+6+(8+9)+7\n" + "(7+6+5*5+6)+2+2+2*2\n" + "(4+2*9*6+8+(5*7))+6*8+3*5\n" + "(8*6)*6*9*6+7\n"
            + "3*8+(4+(2+8)*(3*6+3*5+9*3)*(9*6*6*2+5)+(9+9))\n" + "4*(4*8*6*8*3)*2\n" + "3+(5*5*5*(6*6)*6+3)+9*8\n"
            + "9+5*3*(2+3*6+4*(6+7+6*6)+4)*8+9\n" + "(5+5)*(2+3*3*8*9*7)*4+4*8+9\n" + "7+(4+6)+5+6\n"
            + "(8+(6+8+2+9*6*7)+2)*8+6*(3+5)+2\n" + "9+9*8*(7+7*9+9+6+(8*5))\n" + "5*9*3*5*3+((8+9+9)*3*6+9+6)\n"
            + "(8*9+3*8)*3+(5+3*(8*9+8)*(3+7+7+7)*9)\n" + "4*7+7+(6*(2*8)*2+4)\n" + "(3+9*2*(5*8))*9\n" + "3+3*6*2*(9*9)*6\n"
            + "3*8+(7+7+(8+3)+(7*6*2)+3)";
}
