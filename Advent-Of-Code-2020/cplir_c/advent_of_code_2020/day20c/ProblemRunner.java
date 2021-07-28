package cplir_c.advent_of_code_2020.day20c;

import cplir_c.advent_of_code_2020.Day20;


public class ProblemRunner {
    public static void main(String... args) {
        System.out.println(new TileProblem(Day20.EXAMPLE).getCornerProduct());
        System.out.println(new TileProblem(Day20.INPUT).getCornerProduct());
    }
}
