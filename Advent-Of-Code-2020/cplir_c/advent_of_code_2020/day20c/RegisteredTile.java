package cplir_c.advent_of_code_2020.day20c;

import java.util.Set;
import java.util.regex.Pattern;


public class RegisteredTile extends StaticTile implements RegisteredTileSquare {

    static final Pattern TILE_ID = Pattern.compile("\\d{2,}");

    final int problemTileID;
    final int tileID;
    private TileSet<RegisteredTile> usedTiles;
    private TileSet<RegisteredTile> unusedTiles;

    public RegisteredTile(String tileString, Set<RegisteredTile> tiles) {
        super(tileString.substring(tileString.indexOf('\n') + 1));
        var tileHeader = tileString.substring(5, tileString.indexOf('\n'));
        var idMatcher  = TILE_ID.matcher(tileHeader);
        if (idMatcher.lookingAt()) {
            var tileIDString = idMatcher.group();
            this.problemTileID = Integer.parseInt(tileIDString);
        } else {
            System.err.println(tileString);
            throw new AssertionError();
        }
        this.tileID = tiles.size();
        // now fully initialized
        this.registerTo(tiles);
    }
    private final void registerTo(Set<RegisteredTile> tiles) {
        if (!tiles.add(this)) {
            throw new AssertionError();
        }
    }
    @Override
    public TileSet<RegisteredTile> usedTiles(TileSet<RegisteredTile> allTiles) {
        if (this.usedTiles == null) {
            this.usedTiles = new TileSet<>(allTiles);
            this.usedTiles.clear();
            this.usedTiles.add(this);
        }
        return this.usedTiles;
    }
    @Override
    public TileSet<RegisteredTile> unusedTiles(TileSet<RegisteredTile> allTiles) {
        if (this.unusedTiles == null) {
            this.unusedTiles = new TileSet<>(allTiles);
            this.unusedTiles.remove(this);
        }
        return this.unusedTiles;
    }
    @Override
    public RegisteredTile upperRightTile() {
        return this;
    }
    @Override
    public RegisteredTile upperLeftTile() {
        return this;
    }
    @Override
    public RegisteredTile lowerLeftTile() {
        return this;
    }
    @Override
    public RegisteredTile lowerRightTile() {
        return this;
    }
    @Override
    public String toString() {
        var sb = new StringBuilder(this.body().length() + 12);
        sb.append("Tile ");
        sb.append(this.problemTileID);
        sb.append('#');
        sb.append(this.tileID);
        // sb.append('\n');
        // sb.append(this.body());
        return sb.toString();
    }
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other.hashCode() != this.hashCode() || !(other instanceof RegisteredTile)) {
            return false;
        } else if (other.getClass() == RegisteredTile.class) {
            var rt = (RegisteredTile) other;
            return rt.problemTileID == this.problemTileID && rt.tileID == this.tileID && rt.body().equals(this.body());
        } else {
            return other.equals(this);
        }
    }
    @Override
    public int hashCode() {
        return ((this.problemTileID << 16) + this.tileID) ^ this.body().hashCode();
    }
}
