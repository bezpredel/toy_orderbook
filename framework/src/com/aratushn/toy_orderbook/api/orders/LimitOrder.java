package com.aratushn.toy_orderbook.api.orders;

import com.aratushn.toy_orderbook.api.primitives.OrderId;

/**
 * A limit order
 */
public interface LimitOrder {
    /**
     * @return identity of the order
     */
    OrderId getOrderId();

    /**
     * @return fundamental immutable properties of the order
     */
    LimitOrderAttributes getOrderAttributes();

    /**
     * @return current state of the order
     */
    OrderState getOrderState();
}
