package cplir_c.advent_of_code_2020;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.ShortArrayFIFOQueue;


public class Day22 {

    static final Pattern LINES = Pattern.compile("\\R{2,}");
    static final Pattern LINE  = Pattern.compile("\\R+");
    private static final boolean debug = false;

    public static void main(String[] args) {
        simulateFight(EXAMPLE);
        simulateFight(INPUT);
        simulateRecursiveFight(INPUT);
        simulateRecursiveFight(EXAMPLE);
    }

    static int games = 0;

    static void simulateRecursiveFight(String input) {
        games = 0;
        var spaceDeckOne = new PrintableShortArrayFIFOQueue();
        var spaceDeckTwo = new PrintableShortArrayFIFOQueue();
        parseDecks(input, spaceDeckOne, spaceDeckTwo);
        simulateRecursiveCombat(spaceDeckOne, spaceDeckTwo);
        String              winningPlayer;
        ShortArrayFIFOQueue winningDeck;
        if (spaceDeckOne.isEmpty()) {
            winningPlayer = "player 2";
            winningDeck   = spaceDeckTwo;
        } else {
            winningPlayer = "player 1";
            winningDeck   = spaceDeckOne;
        }
        var score = calculateScore(winningDeck);
        System.out.println(winningPlayer + " won with a recursive score of " + score);
    }

    static void simulateRecursiveCombat(PrintableShortArrayFIFOQueue spaceDeckOne,
                                                PrintableShortArrayFIFOQueue spaceDeckTwo) {
        Object2ObjectMap<PrintableShortArrayFIFOQueue, ObjectSet<PrintableShortArrayFIFOQueue>> oldInstances
            = new Object2ObjectAVLTreeMap<>();
        ++games;
        var gameName = "Game " + games;
        if (debug) { System.out.println("=== " + gameName + " ===\n"); }
        var round = 0;
        while (!spaceDeckOne.isEmpty() && !spaceDeckTwo.isEmpty()) {
            ++round;
            if (debug) {
                System.out.println("-- Round " + round + " (" + gameName + ") --");
            }
            playRecursiveRound(spaceDeckOne, spaceDeckTwo, oldInstances);
            if (debug) {
                System.out.println(round + " of " + gameName.toLowerCase() + "!\n");
            }
        }
        String winningPlayer;
        if (spaceDeckOne.isEmpty()) {
            winningPlayer = "player 2";
        } else {
            winningPlayer = "player 1";
        }
        if (debug) {
            System.out.println("The winner of " + gameName.toLowerCase() + " is " + winningPlayer + "!");
        }
    }

    static void
        playRecursiveRound(PrintableShortArrayFIFOQueue spaceDeckOne, PrintableShortArrayFIFOQueue spaceDeckTwo,
                           Object2ObjectMap<PrintableShortArrayFIFOQueue,
                                            ObjectSet<PrintableShortArrayFIFOQueue>> oldInstances) {
        if (debug) {
            System.out.println("Player 1's deck: " + trimEnds(spaceDeckOne.toString()));
        }
        if (debug) {
            System.out.println("Player 2's deck: " + trimEnds(spaceDeckTwo.toString()));
        }
        if (oldInstances.getOrDefault(spaceDeckOne, ObjectSets.emptySet()).contains(spaceDeckTwo)) {
            spaceDeckOne.enqueue(spaceDeckOne.dequeueShort());
            while (!spaceDeckTwo.isEmpty()) {
                spaceDeckOne.enqueue(spaceDeckTwo.dequeueShort());
            }
            if (debug) { System.out.println("Player 1 won the game due to a repeat configuration"); }
            return;
        } else {
            var newDeckOne = new PrintableShortArrayFIFOQueue(spaceDeckOne);
            var newDeckTwo = new PrintableShortArrayFIFOQueue(spaceDeckTwo);
            oldInstances.computeIfAbsent(newDeckOne, $ -> new ObjectAVLTreeSet<>()).add(newDeckTwo);
        }
        var onesTop = spaceDeckOne.dequeueShort();
        if (debug) { System.out.println("Player 1 plays: " + onesTop); }
        var twosTop = spaceDeckTwo.dequeueShort();
        if (debug) { System.out.println("Player 2 plays: " + twosTop); }

        if (onesTop <= spaceDeckOne.size() && twosTop <= spaceDeckTwo.size()) {
            if (spaceDeckOne.size() < onesTop || spaceDeckTwo.size() < twosTop) {
                throw new AssertionError();
            }
            var copyDeckOne = new PrintableShortArrayFIFOQueue(spaceDeckOne, onesTop);
            if (copyDeckOne.size() != onesTop) {
                throw new AssertionError();
            }
            var copyDeckTwo = new PrintableShortArrayFIFOQueue(spaceDeckTwo, twosTop);
            if (copyDeckTwo.size() != twosTop) {
                throw new AssertionError();
            }
            if (debug) { System.out.println("Player a sub-game to determine the winner...\n"); }
            simulateRecursiveCombat(copyDeckOne, copyDeckTwo);
            if (copyDeckOne.isEmpty()) {
                if (debug) { System.out.print("Player 2 wins round "); }
                spaceDeckTwo.enqueue(twosTop);
                spaceDeckTwo.enqueue(onesTop);
            } else if (copyDeckTwo.isEmpty()) {
                if (debug) { System.out.print("Player 1 wins round "); }
                spaceDeckOne.enqueue(onesTop);
                spaceDeckOne.enqueue(twosTop);
            } else {
                throw new AssertionError("while condition failed");
            }
        } else if (onesTop > twosTop) {
            if (debug) { System.out.print("Player 1 wins round "); }
            spaceDeckOne.enqueue(onesTop);
            spaceDeckOne.enqueue(twosTop);
        } else if (twosTop > onesTop) {
            if (debug) { System.out.print("Player 2 wins round "); }
            spaceDeckTwo.enqueue(twosTop);
            spaceDeckTwo.enqueue(onesTop);
        } else {
            throw new AssertionError("vague rules");
        }
    }

