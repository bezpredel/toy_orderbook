package com.aratushn.toy_orderbook.impl.events;

import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.events.OrderEventType;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.events.FillEvent;
import com.aratushn.toy_orderbook.api.events.OrderEventVisitor;
import com.aratushn.toy_orderbook.impl.events.AbstractEvent;
import com.google.common.base.MoreObjects;

public class FillEventImpl extends AbstractEvent implements FillEvent {
    private final Fill fill;
    private final Quantity leavesQuantity;

    public FillEventImpl(LimitOrder order, Fill fill, Quantity leavesQuantity) {
        super(order, fill.getFillTime());
        this.fill = fill;
        this.leavesQuantity = leavesQuantity;
    }

    @Override
    public OrderEventType getType() {
        return OrderEventType.FILL;
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public Quantity getLeavesQuantity() {
        return leavesQuantity;
    }

    @Override
    public <T> T visit(OrderEventVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", getOrder().getOrderId())
                .add("leavesQuantity", leavesQuantity)
                .add("fill", fill)
                .toString();
    }
}
