package cplir_c.advent_of_code_2020;

import java.util.Arrays;
import java.util.Objects;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;


public class Completion implements Comparable<Completion> {
    final Tile[]          tiles;
    final int             tileCount;
    final int             edgeLength;
    final int             hashCode;
    final int[]           adjancentSpaces;
    final int             maxCount;
    final IntSet usedTiles;

    public Completion(Tile[] tiles, int edgeLength, byte[] neighborCounts) {
        this.tiles      = tiles;
        this.tileCount  = countTiles(this.tiles);
        this.edgeLength = edgeLength;
        this.hashCode   = System.identityHashCode(Boolean.FALSE) ^ (Arrays.hashCode(tiles) * this.tileCount + this.edgeLength);
        findAdjancentCount(this.edgeLength, neighborCounts, tiles);
        var neighboringSpaces = new IntArrayList(4);
        this.maxCount = findAdjancentSpaces(this.edgeLength, neighboringSpaces, neighborCounts, tiles);
        this.adjancentSpaces = neighboringSpaces.toIntArray();
        if (this.tileCount > 0) {
            this.usedTiles = initializeUsedTiles(this.tileCount, tiles);
        } else {
            this.usedTiles = IntSets.EMPTY_SET;
        }
    }
    static IntSet initializeUsedTiles(int tileCount, Tile[] tiles) {
        var usedTiles = new IntOpenHashSet(tileCount);
        for (var i = tiles.length - 1; i >= 0; --i) {
            var tile = tiles[i];
            if (tile != null) {
                usedTiles.add(tile.id);
            }
        }
        usedTiles.trim();
        return usedTiles;
    }
    static int countTiles(Tile[] a) {
        var count = 0;
        for (var i = a.length - 1; i >= 0; --i) {
            if (a[i] != null) {
                ++count;
            }
        }
        return count;
    }
    static void findAdjancentCount(int edgeCount, byte[] adjancentCount, Tile[] possibility) {
        Arrays.fill(adjancentCount, (byte) 0);
        for (var row = possibility.length - edgeCount; row >= 0; row -= edgeCount) {
            for (var col = row + edgeCount - 1; col >= row; --col) {
                var tile = possibility[col];
                if (col > row) {
                    var prev     = col - 1;
                    var prevTile = possibility[prev];
                    incrementCounts(adjancentCount, col, tile, prev, prevTile);
                }
                if (row > 0) {
                    var prev     = col - edgeCount;
                    var prevTile = possibility[prev];
                    incrementCounts(adjancentCount, col, tile, prev, prevTile);
                }
            }
        }
    }

