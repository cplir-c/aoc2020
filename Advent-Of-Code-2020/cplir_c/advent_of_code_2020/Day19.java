package cplir_c.advent_of_code_2020;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;


public class Day19 {
    // started at 5:08 PM 12/20/2020
    // part 1 passed 7:19:50 PM 12/20/2020
    static final Pattern LINES = Pattern.compile("\n{2,}");
    static final Pattern LINE  = Pattern.compile("\n+");
    static final Pattern LABEL = Pattern.compile(":\\s+");
    static final Pattern OR    = Pattern.compile("\\s*[|]\\s*");
    static final Pattern QUOTE = Pattern.compile("\"");
    static final Pattern SPACE = Pattern.compile("\\s+");

    public static final void main(String... argen) {
        // countMatchingZero(INPUT);
        countLoopedMatchingZero(INPUT);
        // countMatchingZero(EXAMPLE2);
        countLoopedMatchingZero(EXAMPLE2);
        // countMatchingZero(EXAMPLE);
        countLoopedMatchingZero(EXAMPLE);
    }

    private static void countLoopedMatchingZero(String input) {
        var sections = LINES.split(input);

        var rulesString    = sections[0];
        var messagesString = sections[1];

        var messages = LINE.split(messagesString);
        System.out.println(Arrays.stream(messages).mapToInt(String::length).max());

        var stringRules = parseRuleString(rulesString);
        var rules       = parseLoopRules(stringRules);
        var rule0       = rules.get(0);
        var matching    = countMatching(messages, rule0);
        System.out.println(matching + " rules match loopy rule 0: " + rule0.pattern() + "\n");
    }

    private static ObjectList<Pattern> parseLoopRules(ObjectList<String> stringRules) {
        var                 laterSet  = new IntOpenHashSet();
        var                 laterList = new PrintableIntArrayFIFOQueue();
        ObjectList<Pattern> rules     = new ObjectArrayList<>(stringRules.size());
        rules.size(stringRules.size());
        laterList.enqueue(0);
        laterSet.add(0);
        while (!laterList.isEmpty()) {
            // System.out.println(laterList);
            var ruleLabel = laterList.dequeueInt();
            laterSet.remove(ruleLabel);
            if (rules.get(ruleLabel) != null) {
                continue;
            }
            var ruleString = stringRules.get(ruleLabel);
            // System.out.println(ruleLabel + ": " + ruleString);
            if (laterSet.size() != laterList.size()) {
                throw new AssertionError();
            }
            if (ruleString.indexOf('"') >= 0) {
                // literal rule
                var rule = QUOTE.split(ruleString);
                if (rule.length != 2) {
                    throw new AssertionError(rule.length + " " + Arrays.toString(rule) + " " + ruleString);
                }
                // System.out.println(rule.length + " " + Arrays.toString(rule) + " " + ruleString);
                var chr = rule[1];
                chr = Pattern.quote(chr).substring(2, 3);
                var pattern = Pattern.compile(chr);
                rules.set(ruleLabel, pattern);
            } else {
                var orBlocks    = OR.split(ruleString);
                var ruleBuilder = new StringBuilder(ruleString.length());
                ruleBuilder.append("(?:");
                int orIndex;
                for (orIndex = orBlocks.length - 1; orIndex >= 0; --orIndex) {
                    var orBlock            = orBlocks[orIndex];
                    var consecutiveStrings = SPACE.split(orBlock);
                    var consecutiveRules   = parseIntArray(consecutiveStrings);
                    var blockBuilder       = new StringBuilder(orBlock.length());
                    blockBuilder.append("(?:");
                    int consecIndex;
                    for (consecIndex = 0; consecIndex < consecutiveStrings.length; ++consecIndex) {
                        var otherLabel = consecutiveRules[consecIndex];
                        var otherRule  = rules.get(otherLabel);
                        if (otherRule == null) {
                            break;
                        }
                        blockBuilder.append(otherRule.pattern());
                    }
                    if (consecIndex < consecutiveStrings.length) {
                        consecIndex = dumpRemainderOfRuleBlock(
                            laterSet, laterList, rules, consecutiveStrings, consecutiveRules, consecIndex
                        );
                        break;
                    }
                    blockBuilder.append(')');
                    ruleBuilder.append(blockBuilder);
                    ruleBuilder.append('|');
                }
                if (orIndex >= 0) {
                    orIndex = dumpRemainingRuleBlocks(laterSet, laterList, rules, ruleLabel, orBlocks, orIndex);
                    continue;
                }
                ruleBuilder.setLength(ruleBuilder.length() - 1);
                ruleBuilder.append(')');
                Pattern pattern;
                // loopy 8 and 11 have the same dependencies as normal 8 and 11, so they must be satisfied now that the rule
                // built
                // hardcode the special patterns
                if (ruleLabel == 8) {
                    ruleBuilder.append('+');
                } else if (ruleLabel == 11) {
                    ruleBuilder.insert(0, "(?:");
                    var rule42 = rules.get(42);
                    var rule31 = rules.get(31);
                    for (var i = 2; i <= 100; ++i) {
                        ruleBuilder.append('|');
                        ruleBuilder.append("(?:");
                        ruleBuilder.append(rule42);
                        ruleBuilder.append('{');
                        ruleBuilder.append(i);
                        ruleBuilder.append('}');
                        ruleBuilder.append(rule31);
                        ruleBuilder.append('{');
                        ruleBuilder.append(i);
                        ruleBuilder.append('}');
                        ruleBuilder.append(')');
                    }
                    ruleBuilder.append(')');
                } else if (ruleBuilder.indexOf("\\1)") >= 0) {
                    var j = 1;
                    for (var i = ruleBuilder.indexOf("\\1)", ruleBuilder.indexOf("\\1)") + 1); i >= 0;
                         i = ruleBuilder.indexOf("\\1)", i + 1)) {
                        ++j; // starts counting at 2, position 1 is fine
                        ruleBuilder.replace(i, i + "\\1".length(), "\\" + Integer.toString(j));
                    }
                }
                pattern = Pattern.compile(ruleBuilder.toString());
                if (pattern == null) {
                    throw new AssertionError();
                }
                // System.out.println("label " + ruleLabel + " compiled to " + pattern.pattern());
                rules.set(ruleLabel, pattern);
            }
        }
        return rules;
    }

    private static int countMatching(String[] messages, Pattern rule0) {
        var     matching = 0;
        Matcher matcher  = null;
        for (String message : messages) {
            if (matcher == null) {
                matcher = rule0.matcher(message);
            } else {
                matcher.reset(message);
            }
            if (matcher.matches()) {
                ++matching;
            }
        }
        return matching;
    }

