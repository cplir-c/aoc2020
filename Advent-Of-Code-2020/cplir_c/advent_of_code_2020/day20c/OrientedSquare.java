package cplir_c.advent_of_code_2020.day20c;

public class OrientedSquare<S extends TileSquare> extends AbstractCachedTileSquare {

    final byte     orientation;
    final S underSquare;

    public OrientedSquare(S underTile, byte orientation) {
        this.orientation = orientation;
        this.underSquare = underTile;
    }

    @Override
    /**
     * Equivalent to:
     * <code>
       return switch(this.orientation) &#123;
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.tilesWide();
            case TileOrientation.TOP_UPWARD    -> this.underSquare.tilesTall();
            case TileOrientation.TOP_LEFTWARD  -> this.underSquare.tilesWide();
            case TileOrientation.TOP_DOWNWARD  -> this.underSquare.tilesTall();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.tilesWide();
            case TileOrientation.BACKWARD_TOP_UPWARD    -> this.underSquare.tilesTall();
            case TileOrientation.BACKWARD_TOP_LEFTWARD  -> this.underSquare.tilesWide();
            case TileOrientation.BACKWARD_TOP_DOWNWARD  -> this.underSquare.tilesTall();
            default -> throw new AssertionError();
       &#125;</code>
     */
    protected int getTilesTall() {
        if (this.orientation > 7) {
            throw new AssertionError();
        } else if ((this.orientation & 1) == 0) {
            return this.underSquare.tilesWide();
        } else {
            return this.underSquare.tilesTall();
        }
    }

    @Override
    /**
     * Equivalent to:
     * <code>
       return switch(this.orientation) &#123;
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.tilesTall();
            case TileOrientation.TOP_UPWARD    -> this.underSquare.tilesWide();
            case TileOrientation.TOP_LEFTWARD  -> this.underSquare.tilesTall();
            case TileOrientation.TOP_DOWNWARD  -> this.underSquare.tilesWide();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.tilesTall();
            case TileOrientation.BACKWARD_TOP_UPWARD    -> this.underSquare.tilesWide();
            case TileOrientation.BACKWARD_TOP_LEFTWARD  -> this.underSquare.tilesTall();
            case TileOrientation.BACKWARD_TOP_DOWNWARD  -> this.underSquare.tilesWide();
            default -> throw new AssertionError();
       &#125;</code>
     */
    protected int getTilesWide() {
        if (this.orientation > 7) {
            throw new AssertionError();
        } else if ((this.orientation & 1) == 0) {
            return this.underSquare.tilesTall();
        } else {
            return this.underSquare.tilesWide();
        }
    }

    @Override
    protected String getRightEdge() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.up();
            case TileOrientation.TOP_UPWARD -> this.underSquare.right();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.down();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.left();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> reverse(this.underSquare.up());
            case TileOrientation.BACKWARD_TOP_UPWARD -> reverse(this.underSquare.left());
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> reverse(this.underSquare.down());
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> reverse(this.underSquare.right());
            default -> throw new AssertionError();
        };
    }



    @Override
    protected String getUpperEdge() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.left();
            case TileOrientation.TOP_UPWARD -> this.underSquare.up();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.right();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.down();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> reverse(this.underSquare.left());
            case TileOrientation.BACKWARD_TOP_UPWARD -> reverse(this.underSquare.down());
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> reverse(this.underSquare.right());
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> reverse(this.underSquare.up());
            default -> throw new AssertionError();
        };
    }

    @Override
    protected String getLeftEdge() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.down();
            case TileOrientation.TOP_UPWARD -> this.underSquare.left();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.up();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.right();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> reverse(this.underSquare.down());
            case TileOrientation.BACKWARD_TOP_UPWARD -> reverse(this.underSquare.right());
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> reverse(this.underSquare.up());
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> reverse(this.underSquare.left());
            default -> throw new AssertionError();
        };
    }

    @Override
    protected String getLowerEdge() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.right();
            case TileOrientation.TOP_UPWARD -> this.underSquare.down();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.left();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.up();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> reverse(this.underSquare.right());
            case TileOrientation.BACKWARD_TOP_UPWARD -> reverse(this.underSquare.up());
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> reverse(this.underSquare.left());
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> reverse(this.underSquare.down());
            default -> throw new AssertionError();
        };
    }

    static final String reverse(String in) {
        var sb = new StringBuilder(in);
        sb.reverse();
        return sb.toString();
    }

    @Override
    protected String getBody() {
        if (this.orientation == TileOrientation.TOP_UPWARD) {
            return this.underSquare.body();
        }
        var lines  = StaticTile.LINE.split(this.underSquare.body());
        var width  = 0;
        var height = lines.length;
        for (String line : lines) {
            if (line.length() > width) {
                width = line.length();
            }
        }
        var bodyOut = new StringBuilder(this.underSquare.body().length());
        switch(this.orientation) {
            case TileOrientation.TOP_LEFTWARD:
                // reading order -> ^\^\^
                for (var col = 0; col < width; ++col) {
                    GridFormatting.readLineUp(bodyOut, lines, col);
                }
                break;
            case TileOrientation.TOP_DOWNWARD:
                for (var row = height - 1; row > 0; --row) {
                    GridFormatting.readLineLeft(bodyOut, lines[row]);
                }
                break;
            case TileOrientation.TOP_RIGHTWARD:
                // reading order v\v\v <-
                for (var col = width - 1; col > 0; --col) {
                    GridFormatting.readLineDown(bodyOut, lines, col);
                }
                break;
            case TileOrientation.BACKWARD_TOP_UPWARD:
                for (var line : lines) {
                    GridFormatting.readLineLeft(bodyOut, line);
                }
                break;
            case TileOrientation.BACKWARD_TOP_LEFTWARD:
                // reading order -> v\v\v
                for (var col = 0; col < width; ++col) {
                    GridFormatting.readLineDown(bodyOut, lines, col);
                }
                break;
            case TileOrientation.BACKWARD_TOP_DOWNWARD:
                bodyOut.append(this.underSquare.body());
                bodyOut.reverse();
                break;
            default :
                assert false;
        }
        return bodyOut.toString();
    }

}
