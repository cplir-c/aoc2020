package cplir_c.advent_of_code_2020;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class Day7 {

    public static void main(String[] args) {
        countShinyGoldHolders(INPUT);
        countShinyGoldHolders(EXAMPLE);
    }

    private static final String  SHINY_GOLD   = "shiny gold";
    private static final Pattern RULE_SPLIT   = Pattern.compile("\\.\n?");
    private static final Pattern BAGS_CONTAIN = Pattern.compile(" bags contain ");
    private static final Pattern COMMA_SPLIT  = Pattern.compile(" bag(?:s?)(?:, )?");
    private static final Pattern SPACE_SPLIT  = Pattern.compile("\\s+");
    private static void countShinyGoldHolders(String input) {
        Object2ObjectMap<String, ObjectList<String>> fitsIn   = new Object2ObjectOpenHashMap<>();
        Object2ObjectMap<String, Object2IntMap<String>> contained = new Object2ObjectOpenHashMap<>();
        parseRules(input, contained, fitsIn);
        var counted = countShinyGoldHolders(fitsIn);
        System.out.println("Counted " + counted + " bags that can hold a shiny gold bag somewhere.");
        counted = countShinyGoldContained(contained);
        System.out.println("Counted " + counted + " bags that fit somewhere inside a single shiny gold bag.");
    }
    private static int countShinyGoldContained(Object2ObjectMap<String, Object2IntMap<String>> contained) {
        Object2IntMap<String> fullyContained = new Object2IntOpenHashMap<>();
        ObjectList<String>    toCalculate    = new ObjectArrayList<>();
        toCalculate.add(SHINY_GOLD);
        System.out.println(contained);
        while (!toCalculate.isEmpty()) {
            var toColor = toCalculate.get(toCalculate.size() - 1);

            while (fullyContained.containsKey(toColor)) {
                toCalculate.remove(toCalculate.size() - 1);
                if (toCalculate.isEmpty()) {
                    toColor = "";
                    break;
                } else {
                    toColor = toCalculate.get(toCalculate.size() - 1);
                }
            }
            var toCount       = 1;
            var fromContained = contained.get(toColor);
            if (fromContained == null) {
                fullyContained.put(toColor, 1);
                continue;
            }
            var entryIterator = fromContained.object2IntEntrySet().iterator();
            while (entryIterator.hasNext()) {
                var entry            = entryIterator.next();
                var fromColor        = entry.getKey();
                var fromMultiplicity = entry.getIntValue();
                var fromCount        = fullyContained.getOrDefault(fromColor, -1);
                if (fromCount == -1) {
                    toCalculate.add(fromColor);
                    toCount = -1;
                    break;
                } else {
                    toCount += fromMultiplicity * fromCount;
                    System.out.println(
                        toColor + " bags contain " + fromMultiplicity + " " + fromColor + " bags, each of which contain "
                                + (fromCount - 1) + " other bags"
                    );
                }
            }
            if (toCount >= 0) {
                fullyContained.put(toColor, toCount);
            } else {
                while (entryIterator.hasNext()) {
                    var entry     = entryIterator.next();
                    var fromColor = entry.getKey();
                    if (!fullyContained.containsKey(fromColor)) {
                        toCalculate.add(fromColor);
                    }
                }
            }
        }
        System.out.println(fullyContained);
        return fullyContained.getInt(SHINY_GOLD) - 1;
    }
    private static int countShinyGoldHolders(Object2ObjectMap<String, ObjectList<String>> fitsIn) {
        var shinyGoldContainers = fitsIn.get(SHINY_GOLD);
        if (shinyGoldContainers == null) {
            return 0;
        }
        var newShinyGoldContainerList = new ObjectArrayList<>(shinyGoldContainers);
        var newShinyGoldContainerSet  = new ObjectOpenHashSet<>(newShinyGoldContainerList);
        shinyGoldContainers = new ObjectArrayList<>();
        shinyGoldContainers.add(SHINY_GOLD);
        while (!newShinyGoldContainerList.isEmpty()) {
            var goldContainer = newShinyGoldContainerList.pop();
            newShinyGoldContainerSet.remove(goldContainer);
            shinyGoldContainers.add(goldContainer);
            var newGoldContainers = fitsIn.get(goldContainer);
            if (newGoldContainers == null) {
                continue;
            }
            for (var newGoldContainer : newGoldContainers) {
                if (!newShinyGoldContainerSet.contains(newGoldContainer) && !shinyGoldContainers.contains(newGoldContainer)) {
                    newShinyGoldContainerSet.add(newGoldContainer);
                    newShinyGoldContainerList.add(newGoldContainer);
                }
            }
        }
        return shinyGoldContainers.size() - 1;
    }
    private static void parseRules(String input, Object2ObjectMap<String, Object2IntMap<String>> contained,
                                   Object2ObjectMap<String, ObjectList<String>> insideOf) {
        // System.out.println(RULE_SPLIT);
        var rules = RULE_SPLIT.split(input);
        for (String ruleString : rules) {
            // System.out.println(ruleString);
            var rule            = BAGS_CONTAIN.split(ruleString);
            var container       = rule[0];
            var containedString = rule[1];
            if ("no other bags".equals(containedString)) {
                continue;
            }

            var               containedStringList = COMMA_SPLIT.split(containedString);
            Object2IntMap<String> containedBags       = new Object2IntOpenHashMap<>();
            // System.out.println(Arrays.toString(containedStringList));
            for (String bagString : containedStringList) {
                // color = color.trim();
                var bag = SPACE_SPLIT.split(bagString, 2);
                // System.out.println(Arrays.toString(bag));
                var color = bag[1];
                if (color.isEmpty()) {
                    continue;
                }
                var countString = bag[0];
                var count       = Integer.parseInt(countString);
                containedBags.put(color, count);
                insideOf.computeIfAbsent(color, ignoredString -> new ObjectArrayList<>()).add(container);
            }
            contained.put(container, containedBags);
        }
    }

    private static final String EXAMPLE = "light red bags contain 1 bright white bag, 2 muted yellow bags.\n"
            + "dark orange bags contain 3 bright white bags, 4 muted yellow bags.\n"
            + "bright white bags contain 1 shiny gold bag.\n"
            + "muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.\n"
            + "shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.\n"
            + "dark olive bags contain 3 faded blue bags, 4 dotted black bags.\n"
            + "vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.\n" + "faded blue bags contain no other bags.\n"
            + "dotted black bags contain no other bags.";
    static final String         INPUT
                                        = "dull silver bags contain 2 striped magenta bags, 2 dark coral bags, 1 bright orange bag, 4 plaid blue bags.\n"
            + "dark plum bags contain 3 wavy teal bags.\n"
            + "wavy turquoise bags contain 3 bright salmon bags.\n"
            + "mirrored gold bags contain 3 wavy brown bags, 5 posh beige bags, 3 light crimson bags, 3 vibrant salmon bags.\n"
            + "drab green bags contain 4 dull white bags, 1 posh indigo bag.\n"
            + "faded lime bags contain 1 dim magenta bag, 1 wavy salmon bag, 4 dull purple bags.\n"
            + "mirrored blue bags contain 5 bright orange bags, 1 muted black bag, 2 muted brown bags, 2 vibrant gold bags.\n"
            + "faded crimson bags contain 4 wavy teal bags, 4 mirrored fuchsia bags, 3 plaid white bags.\n"
            + "faded magenta bags contain 2 clear orange bags, 5 dull green bags, 2 pale white bags.\n"
            + "pale red bags contain 5 shiny gold bags, 4 dull gold bags, 2 drab black bags.\n"
            + "dark coral bags contain 1 light turquoise bag.\n"
            + "faded chartreuse bags contain 4 shiny brown bags, 4 mirrored beige bags, 4 clear purple bags.\n"
            + "muted coral bags contain 4 pale coral bags, 4 plaid brown bags.\n"
            + "bright teal bags contain 5 striped blue bags, 4 faded orange bags, 2 faded crimson bags.\n"
            + "wavy green bags contain 5 dim chartreuse bags.\n"
            + "clear white bags contain 2 mirrored fuchsia bags.\n"
            + "clear aqua bags contain 1 faded beige bag.\n"
            + "vibrant yellow bags contain 5 posh brown bags.\n"
            + "pale lavender bags contain 1 striped beige bag, 2 striped cyan bags.\n"
            + "mirrored lime bags contain 3 bright orange bags.\n"
            + "faded tan bags contain 2 drab beige bags.\n"
            + "dark indigo bags contain 1 dark brown bag, 5 shiny beige bags, 1 vibrant indigo bag.\n"
            + "drab teal bags contain 2 vibrant fuchsia bags, 3 muted green bags, 5 dotted magenta bags, 2 shiny lavender bags.\n"
            + "shiny aqua bags contain 1 shiny gold bag, 4 clear white bags, 4 faded gold bags.\n"
            + "dull bronze bags contain 4 vibrant teal bags, 1 vibrant violet bag.\n"
            + "dark aqua bags contain 4 posh white bags.\n"
            + "dim coral bags contain 2 light yellow bags.\n"
            + "faded salmon bags contain 5 muted brown bags, 2 dotted red bags, 3 drab yellow bags, 4 dark red bags.\n"
            + "bright lavender bags contain 5 wavy maroon bags, 5 light brown bags, 5 bright silver bags, 1 dark gray bag.\n"
            + "mirrored cyan bags contain 4 dotted cyan bags, 5 striped orange bags, 1 vibrant gold bag.\n"
            + "drab aqua bags contain 3 striped black bags, 4 dark salmon bags, 1 drab white bag, 4 faded crimson bags.\n"
            + "striped purple bags contain 5 faded yellow bags, 2 faded brown bags.\n"
            + "drab fuchsia bags contain 4 vibrant violet bags, 5 mirrored yellow bags.\n"
            + "shiny red bags contain 3 faded cyan bags, 1 dull beige bag, 1 shiny blue bag, 5 dull cyan bags.\n"
            + "mirrored teal bags contain 4 clear brown bags, 5 light bronze bags, 3 light teal bags, 2 pale tomato bags.\n"
            + "dotted orange bags contain 3 dull white bags, 2 wavy blue bags.\n"
            + "dotted lavender bags contain 1 vibrant aqua bag, 4 shiny magenta bags, 3 dull plum bags.\n"
            + "pale crimson bags contain 4 muted cyan bags, 1 posh brown bag, 3 light magenta bags.\n"
            + "shiny black bags contain 4 vibrant chartreuse bags, 1 mirrored yellow bag, 3 posh brown bags, 5 vibrant violet bags.\n"
            + "clear bronze bags contain 5 dull violet bags, 3 pale plum bags.\n"
            + "striped lavender bags contain 1 dark plum bag, 2 striped yellow bags.\n"
            + "plaid indigo bags contain 2 plaid chartreuse bags.\n"
            + "shiny teal bags contain 4 wavy gray bags, 4 drab teal bags, 1 dark silver bag.\n"
            + "dull turquoise bags contain 1 wavy gray bag.\n"
            + "striped brown bags contain 1 striped olive bag, 1 wavy olive bag, 5 posh brown bags.\n"
            + "dotted magenta bags contain 4 drab silver bags, 3 light olive bags, 1 bright tan bag, 4 dull gold bags.\n"
            + "plaid yellow bags contain 5 drab black bags, 1 wavy lavender bag, 1 drab silver bag.\n"
            + "muted blue bags contain 5 posh aqua bags.\n"
            + "shiny olive bags contain 4 dark salmon bags, 1 faded gold bag, 3 drab chartreuse bags, 4 dotted yellow bags.\n"
            + "vibrant lime bags contain 4 shiny aqua bags, 1 bright maroon bag, 4 striped orange bags.\n"
            + "dim crimson bags contain 5 faded crimson bags.\n"
            + "vibrant gray bags contain 1 mirrored coral bag, 5 wavy beige bags, 3 drab turquoise bags.\n"
            + "posh chartreuse bags contain 3 light plum bags, 2 pale green bags, 5 drab white bags.\n"
            + "striped beige bags contain 5 dull red bags, 5 drab salmon bags, 3 vibrant salmon bags.\n"
            + "dotted tan bags contain 4 wavy crimson bags, 4 shiny orange bags, 1 drab turquoise bag.\n"
            + "vibrant aqua bags contain 1 vibrant gray bag, 5 light violet bags, 3 dim yellow bags.\n"
            + "faded turquoise bags contain 2 faded yellow bags, 4 mirrored coral bags.\n"
            + "mirrored purple bags contain 3 pale orange bags.\n"
            + "dim white bags contain 1 drab turquoise bag.\n"
            + "bright purple bags contain 5 muted chartreuse bags, 1 dotted yellow bag, 3 bright salmon bags.\n"
            + "drab red bags contain 1 mirrored magenta bag.\n"
            + "clear coral bags contain 4 drab black bags, 3 dark black bags.\n"
            + "mirrored orange bags contain 1 muted chartreuse bag.\n"
            + "wavy cyan bags contain 3 posh lime bags, 4 dark magenta bags, 4 vibrant turquoise bags.\n"
            + "pale magenta bags contain 4 vibrant turquoise bags, 3 clear gold bags.\n"
            + "posh gold bags contain 5 dotted lime bags, 5 wavy silver bags, 4 muted crimson bags, 1 dull yellow bag.\n"
            + "clear silver bags contain 1 drab indigo bag.\n"
            + "faded violet bags contain 2 mirrored bronze bags.\n"
            + "muted turquoise bags contain 2 plaid green bags, 2 light yellow bags, 4 dark violet bags.\n"
            + "striped bronze bags contain 4 striped white bags, 1 dim yellow bag, 5 clear aqua bags.\n"
            + "muted aqua bags contain 5 plaid green bags.\n"
            + "wavy teal bags contain no other bags.\n"
            + "pale black bags contain 5 dark salmon bags.\n"
            + "clear gold bags contain 2 plaid white bags, 5 drab coral bags, 5 pale coral bags.\n"
            + "muted chartreuse bags contain 5 faded crimson bags.\n"
            + "dotted fuchsia bags contain 1 plaid brown bag, 1 dark violet bag.\n"
            + "bright tomato bags contain 1 bright blue bag.\n"
            + "dim bronze bags contain 1 dotted green bag, 5 pale violet bags, 4 vibrant chartreuse bags, 3 striped yellow bags.\n"
            + "bright beige bags contain 1 drab blue bag.\n"
            + "vibrant olive bags contain 3 dotted olive bags.\n"
            + "clear tomato bags contain 1 light gray bag, 2 light turquoise bags, 2 striped yellow bags.\n"
            + "mirrored beige bags contain 3 light coral bags, 2 bright teal bags, 1 wavy magenta bag.\n"
            + "shiny gold bags contain 3 pale silver bags, 3 mirrored yellow bags, 2 shiny black bags, 2 light magenta bags.\n"
            + "plaid aqua bags contain 4 plaid crimson bags, 4 dim gray bags, 3 plaid orange bags, 2 dotted blue bags.\n"
            + "light green bags contain 2 light violet bags, 5 striped violet bags, 5 drab brown bags, 4 dull white bags.\n"
            + "vibrant beige bags contain 3 posh violet bags, 2 plaid blue bags, 4 shiny lavender bags, 5 wavy orange bags.\n"
            + "drab orange bags contain 3 striped beige bags, 3 posh teal bags, 5 drab silver bags, 1 dark indigo bag.\n"
            + "shiny orange bags contain 3 dark aqua bags, 4 clear beige bags, 2 mirrored lime bags, 3 dark violet bags.\n"
            + "wavy maroon bags contain 3 vibrant chartreuse bags.\n"
            + "wavy olive bags contain 5 dark aqua bags, 1 light yellow bag, 1 shiny crimson bag.\n"
            + "dotted cyan bags contain 2 drab gold bags.\n"
            + "muted cyan bags contain 1 clear gold bag, 4 dark plum bags, 2 wavy lavender bags, 5 vibrant indigo bags.\n"
            + "posh cyan bags contain 1 light fuchsia bag, 1 dark maroon bag.\n"
            + "faded bronze bags contain 2 muted salmon bags, 4 dim violet bags, 5 dark tan bags, 3 vibrant white bags.\n"
            + "pale green bags contain 4 muted turquoise bags, 1 vibrant green bag, 1 drab white bag.\n"
            + "clear brown bags contain 4 wavy teal bags, 4 drab violet bags.\n"
            + "striped salmon bags contain 5 mirrored orange bags, 1 shiny yellow bag, 1 muted beige bag, 1 clear purple bag.\n"
            + "posh brown bags contain 3 posh white bags, 4 drab chartreuse bags, 5 dark violet bags, 4 wavy teal bags.\n"
            + "mirrored green bags contain 1 dim tan bag.\n"
            + "bright yellow bags contain 2 striped indigo bags, 2 dark silver bags.\n"
            + "wavy yellow bags contain 3 dotted gold bags, 3 posh green bags.\n"
            + "light chartreuse bags contain 3 faded blue bags, 3 mirrored yellow bags, 3 shiny plum bags, 4 light red bags.\n"
            + "dark lime bags contain 5 vibrant chartreuse bags, 2 clear brown bags, 1 posh brown bag.\n"
            + "muted magenta bags contain 4 shiny silver bags, 2 dotted yellow bags, 4 pale fuchsia bags, 5 muted tan bags.\n"
            + "light plum bags contain 4 drab gold bags.\n"
            + "dim tomato bags contain 1 light silver bag.\n"
            + "pale lime bags contain 4 dull blue bags.\n"
            + "dim black bags contain 1 dark plum bag, 1 dull crimson bag, 5 wavy white bags, 2 plaid chartreuse bags.\n"
            + "muted teal bags contain 3 dim black bags, 4 mirrored lavender bags, 5 dull indigo bags, 3 clear red bags.\n"
            + "muted purple bags contain 1 mirrored red bag.\n"
            + "dull coral bags contain 5 pale teal bags, 2 faded cyan bags, 4 pale black bags, 2 muted olive bags.\n"
            + "vibrant red bags contain 4 light teal bags, 5 shiny fuchsia bags, 1 drab purple bag, 2 muted olive bags.\n"
            + "mirrored tomato bags contain 4 posh brown bags.\n"
            + "shiny coral bags contain 5 clear turquoise bags, 2 wavy salmon bags, 1 drab brown bag.\n"
            + "wavy indigo bags contain 1 vibrant brown bag, 2 dim turquoise bags, 1 posh violet bag, 1 plaid green bag.\n"
            + "dotted gold bags contain 5 pale aqua bags, 1 bright olive bag.\n"
            + "dotted violet bags contain 2 drab olive bags, 1 plaid cyan bag, 2 posh beige bags.\n"
            + "pale fuchsia bags contain 5 faded beige bags, 5 dark purple bags.\n"
            + "shiny chartreuse bags contain 1 striped tan bag, 5 pale tomato bags.\n"
            + "clear gray bags contain 1 bright fuchsia bag, 4 dotted olive bags, 2 light teal bags, 4 shiny magenta bags.\n"
            + "vibrant tomato bags contain 1 clear crimson bag, 3 pale purple bags, 3 faded gray bags.\n"
            + "light orange bags contain 5 plaid brown bags.\n"
            + "shiny tomato bags contain 3 light olive bags, 5 dim silver bags, 3 posh violet bags, 2 striped lavender bags.\n"
            + "faded green bags contain 2 dotted gold bags, 1 dark plum bag, 1 dull gray bag, 5 dark brown bags.\n"
            + "dim gray bags contain 5 muted white bags, 2 mirrored yellow bags, 1 muted tomato bag.\n"
            + "faded black bags contain 3 faded teal bags, 3 striped lavender bags, 2 striped blue bags, 4 muted lavender bags.\n"
            + "clear lime bags contain 3 mirrored yellow bags, 1 light yellow bag.\n"
            + "dark silver bags contain 4 wavy orange bags, 2 muted green bags.\n"
            + "plaid black bags contain 3 wavy indigo bags, 1 pale red bag.\n"
            + "mirrored black bags contain 5 dull black bags, 4 clear coral bags, 1 wavy olive bag, 4 dull silver bags.\n"
            + "light coral bags contain 5 drab black bags, 1 dark magenta bag, 1 drab teal bag, 1 mirrored crimson bag.\n"
            + "shiny yellow bags contain 5 faded indigo bags.\n"
            + "posh plum bags contain 3 faded maroon bags, 2 vibrant indigo bags, 1 bright turquoise bag.\n"
            + "faded olive bags contain 1 vibrant gray bag, 4 drab teal bags, 5 wavy teal bags.\n"
            + "dim plum bags contain 1 plaid white bag, 4 wavy beige bags, 3 wavy green bags.\n"
            + "muted tomato bags contain 5 dotted red bags, 1 drab purple bag, 1 light orange bag.\n"
            + "clear fuchsia bags contain 1 mirrored olive bag, 2 faded salmon bags.\n"
            + "striped violet bags contain 2 light olive bags, 1 plaid olive bag, 5 light white bags.\n"
            + "dim aqua bags contain 2 vibrant purple bags, 5 drab silver bags.\n"
            + "striped crimson bags contain 5 muted coral bags.\n"
            + "bright indigo bags contain 3 muted gold bags.\n"
            + "dotted black bags contain 4 shiny crimson bags, 5 dark salmon bags, 5 faded crimson bags, 2 vibrant magenta bags.\n"
            + "faded indigo bags contain 1 drab tomato bag.\n"
            + "bright bronze bags contain 1 wavy lime bag, 4 pale violet bags.\n"
            + "drab turquoise bags contain 2 drab gold bags, 2 vibrant gold bags, 4 pale tomato bags.\n"
            + "wavy black bags contain 2 dotted brown bags, 1 light salmon bag.\n"
            + "posh green bags contain 1 striped olive bag, 5 vibrant turquoise bags, 4 pale coral bags.\n"
            + "clear green bags contain 4 dull bronze bags, 4 shiny crimson bags, 1 light white bag.\n"
            + "dull chartreuse bags contain 2 dim aqua bags, 3 shiny black bags.\n"
            + "drab lime bags contain 1 wavy chartreuse bag, 4 mirrored chartreuse bags, 1 posh olive bag, 5 mirrored lavender bags.\n"
            + "bright tan bags contain 4 muted tan bags, 5 shiny gold bags, 1 mirrored red bag, 3 dull crimson bags.\n"
            + "dim maroon bags contain 3 clear red bags, 5 dark brown bags, 2 bright maroon bags, 1 muted teal bag.\n"
            + "drab tomato bags contain 4 dim orange bags, 2 mirrored violet bags, 3 faded purple bags.\n"
            + "muted gold bags contain 1 dim cyan bag.\n"
            + "striped white bags contain 1 plaid white bag, 1 posh purple bag, 3 muted cyan bags, 2 pale crimson bags.\n"
            + "wavy beige bags contain 2 plaid white bags, 3 dark brown bags.\n"
            + "vibrant turquoise bags contain 2 muted turquoise bags, 3 plaid green bags, 1 shiny crimson bag.\n"
            + "dark fuchsia bags contain 1 pale purple bag, 1 dim fuchsia bag, 3 light teal bags, 3 vibrant magenta bags.\n"
            + "dotted aqua bags contain 1 bright white bag, 5 clear gold bags, 5 clear tomato bags.\n"
            + "faded silver bags contain 1 light lime bag, 4 wavy gold bags.\n"
            + "faded brown bags contain 4 light aqua bags.\n"
            + "bright gray bags contain 3 faded red bags, 2 muted plum bags, 1 wavy brown bag.\n"
            + "wavy tan bags contain 1 pale maroon bag, 5 posh black bags.\n"
            + "bright blue bags contain 5 posh purple bags.\n"
            + "striped gold bags contain 4 dull tan bags, 1 shiny crimson bag, 2 clear blue bags.\n"
            + "bright magenta bags contain 4 shiny orange bags.\n"
            + "dotted brown bags contain 4 faded teal bags, 5 mirrored coral bags.\n"
            + "muted silver bags contain 5 striped black bags, 3 faded beige bags, 4 plaid crimson bags, 2 wavy brown bags.\n"
            + "wavy purple bags contain 1 dim brown bag, 1 bright yellow bag, 5 shiny lime bags.\n"
            + "dull plum bags contain 1 posh black bag, 4 vibrant fuchsia bags, 5 dull bronze bags.\n"
            + "dotted red bags contain 5 striped tomato bags, 4 shiny orange bags, 4 clear magenta bags, 5 pale coral bags.\n"
            + "light violet bags contain 2 bright beige bags, 5 mirrored plum bags, 3 wavy fuchsia bags, 1 clear tan bag.\n"
            + "dark teal bags contain 3 dull gray bags, 2 dark aqua bags, 1 clear beige bag.\n"
            + "light fuchsia bags contain 2 muted silver bags, 2 striped beige bags.\n"
            + "posh blue bags contain 5 striped olive bags, 5 dim coral bags.\n"
            + "light black bags contain 2 drab coral bags, 2 shiny indigo bags.\n"
            + "pale chartreuse bags contain 5 pale tomato bags.\n"
            + "drab gold bags contain 1 faded gold bag, 5 shiny gold bags.\n"
            + "posh bronze bags contain 2 drab aqua bags, 5 pale gray bags.\n"
            + "light tomato bags contain 5 wavy lime bags.\n"
            + "dull tan bags contain 3 drab blue bags, 4 dull green bags, 4 clear violet bags.\n"
            + "muted beige bags contain 5 clear white bags, 5 faded crimson bags.\n"
            + "faded fuchsia bags contain 5 plaid purple bags, 1 shiny silver bag, 4 muted violet bags.\n"
            + "bright green bags contain 5 dim teal bags, 5 shiny crimson bags, 5 clear crimson bags.\n"
            + "mirrored fuchsia bags contain 4 posh white bags, 5 wavy teal bags, 2 dark violet bags.\n"
            + "vibrant plum bags contain 2 posh yellow bags.\n"
            + "plaid bronze bags contain 4 dotted coral bags, 4 dull green bags, 2 plaid chartreuse bags.\n"
            + "plaid fuchsia bags contain 5 bright white bags.\n"
            + "dull red bags contain 2 mirrored fuchsia bags, 3 vibrant violet bags, 2 bright olive bags, 1 dim orange bag.\n"
            + "faded gray bags contain 1 dull purple bag, 2 posh salmon bags.\n"
            + "wavy plum bags contain 4 pale violet bags, 3 striped magenta bags, 4 pale red bags.\n"
            + "dark crimson bags contain 4 dim yellow bags, 1 dotted purple bag, 2 wavy indigo bags, 4 clear black bags.\n"
            + "pale cyan bags contain 5 shiny coral bags, 4 shiny beige bags, 2 plaid olive bags.\n"
            + "dull violet bags contain 3 wavy olive bags, 1 dull gray bag, 5 vibrant turquoise bags, 1 plaid purple bag.\n"
            + "wavy chartreuse bags contain 1 dotted magenta bag, 3 bright orange bags, 1 mirrored red bag.\n"
            + "dark cyan bags contain 5 dotted turquoise bags, 1 clear purple bag, 1 dim teal bag.\n"
            + "posh coral bags contain 3 muted bronze bags.\n"
            + "pale yellow bags contain 1 drab tomato bag.\n"
            + "plaid turquoise bags contain 1 muted gray bag.\n"
            + "dotted purple bags contain 5 posh silver bags, 4 dark salmon bags.\n"
            + "light indigo bags contain 4 mirrored red bags, 4 light olive bags.\n"
            + "faded plum bags contain 3 mirrored gold bags.\n"
            + "faded coral bags contain 5 dull tan bags.\n"
            + "clear indigo bags contain 5 mirrored magenta bags, 1 clear maroon bag, 1 bright blue bag, 5 light aqua bags.\n"
            + "dim cyan bags contain 5 plaid green bags.\n"
            + "dotted maroon bags contain 5 pale maroon bags, 2 dark indigo bags.\n"
            + "faded beige bags contain 1 plaid chartreuse bag.\n"
            + "striped indigo bags contain 1 dark gray bag, 3 drab olive bags.\n"
            + "clear yellow bags contain 4 dull gray bags, 1 muted green bag.\n"
            + "light lavender bags contain 4 pale coral bags, 2 light yellow bags, 2 light indigo bags.\n"
            + "light turquoise bags contain 5 pale fuchsia bags, 5 vibrant fuchsia bags, 5 vibrant magenta bags, 3 pale indigo bags.\n"
            + "light purple bags contain 2 light cyan bags.\n"
            + "bright gold bags contain 1 dark aqua bag.\n"
            + "muted bronze bags contain 2 light teal bags.\n"
            + "striped gray bags contain 2 light cyan bags, 1 pale black bag, 5 plaid plum bags.\n"
            + "wavy orange bags contain 2 pale coral bags, 2 dim coral bags.\n"
            + "wavy silver bags contain 2 posh white bags, 1 faded beige bag.\n"
            + "clear chartreuse bags contain 1 vibrant lime bag, 2 faded plum bags, 1 striped chartreuse bag, 5 clear maroon bags.\n"
            + "vibrant tan bags contain 3 striped lime bags, 4 pale maroon bags, 2 muted turquoise bags, 4 dark lime bags.\n"
            + "posh aqua bags contain 2 muted tan bags, 2 shiny blue bags, 2 posh purple bags.\n"
            + "bright orange bags contain no other bags.\n"
            + "drab coral bags contain no other bags.\n"
            + "light white bags contain 5 striped yellow bags.\n"
            + "wavy violet bags contain 1 pale silver bag, 2 shiny fuchsia bags, 1 vibrant violet bag, 1 shiny plum bag.\n"
            + "dark white bags contain 4 shiny maroon bags, 2 dim brown bags, 2 dark beige bags, 1 pale blue bag.\n"
            + "vibrant violet bags contain 4 striped blue bags, 1 mirrored lime bag, 1 posh white bag.\n"
            + "vibrant lavender bags contain 4 dotted magenta bags, 1 wavy red bag, 3 pale coral bags, 3 clear indigo bags.\n"
            + "dark purple bags contain 4 posh white bags.\n"
            + "mirrored lavender bags contain 5 clear brown bags, 2 faded gold bags.\n"
            + "striped silver bags contain 3 light yellow bags, 1 drab violet bag.\n"
            + "faded blue bags contain 3 muted violet bags, 4 wavy plum bags, 2 pale indigo bags, 1 wavy bronze bag.\n"
            + "drab cyan bags contain 4 dim tomato bags, 1 plaid lavender bag, 4 pale red bags, 2 drab olive bags.\n"
            + "clear lavender bags contain 3 wavy olive bags, 5 bright gray bags, 3 wavy beige bags, 2 dim violet bags.\n"
            + "striped aqua bags contain 3 mirrored lavender bags.\n"
            + "plaid tomato bags contain 2 posh cyan bags, 3 pale silver bags.\n"
            + "plaid salmon bags contain 1 muted lavender bag, 5 muted green bags, 3 bright aqua bags.\n"
            + "light blue bags contain 1 light white bag, 4 clear violet bags, 3 dark brown bags.\n"
            + "dark blue bags contain 5 posh aqua bags.\n"
            + "faded teal bags contain 3 light beige bags.\n"
            + "plaid chartreuse bags contain 4 wavy teal bags.\n"
            + "wavy gray bags contain 3 drab white bags, 2 muted lavender bags.\n"
            + "pale maroon bags contain 4 faded crimson bags, 4 vibrant chartreuse bags, 1 plaid green bag, 1 vibrant turquoise bag.\n"
            + "dark bronze bags contain 4 faded turquoise bags, 2 faded silver bags, 5 faded salmon bags.\n"
            + "pale coral bags contain 3 mirrored yellow bags, 4 dark plum bags, 2 dark aqua bags, 4 plaid white bags.\n"
            + "mirrored magenta bags contain 5 vibrant lime bags, 4 vibrant chartreuse bags, 3 striped aqua bags.\n"
            + "mirrored salmon bags contain 4 striped salmon bags, 1 posh tan bag, 3 faded bronze bags.\n"
            + "drab tan bags contain 5 vibrant violet bags.\n"
            + "vibrant indigo bags contain 1 pale coral bag, 1 light teal bag, 2 light magenta bags.\n"
            + "plaid lavender bags contain 1 striped silver bag, 1 clear lime bag.\n"
            + "muted plum bags contain 2 plaid crimson bags.\n"
            + "posh gray bags contain 5 mirrored lime bags.\n"
            + "clear maroon bags contain 5 wavy bronze bags, 3 dim gold bags, 2 muted beige bags, 5 posh coral bags.\n"
            + "striped maroon bags contain 2 dotted violet bags, 4 bright fuchsia bags, 4 striped aqua bags.\n"
            + "faded yellow bags contain 2 wavy teal bags, 3 wavy lavender bags.\n"
            + "posh black bags contain 1 drab silver bag, 2 clear white bags, 5 muted silver bags.\n"
            + "muted indigo bags contain 1 dark green bag, 1 plaid chartreuse bag, 3 bright indigo bags, 5 wavy silver bags.\n"
            + "wavy brown bags contain 5 faded red bags, 4 bright orange bags, 3 dim black bags.\n"
            + "vibrant coral bags contain 5 plaid white bags, 5 vibrant indigo bags.\n"
            + "wavy white bags contain 1 plaid green bag, 3 drab chartreuse bags, 1 posh white bag.\n"
            + "pale violet bags contain 2 shiny orange bags, 4 plaid crimson bags.\n"
            + "clear black bags contain 4 wavy blue bags, 5 plaid tan bags, 4 clear magenta bags.\n"
            + "faded aqua bags contain 2 wavy teal bags.\n"
            + "dotted green bags contain 5 shiny orange bags, 1 light magenta bag.\n"
            + "bright coral bags contain 2 shiny fuchsia bags, 4 light lime bags, 1 shiny gold bag.\n"
            + "vibrant fuchsia bags contain 1 vibrant chartreuse bag, 1 striped black bag.\n"
            + "dark turquoise bags contain 5 shiny salmon bags, 2 light lavender bags.\n"
            + "shiny green bags contain 1 pale silver bag, 4 dim red bags, 3 dark lime bags, 4 drab coral bags.\n"
            + "clear red bags contain 5 light teal bags, 5 posh brown bags.\n"
            + "dull gold bags contain 1 drab tan bag, 4 striped tomato bags, 5 pale maroon bags, 2 dim crimson bags.\n"
            + "mirrored red bags contain 3 shiny crimson bags, 4 plaid brown bags, 2 shiny black bags.\n"
            + "pale blue bags contain 1 wavy crimson bag, 4 faded beige bags, 4 shiny chartreuse bags.\n"
            + "clear beige bags contain 4 plaid crimson bags, 5 shiny crimson bags.\n"
            + "drab salmon bags contain 4 dim crimson bags, 3 light magenta bags, 1 clear violet bag.\n"
            + "vibrant magenta bags contain 4 dim black bags.\n"
            + "dark salmon bags contain 3 dull green bags, 4 faded red bags.\n"
            + "posh white bags contain no other bags.\n"
            + "light lime bags contain 5 dark aqua bags.\n"
            + "vibrant salmon bags contain 4 striped tomato bags, 4 clear aqua bags.\n"
            + "clear teal bags contain 3 striped gray bags.\n"
            + "plaid silver bags contain 3 vibrant violet bags, 2 muted magenta bags, 3 dark olive bags, 4 mirrored gold bags.\n"
            + "striped cyan bags contain 4 light gold bags, 2 dotted magenta bags.\n"
            + "wavy bronze bags contain 1 plaid crimson bag, 1 dull gray bag, 5 dull tan bags, 1 mirrored teal bag.\n"
            + "dim olive bags contain 4 vibrant lime bags, 2 shiny crimson bags, 2 muted turquoise bags.\n"
            + "dotted blue bags contain 4 vibrant gray bags, 2 shiny beige bags.\n"
            + "plaid blue bags contain 5 drab silver bags.\n"
            + "dark olive bags contain 1 pale coral bag, 4 vibrant indigo bags.\n"
            + "pale gray bags contain 3 dotted crimson bags, 1 striped magenta bag, 5 wavy white bags, 2 vibrant blue bags.\n"
            + "mirrored brown bags contain 2 bright cyan bags, 4 plaid brown bags, 5 faded turquoise bags.\n"
            + "wavy gold bags contain 1 wavy coral bag.\n"
            + "pale teal bags contain 3 vibrant magenta bags.\n"
            + "mirrored turquoise bags contain 4 mirrored olive bags, 5 bright yellow bags.\n"
            + "dark chartreuse bags contain 4 dotted cyan bags, 5 shiny turquoise bags, 5 vibrant salmon bags, 4 wavy yellow bags.\n"
            + "muted gray bags contain 3 dim orange bags.\n"
            + "posh turquoise bags contain 4 clear lavender bags, 5 dim coral bags, 2 striped salmon bags.\n"
            + "shiny cyan bags contain 4 striped gold bags.\n"
            + "drab olive bags contain 2 plaid cyan bags, 1 mirrored lime bag.\n"
            + "posh tomato bags contain 2 drab tan bags, 3 shiny orange bags.\n"
            + "muted fuchsia bags contain 1 pale salmon bag, 3 wavy violet bags, 3 mirrored maroon bags.\n"
            + "pale bronze bags contain 3 drab yellow bags, 3 muted chartreuse bags.\n"
            + "striped green bags contain 3 striped orange bags, 2 dull green bags, 3 vibrant tan bags.\n"
            + "faded orange bags contain 3 mirrored plum bags, 5 mirrored lime bags, 5 faded red bags.\n"
            + "dull aqua bags contain 2 striped coral bags.\n"
            + "dotted olive bags contain 4 faded salmon bags, 1 wavy green bag.\n"
            + "vibrant silver bags contain 4 mirrored yellow bags, 2 dotted salmon bags, 3 drab silver bags.\n"
            + "striped olive bags contain 1 mirrored fuchsia bag, 1 faded gold bag, 1 mirrored lavender bag.\n"
            + "dark violet bags contain no other bags.\n"
            + "mirrored olive bags contain 5 dull teal bags, 1 dim white bag.\n"
            + "plaid tan bags contain 4 pale gray bags, 2 dim crimson bags, 1 clear violet bag, 1 wavy lime bag.\n"
            + "pale tomato bags contain 5 posh green bags, 4 faded red bags.\n"
            + "dim turquoise bags contain 1 shiny gold bag, 5 drab blue bags.\n"
            + "muted lime bags contain 3 vibrant lime bags, 1 pale plum bag, 1 dark indigo bag.\n"
            + "drab beige bags contain 3 vibrant magenta bags.\n"
            + "posh beige bags contain 2 dark violet bags.\n"
            + "muted olive bags contain 2 pale brown bags, 5 light gray bags, 3 wavy green bags, 2 drab tan bags.\n"
            + "dim orange bags contain 1 clear lime bag, 4 faded beige bags, 2 mirrored fuchsia bags.\n"
            + "dull salmon bags contain 4 striped coral bags, 3 striped aqua bags.\n"
            + "dull maroon bags contain 3 clear brown bags, 5 dull magenta bags, 1 dim red bag.\n"
            + "mirrored coral bags contain 5 muted tan bags, 4 dotted magenta bags, 5 dim olive bags.\n"
            + "posh olive bags contain 4 dull magenta bags, 4 wavy blue bags, 2 drab yellow bags, 5 dotted gold bags.\n"
            + "mirrored violet bags contain 2 pale fuchsia bags.\n"
            + "drab bronze bags contain 4 drab chartreuse bags.\n"
            + "wavy fuchsia bags contain 2 dark gray bags, 5 muted silver bags.\n"
            + "pale plum bags contain 3 vibrant salmon bags, 5 drab chartreuse bags, 2 posh violet bags.\n"
            + "mirrored aqua bags contain 2 pale aqua bags.\n"
            + "bright silver bags contain 3 drab black bags, 5 dark salmon bags, 2 shiny beige bags, 2 posh lavender bags.\n"
            + "plaid lime bags contain 4 faded teal bags, 5 pale brown bags, 5 dim red bags.\n"
            + "dotted turquoise bags contain 3 dim olive bags, 2 mirrored blue bags, 3 dull lime bags, 4 vibrant lavender bags.\n"
            + "drab maroon bags contain 5 bright red bags.\n"
            + "wavy lavender bags contain 1 striped lime bag, 1 posh brown bag.\n"
            + "shiny beige bags contain 5 shiny aqua bags, 3 muted teal bags, 5 clear gold bags.\n"
            + "dark gold bags contain 4 clear maroon bags, 2 dotted maroon bags, 3 light red bags.\n"
            + "light aqua bags contain 2 dim red bags, 3 pale red bags.\n"
            + "posh teal bags contain 3 muted brown bags, 5 shiny gold bags, 5 dotted purple bags.\n"
            + "dull lavender bags contain 5 shiny blue bags.\n"
            + "dark tan bags contain 1 muted tan bag, 5 vibrant turquoise bags, 4 dark violet bags, 4 muted plum bags.\n"
            + "light beige bags contain 2 mirrored fuchsia bags, 1 drab chartreuse bag, 1 muted tan bag.\n"
            + "pale olive bags contain 3 clear brown bags.\n"
            + "dark maroon bags contain 5 dull plum bags, 3 muted green bags.\n"
            + "muted red bags contain 4 pale tan bags, 1 bright white bag.\n"
            + "light tan bags contain 3 light purple bags, 2 pale aqua bags, 3 wavy bronze bags.\n"
            + "clear magenta bags contain 3 vibrant chartreuse bags, 1 dim crimson bag.\n"
            + "dark orange bags contain 2 posh cyan bags, 1 wavy brown bag, 5 dull black bags.\n"
            + "dim lavender bags contain 5 drab fuchsia bags.\n"
            + "dotted beige bags contain 5 light blue bags, 4 plaid tan bags, 2 wavy maroon bags, 5 dim crimson bags.\n"
            + "dim green bags contain 3 plaid tan bags, 1 drab blue bag, 1 clear aqua bag.\n"
            + "plaid brown bags contain 3 muted turquoise bags, 4 drab chartreuse bags.\n"
            + "bright black bags contain 5 striped white bags.\n"
            + "plaid gold bags contain 3 shiny lime bags, 1 plaid maroon bag, 4 bright blue bags.\n"
            + "pale white bags contain 4 drab chartreuse bags, 3 pale tan bags, 5 pale aqua bags.\n"
            + "drab violet bags contain no other bags.\n"
            + "light magenta bags contain 3 faded crimson bags.\n"
            + "light gold bags contain 1 dim lavender bag, 3 light magenta bags, 5 drab gold bags.\n"
            + "plaid coral bags contain 1 vibrant salmon bag, 3 striped tomato bags, 3 posh blue bags.\n"
            + "dotted white bags contain 1 wavy lavender bag.\n"
            + "striped yellow bags contain 4 drab black bags, 2 faded red bags, 2 shiny gold bags, 4 dark aqua bags.\n"
            + "dark yellow bags contain 5 wavy bronze bags, 5 bright purple bags.\n"
            + "faded white bags contain 3 light tomato bags.\n"
            + "muted white bags contain 5 faded gold bags, 1 plaid magenta bag, 3 drab white bags, 5 dim brown bags.\n"
            + "faded red bags contain 1 posh brown bag, 2 muted turquoise bags, 3 plaid crimson bags, 4 shiny orange bags.\n"
            + "dark red bags contain 5 shiny salmon bags.\n"
            + "dull gray bags contain 2 posh white bags.\n"
            + "bright salmon bags contain 2 shiny aqua bags, 3 dotted crimson bags, 1 drab violet bag, 4 pale chartreuse bags.\n"
            + "mirrored chartreuse bags contain 1 vibrant magenta bag, 3 plaid salmon bags, 1 plaid chartreuse bag, 3 muted violet bags.\n"
            + "vibrant orange bags contain 5 posh yellow bags.\n"
            + "dark gray bags contain 1 plaid chartreuse bag, 2 drab violet bags, 1 bright chartreuse bag, 1 muted purple bag.\n"
            + "mirrored crimson bags contain 3 drab coral bags, 5 dull lime bags.\n"
            + "muted violet bags contain 3 dotted crimson bags, 3 light olive bags.\n"
            + "shiny brown bags contain 2 dark lavender bags, 2 vibrant yellow bags, 1 dark black bag, 2 drab olive bags.\n"
            + "muted green bags contain 1 dull cyan bag, 5 dull red bags, 4 pale chartreuse bags.\n"
            + "drab yellow bags contain 3 pale tomato bags.\n"
            + "dotted tomato bags contain 2 shiny magenta bags, 3 mirrored tomato bags, 5 plaid chartreuse bags.\n"
            + "plaid red bags contain 3 pale cyan bags.\n"
            + "bright red bags contain 1 shiny beige bag.\n"
            + "plaid purple bags contain 5 dull gold bags.\n"
            + "dark green bags contain 3 pale salmon bags, 3 dim brown bags, 2 wavy violet bags, 2 pale chartreuse bags.\n"
            + "plaid orange bags contain 1 vibrant chartreuse bag, 2 dotted coral bags, 1 posh teal bag.\n"
            + "plaid violet bags contain 1 shiny maroon bag.\n"
            + "posh tan bags contain 1 shiny beige bag, 2 dim magenta bags, 1 dark violet bag.\n"
            + "bright aqua bags contain 1 drab brown bag, 4 dotted purple bags.\n"
            + "vibrant white bags contain 4 light gray bags, 2 dark fuchsia bags, 1 pale cyan bag.\n"
            + "striped red bags contain 5 faded gold bags, 5 drab crimson bags, 3 faded turquoise bags.\n"
            + "dull purple bags contain 1 pale crimson bag.\n"
            + "shiny blue bags contain 1 pale violet bag, 5 mirrored plum bags, 3 posh white bags, 1 light yellow bag.\n"
            + "clear salmon bags contain 2 striped lime bags, 1 dull violet bag.\n"
            + "faded gold bags contain 3 light teal bags, 3 wavy teal bags.\n"
            + "mirrored yellow bags contain 2 muted turquoise bags, 4 drab chartreuse bags.\n"
            + "plaid gray bags contain 4 plaid plum bags.\n"
            + "plaid white bags contain no other bags.\n"
            + "drab black bags contain 2 mirrored yellow bags, 2 drab chartreuse bags, 1 shiny orange bag.\n"
            + "dotted chartreuse bags contain 2 vibrant cyan bags, 2 light salmon bags, 3 vibrant red bags, 5 light turquoise bags.\n"
            + "faded lavender bags contain 3 dark tomato bags, 5 muted lime bags, 4 light fuchsia bags, 4 dull lavender bags.\n"
            + "vibrant cyan bags contain 2 clear crimson bags, 3 pale orange bags, 4 dull indigo bags, 3 light red bags.\n"
            + "bright maroon bags contain 2 muted tan bags, 2 light teal bags.\n"
            + "drab silver bags contain 3 bright chartreuse bags, 4 pale crimson bags, 5 dotted crimson bags, 5 faded yellow bags.\n"
            + "drab purple bags contain 5 drab blue bags.\n"
            + "dim gold bags contain 1 bright cyan bag, 5 dull white bags, 3 vibrant blue bags.\n"
            + "dark beige bags contain 4 pale coral bags, 1 pale indigo bag.\n"
            + "dotted salmon bags contain 2 drab violet bags, 5 posh white bags.\n"
            + "vibrant crimson bags contain 5 faded teal bags, 3 dotted green bags, 1 clear maroon bag.\n"
            + "dim chartreuse bags contain 3 clear white bags.\n"
            + "bright violet bags contain 1 dim yellow bag, 1 muted purple bag, 4 muted teal bags, 5 striped cyan bags.\n"
            + "dim magenta bags contain 1 pale aqua bag, 4 pale maroon bags, 5 mirrored red bags, 4 drab yellow bags.\n"
            + "dim brown bags contain 5 faded salmon bags, 4 dotted magenta bags, 5 drab tomato bags, 2 faded teal bags.\n"
            + "shiny lime bags contain 5 dotted black bags, 4 plaid turquoise bags, 2 dim tomato bags, 2 clear magenta bags.\n"
            + "drab magenta bags contain 3 dark beige bags.\n"
            + "faded cyan bags contain 2 striped lime bags, 4 bright red bags.\n"
            + "dark brown bags contain 4 mirrored lime bags, 1 bright orange bag.\n"
            + "posh fuchsia bags contain 5 shiny gold bags, 5 pale salmon bags, 1 light coral bag, 1 mirrored plum bag.\n"
            + "shiny magenta bags contain 4 dark aqua bags.\n"
            + "dark lavender bags contain 1 pale purple bag, 3 vibrant yellow bags.\n"
            + "vibrant brown bags contain 2 posh teal bags, 1 wavy silver bag, 2 pale plum bags.\n"
            + "muted black bags contain 5 faded crimson bags, 3 dim crimson bags, 4 vibrant magenta bags.\n"
            + "muted brown bags contain 5 striped olive bags, 5 dark brown bags, 2 clear brown bags, 4 plaid white bags.\n"
            + "pale orange bags contain 4 bright blue bags, 3 dark aqua bags, 1 clear gold bag.\n"
            + "light cyan bags contain 3 muted silver bags.\n"
            + "drab white bags contain 2 drab tan bags, 2 striped tomato bags, 4 dull gray bags, 5 drab blue bags.\n"
            + "dim tan bags contain 3 mirrored bronze bags, 3 faded salmon bags, 4 drab purple bags.\n"
            + "dull tomato bags contain 3 clear lime bags.\n"
            + "wavy aqua bags contain 3 dotted orange bags, 5 shiny crimson bags.\n"
            + "shiny lavender bags contain 5 vibrant blue bags, 4 pale purple bags, 1 wavy bronze bag, 2 posh violet bags.\n"
            + "shiny white bags contain 5 faded blue bags, 5 pale cyan bags.\n"
            + "wavy blue bags contain 4 vibrant chartreuse bags, 4 plaid brown bags, 3 plaid white bags, 2 faded gold bags.\n"
            + "striped magenta bags contain 2 dark olive bags, 5 bright chartreuse bags.\n"
            + "vibrant black bags contain 4 plaid white bags, 2 dull silver bags, 5 striped purple bags, 1 dark plum bag.\n"
            + "dull green bags contain 2 dull crimson bags.\n"
            + "vibrant chartreuse bags contain 2 bright orange bags, 4 dark aqua bags.\n"
            + "dim violet bags contain 2 dark teal bags, 4 plaid brown bags, 4 mirrored yellow bags.\n"
            + "clear blue bags contain 5 posh maroon bags.\n"
            + "faded tomato bags contain 5 clear beige bags, 4 bright orange bags.\n"
            + "posh violet bags contain 3 clear gold bags.\n"
            + "striped tomato bags contain 2 shiny black bags.\n"
            + "muted crimson bags contain 4 light aqua bags, 3 dim gold bags.\n"
            + "clear tan bags contain 4 drab tomato bags, 4 mirrored bronze bags, 1 shiny chartreuse bag.\n"
            + "posh magenta bags contain 4 posh red bags, 3 light bronze bags.\n"
            + "dim blue bags contain 5 dim gray bags, 1 light turquoise bag, 5 muted bronze bags.\n"
            + "drab plum bags contain 1 vibrant plum bag, 4 striped coral bags.\n"
            + "pale silver bags contain 5 drab black bags.\n"
            + "posh purple bags contain 2 dark brown bags.\n"
            + "drab indigo bags contain 1 muted lavender bag, 2 posh salmon bags, 1 pale brown bag.\n"
            + "striped blue bags contain 4 wavy teal bags.\n"
            + "wavy magenta bags contain 1 dotted salmon bag, 1 drab black bag, 2 dull tan bags, 1 drab silver bag.\n"
            + "pale turquoise bags contain 4 dark lime bags, 4 drab maroon bags.\n"
            + "shiny tan bags contain 4 plaid coral bags, 3 dim black bags, 1 dull plum bag.\n"
            + "light teal bags contain 3 dark violet bags.\n"
            + "mirrored silver bags contain 1 striped salmon bag, 1 clear chartreuse bag, 2 clear orange bags, 2 posh aqua bags.\n"
            + "wavy coral bags contain 2 muted teal bags, 1 wavy white bag.\n"
            + "wavy crimson bags contain 1 shiny aqua bag, 3 muted beige bags.\n"
            + "shiny bronze bags contain 1 posh indigo bag, 5 wavy blue bags, 1 faded gold bag, 3 striped tomato bags.\n"
            + "shiny maroon bags contain 1 clear tomato bag, 1 wavy crimson bag.\n"
            + "bright turquoise bags contain 3 dull tan bags, 3 vibrant teal bags.\n"
            + "faded purple bags contain 1 plaid chartreuse bag.\n"
            + "bright white bags contain 2 faded gold bags.\n"
            + "clear orange bags contain 4 striped blue bags, 2 mirrored lime bags, 5 muted turquoise bags.\n"
            + "clear cyan bags contain 1 dim plum bag, 3 shiny brown bags, 1 muted purple bag, 2 plaid lime bags.\n"
            + "plaid teal bags contain 2 faded aqua bags, 4 wavy olive bags.\n"
            + "dotted lime bags contain 5 posh olive bags, 2 pale orange bags.\n"
            + "muted tan bags contain 1 dull gray bag, 2 dark aqua bags, 1 pale violet bag.\n"
            + "striped orange bags contain 3 mirrored lime bags, 2 dull crimson bags, 4 faded gold bags, 3 pale silver bags.\n"
            + "light olive bags contain 2 dark tan bags, 3 dim orange bags, 5 mirrored yellow bags.\n"
            + "plaid magenta bags contain 2 wavy orange bags, 1 wavy chartreuse bag, 5 striped coral bags.\n"
            + "dark magenta bags contain 3 bright white bags, 3 plaid purple bags, 3 striped black bags, 4 light beige bags.\n"
            + "dark black bags contain 3 pale tan bags, 4 mirrored orange bags, 3 dull teal bags.\n"
            + "posh orange bags contain 4 bright aqua bags, 1 dim crimson bag, 4 dim turquoise bags, 1 dotted bronze bag.\n"
            + "dull crimson bags contain 4 vibrant violet bags.\n"
            + "clear turquoise bags contain 1 muted brown bag, 2 dull yellow bags, 3 pale black bags, 1 plaid crimson bag.\n"
            + "vibrant blue bags contain 2 clear beige bags.\n"
            + "dull lime bags contain 4 shiny plum bags, 3 vibrant magenta bags, 3 dark olive bags.\n"
            + "drab brown bags contain 4 clear green bags.\n"
            + "mirrored plum bags contain 2 faded red bags.\n"
            + "shiny fuchsia bags contain 2 muted cyan bags, 4 dark aqua bags, 3 light olive bags, 2 clear gold bags.\n"
            + "vibrant purple bags contain 3 pale aqua bags, 3 dark lime bags, 1 bright chartreuse bag.\n"
            + "bright crimson bags contain 1 vibrant gold bag.\n"
            + "shiny plum bags contain 2 clear olive bags, 4 dark plum bags.\n"
            + "shiny crimson bags contain no other bags.\n"
            + "dull beige bags contain 1 mirrored coral bag.\n"
            + "dim salmon bags contain 1 clear tomato bag, 2 shiny teal bags, 4 plaid olive bags, 3 plaid purple bags.\n"
            + "muted maroon bags contain 2 muted violet bags, 4 dark white bags.\n"
            + "pale aqua bags contain 4 dark beige bags, 1 muted brown bag.\n"
            + "bright plum bags contain 4 dim black bags.\n"
            + "striped tan bags contain 3 bright orange bags, 3 dark violet bags, 4 drab blue bags, 2 vibrant lime bags.\n"
            + "clear crimson bags contain 3 wavy brown bags, 1 faded blue bag, 2 striped cyan bags.\n"
            + "dim indigo bags contain 3 dotted lime bags, 1 dotted purple bag.\n"
            + "pale tan bags contain 2 drab blue bags, 5 dim orange bags, 5 wavy olive bags, 3 striped tomato bags.\n"
            + "vibrant bronze bags contain 5 clear red bags, 5 posh red bags.\n"
            + "dotted bronze bags contain 1 light yellow bag.\n"
            + "wavy salmon bags contain 2 striped olive bags, 4 muted teal bags.\n"
            + "shiny turquoise bags contain 3 dark teal bags, 1 plaid yellow bag.\n"
            + "faded maroon bags contain 1 vibrant salmon bag, 5 dotted magenta bags, 1 faded tan bag, 5 striped tomato bags.\n"
            + "vibrant teal bags contain 4 light teal bags, 3 pale orange bags, 5 drab white bags.\n"
            + "vibrant gold bags contain 1 muted cyan bag, 2 mirrored plum bags, 1 drab coral bag, 4 dark lime bags.\n"
            + "bright cyan bags contain 2 muted silver bags, 5 plaid bronze bags, 3 light beige bags, 2 faded crimson bags.\n"
            + "shiny silver bags contain 4 mirrored fuchsia bags, 2 clear violet bags, 3 faded beige bags.\n"
            + "dark tomato bags contain 4 clear lime bags, 2 light beige bags, 3 bright turquoise bags.\n"
            + "mirrored indigo bags contain 5 posh chartreuse bags, 5 clear tomato bags.\n"
            + "dotted indigo bags contain 2 drab olive bags, 2 dim indigo bags, 5 dotted magenta bags.\n"
            + "shiny gray bags contain 4 muted chartreuse bags, 4 plaid gray bags, 3 dull red bags, 5 striped orange bags.\n"
            + "dim yellow bags contain 1 muted cyan bag, 4 mirrored fuchsia bags, 1 faded gold bag, 1 drab turquoise bag.\n"
            + "bright lime bags contain 5 shiny bronze bags, 3 wavy aqua bags, 4 plaid turquoise bags.\n"
            + "dotted gray bags contain 2 shiny gold bags.\n"
            + "striped black bags contain 1 wavy teal bag, 5 dim chartreuse bags, 4 mirrored lavender bags.\n"
            + "pale beige bags contain 2 posh black bags, 4 clear white bags.\n"
            + "posh indigo bags contain 5 plaid white bags.\n"
            + "dull blue bags contain 4 dark violet bags, 2 clear magenta bags, 4 dotted crimson bags.\n"
            + "mirrored white bags contain 5 faded yellow bags.\n"
            + "bright brown bags contain 3 pale gray bags.\n"
            + "light gray bags contain 3 clear magenta bags, 5 wavy brown bags, 3 dotted salmon bags.\n"
            + "muted orange bags contain 1 bright magenta bag, 1 bright plum bag.\n"
            + "clear purple bags contain 1 shiny gold bag, 1 dark white bag.\n"
            + "striped coral bags contain 5 pale gray bags, 3 wavy chartreuse bags.\n"
            + "plaid green bags contain no other bags.\n"
            + "plaid maroon bags contain 5 posh brown bags, 3 striped crimson bags, 4 plaid green bags.\n"
            + "light maroon bags contain 3 muted gray bags, 5 dull crimson bags, 2 shiny maroon bags.\n"
            + "pale purple bags contain 2 striped white bags, 3 plaid chartreuse bags, 1 mirrored lime bag.\n"
            + "muted yellow bags contain 5 vibrant purple bags, 1 dark teal bag.\n"
            + "plaid olive bags contain 1 light crimson bag, 1 faded gold bag, 1 vibrant blue bag.\n"
            + "dim beige bags contain 4 muted silver bags, 3 mirrored beige bags, 4 striped violet bags.\n"
            + "striped chartreuse bags contain 3 dull teal bags.\n"
            + "muted salmon bags contain 2 posh salmon bags, 2 posh silver bags.\n"
            + "dim red bags contain 2 drab blue bags, 4 plaid crimson bags, 3 vibrant gold bags.\n"
            + "dull magenta bags contain 5 faded crimson bags, 1 shiny orange bag, 1 dark tan bag.\n"
            + "plaid beige bags contain 1 vibrant turquoise bag.\n"
            + "striped teal bags contain 2 dim chartreuse bags, 4 dark green bags.\n"
            + "dotted plum bags contain 2 light cyan bags.\n"
            + "dotted yellow bags contain 5 posh black bags, 5 dull tan bags, 2 dull violet bags, 5 muted plum bags.\n"
            + "dotted coral bags contain 1 striped tomato bag, 2 light crimson bags, 3 clear violet bags.\n"
            + "dull fuchsia bags contain 3 plaid purple bags, 4 mirrored red bags.\n"
            + "dull yellow bags contain 5 vibrant violet bags, 2 dark olive bags.\n"
            + "dull white bags contain 5 posh olive bags, 5 pale tomato bags, 2 bright teal bags.\n"
            + "pale brown bags contain 3 dim crimson bags, 3 pale indigo bags, 1 dim chartreuse bag, 4 muted teal bags.\n"
            + "shiny violet bags contain 1 muted tomato bag, 2 dull yellow bags, 1 drab teal bag.\n"
            + "drab blue bags contain 3 vibrant gold bags, 4 drab black bags.\n"
            + "posh lavender bags contain 5 shiny plum bags, 3 drab salmon bags, 4 dim brown bags, 4 plaid blue bags.\n"
            + "dull teal bags contain 2 drab turquoise bags, 1 shiny crimson bag, 5 shiny aqua bags.\n"
            + "shiny purple bags contain 3 drab orange bags, 4 dark red bags, 4 vibrant fuchsia bags, 2 light fuchsia bags.\n"
            + "pale salmon bags contain 2 plaid chartreuse bags, 3 striped white bags.\n"
            + "posh silver bags contain 4 clear magenta bags, 5 light magenta bags.\n"
            + "light salmon bags contain 4 vibrant olive bags.\n"
            + "striped turquoise bags contain 1 faded magenta bag, 3 shiny indigo bags, 4 striped lavender bags.\n"
            + "dotted crimson bags contain 2 pale silver bags, 2 striped magenta bags, 1 striped white bag.\n"
            + "dull brown bags contain 5 clear crimson bags, 1 dotted green bag, 4 dull magenta bags, 3 dim tan bags.\n"
            + "plaid cyan bags contain 1 striped orange bag, 2 muted cyan bags.\n"
            + "muted lavender bags contain 5 mirrored fuchsia bags.\n"
            + "dim lime bags contain 1 muted black bag.\n"
            + "light bronze bags contain 1 dull crimson bag, 5 dim chartreuse bags.\n"
            + "dull olive bags contain 2 vibrant coral bags, 3 shiny teal bags, 4 plaid purple bags.\n"
            + "posh yellow bags contain 5 dark lime bags, 3 mirrored plum bags.\n"
            + "bright fuchsia bags contain 1 striped silver bag.\n"
            + "posh maroon bags contain 4 dotted magenta bags, 4 posh yellow bags, 2 drab beige bags.\n"
            + "posh salmon bags contain 2 muted green bags.\n"
            + "mirrored gray bags contain 4 striped silver bags.\n"
            + "dull cyan bags contain 2 bright orange bags, 4 dark plum bags.\n"
            + "light crimson bags contain 3 drab fuchsia bags, 3 bright blue bags, 1 dark purple bag.\n"
            + "light red bags contain 3 dim maroon bags, 4 muted green bags, 3 dotted olive bags.\n"
            + "dull indigo bags contain 2 plaid brown bags, 1 wavy white bag, 2 vibrant turquoise bags, 5 drab chartreuse bags.\n"
            + "drab crimson bags contain 2 vibrant salmon bags.\n"
            + "posh red bags contain 3 mirrored violet bags, 1 striped tomato bag, 2 striped olive bags.\n"
            + "drab chartreuse bags contain no other bags.\n"
            + "posh lime bags contain 3 drab violet bags, 1 bright coral bag.\n"
            + "wavy red bags contain 4 striped gray bags, 3 posh salmon bags, 1 dotted violet bag, 3 striped aqua bags.\n"
            + "striped fuchsia bags contain 3 bright crimson bags, 3 dark silver bags, 1 clear magenta bag, 3 drab salmon bags.\n"
            + "striped plum bags contain 2 drab tan bags, 5 pale gold bags, 1 dull white bag, 1 clear coral bag.\n"
            + "mirrored maroon bags contain 1 dark magenta bag, 1 plaid purple bag, 2 light gray bags.\n"
            + "shiny indigo bags contain 2 drab teal bags.\n"
            + "dim silver bags contain 1 striped aqua bag, 3 dull tan bags, 3 striped tan bags, 2 wavy maroon bags.\n"
            + "shiny salmon bags contain 5 faded beige bags.\n"
            + "dull black bags contain 3 vibrant plum bags, 2 plaid chartreuse bags, 1 muted brown bag, 2 clear tomato bags.\n"
            + "clear plum bags contain 3 striped maroon bags, 2 dark white bags.\n"
            + "vibrant green bags contain 5 light orange bags, 5 mirrored magenta bags, 3 bright teal bags, 2 striped brown bags.\n"
            + "drab gray bags contain 1 plaid maroon bag, 2 pale tan bags, 1 plaid white bag.\n"
            + "wavy lime bags contain 2 clear gold bags, 2 bright chartreuse bags, 1 faded crimson bag.\n"
            + "light silver bags contain 4 dim maroon bags, 1 mirrored teal bag.\n"
            + "light brown bags contain 5 muted magenta bags.\n"
            + "dim fuchsia bags contain 5 pale purple bags, 5 wavy orange bags, 5 clear lime bags.\n"
            + "vibrant maroon bags contain 4 light gray bags.\n"
            + "dim purple bags contain 2 muted white bags, 2 shiny aqua bags.\n"
            + "clear olive bags contain 3 bright olive bags.\n"
            + "drab lavender bags contain 4 mirrored crimson bags, 3 bright violet bags, 5 posh gold bags, 2 bright olive bags.\n"
            + "light yellow bags contain 1 posh brown bag, 2 pale violet bags.\n"
            + "plaid crimson bags contain 1 plaid green bag, 3 shiny crimson bags.\n"
            + "pale indigo bags contain 3 clear aqua bags, 2 pale silver bags.\n"
            + "mirrored bronze bags contain 4 muted tomato bags, 4 bright white bags, 1 faded crimson bag.\n"
            + "dim teal bags contain 1 muted salmon bag.\n"
            + "clear violet bags contain 2 dim coral bags, 2 faded beige bags.\n"
            + "dotted silver bags contain 2 posh plum bags, 4 pale chartreuse bags.\n"
            + "pale gold bags contain 2 vibrant gold bags, 1 dotted magenta bag.\n"
            + "posh crimson bags contain 4 dull yellow bags, 3 clear fuchsia bags.\n"
            + "dull orange bags contain 3 dull silver bags, 3 clear violet bags, 4 clear chartreuse bags, 3 faded salmon bags.\n"
            + "striped lime bags contain 5 mirrored plum bags, 4 faded gold bags, 3 wavy white bags, 3 light teal bags.\n"
            + "mirrored tan bags contain 4 dull silver bags, 4 light coral bags, 2 plaid lavender bags.\n"
            + "wavy tomato bags contain 4 clear orange bags, 5 shiny fuchsia bags, 3 light red bags.\n"
            + "dotted teal bags contain 5 dark salmon bags, 1 light indigo bag, 4 pale white bags, 5 clear olive bags.\n"
            + "bright olive bags contain 1 dark tan bag, 4 striped orange bags, 3 bright orange bags.\n"
            + "plaid plum bags contain 1 shiny maroon bag, 1 dotted coral bag.\n"
            + "bright chartreuse bags contain 2 wavy blue bags.";
}
