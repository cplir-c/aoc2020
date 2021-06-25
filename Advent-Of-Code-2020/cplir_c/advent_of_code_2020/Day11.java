package cplir_c.advent_of_code_2020;

import java.util.BitSet;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;


public class Day11 {
    public static void main(String[] args) {
        countOccupied(INPUT);
        countOccupied(EXAMPLE);
    }

    static final Pattern SPACE_SPLIT = Pattern.compile("\\s+");

    private static int countOccupied(String input) {
        var lines = SPACE_SPLIT.split(input);
        var      width = lines[0].length();
        for (var i = 1; i < lines.length; ++i) {
            var len = lines[i].length();
            if (width < len) {
                width = len;
            }
        }
        var height = lines.length;
        System.out.println(width);
        System.out.println(height);
        var    length   = width * height;
        var occupied = new BitSet(length);
        var floor    = new BitSet(length);
        var updateSet = new BitSet(length);
        var bitIndex = 0;
        for (String line : lines) {
            for (var i = 0; i < line.length(); ++i) {
                var currentChar = line.charAt(i);
                if (currentChar == '.') {
                    floor.set(bitIndex);
                } else if (currentChar == 'L') {
                    updateSet.set(bitIndex);
                }
                ++bitIndex;
            }
        }
        simulateSitting(width, length, floor, occupied, updateSet);
        final var finalOccupied = occupied.cardinality();
        occupied.clear();
        // reset the update set
        updateSet.set(0, length);
        updateSet.xor(floor);
        simulateSightSeating(width, length, floor, occupied, updateSet);
        return finalOccupied;
    }
    private static void simulateSightSeating(int width, int length, BitSet floor, BitSet occupied, BitSet updateSet) {
        var seats = length - floor.cardinality();
        var visibleSeats = findVisibleSeats(width, length, floor);
        var newOccupied = (BitSet) occupied.clone();
        var newUpdateSet = new BitSet(occupied.length());
        while (updateSet.nextSetBit(0) >= 0) {
            for (var updateIndex = updateSet.nextSetBit(0); updateIndex >= 0 && updateIndex < length;
                 updateIndex = updateSet.nextSetBit(updateIndex + 1)) {
                var neighbors = visibleSeats[updateIndex];
                var count     = countOccupied(neighbors, occupied);
                if (occupied.get(updateIndex)) {
                    // occupied
                    if (count >= 5) {
                        newOccupied.clear(updateIndex);
                        addUpdates(newUpdateSet, updateIndex, neighbors);
                    } else {
                        newUpdateSet.clear(updateIndex);
                    }
                } else {
                    // empty
                    maybeOccupyEmptySeat(newOccupied, newUpdateSet, updateIndex, neighbors, count);
                }
            }
            copyBitSets(occupied, updateSet, newOccupied, newUpdateSet);
            if (newUpdateSet.length() > length) {
                newUpdateSet.clear(length, newUpdateSet.length());
            }
        }
        System.out.println(prettyPrintState(width, length, floor, occupied, updateSet));
        System.out.println(
            "the seating area with " + (length - floor.cardinality()) + " seats halted with " + occupied.cardinality()
                    + " visible seats occupied."
        );
    }
    private static void maybeOccupyEmptySeat(BitSet newOccupied, BitSet newUpdateSet, int updateIndex, IntList neighbors,
                                        int count) {
        if (count == 0) {
            newOccupied.set(updateIndex);
            addUpdates(newUpdateSet, updateIndex, neighbors);
        } else {
            newUpdateSet.clear(updateIndex);
        }
    }
    private static void copyBitSets(BitSet occupied, BitSet updateSet, BitSet newOccupied, BitSet newUpdateSet) {
        occupied.clear();
        occupied.or(newOccupied);
        updateSet.clear();
        updateSet.or(newUpdateSet);
    }
    private static int countOccupied(IntList neighbors, BitSet occupied) {
        var count = 0;
        for (int neighbor : neighbors) {
            if (occupied.get(neighbor)) {
                ++count;
            }
        }
        return count;
    }
    private static IntList[] findVisibleSeats(int width, int length, BitSet floor) {
        ObjectList<IntList> visibleSeatsMap = new ObjectArrayList<>(length);
        for (var i = 0; i < length; ++i) {
            if (floor.get(i)) {
                visibleSeatsMap.add(null);
            } else {
                var seatsList = new IntArrayList(8);
                findVisibleSeats(width, length, floor, i, seatsList);
                visibleSeatsMap.add(seatsList);
            }
        }
        return visibleSeatsMap.toArray(new IntList[visibleSeatsMap.size()]);
    }
    private static void findVisibleSeats(int width, int length, BitSet floor, int i, IntArrayList seatsList) {
        final var height = length / width;

        final var row    = i / width;
        final var col    = i % width;
        final var upRow   = height - row - 1;
        final var leftCol = width - col - 1;

        final var upLimit    = col;
        assert col == upLimit % width;
        final var downLimit  = length - leftCol - 1;
        assert col == downLimit % width;
        final var leftLimit  = i - col;
        assert row == leftLimit / width;
        final var rightLimit = i + leftCol;
        assert row == rightLimit / width;

        final int upLeftLimit;
        final int upRightLimit;
        final int downLeftLimit;
        final int downRightLimit;

        if (row > col) {
            // lower left triangle  [.\ ]
            upLeftLimit    = leftLimit - col * width;
            downRightLimit = downLimit + upRow;
        } else {
            // upper right triangle [ \.]
            upLeftLimit    = upLimit - row;
            downRightLimit = rightLimit + leftCol * width;
        }
        if (row > leftCol) {
            // lower right triangle [ /.]
            upRightLimit  = rightLimit - leftCol * width;
            downLeftLimit = downLimit - upRow;
        } else {
            // upper left triangle  [./ ]
            upRightLimit  = upLimit + row;
            downLeftLimit = leftLimit + col * width;
        }

        var leftCond  = i > leftLimit;
        var rightCond = i < rightLimit;
        if (i >= width) {
            projectView(i, upLimit, floor, -width, seatsList);
            if (leftCond) {
                final var upLeftDir = -width - 1;
                projectView(i, upLeftLimit, floor, upLeftDir, seatsList);
            }
            if (rightCond) {
                final var upRightDir = -width + 1;
                projectView(i, upRightLimit, floor, upRightDir, seatsList);
            }
        }
        if (leftCond) {
            projectView(i, leftLimit, floor, -1, seatsList);
        }
        if (rightCond) {
            projectView(i, rightLimit, floor, 1, seatsList);
        }
        if (i < downLimit) {
            projectView(i, downLimit, floor, width, seatsList);
            if (leftCond) {
                final var downLeftDir = width - 1;
                projectView(i, downLeftLimit, floor, downLeftDir, seatsList);
            }
            if (rightCond) {
                final var downRightDir = width + 1;
                projectView(i, downRightLimit, floor, downRightDir, seatsList);
            }
        }
        var it = seatsList.iterator();
        for (var j = it.nextInt(); it.hasNext(); j = it.nextInt()) {
            if (j >= length) {
                System.out.println(i);
                System.out.println(seatsList);
                throw new AssertionError();
            }
        }
    }
    private static void projectView(int start, int limit, BitSet floor, int direction, IntList seatsList) {
        var i = start;
        do {
            i += direction;
            if (!floor.get(i)) {
                seatsList.add(i);
                return;
            }
        } while (i != limit);
    }
    // the rules are B0/S0123, but with some dead cells
    private static void simulateSitting(int width, int length, BitSet floor, BitSet occupied, BitSet updateSet) {
        var newOccupied = (BitSet) occupied.clone();
        var newUpdateSet = new BitSet(occupied.length());
        IntList neighbors    = new IntArrayList(8);
        while (updateSet.nextSetBit(0) >= 0) {
            for (var updateIndex = updateSet.nextSetBit(0); updateIndex >= 0;
                 updateIndex = updateSet.nextSetBit(updateIndex + 1)) {
                var count = findNeighbors(width, floor, updateIndex, occupied, neighbors);
                if (occupied.get(updateIndex)) {
                    // occupied
                    if (count >= 4) {
                        newOccupied.clear(updateIndex);
                        addUpdates(newUpdateSet, updateIndex, neighbors);
                    } else {
                        newUpdateSet.clear(updateIndex);
                    }
                } else {
                    maybeOccupyEmptySeat(newOccupied, newUpdateSet, updateIndex, neighbors, count);
                }
            }
            copyBitSets(occupied, updateSet, newOccupied, newUpdateSet);
        }
        System.out.println(prettyPrintState(width, length, floor, occupied, updateSet));
        System.out.println(
            "the seating area with " + (length - floor.cardinality()) + " seats halted with " + occupied.cardinality()
                    + " seats occupied."
        );
    }
    private static String prettyPrintState(int width, int length, BitSet floor, BitSet occupied, BitSet updateSet) {
        var s = new StringBuilder(length * 2);
        for (var lineI = 0; lineI < length; lineI += width) {
            var nextLineI = lineI + width;
            for (var i = lineI; i < nextLineI; ++i) {
                if (floor.get(i)) {
                    s.append('.');
                } else if (updateSet.get(i)) {
                    if (occupied.get(i)) {
                        s.append('$');
                    } else {
                        s.append('A');
                    }
                } else if (occupied.get(i)) {
                    s.append('#');
                } else {
                    s.append('L');
                }
            }
            s.append('\n');
        }
        return s.toString();
    }
    private static void addUpdates(BitSet newUpdateSet, int updateIndex, IntList neighbors) {
        newUpdateSet.set(updateIndex);
        for (int index : neighbors) {
            newUpdateSet.set(index);
        }
    }
    private static int addNeighbor(IntList neighbors, BitSet floor, BitSet occupied, int count, int index) {
        if (!floor.get(index)) {
            neighbors.add(index);
            if (occupied.get(index)) {
                ++count;
            }
        }
        return count;
    }
    private static int findNeighbors(int width, BitSet floor, int updateIndex, BitSet occupied, IntList neighbors) {
        var count = 0;
        var up    = updateIndex >= width;
        var down  = updateIndex < occupied.size() - width;
        var col = updateIndex % width;
        var left = col > 0;
        var right = col < width - 1;
        neighbors.clear();
        if (up) {
            var upPos = updateIndex - width;
            count = addNeighbor(neighbors, floor, occupied, count, upPos);
            if (left) {
                count = addNeighbor(neighbors, floor, occupied, count, upPos - 1);
            }
            if (right) {
                count = addNeighbor(neighbors, floor, occupied, count, upPos + 1);
            }
        }
        if (left) {
            count = addNeighbor(neighbors, floor, occupied, count, updateIndex - 1);
        }
        if (right) {
            count = addNeighbor(neighbors, floor, occupied, count, updateIndex + 1);
        }
        if (down) {
            var downPos = updateIndex + width;
            count = addNeighbor(neighbors, floor, occupied, count, downPos);
            if (left) {
                count = addNeighbor(neighbors, floor, occupied, count, downPos - 1);
            }
            if (right) {
                count = addNeighbor(neighbors, floor, occupied, count, downPos + 1);
            }
        }
        return count;
    }

