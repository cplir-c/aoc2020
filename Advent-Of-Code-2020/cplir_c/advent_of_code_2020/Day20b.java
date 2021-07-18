package cplir_c.advent_of_code_2020;

import static cplir_c.advent_of_code_2020.Day20.EXAMPLE;
import static cplir_c.advent_of_code_2020.Day20.INPUT;
import static cplir_c.advent_of_code_2020.Day20.LINES;
import static cplir_c.advent_of_code_2020.Day20.TILE_LINE;

import cplir_c.advent_of_code_2020.Edge.EdgeOrientation;
import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;


public final class Day20b {

    public static void main(String[] args) {
        if (System.identityHashCode(INPUT) != 0) {
            findCornerProduct(INPUT);
        }
        findCornerProduct(EXAMPLE);
    }

    static void findCornerProduct(String input) {
        System.out.println("finding corners with input length " + input.length());
        var id2TileStringMap = parseInput(input);
        var id2EdgesMap      = findEdges(id2TileStringMap);
        var tileTotal        = id2EdgesMap.size();
        var edge2IdsMap      = createInverseEdgeMap(id2EdgesMap);
        var edgeLength       = Day20.sqrt(tileTotal);
        var solution         = matchEdges(id2EdgesMap, edge2IdsMap, edgeLength, tileTotal);
        var one              = solution.getInt(0);
        var two              = solution.getInt(edgeLength - 1);
        var three            = solution.getInt(tileTotal - edgeLength);
        var four             = solution.getInt(tileTotal - 1);
        System.out.println(one + " " + two + " " + three + " " + four + " *= " + (one * two * three * four));
    }

    static IntList matchEdges(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap, Object2ObjectMap<Edge, IntSet> edge2IdsMap,
                              int edgeLength, int totalTiles) {
        var solution = new Int2IntOpenHashMap(totalTiles);
        solution.defaultReturnValue(-1);
        IntSet usedTiles = new IntOpenHashSet(totalTiles);
        // mode -4: try shifting down
        // mode -3: try shifting left
        // mode -2: try shifting up
        // mode -1: try shifting right
        // mode 0: try inferring slot 0
        // mode n: try inferring slot n
        // modes greater than or equal to 0 have an accompanying tileStack of remaining tile choices.
        var modeStack      = new IntArrayList();
        var modeStackStack = new ObjectArrayList<AbstractIntList>();
        var tileStackStack = new ObjectArrayList<AbstractIntList>();
        initializeModes(totalTiles, modeStack, modeStackStack);
        tileStackStack.add(new IntArrayList(id2EdgesMap.keySet()));
        while (solution.size() < totalTiles) {
            var mode = modeStack.popInt();
            if (mode < 0) {
                // move the tiles back to before they were shifted
                shiftTiles(solution, switch (mode) {
                    case -1 -> -1;
                    case -2 -> edgeLength;
                    case -3 -> 1;
                    case -4 -> -edgeLength;
                    default -> throw new IndexOutOfBoundsException(mode);
                });
                mode = tryPopNextMode(modeStack, modeStackStack);
                if (mode > -5) {
                    evaluateNewMode(
                        id2EdgesMap, edge2IdsMap, mode, solution, modeStack, usedTiles, tileStackStack, edgeLength, totalTiles
                    );
                }
            } else {
                var tileStack = tileStackStack.top();
                if (tileStack.isEmpty()) {
                    tileStackStack.pop();
                    // undo the effects of running this mode
                    var tileID = solution.remove(mode);
                    usedTiles.remove(tileID);
                    usedTiles.remove(~tileID);
                    // swap to the next mode if available to try
                    mode = tryPopNextMode(modeStack, modeStackStack);
                    if (mode > -5) {
                        evaluateNewMode(
                            id2EdgesMap, edge2IdsMap, mode, solution, modeStack, usedTiles, tileStackStack, edgeLength,
                            totalTiles
                        );
                    }
                } else {
                    var tileID = tileStack.popInt();
                    // replace the old tried tile with the new one
                    var prevTileID = solution.put(mode, tileID);
                    usedTiles.remove(prevTileID);
                    usedTiles.add(tileID);
                    // recurse if possible
                    populateNextMove(id2EdgesMap, edge2IdsMap, solution, usedTiles, modeStack, tileStackStack);
                }
            }
        }

        var listOut = new IntArrayList(solution.keySet());
        for (var entry : Int2IntMaps.fastIterable(solution)) {
            listOut.set(entry.getIntKey(), entry.getIntValue());
        }
        return listOut;
    }

