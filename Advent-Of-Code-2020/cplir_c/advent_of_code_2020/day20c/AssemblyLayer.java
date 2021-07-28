package cplir_c.advent_of_code_2020.day20c;

import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;


public class AssemblyLayer {

    private static final Function<? super Object, ObjectSet<? extends RegisteredTileSquare>>   REGISTERED_SQUARE_SET_SUPPLIER
                                                                                                                              = ignore -> new ObjectOpenHashSet<>(
            Integer.BYTES
        );
    int                                                                                        tilesWide;
    int                                                                                        tilesTall;
    final Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies;
    final Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<RegisteredTileSquare>>           rotatedAssemblies;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByRight;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByUp;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByLeft;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>                            assembliesByDown;

    private AssemblyLayer(int tilesWide, int tilesTall,
                          Object2ObjectMap<TileSet<RegisteredTile>,
                                           ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies) {
        this.tilesWide           = tilesWide;
        this.tilesTall           = tilesTall;
        this.unrotatedAssemblies = unrotatedAssemblies;
        this.rotatedAssemblies   = rotateAssemblies(unrotatedAssemblies);
        this.assembliesByRight   = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByUp      = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByLeft    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByDown    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.indexTileAssembliesByEdge();
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

    private static Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<RegisteredTileSquare>>
        rotateAssemblies(Object2ObjectMap<TileSet<RegisteredTile>,
                                          ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies2) {
        var rotatedAssemblies = new Object2ObjectOpenHashMap<TileSet<RegisteredTile>, ObjectSet<RegisteredTileSquare>>(
            unrotatedAssemblies2.size()
        );
        for (var assemblySetAndTiles : unrotatedAssemblies2.object2ObjectEntrySet()) {
            var tileSet            = assemblySetAndTiles.getKey();
            var rotatedAssemblySet = new ObjectOpenHashSet<RegisteredTileSquare>(assemblySetAndTiles.getValue().size() * 8);
            for (var assembly : assemblySetAndTiles.getValue()) {
                for (byte orientation = 0; orientation <= 7; ++orientation) {
                    var rotatedAssembly = new OrientedRegisteredSquare<>(assembly, orientation);
                    rotatedAssemblySet.add(rotatedAssembly);
                }
            }
            rotatedAssemblies.put(tileSet, rotatedAssemblySet);
        }
        return rotatedAssemblies;
    }
    @SuppressWarnings("unchecked")
    private final void indexTileAssembliesByEdge() {
        var setSupplier = (Function<? super String, ? extends ObjectSet<RegisteredTileSquare>>) REGISTERED_SQUARE_SET_SUPPLIER;
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
            return packageTileSet(problem.allTiles);
        }

        var topTilesTall    = tilesTall >> 1;
        var bottomTilesTall = tilesTall - topTilesTall;

        var topDimensions    = packDimensionsForLookup(tilesWide, topTilesTall);
        var bottomDimensions = packDimensionsForLookup(tilesWide, bottomTilesTall);

        var topAssemblyLayer    = problem.tileAssemblies.get(topDimensions);
        var bottomAssemblyLayer = problem.tileAssemblies.get(bottomDimensions);

        if (topAssemblyLayer == null || bottomAssemblyLayer == null) {
            queueUnqueuedAssemblyLayerDimensions(
                problem, topDimensions, bottomDimensions, topAssemblyLayer, bottomAssemblyLayer
            );
            return null;
        }

        Object2ObjectMap<TileSet<RegisteredTile>, ObjectSet<? extends RegisteredTileSquare>> assemblies
            = new Object2ObjectOpenHashMap<>();

        // have both assembly layers
        for (var topAssemblySet : topAssemblyLayer.rotatedAssemblies.values()) {
            for (var topAssembly : topAssemblySet) {
                if (topAssembly.tilesTall() != topTilesTall || topAssembly.tilesWide() != tilesWide) {
                    continue;
                }
                var topDown           = topAssembly.down();
                var bottomAssemblySet = bottomAssemblyLayer.assembliesByUp.get(topDown);
                if (bottomAssemblySet == null) {
                    continue;
                }
                var topUsedTiles = topAssembly.usedTiles(problem.allTiles);
                addPossibleBottomAssemblies(
                    problem, tilesWide, bottomTilesTall, assemblies, topAssembly, bottomAssemblySet, topUsedTiles
                );
            }
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
            set.add(tile);
            packagedTileSet.put(set, set);
        }
        return packagedTileSet;
    }
    private static int packDimensionsForLookup(int tilesWide, int tilesTall) {
        if (tilesWide > tilesTall) {
            return (tilesTall << Short.SIZE) | tilesWide;
        } else {
            return (tilesWide << Short.SIZE) | tilesTall;
        }
    }

    @SuppressWarnings("unchecked")
    private static void addPossibleBottomAssemblies(TileProblem problem, int tilesWide, int bottomTilesTall,
                                                    Object2ObjectMap<TileSet<RegisteredTile>,
                                                                     ObjectSet<? extends RegisteredTileSquare>> assemblies,
                                                    RegisteredTileSquare topAssembly,
                                                    ObjectSet<RegisteredTileSquare> bottomAssemblySet,
                                                    TileSet<RegisteredTile> topUsedTiles) {
        var setSupplier
            = (Function<? super TileSet<RegisteredTile>,
                        ? extends ObjectSet<RegisteredStackedTileSquares<RegisteredTile>>>) REGISTERED_SQUARE_SET_SUPPLIER;
        for (var bottomAssembly : bottomAssemblySet) {
            if (bottomAssembly.tilesTall() != bottomTilesTall || bottomAssembly.tilesWide() != tilesWide
                || !bottomAssembly.unusedTiles(problem.allTiles).containsAll(topUsedTiles)) {
                continue;
            }
            var assembly  = new RegisteredStackedTileSquares<>(topAssembly, bottomAssembly);
            var tilesUsed = assembly.usedTiles(problem.allTiles);
            assemblies.computeIfAbsent(tilesUsed, setSupplier);
        }
    }

    private static void queueUnqueuedAssemblyLayerDimensions(TileProblem problem, int topDimensions, int bottomDimensions,
                                                             AssemblyLayer topAssemblyLayer,
                                                             AssemblyLayer bottomAssemblyLayer) {
        queueAssemblyLayerDimension(problem, bottomDimensions, bottomAssemblyLayer);
        queueAssemblyLayerDimension(problem, topDimensions, topAssemblyLayer);
    }
    private static void queueAssemblyLayerDimension(TileProblem problem, int dimensions,
                                                    AssemblyLayer assemblyLayer) {
        if (assemblyLayer == null && problem.queuedAssemblyLayerSizes.add(dimensions)) {
            if (dimensions <= 1) {
                throw new AssertionError();
            }
            problem.assemblyLayerAssemblyOrderStack.add(dimensions);
        } else {
            if (dimensions != problem.assemblyLayerAssemblyOrderStack.peekInt(0)) {
                problem.assemblyLayerAssemblyOrderStack.rem(dimensions);
                problem.assemblyLayerAssemblyOrderStack.add(dimensions);
            }
        }
    }

}
