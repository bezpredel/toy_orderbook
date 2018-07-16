package com.aratushn.toy_orderbook.api.events;

import com.aratushn.toy_orderbook.api.primitives.Quantity;

/**
 * Event designates that the order has been cancelled
 */
public interface CancelEvent extends OrderEvent {
    /**
     * @return total shares that have been cancelled in this event
     */
    Quantity getCancelledQuantity();

    /**
     * @return shares that have been cancelled that have been posted on the book prior to this cancel (for example, this
     * would be always 0 if an order was of "Immediate-or-cancel" variety)
     */
    Quantity getCancelledDisplayedQuantity();
}
