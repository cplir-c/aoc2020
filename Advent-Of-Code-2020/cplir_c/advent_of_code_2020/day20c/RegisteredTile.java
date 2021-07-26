package cplir_c.advent_of_code_2020.day20c;

import java.util.Set;
import java.util.regex.Pattern;

public class RegisteredTile extends StaticTile {

    static final Pattern TILE_ID = Pattern.compile("\\d{2,}");

    final int problemTileID;
    final int tileID;

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
        this.registerTo(tiles);
    }

    private final void registerTo(Set<RegisteredTile> tiles) {
        tiles.add(this);
    }
}
