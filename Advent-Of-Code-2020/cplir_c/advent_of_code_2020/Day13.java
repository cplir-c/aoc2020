package cplir_c.advent_of_code_2020;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongHeapPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongSet;


public class Day13 {
    // Star 2 is borked
    public static void main(String[] args) {
        evaluateBusses(EXAMPLE);
        evaluateBusses(INPUT);
        for (var i = 0; i < OTHER_EXAMPLES.length; ++i) {
            var other  = OTHER_EXAMPLES[i];
            var time   = OTHER_TIMES[i];
            var myTime = findGoldenT(other);
            if (myTime != time) {
                System.out.println("wrong time " + myTime + " good time " + time);
            }
        }
        findGoldenT(EXAMPLE);
        findGoldenT(INPUT);
    }

    static int MODE = 1;

    private static long findGoldenT(String inputString) {
        var       busNumberCongruencies = parseBusNumbers(inputString);
        final var busNumbers            = busNumberCongruencies.keySet();
        checkForSharedFactors(busNumbers);
        long time;
        long period = -1;
        if (MODE >= 1) {
            LongPriorityQueue busNumberQueue = new LongHeapPriorityQueue(
                busNumberCongruencies.keySet().toLongArray(), LongComparators.OPPOSITE_COMPARATOR
            );
            var               lcm            = findLCM(busNumbers);
            while (busNumberQueue.size() >= 2) {
                // System.out.println(busNumberCongruencies);

                var modulusA = busNumberQueue.dequeueLong();
                var modulusB = busNumberQueue.dequeueLong();
                // System.out.println("popped a: " + modulusA + ", b: " + modulusB);
                var newModulus = combineCongruencies(modulusA, modulusB, busNumberCongruencies);
                // System.out.println("pushed " + newModulus);
                busNumberQueue.enqueue(newModulus);
            }
            System.out.println(busNumberCongruencies);
            period = busNumberQueue.dequeueLong();
            if (period < 0) {
                period *= -1;
            }
            time   = busNumberCongruencies.remove(period);
            time *= -1;
            while (time < 0) {
                time += period;
            }
            System.out.println(time < lcm);
            // while (time > 0) {
            // time -= lcm;
            // }
            // time += lcm;
        } else {
            var busList = new LongArrayList(busNumbers);
            busList.sort(LongComparators.OPPOSITE_COMPARATOR);
            var busIterator = busList.iterator();
            var product     = 1L;
            time = 0L;
            while (busIterator.hasNext()) {
                var busNumber = busIterator.nextLong();
                var busOffset = busNumberCongruencies.get(busNumber);
                while (time % busNumber != busOffset) {
                    time += product;
                }
                product *= busNumber;
            }
            period = product;
        }
        System.out.println("all busses leave in this pattern at time " + time + " with period " + period + ".");
        return time;
    }

    static final boolean checkBezoutsIdentity(long a, long b, long x, long y, long gcd) { return a * x + b * y == gcd; }
    private static Long2LongMap parseBusNumbers(String inputString) {
        Long2LongMap busNumberCongruencies = new Long2LongOpenHashMap();
        var          busStrings            = LINE_SPLIT.split(inputString);
        var          busNumberStrings      = COMMA_SPLIT.split(busStrings[1]);
        for (var i = 0; i < busNumberStrings.length; ++i) {
            var busNumberString = busNumberStrings[i];
            if (!"x".equals(busNumberString)) {
                var busNumber = Integer.parseInt(busNumberString);
                busNumberCongruencies.put(busNumber, i % busNumber);
            }
        }
        return busNumberCongruencies;
    }

    private static void checkForSharedFactors(final LongSet busNumbers) throws AssertionError {
        var commonLCM = findLCM(busNumbers);
        var product   = findProduct(busNumbers);
        if (product > commonLCM) {
            throw new AssertionError("shared factors");
        }
    }

    private static long combineCongruencies(long modulusA, long modulusB, Long2LongMap busNumberCongruencies) {
        long modulusAB;
        long congruenceAB;
        var  congruenceA = busNumberCongruencies.remove(modulusA);
        var  congruenceB = busNumberCongruencies.remove(modulusB);
        if (MODE >= 2) {
            long coeffA;
            long coeffB;
            {
                var oldRemainder = modulusA;
                var remainder    = modulusB;

                var oldS = 1L;
                var s    = 0L;

                var oldT = 0L;
                var t    = 1L;

                while (remainder != 0) {
                    var quot = oldRemainder / remainder;

                    var swap = oldRemainder - quot * remainder;
                    oldRemainder = remainder;
                    remainder    = swap;

                    swap = oldS - quot * s;
                    oldS = s;
                    s    = swap;

                    swap = oldT - quot * t;
                    oldT = t;
                    t    = swap;
                }
                coeffA = oldS;
                coeffB = oldT;
                var gcd = oldRemainder;
                System.out.println("coeffA: " + coeffA + " coeffB: " + coeffB);
                if (!checkBezoutsIdentity(modulusA, modulusB, coeffA, coeffB, gcd)) {
                    throw new AssertionError();
                }
            }
            modulusAB    = Math.multiplyExact(modulusA, modulusB);
            congruenceAB = Math.floorMod(
                ((((congruenceA * coeffB)/* % modulusAB */) * modulusB)                        /* % modulusAB */
                    + (((congruenceB * coeffA)/* % modulusAB */) * modulusA)/* % modulusAB */),
                modulusAB
            );
            if (Math.floorMod(congruenceAB, modulusB) != congruenceB) {
                System.out.println("cb:" + congruenceB + " mb:" + modulusB + " cab:" + congruenceAB + " mab:" + modulusAB);
                throw new AssertionError(Math.floorMod(congruenceAB, modulusB));
            }
            if (Math.floorMod(congruenceAB, modulusA) != congruenceA) {
                System.out.println("ca:" + congruenceA + " ma:" + modulusA + " cab:" + congruenceAB + " mab:" + modulusAB);
                throw new AssertionError(Math.floorMod(congruenceAB, modulusA));
            }
        } else {
            modulusAB = modulusA * modulusB;
            congruenceAB = congruenceA;
            while (congruenceAB % modulusB != congruenceB) {
                congruenceAB += modulusA;
            }
            congruenceAB %= modulusAB;
        }
        busNumberCongruencies.put(modulusAB, congruenceAB);
        return modulusAB;
    }

