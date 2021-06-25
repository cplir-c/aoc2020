package cplir_c.advent_of_code_2020;

import java.util.Arrays;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;


public final class Day14 {
    public static final void main(String... args) {
        findMemorySum(INPUT);
        findMemorySum(EXAMPLE);
        findMemoryV2Sum(INPUT);
        findMemoryV2Sum(EXAMPLE2);
    }
    protected static long findMemoryV2Sum(String programString) {
        var         programLines = LINE_SPLIT.split(programString);
        var         orMask       = 0L;
        var         floatingBits = new ByteArrayList(4);
        Long2LongMap memory       = new Long2LongOpenHashMap(programLines.length * 3 / 4);
        for (String lineString : programLines) {
            var line = INLINE_SPLIT.split(lineString);
            if (line.length == 2) {
                // assignment
                if (!"mask".equals(line[0])) {
                    throw new AssertionError(lineString);
                }
                collectFloatingBits(line[1], floatingBits);
                orMask  = collectOrMask(line[1]);
                System.out.println(floatingBits);
                System.out.println("or mask " + orMask);
            } else if (line.length == 3) {
                if (!"mem".equals(line[0])) {
                    throw new AssertionError(lineString);
                }
                long dest  = Integer.parseInt(line[1]);
                var value = Long.parseLong(line[2]);
                System.out.println("read " + Long.toBinaryString(dest));
                dest |= orMask;
                for (var i = (1 << floatingBits.size()) - 1; i >= 0; --i) {
                    if (i > 0) {
                        var ctz = Integer.numberOfTrailingZeros(i);
                        dest ^= 1L << floatingBits.getByte(ctz);
                    } else {
                        var last = floatingBits.size() - 1;
                        dest ^= 1L << floatingBits.getByte(last);
                    }
                    memory.put(dest, value);
                    System.out.println("wrote " + value + " to " + Long.toBinaryString(dest));
                }
                System.out.println();

            } else {
                throw new AssertionError(Arrays.toString(line));
            }
        }
        var sum = longCollectionSum(memory.values());
        System.out.println("memory " + programLines.length + " totalled " + sum + ".");
        return sum;
    }

    private static void collectFloatingBits(String string, ByteArrayList floatingBits) {
        floatingBits.clear();
        var len = string.length() - 1;
        for (var i = (byte) 0; i <= len; ++i) {
            var trit = string.charAt(len - i);
            if (trit == 'X') {
                floatingBits.add(i);
            }
        }
    }

    protected static final Pattern INLINE_SPLIT = Pattern.compile("(?:\\s|\\[|\\]|=)+");
    protected static final Pattern LINE_SPLIT   = Pattern.compile("[\n\r]+");

    protected static long findMemorySum(String programString) {
        var         programLines = LINE_SPLIT.split(programString);
        var         andMask      = -1L;
        var         orMask       = 0L;
        Int2LongMap memory       = new Int2LongOpenHashMap(programLines.length * 3 / 4);
        for (String lineString : programLines) {
            var line = INLINE_SPLIT.split(lineString);
            if (line.length == 2) {
                // assignment
                if (!"mask".equals(line[0])) {
                    throw new AssertionError(lineString);
                }
                andMask = collectAndMask(line[1]);
                orMask  = collectOrMask(line[1]);
                // System.out.println("or mask " + orMask);
            } else if (line.length == 3) {
                if (!"mem".equals(line[0])) {
                    throw new AssertionError(lineString);
                }
                var dest  = Integer.parseInt(line[1]);
                var value = Long.parseLong(line[2]);
                // System.out.println("read " + Long.toBinaryString(value));
                value &= andMask;
                value |= orMask;
                memory.put(dest, value);
                // System.out.println("wrote " + Long.toBinaryString(value) + " to " + dest);
            } else {
                throw new AssertionError(Arrays.toString(line));
            }
        }
        var sum = longCollectionSum(memory.values());
        System.out.println("memory " + programLines.length + " totalled " + sum + ".");
        return sum;
    }

