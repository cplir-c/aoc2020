package cplir_c.advent_of_code_2020.day20c;

import java.util.Set;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;


public final class TileProblem {
    private static final Pattern       DOUBLE_LINES = Pattern.compile("\n{2,}");
    final TileSet<RegisteredTile>      allTiles;
    final Int2ObjectMap<AssemblyLayer> tileAssemblies;
    final IntArrayList                 assemblyLayerAssemblyOrderStack;
    final IntSet                       queuedAssemblyLayerSizes;
    private final int                  finalDimensions;

    public TileProblem(String tiles) {
        var allTilesArray = TileProblem.parseTiles(tiles);
        this.allTiles = new TileSet<>(allTilesArray);
        var maxWidth = (int) Math.sqrt(this.allTiles.size());
        var maxSize  = (maxWidth << Short.SIZE) | maxWidth;
        this.finalDimensions                 = maxSize;
        this.assemblyLayerAssemblyOrderStack = new IntArrayList(maxWidth);
        this.queuedAssemblyLayerSizes        = new IntOpenHashSet(maxWidth);
        this.assemblyLayerAssemblyOrderStack.add(maxSize);
        this.queuedAssemblyLayerSizes.add(maxSize);
        this.tileAssemblies = new Int2ObjectOpenHashMap<>();
    }
    @SuppressWarnings("unused")
    private static RegisteredTile[] parseTiles(String tilesString) {
        var                 tilesStrings = DOUBLE_LINES.split(tilesString);
        Set<RegisteredTile> tileRegistry = new ObjectOpenHashSet<>(tilesStrings.length);
        for (String tileString : tilesStrings) {
            new RegisteredTile(tileString, tileRegistry);
        }
        return tileRegistry.toArray(new RegisteredTile[tilesStrings.length]);
    }
    /** @return 1 if assembled a layer, 0 if no more layers to assemble, and -1 if added a layer */
    public int assembleNextAssemblyLayer() {
        if (this.assemblyLayerAssemblyOrderStack.isEmpty()) {
            return 0;
        }
        var dimensions    = this.assemblyLayerAssemblyOrderStack.peekInt(0);
        var assemblyLayer = AssemblyLayer.assembleAssemblyLayer(this, dimensions);
        if (assemblyLayer == null) {
            return -1;
        }
        this.assemblyLayerAssemblyOrderStack.popInt();
        this.queuedAssemblyLayerSizes.remove(dimensions);
        this.tileAssemblies.put(dimensions, assemblyLayer);
        return 1;
    }
    public long getCornerProduct() {
        var product = 1L;
        while (this.assembleNextAssemblyLayer() != 0) { /* intentionally empty */ }
        var finalAssemblyLayer = this.tileAssemblies.get(this.finalDimensions);
        var assembly           = finalAssemblyLayer.unrotatedAssemblies.values().iterator().next().iterator().next();
        product *= assembly.upperLeftTile().problemTileID;
        product *= assembly.upperRightTile().problemTileID;
        product *= assembly.lowerLeftTile().problemTileID;
        product *= assembly.lowerRightTile().problemTileID;
        return product;
    }
}
