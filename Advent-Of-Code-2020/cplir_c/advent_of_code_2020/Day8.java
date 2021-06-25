package cplir_c.advent_of_code_2020;

import java.util.BitSet;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;


public class Day8 {
    static final String EXAMPLE
        = "nop +0\n" + "acc +1\n" + "jmp +4\n" + "acc +3\n" + "jmp -3\n" + "acc -99\n" + "acc +1\n" + "jmp -4\n" + "acc +6";
    public static final void main(String... args) {
        findLoop(EXAMPLE);
        findLoop(INPUT);
        fixLoop(EXAMPLE);
        fixLoop(INPUT);
    }

    private static void fixLoop(String input) {
        var il = compileInput(input);
        var swapIndex = findSwapIndex(il);
        il.set(swapIndex, (il.getInt(swapIndex) ^ 0b11));
        var acc = runIntcode(il);
        System.out.println("fixed at instruction " + swapIndex + " with a result of " + acc);
    }

    static class InsnRoute implements Comparable<InsnRoute> {
        int insnPtr;
        int swappedPosition;

        public InsnRoute(int insnPtr, int swappedPosition) {
            this.insnPtr         = insnPtr;
            this.swappedPosition = swappedPosition;
        }
        @Override
        public int compareTo(InsnRoute o) {
            var insnComp = Integer.compare(o.insnPtr, this.insnPtr);
            if (insnComp != 0) {
                return insnComp;
            }
            return Boolean.compare(o.swappedPosition >= 0, this.swappedPosition >= 0);
        }
        @Override
        public int hashCode() {
            var a = (this.insnPtr ^ this.swappedPosition);
            return ((a >> 16) ^ this.insnPtr) ^ ((a << 16) ^ this.swappedPosition);
        }
        @Override
        public boolean equals(Object otherObj) {
            if (otherObj instanceof InsnRoute) {
                var other = (InsnRoute) otherObj;
                return other.insnPtr == this.insnPtr && other.swappedPosition == this.swappedPosition;
            }
            return false;
        }
        public long summary() { return summarize(this.insnPtr, this.swappedPosition); }
        public static long summarize(int insnPtr, int swappedPosition) {
            return (((long) insnPtr) << 32) | ((swappedPosition) & 0xff_ff_ff_ffL);
        }
        @Override
        public String toString() {
            return String.format("InsnRoute(insnPtr=%d, swappedPosition=%d)", this.insnPtr, this.swappedPosition);
        }
    }
    private static int findSwapIndex(IntList il) {
        IntList             directJumps   = new IntArrayList(il.size());
        IntList             swappedJumps  = new IntArrayList(il.size());
        recordJumps(il, directJumps, swappedJumps);
        LongSet discoveredRoutes = new LongOpenHashSet();
        var routes = new PriorityQueue<InsnRoute>();
        routes.add(new InsnRoute(0, -1));
        var goalInsPtr = il.size();
        while (!routes.isEmpty()) {
            var route  = routes.remove();
            var insPtr = route.insnPtr;
            var ins    = il.getInt(insPtr);
            if (route.swappedPosition < 0 && ((ins & 0b11) != 0)) {
                // spawn a swapped route
                var newInsPtr = swappedJumps.getInt(insPtr);
                var newSwappedPos = insPtr;
                if (newInsPtr == goalInsPtr) {
                    return newSwappedPos;
                }
                spawnRoute(discoveredRoutes, routes, newInsPtr, newSwappedPos);
            }
            // spawn a normal route
            var newInsPtr     = directJumps.getInt(insPtr);
            var newSwappedPos = route.swappedPosition;
            if (newInsPtr == goalInsPtr) {
                return newSwappedPos;
            }
            spawnRoute(discoveredRoutes, routes, newInsPtr, newSwappedPos);
        }
        throw new AssertionError("could not find a safe route");
    }

    private static void spawnRoute(LongSet discoveredRoutes, PriorityQueue<InsnRoute> routes, int newInsPtr,
                                   int newSwappedPos) {
        var summary       = InsnRoute.summarize(newInsPtr, newSwappedPos);
        if (!discoveredRoutes.contains(summary)) {
            routes.add(new InsnRoute(newInsPtr, newSwappedPos));
            discoveredRoutes.add(summary);
        }
    }

