package cplir_c.advent_of_code_2020;

import java.util.Arrays;


public class Day5 {
    private static final int[] PASSES
        = new int[] {0b0_001_101_001, 0b1_011_100_101, 0b1_011_110_011, 0b1_101_000_001, 0b1_101_010_000, 0b1_010_101_001,
                     0b0_110_010_101, 0b1_001_110_001, 0b0_101_001_101, 0b0_100_100_111, 0b1_100_000_010, 0b0_010_110_000,
                     0b1_011_101_100, 0b1_001_100_111, 0b1_101_101_001, 0b1_101_111_100, 0b0_010_011_110, 0b0_101_100_001,
                     0b1_000_101_100, 0b1_010_111_111, 0b0_110_101_101, 0b1_001_110_000, 0b1_010_100_101, 0b1_000_010_101,
                     0b1_100_010_000, 0b1_101_011_011, 0b0_011_010_110, 0b0_110_100_111, 0b1_011_110_110, 0b0_111_000_110,
                     0b0_100_011_110, 0b1_101_111_000, 0b1_101_000_000, 0b1_010_101_000, 0b1_100_101_001, 0b0_100_011_010,
                     0b1_011_001_110, 0b0_010_001_110, 0b1_000_000_011, 0b1_101_011_100, 0b1_100_110_110, 0b0_010_001_111,
                     0b1_000_110_011, 0b0_111_111_110, 0b1_010_010_010, 0b0_000_111_111, 0b1_001_011_101, 0b0_000_111_000,
                     0b0_010_110_100, 0b0_110_000_111, 0b0_001_010_001, 0b0_001_100_001, 0b0_100_001_111, 0b0_110_010_110,
                     0b0_011_011_001, 0b1_001_111_101, 0b0_101_101_100, 0b0_001_101_010, 0b0_101_100_000, 0b0_100_000_111,
                     0b0_101_101_000, 0b0_110_010_100, 0b0_100_110_000, 0b1_000_101_010, 0b1_001_001_101, 0b0_110_000_100,
                     0b1_101_100_000, 0b1_010_000_011, 0b0_111_111_111, 0b0_110_001_111, 0b0_000_111_110, 0b1_110_000_100,
                     0b0_101_110_010, 0b0_111_101_100, 0b0_011_100_011, 0b0_100_111_000, 0b1_011_000_110, 0b1_000_110_110,
                     0b1_001_011_100, 0b1_010_110_000, 0b0_001_111_010, 0b0_101_010_100, 0b1_100_110_101, 0b0_111_111_001,
                     0b1_000_111_111, 0b0_001_111_001, 0b0_110_101_011, 0b0_011_001_011, 0b1_001_000_101, 0b0_011_011_010,
                     0b0_110_100_000, 0b1_000_011_010, 0b0_011_011_000, 0b0_111_111_000, 0b0_100_001_000, 0b1_011_011_111,
                     0b0_101_000_011, 0b0_001_000_101, 0b0_100_010_010, 0b1_001_100_001, 0b0_111_100_000, 0b1_101_010_111,
                     0b1_000_111_100, 0b1_010_101_011, 0b0_100_000_001, 0b1_000_010_001, 0b1_101_001_111, 0b0_101_001_000,
                     0b0_000_111_001, 0b1_010_111_001, 0b0_110_110_111, 0b0_110_111_101, 0b1_001_010_000, 0b1_001_000_011,
                     0b0_111_000_000, 0b1_011_101_111, 0b0_110_100_001, 0b1_011_010_111, 0b0_101_101_010, 0b0_101_001_111,
                     0b1_011_011_101, 0b1_000_001_000, 0b0_011_001_100, 0b0_011_000_001, 0b1_101_010_011, 0b1_100_111_101,
                     0b1_101_110_111, 0b1_101_101_011, 0b0_111_010_110, 0b1_011_010_001, 0b1_011_011_001, 0b0_001_000_000,
                     0b0_010_010_111, 0b1_001_101_111, 0b0_001_011_001, 0b1_100_010_110, 0b0_101_110_101, 0b1_010_110_100,
                     0b1_000_110_100, 0b1_101_100_011, 0b1_000_111_001, 0b0_110_110_101, 0b0_100_101_001, 0b1_100_010_011,
                     0b1_010_101_010, 0b1_010_101_111, 0b0_110_011_010, 0b0_011_010_000, 0b1_010_010_101, 0b1_101_110_110,
                     0b1_010_000_110, 0b0_001_000_100, 0b1_101_001_110, 0b1_110_001_001, 0b1_101_000_010, 0b0_010_111_011,
                     0b0_100_000_000, 0b0_011_001_000, 0b0_111_110_101, 0b0_110_100_101, 0b1_110_000_101, 0b1_011_110_101,
                     0b0_010_000_010, 0b1_000_001_100, 0b0_010_001_001, 0b0_001_010_010, 0b0_111_101_011, 0b0_100_010_011,
                     0b1_101_100_101, 0b1_000_001_111, 0b1_001_101_000, 0b0_111_101_000, 0b1_101_111_010, 0b0_101_010_110,
                     0b1_101_111_110, 0b0_010_000_100, 0b0_011_111_101, 0b0_001_110_100, 0b0_101_111_101, 0b0_010_010_101,
                     0b0_100_100_001, 0b0_001_100_100, 0b1_101_011_001, 0b0_110_000_011, 0b0_100_111_100, 0b1_010_010_000,
                     0b1_010_010_111, 0b0_011_111_100, 0b0_010_111_111, 0b1_001_000_111, 0b0_111_100_101, 0b1_100_001_111,
                     0b1_101_111_011, 0b1_000_111_000, 0b1_000_000_010, 0b1_001_110_101, 0b0_011_011_100, 0b0_100_010_000,
                     0b0_001_001_100, 0b1_000_100_010, 0b0_011_010_100, 0b0_111_110_111, 0b1_000_010_100, 0b0_111_100_100,
                     0b1_011_111_100, 0b0_111_010_000, 0b0_001_111_011, 0b0_010_010_010, 0b0_100_101_101, 0b1_010_100_001,
                     0b0_010_011_011, 0b0_001_010_000, 0b1_011_100_110, 0b0_001_100_111, 0b1_100_110_000, 0b1_100_111_001,
                     0b0_101_011_001, 0b1_010_110_001, 0b0_110_101_000, 0b0_001_111_111, 0b0_101_010_010, 0b0_001_001_111,
                     0b0_000_111_100, 0b1_101_110_010, 0b0_110_000_001, 0b0_100_000_011, 0b0_111_011_101, 0b1_000_100_100,
                     0b0_111_001_111, 0b0_111_001_011, 0b1_001_111_000, 0b0_110_111_001, 0b0_111_110_110, 0b0_101_011_110,
                     0b0_101_010_000, 0b1_101_100_111, 0b0_001_011_111, 0b0_011_111_000, 0b1_011_100_011, 0b0_001_110_101,
                     0b0_001_001_010, 0b0_110_011_001, 0b0_101_111_001, 0b1_000_000_100, 0b0_010_000_101, 0b1_100_110_010,
                     0b1_011_101_101, 0b1_001_010_101, 0b0_110_001_000, 0b0_111_001_101, 0b0_011_000_110, 0b0_101_101_111,
                     0b1_001_101_110, 0b0_111_010_111, 0b0_110_100_110, 0b0_100_111_010, 0b1_010_001_110, 0b0_111_111_010,
                     0b1_101_001_010, 0b1_100_000_100, 0b0_101_111_100, 0b1_101_101_110, 0b1_100_000_000, 0b1_011_001_101,
                     0b1_101_001_000, 0b0_110_110_000, 0b1_000_101_000, 0b1_101_000_111, 0b0_111_110_011, 0b1_000_001_101,
                     0b0_011_000_000, 0b0_011_011_110, 0b0_001_000_010, 0b0_100_101_010, 0b1_100_100_010, 0b1_001_100_100,
                     0b1_101_110_001, 0b0_111_001_100, 0b1_011_011_011, 0b1_100_010_111, 0b0_010_011_010, 0b0_001_110_000,
                     0b0_011_110_101, 0b1_011_010_010, 0b1_001_010_111, 0b0_110_000_110, 0b1_101_101_000, 0b0_011_101_110,
                     0b1_000_100_001, 0b0_110_001_100, 0b1_011_010_000, 0b0_110_100_011, 0b1_010_000_000, 0b0_011_101_111,
                     0b0_011_101_011, 0b0_011_100_101, 0b0_011_101_000, 0b0_001_011_101, 0b1_010_000_001, 0b1_100_111_100,
                     0b1_101_010_001, 0b0_011_100_100, 0b0_100_101_100, 0b0_001_000_001, 0b1_101_111_001, 0b0_101_110_111,
                     0b1_101_101_100, 0b0_011_110_100, 0b1_010_101_100, 0b0_100_001_001, 0b1_000_010_000, 0b1_011_111_101,
                     0b1_000_101_101, 0b1_001_011_000, 0b0_100_010_101, 0b0_001_000_110, 0b1_110_000_110, 0b0_001_111_100,
                     0b0_110_000_000, 0b0_001_011_000, 0b1_000_110_010, 0b0_111_110_100, 0b0_100_110_100, 0b1_001_001_110,
                     0b1_100_011_010, 0b1_101_101_101, 0b1_010_100_100, 0b1_100_100_011, 0b0_011_111_111, 0b0_010_110_001,
                     0b0_001_110_011, 0b0_110_001_110, 0b1_101_110_101, 0b0_001_111_101, 0b1_011_010_011, 0b1_100_111_111,
                     0b0_100_001_010, 0b0_110_010_001, 0b0_110_110_011, 0b1_100_100_001, 0b0_010_000_011, 0b1_010_001_100,
                     0b0_101_000_110, 0b0_110_001_101, 0b1_001_000_110, 0b0_100_011_101, 0b0_100_110_110, 0b0_010_011_000,
                     0b1_010_011_110, 0b1_010_111_100, 0b0_100_011_011, 0b0_110_111_100, 0b0_111_101_111, 0b0_111_001_001,
                     0b1_101_011_111, 0b0_001_110_001, 0b1_000_111_011, 0b0_001_001_001, 0b0_010_001_100, 0b1_011_111_000,
                     0b0_100_011_111, 0b0_011_001_001, 0b1_001_100_010, 0b0_011_111_010, 0b0_010_111_100, 0b0_110_111_011,
                     0b0_001_001_000, 0b0_010_111_001, 0b1_100_110_001, 0b0_001_011_100, 0b0_010_110_101, 0b1_001_110_111,
                     0b1_010_010_100, 0b0_011_011_111, 0b0_010_101_011, 0b1_011_101_000, 0b0_010_101_100, 0b1_001_000_010,
                     0b1_101_010_100, 0b1_000_100_000, 0b0_101_010_101, 0b0_010_100_001, 0b0_110_110_001, 0b0_111_101_001,
                     0b0_100_100_000, 0b0_011_100_111, 0b0_110_000_010, 0b0_100_110_001, 0b0_011_110_010, 0b0_110_100_010,
                     0b1_010_001_010, 0b1_000_001_011, 0b0_011_110_000, 0b1_011_100_001, 0b0_110_111_000, 0b1_100_111_010,
                     0b0_101_000_101, 0b1_001_010_100, 0b1_011_011_010, 0b1_100_100_111, 0b0_111_011_001, 0b1_011_001_010,
                     0b0_010_100_000, 0b1_010_111_101, 0b0_110_101_001, 0b0_011_100_001, 0b0_011_101_101, 0b1_000_111_010,
                     0b0_110_100_100, 0b0_101_011_000, 0b1_001_010_011, 0b0_010_100_100, 0b1_100_100_101, 0b1_010_111_000,
                     0b1_100_111_011, 0b0_101_111_011, 0b0_110_010_111, 0b1_101_000_011, 0b1_000_011_110, 0b0_011_100_000,
                     0b1_011_110_000, 0b1_100_011_100, 0b1_001_000_000, 0b0_011_110_001, 0b1_100_000_111, 0b1_100_000_011,
                     0b1_101_011_000, 0b1_101_010_110, 0b0_111_000_101, 0b0_011_101_100, 0b0_100_100_010, 0b1_010_000_100,
                     0b0_100_111_111, 0b0_111_010_010, 0b1_011_000_010, 0b1_101_010_010, 0b0_010_010_100, 0b0_010_111_010,
                     0b0_001_100_011, 0b0_110_111_010, 0b1_011_000_000, 0b0_110_010_010, 0b1_001_010_001, 0b1_100_001_110,
                     0b1_000_010_011, 0b0_001_101_101, 0b0_011_011_101, 0b0_010_101_010, 0b1_000_001_110, 0b0_110_010_000,
                     0b0_100_100_011, 0b0_011_110_110, 0b0_100_110_011, 0b0_111_010_101, 0b1_011_000_011, 0b0_100_011_000,
                     0b0_101_110_100, 0b0_100_100_101, 0b0_011_100_010, 0b0_010_001_000, 0b0_111_011_110, 0b1_001_010_010,
                     0b0_010_000_001, 0b0_111_000_010, 0b1_000_010_010, 0b1_101_101_111, 0b0_001_111_110, 0b1_101_000_101,
                     0b1_010_111_011, 0b0_001_101_000, 0b1_011_110_010, 0b0_110_011_011, 0b0_011_001_101, 0b1_011_110_111,
                     0b1_001_101_011, 0b0_101_001_100, 0b1_101_111_101, 0b1_100_010_001, 0b1_000_011_000, 0b0_110_001_011,
                     0b0_001_010_011, 0b0_100_110_111, 0b0_111_000_001, 0b1_000_010_111, 0b0_111_011_111, 0b0_100_000_110,
                     0b1_010_011_001, 0b1_010_000_111, 0b1_010_110_111, 0b1_011_101_011, 0b1_001_011_111, 0b1_100_011_011,
                     0b1_001_111_001, 0b0_100_000_010, 0b1_101_110_011, 0b1_011_111_111, 0b1_011_010_101, 0b0_010_001_101,
                     0b1_101_100_010, 0b0_100_001_011, 0b0_001_010_111, 0b1_001_010_110, 0b0_011_111_011, 0b0_100_000_100,
                     0b1_001_011_001, 0b1_101_001_100, 0b0_111_011_011, 0b0_101_100_010, 0b1_000_000_101, 0b1_001_110_010,
                     0b1_001_001_011, 0b1_000_010_110, 0b1_001_110_110, 0b1_000_001_010, 0b0_011_111_110, 0b0_000_111_101,
                     0b1_001_100_110, 0b0_111_000_100, 0b0_110_011_101, 0b0_111_100_110, 0b0_111_011_100, 0b0_011_001_110,
                     0b1_100_001_100, 0b0_110_000_101, 0b1_010_001_101, 0b0_100_110_101, 0b0_110_001_001, 0b1_011_001_100,
                     0b0_100_000_101, 0b0_001_101_111, 0b1_101_011_010, 0b1_100_010_101, 0b1_010_110_101, 0b1_100_101_101,
                     0b0_101_111_000, 0b0_111_001_000, 0b0_101_000_100, 0b1_001_100_000, 0b0_111_110_001, 0b1_000_111_110,
                     0b0_100_010_100, 0b0_101_010_001, 0b0_100_001_101, 0b1_011_111_011, 0b0_010_000_000, 0b1_010_100_110,
                     0b0_010_011_111, 0b1_100_010_100, 0b0_010_101_111, 0b0_010_010_011, 0b0_110_011_100, 0b1_100_110_100,
                     0b0_010_110_010, 0b0_101_000_000, 0b0_001_110_010, 0b1_001_110_100, 0b1_000_011_011, 0b0_011_000_100,
                     0b1_100_111_000, 0b0_001_101_110, 0b1_001_001_010, 0b0_010_000_110, 0b0_011_000_011, 0b1_011_000_101,
                     0b1_010_011_101, 0b1_101_100_001, 0b1_001_011_011, 0b1_110_000_001, 0b0_111_000_011, 0b1_101_000_100,
                     0b0_010_101_001, 0b0_001_101_100, 0b1_101_110_100, 0b0_111_101_010, 0b0_100_100_110, 0b1_010_100_111,
                     0b1_011_101_001, 0b1_001_100_101, 0b0_100_010_110, 0b0_101_001_010, 0b0_011_011_011, 0b1_001_101_001,
                     0b0_001_101_011, 0b1_010_011_010, 0b0_010_110_011, 0b1_000_101_111, 0b1_011_000_001, 0b1_100_101_000,
                     0b1_000_100_101, 0b0_101_100_101, 0b0_010_101_110, 0b1_101_011_110, 0b0_110_101_010, 0b1_010_010_110,
                     0b1_010_010_001, 0b1_000_110_111, 0b1_101_111_111, 0b0_110_111_111, 0b0_110_101_100, 0b0_010_011_001,
                     0b1_100_101_011, 0b0_100_111_001, 0b1_101_001_011, 0b1_101_000_110, 0b1_001_101_101, 0b1_100_111_110,
                     0b0_110_010_011, 0b1_011_100_100, 0b1_000_110_001, 0b1_001_111_011, 0b1_100_110_111, 0b1_010_110_011,
                     0b0_101_011_100, 0b1_100_001_101, 0b0_010_111_110, 0b1_101_011_101, 0b0_101_000_001, 0b0_101_110_011,
                     0b0_010_100_111, 0b1_001_001_111, 0b1_000_000_110, 0b1_010_011_100, 0b0_101_000_010, 0b0_011_100_110,
                     0b1_000_011_101, 0b0_010_001_011, 0b0_011_010_011, 0b1_100_011_101, 0b0_101_100_100, 0b0_001_100_000,
                     0b0_111_011_010, 0b1_100_000_001, 0b1_100_010_010, 0b0_111_110_000, 0b0_001_000_111, 0b0_101_011_111,
                     0b1_011_001_011, 0b0_101_110_110, 0b0_111_010_100, 0b1_100_011_110, 0b0_100_101_000, 0b1_011_011_000,
                     0b0_010_010_001, 0b1_101_100_110, 0b0_100_001_100, 0b1_000_110_000, 0b0_001_001_011, 0b1_011_001_000,
                     0b0_111_111_100, 0b0_011_001_010, 0b1_011_111_010, 0b0_111_100_001, 0b1_001_101_100, 0b1_010_100_010,
                     0b1_100_000_110, 0b1_010_100_000, 0b0_100_110_010, 0b1_101_100_100, 0b1_010_011_000, 0b1_000_101_001,
                     0b1_110_001_000, 0b1_101_110_000, 0b0_010_100_011, 0b1_000_000_000, 0b1_100_101_111, 0b0_001_010_101,
                     0b0_111_010_001, 0b0_010_100_010, 0b1_010_101_110, 0b0_110_011_111, 0b1_001_100_011, 0b1_001_011_010,
                     0b0_010_011_100, 0b0_101_001_001, 0b1_000_000_001, 0b0_000_111_011, 0b0_100_010_111, 0b1_011_100_111,
                     0b1_011_011_100, 0b0_010_100_110, 0b1_001_001_000, 0b0_011_000_010, 0b0_111_111_101, 0b0_111_010_011,
                     0b0_101_011_011, 0b1_000_110_101, 0b0_010_110_110, 0b0_100_010_001, 0b1_100_110_011, 0b1_110_000_010,
                     0b1_110_000_111, 0b0_100_001_110, 0b1_010_001_111, 0b1_000_101_110, 0b0_101_011_010, 0b1_010_000_010,
                     0b0_101_111_111, 0b1_000_111_101, 0b0_111_001_010, 0b0_001_011_110, 0b0_110_110_110, 0b1_011_101_110,
                     0b0_011_000_101, 0b1_101_101_010, 0b0_101_101_101, 0b1_100_100_100, 0b0_110_111_110, 0b0_110_110_100,
                     0b1_010_110_110, 0b0_010_001_010, 0b1_001_000_100, 0b1_010_111_110, 0b0_011_010_010, 0b0_101_111_010,
                     0b1_001_110_011, 0b0_000_110_111, 0b0_101_100_111, 0b1_000_100_110, 0b1_011_000_100, 0b0_011_101_001,
                     0b1_010_010_011, 0b0_101_100_011, 0b0_010_100_101, 0b0_111_100_010, 0b0_001_100_010, 0b0_101_001_110,
                     0b1_011_010_110, 0b0_110_101_110, 0b0_001_100_101, 0b0_010_101_101, 0b0_010_011_101, 0b1_100_100_000,
                     0b1_010_011_111, 0b1_011_111_110, 0b1_010_001_011, 0b0_101_100_110, 0b1_101_010_101, 0b1_000_001_001,
                     0b0_111_001_110, 0b0_100_111_011, 0b1_100_001_000, 0b1_010_001_001, 0b0_010_111_000, 0b1_000_011_100,
                     0b1_001_111_110, 0b0_101_011_101, 0b0_101_110_001, 0b0_010_010_000, 0b1_011_010_100, 0b1_011_000_111,
                     0b1_011_001_111, 0b1_001_101_010, 0b1_001_111_010, 0b0_001_000_011, 0b1_011_001_001, 0b0_001_001_110,
                     0b1_011_110_100, 0b0_011_010_101, 0b0_111_011_000, 0b1_110_000_000, 0b1_100_001_010, 0b1_101_001_101,
                     0b1_010_110_010, 0b0_001_001_101, 0b0_001_010_100, 0b1_100_001_001, 0b1_010_101_101, 0b0_101_101_011,
                     0b1_100_011_001, 0b0_101_110_000, 0b1_000_100_111, 0b1_101_001_001, 0b1_001_011_110, 0b0_111_100_111,
                     0b1_000_011_111, 0b0_011_101_010, 0b1_011_111_001, 0b0_011_000_111, 0b0_001_111_000, 0b0_111_101_101,
                     0b1_110_001_010, 0b0_101_010_011, 0b1_001_001_100, 0b1_001_111_100, 0b1_100_101_110, 0b0_010_110_111,
                     0b0_100_101_110, 0b1_011_100_000, 0b0_111_000_111, 0b0_111_110_010, 0b0_110_110_010, 0b0_010_000_111,
                     0b0_000_111_010, 0b0_101_101_110, 0b1_110_000_011, 0b0_001_011_011, 0b1_100_101_100, 0b0_011_001_111,
                     0b0_100_100_100, 0b0_010_010_110, 0b0_101_000_111, 0b1_001_000_001, 0b1_011_011_110, 0b0_001_010_110,
                     0b1_011_110_001, 0b1_000_100_011, 0b0_110_001_010, 0b1_000_011_001, 0b1_010_100_011, 0b0_011_110_011,
                     0b0_111_100_011, 0b0_111_101_110, 0b1_100_000_101, 0b0_100_011_001, 0b0_001_110_111, 0b1_100_011_111,
                     0b0_110_101_111, 0b0_111_111_011, 0b0_001_011_010, 0b1_010_001_000, 0b0_011_010_001, 0b0_011_110_111,
                     0b0_100_111_101, 0b0_101_001_011, 0b1_100_101_010, 0b0_100_101_111, 0b1_010_111_010, 0b0_001_110_110,
                     0b1_011_100_010, 0b1_100_100_110, 0b0_001_100_110, 0b1_010_011_011, 0b1_100_011_000, 0b1_011_101_010,
                     0b1_001_001_001, 0b1_010_000_101, 0b0_011_111_001, 0b1_001_111_111, 0b0_110_011_000, 0b0_101_010_111,
                     0b0_101_101_001, 0b0_110_011_110, 0b0_010_101_000, 0b1_000_101_011, 0b0_101_111_110, 0b0_100_101_011,
                     0b0_100_111_110, 0b0_010_111_101, 0b1_100_001_011, 0b0_100_011_100, 0b0_011_010_111};

    public static void main(String... args) {
        var maxID = PASSES[0];
        var minID = PASSES[0];
        for (int seatID : PASSES) {
            if (seatID > maxID) {
                maxID = seatID;
            } else if (seatID < minID) {
                minID = seatID;
            }
        }
        Arrays.sort(PASSES);
        var min = minID;
        var max = maxID;
        while (min + 1 != max) {
            var mid    = (min + max) >> 1;
            var offset = PASSES[mid - minID] - mid;
            if (offset == 0) {
                min = mid;
            } else if (offset > 0) {
                max = mid;
            } else {
                assert false;
            }
        }
        System.out.println("your seat is " + max);
    }
}
