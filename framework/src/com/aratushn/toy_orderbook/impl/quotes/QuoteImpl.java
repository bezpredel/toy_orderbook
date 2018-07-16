package com.aratushn.toy_orderbook.impl.quotes;

import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.marketdata.Quote;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;

@Immutable
class QuoteImpl implements Quote {
    private final Instant quoteTime;
    private final SideQuote bestBid;
    private final SideQuote bestOffer;

    QuoteImpl(Instant quoteTime, SideQuote bestBid, SideQuote bestOffer) {
        this.quoteTime = quoteTime;
        this.bestBid = bestBid;
        this.bestOffer = bestOffer;
    }

    @Override
    @Nonnull
    public Instant getQuoteTime() {
        return quoteTime;
    }

    @Override
    public SideQuote getBestBid() {
        return bestBid;
    }

    @Override
    public SideQuote getBestOffer() {
        return bestOffer;
    }

    public String toString() {
        return String.format(
                "%s: %15s x %-15s",
                quoteTime,
                (bestBid != null ? (bestBid.getQuantity() + "@" + bestBid.getPrice()) : "---"),
                (bestOffer != null ? (bestOffer.getQuantity() + "@" + bestOffer.getPrice()) : "---")
        );
    }
}
