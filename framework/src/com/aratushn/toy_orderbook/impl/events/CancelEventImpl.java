package com.aratushn.toy_orderbook.impl.events;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.events.OrderEventType;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.events.CancelEvent;
import com.aratushn.toy_orderbook.api.events.OrderEventVisitor;
import com.aratushn.toy_orderbook.impl.events.AbstractEvent;
import com.google.common.base.MoreObjects;

import java.time.Instant;

public class CancelEventImpl extends AbstractEvent implements CancelEvent {
    private final Quantity cancelledQuantity;
    private final Quantity cancelledDisplayedQuantity;

    public CancelEventImpl(LimitOrder order, Quantity cancelledQuantity, Quantity cancelledDisplayedQuantity, Instant cancelTime) {
        super(order, cancelTime);
        this.cancelledQuantity = cancelledQuantity;
        this.cancelledDisplayedQuantity = cancelledDisplayedQuantity;
    }

    @Override
    public OrderEventType getType() {
        return OrderEventType.CANCEL;
    }

    @Override
    public Quantity getCancelledQuantity() {
        return cancelledQuantity;
    }

    @Override
    public Quantity getCancelledDisplayedQuantity() {
        return cancelledDisplayedQuantity;
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
                .add("cancelledQuantity", cancelledQuantity)
                .add("cancelledDisplayedQuantity", cancelledDisplayedQuantity)
                .toString();
    }
}
