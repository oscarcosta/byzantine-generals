package com.byzantinegenerals;

import org.apache.commons.math3.util.Combinations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ByzantineGeneralsTest {

    /**
     * Build the ByzantineGenerals with the number of generals (totalGenerals), the commander order, the recursionLevel,
     * and (optionally) the list of traitors. If traitors are not specified, all generals are loyal.
     */
    private static ByzantineGenerals buildByzantineGenerals(int totalGenerals, Order order, int recursionLevel, int... traitors) {
        ByzantineGenerals byzantineGenerals = buildByzantineGeneralsWithRandomTraitors(totalGenerals, order, recursionLevel);

        List<Integer> traitorsList = Arrays.stream(traitors).boxed().collect(Collectors.toList());
        for (int i = 0; i < totalGenerals; i++) {
            byzantineGenerals.getGenerals().get(i).setLoyal(true);  // set all loyal
            if (traitorsList.contains(i)) {
                byzantineGenerals.getGenerals().get(i).setLoyal(false); // specify the traitor(s)
            }
        }

        return byzantineGenerals;
    }

    /**
     * Build the ByzantineGenerals with the number of generals (totalGenerals), the commander order, the recursionLevel.
     * <p>The number of traitors is equal the recursionLevel, random selected.
     */
    private static ByzantineGenerals buildByzantineGeneralsWithRandomTraitors(int totalGenerals, Order order, int recursionLevel) {
        return new ByzantineGenerals.Builder(IntStream.range(0, totalGenerals).mapToObj(i -> "G" + i).collect(Collectors.toList()))
                .setCommanderOrder(order)
                .setRecursionLevel(recursionLevel)
                .build();
    }

    /**
     * THEOREM 1. For any 'm', Algorithm OM(m) satisfies condition IC1 and IC2 if there are more than '3m' generals
     * and at most 'm' traitors.
     */
    private static int theoremOne(int m) {
        return 3 * m + 1;
    }

    /**
     * Generate the traitors lists, encapsulated in a Stream of arrays.
     * <p> Utilizes the Apache Common Math library to calculate the 'k-subsets' of 'n'.
     */
    private static Stream<int[]> generateTraitors(int n, int k) {
        return StreamSupport.stream(new Combinations(n, k).spliterator(), false);
    }

    @Test
    public void testAllLoyal() {
        ByzantineGenerals byzantineGenerals = buildByzantineGenerals(4, Order.ATTACK, 1);
        Assertions.assertTrue(byzantineGenerals.executeAlgorithmOMm());
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsForOneTraitor() {
        return IntStream.range(0, 4)      // generate tests for [0, 4) traitors 'i'
                .mapToObj(i -> DynamicTest.dynamicTest("traitor = " + i , () -> {
                    ByzantineGenerals byzantineGenerals  = buildByzantineGenerals(4, Order.ATTACK, 1, i);
                    Assertions.assertTrue(byzantineGenerals.executeAlgorithmOMm());
                }));
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsNGreaterThan3M() {
        return IntStream.range(1, 4).boxed()            // generate tests for [1, 4) 'm'
                .flatMap(m -> generateTraitors(theoremOne(m), m)   // and the 'm-subset' of 'n' generals, where 'm' is the recursion level
                        .map(traitors -> DynamicTest.dynamicTest("total generals = " + theoremOne(m) + ", m = " + m + ", traitors = " + Arrays.toString(traitors), () -> {
                            ByzantineGenerals byzantineGenerals  = buildByzantineGenerals(theoremOne(m), Order.ATTACK, m, traitors);
                            Assertions.assertTrue(byzantineGenerals.executeAlgorithmOMm());
                        })));
    }

}

