package com.aratushn.toy_orderbook.api.marketdata;

import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Represents a trade in the market.
 *
 * Note: A trade can only be between one buy order and one sell order. Ie, if bid A is for 1000 shares, and there are
 * two matching offers for 500 shares each, there will be 2 trades.
 */
public interface Trade {
    /** Time this trade has occurred */
    @Nonnull Instant getTradeTime();
    /** Price at which this trade has occurred */
    @Nonnull Price getTradePrice();
    /** How many shares have traded */
    @Nonnull Quantity getTradeQuantity();
    /** Buy leg of this trade */
    @Nonnull Fill getBuyer();
    /** Sell leg of this trade */
    @Nonnull Fill getSeller();
}
