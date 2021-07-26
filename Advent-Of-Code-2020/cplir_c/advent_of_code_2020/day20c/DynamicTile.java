package cplir_c.advent_of_code_2020.day20c;

public class DynamicTile<S extends StaticTile> {

    byte       orientation;
    final S underTile;

    public DynamicTile(S underTile) {
        this.orientation = TileOrientation.TOP_UPWARD;
        this.underTile = underTile;
    }

    public String body() {
        if (this.orientation == TileOrientation.TOP_UPWARD) {
            return this.underTile.body();
        }
        var lines  = StaticTile.LINE.split(this.underTile.body());
        var width  = 0;
        var height = lines.length;
        for (String line : lines) {
            if (line.length() > width) {
                width = line.length();
            }
        }
        var bodyOut = new StringBuilder(this.underTile.body().length());
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
                bodyOut.append(this.underTile.body());
                bodyOut.reverse();
                break;
            default :
                assert false;
        }
        return bodyOut.toString();
    }
}
