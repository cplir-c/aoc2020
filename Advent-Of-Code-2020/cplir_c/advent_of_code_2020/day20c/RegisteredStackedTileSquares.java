package cplir_c.advent_of_code_2020.day20c;

public class RegisteredStackedTileSquares<S extends RegisteredTileSquare> extends StackedTileSquares<S>
                                         implements RegisteredTileSquare {
    public RegisteredStackedTileSquares(S top, S bottom) {
        super(top, bottom);
        this.usedTiles   = null;
        this.unusedTiles = null;
    }

    protected TileSet usedTiles;
    protected TileSet unusedTiles;

    @Override
    public TileSet usedTiles(TileSet allTiles) {
        if (this.usedTiles == null) {
            TileSet used;
            if (this.unusedTiles != null) {
                used = new TileSet(allTiles);
                used.removeAll(this.unusedTiles);
            } else {
                var topTiles    = this.top.usedTiles(allTiles);
                var bottomTiles = this.bottom.usedTiles(allTiles);
                used = new TileSet(topTiles);
                used.addAll(bottomTiles);
            }
            this.usedTiles = used;
        }
        return this.usedTiles;
    }

    @Override
    public TileSet unusedTiles(TileSet allTiles) {
        if (this.unusedTiles == null) {
            TileSet unused;
            if (this.unusedTiles != null) {
                unused = new TileSet(allTiles);
                unused.removeAll(this.usedTiles);
            } else {
                var topTiles    = this.top.unusedTiles(allTiles);
                var bottomTiles = this.bottom.unusedTiles(allTiles);
                unused = new TileSet(topTiles);
                unused.retainAll(bottomTiles);
            }
            this.unusedTiles = unused;
        }
        return this.unusedTiles;
    }

}
