package cplir_c.advent_of_code_2020.day20c;

import java.util.function.Function;
import java.util.function.IntFunction;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;


public class AssemblyLayer {

    private static final Function<? super Object, ObjectSet<RegisteredTileSquare>>             REGISTERED_SQUARE_SET_SUPPLIER
                                                                                                                              = ignore -> new ObjectOpenHashSet<>(
            Integer.BYTES
        );
    private static final IntFunction<ObjectSet<RegisteredTileSquare>>                          INT_SET_SUPPLIER
                                                                                                                              = ignore -> new ObjectOpenHashSet<>(
            Integer.BYTES
        );
    int                                                                                        tilesWide;
    int                                                                                        tilesTall;
    final Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies;
    final Int2ObjectMap<ObjectSet<RegisteredTileSquare>>                                       rotatedAssemblies;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByRight;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByUp;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByLeft;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByDown;

    private AssemblyLayer(int tilesWide, int tilesTall,
                          Object2ObjectMap<TileSet<RegisteredTile>,
                                           ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies) {
        for (var set : unrotatedAssemblies.values()) {
            for (var assembly : set) {
                if (assembly.body() == null) {
                    throw new AssertionError();
                }
            }
        }
        this.tilesWide           = tilesWide;
        this.tilesTall           = tilesTall;
        this.unrotatedAssemblies = unrotatedAssemblies;
        this.rotatedAssemblies   = rotateAssemblies(unrotatedAssemblies);
        this.assembliesByRight   = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByUp      = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByLeft    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByDown    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.indexTileAssembliesByEdge();
        System.out.println("assembled " + this.tilesWide + " by " + this.tilesTall + " layer");
    }
    public static AssemblyLayer assembleAssemblyLayer(TileProblem problem, int dimensions) {
        var tilesWide           = dimensions >>> Short.SIZE;
        var tilesTall           = dimensions & 0xffff;
        var unrotatedAssemblies = assembleUnrotatedAssemblies(problem, tilesWide, tilesTall);
        if (unrotatedAssemblies == null) {
            return null;
        }
        return new AssemblyLayer(tilesWide, tilesTall, unrotatedAssemblies);
    }

    private static Int2ObjectMap<ObjectSet<RegisteredTileSquare>>
        rotateAssemblies(Object2ObjectMap<TileSet<RegisteredTile>,
                                          ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies) {
        Int2ObjectMap<ObjectSet<RegisteredTileSquare>> rotatedAssemblies
            = new Int2ObjectOpenHashMap<>(unrotatedAssemblies.size());
        for (var assemblySet : unrotatedAssemblies.values()) {
            var assemblySetSize = assemblySet.size();
            if (assemblySetSize == 0) {
                throw new AssertionError();
            }
            for (var assembly : assemblySet) {
                for (byte orientation = 0; orientation <= 0x7; ++orientation) {
                    var rotatedAssembly = new OrientedRegisteredSquare<>(assembly, orientation);
                    rotatedAssemblies.computeIfAbsent(rotatedAssembly.tilesTall(), INT_SET_SUPPLIER).add(rotatedAssembly);
                }
            }
        }
        return rotatedAssemblies;
    }
    private final void indexTileAssembliesByEdge() {
        var setSupplier = REGISTERED_SQUARE_SET_SUPPLIER;
        for (var assemblySet : this.rotatedAssemblies.values()) {
            for (var tileSquare : assemblySet) {
                var right = tileSquare.right();
                var up    = tileSquare.up();
                var left  = tileSquare.left();
                var down  = tileSquare.down();
                this.assembliesByRight.computeIfAbsent(right, setSupplier).add(tileSquare);
                this.assembliesByUp.computeIfAbsent(up, setSupplier).add(tileSquare);
                this.assembliesByLeft.computeIfAbsent(left, setSupplier).add(tileSquare);
                this.assembliesByDown.computeIfAbsent(down, setSupplier).add(tileSquare);
            }
        }
    }

