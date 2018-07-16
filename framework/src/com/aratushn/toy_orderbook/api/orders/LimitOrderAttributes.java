package com.aratushn.toy_orderbook.api.orders;

import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.primitives.Side;

/**
 * Immutable attributes that define an order
 *
 * (nb in the world of cancel/replace and cancel/reduce the replaced order is a new order)
 */
public interface LimitOrderAttributes {
    // there could also be TimeInForce. Implied in the probem is "good till cancel"

    /**
     * @return instrument for which the order is created
     */
    Instrument getInstrument();

    /**
     * @return side of the order
     */
    Side getSide();
    /**
     * @return limit price of the order
     */
    Price getLimitPrice();

    /**
     * @return quantity of the order
     */
    Quantity getSizeUnsigned();
}
