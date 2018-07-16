package com.aratushn.toy_orderbook.impl.quotes;

import com.aratushn.toy_orderbook.api.events.*;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.primitives.*;

/**
 * {@link OrderEvent} subscription based tracker for the top of the book quotes. This class does not read mutable state
 * of the order, so it can process events on a different timeline than that of the trading engine. However, it does
 * rely on order states being consistent, ie it will work incorrectly if there are overfills, or duplicate cancel events,
 * or some such
 */
public class TopOfTheBookQuoteSource implements OrderEventSubscriber {
    private final Instrument instrument;
    private final QuoteBook book;

    public TopOfTheBookQuoteSource(Instrument instrument) {
        this.book = new QuoteBook(instrument);
        this.instrument = instrument;
    }

    @Override
    public void onOrderChange(OrderEvent event) {
        assert instrument == event.getOrder().getOrderAttributes().getInstrument();

        QuoteBookSide bookSide = book.getSide(event.getOrder().getOrderAttributes().getSide());

        event.visit(bookSide);
    }

    /**
     * @return current quote
     */
    public Quote getQuote() {
        return book.getQuote();
    }

}
