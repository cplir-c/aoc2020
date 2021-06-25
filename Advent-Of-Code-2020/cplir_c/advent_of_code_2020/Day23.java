package cplir_c.advent_of_code_2020;

import java.util.Arrays;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntCollection;


public final class Day23 {

    private static final boolean debug = false;
    public static void main(String[] args) {

        playCrabCups(INPUT, 100);
        playCrabCups(EXAMPLE, 100);
        playCrabCups(EXAMPLE, 10);

        playMegaCrabCups(INPUT);
        playMegaCrabCups(EXAMPLE);
    }

    static final int MILLION     = 1_000_000;
    static final int TEN_MILLION = 10_000_000;
    private static void playMegaCrabCups(int cupsIn) {
        var cupCount  = countDigits(cupsIn);
        var tableCups = new IntLinkedList();
        var positions = new CopyableIntListIterator[MILLION + 1];
        listMegaTableCups(cupsIn, tableCups, positions);
        simulateMegaMoves(tableCups, positions, TEN_MILLION);
        var oneIt = positions[1];
        System.out.println("after:");
        var a = oneIt.nextInt();
        var b = oneIt.nextInt();
        System.out.println(a + " " + b);
        System.out.println("a * b = " + (((long) a) * ((long) b)));
    }
    static void listMegaTableCups(int cupsIn, IntLinkedList tableCups, CopyableIntListIterator[] positions) {
        while (cupsIn > 0) {
            tableCups.add(cupsIn % 10);
            cupsIn /= 10;
        }
        var max  = max(tableCups);
        var iter  = tableCups.iterator();
        for (var label = MILLION; label > max; --label) {
            iter.add(label);
            if (label == (MILLION - 10)) {
                System.out.println(tableCups);
            }
        }
        var iterator = (IntLinkedIterator) tableCups.listIterator(0);
        System.out.println(iterator.node.value);
        for (var i = MILLION; i >= 0; --i) {
            var newIterator = iterator.copy();
            positions[iterator.previousInt()] = newIterator;
        }
        System.out.println("initialized linked int list of size " + tableCups.size);
    }
    static void simulateMegaMoves(IntLinkedList tableCups, CopyableIntListIterator[] positions, int moveCount) {
        var currentCupPosition = tableCups.listIterator(0);
        var currentCup         = currentCupPosition.previousInt();
        if (currentCupPosition.nextInt() != currentCup) {
            System.out.print("#");
        }
        var moveCups = new CopyableIntListIterator[3];
        while (moveCount > 0) {
            --moveCount;
            simulateMegaMove(tableCups, positions, currentCupPosition, currentCup, moveCups);
            currentCupPosition.copyFrom(positions[currentCup]);
            currentCup = currentCupPosition.nextInt();
        }
    }
    private static void simulateMegaMove(IntLinkedList tableCups, CopyableIntListIterator[] positions,
                                         CopyableIntListIterator currentCupPosition, int currentCup,
                                         CopyableIntListIterator[] moveCups) {
        if (debug) {
            prettyPrintCups(currentCupPosition, currentCup);
        }
        for (byte moveIndex = 0; moveIndex < 3; ++moveIndex) {
            moveCups[moveIndex] = removeMoveCup(tableCups, positions, currentCup);
        }
        var destinationCup         = selectDestinationCup(tableCups, positions, currentCup);
        var destinationCupPosition = positions[destinationCup];
        if (debug) {
            prettyPrintState(moveCups, destinationCup);
        }
        var insertionPosition = destinationCupPosition.copy();
        insertionPosition.nextInt();

        for (var i = 0; i < 3; ++i) {
            var moveCupPosition = moveCups[i];
            var label           = moveCupPosition.add(insertionPosition);

            positions[label] = moveCupPosition;
        }
    }
    private static void prettyPrintState(CopyableIntListIterator[] moveCups, int destinationCup) {
        var sb = new StringBuilder();
        sb.append("pick up:");
        for (CopyableIntListIterator cup : moveCups) {
            sb.append(' ');
            sb.append(cup.copy().previousInt());
        }
        sb.append("\ndestination: ");
        sb.append(destinationCup);
        sb.append('\n');
        System.out.println(sb);
    }
    private static int selectDestinationCup(IntLinkedList tableCups, CopyableIntListIterator[] positions, int currentCup) {

        for (var destinationCup = currentCup - 1; destinationCup > 0; --destinationCup) {
            var destinationCupPosition = positions[destinationCup];
            if (destinationCupPosition != null) {
                return destinationCup;
            }
        }

        for (var destinationCup = positions.length - 1; destinationCup > currentCup; --destinationCup) {
            var destinationCupPosition = positions[destinationCup];
            if (destinationCupPosition != null) {
                return destinationCup;
            }
        }
        throw new AssertionError(tableCups + " " + Arrays.toString(positions));
    }
    private static CopyableIntListIterator removeMoveCup(IntLinkedList tableCups, CopyableIntListIterator[] positions,
                                                         int currentCup) {
        var currentCupPosition = positions[currentCup];
        var moveCup            = currentCupPosition.nextInt();
        currentCupPosition.remove();
        var moveCupPosition    = positions[moveCup];
        if (currentCupPosition.previousInt() != moveCup) {
            throw new AssertionError();
        }
        positions[moveCup] = null;
        return moveCupPosition;
    }
    private static void prettyPrintCups(CopyableIntListIterator currentCupPosition, int currentCup) {
        var sb = new StringBuilder(127);
        sb.append("cups: ... ");
        var it = currentCupPosition.copy();
        it.back(5);
        for (var i = 10; i > 0; --i) {
            var  cup = it.nextInt();
            char left;
            char right;
            if (cup == currentCup) {
                left  = '(';
                right = ')';
            } else {
                left = right = ' ';
            }
            sb.append(left);
            sb.append(cup);
            sb.append(right);
        }
        sb.append(" ...\ncurrent cup: ");
        sb.append(currentCup);
        System.out.println(sb);
    }
    private static int max(IntCollection iterable) {
        var max = Integer.MIN_VALUE;
        var it  = iterable.iterator();
        for (var $ = iterable.size(); $ >= 0; --$) {
            var i = it.nextInt();
            if (i > max) {
                max = i;
            }
        }
        return max;
    }
    static void playCrabCups(int cupsIn, int moveCount) {
        var      cupCount  = countDigits(cupsIn);
        ByteList tableCups = new ByteArrayList(cupCount);
        var      positions = new byte[cupCount + 1];
        listTableCups(cupsIn, tableCups, positions);
        if (debug) {
            System.out.println("before:");
            System.out.println(tableCups);
            System.out.println(Arrays.toString(positions));
        }
        simulateMoves(tableCups, positions, moveCount);
        while (tableCups.getByte(0) != 1) {
            tableCups.add(tableCups.removeByte(0));
        }
        System.out.println("after:");
        System.out.println(tableCups);
        updatePositions(tableCups, positions, (byte) 0);
        System.out.println(Arrays.toString(positions));

    }