    private static void recordJumps(IntList il, IntList directJumps, IntList swappedJumps) {
        for (var insPtr = 0; insPtr < il.size(); ++insPtr) {
            var ins = il.getInt(insPtr);
            switch (ins & 0b11) {
                case 0:
                    // acc
                    writeAccInsn(directJumps, swappedJumps, insPtr);
                    break;

                case 2:
                    // nop
                    writeSwappableInsn(il, directJumps, swappedJumps, insPtr, ins);
                    break;

                case 1:
                    // jmp
                    writeSwappableInsn(il, swappedJumps, directJumps, insPtr, ins);
                    break;

                default :
                    assert false;
            }
        }
    }

    private static void writeAccInsn(IntList directJumps, IntList swappedJumps, int insPtr) {
        var nextInsPtr = insPtr + 1;
        directJumps.add(nextInsPtr);
        swappedJumps.add(nextInsPtr);
    }

    private static void writeSwappableInsn(IntList il, IntList nopJumps, IntList jmpJumps, int insPtr, int ins) {
        nopJumps.add(insPtr + 1);
        jmpJumps.add(insPtr + (ins >> 2));
    }

    private static void findLoop(String input) {
        var il  = compileInput(input);
        var acc = runIntcode(il);
        System.out.println(acc);
    }

    private static Object runIntcode(IntList il) {
        var executed = new BitSet(il.size());
        var insPtr   = 0;
        var acc      = 0;
        while (insPtr < il.size() && !executed.get(insPtr)) {
            // System.out.println("ip " + insPtr + " acc " + acc);
            var ins = il.getInt(insPtr);
            executed.set(insPtr);
            switch (ins & 0b11) {
                case 0:
                    acc += ins >> 2;
                case 2:
                    insPtr++;
                    break;

                case 1:
                    insPtr += ins >> 2;
                    break;

                default :
                    assert false;
            }
        }
        return acc;
    }

    private static final Pattern LINE_SPLIT = Pattern.compile("\n[\n \t]*");
    private static final Pattern SPACE_SPLIT = Pattern.compile("\\s+");

    private static IntList compileInput(String input) {
        IntList il    = new IntArrayList();
        var lines = LINE_SPLIT.split(input);
        for (String lineString : lines) {
            var line = SPACE_SPLIT.split(lineString);
            var ins       = line[0];
            var argString = line[1];

            var arg = Integer.parseInt(argString) << 2;

            switch (ins) {
                case "nop":
                    il.add(arg | 2);
                    break;
                case "jmp":
                    il.add(arg | 1);
                    break;
                case "acc":
                    il.add(arg);
                    break;

                default :
                    continue;
            }
        }
        return il;
    }

