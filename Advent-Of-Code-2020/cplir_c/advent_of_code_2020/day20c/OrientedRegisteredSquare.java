package cplir_c.advent_of_code_2020.day20c;

public class OrientedRegisteredSquare<S extends RegisteredTileSquare> extends OrientedSquare<S>
                                     implements RegisteredTileSquare {
    private TileSet usedTiles;
    private TileSet unusedTiles;
    public OrientedRegisteredSquare(S underTile, byte orientation) {
        super(underTile, orientation);
    }

    @Override
    public TileSet usedTiles(TileSet allTiles) {
        if (this.usedTiles == null) {
            this.usedTiles = this.underSquare.usedTiles(allTiles);
        }
        return this.usedTiles;
    }

    @Override
    public TileSet unusedTiles(TileSet allTiles) {
        if (this.unusedTiles == null) {
            this.unusedTiles = this.underSquare.unusedTiles(allTiles);
        }
        return this.unusedTiles;
    }

}
