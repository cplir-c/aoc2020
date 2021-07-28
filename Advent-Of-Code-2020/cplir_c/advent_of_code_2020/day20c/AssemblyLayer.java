package cplir_c.advent_of_code_2020.day20c;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class AssemblyLayer<S extends RegisteredTileSquare> {
    int                                                              tilesWide;
    int                                                              tilesTall;
    final Object2ObjectMap<TileSet, RegisteredTileSquare>            unrotatedAssemblies;
    final Object2ObjectMap<TileSet, ObjectSet<RegisteredTileSquare>> rotatedAssemblies;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByRight;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByUp;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByLeft;
    final Object2ObjectMap<String, ObjectSet<RegisteredTileSquare>>  assembliesByDown;

    protected AssemblyLayer(TileSet allTiles, int dimensions) {
        this.tilesWide           = dimensions >>> Short.SIZE;
        this.tilesTall           = dimensions & 0xffff;
        this.unrotatedAssemblies = assembleUnrotatedAssemblies(allTiles, this.tilesWide, this.tilesTall);
        this.rotatedAssemblies   = rotateAssemblies(this.unrotatedAssemblies);
        this.assembliesByRight   = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByUp      = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByLeft    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
        this.assembliesByDown    = new Object2ObjectOpenHashMap<>(this.rotatedAssemblies.size());
    }

    private static Object2ObjectMap<TileSet, ObjectSet<RegisteredTileSquare>>
        rotateAssemblies(Object2ObjectMap<TileSet, RegisteredTileSquare> unrotatedAssemblies2) {
        // TODO Auto-generated method stub
        return null;
    }

    private static Object2ObjectMap<TileSet, RegisteredTileSquare> assembleUnrotatedAssemblies(TileSet allTiles, int tilesWide2,
                                                                                               int tilesTall2) {
        // TODO Auto-generated method stub
        return null;
    }


}
