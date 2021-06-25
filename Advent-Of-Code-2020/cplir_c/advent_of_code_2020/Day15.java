package cplir_c.advent_of_code_2020;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class Day15 {

    public static void main(String[] args) {
        final var TURN_NUMBER = 2020;

        findTurnNumber(INPUT, TURN_NUMBER);
        for (int[] example : EXAMPLES) {
            findTurnNumber(example, TURN_NUMBER);
        }
        findTurnNumber(DESCRIPTION, TURN_NUMBER);
        // lmao, I just had to switch from shorts to ints and it worked in a couple seconds
        findTurnNumber(INPUT, 30_000_000);
    }

    static final int NOT_FOUND = -1;

    private static int findTurnNumber(int[] input, int endTurn) {
        int        turnNumber;
        Int2IntMap lessRecent = new Int2IntOpenHashMap();
        Int2IntMap mostRecent = new Int2IntOpenHashMap();

        for (turnNumber = 0; turnNumber < input.length; ++turnNumber) {
            var spoken = input[turnNumber];
            speakNumber(spoken, (turnNumber + 1), lessRecent, mostRecent);
        }
        var previousNumber = input[input.length - 1];
        while (turnNumber < endTurn) {
            ++turnNumber;
            // most recent turn would always be the previous turn
            var lessRecentTurn = lessRecent.getOrDefault(previousNumber, NOT_FOUND);
            int speaking;
            if (lessRecentTurn == NOT_FOUND) {
                speaking = 0;
            } else {
                speaking = (turnNumber - 1) - lessRecentTurn;
            }
            // System.out.println("s" + speaking);
            previousNumber = speakNumber(speaking, turnNumber, lessRecent, mostRecent);
        }
        System.out.println(
            "starting with " + Arrays.toString(input) + " resulted in a " + endTurn + "th turn of " + previousNumber + "."
        );
        return previousNumber;
    }

    private static int speakNumber(int speaking, int turnNumber, Int2IntMap lessRecent, Int2IntMap mostRecent) {
        var mostRecentTurn = mostRecent.getOrDefault(speaking, NOT_FOUND);
        if (mostRecentTurn != NOT_FOUND) {
            lessRecent.put(speaking, mostRecentTurn);
        }
        mostRecent.put(speaking, turnNumber);
        return speaking;
    }

    protected static int[]   DESCRIPTION = {0, 3, 6};
    protected static int[][] EXAMPLES    = {{1, 3, 2}, {2, 1, 3}, {1, 2, 3}, {2, 3, 1}, {3, 2, 1}, {3, 1, 2}};
    protected static int[]   INPUT       = {14, 8, 16, 0, 1, 17};
}
