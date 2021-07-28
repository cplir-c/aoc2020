package cplir_c.advent_of_code_2020.day20c;

public class StackedTileSquares<S extends TileSquare> extends AbstractCachedTileSquare implements TileSquare {
    protected final S top;
    protected final S bottom;

    public StackedTileSquares(S top, S bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    protected int getTilesTall() {
        return this.top.tilesTall() + this.bottom.tilesTall();
    }

    @Override
    protected int getTilesWide() {
        var topWidth    = this.top.tilesWide();
        var bottomWidth = this.bottom.tilesWide();
        if (topWidth != bottomWidth) {
            throw new AssertionError();
        }
        return topWidth;
    }

    @Override
    protected String getUpperEdge() { return this.top.up(); }

    @Override
    protected String getLeftEdge() { return this.top.left() + this.bottom.left(); }

    @Override
    protected String getLowerEdge() { return this.bottom.down(); }

    @Override
    protected String getRightEdge() { return this.top.right() + this.bottom.right(); }

    @Override
    protected String getBody() {
        var topBody    = this.top.body();
        var bottomBody = this.bottom.body();
        var sb         = new StringBuilder(topBody.length() + bottomBody.length() + 1);
        sb.append(topBody);
        sb.append('\n');
        sb.append(bottomBody);
        return sb.toString();
    }

}