    private static void evaluateNewMode(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap, Object2ObjectMap<Edge, IntSet> edge2IdsMap,
                                        int mode, Int2IntOpenHashMap solution, IntArrayList modeStack, IntSet usedTiles,
                                        ObjectArrayList<AbstractIntList> tileStackStack, int edgeLength, int totalTiles) {
        modeStack.add(mode);
        if (mode < 0) {
            // new shifting mode selected
            shiftTiles(solution, switch (mode) {
                case -1 -> 1;
                case -2 -> -edgeLength;
                case -3 -> -1;
                case -4 -> edgeLength;
                default -> throw new IndexOutOfBoundsException(mode);
            });
        } else {
            // new tile placing mode selected
            populateTileStackStack(id2EdgesMap, edge2IdsMap, mode, solution, usedTiles, tileStackStack, edgeLength, totalTiles);
        }
    }

    private static void
        populateTileStackStack(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap, Object2ObjectMap<Edge, IntSet> edge2IdsMap,
                               int position, Int2IntOpenHashMap solution, IntSet usedTiles,
                               ObjectArrayList<AbstractIntList> tileStackStack, int edgeLength, int totalTiles) {
        // new tile placing mode selected, so queue the most likely tiles in order
        ReferenceSet<Edge> matchedEdges = new ReferenceArraySet<>(4);
        if (position > edgeLength) {
            // down from the neighbor
            final var neighborPos             = position - edgeLength;
            final var requiredEdgeOrientation = EdgeOrientation.CLOCKWISE_DOWN;
            addMatchedEdges(id2EdgesMap, solution, matchedEdges, neighborPos, requiredEdgeOrientation);
        }
        if (position + edgeLength < totalTiles) {
            // up from the neighbor
            final var neighborPos             = position + edgeLength;
            final var requiredEdgeOrientation = EdgeOrientation.CLOCKWISE_UP;
            addMatchedEdges(id2EdgesMap, solution, matchedEdges, neighborPos, requiredEdgeOrientation);
        }
        var col = position % edgeLength;
        if (col > 0) {
            // right of the neighbor
            final var neighborPos             = position - 1;
            final var requiredEdgeOrientation = EdgeOrientation.CLOCKWISE_RIGHT;
            addMatchedEdges(id2EdgesMap, solution, matchedEdges, neighborPos, requiredEdgeOrientation);
        }
        if (col + 1 < edgeLength) {
            // left of the neighbor
            final var neighborPos             = position + 1;
            final var requiredEdgeOrientation = EdgeOrientation.CLOCKWISE_LEFT;
            addMatchedEdges(id2EdgesMap, solution, matchedEdges, neighborPos, requiredEdgeOrientation);
        }
        IntSet possibleTileIDs;
        if (matchedEdges.isEmpty()) {
            possibleTileIDs = new IntOpenHashSet(id2EdgesMap.keySet());
            possibleTileIDs.removeAll(usedTiles);
        } else {
            possibleTileIDs = null;
            for (Edge matchEdge : matchedEdges) {
                var tileIDs = edge2IdsMap.get(matchEdge);
                if (possibleTileIDs == null) {
                    possibleTileIDs = new IntArraySet(tileIDs);
                } else {
                    possibleTileIDs.retainAll(tileIDs);
                }
            }
        }
        tileStackStack.push(new IntArrayList(possibleTileIDs));
    }

    static void addMatchedEdges(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap, Int2IntOpenHashMap solution,
                                ReferenceSet<Edge> matchedEdges, int neighborPos, byte requiredEdgeOrientation) {
        var neighborTileID = solution.get(neighborPos);
        var neighborTile   = id2EdgesMap.get(neighborTileID);
        if (neighborTile != null) {
            var translatedNeighborEdge = findTranslatedEdge(neighborTile, requiredEdgeOrientation);
            matchedEdges.add(translatedNeighborEdge);
        }
    }

    static Edge findTranslatedEdge(ObjectSet<Edge> neighborTile, byte clockwise) {
        var counterclockwise = clockwise;
        counterclockwise ^= 1;
        Edge translatedEdge = null;
        for (var edge : neighborTile) {
            var edgeOrientation = edge.edgeOrientation;
            if (edgeOrientation == clockwise || edgeOrientation == counterclockwise) {
                translatedEdge = edge.translate();
                break;
            }
        }
        if (translatedEdge == null) {
            throw new AssertionError();
        }
        return translatedEdge;
    }

    private static void shiftTiles(Int2IntOpenHashMap solution, int tileShift) {
        IntSet toMove = new IntOpenHashSet(solution.keySet());
        var    it     = toMove.iterator();
        while (it.hasNext()) {
            var source = it.nextInt();
            it.remove();
            var tileID = solution.remove(source);
            do {
                source += tileShift;
                tileID  = solution.put(source, tileID);
            } while (tileID != -1);
        }
    }

