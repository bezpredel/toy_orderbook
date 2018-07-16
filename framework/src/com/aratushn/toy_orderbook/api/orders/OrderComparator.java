package com.aratushn.toy_orderbook.api.orders;

import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Side;

import java.time.Instant;
import java.util.Comparator;

public interface OrderComparator {
    Comparator<LimitOrder> BID = Comparator
            .<LimitOrder, Price>comparing(o -> o.getOrderAttributes().getLimitPrice(), Price.mostToLeastAggressiveComparatorFor(Side.BUY))
            .<Instant>thenComparing(o -> o.getOrderState().getOrderTime());
    Comparator<LimitOrder> OFFER = Comparator
            .<LimitOrder, Price>comparing(o -> o.getOrderAttributes().getLimitPrice(), Price.mostToLeastAggressiveComparatorFor(Side.SELL))
            .<Instant>thenComparing(o -> o.getOrderState().getOrderTime());

    static Comparator<LimitOrder> forSide(Side side) {
        return side == Side.BUY ? BID : OFFER;
    }
}
