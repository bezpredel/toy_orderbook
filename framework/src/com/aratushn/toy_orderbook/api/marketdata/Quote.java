package com.aratushn.toy_orderbook.api.marketdata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;

/**
 * Snapshot in time of best bid and offer price and quantity
 */
@Immutable
public interface Quote {
    /**
     * @return time of the last quote update. Expect that subsequent quotes have timestamps monotonously increasing
     */
    @Nonnull Instant getQuoteTime();

    /**
     * @return best bid, or null if there is no bid
     */
    @Nullable
    SideQuote getBestBid();
    /**
     * @return best offer, or null if there is no offer
     */
    @Nullable
    SideQuote getBestOffer();

    default String toShortString() {
        return (getBestBid() != null ? getBestBid().getQuantity() + "@" + getBestBid().getPrice() : "--")
                + " x "
                + (getBestOffer() != null ? getBestOffer().getQuantity() + "@" + getBestOffer().getPrice() : "--");
    }
}
