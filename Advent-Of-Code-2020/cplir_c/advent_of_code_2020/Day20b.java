package cplir_c.advent_of_code_2020;

import static cplir_c.advent_of_code_2020.Day20.EXAMPLE;
import static cplir_c.advent_of_code_2020.Day20.INPUT;
import static cplir_c.advent_of_code_2020.Day20.LINES;
import static cplir_c.advent_of_code_2020.Day20.TILE_LINE;

import cplir_c.advent_of_code_2020.Edge.EdgeOrientation;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
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
        var tileTotal        = id2TileStringMap.size();

        var id2EdgesMap      = findEdges(id2TileStringMap);
        var edge2IdsMap     = createEdge2IdsMap(id2EdgesMap);
        var edgeMatchingMap = createEdgeMatchingMap(id2EdgesMap);

        var edgeLength       = Day20.sqrt(tileTotal);
        var solution   = matchEdges(id2EdgesMap, edge2IdsMap, edgeMatchingMap, edgeLength, tileTotal);
        var one              = solution.getInt(0);
        var two              = solution.getInt(edgeLength - 1);
        var three            = solution.getInt(tileTotal - edgeLength);
        var four             = solution.getInt(tileTotal - 1);
        System.out.println(one + " " + two + " " + three + " " + four + " *= " + (one * two * three * four));
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

    static Int2ObjectMap<Byte2ObjectMap<Edge>> findEdges(Int2ObjectMap<String> id2TileStringMap) {
        Int2ObjectMap<Byte2ObjectMap<Edge>> idEdgeMap = new Int2ObjectOpenHashMap<>(id2TileStringMap.size());
        for (var entry : id2TileStringMap.int2ObjectEntrySet()) {
            var edgeMap         = new Byte2ObjectOpenHashMap<Edge>(8);
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
            edgeMap.put(leftEdge.edgeOrientation, leftEdge);
            edgeMap.put(rightEdge.edgeOrientation, rightEdge);
            edgeMap.put(topEdge.edgeOrientation, topEdge);
            edgeMap.put(bottomEdge.edgeOrientation, bottomEdge);
            for (Edge mainEdge : edgeMap.values().toArray(Edge[]::new)) {
                addEdgeRotations(edgeMap, mainEdge);
                addEdgeRotations(edgeMap, mainEdge.translate());
            }
            edgeMap.trim();
            idEdgeMap.put(id, edgeMap);
        }
        return idEdgeMap;
    }

    private static void addEdgeRotations(Byte2ObjectMap<Edge> edgeMap, Edge root) {
        var rot       = root.rotate();
        var rotRot    = rot.rotate();
        var rotRotRot = rotRot.rotate();
        edgeMap.putIfAbsent(root.edgeOrientation, root);
        edgeMap.putIfAbsent(rot.edgeOrientation, rot);
        edgeMap.putIfAbsent(rotRot.edgeOrientation, rotRot);
        edgeMap.putIfAbsent(rotRotRot.edgeOrientation, rotRotRot);
    }

    static Reference2IntMap<Edge> createEdge2IdsMap(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap) {
        var edge2IdsMap = new Reference2IntOpenHashMap<Edge>(id2EdgesMap.size());
        for (var idAndTile : id2EdgesMap.int2ObjectEntrySet()) {
            var id   = idAndTile.getIntKey();
            var tile = idAndTile.getValue();
            for (Edge edge : tile.values()) {
                edge2IdsMap.put(edge, id);
            }
        }
        edge2IdsMap.trim();
        return edge2IdsMap;
    }

    static Int2ObjectMap<Edge> createEdgeMatchingMap(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap) {
        var edgeMatchingMap = new Int2ObjectOpenHashMap<Edge>(id2EdgesMap.size());
        for (Byte2ObjectMap<Edge> tile : id2EdgesMap.values()) {
            for (Edge edge : tile.values()) {
                edgeMatchingMap.put(edge.translate().hashCode(), edge);
            }
        }
        edgeMatchingMap.trim();
        return edgeMatchingMap;
    }

    static IntList matchEdges(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap, Reference2IntMap<Edge> edge2IdsMap,
                              Int2ObjectMap<Edge> edgeMatchingMap, int edgeLength, int totalTiles) {
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
                        id2EdgesMap, edge2IdsMap, edgeMatchingMap, mode, solution, modeStack, usedTiles, tileStackStack,
                        edgeLength, totalTiles
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
                            id2EdgesMap, edge2IdsMap, edgeMatchingMap, mode, solution, modeStack, usedTiles, tileStackStack,
                            edgeLength, totalTiles
                        );
                    }
                } else {
                    var tileID = tileStack.popInt();
                    // replace the old tried tile with the new one
                    var prevTileID = solution.put(mode, tileID);
                    usedTiles.remove(prevTileID);
                    usedTiles.add(tileID);
                    // recurse if possible
                    populateNextMove(id2EdgesMap, edge2IdsMap, edgeMatchingMap, solution, usedTiles, modeStack, tileStackStack);
                }
            }
        }

        var listOut = new IntArrayList(solution.keySet());
        for (var entry : Int2IntMaps.fastIterable(solution)) {
            listOut.set(entry.getIntKey(), entry.getIntValue());
        }
        return listOut;
    }

    private static void evaluateNewMode(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap,
                                        Reference2IntMap<Edge> edge2IdsMap, Int2ObjectMap<Edge> edgeMatchingMap,
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
            populateTileStackStack(
                id2EdgesMap, edge2IdsMap, edgeMatchingMap, mode, solution, usedTiles, tileStackStack, edgeLength, totalTiles
            );
        }
    }

    private static void
        populateTileStackStack(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap, Reference2IntMap<Edge> edge2IdsMap,
                               Int2ObjectMap<Edge> edgeMatchingMap,
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
                let tiles
                if (possibleTileIDs == null) {
                    possibleTileIDs = new IntArraySet(tileIDs);
                } else {
                    possibleTileIDs.retainAll(tileIDs);
                }
            }
        }
        tileStackStack.push(new IntArrayList(possibleTileIDs));
    }

    static void addMatchedEdges(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap, Int2IntOpenHashMap solution,
                                ReferenceSet<Edge> matchedEdges, int neighborPos, byte requiredEdgeOrientation) {
        var neighborTileID = solution.get(neighborPos);
        var neighborTile   = id2EdgesMap.get(neighborTileID);
        if (neighborTile != null) {
            var translatedNeighborEdge = findTranslatedEdge(neighborTile, requiredEdgeOrientation);
            matchedEdges.add(translatedNeighborEdge);
        }
    }

    static Edge findTranslatedEdge(Byte2ObjectMap<Edge> neighborTile, byte clockwise) {
        var counterclockwise = clockwise;
        counterclockwise ^= 1;
        final Edge translatedEdge;
        do {
            {
                var clockwiseEdge = neighborTile.get(clockwise);
                if (clockwiseEdge != null) {
                    translatedEdge = clockwiseEdge;
                    break;
                }
            }
            {
                var counterclockwiseEdge = neighborTile.get(counterclockwise);
                if (counterclockwiseEdge != null) {
                    translatedEdge = counterclockwiseEdge;
                    break;
                }
            }
            throw new AssertionError("found no translated edge");
        } while (false);
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

    private static void populateNextMove(Int2ObjectMap<Byte2ObjectMap<Edge>> id2EdgesMap,
                                         Reference2IntMap<Edge> edge2IdsMap,
                                         Int2ObjectMap<Edge> edgeMatchingMap,
                                         Int2IntOpenHashMap solution, IntSet usedTiles, IntArrayList modeStack,
                                         ObjectArrayList<AbstractIntList> tileStackStack) {
        // test feasibility of shifts
        // test feasibility of tile-adjacent spaces
        Int2ByteMap
    }


}
