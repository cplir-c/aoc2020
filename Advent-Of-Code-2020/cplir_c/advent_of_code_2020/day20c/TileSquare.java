package cplir_c.advent_of_code_2020.day20c;

public interface TileSquare {
    String up();
    String left();
    String down();
    String right();
    String body();
    int tilesWide();
    int tilesTall();
}
