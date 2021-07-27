package cplir_c.advent_of_code_2020.day20c;

abstract class AbstractCachedTileSquare implements TileSquare {
    private String         right;
    private String         up;
    private String         left;
    private String         down;
    private String         body;
    private int    tilesWide;
    private int    tilesTall;

    protected AbstractCachedTileSquare(int tilesWide, int tilesTall) {
        this.tilesWide = tilesWide;
        this.tilesTall = tilesTall;
    }
    protected AbstractCachedTileSquare() {
        this.tilesWide = -1;
        this.tilesTall = -1;
    }

    protected abstract int getTilesTall();
    protected abstract int getTilesWide();
    protected abstract String getUpperEdge();
    protected abstract String getLeftEdge();
    protected abstract String getLowerEdge();
    protected abstract String getRightEdge();
    protected abstract String getBody();

    @Override
    public int tilesWide() {
        if (this.tilesWide < 0) {
            this.tilesWide = this.getTilesWide();
            if (this.tilesWide < 0) {
                throw new AssertionError();
            }
        }
        return this.tilesWide;
    }
    @Override
    public int tilesTall() {
        if (this.tilesTall < 0) {
            this.tilesTall = this.getTilesTall();
            if (this.tilesTall < 0) {
                throw new AssertionError();
            }
        }
        return this.tilesTall;
    }

    @Override
    public String up() {
        if (this.up == null) {
            this.up = this.getUpperEdge();
        }
        return this.up;
    }
    @Override
    public String left() {
        if (this.left == null) {
            this.left = this.getLeftEdge();
        }
        return this.left;
    }
    @Override
    public String down() {
        if (this.down == null) {
            this.down = this.getLowerEdge();
        }
        return this.down;
    }
    @Override
    public String right() {
        if (this.right == null) {
            this.right = this.getRightEdge();
        }
        return this.right;
    }
    @Override
    public String body() {
        if (this.body == null) {
            this.body = this.getBody();
        }
        return this.body;
    }
}
