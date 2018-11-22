package com.byzantinegenerals;

/**
 * Message to be send among generals
 */
public class Message {

    private final Order order;
    private final Officer sender;

    public Message(Order order, Officer sender) {
        this.order = order;
        this.sender = sender;
    }

    public Order getOrder() {
        return order;
    }

    public Officer getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "order=" + order +
                ", sender=" + sender +
                '}';
    }
}
