package cplir_c.advent_of_code_2020.day20c;

final class TileOrientation {
    static final byte TOP_RIGHTWARD          = 0;
    static final byte TOP_UPWARD             = TOP_RIGHTWARD + 1;
    static final byte TOP_LEFTWARD           = TOP_UPWARD + 1;
    static final byte TOP_DOWNWARD           = TOP_LEFTWARD + 1;
    static final byte BACKWARD_TOP_UPWARD    = TOP_DOWNWARD + 1;
    static final byte BACKWARD_TOP_LEFTWARD  = BACKWARD_TOP_UPWARD + 1;
    static final byte BACKWARD_TOP_DOWNWARD  = BACKWARD_TOP_LEFTWARD + 1;
    static final byte BACKWARD_TOP_RIGHTWARD = BACKWARD_TOP_DOWNWARD + 1;
}