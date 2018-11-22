package com.byzantinegenerals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main class of the Byzantine Generals problem (algorithm).
 */
public class ByzantineGenerals {

    private static final Logger LOGGER = LogManager.getLogger(ByzantineGenerals.class);

    private final List<General> generals;
    private final Order commanderOrder;
    private final int recursionLevel;

    public ByzantineGenerals(List<General> generals, Order commanderOrder, int recursionLevel) {
        this.generals = generals;
        this.commanderOrder = commanderOrder;
        this.recursionLevel = recursionLevel;
    }

    // Utilized on unit tests
    List<General> getGenerals() {
        return generals;
    }

    /**
     * Executes the OM(m) Algorithm
     */
    public boolean executeAlgorithmOMm() {
        // Define the commander setting a order
        General commander = generals.get(0);
        commander.setOrder(commanderOrder);

        // call the algorithm (generals lists goes without the commander)
        algorithmOMm(commander, generals.subList(1, generals.size()), recursionLevel);

        // show the final results
        boolean result = executeOrders();
        LOGGER.info("Mission {}!", result ? "SUCCEEDED" : "FAILED");
        return result;
    }

    /**
     * Recursive Algorithm OM(m)
     *
     * @param commander Commander
     * @param lieutenants List of Lieutenants
     * @param m recursive level
     */
    void algorithmOMm(General commander, List<General> lieutenants, int m) {
        // OM(0)
        if (m == 0) {
            LOGGER.info("Executing m({}) for {}", m, commander);
            // (1) The commander sends his value to every lieutenant.
            // (2) Each lieutenant uses the value he receives from the commander, or uses the value RETREAT if he
            // receives no value.
            for (int i = 0; i < lieutenants.size(); i++) {
                commander.sendOrder(lieutenants.get(i), i+1);
            }
            return;
        }

        // OM(m), m > 0
        // (1) The commander sends his value to every lieutenant.
        LOGGER.info("Executing m({}), step 1 for {}", m, commander);
        for (int i = 0; i < lieutenants.size(); i++) {
            commander.sendOrder(lieutenants.get(i), i+1);
        }

        // (2) For each i, let vi be the value Lieutenant i receives from the commander, or else be RETREAT if he
        // receives no value. Lieutenant i acts as the commander in Algorithm OM(m - 1) to send the value vi to each
        // of the n - 2 other lieutenants.
        LOGGER.info("Executing m({}), step 2 for {}", m, commander);
        int mI = m - 1;                                                 // execute the algorithm for (m - 1)
        for (int i = 0; i < lieutenants.size(); i++) {
            General commanderI = lieutenants.get(i);                    // new commander 'i'
            List<General> lieutenantsI = lieutenants.stream()           // new lieutenants list,
                    .filter(general -> !general.equals(commanderI))     // without the new commander
                    .collect(Collectors.toList());
            algorithmOMm(commanderI, lieutenantsI, mI);
        }

        // (3) For each i, and each j != i, let vj be the value Lieutenant i received from Lieutenant j in step (2)
        // (using Algorithm OM(m - 1)), or else RETREAT if he received no such value. Lieutenant i uses the value
        // majority (vl, ..., vn-1 ).
        LOGGER.info("Executing m({}), step 3 for {}", m, commander);
        for (int i = 0; i < lieutenants.size(); i++) {
            lieutenants.get(i).majorityOrder();
        }
    }

    /**
     * Execute the generals orders returning if the mission was accomplished.
     * <p>The mission is considered accomplished when all generals execute the same order.
     *
     * @return true if the mission was accomplished, or false if it failed.
     */
    boolean executeOrders() {
        LOGGER.info("Executing orders...");

        Map<Order, Long> orderCounter = generals.stream()
                .map(General::executeOrder).collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        return orderCounter.getOrDefault(Order.ATTACK, 0L) > generals.size() / 2 ||
                orderCounter.getOrDefault(Order.RETREAT, 0L) > generals.size() / 2;
    }

    /**
     * Main method
     *
     * @param args 'm' 'g1,g2,g3,...,gn' 'o', where:
     *             <p>'m' is a Integer representing the level of recursion, assuming that m > 0
     *             <p>'g1,g2,g3,...,gn' is a String representing the list of general names, separated by comma ','
     *             <p>'o' is a String representing the commander order, that could be ATTACK or RETREAT
     *
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Execution:\n java -jar ByzantineGenerals <m> <g1,g2,g3,...,gn> <o>\nwhere:");
            System.err.println(" <m> is a Integer representing the level of recursion, assuming that m > 0");
            System.err.println(" <g1,g2,g3,...,gn> is a String representing the list of general names, separated by comma ','");
            System.err.println(" <o> is a String representing the commander order, that could be ATTACK or RETREAT");
            System.exit(1);
        }

        ByzantineGenerals byzantineGenerals = new Builder(args[1])
                .setCommanderOrder(args[2])
                .setRecursionLevel(args[0])
                .build();

        byzantineGenerals.executeAlgorithmOMm();
    }

    /**
     * Builds a ByzantineGenerals (Builder pattern)
     */
    static class Builder {
        private final List<String> generalNames;
        private final int totalGenerals;
        private Order commanderOrder;
        private int recursionLevel;

        public Builder(List<String> generalNames) {
            this.generalNames = generalNames;
            this.totalGenerals = generalNames.size();
        }

        public Builder(String generalNames) {
            this(Arrays.asList(generalNames.split(",")));
        }

        public Builder setCommanderOrder(Order commanderOrder) {
            this.commanderOrder = commanderOrder;
            return this;
        }

        public Builder setCommanderOrder(String commanderOrder) {
            return setCommanderOrder(Order.valueOf(commanderOrder));
        }

        public Builder setRecursionLevel(int recursionLevel) {
            this.recursionLevel = recursionLevel;
            return this;
        }

        public Builder setRecursionLevel(String recursionLevel) {
            return setRecursionLevel(Integer.parseInt(recursionLevel));
        }

        private List<String> generateTraitorNames(int numberTraitors) {
            List<String> traitors = new ArrayList<>();
            while (numberTraitors > 0) {
                String name = generalNames.get(ThreadLocalRandom.current().nextInt(0, totalGenerals));
                if (!traitors.contains(name)) {
                    traitors.add(name);
                    numberTraitors--;
                }
            }
            return traitors;
        }

        public ByzantineGenerals build() {
            List<General> generals = new ArrayList<>();
            List<String> traitors = generateTraitorNames(recursionLevel);
            // init generals
            for (int i = 0; i < totalGenerals; i++) {
                generals.add(new General(generalNames.get(i), !traitors.contains(generalNames.get(i))));
            }
            return new ByzantineGenerals(generals, commanderOrder, recursionLevel);
        }
    }
}