    private static long findProduct(LongSet busNumbers) {
        var product = 1L;
        var it      = busNumbers.iterator();
        for (var b = it.nextLong(); it.hasNext(); b = it.nextLong()) {
            product *= b;
        }
        return product;
    }

    static int findGCD(IntSet busNumbers) {
        var it  = busNumbers.iterator();
        var gcd = it.nextInt();
        for (var b = it.nextInt(); it.hasNext(); b = it.nextInt()) {
            gcd = gcd(gcd, b);
        }
        return gcd;
    }

    static long findLCM(LongSet busNumbers) {
        var thread = 1L;
        var it     = busNumbers.iterator();
        for (var needle = it.nextLong(); it.hasNext(); needle = it.nextLong()) {
            thread = lcm(thread, needle);
        }
        return thread;
    }
    static final long lcm(long a, long b) { return a / gcd(a, b) * b; }
    static final long gcd(long u, long v) {
        if (u == 0) {
            return v;
        } else if (v == 0) {
            return u;
        }
        int k;
        {
            var i = Long.numberOfTrailingZeros(u);
            var j = Long.numberOfTrailingZeros(v);
            if (i < j) {
                k = i;
            } else {
                k = j;
            }
        }
        for (;;) {
            if (u > v) {
                u ^= v;
                v ^= u;
                u ^= v;
            }

            v -= u;

            if (v == 0) {
                return u << k;
            }

            v >>= Long.numberOfTrailingZeros(v);
        }
    }
    static final int gcd(int u, int v) {
        if (u == 0) {
            return v;
        } else if (v == 0) {
            return u;
        }
        int k;
        {
            var i = Integer.numberOfTrailingZeros(u);
            var j = Integer.numberOfTrailingZeros(v);
            if (i < j) {
                k = i;
            } else {
                k = j;
            }
        }
        for (;;) {
            if (u > v) {
                u ^= v;
                v ^= u;
                u ^= v;
            }

            v -= u;

            if (v == 0) {
                return u << k;
            }

            v >>= Integer.numberOfTrailingZeros(v);
        }
    }

    static final Pattern LINE_SPLIT  = Pattern.compile("[\n\r]+");
    static final Pattern COMMA_SPLIT = Pattern.compile(",\\s*");

    private static void evaluateBusses(String inputString) {
        var busStrings       = LINE_SPLIT.split(inputString);
        var timeString       = busStrings[0];
        var time             = Integer.parseInt(timeString);
        var busNumberStrings = COMMA_SPLIT.split(busStrings[1]);
        var busNumberList    = new IntArrayList();
        for (String busNumber : busNumberStrings) {
            if (!"x".equals(busNumber)) {
                busNumberList.add(Integer.parseInt(busNumber));
            }
        }
        var earliestBusTime  = Integer.MAX_VALUE;
        var earliestBusRoute = -1;
        for (int busRoute : busNumberList.toArray(new int[busNumberList.size()])) {
            var busTime = busRoute - time % busRoute;
            if (busTime < earliestBusTime) {
                earliestBusTime  = busTime;
                earliestBusRoute = busRoute;
            }
        }
        System.out.println(
            "earliest bus time: " + earliestBusTime + ", route: " + earliestBusRoute + ", *= "
                    + (earliestBusRoute * earliestBusTime)
        );
    }

    /**
     * 7,13,x,x,59,x,31,19
     * t % 7 == 0
     * t % 13 == 13 - 1
     * t % 59 == 59 - 4
     * t % 31 == 31 - 6
     * t % 19 == 19 - 7
     */
    static final String   EXAMPLE        = "939\n" + "7,13,x,x,59,x,31,19";
    static final String[] OTHER_EXAMPLES = {"\n17,x,13,19", "\n67,7,59,61", "\n67,x,7,59,61", "\n67,7,x,59,61",
                                            "\n1789,37,47,1889"};
    static final long[]   OTHER_TIMES    = {3417, 754018, 779210, 1261476, 1202161486};
    static final String   INPUT          = "1004098\n"
            + "23,x,x,x,x,x,x,x,x,x,x,x,x,41,x,x,x,x,x,x,x,x,x,509,x,x,x,x,x,x,x,x,x,x,x,x,13,17,x,x,x,x,x,x,x,x,x,x,x,x,x,x,29,x,401,x,x,x,x,x,37,x,x,x,x,x,x,x,x,x,x,x,x,19";
}
