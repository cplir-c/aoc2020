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


    @Override
    public RegisteredTile upperRightTile() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.TOP_UPWARD -> this.underSquare.upperRightTile();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.upperRightTile();
            case TileOrientation.BACKWARD_TOP_UPWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> this.underSquare.lowerRightTile();
            default -> throw new AssertionError();
        };
    }
    @Override
    public RegisteredTile upperLeftTile() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.TOP_UPWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.upperRightTile();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.BACKWARD_TOP_UPWARD -> this.underSquare.upperRightTile();
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> this.underSquare.lowerLeftTile();
            default -> throw new AssertionError();
        };
    }

    @Override
    public RegisteredTile lowerLeftTile() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.TOP_UPWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.upperRightTile();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.BACKWARD_TOP_UPWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> this.underSquare.upperRightTile();
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> this.underSquare.upperLeftTile();
            default -> throw new AssertionError();
        };
    }

    @Override
    public RegisteredTile lowerRightTile() {
        return switch (this.orientation) {
            case TileOrientation.TOP_RIGHTWARD -> this.underSquare.upperRightTile();
            case TileOrientation.TOP_UPWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.TOP_LEFTWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.TOP_DOWNWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.BACKWARD_TOP_RIGHTWARD -> this.underSquare.upperLeftTile();
            case TileOrientation.BACKWARD_TOP_UPWARD -> this.underSquare.lowerLeftTile();
            case TileOrientation.BACKWARD_TOP_LEFTWARD -> this.underSquare.lowerRightTile();
            case TileOrientation.BACKWARD_TOP_DOWNWARD -> this.underSquare.upperRightTile();
            default -> throw new AssertionError();
        };
    }

}
