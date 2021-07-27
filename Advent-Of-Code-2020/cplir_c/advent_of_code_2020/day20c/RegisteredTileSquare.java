package cplir_c.advent_of_code_2020.day20c;

public interface RegisteredTileSquare extends TileSquare {
    TileSet usedTiles(TileSet allTiles);
    TileSet unusedTiles(TileSet allTiles);
}
