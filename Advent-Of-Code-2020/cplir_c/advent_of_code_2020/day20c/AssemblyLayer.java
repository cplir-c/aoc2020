package cplir_c.advent_of_code_2020.day20c;

import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class AssemblyLayer<S extends RegisteredTileSquare> {
    int                                                              tilesWide;
    int                                                              tilesTall;
    final Object2ObjectMap<TileSet, ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies;
    final Object2ObjectMap<TileSet, ObjectSet<RegisteredTileSquare>> rotatedAssemblies;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByRight;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByUp;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByLeft;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByDown;

    protected AssemblyLayer(TileProblem problem, int dimensions) {
        this.tilesWide           = dimensions >>> Short.SIZE;
        this.tilesTall           = dimensions & 0xffff;
        this.unrotatedAssemblies = assembleUnrotatedAssemblies(problem, this.tilesWide, this.tilesTall);
        this.rotatedAssemblies   = rotateAssemblies(this.unrotatedAssemblies);
        this.assembliesByRight   = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByUp      = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByLeft    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByDown    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.indexTileAssembliesByEdge();
    }

    private static Object2ObjectMap<TileSet, ObjectSet<RegisteredTileSquare>>
        rotateAssemblies(Object2ObjectMap<TileSet, ObjectSet<? extends RegisteredTileSquare>> unrotatedAssemblies2) {
        var rotatedAssemblies
            = new Object2ObjectOpenHashMap<TileSet, ObjectSet<RegisteredTileSquare>>(unrotatedAssemblies2.size());
        for (var assemblySetAndTiles : unrotatedAssemblies2.object2ObjectEntrySet()) {
            var tileSet = assemblySetAndTiles.getKey();
            var rotatedAssemblySet = new ObjectOpenHashSet<RegisteredTileSquare>(assemblySetAndTiles.getValue().size() * 8);
            for (var assembly : assemblySetAndTiles.getValue()) {
                for (byte orientation = 0; orientation <= 7; ++orientation) {
                    var rotatedAssembly = new OrientedRegisteredSquare<>(assembly, orientation);
                    rotatedAssemblySet.add(rotatedAssembly);
                }
            }
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

    private static Object2ObjectMap<TileSet, ObjectSet<? extends RegisteredTileSquare>>
        assembleUnrotatedAssemblies(TileProblem problem, int tilesWide, int tilesTall) {
        // height >= width
        if (tilesTall < tilesWide) {
            throw new IllegalArgumentException();
        } else if (tilesTall == 1) {
            return assembleStrip(problem, tilesWide);
        } else {
            return assembleStacked(problem, tilesWide, tilesTall);
        }
    }

    private static Object2ObjectMap<TileSet, ObjectSet<? extends RegisteredTileSquare>> assembleStacked(TileProblem problem,
                                                                                              int tilesWide, int tilesTall) {
        var topTilesTall    = tilesTall >> 1;
        var bottomTilesTall = tilesTall - topTilesTall;

        var topDimensions   = (tilesWide << 16) | topTilesTall;
        var bottomDimensions = (tilesWide << 16) | bottomTilesTall;

        var topAssemblyLayer    = problem.tileAssemblies.get(topDimensions);
        var bottomAssemblyLayer = problem.tileAssemblies.get(bottomDimensions);

        if (topAssemblyLayer == null || bottomAssemblyLayer == null) {
            queueUnqueuedAssemblyLayerDimensions(
                problem, topDimensions, bottomDimensions, topAssemblyLayer, bottomAssemblyLayer
            );
            return null;
        }

        var assemblies = new Object2ObjectOpenHashMap<TileSet, ObjectSet<? extends RegisteredTileSquare>>();

        // have both assembly layers
        for (var topAssemblySet : topAssemblyLayer.rotatedAssemblies.values()) {
            for (var topAssembly : topAssemblySet) {
                if (topAssembly.tilesTall() != topTilesTall || topAssembly.tilesWide() != tilesWide) {
                    continue;
                }
                var topDown = topAssembly.down();
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

    private static final Function<? super Object, ObjectSet<? extends RegisteredTileSquare>> REGISTERED_SQUARE_SET_SUPPLIER
        = $ -> new ObjectOpenHashSet<>(Integer.BYTES);
    private static void
        addPossibleBottomAssemblies(TileProblem problem, int tilesWide, int bottomTilesTall,
                                    Object2ObjectOpenHashMap<TileSet, ObjectSet<? extends RegisteredTileSquare>> assemblies,
                                    RegisteredTileSquare topAssembly, ObjectSet<RegisteredTileSquare> bottomAssemblySet,
                                    TileSet topUsedTiles) {
        for (var bottomAssembly: bottomAssemblySet) {
            if (bottomAssembly.tilesTall() != bottomTilesTall
                || bottomAssembly.tilesWide() != tilesWide
                || !bottomAssembly.unusedTiles(problem.allTiles).containsAll(topUsedTiles)) {
                continue;
            }
            var assembly  = new RegisteredStackedTileSquares<>(topAssembly, bottomAssembly);
            var tilesUsed = assembly.usedTiles(problem.allTiles);
            assemblies.computeIfAbsent(tilesUsed, REGISTERED_SQUARE_SET_SUPPLIER);
        }
    }

    private static void
        queueUnqueuedAssemblyLayerDimensions(TileProblem problem, int topDimensions, int bottomDimensions,
                                             AssemblyLayer<RegisteredTileSquare> topAssemblyLayer,
                                             AssemblyLayer<RegisteredTileSquare> bottomAssemblyLayer) {
        if (topAssemblyLayer == null && problem.queuedAssemblyLayerSizes.add(topDimensions)) {
            problem.assemblyLayerAssemblyOrderStack.add(topDimensions);
        }
        if (bottomAssemblyLayer == null && problem.queuedAssemblyLayerSizes.add(bottomDimensions)) {
            problem.assemblyLayerAssemblyOrderStack.add(bottomDimensions);
        }
    }

    private static Object2ObjectMap<TileSet, ObjectSet<? super RegisteredTileSquare>> assembleStrip(TileProblem problem,
                                                                                            int tilesWide) {
        if (tilesWide == 1) {
            return packageTileSet(problem.allTiles);
        }
        var verticalDimensions = (1 << 16) | tilesWide;
        // var dimensions = (tilesWide << 16) | 1;

        var verticalAssemblyLayer = problem.tileAssemblies.get(verticalDimensions);
        if (verticalAssemblyLayer == null) {
            if (problem.queuedAssemblyLayerSizes.contains(verticalDimensions)) {
                problem.assemblyLayerAssemblyOrderStack.add(verticalDimensions);
            }
            return null;
        }
        var assembledTileStrips = new Object2ObjectOpenHashMap<TileSet, ObjectSet<? super RegisteredTileSquare>>(
            verticalAssemblyLayer.rotatedAssemblies.size()
        );
        for (var assemblySetEntry : verticalAssemblyLayer.rotatedAssemblies.entrySet()) {
            var assembledStrips = new ObjectOpenHashSet<? super RegisteredTileSquare>(4);
            for (var assembly : assemblySetEntry.getValue()) {
                if (assembly.tilesTall() != 1 || assembly.tilesWide() != tilesWide) {
                    continue;
                }
                assembledStrips.add(assembly);
            }
            assembledTileStrips.put(assemblySetEntry.getKey(), assembledStrips);
        }
        return assembledTileStrips;
    }

    private static Object2ObjectMap<TileSet, ObjectSet<? super RegisteredTileSquare>> packageTileSet(TileSet allTiles) {
        var packagedTileSet = new Object2ObjectOpenHashMap<TileSet, ObjectSet<? super RegisteredTileSquare>>(allTiles.size());
        for (RegisteredTile tile : allTiles) {
            var set = new TileSet(allTiles);
            set.clear();
            set.add(tile);
            packagedTileSet.put(set, (ObjectSet<? super RegisteredTileSquare>) set);
        }
        return packagedTileSet;
    }

}