    private static long longCollectionSum(LongCollection memory) {
        if (memory.size() < 500) {
            System.out.println(memory);
        }
        var it  = memory.iterator();
        var sum = 0L;
        while (it.hasNext()) {
            sum += it.nextLong();
        }
        return sum;
    }

    private static long collectOrMask(String string) {
        var orMask = 0L;
        var len    = string.length() - 1;
        for (var i = 0; i <= len; ++i) {
            var trit = string.charAt(len - i);
            if (trit == '1') {
                orMask |= 1L << i;
            }
        }
        return orMask;
    }

    private static long collectAndMask(String string) {
        var andMask = -1L;
        var len     = string.length() - 1;
        for (var i = 0; i <= len; ++i) {
            var trit = string.charAt(len - i);
            if (trit == '0') {
                andMask ^= 1L << i;
            }
        }
        return andMask;
    }

    protected static final String EXAMPLE = "mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X\n" + "mem[8] = 11\n" + "mem[7] = 101\n"
            + "mem[8] = 0";
    protected static final String EXAMPLE2 = "mask = 000000000000000000000000000000X1001X\n" + "mem[42] = 100\n"
            + "mask = 00000000000000000000000000000000X0XX\n" + "mem[26] = 1";
    protected static final String INPUT   = "mask = 1010X101010010101X00X00011XX11011111\n" + "mem[1303] = 728\n"
            + "mem[5195] = 213352120\n" + "mem[34818] = 782\n" + "mem[43971] = 29724050\n" + "mem[51737] = 1731727\n"
            + "mem[5175] = 353551570\n" + "mem[45056] = 8766\n" + "mask = 0110X1011110XX111X0011X0X01X00010010\n"
            + "mem[7343] = 6334776\n" + "mem[28415] = 10870\n" + "mem[4761] = 2912\n" + "mem[43137] = 14501587\n"
            + "mem[27900] = 10713\n" + "mem[19990] = 519691\n" + "mask = 001X0X011110X01XX000100X10010100X00X\n"
            + "mem[60244] = 1003035\n" + "mem[4068] = 7428\n" + "mem[36608] = 846\n" + "mem[41866] = 7255\n"
            + "mem[6694] = 1615\n" + "mask = 00100X011110101011X01X1X100011X111XX\n" + "mem[48890] = 66269\n"
            + "mem[17236] = 189693\n" + "mem[2699] = 43253\n" + "mem[2454] = 3144\n" + "mem[39460] = 3089616\n"
            + "mem[15030] = 12081234\n" + "mask = 0010010111100011110001XX110X111X1101\n" + "mem[39460] = 11410\n"
            + "mem[60142] = 71274\n" + "mem[25233] = 1470014\n" + "mask = 001010XX0110X0X010001001X01011XX010X\n"
            + "mem[22857] = 1694968\n" + "mem[45337] = 4212\n" + "mem[11908] = 1413\n" + "mem[22285] = 37595935\n"
            + "mem[47401] = 85\n" + "mask = X0100000010111XX10XX100X00011X101000\n" + "mem[7417] = 1439\n"
            + "mem[30454] = 231239\n" + "mem[57206] = 135231401\n" + "mask = X00X00011110X01XX1X0101X01X0X1001101\n"
            + "mem[3723] = 783778\n" + "mem[13431] = 5668213\n" + "mem[51267] = 10450641\n" + "mem[34637] = 7118\n"
            + "mem[61773] = 37952031\n" + "mask = 01100011111010100X00X11001100001X01X\n" + "mem[43657] = 4053\n"
            + "mem[60574] = 208\n" + "mem[63077] = 1061\n" + "mask = 0010XX011111111X110X01X1011X100101X1\n"
            + "mem[3723] = 415\n" + "mem[3445] = 15859116\n" + "mem[41920] = 420621\n"
            + "mask = 110000X1X11111X01100101001X01X10X010\n" + "mem[35499] = 20781439\n" + "mem[52838] = 7255541\n"
            + "mask = 11X0X001111111X1110X111XX110100XXX11\n" + "mem[54458] = 6901034\n" + "mem[22912] = 933561\n"
            + "mem[56316] = 22094822\n" + "mem[9061] = 4263320\n" + "mem[18464] = 12349351\n"
            + "mask = 0010XXX111101010X10010110X10XXX001X1\n" + "mem[47210] = 950410\n" + "mem[44693] = 64746868\n"
            + "mem[43376] = 231697502\n" + "mem[16345] = 64190\n" + "mem[20801] = 47984501\n"
            + "mask = 00101X01111X1X10110000XX011011X00101\n" + "mem[29047] = 56934570\n" + "mem[7343] = 34945618\n"
            + "mem[20465] = 117516955\n" + "mem[37958] = 2965\n" + "mem[42804] = 7022\n" + "mem[55559] = 2588315\n"
            + "mem[42833] = 496336618\n" + "mask = 001001001X10X01011000XX01011X0X00X1X\n" + "mem[36900] = 4267113\n"
            + "mem[18097] = 787869710\n" + "mem[20935] = 9666\n" + "mask = 0X101101X110XX1110110X00011001110110\n"
            + "mem[8063] = 61539\n" + "mem[62771] = 13459\n" + "mem[22406] = 1573083\n" + "mem[57402] = 259790331\n"
            + "mem[6391] = 60\n" + "mem[31844] = 43954\n" + "mem[47641] = 902301\n"
            + "mask = XX10X10101001X101X00101010X01XX00111\n" + "mem[42804] = 3474837\n" + "mem[8265] = 8147\n"
            + "mem[31405] = 23707\n" + "mem[12687] = 173\n" + "mem[44291] = 1721\n"
            + "mask = X01X1001011XX0X0110001011X011X1X0101\n" + "mem[23640] = 92068\n" + "mem[34308] = 47290\n"
            + "mem[19715] = 1865698\n" + "mem[34086] = 11397123\n" + "mem[38401] = 25087116\n" + "mem[23653] = 2124900\n"
            + "mem[5175] = 59504\n" + "mask = 010X1101X1101X1010000000101011X01101\n" + "mem[36608] = 27387896\n"
            + "mem[51052] = 9633930\n" + "mem[49440] = 27834809\n" + "mem[213] = 6773\n" + "mem[61490] = 1532\n"
            + "mask = 0X10X11101101XX010000010X1101XX10001\n" + "mem[58790] = 140286263\n" + "mem[43181] = 7274951\n"
            + "mem[33657] = 102007\n" + "mem[62963] = 38045093\n" + "mem[10183] = 1593\n"
            + "mask = 0X100XX11110101XX1001X1X1110X0010110\n" + "mem[34818] = 576\n" + "mem[25241] = 29771912\n"
            + "mem[55694] = 25675255\n" + "mem[55532] = 2905\n" + "mem[31674] = 22202384\n" + "mem[40737] = 240265396\n"
            + "mask = 01000X01XX1XX01010001XX0XX0011000101\n" + "mem[3861] = 188\n" + "mem[31405] = 28053743\n"
            + "mem[19392] = 130524\n" + "mem[53356] = 9628388\n" + "mask = X010010011X01010110000111X0X00000010\n"
            + "mem[29019] = 5874\n" + "mem[58933] = 3630\n" + "mem[11075] = 8076\n" + "mem[26867] = 1617118\n"
            + "mem[50839] = 5784986\n" + "mem[62785] = 2319201\n" + "mask = X1000111001X0X101110011001X111011110\n"
            + "mem[38706] = 3267\n" + "mem[51436] = 108\n" + "mem[56768] = 93786924\n" + "mem[60797] = 1829\n"
            + "mem[13226] = 101560323\n" + "mask = 10100X011XXX11111X000X0XX1X011000101\n" + "mem[30557] = 24517\n"
            + "mem[44625] = 133397612\n" + "mem[52664] = 13349\n" + "mem[40985] = 1162102\n" + "mem[3103] = 144664\n"
            + "mask = 01X0X10001111X1010000111XX0110000100\n" + "mem[18838] = 93742603\n" + "mem[65481] = 64659697\n"
            + "mem[46060] = 6434\n" + "mem[42804] = 38595\n" + "mem[28112] = 9053\n" + "mem[44064] = 36165247\n"
            + "mask = 001X110111X110101100XX010X1X010001X1\n" + "mem[63113] = 127541\n" + "mem[1765] = 7989\n"
            + "mem[9226] = 4084\n" + "mem[3861] = 97296879\n" + "mask = 00100X0101101010111X100X010000111X1X\n"
            + "mem[35358] = 686552\n" + "mem[6694] = 1841506\n" + "mem[55621] = 1345\n" + "mem[41000] = 60174738\n"
            + "mask = 0010000X0XX111X110X000XX00X1011010X1\n" + "mem[40402] = 145874501\n" + "mem[57922] = 399\n"
            + "mem[52664] = 99438939\n" + "mask = 1100000111X110111X011X10X11XXX1XX011\n" + "mem[33569] = 114300\n"
            + "mem[61164] = 14120711\n" + "mask = 11000X0110X1101X1X00110100001001X010\n" + "mem[53897] = 494676\n"
            + "mem[20401] = 3268954\n" + "mem[11948] = 7012\n" + "mem[57951] = 596\n" + "mem[39747] = 190642769\n"
            + "mem[48609] = 8683\n" + "mem[1886] = 30506550\n" + "mask = 1X101100011010XX1000100100001X000001\n"
            + "mem[29590] = 158427\n" + "mem[60687] = 1158\n" + "mem[41555] = 82232640\n" + "mem[8063] = 650\n"
            + "mem[62026] = 1522\n" + "mask = X01X0100111X101X1100101110XX1X100X10\n" + "mem[62760] = 2759\n"
            + "mem[65014] = 15033892\n" + "mem[16548] = 227511\n" + "mem[25472] = 940\n" + "mem[58257] = 303172074\n"
            + "mem[25462] = 1448494\n" + "mem[14207] = 11623\n" + "mask = 111X00X101X0X011100XX000100101000X00\n"
            + "mem[54065] = 385095\n" + "mem[6381] = 173190\n" + "mem[45414] = 2576\n" + "mem[32716] = 216614666\n"
            + "mem[47401] = 512991\n" + "mem[39753] = 1919665\n" + "mask = 0X00011100100110XX0X011X01111X0X11X0\n"
            + "mem[2194] = 547\n" + "mem[61316] = 3475\n" + "mem[35639] = 64138\n" + "mem[24776] = 1204\n"
            + "mask = 1X101101X1X01010100000001100X1000100\n" + "mem[23580] = 234194\n" + "mem[3192] = 121525545\n"
            + "mem[19701] = 4464888\n" + "mem[30757] = 224067766\n" + "mem[32607] = 1173\n"
            + "mask = 1X1X1100011X101X10000X00110101110111\n" + "mem[15935] = 3229\n" + "mem[41229] = 555\n"
            + "mem[34746] = 395\n" + "mem[18832] = 617\n" + "mask = 0010010111X01XX0110000100000X1010011\n"
            + "mem[48352] = 7847\n" + "mem[6372] = 27568\n" + "mem[38353] = 7485288\n" + "mem[4384] = 6397740\n"
            + "mem[45671] = 1826\n" + "mask = 0010000101101010XXX000001XX010100011\n" + "mem[41581] = 11155\n"
            + "mem[16345] = 6986933\n" + "mem[54042] = 12767\n" + "mask = 0XXX1X1111101010010X1010011X1101X101\n"
            + "mem[2194] = 6152\n" + "mem[38909] = 83\n" + "mem[31247] = 1573830\n" + "mem[60597] = 386\n"
            + "mem[6753] = 14417902\n" + "mask = X110XX1101X011X0100X101001111011X101\n" + "mem[15722] = 757783\n"
            + "mem[50431] = 720318\n" + "mem[63271] = 210820202\n" + "mem[53226] = 714717\n" + "mem[59123] = 4122\n"
            + "mask = 0010X001X1X01010X1001011100100010101\n" + "mem[17236] = 197898992\n" + "mem[38190] = 2368352\n"
            + "mem[9018] = 235\n" + "mem[1553] = 373976\n" + "mem[38729] = 89918321\n" + "mem[31669] = 50727\n"
            + "mem[28423] = 18976\n" + "mask = 111010011111X1X1X101X10101X010X111X1\n" + "mem[62005] = 1863145\n"
            + "mem[62607] = 191764\n" + "mem[12344] = 516953\n" + "mem[14945] = 454940\n" + "mem[44064] = 420728\n"
            + "mask = 000001X100100110XX0100101010000XX100\n" + "mem[2292] = 25305594\n" + "mem[33356] = 189060799\n"
            + "mem[1785] = 100787\n" + "mem[28040] = 246660557\n" + "mem[25931] = 365777315\n" + "mem[50067] = 12600\n"
            + "mem[4177] = 142606369\n" + "mask = 001X11X10110XX10XX001X10000010001001\n" + "mem[61912] = 261608214\n"
            + "mem[44398] = 2204\n" + "mem[64497] = 697913547\n" + "mem[10951] = 23268\n" + "mem[58010] = 589\n"
            + "mem[45644] = 445371547\n" + "mem[24722] = 930518\n" + "mask = 101000X11001X1X11X00000011X00XX10X01\n"
            + "mem[3726] = 93\n" + "mem[9735] = 3651838\n" + "mask = 001001X111X01111X100101110X011000011\n"
            + "mem[33718] = 1322993\n" + "mem[37047] = 4759\n" + "mem[43376] = 833\n" + "mem[53897] = 3261\n"
            + "mem[62389] = 71090581\n" + "mask = 0X101001011010101100011111X11X0111X0\n" + "mem[39230] = 906\n"
            + "mem[25233] = 3657471\n" + "mem[7377] = 44247\n" + "mask = 00101X010X1010101100000XX1011X0001X0\n"
            + "mem[25241] = 291758\n" + "mem[23845] = 1462440\n" + "mem[51530] = 273094825\n" + "mem[15135] = 30712212\n"
            + "mask = XX1X1X011X1110101101100011101X110X00\n" + "mem[16538] = 243646453\n" + "mem[60552] = 27316\n"
            + "mem[19220] = 1982193\n" + "mem[31113] = 2444\n" + "mask = 101011XX01X010101X0010XX00XX11000101\n"
            + "mem[1172] = 24817491\n" + "mem[15935] = 104503643\n" + "mask = 1010XX0110111X10X1001010XX0000X000XX\n"
            + "mem[8812] = 389608924\n" + "mem[6230] = 12722765\n" + "mem[5392] = 251053\n" + "mem[25888] = 13081575\n"
            + "mem[1704] = 158591\n" + "mem[46201] = 13297\n" + "mem[61316] = 943\n"
            + "mask = 001001X111101X10110X01X0111000011110\n" + "mem[37958] = 3378989\n" + "mem[58790] = 16704\n"
            + "mem[24122] = 423950643\n" + "mask = 00X0X0010X0X1X111X001010X00101X01011\n" + "mem[61319] = 1502\n"
            + "mem[49739] = 5640\n" + "mem[43144] = 101689290\n" + "mask = 001000X1X1X1X111100X000X0X1X000X0010\n"
            + "mem[59936] = 26771\n" + "mem[50781] = 54614700\n" + "mem[58566] = 10101\n" + "mem[32495] = 361\n"
            + "mem[32592] = 11958\n" + "mem[50027] = 200985\n" + "mem[59514] = 6947531\n"
            + "mask = 0XX0X101011010101000X0X01110110X01X0\n" + "mem[49739] = 794426\n" + "mem[25462] = 8540549\n"
            + "mem[43849] = 2037191\n" + "mem[11862] = 80922734\n" + "mem[15935] = 49909230\n" + "mem[12097] = 9886926\n"
            + "mem[16345] = 533565\n" + "mask = 0110010XX11XXX10100001X0101011001001\n" + "mem[34380] = 1226\n"
            + "mem[46477] = 14011\n" + "mem[13573] = 110921\n" + "mask = 01101X0X01101010X000X100111X100X1011\n"
            + "mem[17502] = 24422\n" + "mem[53627] = 60689\n" + "mem[26767] = 176156438\n"
            + "mask = 00101X00111011X01X010010X111001XXX1X\n" + "mem[35284] = 7683672\n" + "mem[28519] = 957363766\n"
            + "mask = 01X001X10X1X01101XX00111X110110X110X\n" + "mem[2390] = 50690464\n" + "mem[9515] = 36413\n"
            + "mem[4966] = 455023\n" + "mem[16345] = 4642\n" + "mem[23940] = 967447277\n"
            + "mask = 001011011X1010X00100X0X0X1000X001101\n" + "mem[30362] = 492\n" + "mem[39436] = 55771435\n"
            + "mem[54750] = 4405963\n" + "mem[12154] = 5316113\n" + "mask = X01001X1111X1111XX0XX01001001X01010X\n"
            + "mem[11861] = 8093273\n" + "mem[5528] = 594330\n" + "mem[2580] = 749\n" + "mem[41339] = 2813480\n"
            + "mask = 0X100X0101X010101010010X001110XX10X0\n" + "mem[49739] = 25\n" + "mem[56763] = 3225353\n"
            + "mem[62676] = 82\n" + "mask = 00X0010111100010X10X101010000X0XX11X\n" + "mem[24711] = 19689\n"
            + "mem[25883] = 1662\n" + "mem[39685] = 1366\n" + "mem[64022] = 31675243\n"
            + "mask = 1010000110111X1XX1X1X00X1X100X000101\n" + "mem[50781] = 138807\n" + "mem[61134] = 212475189\n"
            + "mask = 1X10000110111110010X00X010X000X00100\n" + "mem[26767] = 521755660\n" + "mem[43849] = 297\n"
            + "mem[6773] = 56375\n" + "mem[7440] = 6344\n" + "mem[54750] = 375\n"
            + "mask = 001001011110XX1X110XX0101XX0X1010X01\n" + "mem[14512] = 116152715\n" + "mem[1785] = 849605743\n"
            + "mem[28216] = 741473\n" + "mem[24722] = 98336\n" + "mem[44689] = 10577\n" + "mem[61134] = 96966\n"
            + "mem[15075] = 68597\n" + "mask = 1X00000XX11X10X11100101X001X001X1101\n" + "mem[22210] = 7191\n"
            + "mem[10538] = 441544661\n" + "mask = 000X010111101110110010X1101XX01001X0\n" + "mem[50023] = 5845123\n"
            + "mem[9193] = 343236\n" + "mem[14594] = 328315\n" + "mask = 0010XX01X1X0101X10X0101010X011X00010\n"
            + "mem[10568] = 213103847\n" + "mem[35471] = 19909935\n" + "mem[51052] = 198214667\n" + "mem[33707] = 212943\n"
            + "mem[18838] = 218437742\n" + "mask = 1X10X00X11X1010101X1101X1110XX0X1100\n" + "mem[29150] = 3702971\n"
            + "mem[28145] = 781579\n" + "mem[62509] = 517\n" + "mem[44281] = 3796239\n" + "mem[29845] = 72642510\n"
            + "mem[7666] = 2181716\n" + "mask = 101X100X1X11101X1X011X00X000000X0000\n" + "mem[44068] = 3210156\n"
            + "mem[34746] = 463\n" + "mem[19733] = 150\n" + "mask = 0010000101X0101X1100000X001X000111XX\n"
            + "mem[51921] = 5582\n" + "mem[8182] = 1826115\n" + "mem[9247] = 159\n" + "mem[62726] = 65701789\n"
            + "mem[13992] = 293819555\n" + "mask = 0010X0010X101X1011X00X110XX000011101\n" + "mem[16844] = 508434281\n"
            + "mem[48132] = 19312\n" + "mem[57886] = 10241870\n" + "mem[33326] = 5734\n" + "mem[15821] = 61381475\n"
            + "mem[2454] = 7235981\n" + "mem[37695] = 4671\n" + "mask = 101X1101010010X0100X0XX1110XX1X1110X\n"
            + "mem[43877] = 2937\n" + "mem[41115] = 438513\n" + "mem[60412] = 12947\n" + "mem[54334] = 556096\n"
            + "mem[61587] = 3806\n" + "mem[28680] = 5115\n" + "mask = 0011000111X0X011000XXX1110X1X10X1X11\n"
            + "mem[30303] = 2309\n" + "mem[7287] = 126478\n" + "mem[213] = 3432\n" + "mem[32811] = 22838482\n"
            + "mem[51164] = 13661\n" + "mem[37058] = 63634172\n" + "mask = X010X0011011101X1101X1X0000X00000X00\n"
            + "mem[19037] = 78465\n" + "mem[26276] = 710992554\n" + "mem[64165] = 398875\n" + "mem[50921] = 353203677\n"
            + "mem[44064] = 1387869\n" + "mask = 01XX110101X01X10X000001X00010001000X\n" + "mem[43320] = 109786\n"
            + "mem[55034] = 10048064\n" + "mem[23862] = 1534456\n" + "mem[26276] = 241328811\n" + "mem[7886] = 2239\n"
            + "mem[61998] = 107016\n" + "mask = 0110110X01000110X0X01X11000011X01XX1\n" + "mem[7377] = 671018\n"
            + "mem[34883] = 808846\n" + "mem[32738] = 277\n" + "mem[27774] = 491651359\n"
            + "mask = 1X1000X111111111110011101X10X0110111\n" + "mem[41555] = 4053944\n" + "mem[34829] = 751\n"
            + "mem[50875] = 443892\n" + "mask = XXX10001X110101110X0100X1X11X000100X\n" + "mem[42467] = 95922687\n"
            + "mem[33576] = 205350883\n" + "mem[51342] = 12652555\n" + "mem[12687] = 1796343\n" + "mem[3136] = 107821\n"
            + "mem[8430] = 21508842\n" + "mask = X010XX01X11010101X001010X0X011XX0101\n" + "mem[35499] = 968828445\n"
            + "mem[44464] = 3653\n" + "mem[5361] = 60058\n" + "mem[30081] = 123907\n"
            + "mask = 00111001X110X0101XXX00011001101X0X01\n" + "mem[6862] = 20247901\n" + "mem[27705] = 9416895\n"
            + "mem[8766] = 1155691\n" + "mem[48820] = 821601\n" + "mem[16804] = 9608\n" + "mem[8357] = 68479859\n"
            + "mem[3679] = 6874765\n" + "mask = 00X0X10X11101X10110X0X0010110X100110\n" + "mem[19220] = 43510192\n"
            + "mem[46054] = 12027499\n" + "mem[44734] = 12928\n" + "mem[64207] = 32793280\n" + "mem[53919] = 788\n"
            + "mem[61604] = 43110\n" + "mask = 1XX000011X1X1X1X110X1X100X1010001011\n" + "mem[26418] = 45221\n"
            + "mem[35535] = 53651\n" + "mem[51747] = 328535529\n" + "mem[62676] = 675298\n" + "mem[63077] = 248663921\n"
            + "mask = 0X10X10X0100XX10100XXX11X00010001101\n" + "mem[1281] = 5448838\n" + "mem[53899] = 589128\n"
            + "mem[60412] = 296\n" + "mem[60829] = 3224999\n" + "mem[47927] = 6185999\n" + "mem[57442] = 2119\n"
            + "mask = 1100000111101111110X10000000XX001X11\n" + "mem[843] = 15340\n" + "mem[10831] = 930479\n"
            + "mem[46477] = 109098\n" + "mem[48820] = 2088358\n" + "mem[31113] = 8722\n"
            + "mask = 1X100001X01XX1X01100001011000X01X101\n" + "mem[29801] = 82058\n" + "mem[26073] = 49617\n"
            + "mem[64220] = 788\n" + "mem[30409] = 62851693\n" + "mem[36932] = 16032238\n" + "mem[27496] = 7145\n"
            + "mask = X010100111111X10110X1100001X1010X01X\n" + "mem[44871] = 1176\n" + "mem[30454] = 416371248\n"
            + "mem[52631] = 311042\n" + "mem[11862] = 960076\n" + "mem[22356] = 256\n"
            + "mask = 0010X1010X001010101010001X0X0X0010X1\n" + "mem[9193] = 274\n" + "mem[5787] = 47507\n"
            + "mem[24647] = 807446\n" + "mask = 001000010X1010101100X010001XX0001111\n" + "mem[23915] = 4026\n"
            + "mem[41555] = 1918531\n" + "mem[54334] = 391383\n" + "mem[34366] = 9256\n" + "mem[1303] = 7754953\n"
            + "mask = 101X110X01001X00X000X10001110X0X0100\n" + "mem[56736] = 13339\n" + "mem[56952] = 432959128\n"
            + "mem[53279] = 656667\n" + "mem[46365] = 2024606\n" + "mem[65212] = 15190\n" + "mem[11342] = 42370426\n"
            + "mask = 00100X0000011111XX00XX01X1X10010000X\n" + "mem[48859] = 13295465\n" + "mem[13701] = 1119634\n"
            + "mem[61592] = 60623405\n" + "mask = X0101XX101101010X100010X010101010100\n" + "mem[43417] = 214737\n"
            + "mem[35728] = 2015\n" + "mem[28668] = 448775513\n" + "mem[58101] = 3296\n" + "mem[28096] = 2626693\n"
            + "mem[65358] = 59779358\n" + "mask = 1010X10XX11010X01X0010X0100X10110110\n" + "mem[31600] = 571\n"
            + "mem[20168] = 261939\n" + "mem[8575] = 3237\n" + "mem[18097] = 12166\n" + "mem[29592] = 293500\n"
            + "mem[48171] = 676\n" + "mem[16380] = 2093\n" + "mask = 1X100101X100101XX0X01001100X11101111\n"
            + "mem[31473] = 1160\n" + "mem[14821] = 237\n" + "mem[11809] = 153714176\n" + "mem[37695] = 39507135\n"
            + "mem[55555] = 171731\n" + "mem[30757] = 3238878\n" + "mem[5787] = 363\n"
            + "mask = 001011X1X100111010X01X10101010001000\n" + "mem[143] = 147026133\n" + "mem[15841] = 2748727\n"
            + "mem[58986] = 108963\n" + "mem[42804] = 21362\n" + "mask = 001X10000X10100XX000100101110X001X00\n"
            + "mem[21444] = 118026\n" + "mem[18464] = 18560\n" + "mem[13838] = 1005630\n" + "mem[4380] = 194314\n"
            + "mask = 0110110101XX1X1X10XX010X11X0X0000110\n" + "mem[2974] = 3177\n" + "mem[19734] = 44943565\n"
            + "mem[27376] = 71889\n" + "mask = X110X1X10110101010X0000010100X01X000\n" + "mem[59987] = 195313\n"
            + "mem[12183] = 28345\n" + "mem[5495] = 47272\n" + "mem[19742] = 8940";
}
