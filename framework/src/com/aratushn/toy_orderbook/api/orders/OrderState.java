package com.aratushn.toy_orderbook.api.orders;

import com.aratushn.toy_orderbook.api.events.OrderEvent;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import java.time.Instant;
import java.util.List;

/**
 * Represents that state of an order that can change over time
 */
public interface OrderState {
    /**
     * @return time the order has been submitted
     */
    Instant getOrderTime();

    /**
     * @return describes status of the order
     */
    OrderStatus getStatus();

    /**
     * @return part of the order quantity that has not yet been filled or canceled
     */
    Quantity getOutstandingQuantity();

    /**
     * @return total quantity filled
     */
    Quantity getFilledQuantity();

    /**
     * @return total quantity cancelled
     */
    Quantity getCancelledQuantity();

    /**
     * @return whether the order has any more {@link #getOutstandingQuantity} left
     */
    boolean isOutstanding();

    /**
     * @return whether this order has been posted on the book yet (ie, if it has been made available for future orders to match against)
     */
    boolean isDisplayed();

    /**
     * @return a list of fills that have occurred thus far, in the order they have occurred
     */
    List<Fill> getFills();

    /**
     * @return a list of events that have occurred thus far, in the order they have occurred
     */
    List<OrderEvent> getEvents();
}
