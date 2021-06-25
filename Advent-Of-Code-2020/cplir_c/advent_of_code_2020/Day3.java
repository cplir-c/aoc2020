package cplir_c.advent_of_code_2020;

public class Day3 {

    protected static final int[] HILL = new int[] {0x1844a10, 0x892180, 0x5100eb20, 0x834c6c, 0x228c0090, 0x4060e881, 0x214048,
                                                   0x101e0398, 0x2b28c8e3, 0x800, 0x5010029, 0x14007a02, 0x2c99440, 0x58e40003,
                                                   0x889a001, 0x58610090, 0x10283010, 0x810810, 0x2018288a, 0x1a2400,
                                                   0x7cc08205, 0x1000188, 0x15914022, 0x102007, 0x11b30020, 0x5a0910,
                                                   0x41004000, 0x1b0920, 0x10010100, 0x482a20, 0x4060822c, 0x75090402,
                                                   0x12400842, 0x118ac120, 0x87001a0, 0x30000680, 0x459100, 0x32001801, 0x74240,
                                                   0x3082041, 0x4160228, 0x34080230, 0x18102002, 0xa00080, 0x344c3010,
                                                   0x81a8107, 0x5080a042, 0x40a08009, 0x698805a1, 0x2010088, 0x8090, 0x2a510106,
                                                   0x28002420, 0x404c01c, 0x20a12420, 0x408284, 0x60430528, 0x225ad820,
                                                   0x44200d08, 0x320914, 0x20010028, 0x82050, 0x44412100, 0x4c120211,
                                                   0x49002044, 0x8ad108c, 0x18610cf, 0xe908080, 0x40181100, 0x108080,
                                                   0x430000c3, 0x214068, 0x2070123, 0x704c3800, 0x2210607, 0x2440c032,
                                                   0x55080020, 0x10684c4, 0x1000a122, 0x28c04, 0x85a298e, 0x8001010, 0x1d00082,
                                                   0x31cbc400, 0x9152048, 0x21b84020, 0x12100600, 0x1b8051, 0x224020, 0x823800,
                                                   0x2981110, 0x21201a4, 0x8008004, 0x6085110c, 0x8208c44, 0xd00315a, 0x5602881,
                                                   0x1c42c02, 0x2008893, 0x200010, 0x33800015, 0x30010010, 0x44020918,
                                                   0x2440101, 0x10002040, 0x42000280, 0x8050976, 0x991238, 0x1901b0, 0x69460040,
                                                   0x2d18210b, 0xa045980, 0x602810ca, 0x9600882, 0x36620120, 0x2c001504,
                                                   0x800002, 0x1109002, 0x12140002, 0x104a040e, 0x8cc2474, 0x8200181, 0xa81070,
                                                   0x2041005, 0x45122420, 0x4c4c0012, 0x6180a025, 0x11204, 0x11145000,
                                                   0x40088002, 0x181008, 0x44428008, 0x15181452, 0x808002, 0x20040000,
                                                   0x18810021, 0x26004400, 0x84a0cd2, 0x8890c84, 0x14103, 0x1e148, 0x20184512,
                                                   0x26e14190, 0xe8024b4, 0x2412303, 0x2b000460, 0x6010080, 0x838001,
                                                   0x580c2828, 0x420013, 0xa404008, 0x184c0e82, 0x25610100, 0x5e80b0, 0x180008,
                                                   0x2410c989, 0x505007c, 0xb08ed20, 0x2002000d, 0x10878284, 0x1808480,
                                                   0x3c6a5ea0, 0x7004aa01, 0x8900058, 0x21020a, 0x8409021, 0x11003300,
                                                   0x3008104, 0x8421210, 0x62003000, 0x9901a0, 0xb040, 0x50d04400, 0x408815,
                                                   0x120a0100, 0x1e0d602a, 0x2d018104, 0x4180208, 0x28ea0042, 0x350480,
                                                   0x1224ea0, 0x2294051, 0x1804881, 0x4c448249, 0x10800a0, 0x8080008,
                                                   0x40102000, 0x1c000081, 0x41501840, 0x200214, 0x386cc001, 0x34088c00,
                                                   0x4038140, 0x96800, 0x24040be0, 0x6181088, 0x9051c5, 0x18202a41, 0x220010c4,
                                                   0x24602000, 0x180800a1, 0xa212080, 0x8046294, 0x40200400, 0x1020080,
                                                   0xc410116, 0x1981390, 0x613a0c0, 0x6c824000, 0x14842145, 0x20d69400,
                                                   0x81a3310, 0x38204041, 0x6020908, 0x1309a1, 0x84d00a8, 0xa210135, 0x48014420,
                                                   0x48000000, 0x410514, 0x448801c5, 0x34d5610, 0x40429250, 0x1d015800,
                                                   0x42400b28, 0x120184c0, 0x46401428, 0x67310051, 0x721314a0, 0x2d020010,
                                                   0x22d00600, 0x60e08048, 0xd0000, 0x2070213c, 0x46082614, 0x1080a811,
                                                   0x41008510, 0x1800084e, 0x21012200, 0x34912421, 0xa10620, 0x21500100,
                                                   0x264880, 0x66204100, 0x40041381, 0x2002111a, 0x1bafb, 0x510006, 0x4000072c,
                                                   0x41180000, 0x3002500, 0x21a10e11, 0xa00282, 0x100e230, 0x30024800,
                                                   0x4048348, 0x16048240, 0x1130a4a, 0x40000011, 0x74040c24, 0x1ba20008,
                                                   0x222043e, 0x400ad412, 0x4011401, 0x580222f3, 0x30000400, 0x608004a0,
                                                   0x51402011, 0x20021202, 0x20200659, 0x26130298, 0x40800405, 0x30001110,
                                                   0xd26080, 0x809c108, 0x1000800, 0x78004016, 0x40a44120, 0x8980f, 0x5190a00a,
                                                   0x8480802, 0x2a2100, 0x20804802, 0x4241100, 0x10a09800, 0x224b7080,
                                                   0x40c06600, 0x8020340, 0x22202018, 0x10004041, 0x10015101, 0x3210002,
                                                   0x50500000, 0x2644100, 0x1013c85a, 0x42082204, 0x10c42482, 0x17800180,
                                                   0x20091018, 0x42804223, 0x2429158, 0x30000460, 0x36c40046, 0x10904818,
                                                   0x20020425, 0x28004910, 0x4082a08, 0x40600080, 0x4000028, 0x1009414,
                                                   0x2428042, 0xc011612, 0x2122a040, 0x40ad2418, 0x10a2500, 0x10608a02,
                                                   0x1410080, 0x1512a80, 0x15925620, 0x1502088, 0x2c26221, 0x10400498,
                                                   0x120210e8};
    protected static final int[] RISE = new int[] {0b00110000000, 0b10001000100, 0b01000010010, 0b00101000101, 0b01000110010,
                                                   0b00101100000, 0b01010100001, 0b01000000001, 0b10110001000, 0b10001100001,
                                                   0b01001000101};
    protected static final int[] SLOPES = new int[] {1, 3, 5, 7, -2};
    public static void main(String... _$) {
        findMultipleSlopes(HILL);
        findMultipleSlopes(RISE);
    }
    private static void findMultipleSlopes(int[] hill) {
        var result = 1L;
        for (int slope : SLOPES) {
            var hitTrees = hitTrees(hill, slope);
            result *= hitTrees;
            System.out.println("Hill " + (hill == RISE) + " hit " + hitTrees + " trees with a slope of " + slope);
        }
        System.out.println("your slopes hit a product of " + result + " trees.");
    }
    private static int hitTrees(int[] hill, int slope) {
        var hillWidth = prepareHill(hill);
        var bit       = hillWidth - 1;
        final int right;
        final int down;
        if (slope < 0) {
            down = -slope;
            right = 1;
        } else {
            down = 1;
            right = slope;
        }
        var trees = 0;
        for (var i = 0; i < hill.length; i += down) {
            var element = hill[i];
            if ((element & (1 << bit)) != 0) {
                ++trees;
            }
            bit -= right;
            if (bit < 0) {
                bit += hillWidth;
            }

        }
        return trees;
    }

    private static int prepareHill(int[] hill) {
        var hillWidth = Integer.MIN_VALUE;
        for (var line : hill) {
            var width = Integer.SIZE - Integer.numberOfLeadingZeros(line);
            if (width > hillWidth) {
                hillWidth = width;
            }
        }
        return hillWidth;
    }
}
