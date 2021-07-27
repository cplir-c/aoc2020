package cplir_c.advent_of_code_2020.day20c;

import java.util.regex.Pattern;


public class StaticTile implements TileSquare {
    static final Pattern LINE  = Pattern.compile("\n+");
    private final String         right;
    private final String         up;
    private final String         left;
    private final String         down;
    private final String         body;

    public StaticTile(String body) {
        this.body = body;
        var leftSB  = new StringBuilder(10);
        var rightSB = new StringBuilder(10);
        var lines = LINE.split(body);
        this.up   = lines[0];
        this.down = lines[lines.length - 1];
        for (var row : lines) {
            leftSB.append(row.charAt(0));
            rightSB.append(row.charAt(row.length()));
        }
        this.left  = leftSB.toString();
        this.right = rightSB.toString();
    }

    @Override
    public
    String right() {
        return this.right;
    }

    @Override
    public
    String up() {
        return this.up;
    }

    @Override
    public
    String left() {
        return this.left;
    }

    @Override
    public
    String down() {
        return this.down;
    }

    @Override
    public String body() {
        return this.body;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StaticTile tile) {
            return tile.hashCode() == this.hashCode() && tile.body().equals(this.body);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.body.hashCode();
    }

    @Override
    public int tilesWide() {
        return 1;
    }

    @Override
    public int tilesTall() {
        return 1;
    }
}
