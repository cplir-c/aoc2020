package cplir_c.advent_of_code_2020;

import java.util.Arrays;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;


public class Day10 {

    public static void main(String[] args) {
        findJoltages(SMALL);
        findJoltages(MEDIUM);
        findJoltages(INPUT);
        if (findArrangements(SMALL) != 8) {
            throw new AssertionError("findArrangements(SMALL) == 8");
        }
        if (findArrangements(MEDIUM) != 19208) {
            throw new AssertionError("findArrangements(MEDIUM) == 19288");
        }
        if (findArrangements(INPUT) < 1_000_000_000_000L) {
            throw new AssertionError("findArrangements(INPUT) >= 1_000_000_000_000L");
        }
    }
    private static long findArrangements(String inputString) {
        var input = parseAndSortAdapters(inputString);
        var graph = buildDAG(input);
        var paths = countPaths(graph);
        System.out.println("found " + paths + " paths through an input of size " + input.length);
        return paths;
    }
    private static long countPaths(int[][] graph) {
        System.out.println(Arrays.deepToString(graph));
        var nodePaths = new long[graph.length + 1];
        nodePaths[graph.length] = 1;
        for (var i = graph.length - 1; i >= 0; --i) {
            var paths     = 0L;
            var neighbors = graph[i];
            for (int neighbor : neighbors) {
                paths += nodePaths[neighbor];
            }
            nodePaths[i] = paths;
        }
        return nodePaths[0];
    }
    private static int[][] buildDAG(int[] input) {
        ObjectList<int[]> ill = new ObjectArrayList<>();
        for (var i = 0; i < input.length - 1; ++i) {
            IntList connections = new IntArrayList(3);
            var ival = input[i] + 3;
            var j    = i + 1;
            while (j < input.length && input[j] <= ival) {
                connections.add(j);
                j++;
            }
            ill.add(connections.toArray(new int[connections.size()]));
        }
        return ill.toArray(new int[ill.size()][]);
    }
    private static void findJoltages(String inputString) {
        var input      = parseAndSortAdapters(inputString);
        var oneDiffs   = 0;
        var threeDiffs = 0;
        for (var i = 0; i < input.length - 1; ++i) {
            var diff = input[i + 1] - input[i];
            if (diff == 1) {
                ++oneDiffs;
            } else if (diff == 3) {
                ++threeDiffs;
            }
        }
        System.out.println("1 " + oneDiffs + " 3 " + threeDiffs + " * " + (oneDiffs * threeDiffs));

    }
    private static int[] parseAndSortAdapters(String input) {
        var inputLines = SPACE_SPLIT.split(input);
        var input1     = new int[inputLines.length + 2];
        for (var i = 0; i < inputLines.length; ++i) {
            input1[i] = Integer.parseInt(inputLines[i]);
        }
        input1[input1.length - 2] = 0;
        var maxJoltage = input1[0];
        for (var i = 0; i < input1.length - 2; ++i) {
            var joltage = input1[i];
            if (joltage > maxJoltage) {
                maxJoltage = joltage;
            }
        }
        input1[input1.length - 1] = maxJoltage + 3;
        var joltages = input1;
        IntArrays.unstableSort(joltages);
        System.out.println(Arrays.toString(joltages));
        return input1;
    }

    private static final Pattern SPACE_SPLIT = Pattern.compile("\\s+");

    static final String SMALL  = "16\n" + "10\n" + "15\n" + "5\n" + "1\n" + "11\n" + "7\n" + "19\n" + "6\n" + "12\n" + "4";
    static final String MEDIUM = "28\n" + "33\n" + "18\n" + "42\n" + "31\n" + "14\n" + "46\n" + "20\n" + "48\n" + "47\n"
            + "24\n" + "23\n" + "49\n" + "45\n" + "19\n" + "38\n" + "39\n" + "11\n" + "1\n" + "32\n" + "25\n" + "35\n" + "8\n"
            + "17\n" + "7\n" + "9\n" + "4\n" + "2\n" + "34\n" + "10\n" + "3";
    static final String INPUT  = "153\n" + "17\n" + "45\n" + "57\n" + "16\n" + "147\n" + "39\n" + "121\n" + "75\n" + "70\n"
            + "85\n" + "134\n" + "128\n" + "115\n" + "51\n" + "139\n" + "44\n" + "65\n" + "119\n" + "168\n" + "122\n" + "72\n"
            + "105\n" + "31\n" + "103\n" + "89\n" + "154\n" + "114\n" + "55\n" + "25\n" + "48\n" + "38\n" + "132\n" + "157\n"
            + "84\n" + "71\n" + "113\n" + "143\n" + "83\n" + "64\n" + "109\n" + "129\n" + "120\n" + "100\n" + "151\n" + "79\n"
            + "125\n" + "22\n" + "161\n" + "167\n" + "19\n" + "26\n" + "118\n" + "142\n" + "4\n" + "158\n" + "11\n" + "35\n"
            + "56\n" + "18\n" + "40\n" + "7\n" + "150\n" + "99\n" + "54\n" + "152\n" + "60\n" + "27\n" + "164\n" + "78\n"
            + "47\n" + "82\n" + "63\n" + "46\n" + "91\n" + "32\n" + "135\n" + "3\n" + "108\n" + "10\n" + "159\n" + "127\n"
            + "69\n" + "110\n" + "126\n" + "133\n" + "28\n" + "15\n" + "104\n" + "138\n" + "160\n" + "98\n" + "90\n" + "144\n"
            + "1\n" + "2\n" + "92\n" + "41\n" + "86\n" + "66\n" + "95\n" + "12";
}
