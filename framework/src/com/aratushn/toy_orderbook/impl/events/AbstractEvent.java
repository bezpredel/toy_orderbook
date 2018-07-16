package com.aratushn.toy_orderbook.impl.events;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.events.OrderEvent;

import java.time.Instant;

public abstract class AbstractEvent implements OrderEvent {

    private final LimitOrder order;
    private final Instant time;

    public AbstractEvent(LimitOrder order, Instant time) {
        this.order = order;
        this.time = time;
    }

    @Override
    public Instant getEventTime() {
        return time;
    }

    @Override
    public LimitOrder getOrder() {
        return order;
    }
}
