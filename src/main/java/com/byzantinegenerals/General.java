package com.byzantinegenerals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Defines the General.
 * <p>General implements Officer.
 */
public class General implements Officer {

    private static final Logger LOGGER = LogManager.getLogger(General.class);

    private String name;
    private boolean loyal;

    private TreeMap<String, Order> orders = new TreeMap<>();
    private Order order;

    /**
     * Constructs a General.
     *
     * @param name The Generals name.
     * @param loyal True if this General is loyal, or False if it is a traitor.
     */
    public General(String name, boolean loyal) {
        this.name = name;
        this.loyal = loyal;
    }

    @Override
    public String getName() {
        return name;
    }

    // Utilized on unit tests
    void setLoyal(boolean loyal) {
        this.loyal = loyal;
    }

    // Utilized on unit tests
    boolean isLoyal() {
        return loyal;
    }

    public Order getOrder() {
        return order != null ? order : Order.RETREAT;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Sends the order to a General (lieutenant).
     *
     * @param lieutenant The receiver of the order
     * @param index The index of the lieutenant receiving the order (used to scramble the order when the general is traitor)
     * @throws IllegalStateException If this general cannot send orders to lieutenant.
     */
    @Override
    public void sendOrder(Officer lieutenant, int index) {
        if (Objects.equals(name, lieutenant.getName())) {
            throw new IllegalStateException(String.format("%s CANNOT send orders to %s!", this.getName(), lieutenant.getName()));
        }

        Order order = scrambleOrder(getOrder(), index);

        //LOGGER.info("{} sent {} to {}", this.getName(), order, lieutenant.getName());
        lieutenant.receiveOrder(new Message(order, this));
    }

    /**
     * Scramble a order if this general is a traitor following the algorithm:
     * <p>If a general is a traitor, and it has to relay an order O to general Gi, it will relay the value of O if i is odd,
     * and it will send the opposite of O if i is even.
     * <p>Note that, in the case of a traitorous commander general, the order being relayed is still just Oc.
     * FIXME: if the traitorous commander general always send the same order, it would be the same if he was loyal.
     *
     * @param order The original order
     * @param index The index of the lieutenant receiving the order (used to scramble the order when the general is traitor)
     * @return The resultant order
     */
    private Order scrambleOrder(Order order, int index) {
        if (/*!isCommander() &&*/ !loyal && index % 2 != 0) {                   // if this /*is not commander and*/ is a traitor and index is even,
            return Order.ATTACK.equals(order) ? Order.RETREAT : Order.ATTACK;   // invert the order
        }
        return order;
    }

    /**
     * Receives a order encapsulated by a Message.
     *
     * @param message The received message.
     * @throws IllegalStateException If this general cannot receive orders from the (sender) general.
     */
    @Override
    public void receiveOrder(Message message) {
        if (Objects.equals(name, message.getSender().getName())) {
            throw new IllegalStateException(String.format("%s CANNOT receive orders from %s!", this.getName(), message.getSender().getName()));
        }

        LOGGER.info("{} received {} from {}", this.getName(), message.getOrder(), message.getSender().getName());
        if (orders.isEmpty()) { // first order from commander [OM(m), step 2]
            setOrder(message.getOrder());
        }
        orders.put(message.getSender().getName(), message.getOrder());
    }

    /**
     * Executes a order, using the majority algorithm to choose a order.
     *
     * @return The executed order.
     */
    public Order executeOrder() {
        Order order = getOrder();
        LOGGER.info("{} executed {}", this.getName(), order);
        return order;
    }

    /**
     * Calculates the majority of received orders.
     *
     * @return The selected order.
     */
    public Order majorityOrder() {
        // calculates the majority order
        Map<Order, Long> orderCounter = this.orders.values().stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        Order order = orderCounter.getOrDefault(Order.ATTACK, 0L) > orderCounter.getOrDefault(Order.RETREAT, 0L)
                ? Order.ATTACK : Order.RETREAT;

        LOGGER.info("{} calculated majority {} for orders {}", this.getName(), order, orders);
        setOrder(order); // majority order
        return order;
    }

    @Override
    public String toString() {
        return "General{" +
                "name='" + name + '\'' +
                ", loyal=" + loyal +
                ", orders=" + orders +
                ", order=" + order +
                '}';
    }
}
