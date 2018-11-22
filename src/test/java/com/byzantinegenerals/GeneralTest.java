package com.byzantinegenerals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneralTest {

    private static List<General> buildGeneralList(int totalGenerals) {
        return buildGeneralList(totalGenerals, -1);
    }

    private static List<General> buildGeneralList(int totalGenerals, Integer... traitorIndexes) {
        List<Integer> traitors = Arrays.asList(traitorIndexes);
        return IntStream.range(0, totalGenerals)
                .mapToObj(i -> new General("G" + i, !traitors.contains(i)))
                .collect(Collectors.toList());
    }

    @Test
    public void shouldCreateGeneral() {
        General general = new General("Test",true);

        Assertions.assertAll(
                () -> Assertions.assertEquals("Test", general.getName()),
                () -> Assertions.assertTrue(general.isLoyal()));
    }

    @Test
    public void shouldReceiveOrder() {
        List<General> generals = buildGeneralList(2);
        General commander = generals.get(0);

        generals.get(1).receiveOrder(new Message(Order.ATTACK, commander));

        Assertions.assertEquals(Order.ATTACK, generals.get(1).getOrder());
    }

    @Test
    public void shouldSendAOrderToLieutenants() {
        List<General> generals =  buildGeneralList(3);

        General commander = generals.get(0);
        commander.setOrder(Order.ATTACK);
        for (int i = 1; i < generals.size(); i++) {
            commander.sendOrder(generals.get(i), i - 1);
        }

        Assertions.assertTrue(generals.stream()
                .allMatch(general -> Objects.equals(Order.ATTACK, general.getOrder())));
    }

    @Test
    public void shouldSendScrambledOrdersToLieutenants() {
        List<General> generals =  buildGeneralList(3);

        General commander = generals.get(0);
        commander.setOrder(Order.ATTACK);
        commander.setLoyal(false);
        for (int i = 1; i < generals.size(); i++) {
            commander.sendOrder(generals.get(i), i - 1);
        }

        Assertions.assertTrue(generals.stream()
                .anyMatch(general -> Objects.equals(Order.ATTACK, general.getOrder())));
        Assertions.assertTrue(generals.stream()
                .anyMatch(general -> Objects.equals(Order.RETREAT, general.getOrder())));
    }

    @Test
    public void shouldCalculateMajorityForAllEqual() {
        List<General> generals =  buildGeneralList(4);

        General general = generals.get(0);
        for (int i = 1; i < generals.size(); i++) {
            generals.get(i).setOrder(Order.ATTACK);
            generals.get(i).sendOrder(general, i);
        }

        Assertions.assertEquals(Order.ATTACK, general.majorityOrder());
    }

    @Test
    public void shouldCalculateMajority() {
        List<General> generals =  buildGeneralList(4);

        General general = generals.get(0);
        for (int i = 1; i < generals.size(); i++) {
            generals.get(i).setOrder(i >= generals.size() / 2  ? Order.ATTACK : Order.RETREAT);
            generals.get(i).sendOrder(general, i);
        }

        Assertions.assertEquals(Order.ATTACK, general.majorityOrder());
    }

}