    private static ObjectList<String> parseRuleString(String rulesString) {
        var                ruleStringArray = LINE.split(rulesString);
        ObjectList<String> stringRules     = new ObjectArrayList<>(ruleStringArray.length);
        stringRules.size(ruleStringArray.length);
        for (String lineString : ruleStringArray) {
            var rule        = LABEL.split(lineString);
            var labelString = rule[0];
            var label       = Integer.parseInt(labelString);
            var ruleString  = rule[1];
            while (stringRules.size() <= label) {
                stringRules.add(null);
            }
            stringRules.set(label, ruleString);
        }
        return stringRules;
    }

    static void countMatchingZero(String input) {
        var sections = LINES.split(input);

        var rulesString    = sections[0];
        var messagesString = sections[1];

        var messages = LINE.split(messagesString);

        var stringRules = parseRuleString(rulesString);
        var rules       = parseRules(stringRules);
        System.out.println(rules);
        var rule0 = rules.get(0);
        System.out.println(rule0);
        var matching = countMatching(messages, rule0);
        System.out.println(matching + " rules match rule 0: " + rule0.pattern());
    }

    static ObjectList<Pattern> parseRules(ObjectList<String> stringRules) {
        var                 laterSet  = new IntOpenHashSet();
        var                 laterList = new PrintableIntArrayFIFOQueue();
        ObjectList<Pattern> rules     = new ObjectArrayList<>(stringRules.size());
        rules.size(stringRules.size());
        laterList.enqueue(0);
        laterSet.add(0);
        while (!laterList.isEmpty()) {
            // System.out.println(laterList);
            var ruleLabel = laterList.dequeueInt();
            laterSet.remove(ruleLabel);
            if (rules.get(ruleLabel) != null) {
                continue;
            }
            var ruleString = stringRules.get(ruleLabel);
            System.out.println(ruleLabel + ": " + ruleString);
            if (laterSet.size() != laterList.size()) {
                throw new AssertionError();
            }
            if (ruleString.indexOf('"') >= 0) {
                // literal rule
                var rule = QUOTE.split(ruleString);
                if (rule.length != 2) {
                    throw new AssertionError(rule.length + " " + Arrays.toString(rule) + " " + ruleString);
                }
                System.out.println(rule.length + " " + Arrays.toString(rule) + " " + ruleString);
                var chr = rule[1];
                chr = Pattern.quote(chr).substring(2, 3);
                var pattern = Pattern.compile(chr);
                rules.set(ruleLabel, pattern);
            } else {
                var orBlocks    = OR.split(ruleString);
                var ruleBuilder = new StringBuilder(ruleString.length());
                ruleBuilder.append("(?:");
                int orIndex;
                for (orIndex = orBlocks.length - 1; orIndex >= 0; --orIndex) {
                    var orBlock            = orBlocks[orIndex];
                    var consecutiveStrings = SPACE.split(orBlock);
                    var consecutiveRules   = parseIntArray(consecutiveStrings);
                    var blockBuilder       = new StringBuilder(orBlock.length());
                    blockBuilder.append("(?:");
                    int consecIndex;
                    for (consecIndex = 0; consecIndex < consecutiveStrings.length; ++consecIndex) {
                        var otherLabel = consecutiveRules[consecIndex];
                        var otherRule  = rules.get(otherLabel);
                        if (otherRule == null) {
                            break;
                        }
                        blockBuilder.append(otherRule.pattern());
                    }
                    if (consecIndex < consecutiveStrings.length) {
                        consecIndex = dumpRemainderOfRuleBlock(
                            laterSet, laterList, rules, consecutiveStrings, consecutiveRules, consecIndex
                        );
                        break;
                    }
                    blockBuilder.append(')');
                    ruleBuilder.append(blockBuilder);
                    ruleBuilder.append('|');
                }
                if (orIndex >= 0) {
                    orIndex = dumpRemainingRuleBlocks(laterSet, laterList, rules, ruleLabel, orBlocks, orIndex);
                    continue;
                }
                ruleBuilder.setLength(ruleBuilder.length() - 1);
                ruleBuilder.append(')');
                var pattern = Pattern.compile(ruleBuilder.toString());
                System.out.println("label " + ruleLabel + " compiled to " + pattern.pattern());
                rules.set(ruleLabel, pattern);
            }
        }
        return rules;
    }

    static int dumpRemainderOfRuleBlock(IntOpenHashSet laterSet, PrintableIntArrayFIFOQueue laterList,
                                        ObjectList<Pattern> rules, String[] consecutiveStrings, int[] consecutiveRules,
                                        int consecIndex) {
        for (consecIndex = Math.max(--consecIndex, 0); consecIndex < consecutiveStrings.length; ++consecIndex) {
            var otherLabel = consecutiveRules[consecIndex];
            var otherRule  = rules.get(otherLabel);
            if (otherRule == null && !laterSet.contains(otherLabel)) {
                laterSet.add(otherLabel);
                laterList.enqueue(otherLabel);
            }
        }
        return consecIndex;
    }

    static int dumpRemainingRuleBlocks(IntOpenHashSet laterSet, PrintableIntArrayFIFOQueue laterList, ObjectList<Pattern> rules,
                                       int ruleLabel, String[] orBlocks, int orIndex)
            throws AssertionError {
        for (--orIndex; orIndex >= 0; --orIndex) {
            var orBlock            = orBlocks[orIndex];
            var consecutiveStrings = SPACE.split(orBlock);
            var consecutiveRules   = parseIntArray(consecutiveStrings);
            for (var consecIndex = consecutiveRules.length - 1; consecIndex >= 0; --consecIndex) {
                var otherLabel = consecutiveRules[consecIndex];
                var otherRule  = rules.get(otherLabel);
                if (otherRule == null && !laterSet.contains(otherLabel)) {
                    laterSet.add(otherLabel);
                    laterList.enqueue(otherLabel);
                }
            }
        }
        if (laterSet.contains(ruleLabel)) {
            throw new AssertionError();
        }
        laterList.enqueue(ruleLabel);
        laterSet.add(ruleLabel);
        return orIndex;
    }

    static int[] parseIntArray(String[] integerArray) {
        var ints = new int[integerArray.length];
        for (var i = ints.length - 1; i >= 0; --i) {
            ints[i] = Integer.parseInt(integerArray[i]);
        }
        return ints;
    }

