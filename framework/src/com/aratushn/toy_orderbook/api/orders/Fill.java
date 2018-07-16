package com.aratushn.toy_orderbook.api.orders;

import com.aratushn.toy_orderbook.api.primitives.FillId;
import com.aratushn.toy_orderbook.api.primitives.FillType;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import java.time.Instant;

/**
 * Fill of an order
 */
public interface Fill {
    /** Time this fill happened */
    Instant getFillTime();
    /** Order this fill has occurred on */
    LimitOrder getOrder();
    /** Unique identifier of this fill */
    FillId getFillId();
    /** Price at which this fill has occurred*/
    Price getFillPrice();
    /** Quantity of this fill */
    Quantity getFillQuantity();
    /** How this fill occurred */
    FillType getFillType();
}