    static void simulateMoves(ByteList tableCups, byte[] positions, int moveCount) {
        byte currentCupPosition = 0;
        var  currentCup         = tableCups.getByte(currentCupPosition);
        var  moveCups           = new byte[3];
        while (moveCount > 0) {
            --moveCount;
            simulateMove(tableCups, positions, currentCupPosition, currentCup, moveCups);
            currentCupPosition = positions[currentCup];
            ++currentCupPosition;
            if (currentCupPosition >= tableCups.size()) {
                currentCupPosition -= tableCups.size();
            }
            currentCup          = tableCups.getByte(currentCupPosition);
        }
    }

    static void simulateMove(ByteList tableCups, byte[] positions, byte currentCupPosition, byte currentCup, byte[] moveCups) {
        if (debug) {
            prettyPrintCups(tableCups, currentCup);
        }
        for (byte moveIndex = 0; moveIndex < 3; ++moveIndex) {
            moveCups[moveIndex] = removeMoveCup(tableCups, positions, currentCup);
        }
        var destinationCup         = selectDestinationCup(tableCups, positions, currentCup);
        var destinationCupPosition = positions[destinationCup];
        if (debug) {
    prettyPrintState(tableCups, currentCup, moveCups, destinationCup);
}
        var insertionPosition      = destinationCupPosition;
        ++insertionPosition;
        if (insertionPosition >= tableCups.size()) {
            insertionPosition -= tableCups.size();
        }
        tableCups.addElements(insertionPosition, moveCups);
        updatePositions(tableCups, positions, insertionPosition);
    }

