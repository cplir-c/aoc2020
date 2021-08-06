package cplir_c.advent_of_code_2020.day20c;

import java.util.Random;

import cplir_c.advent_of_code_2020.Day20;


public class ProblemRunner {
    public static void main(String... args) {
        testTileSet();
        System.out.println(new TileProblem(Day20.EXAMPLE).getCornerProduct());
        System.out.println(new TileProblem(Day20.INPUT).getCornerProduct());

    }
    public static void testTileSet() {
        var tileSet  = makeRandomTileSet();
        var testLow  = new int[] {0, 2, 4, 6, 8};
        var testHigh = new int[] {0, 1, 2, 3, 4, 6, 7, 8, 9};
        var tileLow  = tileSetFromIntArray(tileSet, testLow);
        var tileHigh = tileSetFromIntArray(tileSet, testHigh);
        if (!tileHigh.containsAll(tileLow)) {
            throw new AssertionError();
        }
        var newTileLow = new TileSet<>(tileHigh);
        newTileLow.retainAll(tileLow);
        if (!newTileLow.equals(tileLow)) {
            throw new AssertionError();
        }
        tileHigh.removeAll(tileLow);
        var testMed = new int[] {1, 3, 7, 9};
        var tileMed = tileSetFromIntArray(tileSet, testMed);
        if (!tileHigh.equals(tileMed)) {
            throw new AssertionError();
        }
    }
    public static TileSet<RegisteredTile> tileSetFromIntArray(TileSet<RegisteredTile> allTiles, int[] tileIndexArray) {
        var tileSet = new TileSet<>(allTiles);
        tileSet.clear();
        for (int i : tileIndexArray) {
            tileSet.add(tileSet.allTiles[i]);
        }
        return tileSet;
    }
    private static TileSet<RegisteredTile> makeRandomTileSet() {
        var rand = new Random();
        var sb = new StringBuffer(200);
        for (var i = 0; i < 10; ++i) {
            sb.append("Tile 111");
            sb.append(i);
            sb.append(":\n");
            for (var j = 0; j < 10; ++j) { writeRandomTileLine(sb, rand); }
            sb.append('\n');
        }
        sb.setLength(sb.length() - 2);
        return new TileProblem(sb.toString()).allTiles();
    }
    private static void writeRandomTileLine(StringBuffer sb, Random rand) {
        var lineBits = rand.nextInt() & 0b1111_1111_11;
        for (var i = 0; i < 10; ++i) {
            if ((lineBits & 1) == 0) {
                sb.append('#');
            } else {
                sb.append('.');
            }
            lineBits >>>= 1;
        }
        sb.append('\n');
    }
}
