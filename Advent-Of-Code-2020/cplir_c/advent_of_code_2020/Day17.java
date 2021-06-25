package cplir_c.advent_of_code_2020;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;


public final class Day17 {

    private static final int NEIGHBORHOOD_SIZE = 3 * 3 * 3 - 1;
    private static final int NEIGHBORHOOD_SIZE_4D = 3 * 3 * 3 * 3 - 1;

    public static void main(String[] args) {
        findBootPopulation(GLIDER);
        findBootPopulation(INPUT);
        find4DBootPopulation(GLIDER);
        find4DBootPopulation(INPUT);
    }

    private static void find4DBootPopulation(String input) {
        var aliveCells    = parseInput4D(input);
        var cellsToUpdate = aliveCells.clone();
        System.out.println(prettyPrint4DCellArray(aliveCells.toLongArray()));

        initializeUpdates4D(cellsToUpdate);

        simulate4DCells(aliveCells, cellsToUpdate, 6);
        System.out.println("after 6 cycles " + aliveCells.size() + " are alive.");
    }

    private static void simulate4DCells(LongOpenHashSet aliveCells, LongOpenHashSet cellsToUpdate, int generations) {
        var neighborhood = new long[NEIGHBORHOOD_SIZE_4D];
        for (var generation = 0; generation < generations; ++generation) {
            var updateArray  = cellsToUpdate.toLongArray();
            cellsToUpdate.clear();
            System.out.println("updates: " + (updateArray.length));
            var oldLiveCells = aliveCells.clone();
            for (long cell : updateArray) {
                generate4DNeighborhood(cell, neighborhood);
                var neighborCount = countNeighbors(oldLiveCells, neighborhood);
                applyRule(aliveCells, cellsToUpdate, neighborhood, oldLiveCells, cell, neighborCount);
            }
        }
    }

    private static void initializeUpdates4D(LongOpenHashSet cellsToUpdate) {
        var neighborhood = new long[NEIGHBORHOOD_SIZE_4D];
        for (long cell : cellsToUpdate.toLongArray()) {
            generate4DNeighborhood(cell, neighborhood);
            addCellUpdate(neighborhood, cellsToUpdate, cell);
        }
    }

    private static long[] generate4DNeighborhood(long cell, long[] neighborhood) {
        var i  = 0;
        var ox = (int) (cell >> 48);
        var oy = ((int) (cell >> 16)) >> 16;
        var oz = ((int) cell) >> 16;
        var ow = (((int) cell) << 16) >> 16;
        var x  = -1;
        var y  = -1;
        var w  = -1;
        var z  = -1;
        while (w < 2) {
            while (z < 2) {
                while (y < 2) {
                    while (x < 2) {
                        if (z != 0 || y != 0 || x != 0 || w != 0) {
                            var newCell = encodeCellCoordinates(ox + x, oy + y, oz + z, ow + w);
                            neighborhood[i] = newCell;
                            ++i;
                        }
                        ++x;
                    }
                    ++y;
                    x = -1;
                }
                ++z;
                y = -1;
            }
            ++w;
            z = -1;
        }
        // System.out.println(prettyPrintCellArray(neighborhood));
        return neighborhood;
    }

    private static CharSequence prettyPrint4DCellArray(long[] cells) {
        var sb = new StringBuilder(cells.length * 22);
        sb.append("[\n\t");
        for (long cell : cells) {
            sb.append(prettyPrint4DCell(cell));
            sb.append("\n\t, ");
        }
        if (cells.length >= 1) {
            sb.setLength(sb.length() - 4);
        }
        sb.append("\n]");
        return sb;
    }

    static CharSequence prettyPrint4DCell(long cell) {
        var sb = new StringBuilder(22);
        sb.append("(x:");
        var ox = (int) (cell >> 48);
        sb.append(ox);
        sb.append(" y:");
        var oy = ((int) (cell >> 16)) >> 16;
        sb.append(oy);
        sb.append(" z:");
        var oz = ((int) cell) >> 16;
        sb.append(oz);
        sb.append(" w:");
        var ow = (((int) cell) << 16) >> 16;
        sb.append(ow);
        sb.append(')');
        return sb;
    }

    private static LongOpenHashSet parseInput4D(String input) {
        var     lines = LINE_SPLIT.split(input);
        var ls    = new LongOpenHashSet(0x7f);
        for (var y = lines.length - 1; y >= 0; --y) {
            var line = lines[y];
            for (var x = line.length() - 1; x >= 0; --x) {
                var cell = line.charAt(x);
                if (cell == '#') {
                    ls.add(encodeCellCoordinates(x, y, 0, 0));
                }
            }
        }
        System.out.println(ls);
        return ls;
    }

    private static long encodeCellCoordinates(int x, int y, int z, int w) {
        if (fitsSignedInNBits(x, 16) && fitsSignedInNBits(y, 16) && fitsSignedInNBits(z, 16) && fitsSignedInNBits(w, 16)) {
            x = (x << 16) | (y & 0xff_ff);
            z = (z << 16) | (w & 0xff_ff);
            return (((long) x) << 32L) | (z & 0xff_ff_ff_ffL);
        } else {
            throw new AssertionError("encoding failed " + x + " " + y + " " + z + " " + w);
        }
    }

    static final Pattern LINE_SPLIT = Pattern.compile("\n+");

    static void findBootPopulation(String input) {
        var aliveCells    = (LongOpenHashSet) parseInput(input);
        var cellsToUpdate = aliveCells.clone();
        System.out.println(prettyPrintCellArray(aliveCells.toLongArray()));

        initializeUpdates(cellsToUpdate);

        simulateCells(aliveCells, cellsToUpdate, 6);
        System.out.println("after 6 cycles " + aliveCells.size() + " are alive.");
    }

