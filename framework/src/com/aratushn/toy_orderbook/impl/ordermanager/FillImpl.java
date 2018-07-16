package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.primitives.FillId;
import com.aratushn.toy_orderbook.api.primitives.FillType;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.google.common.base.MoreObjects;

import java.time.Instant;

class FillImpl implements Fill {
    private final Instant fillTime;
    private final LimitOrder order;
    private final FillId fillId;
    private final Price fillPrice;
    private final Quantity fillSize;
    private final FillType fillType;

    public FillImpl(Instant fillTime, LimitOrder order, FillId fillId, Price fillPrice, Quantity fillSize, FillType fillType) {
        this.fillTime = fillTime;
        this.order = order;
        this.fillId = fillId;
        this.fillPrice = fillPrice;
        this.fillSize = fillSize;
        this.fillType = fillType;
    }

    @Override
    public Instant getFillTime() {
        return fillTime;
    }

    @Override
    public LimitOrder getOrder() {
        return order;
    }

    @Override
    public FillId getFillId() {
        return fillId;
    }

    @Override
    public Price getFillPrice() {
        return fillPrice;
    }

    @Override
    public Quantity getFillQuantity() {
        return fillSize;
    }

    @Override
    public FillType getFillType() {
        return fillType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fillTime", fillTime)
                .add("fillId", fillId)
                .add("fillPrice", fillPrice)
                .add("fillSize", fillSize)
                .add("fillType", fillType)
                .add("order", order)
                .toString();
    }
}