    static int tryPopNextMode(IntArrayList modeStack, ObjectArrayList<AbstractIntList> modeStackStack) {
        var modeSwapStack = modeStackStack.top();
        if (modeSwapStack.isEmpty()) {
            modeStackStack.pop();
            return -1;
        } else {
            var mode = modeSwapStack.popInt();
            modeStack.add(mode);
            return mode;
        }
    }

    static void initializeModes(int totalTiles, IntArrayList modeStack, ObjectArrayList<AbstractIntList> modeStackStack) {
        modeStack.add(0);
        var modeSwapStack = new IntArrayList(totalTiles - 1);
        for (var i = 1; i < totalTiles; ++i) {
            modeSwapStack.add(i);
        }
        modeStackStack.add(modeSwapStack);
    }

    private static void populateNextMove(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap, Object2ObjectMap<Edge, IntSet> edge2IdsMap,
                                         Int2IntOpenHashMap solution, IntSet usedTiles, IntArrayList modeStack,
                                         ObjectArrayList<AbstractIntList> tileStackStack) {
        // test feasibility of shifts
        // test feasibility of tile-adjacent spaces
        Int2ByteMap
    }

    static Object2ObjectMap<Edge, IntSet> createInverseEdgeMap(Int2ObjectMap<ObjectSet<Edge>> id2EdgesMap) {
        var inverseEdgeMap = new Object2ObjectOpenHashMap<Edge, IntSet>(id2EdgesMap.size());
        var idSet          = new IntOpenHashSet(id2EdgesMap.keySet());
        for (int id : idSet) {
            var edgeSet         = id2EdgesMap.get(id);
            var reversedEdgeSet = new ObjectOpenHashSet<Edge>(edgeSet.size());
            for (var edge : edgeSet) {
                reversedEdgeSet.add(edge.reverse());
                inverseEdgeMap.computeIfAbsent(edge, $ -> new IntArraySet(1)).add(id);
            }
            id2EdgesMap.put(~id, reversedEdgeSet);
            for (var edge : reversedEdgeSet) {
                inverseEdgeMap.computeIfAbsent(edge, $ -> new IntArraySet(1)).add(id + (1 << Short.SIZE));
            }
        }
        inverseEdgeMap.trim();
        return inverseEdgeMap;
    }

    static Int2ObjectMap<ObjectSet<Edge>> findEdges(Int2ObjectMap<String> id2TileStringMap) {
        Int2ObjectMap<ObjectSet<Edge>> idEdgeMap = new Int2ObjectOpenHashMap<>(id2TileStringMap.size());
        for (var entry : id2TileStringMap.int2ObjectEntrySet()) {
            var edgeSet         = new ObjectOpenHashSet<Edge>(4);
            var id              = entry.getIntKey();
            var tileString      = entry.getValue();
            var tileLines       = TILE_LINE.split(tileString);
            var topEdge         = new Edge(tileLines[0], Edge.EdgeOrientation.COUNTERCLOCKWISE_UP);
            var bottomEdge      = new Edge(tileLines[9], Edge.EdgeOrientation.CLOCKWISE_DOWN);
            var leftEdgeString  = new StringBuilder(10);
            var rightEdgeString = new StringBuilder(10);
            for (var i = 9; i >= 0; --i) {
                var line  = tileLines[i];
                var left  = line.charAt(0);
                var right = line.charAt(9);
                leftEdgeString.append(left);
                rightEdgeString.append(right);
            }
            var leftEdge  = new Edge(leftEdgeString.toString(), Edge.EdgeOrientation.CLOCKWISE_LEFT);
            var rightEdge = new Edge(rightEdgeString.toString(), Edge.EdgeOrientation.COUNTERCLOCKWISE_RIGHT);
            edgeSet.add(leftEdge);
            edgeSet.add(rightEdge);
            edgeSet.add(topEdge);
            edgeSet.add(bottomEdge);
            edgeSet.trim();
            idEdgeMap.put(id, edgeSet);
        }
        return idEdgeMap;
    }

    static Int2ObjectMap<String> parseInput(String input) {
        var idTileStrings = LINES.split(input);
        var map           = new Int2ObjectOpenHashMap<String>(idTileStrings.length);
        for (String idTileString : idTileStrings) {
            var lines      = TILE_LINE.split(idTileString, 2);
            var idString   = lines[0];
            var id         = Integer.parseInt(idString.substring(5));
            var tileString = lines[1];
            map.put(id, tileString);
        }
        return map;
    }

}