    static void incrementCounts(byte[] adjancentCount, int col, Tile tile, int prev, Tile prevTile) {
        if (tile != null) {
            ++adjancentCount[prev];
        }
        if (prevTile != null) {
            ++adjancentCount[col];
        }
    }
    static byte findAdjancentSpaces(int edgeCount, IntList adjancentSpaces, byte[] adjancentCount, Tile[] possibility) {
        adjancentSpaces.clear();
        var maxCount = (byte) 0;
        for (var i = possibility.length - 1; i >= 0; --i) {
            var count = adjancentCount[i];
            if (possibility[i] == null) {
                if (count > maxCount) {
                    if (maxCount == 0) {
                        adjancentSpaces.clear();
                    }
                    maxCount = count;
                } else if (count == maxCount) {
                    adjancentSpaces.add(i);
                } else {
                    // ignore this unoptimal space
                }
            }
        }
        return maxCount;
    }
    @Override
    public int hashCode() { return this.hashCode; }
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        } else if (otherObj instanceof Completion && this.hashCode == Objects.hashCode(otherObj)) {
            var other = (Completion) otherObj;
            return other.tileCount == this.tileCount && other.edgeLength == this.edgeLength
                && Arrays.equals(this.tiles, other.tiles);
        }
        return false;
    }
    @Override
    public int compareTo(Completion b) {
        if (this == b) {
            return 0;
        }
        {
            var countComparison = Integer.compare(this.tileCount, b.tileCount);
            if (countComparison != 0) {
                return countComparison;
            }
        }
        var aHash = this.hashCode;
        var bHash = b.hashCode;
        if (aHash == bHash && this.equals(b)) {
            return 0;
        }
        {
            var maxCountComparison = Integer.compare(this.maxCount, b.maxCount);
            if (maxCountComparison != 0) {
                return maxCountComparison;
            }
        }
        {
            var maxCountCountComparison = Integer.compare(this.adjancentSpaces.length, b.adjancentSpaces.length);
            if (maxCountCountComparison != 0) {
                return maxCountCountComparison;
            }
        }
        // at this point, I don't really care which one goes first.
        var hashComparison = Integer.compare(aHash, bHash);
        if (hashComparison != 0) {
            return hashComparison;
        }
        return -1;
    }

    void addShiftedCompletions(PriorityQueue<Completion> completions, ObjectSet<Completion> addedCompletions,
                               byte[] neighborCounts) {
        var possibility = this.tiles;
        var up          = true;
        var down        = true;
        var left        = true;
        var right       = true;
        for (var row = possibility.length - this.edgeLength; row >= 0; row -= this.edgeLength) {
            for (var col = row + this.edgeLength - 1; col >= row; --col) {
                if (possibility[col] != null) {
                    if (col == row) {
                        left = false;
                    } else if (col == row + this.edgeLength - 1) {
                        right = false;
                    } else {
                        // this square isn't blocking left or right
                    }
                    if (row == 0) {
                        up = false;
                    } else if (row == possibility.length - this.edgeLength) {
                        down = false;
                    } else {
                        // this square isn't blocking up or down
                    }
                }
            }
        }
        if (up) {
            var upArray = new Tile[possibility.length];
            System.arraycopy(possibility, this.edgeLength, upArray, 0, possibility.length - this.edgeLength);
            var completion = new Completion(upArray, this.edgeLength, neighborCounts);
            completion.addCompletionIfNew(completions, addedCompletions);
        }
        if (down) {
            var downArray = new Tile[possibility.length];
            System.arraycopy(possibility, 0, downArray, this.edgeLength, possibility.length - this.edgeLength);
            var completion = new Completion(downArray, this.edgeLength, neighborCounts);
            completion.addCompletionIfNew(completions, addedCompletions);
        }
        if (left) {
            var leftArray = new Tile[possibility.length];
            for (var row = possibility.length - this.edgeLength; row >= 0; row -= this.edgeLength) {
                System.arraycopy(possibility, row + 1, leftArray, row, this.edgeLength - 1);
            }
            var completion = new Completion(leftArray, this.edgeLength, neighborCounts);
            completion.addCompletionIfNew(completions, addedCompletions);
        }
        if (right) {
            var rightArray = new Tile[possibility.length];
            for (var row = possibility.length - this.edgeLength; row >= 0; row -= this.edgeLength) {
                System.arraycopy(possibility, row, rightArray, row + 1, this.edgeLength - 1);
            }
            var completion = new Completion(rightArray, this.edgeLength, neighborCounts);
            completion.addCompletionIfNew(completions, addedCompletions);
        }
    }

    void addCompleterCompletions(Short2ObjectMap<Byte2ObjectMap<ObjectSet<Tile>>> tilesByEdge,
                                 PriorityQueue<Completion> completions, ObjectSet<Completion> addedCompletions,
                                 byte[] neighborCounts) {
        var possibility = this.tiles;
        for (var adjancentSpaceListIndex = this.adjancentSpaces.length - 1; adjancentSpaceListIndex >= 0;
             --adjancentSpaceListIndex) {
            var             adjancentSpaceIndex = this.adjancentSpaces[adjancentSpaceListIndex];
            var             col                 = adjancentSpaceIndex % this.edgeLength;
            var             row                 = adjancentSpaceIndex - col;
            System.out.println("inspecting adjancent space " + adjancentSpaceIndex);
            ObjectSet<Tile> possibilities       = null;
            if (col > 0) {
                // neighbor is on the left
                var neighbor     = adjancentSpaceIndex - 1;
                var neighborTile = possibility[neighbor];
                if (neighborTile != null) {
                    var neighborRequired = tilesByEdge.get(neighborTile.edge1).get((byte) 5);
                    if (neighborRequired != null) {
                        possibilities = new ObjectOpenHashSet<>(neighborRequired);
                    }
                }
            }
            if (col < this.edgeLength - 1) {
                // neighbor is on the right
                var neighbor     = adjancentSpaceIndex + 1;
                var neighborTile = possibility[neighbor];
                if (neighborTile != null) {
                    var neighborRequired = tilesByEdge.get(neighborTile.edge3).get((byte) 1);
                    if (neighborRequired != null) {
                        possibilities = replaceOrFilter(possibilities, neighborRequired);
                    }
                }
            }
            if (row > 0) {
                // neighbor is above
                var neighbor     = adjancentSpaceIndex - this.edgeLength;
                var neighborTile = possibility[neighbor];
                if (neighborTile != null) {
                    var neighborRequired = tilesByEdge.get(neighborTile.edge4).get((byte) 3);
                    if (neighborRequired != null) {
                        possibilities = replaceOrFilter(possibilities, neighborRequired);
                    }
                }
            }
            if (row < possibility.length - this.edgeLength) {
                // neighbor is below
                var neighbor     = adjancentSpaceIndex + this.edgeLength;
                var neighborTile = possibility[neighbor];
                if (neighborTile != null) {
                    var neighborRequired = tilesByEdge.get(neighborTile.edge2).get((byte) 7);
                    if (neighborRequired != null) {
                        possibilities = replaceOrFilter(possibilities, neighborRequired);
                    }
                }
            }
            if (possibilities == null || possibilities.isEmpty()) {
                System.out.println("hit a dead end:");
                System.out.println(this);
                System.out.println();
                continue;
            }
            for (Tile newTile : possibilities) {
                if (this.usedTiles.contains(newTile.id)) {
                    System.out.println("skipped matching tile due to duplicate ids");
                    continue;
                }
                var newPossibility = possibility.clone();
                newPossibility[adjancentSpaceIndex] = newTile;
                var newCompletion = new Completion(newPossibility, this.edgeLength, neighborCounts);
                newCompletion.addCompletionIfNew(completions, addedCompletions);
            }
        }
    }
    static ObjectSet<Tile> replaceOrFilter(ObjectSet<Tile> possibilities, ObjectSet<Tile> neighborRequired) {
        if (possibilities == null) {
            possibilities = new ObjectOpenHashSet<>(neighborRequired);
        } else {
            possibilities.retainAll(neighborRequired);
        }
        return possibilities;
    }
    void addCompletionIfNew(PriorityQueue<Completion> completions, ObjectSet<Completion> addedCompletions) {
        if (addedCompletions.add(this)) {
            completions.enqueue(this);
        }
    }
    @Override
    public String toString() { var sb = new StringBuilder(this.tiles.length * 4);
        sb.append("[ ");
        var possibility = this.tiles;
        for (var row = possibility.length - this.edgeLength; row >= 0; row -= this.edgeLength) {
            for (var col = row + this.edgeLength - 1; col >= row; --col) {
                var tile = possibility[col];
                if (tile != null) {
                    sb.append(Integer.toHexString(tile.hash));
                } else {
                    sb.append(tile);
                }
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("\n, ");
        }
        sb.setLength(sb.length() - 3);
        sb.append(']');
        return sb.toString();
    }
}