    private static void prettyPrintCups(ByteList tableCups, byte currentCup) {
        var sb = new StringBuilder(127);
        sb.append("cups: ");
        for (byte i = 0; i < tableCups.size(); ++i) {
            var  cup = tableCups.getByte(i);
            char left;
            char right;
            if (cup == currentCup) {
                left  = '(';
                right = ')';
            } else {
                left = right = ' ';
            }
            sb.append(left);
            sb.append(cup);
            sb.append(right);
        }
        System.out.println(sb);
    }

    private static void prettyPrintState(ByteList tableCups, byte currentCup, byte[] moveCups, byte destinationCup) {
        var sb = new StringBuilder();
        sb.append("pick up:");
        for (byte i = 0; i < moveCups.length; ++i) {
            var cup = moveCups[i];
            sb.append(' ');
            sb.append(cup);
        }
        sb.append("\ndestination: ");
        sb.append(destinationCup);
        sb.append('\n');
        System.out.println(sb);
    }

    static byte selectDestinationCup(ByteList tableCups, byte[] positions, byte currentCup) {
        --currentCup;
        for (var destinationCup = currentCup; destinationCup > 0; --destinationCup) {
            var destinationCupPosition = positions[destinationCup];
            if (destinationCupPosition >= 0) {
                return destinationCup;
            }
        }
        ++currentCup;
        for (var destinationCup = (byte) (positions.length - 1); destinationCup > currentCup; --destinationCup) {
            var destinationCupPosition = positions[destinationCup];
            if (destinationCupPosition >= 0) {
                return destinationCup;
            }
        }
        throw new AssertionError(tableCups + " " + Arrays.toString(positions));
    }

    static byte removeMoveCup(ByteList tableCups, byte[] positions, byte currentCup) {
        var currentCupPosition = positions[currentCup];
        var moveCupPosition = currentCupPosition;
        ++moveCupPosition;
        if (moveCupPosition >= tableCups.size()) {
            moveCupPosition -= tableCups.size();
        }
        var moveCup = tableCups.removeByte(moveCupPosition);
        updatePositions(tableCups, positions, moveCupPosition);
        positions[moveCup] = -1;
        return moveCup;
    }

    static void updatePositions(ByteList tableCups, byte[] positions, byte leastPosition) {
        for (var i = (byte) (tableCups.size() - 1); i >= leastPosition; --i) {
            positions[tableCups.getByte(i)] = i;
        }
    }

    static void listTableCups(int cupsIn, ByteList tableCups, byte[] positions) {
        while (cupsIn > 0) {
            tableCups.add((byte) (cupsIn % 10));
            cupsIn /= 10;
        }
        reverseByteList(tableCups);
        for (var i = (byte) (tableCups.size() - 1); i >= 0; --i) {
            var cup = tableCups.getByte(i);
            positions[cup] = i;
        }
    }

    static void reverseByteList(ByteList byteList) {
        for (var i = byteList.size() >> 1; i >= 0; --i) {
            var j = byteList.size() - 1 - i;
            var b = byteList.getByte(i);
            byteList.set(i, byteList.getByte(j));
            byteList.set(j, b);
        }
    }

    static int countDigits(int cupsIn) {
        var tens  = 1;
        var power = 0;
        while (tens < cupsIn) {
            tens *= 10;
            ++power;
        }
        return power;
    }

    static final int INPUT   = 476_138_259;
    static final int EXAMPLE = 389_125_467;
}
