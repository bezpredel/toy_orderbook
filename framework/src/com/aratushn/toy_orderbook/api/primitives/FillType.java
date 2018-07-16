package com.aratushn.toy_orderbook.api.primitives;

/**
 * Identifies whether fill was of a posted order or an aggression.
 *
 * Note that in an auction model the distinction won't necessarily exist, so more values may need to be added.
 */
public enum FillType {
    /**
     * Fill of an order that has been previously posted in the book
     */
    MAKER,
    /**
     * Fill of an order that is aggressing on an order that existed previously
     */
    TAKER
}
