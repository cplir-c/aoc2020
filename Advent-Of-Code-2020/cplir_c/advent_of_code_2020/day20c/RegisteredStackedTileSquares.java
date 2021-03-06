package cplir_c.advent_of_code_2020.day20c;

public class RegisteredStackedTileSquares<S extends RegisteredTileSquare> extends StackedTileSquares<S>
                                         implements RegisteredTileSquare {
    public RegisteredStackedTileSquares(S top, S bottom) {
        super(top, bottom);
        this.usedTiles   = null;
        this.unusedTiles = null;
    }

    protected TileSet<RegisteredTile> usedTiles;
    protected TileSet<RegisteredTile> unusedTiles;

    @Override
    public TileSet<RegisteredTile> usedTiles(TileSet<RegisteredTile> allTiles) {
        if (this.usedTiles == null) {
            TileSet<RegisteredTile> used;
            if (this.unusedTiles != null) {
                used = new TileSet<>(allTiles);
                used.removeAll(this.unusedTiles);
            } else {
                var topTiles    = this.top.usedTiles(allTiles);
                var bottomTiles = this.bottom.usedTiles(allTiles);
                used = new TileSet<>(topTiles);
                used.addAll(bottomTiles);
            }
            this.usedTiles = used;
        }
        return this.usedTiles;
    }

    @Override
    public TileSet<RegisteredTile> unusedTiles(TileSet<RegisteredTile> allTiles) {
        if (this.unusedTiles == null) {
            TileSet<RegisteredTile> unused;
            if (this.unusedTiles != null) {
                unused = new TileSet<>(allTiles);
                unused.removeAll(this.usedTiles);
            } else {
                var topTiles    = this.top.unusedTiles(allTiles);
                var bottomTiles = this.bottom.unusedTiles(allTiles);
                unused = new TileSet<>(topTiles);
                unused.retainAll(bottomTiles);
            }
            this.unusedTiles = unused;
        }
        return this.unusedTiles;
    }

    @Override
    public RegisteredTile upperRightTile() {
        return this.top.upperRightTile();
    }

    @Override
    public RegisteredTile upperLeftTile() {
        return this.top.upperLeftTile();
    }

    @Override
    public RegisteredTile lowerLeftTile() {
        return this.bottom.lowerLeftTile();
    }

    @Override
    public RegisteredTile lowerRightTile() {
        return this.bottom.lowerRightTile();
    }

}
