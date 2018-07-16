package com.aratushn.toy_orderbook.api.events;

import com.aratushn.toy_orderbook.api.primitives.Quantity;

/**
 * Event designates that an order has been posted on the book.
 * This occurs if there is some quantity remaining after the order has been matched against the book.
 */
public interface DisplayEvent extends OrderEvent {
    /**
     * @return quantity that has been put on the book
     */
    Quantity getDisplayedQuantity();
}
