package cplir_c.advent_of_code_2020.day20c;

public class RegisteredCompositeTileSquare<S extends RegisteredTileSquare> extends CompositeTileSquare<S>
                                         implements RegisteredTileSquare {

    private TileSet usedTiles;
    private TileSet unusedTiles;
    public RegisteredCompositeTileSquare(S upperLeft, S upperRight, S lowerLeft, S lowerRight) {
        super(upperLeft, upperRight, lowerLeft, lowerRight);
    }

    @Override
    public TileSet usedTiles(TileSet allTiles) {
        if (this.usedTiles == null) {
            TileSet used;
            if (this.unusedTiles != null) {
                used = new TileSet(allTiles);
                used.removeAll(this.unusedTiles);
            } else {
                used = new TileSet(this.upperLeft.usedTiles(allTiles));
                used.addAll(this.upperRight.usedTiles(allTiles));
                used.addAll(this.lowerLeft.usedTiles(allTiles));
                used.addAll(this.lowerRight.usedTiles(allTiles));
            }
            this.usedTiles = used;
        }
        return this.usedTiles;
    }

    @Override
    public TileSet unusedTiles(TileSet allTiles) {
        if (this.unusedTiles == null) {
            TileSet unused;
            if (this.usedTiles != null) {
                unused = new TileSet(allTiles);
                unused.removeAll(this.usedTiles);
            } else {
                unused = new TileSet(this.upperLeft.unusedTiles(allTiles));
                unused.retainAll(this.upperRight.unusedTiles(allTiles));
                unused.retainAll(this.lowerLeft.unusedTiles(allTiles));
                unused.retainAll(this.lowerRight.unusedTiles(allTiles));
            }
            this.unusedTiles = unused;
        }
        return this.unusedTiles;
    }

}
