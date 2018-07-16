package com.aratushn.toy_orderbook.api.marketdata;

import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;

/**
 * Represents best quote (price and total size at that price level) on one side of the market
 */
@Immutable
public interface SideQuote {
    /**
     * @return time this quote changed or became best quote. Note that this is not the age of this particular quote, ie
     * if there was a better price which got lifted, this timestamp will reflect the time when that better quote disappeared
     */
    @Nonnull Instant getQuoteTime();

    @Nonnull Price getPrice();

    /**
     * @return cumulative quantity on the book at the price level represented by {@link #getPrice()}
     */
    @Nonnull Quantity getQuantity();
}
