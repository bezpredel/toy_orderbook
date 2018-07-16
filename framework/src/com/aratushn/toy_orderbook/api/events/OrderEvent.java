package com.aratushn.toy_orderbook.api.events;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;

import java.time.Instant;

/**
 * An event on a limit order
 */
public interface OrderEvent {
    LimitOrder getOrder();
    OrderEventType getType();
    Instant getEventTime();

    <T> T visit(OrderEventVisitor<T> visitor);
}
