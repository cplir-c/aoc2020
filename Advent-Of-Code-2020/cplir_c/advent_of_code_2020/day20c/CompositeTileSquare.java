package cplir_c.advent_of_code_2020.day20c;

import java.util.regex.Matcher;


public class CompositeTileSquare<S extends TileSquare> extends AbstractCachedTileSquare {
    protected final S upperLeft;
    protected final S upperRight;
    protected final S lowerLeft;
    protected final S lowerRight;

    public CompositeTileSquare(S upperLeft, S upperRight, S lowerLeft, S lowerRight) {
        this.upperLeft  = upperLeft;
        this.upperRight = upperRight;
        this.lowerLeft  = lowerLeft;
        this.lowerRight = lowerRight;
    }

    @Override
    protected int getTilesTall() {
        var leftHeight  = this.lowerLeft.tilesTall() + this.upperLeft.tilesTall();
        var rightHeight = this.lowerRight.tilesTall() + this.upperRight.tilesTall();
        if (leftHeight != rightHeight) {
            throw new AssertionError();
        }
        return leftHeight;
    }

    @Override
    protected int getTilesWide() {
        var leftWidth  = this.lowerLeft.tilesWide() + this.upperLeft.tilesWide();
        var rightWidth = this.lowerRight.tilesWide() + this.upperRight.tilesWide();
        if (leftWidth != rightWidth) {
            throw new AssertionError();
        }
        return leftWidth;
    }

    @Override
    protected String getUpperEdge() { return this.upperLeft.up() + this.upperRight.up(); }

    @Override
    protected String getLeftEdge() { return this.upperLeft.left() + this.lowerLeft.left(); }

    @Override
    protected String getLowerEdge() { return this.lowerLeft.down() + this.lowerRight.down(); }

    @Override
    protected String getRightEdge() { return this.upperRight.right() + this.lowerRight.right(); }

    @Override
    protected String getBody() {
        var sb = new StringBuilder(this.upperLeft.body().length() * 4);
        CompositeTileSquare.writeByLinesJoined(sb, this.upperLeft.body(), this.upperRight.body());
        sb.append('\n');
        CompositeTileSquare.writeByLinesJoined(sb, this.lowerLeft.body(), this.lowerRight.body());
        return sb.toString();
    }

    protected static final void writeByLinesJoined(StringBuilder sb, String leftBody, String rightBody) {
        var leftLines  = StaticTile.LINE.matcher(leftBody);
        var rightLines = StaticTile.LINE.matcher(rightBody);
        writeByLinesJoined(sb, leftLines, rightLines);
    }

    protected static final void writeByLinesJoined(StringBuilder sb, Matcher leftLines, Matcher rightLines) {
        while (leftLines.find() && rightLines.find()) {
            leftLines.appendReplacement(sb, "");
            rightLines.appendReplacement(sb, "\n");
        }
        leftLines.appendTail(sb);
        rightLines.appendTail(sb);
    }
}
