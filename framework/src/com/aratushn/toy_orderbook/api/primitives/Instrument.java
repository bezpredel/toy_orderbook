package com.aratushn.toy_orderbook.api.primitives;

import javax.annotation.concurrent.Immutable;

/**
 * Defines a thing that is being traded.
 *
 * The types of Quantity and Price are defined per instrument, and there is an OrderBook per instrument
 */
@Immutable
public interface Instrument {
    /**
     * Returns zero for whatever quantity units this instrument is traded in.
     * This is sort of inelegant, but effectively if a system supports more than one type of Quantity, it has to be per
     * instrument.
     */
    Quantity getZeroQuantity();
}
