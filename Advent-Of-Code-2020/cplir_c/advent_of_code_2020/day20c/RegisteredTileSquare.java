package cplir_c.advent_of_code_2020.day20c;

public interface RegisteredTileSquare extends TileSquare {
    TileSet<RegisteredTile> usedTiles(TileSet<RegisteredTile> allTiles);
    TileSet<RegisteredTile> unusedTiles(TileSet<RegisteredTile> allTiles);
    RegisteredTile upperRightTile();
    RegisteredTile upperLeftTile();
    RegisteredTile lowerLeftTile();
    RegisteredTile lowerRightTile();
}
