package com.aratushn.toy_orderbook.api.orders;

/**
 * Represents a status of an order at a particular point in time
 */
public enum OrderStatus {
    /** Order is new and not yet sent to the trading engine */
    PrePlacement,
    /** Order is being matched by the trading engine */
    Working,
    /** Order is posted passively in the book */
    PostedPassively,
    /** Order is fully filled */
    Filled,
    /* Order is fully cancelled */
    Cancelled,
    /* Order is partially filled, and the remainder is either posted passively, or is being worked */
    PartiallyFilled,
    /* Order is partially filled and then cancelled */
    PartiallyFilledAndCanceled
}