    private static Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>>
        assembleUnrotatedAssemblies(TileProblem problem, int tilesWide, int tilesTall) {
        // height >= width
        if (tilesTall < tilesWide) {
            throw new IllegalArgumentException();
        }
        if (tilesTall == 1 && tilesWide == 1) {
            return packageTileSet(problem.allTiles());
        }

        var topTilesTall    = tilesTall >> 1;
        var bottomTilesTall = tilesTall - topTilesTall;

        var topDimensions    = TileProblem.packDimensionsForLookup(tilesWide, topTilesTall);
        var bottomDimensions = TileProblem.packDimensionsForLookup(tilesWide, bottomTilesTall);

        var topAssemblyLayer    = problem.lookupAssemblyLayer(topDimensions);
        var bottomAssemblyLayer = problem.lookupAssemblyLayer(bottomDimensions);

        if (topAssemblyLayer == null || bottomAssemblyLayer == null) {

            queueUnqueuedAssemblyLayerDimensions(
                problem, topDimensions, bottomDimensions, topAssemblyLayer, bottomAssemblyLayer
            );
            return null;
        }

        return stackRotatedAssemblies(problem, tilesWide, topTilesTall, bottomTilesTall, topAssemblyLayer, bottomAssemblyLayer);
    }
    @SuppressWarnings("unchecked")
    private static Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>>
        stackRotatedAssemblies(TileProblem problem, int tilesWide, int topTilesTall, int bottomTilesTall,
                               AssemblyLayer topAssemblyLayer, AssemblyLayer bottomAssemblyLayer)
                throws AssertionError {
        Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>> assemblies
            = new Object2ObjectOpenHashMap<>();

        var setSupplier
            = (Function<? super TileSet<RegisteredTile>,
                        ? extends ObjectSet<RegisteredStackedTileSquares<RegisteredTileSquare>>>) REGISTERED_SQUARE_SET_SUPPLIER;
        // have both assembly layers
        var topAssemblySet = topAssemblyLayer.rotatedAssemblies.get(topTilesTall);
        for (var topAssembly : topAssemblySet) {
            if (topAssembly.tilesTall() != topTilesTall || topAssembly.tilesWide() != tilesWide) {
                throw new AssertionError();
            }
            var topDown           = topAssembly.down();
            var bottomAssemblySet = bottomAssemblyLayer.assembliesByUp.get(topDown);
            if (bottomAssemblySet == null) {
                continue;
            }

            var topUsedTiles = topAssembly.usedTiles(problem.allTiles());
            for (var bottomAssembly : bottomAssemblySet) {
                if (bottomAssembly.tilesTall() != bottomTilesTall) {
                    // skip to the next assembly
                } else if (bottomAssembly.tilesWide() != tilesWide) {
                    throw new AssertionError("if tiles tall matches, tiles wide should match too, from the dimension int.");
                } else if (!bottomAssembly.unusedTiles(problem.allTiles()).containsAll(topUsedTiles)) {
                    System.out.println("fail 3");
                } else {
                    @SuppressWarnings("rawtypes")
                    var assembly  = new RegisteredStackedTileSquares(topAssembly, bottomAssembly);
                    var tilesUsed = assembly.usedTiles(problem.allTiles());
                    assemblies.computeIfAbsent(tilesUsed, setSupplier).add(assembly);
                }
            }
        }
        if (assemblies.isEmpty()) {
            System.err.println(topAssemblyLayer);
            throw new IllegalStateException();
        }
        return assemblies;
    }
    private static Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>>
        packageTileSet(TileSet<RegisteredTile> allTiles) {
        Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>> packagedTileSet
            = new Object2ObjectOpenHashMap<>(allTiles.size());
        for (RegisteredTile tile : allTiles) {
            var set = new TileSet<>(allTiles);
            set.clear();
            if (!set.add(tile)) {
                throw new AssertionError();
            }
            packagedTileSet.put(set, set);
        }
        return packagedTileSet;
    }
    private static void queueUnqueuedAssemblyLayerDimensions(TileProblem problem, int topDimensions, int bottomDimensions,
                                                             AssemblyLayer topAssemblyLayer,
                                                             AssemblyLayer bottomAssemblyLayer) {
        problem.queueAssemblyLayerDimension(bottomDimensions, bottomAssemblyLayer);
        problem.queueAssemblyLayerDimension(topDimensions, topAssemblyLayer);
    }
    @Override
    public String toString() {
        return this.unrotatedAssemblies.toString();
    }
}
