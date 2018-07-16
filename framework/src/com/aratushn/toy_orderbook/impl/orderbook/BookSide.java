package com.aratushn.toy_orderbook.impl.orderbook;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.primitives.Price;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents one side of the {@link Book}
 */
public interface BookSide {
    void add(LimitOrder order);
    void remove(LimitOrder order);

    /**
     * Returns (and possibly removes) the next order for the purposes of matching
     *
     * @param upToPrice returned order price must be at least as aggressive as this
     * @return get the next (in price/time priority) outstanding order with the price at least as aggressive as "upToPrice", or null if no such order exists
     */
    @Nullable
    LimitOrder next(@Nonnull Price upToPrice);
}
