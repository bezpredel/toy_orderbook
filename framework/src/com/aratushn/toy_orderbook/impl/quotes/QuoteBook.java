package com.aratushn.toy_orderbook.impl.quotes;

import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.api.primitives.Side;
import com.aratushn.toy_orderbook.util.Comparables;

import java.time.Instant;

class QuoteBook {
    private final QuoteBookSide bids;
    private final QuoteBookSide offers;

    private Quote cached;

    QuoteBook(Instrument instrument) {
        bids = new QuoteBookSide(instrument, Side.BUY);
        offers = new QuoteBookSide(instrument, Side.SELL);
    }

    QuoteBookSide getSide(Side side) {
        return side == Side.BUY ? bids : offers;
    }

    boolean isDirty() {
        return bids.isDirty() || offers.isDirty();
    }

    Quote getQuote() {
        if (isDirty()) {
            final SideQuote bestBid = bids.getBestQuote();
            final SideQuote bestOffer = offers.getBestQuote();

            final Instant quoteTime = Comparables.max(bestBid.getQuoteTime(), bestOffer.getQuoteTime());

            cached = new QuoteImpl(
                    quoteTime,
                    wrap(bestBid),
                    wrap(bestOffer)
            );
        }

        return cached;
    }

    private SideQuote wrap(SideQuote quote) {
        return quote.getQuantity().isZero() ? null : quote;
    }
}
