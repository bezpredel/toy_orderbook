package com.aratushn.toy_orderbook.api.events;

import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

/**
 * Event designates that the order has been filled
 */
public interface FillEvent extends OrderEvent {
    /**
     * @return corresponding fill
     */
    Fill getFill();

    /**
     * @return quantity that remains outstanding after this fill
     */
    Quantity getLeavesQuantity();
}