    static void initializeUpdates(LongOpenHashSet cellsToUpdate) {
        var neighborhood = new long[NEIGHBORHOOD_SIZE];
        for (long cell : cellsToUpdate.toLongArray()) {
            generateNeighborhood(cell, neighborhood);
            addCellUpdate(neighborhood, cellsToUpdate, cell);
        }
    }

    static void simulateCells(LongOpenHashSet aliveCells, LongOpenHashSet cellsToUpdate, int generations) {
        var neighborhood = new long[NEIGHBORHOOD_SIZE];
        for (var generation = 0; generation < generations; ++generation) {
            var oldLiveCells = aliveCells.clone();
            var updateArray  = cellsToUpdate.toLongArray();
            cellsToUpdate.clear();
            for (long cell : updateArray) {
                generateNeighborhood(cell, neighborhood);
                var neighborCount = countNeighbors(oldLiveCells, neighborhood);
                applyRule(aliveCells, cellsToUpdate, neighborhood, oldLiveCells, cell, neighborCount);
            }
        }
    }

    static void applyRule(LongOpenHashSet aliveCells, LongOpenHashSet cellsToUpdate, long[] neighborhood,
                          LongOpenHashSet oldLiveCells, long cell, short neighborCount) {
        if (oldLiveCells.contains(cell)) {
            if (neighborCount < 2 || neighborCount > 3) {
                // kill cell
                aliveCells.remove(cell);
                addCellUpdate(neighborhood, cellsToUpdate, cell);
            }
        } else if (neighborCount == 3) {
            // revive cell
            aliveCells.add(cell);
            addCellUpdate(neighborhood, cellsToUpdate, cell);
        }
    }

    static void addCellUpdate(long[] neighborhood, LongOpenHashSet cellsToUpdate, long cell) {
        for (long neighbor : neighborhood) {
            cellsToUpdate.add(neighbor);
        }
        cellsToUpdate.add(cell);
    }

    static short countNeighbors(LongOpenHashSet oldLiveCells, long[] neighborhood) {
        short neighborCount = 0;
        for (long neighbor : neighborhood) {
            if (oldLiveCells.contains(neighbor)) {
                ++neighborCount;
            }
        }
        return neighborCount;
    }

    private static long[] generateNeighborhood(long cell, long[] neighborhood) {
        // System.out.println("generating neighborhood of " + prettyPrintCell(cell));
        var i = 0;
        var ox = (int) (cell >> 43);
        var oy = ((int) (cell >> 11)) >> 11;
        var oz = (((int) cell) << 10) >> 10;
        var x  = -1;
        var y  = -1;
        var z  = -1;
        while (z < 2) {
            while (y < 2) {
                while (x < 2) {
                    if (z != 0 || y != 0 || x != 0) {
                        var newCell = encodeCellCoordinates(ox + x, oy + y, oz + z);
                        neighborhood[i] = newCell;
                        ++i;
                    }
                    ++x;
                }
                ++y;
                x = -1;
            }
            ++z;
            y = -1;
        }
        // System.out.println(prettyPrintCellArray(neighborhood));
        return neighborhood;
    }

    static CharSequence prettyPrintCellArray(long[] cells) {
        var sb = new StringBuilder(cells.length * 22);
        sb.append("[\n\t");
        for (long cell : cells) {
            sb.append(prettyPrintCell(cell));
            sb.append("\n\t, ");
        }
        if (cells.length >= 1) {
            sb.setLength(sb.length() - 4);
        }
        sb.append("\n]");
        return sb;
    }


    static CharSequence prettyPrintCell(long cell) {
        var sb = new StringBuilder(22);
        sb.append("(x:");
        var ox = (int) (cell >> 43);
        sb.append(ox);
        sb.append(" y:");
        var oy = ((int) (cell >> 11)) >> 11;
        sb.append(oy);
        sb.append(" z:");
        var oz = (((int) cell) << 10) >> 10;
        sb.append(oz);
        sb.append(')');
        return sb;
    }

    static LongSet parseInput(String input) {
        var     lines = LINE_SPLIT.split(input);
        LongSet ls    = new LongOpenHashSet(0x7f);
        for (var y = lines.length - 1; y >= 0; --y) {
            var line = lines[y];
            for (var x = line.length() - 1; x >= 0; --x) {
                var cell = line.charAt(x);
                if (cell == '#') {
                    ls.add(encodeCellCoordinates(x, y, 0));
                }
            }
        }
        System.out.println(ls);
        return ls;
    }

    static final int Z_MASK = (1 << 22) - 1;
    static final int X_MASK = Z_MASK >>> 1;

    static long encodeCellCoordinates(int x, int y, int z) {
        if (fitsSignedInNBits(x, 21) && fitsSignedInNBits(y, 21) && fitsSignedInNBits(z, 22)) {
            return (((long) (x & X_MASK)) << 43L) | (((long) (y & X_MASK)) << 22L) | (z & Z_MASK);
        } else {
            throw new AssertionError("encoding failed " + x + " " + y + " " + z);
        }
    }

    static boolean fitsSignedInNBits(int value, int bits) {
        bits = (Integer.SIZE - bits);
        return ((value << bits) >> bits) == value;
    }

    static final String GLIDER = ".#.\n" + "..#\n" + "###";
    static final String INPUT  = "##....#.\n" + "#.#..#..\n" + "...#....\n" + "...#.#..\n" + "###....#\n" + "#.#....#\n"
            + ".#....##\n" + ".#.###.#";
}