    static final String INPUT = "jmp +27\n" + "acc +32\n" + "acc +10\n" + "acc +23\n" + "jmp +88\n" + "acc +46\n" + "acc -3\n"
            + "jmp +209\n" + "acc +1\n" + "jmp +159\n" + "acc +29\n" + "jmp +328\n" + "acc +44\n" + "jmp +14\n" + "acc +14\n"
            + "jmp +557\n" + "nop +127\n" + "acc +34\n" + "nop +227\n" + "jmp +214\n" + "jmp +512\n" + "jmp +78\n"
            + "jmp +544\n" + "acc +14\n" + "acc +5\n" + "acc -11\n" + "jmp +291\n" + "acc +5\n" + "nop +115\n" + "jmp +166\n"
            + "acc +2\n" + "jmp +1\n" + "jmp +500\n" + "jmp +244\n" + "jmp +186\n" + "acc +43\n" + "acc +26\n" + "jmp +502\n"
            + "acc +14\n" + "nop +63\n" + "jmp +115\n" + "acc -11\n" + "nop +153\n" + "acc +3\n" + "nop +107\n" + "jmp +468\n"
            + "acc -6\n" + "acc +38\n" + "acc +0\n" + "jmp +102\n" + "acc +27\n" + "acc -9\n" + "acc +45\n" + "jmp +186\n"
            + "nop +457\n" + "acc +14\n" + "jmp +483\n" + "nop +35\n" + "acc +27\n" + "jmp +202\n" + "jmp -55\n" + "jmp +1\n"
            + "acc +33\n" + "acc -2\n" + "acc +5\n" + "jmp +296\n" + "acc +17\n" + "acc +11\n" + "acc +36\n" + "nop +11\n"
            + "jmp +10\n" + "acc +20\n" + "nop +115\n" + "acc +37\n" + "jmp +284\n" + "acc +39\n" + "acc +40\n" + "jmp +1\n"
            + "jmp +1\n" + "jmp +233\n" + "acc +42\n" + "acc +27\n" + "jmp +1\n" + "nop +189\n" + "jmp +329\n" + "jmp +118\n"
            + "acc +13\n" + "jmp -82\n" + "acc +18\n" + "acc -1\n" + "acc +23\n" + "jmp +104\n" + "acc +25\n" + "acc +5\n"
            + "acc +49\n" + "jmp +274\n" + "acc +35\n" + "jmp +1\n" + "jmp +45\n" + "acc -1\n" + "jmp +128\n" + "jmp +110\n"
            + "acc +11\n" + "acc +48\n" + "nop +316\n" + "acc -15\n" + "jmp +150\n" + "nop +396\n" + "acc +19\n" + "acc +15\n"
            + "jmp +129\n" + "acc +17\n" + "acc +42\n" + "jmp +1\n" + "acc +13\n" + "jmp +333\n" + "nop -24\n" + "acc +5\n"
            + "acc -9\n" + "acc -14\n" + "jmp +129\n" + "acc +14\n" + "nop +486\n" + "acc -4\n" + "jmp +274\n" + "jmp +269\n"
            + "acc +0\n" + "acc +36\n" + "acc +8\n" + "jmp -102\n" + "acc -3\n" + "acc +18\n" + "jmp +162\n" + "acc +16\n"
            + "acc +26\n" + "nop +313\n" + "acc +9\n" + "jmp -26\n" + "acc +16\n" + "jmp +383\n" + "acc +10\n" + "jmp +245\n"
            + "jmp +119\n" + "jmp -57\n" + "acc +17\n" + "jmp +75\n" + "acc +13\n" + "jmp +452\n" + "acc -13\n" + "acc -13\n"
            + "jmp -115\n" + "acc +18\n" + "jmp +97\n" + "acc +0\n" + "jmp -28\n" + "acc +43\n" + "jmp +401\n" + "acc -17\n"
            + "jmp +91\n" + "acc +16\n" + "acc +22\n" + "acc +42\n" + "jmp +244\n" + "nop +376\n" + "acc +36\n" + "acc +20\n"
            + "acc +32\n" + "jmp -157\n" + "acc -6\n" + "acc +33\n" + "jmp +295\n" + "jmp -20\n" + "acc -2\n" + "acc +7\n"
            + "jmp +305\n" + "nop -76\n" + "acc +18\n" + "acc +24\n" + "jmp +89\n" + "acc -8\n" + "acc -1\n" + "jmp +171\n"
            + "acc +40\n" + "acc +11\n" + "acc +15\n" + "acc +43\n" + "jmp +234\n" + "jmp +1\n" + "acc +45\n" + "nop +343\n"
            + "jmp -140\n" + "acc +40\n" + "acc -6\n" + "acc +35\n" + "jmp +67\n" + "acc -5\n" + "acc +2\n" + "acc +32\n"
            + "acc +32\n" + "jmp +199\n" + "acc +40\n" + "acc +19\n" + "jmp +337\n" + "acc -1\n" + "acc -14\n" + "acc +34\n"
            + "jmp +266\n" + "nop +265\n" + "acc -1\n" + "acc +31\n" + "jmp +151\n" + "jmp -206\n" + "acc +49\n" + "acc +24\n"
            + "acc -16\n" + "jmp -82\n" + "jmp -117\n" + "nop +238\n" + "acc -10\n" + "jmp +150\n" + "acc +26\n" + "nop -95\n"
            + "acc +21\n" + "jmp +59\n" + "nop -13\n" + "acc +45\n" + "acc +45\n" + "jmp +350\n" + "jmp +285\n" + "acc +28\n"
            + "acc +31\n" + "acc +6\n" + "jmp -106\n" + "jmp +294\n" + "nop -142\n" + "acc +13\n" + "nop +347\n" + "acc +43\n"
            + "jmp +79\n" + "acc +7\n" + "jmp +368\n" + "acc +35\n" + "acc +1\n" + "acc +4\n" + "jmp +355\n" + "acc -10\n"
            + "jmp -175\n" + "acc +35\n" + "jmp -3\n" + "acc -2\n" + "acc +35\n" + "acc +33\n" + "acc +34\n" + "jmp -154\n"
            + "acc +27\n" + "jmp +131\n" + "acc -18\n" + "jmp +74\n" + "acc -14\n" + "nop +173\n" + "jmp +79\n" + "nop -82\n"
            + "acc +26\n" + "acc -4\n" + "nop -237\n" + "jmp +270\n" + "jmp +118\n" + "acc +0\n" + "acc +34\n" + "jmp -212\n"
            + "nop -59\n" + "jmp -150\n" + "acc +26\n" + "jmp +224\n" + "jmp +1\n" + "acc -18\n" + "jmp +85\n" + "nop -134\n"
            + "acc +6\n" + "jmp -136\n" + "acc +4\n" + "jmp -246\n" + "acc +9\n" + "acc +24\n" + "jmp -105\n" + "nop +99\n"
            + "acc -13\n" + "acc -15\n" + "nop +286\n" + "jmp -187\n" + "jmp -276\n" + "acc -14\n" + "acc -12\n" + "jmp +148\n"
            + "acc -18\n" + "jmp -254\n" + "acc +23\n" + "acc -10\n" + "acc +32\n" + "acc +49\n" + "jmp +39\n" + "acc -10\n"
            + "acc +10\n" + "acc -17\n" + "acc +39\n" + "jmp +19\n" + "jmp +236\n" + "jmp -205\n" + "acc +0\n" + "acc +5\n"
            + "acc -15\n" + "jmp +41\n" + "acc +28\n" + "acc -18\n" + "nop -20\n" + "jmp -175\n" + "jmp +23\n" + "acc +36\n"
            + "nop +198\n" + "jmp +223\n" + "jmp +1\n" + "nop -60\n" + "acc +28\n" + "jmp +118\n" + "acc +12\n" + "acc +9\n"
            + "jmp +159\n" + "nop +176\n" + "nop +11\n" + "acc -1\n" + "jmp +183\n" + "acc -6\n" + "acc +16\n" + "jmp -43\n"
            + "acc -17\n" + "nop +222\n" + "acc -4\n" + "jmp +1\n" + "jmp -21\n" + "acc +43\n" + "acc +42\n" + "acc -2\n"
            + "acc +12\n" + "jmp +168\n" + "acc +10\n" + "acc +38\n" + "nop -159\n" + "jmp +94\n" + "acc +5\n" + "acc -1\n"
            + "jmp -317\n" + "jmp -294\n" + "jmp +42\n" + "acc +11\n" + "acc +38\n" + "acc +27\n" + "acc +0\n" + "jmp -63\n"
            + "jmp -57\n" + "acc +23\n" + "jmp -111\n" + "nop +1\n" + "acc -12\n" + "jmp -91\n" + "acc +22\n" + "acc -1\n"
            + "nop -163\n" + "jmp +1\n" + "jmp -165\n" + "acc -12\n" + "acc -7\n" + "acc -9\n" + "acc +37\n" + "jmp +82\n"
            + "acc -10\n" + "acc +29\n" + "acc +0\n" + "nop +200\n" + "jmp -129\n" + "acc +13\n" + "acc +33\n" + "jmp -33\n"
            + "acc +27\n" + "jmp -172\n" + "jmp +57\n" + "jmp -234\n" + "jmp -141\n" + "acc +35\n" + "nop +202\n" + "acc -6\n"
            + "jmp +51\n" + "acc +10\n" + "jmp -8\n" + "jmp -291\n" + "acc +36\n" + "acc +25\n" + "jmp -263\n" + "jmp +211\n"
            + "acc +21\n" + "acc -7\n" + "acc -6\n" + "nop -222\n" + "jmp -247\n" + "acc -8\n" + "acc +29\n" + "jmp -21\n"
            + "acc +0\n" + "jmp -256\n" + "jmp +1\n" + "acc +37\n" + "nop +55\n" + "acc +40\n" + "jmp -266\n" + "acc +17\n"
            + "jmp +200\n" + "jmp +1\n" + "acc +7\n" + "acc +10\n" + "acc +24\n" + "jmp -6\n" + "acc +8\n" + "jmp -104\n"
            + "nop -64\n" + "acc +3\n" + "nop -391\n" + "acc +26\n" + "jmp +6\n" + "acc +12\n" + "acc -9\n" + "nop +110\n"
            + "jmp -420\n" + "jmp -411\n" + "nop -273\n" + "nop -287\n" + "acc +39\n" + "jmp +117\n" + "jmp -119\n"
            + "acc +38\n" + "jmp +119\n" + "acc +0\n" + "jmp -430\n" + "acc -14\n" + "jmp -231\n" + "acc +26\n" + "acc +1\n"
            + "acc -13\n" + "acc +15\n" + "jmp -208\n" + "jmp +1\n" + "acc +50\n" + "jmp -263\n" + "acc +14\n" + "jmp +1\n"
            + "acc +31\n" + "jmp -13\n" + "nop -334\n" + "nop +76\n" + "nop -435\n" + "nop -52\n" + "jmp +131\n" + "nop +53\n"
            + "acc +19\n" + "nop -213\n" + "acc +5\n" + "jmp -338\n" + "acc +48\n" + "acc +22\n" + "acc +43\n" + "acc +1\n"
            + "jmp -377\n" + "acc +38\n" + "jmp -268\n" + "nop -269\n" + "acc +20\n" + "acc +6\n" + "nop -395\n" + "jmp -415\n"
            + "jmp +1\n" + "jmp -398\n" + "acc -12\n" + "acc -10\n" + "acc -18\n" + "jmp +1\n" + "jmp +94\n" + "jmp -358\n"
            + "jmp -313\n" + "acc +12\n" + "acc +20\n" + "acc -13\n" + "jmp -110\n" + "acc +28\n" + "acc +12\n" + "acc +42\n"
            + "acc +43\n" + "jmp +101\n" + "acc -14\n" + "jmp -6\n" + "acc +25\n" + "acc -7\n" + "acc +5\n" + "jmp -420\n"
            + "acc -4\n" + "jmp -89\n" + "acc -17\n" + "nop -499\n" + "jmp -379\n" + "nop -395\n" + "acc +37\n" + "acc +30\n"
            + "acc +5\n" + "jmp -25\n" + "jmp +63\n" + "jmp +71\n" + "acc -3\n" + "jmp -24\n" + "jmp -117\n" + "acc -6\n"
            + "jmp +1\n" + "acc +26\n" + "nop -212\n" + "jmp -498\n" + "jmp -395\n" + "jmp -210\n" + "acc +44\n" + "acc +12\n"
            + "acc +21\n" + "jmp +40\n" + "acc +43\n" + "jmp -382\n" + "nop -509\n" + "acc -17\n" + "jmp -111\n" + "jmp -16\n"
            + "acc +31\n" + "jmp -306\n" + "jmp -22\n" + "acc +50\n" + "acc +47\n" + "jmp -398\n" + "nop -300\n" + "jmp -246\n"
            + "jmp +49\n" + "acc +0\n" + "acc +12\n" + "acc +7\n" + "nop -6\n" + "jmp -109\n" + "acc -19\n" + "acc +21\n"
            + "acc -19\n" + "nop -355\n" + "jmp -418\n" + "jmp -245\n" + "acc +50\n" + "jmp +1\n" + "nop -3\n" + "jmp -177\n"
            + "acc +29\n" + "acc +40\n" + "acc -15\n" + "nop -123\n" + "jmp -305\n" + "nop -313\n" + "acc -3\n" + "acc +50\n"
            + "jmp -530\n" + "jmp -398\n" + "acc +16\n" + "acc +29\n" + "nop -358\n" + "acc +37\n" + "jmp -165\n" + "jmp -193\n"
            + "jmp -132\n" + "acc +21\n" + "jmp -355\n" + "jmp -450\n" + "jmp -456\n" + "acc +25\n" + "acc +49\n" + "acc +50\n"
            + "acc +0\n" + "jmp -60\n" + "acc +5\n" + "acc -15\n" + "jmp -565\n" + "acc +10\n" + "acc -9\n" + "acc -3\n"
            + "jmp -220\n" + "acc +44\n" + "acc -10\n" + "jmp -70\n" + "acc -17\n" + "jmp -174\n" + "jmp -168\n" + "acc +6\n"
            + "acc +35\n" + "jmp -133\n" + "acc -12\n" + "acc +41\n" + "acc +41\n" + "jmp -580\n" + "acc +45\n" + "acc +27\n"
            + "acc +12\n" + "acc +0\n" + "jmp -24\n" + "acc +35\n" + "nop -507\n" + "nop -27\n" + "nop -456\n" + "jmp -379\n"
            + "jmp -222\n" + "acc +6\n" + "acc +43\n" + "acc -9\n" + "acc +45\n" + "jmp +1";
}
