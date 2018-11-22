package com.byzantinegenerals;

/**
 * Defines a officer basic behaviour.
 */
public interface Officer {

    String getName();

    void sendOrder(Officer lieutenant, int index);

    void receiveOrder(Message message);

}