    static String trimEnds(String string) { return string.substring(1, string.length() - 1); }

    static void simulateFight(String input) {
        var spaceDeckOne = new PrintableShortArrayFIFOQueue();
        var spaceDeckTwo = new PrintableShortArrayFIFOQueue();
        parseDecks(input, spaceDeckOne, spaceDeckTwo);
        if (debug) { System.out.println(spaceDeckOne); }
        if (debug) { System.out.println(spaceDeckTwo); }
        while (!spaceDeckOne.isEmpty() && !spaceDeckTwo.isEmpty()) {
            playRound(spaceDeckOne, spaceDeckTwo);
        }
        String              winningPlayer;
        ShortArrayFIFOQueue winningDeck;
        if (spaceDeckOne.isEmpty()) {
            winningPlayer = "player two";
            winningDeck   = spaceDeckTwo;
        } else {
            winningPlayer = "player one";
            winningDeck   = spaceDeckOne;
        }
        var score = calculateScore(winningDeck);
        System.out.println(winningPlayer + " won with a score of " + score);
    }

    static long calculateScore(ShortArrayFIFOQueue winningDeck) {
        var score = 0L;
        var index = 1;
        while (!winningDeck.isEmpty()) {
            score += winningDeck.dequeueLastShort() * index;
            ++index;
        }
        return score;
    }

    static void playRound(PrintableShortArrayFIFOQueue spaceDeckOne, PrintableShortArrayFIFOQueue spaceDeckTwo) {
        var onesTop = spaceDeckOne.dequeueShort();
        var twosTop = spaceDeckTwo.dequeueShort();
        if (onesTop > twosTop) {
            spaceDeckOne.enqueue(onesTop);
            spaceDeckOne.enqueue(twosTop);
        } else if (twosTop > onesTop) {
            spaceDeckTwo.enqueue(twosTop);
            spaceDeckTwo.enqueue(onesTop);
        } else {
            throw new AssertionError("vague rules");
        }
    }

    static void parseDecks(String input, ShortArrayFIFOQueue spaceDeckOne, ShortArrayFIFOQueue spaceDeckTwo) {
        var sections        = LINES.split(input);
        var deckSectionOne  = sections[0];
        var deckSectionTwo  = sections[1];
        var sectionOneLines = LINE.split(deckSectionOne);
        parseDeck(sectionOneLines, spaceDeckOne);
        var sectionTwoLines = LINE.split(deckSectionTwo);
        parseDeck(sectionTwoLines, spaceDeckTwo);
    }

    static void parseDeck(String[] sectionLines, ShortArrayFIFOQueue spaceDeck) {
        for (var i = 1; i < sectionLines.length; ++i) {
            var line = sectionLines[i];
            var card = Short.parseShort(line);
            spaceDeck.enqueue(card);
        }
    }

    static final String EXAMPLE = "Player 1:\n" + "9\n" + "2\n" + "6\n" + "3\n" + "1\n" + "\n" + "Player 2:\n" + "5\n" + "8\n"
            + "4\n" + "7\n" + "10";
    static final String INPUT   = "Player 1:\n" + "7\n" + "1\n" + "9\n" + "10\n" + "12\n" + "4\n" + "38\n" + "22\n" + "18\n"
            + "3\n" + "27\n" + "31\n" + "43\n" + "33\n" + "47\n" + "42\n" + "21\n" + "24\n" + "50\n" + "39\n" + "8\n" + "6\n"
            + "16\n" + "46\n" + "11\n" + "\n" + "Player 2:\n" + "49\n" + "41\n" + "40\n" + "35\n" + "44\n" + "29\n" + "30\n"
            + "19\n" + "14\n" + "2\n" + "34\n" + "17\n" + "25\n" + "5\n" + "15\n" + "32\n" + "20\n" + "48\n" + "45\n" + "26\n"
            + "37\n" + "28\n" + "36\n" + "23\n" + "13";
}
