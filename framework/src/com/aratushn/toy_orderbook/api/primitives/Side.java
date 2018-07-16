package com.aratushn.toy_orderbook.api.primitives;

/** Side of the market */
public enum Side {
    BUY, SELL;

    public Side opposite() {
        return this == BUY ? SELL : BUY;
    }
}
