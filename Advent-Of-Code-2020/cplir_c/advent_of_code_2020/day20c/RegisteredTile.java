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

}
