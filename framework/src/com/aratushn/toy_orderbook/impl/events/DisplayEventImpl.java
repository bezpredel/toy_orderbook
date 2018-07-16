package com.aratushn.toy_orderbook.impl.events;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.events.OrderEventType;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.events.DisplayEvent;
import com.aratushn.toy_orderbook.api.events.OrderEventVisitor;
import com.aratushn.toy_orderbook.impl.events.AbstractEvent;
import com.google.common.base.MoreObjects;

import java.time.Instant;

public class DisplayEventImpl extends AbstractEvent implements DisplayEvent {
    private final Quantity displayedQuantity;

    public DisplayEventImpl(LimitOrder order, Instant displayTime, Quantity displayedQuantity) {
        super(order, displayTime);
        this.displayedQuantity = displayedQuantity;
    }

    @Override
    public Quantity getDisplayedQuantity() {
        return displayedQuantity;
    }

    @Override
    public OrderEventType getType() {
        return OrderEventType.DISPLAY;
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
                .add("displayedQuantity", displayedQuantity)
                .toString();
    }
}
