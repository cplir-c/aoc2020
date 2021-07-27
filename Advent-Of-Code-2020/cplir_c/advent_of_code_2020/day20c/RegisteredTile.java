package cplir_c.advent_of_code_2020.day20c;

import java.util.Set;
import java.util.regex.Pattern;


public class RegisteredTile extends StaticTile implements RegisteredTileSquare {

    static final Pattern TILE_ID = Pattern.compile("\\d{2,}");

    final int problemTileID;
    final int tileID;
    private TileSet usedTiles;
    private TileSet unusedTiles;

    public RegisteredTile(String tileString, Set<RegisteredTile> tiles) {
        super(tileString.substring(tileString.indexOf('\n') + 1));
        var tileHeader = tileString.substring(0, tileString.indexOf('\n'));
        var idMatcher  = TILE_ID.matcher(tileString);
        if (idMatcher.lookingAt()) {
            var tileIDString = idMatcher.group();
            this.problemTileID = Integer.parseInt(tileIDString);
        } else {
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
    public TileSet usedTiles(TileSet allTiles) {
        if (this.usedTiles == null) {
            this.usedTiles = new TileSet(allTiles);
            this.usedTiles.clear();
            this.usedTiles.add(this);
        }
        return this.usedTiles;
    }
    @Override
    public TileSet unusedTiles(TileSet allTiles) {
        if (this.unusedTiles == null) {
            this.unusedTiles = new TileSet(allTiles);
            this.unusedTiles.remove(this);
        }
        return this.unusedTiles;
    }

}
