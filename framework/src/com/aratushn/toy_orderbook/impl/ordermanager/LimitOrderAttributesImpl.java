package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.orders.LimitOrderAttributes;
import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.primitives.Side;
import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;

@Immutable
public class LimitOrderAttributesImpl implements LimitOrderAttributes {
    private final Instrument instrument;
    private final Side side;
    private final Price limitPrice;
    private final Quantity qty;

    public LimitOrderAttributesImpl(Instrument instrument, Side side, Price limitPrice, Quantity qty) {
        this.instrument = instrument;
        this.side = side;
        this.limitPrice = limitPrice;
        this.qty = qty;
    }

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public Price getLimitPrice() {
        return limitPrice;
    }

    @Override
    public Quantity getSizeUnsigned() {
        return qty;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrument", instrument)
                .add("side", side)
                .add("limitPrice", limitPrice)
                .add("qty", qty)
                .toString();
    }
}