    static final String EXAMPLE  = "0: 4 1 5\n" + "1: 2 3 | 3 2\n" + "2: 4 4 | 5 5\n" + "3: 4 5 | 5 4\n" + "4: \"a\"\n"
            + "5: \"b\"\n" + "\n" + "ababbb\n" + "bababa\n" + "abbbab\n" + "aaabbb\n" + "aaaabbb";
    static final String EXAMPLE2 = "42: 9 14 | 10 1\n" + "9: 14 27 | 1 26\n" + "10: 23 14 | 28 1\n" + "1: \"a\"\n"
            + "11: 42 31\n" + "5: 1 14 | 15 1\n" + "19: 14 1 | 14 14\n" + "12: 24 14 | 19 1\n" + "16: 15 1 | 14 14\n"
            + "31: 14 17 | 1 13\n" + "6: 14 14 | 1 14\n" + "2: 1 24 | 14 4\n" + "0: 8 11\n" + "13: 14 3 | 1 12\n"
            + "15: 1 | 14\n" + "17: 14 2 | 1 7\n" + "23: 25 1 | 22 14\n" + "28: 16 1\n" + "4: 1 1\n" + "20: 14 14 | 1 15\n"
            + "3: 5 14 | 16 1\n" + "27: 1 6 | 14 18\n" + "14: \"b\"\n" + "21: 14 1 | 1 14\n" + "25: 1 1 | 1 14\n"
            + "22: 14 14\n" + "8: 42\n" + "26: 14 22 | 1 20\n" + "18: 15 15\n" + "7: 14 5 | 1 21\n" + "24: 14 1\n" + "\n"
            + "abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa\n" + "bbabbbbaabaabba\n" + "babbbbaabbbbbabbbbbbaabaaabaaa\n"
            + "aaabbbbbbaaaabaababaabababbabaaabbababababaaa\n" + "bbbbbbbaaaabbbbaaabbabaaa\n"
            + "bbbababbbbaaaaaaaabbababaaababaabab\n" + "ababaaaaaabaaab\n" + "ababaaaaabbbaba\n"
            + "baabbaaaabbaaaababbaababb\n" + "abbbbabbbbaaaababbbbbbaaaababb\n" + "aaaaabbaabaaaaababaa\n"
            + "aaaabbaaaabbaaa\n" + "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa\n" + "babaaabbbaaabaababbaabababaaab\n"
            + "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba";
    static final String INPUT    = "101: 64 33 | 14 121\n" + "130: 14 96\n" + "117: 64 14 | 14 14\n" + "48: 78 14 | 102 64\n"
            + "107: 14 14 | 64 64\n" + "56: 14 43 | 64 104\n" + "5: 107 14 | 106 64\n" + "67: 14 44 | 64 94\n"
            + "100: 14 39 | 64 103\n" + "32: 14 96 | 64 16\n" + "25: 64 107 | 14 96\n" + "37: 64 108 | 14 30\n"
            + "42: 95 14 | 27 64\n" + "113: 14 79\n" + "13: 14 136 | 64 116\n" + "83: 64 62 | 14 2\n" + "105: 14 108 | 64 117\n"
            + "28: 14 133 | 64 35\n" + "66: 64 106 | 14 96\n" + "41: 64 85 | 14 60\n" + "88: 16 64 | 108 14\n"
            + "49: 58 64 | 14 14\n" + "111: 52 64 | 114 14\n" + "3: 14 124 | 64 119\n" + "82: 14 17 | 64 47\n"
            + "116: 49 14 | 98 64\n" + "91: 58 6\n" + "127: 106 64 | 30 14\n" + "125: 56 14 | 100 64\n" + "23: 64 61 | 14 5\n"
            + "31: 64 9 | 14 109\n" + "86: 125 14 | 99 64\n" + "35: 64 49 | 14 106\n" + "10: 64 25 | 14 84\n"
            + "85: 92 64 | 132 14\n" + "46: 14 18 | 64 82\n" + "128: 78 14 | 79 64\n" + "79: 64 64 | 14 64\n"
            + "29: 135 14 | 68 64\n" + "87: 106 14 | 79 64\n" + "96: 64 14 | 64 64\n" + "27: 134 14 | 51 64\n"
            + "109: 76 64 | 86 14\n" + "9: 64 120 | 14 74\n" + "81: 64 131 | 14 93\n" + "12: 118 64 | 37 14\n" + "11: 42 31\n"
            + "108: 64 14 | 14 64\n" + "16: 64 14 | 14 58\n" + "63: 117 64 | 16 14\n" + "126: 64 129 | 14 15\n"
            + "53: 64 79 | 14 16\n" + "75: 105 14 | 88 64\n" + "93: 127 14 | 73 64\n" + "26: 36 14 | 97 64\n" + "124: 30 58\n"
            + "36: 106 14 | 117 64\n" + "21: 25 14 | 119 64\n" + "92: 64 96 | 14 102\n" + "131: 64 59 | 14 66\n"
            + "4: 64 108 | 14 106\n" + "44: 32 64 | 71 14\n" + "45: 41 14 | 67 64\n" + "94: 119 14 | 110 64\n" + "58: 14 | 64\n"
            + "38: 14 14\n" + "112: 75 14 | 21 64\n" + "104: 58 79\n" + "59: 14 16 | 64 98\n" + "114: 14 30 | 64 102\n"
            + "65: 96 14 | 6 64\n" + "68: 30 14 | 79 64\n" + "129: 29 14 | 12 64\n" + "39: 14 6 | 64 117\n"
            + "78: 14 14 | 64 58\n" + "115: 14 72 | 64 24\n" + "57: 64 122 | 14 22\n" + "71: 107 14 | 96 64\n"
            + "30: 14 14 | 14 64\n" + "64: \"a\"\n" + "106: 14 64\n" + "89: 28 64 | 80 14\n" + "20: 64 70 | 14 19\n"
            + "133: 107 14 | 34 64\n" + "34: 64 64\n" + "22: 96 58\n" + "90: 16 14 | 96 64\n" + "51: 14 46 | 64 112\n"
            + "69: 23 14 | 13 64\n" + "15: 14 55 | 64 3\n" + "123: 30 14 | 108 64\n" + "17: 117 64 | 78 14\n"
            + "40: 49 64 | 117 14\n" + "135: 64 34 | 14 108\n" + "47: 64 102 | 14 78\n" + "97: 14 34 | 64 117\n"
            + "2: 64 63 | 14 88\n" + "54: 14 26 | 64 77\n" + "118: 98 14 | 96 64\n" + "76: 69 14 | 54 64\n" + "102: 64 14\n"
            + "121: 114 14 | 130 64\n" + "99: 64 57 | 14 10\n" + "60: 130 64 | 65 14\n" + "98: 58 58\n"
            + "132: 64 117 | 14 30\n" + "74: 89 14 | 50 64\n" + "19: 78 64 | 6 14\n" + "1: 115 64 | 20 14\n"
            + "18: 53 64 | 128 14\n" + "72: 14 78 | 64 30\n" + "103: 34 64 | 38 14\n" + "52: 30 14 | 49 64\n"
            + "120: 83 14 | 81 64\n" + "70: 79 14 | 108 64\n" + "7: 64 113 | 14 40\n" + "24: 49 14 | 16 64\n"
            + "84: 30 64 | 34 14\n" + "110: 64 30 | 14 96\n" + "55: 14 90 | 64 37\n" + "62: 14 4 | 64 47\n"
            + "80: 64 48 | 14 91\n" + "95: 126 64 | 45 14\n" + "77: 14 87 | 64 35\n" + "14: \"b\"\n" + "134: 101 64 | 1 14\n"
            + "119: 14 102 | 64 102\n" + "0: 8 11\n" + "33: 14 92 | 64 123\n" + "6: 58 64 | 64 14\n" + "50: 111 64 | 7 14\n"
            + "43: 14 98 | 64 38\n" + "73: 102 14 | 6 64\n" + "61: 96 64\n" + "8: 42\n" + "136: 30 64 | 96 14\n"
            + "122: 58 49\n" + "\n" + "ababbbaaabbbbbbbbaaaabaaabbabaabbaaababbabbbbbbaabbbbbbababbaaaa\n"
            + "aabaabaaabbabaaaabbbabbbbbbbaababbababaa\n" + "baaabbbabbababbbbaaabbab\n" + "babbbaabbabbbbbbbbabbbababbbaaaa\n"
            + "baabbaabbabbabbaabbbabababbabaaa\n" + "ababbbabaaaaaaababbabaabaabbaaaa\n"
            + "abbabbaaaabbbaaababbababbbbbbabbbbaaabbaaababbbb\n" + "bbbaabaababaaabbbabaaabbbbaaaabaabaaaaabaaababaa\n"
            + "ababaabbbaababaabbababbabbaabaabaabbababbbbababb\n" + "bbaaaaaabbbbabbabaaababbabaababbaaaabaab\n"
            + "bbaaaabaaaababbbbabbbbaababaabbaababbabbbabbababbbababaa\n" + "bbabbbbabbbabaabaabbbbba\n"
            + "aaabaaabaaabbbbabaaaababbbbaaaaa\n"
            + "bbaabbaabbbbbbaaabababbbaabaaabaabaabbbbbabbaababbbbbbaaabbbabaaababbaabbbbbaabbbbaababababaabaa\n"
            + "bbbbbabaabababbaaaaabbbb\n" + "ababababbbaaaaaaaababababbbaaabaabbbbbaa\n"
            + "aaabbabbaaaaababbaabaaaabaaababbbabaabbb\n" + "bbaaabbaababaaaaabababababbbababbabbbbabaaabbaaaabbbabaa\n"
            + "ababababbabaabaabaaababbbbabbbaabaabababaaaaaabb\n"
            + "aaabbbbaabbbaaabbbabbbababbababaaabababaabbbaaaababbaaaababaaaaabaaabaab\n"
            + "abababababbabaababababababbaaaababbaaaaa\n" + "aaabbaabbbbbbaaabaaaaaaa\n" + "abbaabbaabbababaabbaaaab\n"
            + "baaababbababbaaaababaabababbaaaaaabaabab\n"
            + "baabbabbababbbbbaaabbbbbbaaabaaabaaaabbbbbbababaaabbbabbbbababababbbaabb\n" + "baaabbbbababaaaaabbaaaab\n"
            + "bbababbaaaabbbbaabbaaaba\n" + "baababaabaabbbbbabaabbbabbbbababbaaabaaabbbbaabaaabbabab\n"
            + "bbabaabababbaaabaaaaaaabbabaaaaaaabaaaaa\n" + "baabbabaaabbbbaabbbbaaab\n"
            + "aabaabbabbabbaabbaabbbaaabaabbbbbbabbaba\n"
            + "baaabbbbababbaaaaabaabbabaabbabaabaaaaabaaabbaaaaabbabbbaabbabbbbaaaaabb\n"
            + "baaababbbbabbbaabbababbbaaabbabbaabbbbaabbbabbaabbbbabbb\n"
            + "ababbaaababababbaabbbababaabbbbbababbabbaababbbbaaaaaabbaaaaabba\n" + "aaabbaabaaabbbbabbbbaaaaababbbbb\n"
            + "baabbaababbaaaabbaaabbaa\n" + "bbbababababbbaaabbbaabab\n" + "bbaaabaabbaaababbbbbabbb\n"
            + "abababbabbabbaabaabbbabb\n" + "bbaaaaaaaababababaaaaaaa\n" + "aabaaabbbabbbbaaabbaabbaaabbbababbababab\n"
            + "babaaababbabbaaaabbaabbb\n" + "babababbbabaaabbbaaaaaab\n" + "ababbabbabaaabbabbbbbbbb\n"
            + "babbbbabaabaaabbbaaaabababbbabbbabbaaaba\n" + "bbaaaabbbbaabbbbbbbbaaab\n" + "bbbaaabbbaaaaaaabababaababaabbab\n"
            + "aabaabbababaaaabbbbbbbbbbbbbaabbbbbaabababbbababbabbaaabaabaabbaaabbbababbbbbbbbbaabbbaaabbbabba\n"
            + "abaabbbaaaababbaabbaabaabababbbabaabbabaaabbabaabbababaa\n" + "aaabbbbbbabaabbaabbaaaaa\n"
            + "bbabbbbababbababbaabbababaabbabbabbbbbbbabbaaabaababbaab\n" + "bababbbaaababbbabbaaabababbbaaba\n"
            + "abbababaaaababbbaaabbabbbbababbbbbababab\n" + "bababaaabbbbbabaabaaabbabbbabbbabbbbabbabaabbbbaaabbbbab\n"
            + "baaababbaaabbbaababbbbbaaaaabbbbbabbbabb\n" + "bbbbbbabbaaabbaababaaaaaababaaba\n" + "aabbaaababbbbaabbbaababb\n"
            + "aaabbabbaabaaabbaaaaaaba\n" + "ababbaaaabbaabbabaabbbab\n" + "aaaabbababaabbabbabaababbbaaaaabbbabbabbbbaababb\n"
            + "bbaaabaabbaaabbabbabbbaaabbbabbabaaaabaa\n" + "aaabaaabbaaaabaaaabaabbb\n"
            + "ababbbbaabababaaabbaabbaabaabbbaabbbaababbbbbbbaaabbabab\n" + "bbabbbaaabababaabbbbbbabbabbbbbbabbbbaba\n"
            + "abbabaabbbbaaaabababbaab\n" + "aaabbbaabbaaabbabbbbababaabaabbaabaabbaa\n" + "baaaababbbbbbbabaababbbaaaaabbbb\n"
            + "baababababbabbbbaaaabbbb\n" + "bbbbbaaaaaaabbabaaaabbba\n" + "bbbbabbababababbbabbbbaabababbbaabaaabbb\n"
            + "bbaaabbbabbbaaabbbbbbaab\n" + "bbababbabababbbbbbababbbaabbaaabbabaabaaaabaaaabaabbaaaa\n"
            + "abbabaabbabbaabbbabbaaabbbbbbaaaabaababbaaabaaaababbaabb\n"
            + "babbbbbbbbbbaaabaabbbbabaabbabbababbbabaabababba\n" + "bbbabbbabbaaabaaabbaabbabbbaaaba\n"
            + "abbabbaaaabaabbaababbbbaaababbabbbaabbba\n" + "abbabbababababbaabbbbaba\n" + "babbbaabaaabbbaaabaabbab\n"
            + "aaaaababaaababbaaabbbbaabaabbbaabaaababbbbaabbbabbbabbaaabaaabbb\n" + "abaaabaabbaaabbaabbaabaabbbbaaab\n"
            + "abaabaaabbabbbaaaababaabbababbbbabaabbab\n" + "abaaaabbaaaabbabaaaababb\n" + "aaabbaabbbaabbbbaabaabab\n"
            + "bbabaabaabaaaabababbaaaa\n" + "ababbbaaababbbaaaabaabbaaabaaabbaaabbaabbaabbbbabbbbaabbabbaaaaa\n"
            + "bababbbbababababbbaaaabaabbbbabb\n" + "abaaaaaabaaaabaaababbaaaaaabbbbaabbbaaab\n" + "abababbbbabaaabbbaaaaabb\n"
            + "bbbbbbaababbbaaabbbaabaababbbbabbbababbabbabbabbbbbaaaaa\n"
            + "bbababbbbaaababbaaabbbaabbbbaaaababbbbaaaaaabaabaabaabbbaababbaa\n" + "abaaabbabbaabaabaabbaaaa\n"
            + "abaababbbbaaaaabbbbaabbababababa\n" + "baabbbaaabaabbbabbaabbab\n" + "babbbbbaabbabbbbbabbaabbaabbbbbaaabbaaaa\n"
            + "babababbbbbabbbabbbbbbbb\n" + "ababbbabaabaaabaabaabbab\n" + "babbababbaaabbbbaaaabbaa\n"
            + "abbabbbbbbabbbabaaabbaba\n" + "aaabbbaababbbaaabaabbbaabbbaaaabaababbaa\n"
            + "aabaaaababaaabaaaababbbabbbabababbbaabbb\n"
            + "baaaabbbaabbabaaababbababbaaabbbaaaabababaaaabbbbabbaabbaabbaabbabbababb\n"
            + "aaabbbaaaaabbabbababbbbabababbbababababa\n" + "bbaabaabbbababbaaabbbabb\n" + "ababababbbabaabbababbbbaabbbbbaa\n"
            + "abbaaaabaaaababbabbababb\n" + "abbbababababbbaaabaaababaaaaaaabaaabbabbabbbaaaabaabbaaabbbaabab\n"
            + "aababababaaaababbabbabbb\n" + "babbbbabbabbbbabbbaaabbbaaabaababbaabaabbbbaaabaabbaababaaaaaabb\n"
            + "aaabbbaabaaabbbabbbbabbb\n" + "ababaabbaaabaaabbbbabbabaabbabab\n" + "ababbabbabaababaababbbbaabbbabaa\n"
            + "abbbbaababbabbababbaaabb\n" + "ababbbababbababaabbaabaabbabbbaabbababaa\n" + "abbbaaabbbaabaaabbbbbaabbaaaaabb\n"
            + "abbbabbababaaabaaabbbabb\n" + "bbbaabaabaabbbbabbbabaabbbababbaaaaaabbbabbabbbbbaaabbabaaabbbab\n"
            + "baabbbaabbbbbbababaaababaaaaabaaaabbbaabbaaaaababbbabbaabaaaaabb\n"
            + "aaaaaaabbaababaabbabbbabbababbbababbabaaabbbbabaabaabbbb\n" + "bababbaaaabaaabbbabaabbaabababbaaaaababa\n"
            + "abbabbbbaabababababbaaaa\n" + "aababbbabbaaaabababaabbb\n" + "bbbbbabbbaababababaaaaaa\n"
            + "babbababaabaabbbaabaabbabbababbbbaabaaabaaabbabbabaabbab\n" + "bababaaaabbbabbaaaaababb\n"
            + "aabaabaabbabbbbababababbabbabababaababba\n" + "ababbbabbabbababbbbbaaba\n" + "abaabababbaaabaaaaabaabb\n"
            + "abbabbabbbbbbaaabaabbbaaaababbbabbbaaaaa\n" + "abaaaabbbabbababbaaaaaba\n"
            + "bbabbaaababbbbabaaababaaabaaabbbabaababbaaabaabbaaabbbbb\n" + "aaabaabaababbbabaaabbbaaaaabbaaa\n"
            + "babbbbbaaaabbbaaaaabbbaabaabbabaababbbaa\n" + "babbbbabbbbbababaabaabaababbbababbbaaabb\n"
            + "baabbabaabaabbbaababbabbaaababbbbbbabbaabaaabbaa\n" + "abbabbaaaaabbbbabbabababaababbabbbbabbaa\n"
            + "aaaababababbbaaaaaababbbaaabaabaaabaabbabbaaabaa\n" + "baabaabbabbaabaabbbabbbabbaaabababaabbab\n"
            + "aabbaabbaabbbabaabbbbbaa\n" + "abbabbbbaaaabbabaabaaababbbabbbb\n" + "bbabbbbabbbaabaabbaaaabaaabbaaaa\n"
            + "abaaaababaaaabbabbbaaabababababaabaababb\n" + "babbbbbabbabababbbbbaabbbbababaaabbbabaaabbbaababbaaaaba\n"
            + "abbbabbabbbaabaaabaabbbb\n" + "babbbaabbababbaabbaaabba\n"
            + "bbaaabbaaabbaaabbbbabbbabaaaabbbaabbbbaaababaaabbbaaabbb\n" + "aaaaabbbabaabaaaabababbbabbbaabb\n"
            + "bbabbbabbababbbaabababaabbababbabbbababababaaaaaaaaabaababbaaabb\n"
            + "ababbabbbaaabbbbbabaabaabaabbbaababbabbb\n" + "bbbbbbaabbaaaabaabbabaabbbbaaaababbaababaaaabbbbbbaabbba\n"
            + "babaabbabaabbbbbababbbabababbbaabbbaaaaa\n" + "ababaaaaabbbbaabbaababbb\n" + "bbaaabbbbbbaabaaaabbbbba\n"
            + "bbbbbabbaaaaabbbababbbbaabaabababbabaaaaababbaaabbbaaababaaaabbaabbbbbab\n"
            + "babbaabbbababbbbbabaabbaaabaaabbbaabaaab\n" + "aabbbaaabaaababbaaaaaaababbbabaa\n" + "ababaaabaaabbbaabaabbaaa\n"
            + "bbbbbbaaaaabaaaaaababababbbababababababa\n" + "aaabaabaabaaabbaaaaaaaaa\n"
            + "babaabaaaabbaaabaaabbbaabbbbbbababbaabbb\n" + "abbabaabaabbaaabbbbbaaba\n" + "aabaaaababbabbbbabaaabababaabaab\n"
            + "bbabbbabbababbaabababbbaaaaaabba\n" + "aaababbabbaaabbaaaaaabbbbabbabbaabaabbbabbaabbabbbbbbaab\n"
            + "aaaabaaabbaaabbaaaaabaaaababbbaabbbbbabbbaabababbbabbaaaaaabaabbaabababb\n"
            + "bbbabaabbaabababaaaabbabbaaababbbbabbabb\n" + "babbbbaabaabaabbbbabbbaaabbaaaababbbbabb\n"
            + "ababaaaabbbbbabaabaababb\n" + "bbaabaabbbbabaaaababbaab\n" + "aabaaaabaababaabbaaabbaa\n"
            + "aaabaaaabbbbbabbaababababbbbaabb\n" + "babbbaaaabababaaababbaaabbaaabaaaabaabababaabaab\n"
            + "bababbbbaaabaaaaabbbaaabaabaabbb\n" + "abbbabbabaabaabbaaaaabba\n"
            + "baaababbbbaaabaabbabaabbabababbabababababbbbababaaabaaaa\n" + "bbabbbbababbbbbbbabbbaba\n"
            + "bbbbbabbbbababbbbbbbaaba\n" + "babbaabbbbabbbabbbbababb\n"
            + "bbaabaaabbbbbababababababbaabbaaabaaaabbbabbbaababaabbbbaaababab\n" + "ababbbabbbabbbbabaabbaab\n"
            + "bbaabbbbbababbaaabbaabab\n"
            + "aaabbaaaaaabbabbbabbbbbbabaabbbaaaaaaaaabbaabababbababaabbaababbabaaaabbaaaabaabbbbbbaab\n"
            + "abaaabbbbaaabbaababbbabbabbabbba\n" + "abaabbbababbbbabaababbab\n" + "abaaaabaabbabbaaaabaabab\n"
            + "aaabaabaaaabbbbbaabaabaaaaabbbaababbababbbbbbaab\n" + "ababaabbabaaabbaababbaba\n"
            + "baabbabbaaabbaabbbbaaaababbbbabaaababbbb\n" + "baaaababaaabbbbaaabbaaba\n"
            + "bbabbaababaaabbabbbaaaabbbaabbbababaabbb\n" + "aabaabaababababbaaabbbab\n" + "aababaabbbabaaaaaabbbbab\n"
            + "babbbbbaababbbaaaaaaabba\n" + "bbababbababbbaabbbabbabb\n" + "bbbaaaabbbababbabbaaababbabaaaba\n"
            + "bbababbbbbbbababaababaaa\n" + "babaaabbababaaaabababbaabbbaabab\n" + "ababaaaaabaaaabbababbabbbaaaaababaaaaabb\n"
            + "bbbbabbabbbbbabbbaaaaaaa\n" + "ababbbabbbaabaabbaaabaaa\n" + "baabbbaaaabbbbaaabbbbabb\n"
            + "bbaaabaabaaabbbbabababbbaabababababbaabbbbbababbabaaabbb\n" + "abbbaaababbbbbbbbabaabbb\n"
            + "ababaabbbbbbbbababbbbabbaabaaaabaaabbaabaaabbababbababab\n" + "abbaabaaaabbbbaaaabbabbb\n"
            + "bbaaabbbbbbbbbaaabbabbbbbabbbbaabaaaabbabbbaaabb\n" + "bbbabababbbbbbabbaabaabbbabbaabbbaabaabbabaabaab\n"
            + "bbbaaaabbaaababbabbabbbbabaabbaaabbbabbb\n" + "abbaabbabaabbaaababababaabbbaaba\n" + "babbbbbababbbaaababababa\n"
            + "abbbbaabaaaaababbabaaabbbaaabaab\n" + "aaabbabbbbbabaababbaaaaa\n" + "aaabbbbaabbaabbaaaaaaabb\n"
            + "babbaabbbbaaabababaaaabaaababbbaabbbaababbbabbaabaaaabbabaabaaabbbbaaaba\n"
            + "aababaabbabbbbaabbbababababbaaaa\n" + "abababaabbaaaabaababbbbb\n" + "bbbbababbbabbbbabaaababa\n"
            + "bbababbbaababababbbaabaaabababaabaaabbbbbaaaaaab\n"
            + "baabbbabbbaabbbaabbababbbbbaaabbbabbaaabbabbaabaabbbbbabababbbab\n" + "bbbbbabbbabbbbbbaabaabbb\n"
            + "abbabaabababaabbababbaba\n" + "abaaabaaabbabbbbababbaaaaaaaaabbaabbbabb\n" + "bbabbaabbbbbabbababbaaba\n"
            + "bbababbaaabababaaaaababa\n" + "baabbbaababaaabbabbbabbaaabbbabbabaaabbb\n" + "aaabbabbababbaaabbbabaababbaaabb\n"
            + "abbbbaabaabaabbababbbaabaaaaaaabaabbaabbabbabbba\n" + "babbabababaaaabbbabbaabbbbaaabbaabaababb\n"
            + "abbabbaabbbabaaaaabbbaab\n" + "babbaabbbbbbababaaababab\n" + "aaabaaaaaabbbbbbbaabababaaaaaaba\n"
            + "abbabbaababbababaabbbabb\n" + "abababaabbbabababbaaaabb\n" + "aaaaabbbbbbbbabaabbaaaab\n"
            + "bbabaababababaaabbaaabaababababa\n" + "aababaabbbaabbaababbbbbaaabbbbbaaabbabab\n"
            + "ababaabbbababbabbaaabaaaabaabaabaabbbbabbbbbbbbbabaabbaa\n"
            + "abbababaabaaababbaabbbaababbaabbbabbbabbbaababbb\n" + "bbababbabababbaaababbabbaabbaaababbaabab\n"
            + "aabbbaaababbaaabbbbaaaaa\n" + "babbbbaabbbabaababbbbaabbbaaaaaabbababbbaaababbabaaaaaba\n"
            + "bababaaaaabaabbaaabbaaaa\n" + "babbababbabbaabbbaabbbbbbbaaababbbabaababbabaaaabbbbaaabaaaabaab\n"
            + "aaaabaaaaaaabaaababbbaababbbbaba\n" + "baaaaabaaabbaabaaaaabaabaababbbb\n"
            + "bbbbbabaababbbababaabbbaabaabbbbbabbaaba\n" + "babbbbbbaaaaababbbbbababaabaaabbbaaababbaabbabbb\n"
            + "abaaaabaabbbbbbbbabaaaab\n" + "bababbbabbaaaaaaaaababab\n" + "abbabbabbbababbabbabbaabbaabaaaabbaababbbabaaaba\n"
            + "bbbbababbabababbabbabbabbbaaabaabbabbabb\n" + "aabaaabaabbabbabbaaaabaabbbbaaaabbaaabbbaaaaaabb\n"
            + "bbabbbabaabbbaaaababbbbabbbbbbbb\n" + "bbaaabbbbabbbbbaabbbaaaa\n" + "bbbbbabaaabaabbaaabbbbba\n"
            + "aaabbbbbbbbaabaaabbbbabb\n" + "baabbabbbaabbbbaaabaaabbababababbaaababa\n" + "aaaabaaaaabbbbbbababababbbbbaaba\n"
            + "baabbbbabaaaababababbbbababbbbba\n" + "babbaabbaaabbbbbbbbaabab\n"
            + "abaaaabbbaabbabaabbbaaababbabaabbaaaabbbaaabbaaaababbaab\n"
            + "bbaabaabbaabbbaabaabbabbbbbaabaabbbbaaabbbbaaaba\n" + "abbbababbbbabaaaaaaabbba\n" + "aabbbbbbbabbbbaaabaaabbb\n"
            + "bbaaaaaabbbbabbabaabbaaa\n" + "baaaabababaaabaaabaabaaabababaab\n" + "abaaabaaabbbababbbbababb\n"
            + "bbbabaaabaabaaaabbabbbbabbababbbbbbbaabaaabbbabb\n"
            + "bbaaaaabbbbaabbbbbabbabbbbababbaaaaaaabbbbbbababbaaabbbabbaabaabbaaaaaabbbbbaaba\n"
            + "abbaabbabaabbababaaaabababbaaabbabaaaaab\n" + "abbaabaaabaabbbabbabaaab\n"
            + "baabbbaabbbbbaabababaaaababbaabbabaaaaaaabbbbabbbaabaaaaabababbb\n"
            + "aabbaaabbbaaaabbaaaabbabaabbbaaaaabaababbbababab\n" + "baabbbbaabaaabaaabbaabbbbbaababababbaaba\n"
            + "abaabaaaababaaaaabaabaab\n" + "bbbbabbabbabaaaabaabbabababbbaababbbaabaabbbaaaa\n"
            + "abaaababaabaaabbbbbbbaaababaaaaa\n" + "ababbaaabbbbbabbaabaaaababaaabbb\n"
            + "aababbbbbababbaaaabbbabbabbbabababbbbbababaabaabaabbbaabaaabbbbb\n"
            + "babbbbbbaabaaabaabbababaabbbabbaababbbabababaabbbbbaaaba\n" + "bbbabaabbbaaaabbbbbababb\n"
            + "abbbabbaabaababaabbbbbbbbbababaa\n" + "aaaaabbbabbbbaabaaaabbaa\n"
            + "bababbbaabbabbbbaaabbbbbaabbabababbabbababaabbbbababbbbb\n"
            + "bbababbbabbabbbbbbabbbababaaaababbbbaaaabbbbbabbbbbbaabb\n" + "abbbababaabbaabbbbbaabab\n"
            + "aaababbabbbaabaaaaabbbbbabbbabbaabbababaabbbbbba\n" + "aaabbabbaababaabbababbbabbaabbbbabbbbbbbbaaabaaa\n"
            + "bbbbabbabbaabaabbaaabbbbbaabbabbbaababbbabaaaaaa\n"
            + "baabaabbbababbbbabaaabbbabaabaababbabbaaaabbbabaaaababbbbaaaabaa\n"
            + "bbabbaabbabbbbabababbbaabbabbaaabbbaabbb\n" + "ababaabbababbbbabbbabbbb\n" + "abababbabababbbbbbbababb\n"
            + "bababbbbbbbbbbaaaabaabbb\n" + "babbbbaabaabbabbbbbabbbb\n" + "baabbbbbbabbbbbbbbababab\n"
            + "bbbbabbababbbbbaaabbbbaaabbbbbaa\n"
            + "abababbabbbaaaaaaaabbbababababaaabbbababaaababbababbabbaaaabbaabaabbaababbbbaaba\n"
            + "abbaabaababbbbbbbaaaabba\n" + "abaabababbababbbabbbaaaa\n" + "bbbbbababbaaaabbaabbaaba\n"
            + "aaabbaabbaabbbaabbababaa\n" + "bbaaaabbabbabbbbaaababaa\n" + "bbbabaababaabbbbaababaaa\n"
            + "bbaaabbbaabaaababbbabbabbbbbbbbb\n" + "abbaabababbbbbbbbababbaabbbbabbbbabbbabaaabaabaa\n"
            + "bbbbbaaaaaaaababaaaaababbaababaabbbbaaabbababbab\n" + "bbaabbbbaabbbbaaabaaabaaaaabbabbaaaaabba\n"
            + "baabbbbabaabbabababbabaa\n" + "aabaabaabaaaabaabaabbbaaaaabaaababbbbbbbbbaabaab\n"
            + "ababbbaaaaabbbbbaabbbbbbbbbabbaaababbaab\n" + "babaabbaaaaabaaababbbbbababbbaabbababaab\n"
            + "baabababababbbaababababbbbbaabab\n"
            + "abaabababaabbabaaaaaaabaababbbbaaaaaaabaabbbbabaaaaaabbababbabbababaabbababbbaaa\n"
            + "babababbaaabaabababababa\n" + "aaababbbaababababbbbaaaaaaababbaabbbbbabaabaabbb\n"
            + "aababababbaabbbbbabbaabbbbbaaaaa\n" + "abaaabbaabbababababbaaababaabbab\n"
            + "aaabbaabbabbbbaaaabaabaabaaabbbbabbabbaabaabaaababbaaaab\n"
            + "bababbbbaaabbabbabaaaabbaaaabbbaabbabbbaaaabaabb\n" + "bbbabaaaaaabaaabaabbaabbbaaabaaa\n"
            + "babaabbabbbbababbbabaaaaabbaaabbaaaaaaaa\n" + "abbbbbbbbbaabbbbbbbaabab\n" + "ababbabbbbaaabbaaaaababb\n"
            + "abbbababbbaaaabbabababaaababaaabbaabaaabaaababaabaababbb\n" + "babbbbbbbababbaababababa\n"
            + "babaaabbbaaaabaaaaaaaaba\n" + "bbbabbbaaaababbbaaaababb\n" + "aaabaaabbbabaaaababbbaba\n"
            + "baabbbaaaaaabaaaaabababaaaabaabababaabbb\n" + "abbabbbbbaababaabaabbbab\n"
            + "bbbababbbbbabbabaababbbaabaabbbababbbbbbbbababbabbaabbabaaabaabaabaabaababaaaababbbaabaaaaaabbbb\n"
            + "baaabbbabababbbbbabbaaaaaaababaa\n" + "bbbaabaaaaababbbabbbabbaaaaaaabababaaaab\n" + "ababbbaabbbaabaaaaaaaaaa\n"
            + "baabaaaaababaabbabbbbbaa\n" + "aabbbaaaaaabbaabaabbbaaaabaabbaa\n" + "baababaaaaaabbabbbbabaababaabaab\n"
            + "abbbbabbaaabbabaabbbbabbbababbab\n" + "bbaaabbbabbabbaabbbaaabb\n" + "bbaaababaabaaaabaaabbbab\n"
            + "aaababbbaaaabbabbaaaabba\n" + "bbbbbbbbbbabaaaababbbbabaaabbabbaaaaabaabbabbbaabaabaabb\n"
            + "abbbababbbbbbabbabbbbaaa\n" + "aaaabaaaaabbaabbbaabbabbaaabaababbaabaabbabaaaaa\n"
            + "babababbaabbaabbbbbabaababbbbaabbbababab\n" + "abaaabaaabbbbbbbaababababbaabbbabaaababa\n"
            + "bbaabbbbabbabbabbbaaabbabbaaaaaaaabaaabbbbababaaabaaaabbbabbababbbbbaabbbbaababb\n"
            + "baabbbbbabbbabababbbababbbaaababbbabaabbbbaaabaaaaabbabaaaaaaabb\n"
            + "ababbbaabaabbabbaaabbbbbbababaaababaaaba\n" + "aaaaababbbaabbababbaabbbbbabaaabbaaabbab\n"
            + "aabbaabbaaaabaaaaabbbbaaabaabaaababaabbabaabbaabababbbbb\n" + "aaabbbbaaaaabaaabbbbabbabbbaababbbaabaaa\n"
            + "bbbbbbabbaabaaaabbbaaaaa\n" + "babbbaaabaaaabaabbbbbaabbbbbaabb\n" + "bbaabbbbbaabaaaaabbbaaaa\n"
            + "bbababbaaababbbaababbaaaaaaaabba\n" + "abababaabaabbbaababababbbaaaaaba\n" + "ababaaabbbaabbbbbabbaaba\n"
            + "abaaabbaaabbbbbbbabaabbb\n" + "aabbbbaabbaaaabbbabbbbbbaabaaababaabaababaaabbab\n" + "ababaaabababbbbaaabbbbab\n"
            + "abbbababbbbabaabbbbaabbb\n" + "aabbabaabaabbabbbbbbababababababaabbbabb\n" + "bababbaaaaabaabaaabbbbaaaabbabab\n"
            + "bbabaaaaaababbbaabbaaaaa\n" + "bababaaabbbbaaaaababaaaababbbabaabbaaaab\n" + "bbbbbaaababbbbbbabbbbbba\n"
            + "baabaabbbaaaabbabbbaabbaababbaab\n" + "aabbbbbbaaababbaabaaababababaaabbbbaabbaaaaaaabaaaababaa\n"
            + "bbbbbabbabbabaabbaaabbbbabaababaaababbabbabbabaa\n" + "aaabbabbabababaabaabaaba\n" + "abaaabbabbabbbbabaababbb\n"
            + "baaabbbbbbabaaaaaaaabbbb\n" + "baabaaaabaabbabaababbbabbabbabaa\n" + "babbababbbaabaabbbbbbabbaababbbb\n"
            + "aaabbbaabaabbbbbabbbbbab\n" + "baababbaabbbbbaaaabaabab\n" + "bbabbbababbaabaaaaaabaaaabaababababbbaabbbabbaba\n"
            + "ababbbbabaabbabaababbaab\n" + "ababbbaabbbbbabaaabbbaaaaabbbbbabbaabbba\n" + "babbaaababaabababbbbabaa\n"
            + "baabaabbbbbbabbabbaabbaaabaabbab\n" + "aabbaaabbbaaabababbaabaaabbbabababbaaabbbabbbabb\n"
            + "bababbbababbbaaabbbbbababbaabbaabbbbaabb\n"
            + "baabbbbbbaaaabaaabbbaaabbabbbbaaababbabbaaabaaaaaaaaabaaabbabaaaaabbbaabbbabbbbb\n"
            + "babababbabbabbbbbabbbbab\n" + "baaaababbbbabbabbbabaabaaabbbbba\n"
            + "babbbbabaabbaaababaaababaaabbabbbabaaababaaaaaaa\n" + "aabaaabaabbabbaaabbbbbaa\n" + "bababaaabaababaababaaaaa\n"
            + "ababbbbabbabaabaababaaaabaaaaabb\n" + "bbababbaabaabbbabbabbabb\n" + "baaaababbbbaabbbbbbbabaaababbaba\n"
            + "aaabaaabbabaabbaababbbabbaaabbaa\n" + "babaabbabbbbabbaabbabababbbbbaababbbabbb\n"
            + "bababbaababbbbaabbbabaaaababbababbbbabbb\n"
            + "bbbbbababababbaaabbabbaabbabbaabbaaabbbabaaabababaaabababbbababb\n" + "aabbbbbbbababaaaabaaaabbbbbaabba";
}