    static final String EXAMPLE = "L.LL.LL.LL\n" + "LLLLLLL.LL\n" + "L.L.L..L..\n" + "LLLL.LL.LL\n" + "L.LL.LL.LL\n"
            + "L.LLLLL.LL\n" + "..L.L.....\n" + "LLLLLLLLLL\n" + "L.LLLLLL.L\n" + "L.LLLLL.LL";
    static final String INPUT
                                        = "LLLLLL.LLLLLLLLLLLL.LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LL\n"
                + "LLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.L.LLLLLL.LL.LLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLL..LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLL.LL.LLLLLLLLLLLLLL.LL.LLLLLLLLL\n"
                + "LLLLLL.LLLLL..LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLLL.L.LLLLL.L\n"
                + "LLLLLL.LLLLLL.LLL.L.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLL..LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLL.LLL\n"
                + ".LLLLLLLLLLLL.LLL.L.LLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLL.LLLLLL.L.L.LLLLLLLLLLLLL.L.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL..LLLLLLLLLL..LLLLLLL.LLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLL.LLLL..LLLLLLLL\n"
                + "..........LL....L..LL..L.....L...L....L............L.LL...L.......L.L.LL...L.L...L....L.L....LLL..\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLL.L.LLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LL..LLLLLLL.LLLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLL.L.LLLLLLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLL.LLL\n"
                + ".LLLLL..LL.LL..LLL.LLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLL\n"
                + "L.LLLL.LLL.LLLLLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLL.LLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLL.LLLL\n"
                + "LLLLLL.LLLLLLLLLLLLLLLLLLLL.L.LLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "...LLLLL.......L.LL..L..LL...LL..L.....L...L...LLL.L.L.....L.L....L.L......LL..L.L.L..LL.L.....LL.\n"
                + "LLLLLLLLLLL.L.LLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.LLLLLL.L.LL.LLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLL.LLL.LLLLLLLL.LLLLLLLL..LLLLLLLLLLLLLL.LL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLL.L.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLL..LL.L.LLLLLLL.LLLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLL.L.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLLL.LLLL.LLLLLLLL.LLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.L.LLLLLLLLL\n"
                + "L.LLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLL.LL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + ".LLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.L.LLL.LLLLLLLL.LLL..LLLLLLL.LL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.L.LLLLLL.LLLLLLLLLLLLLLLLL.\n"
                + "L.LL..LLL.L.........L.L....L...LL.L......L..LL.......L.....L.LL.....L..L.L.....L.....L.L...L.L....\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL..LLLL..LLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n"
                + "LLL.L..LLLL.L.LLLLLLLLLLLLL.LLLLL.LLLLL.L.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL..LLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLL.LLLLLLL..LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LL.LLLLLLLLLLLLLLL..LLLLLLLLLLLL..LLLLLLLLL.LLLLLLLLL.LLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.L.LLLLLLLLL\n"
                + "LLLLLL..LLLLLLLLLLLLLLLLL.L.LLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "..L...L...L..L..LL...L..........L.....L......L....L.L..L.L.L..L.LL....L..L...L...L......L.....L..L\n"
                + "LLLLLLLLLLLL..LLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LL.LLLLLLLLLLLLL.L.LLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.L.LLLLL.LLLLLLLLLLLLLLLLL..LLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLLLLL.LLLL.LLLLL.LLLLLLLLLLLLLL.LLLL.LLLLLLLLL.LLLLLL.LLLLLL.LLLLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLL.LLLL.LLLLLLLL.LLLLL.LLLLLLLLLL.L\n"
                + "LLLLLLLLLLLLL.L.LLL.LLLLLLL.LLLLL.LLLLL.LLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLL..LLLLLLLLL\n"
                + "LLL..L..LLLL.LLL.L......L...LL.L..L.L...L...LL....L.L.L.L................L....L..L...L......LLL.L.\n"
                + "LLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLL..LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLL..LLLLLLL.LLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LL.LLLLLLLL.LLLLLL.LLLLLLLLLL.LLLLLLLLL\n"
                + ".LLLLL.LLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLL.LLLL\n"
                + "LLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLL..LLLLLLLL.LLLLLLLL.LLLLLLLL.L.LLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.LLLLL.LL.LLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLL.LL.LLLLL.LLLLL.LLLLLLLL..LLL.LLLLL.L.LLLLLLLLLLLLLLLLL.LLL.LLLL.LLLLLLLLL\n"
                + "LL.LLL.LLLLLL.LLLLL.LLLLLLLLLLLLL.LL.LLLLLLLLLLL.LLLLLLLLLLLL.LLLLLLLL.LLL.L.LL.LLLLLLLLLLL.LLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL..LLLLLLLLLLLLLLLLLL.L.LLLLLL.LLLL.LLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + ".LLLLL..L.L.L...L...L..LL.L.L.LL.L...L.L.L...................LL..L...L......L..L.........L.....LL.\n"
                + "LLLL.LLLLLLLL.LLLLL.LLLLLLL.LLLLLL.LLLLLLLLLLLLL.LLLL.L.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLLL.L.LLLL.LLLLL.LLLLLLLLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.LLLLLLLL.LLLL.LLL.LL..LLLL.\n"
                + ".LLLLL.LLLLLLLLLLLL.LLLL.LLLLLLLL.LLLLLLLLLLLLLL.L.LLLL.LLLLLLLLLLLLLL.LLLLLLL..LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLL...LLLLL.LLLLLLL.LLLL.LLLLLL..LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL\n"
                + "LLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLL.LLLLLLLLL.LLLLLLL.LLLLLLLL..L.LLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LL.LLLLLLLLLLLLLL.\n"
                + "LLL.LL.LLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLL..L.LLLLL.LLLLLLLLLLLLLLLLL.L.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "L.LL.L.L...L.LL.L.LLL...L.......LLL.L.LL..LLLL.L.L...L..LL.L..LL..L.L..LLLL.L.L..L.L....L....L...L\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLL.LLLL.L.LLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLL.LL.LLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.L.LLLL.LLLLL.LLL.LLL.LLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLL..LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLL.LLLLLLLL.LLLLL..LLLLLLLLLLLL.LL.LL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.L.LLLLLL.LLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLL..LLLLLLL.LL.LLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLL.LLLL..LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLL.LLLLL\n"
                + "L.L..L................L..LL.....LL...L............L....LL..LL.LLL.L.............L..L...L.....LLL..\n"
                + "LLLLLLLLLLLLLLLL.LL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLL.LLLLLL.LLLLLLL.LLLLL.LLL.LLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.\n"
                + "LLLLLL.LLLLLLLLLLLLLL.LLLLL.LLLLL.LL.LL.LLLLLLLL.LLLL.LLLLLLL.LL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LLLL.LL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLL..LLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLL.LLLLLLL.LLL.LLLL.LLLLLL.L.LLLLLLLL.LLLLLLLLL\n"
                + "L.L..L...LL.L.....LL..LL.L.....L.L..L....L..L.L...L.LLLL....L...LL......L..L....LL..L..L........LL\n"
                + "LLLLLLLLLLLLLLLLLLL.LLLL.LL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLL.LLL.LLLL.LLLLLLLL.LLLLL.LLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLLLLL.LL.LLLLLL.LLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL\n"
                + "LLLLLL.LLLLL.LLLLLL.LLLLLLL.LLLLLLLLLLL..LLLLLLL.L.LL.LLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLL..LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLL.LLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLLLLLLL.LL.LLLLL.LLLLLLLLLLLLLLLL.LLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLL.L.LLLLLLLLLLLLL.LLLLLLLLL.L.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLL..LLLLLLLL.LLLLLLLLL\n"
                + "LLLLL..LLLL.L.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLLLLL..LLLLLLLL.LLLLLLLLL\n"
                + "L.LLLLLLLLLLLLLLLLL.LLLLLLL.LL.LL.LLLLL.LLLLLLLLLLLLL.LL.LLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LL.L......L......L...L..LLL.....L.L..L.L...L......L..L..L.....L...LL.L.LL.L.LLL....L.LLL....L..L..\n"
                + "LLLLLLLLLL.LLLLLLLLLL.LL.LL.LLLLLLLLLLL.L..LLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLL.LL..LLL.LLLLLLL.LLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL..LL.LL.LLLLL.LLLLLLL.L.LLLLLLLLL.LLLLLLLL.LLLLLLLL.LLL.LLL.LLLL.LLLLLLLLLLLLLLLL..LLLLLLLLL\n"
                + "LLLLLLLLLLL.LLLLLLL.LLL.LLLLLLLLL.LLLLL.LLLLLLLL.LLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLL.L.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.L.LLLLLLLLLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL..LLLL.LLLLLLLLLLLLLLLL.LL.LLLL.LLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLLL.L.L.LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLL.LLLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LL.LLLLLLLLL.LLLLLLL.LLLLLL\n"
                + "L..L.LL......LLL....LLLL.......L.L..L..L....LLLLL..LLLLL..L..L.L.L........LLL...LL.L......LLL.....\n"
                + "LLLLLL.LLLLLLLLLLLLLLLLLLLL.LLLL..LLLLLLLLLLLLLLLLLLL.LLLLLLLLL.LL.LLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLL.LLLL.LLLLLLLL..LLLLLL.LLLLL.LLLL...LLLLL.L.LLLL.LLLLLLL..LLLLLLLL.LLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLL.LLLLLLLL.LLLLL.LLL\n"
                + "LLLLLLLLLLLLL.LLLLL.L.LLLLL.LLLLLLLLLLL.LLLLLLLLLL.LL.LLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLL.LLLL\n"
                + "LLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL\n"
                + "LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLL.L.LLLLLL.LLLLLLLLLLLLLLLLL.\n"
                + "LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLL";


}
