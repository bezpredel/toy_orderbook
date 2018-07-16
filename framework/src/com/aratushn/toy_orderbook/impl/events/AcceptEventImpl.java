package com.aratushn.toy_orderbook.impl.events;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.events.OrderEventType;
import com.aratushn.toy_orderbook.api.events.AcceptEvent;
import com.aratushn.toy_orderbook.api.events.OrderEventVisitor;
import com.aratushn.toy_orderbook.impl.events.AbstractEvent;
import com.google.common.base.MoreObjects;

import java.time.Instant;

public class AcceptEventImpl extends AbstractEvent implements AcceptEvent {
    public AcceptEventImpl(LimitOrder order) {
        super(order, order.getOrderState().getOrderTime());
    }

    @Override
    public OrderEventType getType() {
        return OrderEventType.ACCEPT;
    }

    @Override
    public <T> T visit(OrderEventVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", getOrder().getOrderId())
                .add("time", getEventTime())
                .toString();
    }
}
