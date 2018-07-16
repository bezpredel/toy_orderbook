package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.util.SubscriptionStub;
import com.aratushn.toy_orderbook.api.marketdata.Trade;
import com.aratushn.toy_orderbook.impl.quotes.TopOfTheBookQuoteSource;
import com.aratushn.toy_orderbook.util.SubscriptionHandler;

import java.util.ArrayDeque;

class MarketDataSubscriptionManager {
    private final TopOfTheBookQuoteSource topOfTheBookQuoteSource;
    private final SubscriptionHandler<MarketDataSubscriber> marketDataSubscribers = new SubscriptionHandler<>();

    private final ArrayDeque<Trade> tradesQueue = new ArrayDeque<>();

    private Quote lastPublishedQuote;

    MarketDataSubscriptionManager(TopOfTheBookQuoteSource topOfTheBookQuoteSource) {
        this.topOfTheBookQuoteSource = topOfTheBookQuoteSource;
    }

    Quote getLastPublishedQuote() {
        return lastPublishedQuote;
    }

    void queueTrade(Trade trade) {
        tradesQueue.addLast(trade);
    }

    SubscriptionStub subscribe(MarketDataSubscriber subscriber) {
        return marketDataSubscribers.subscribe(subscriber);
    }

    void publishMarketDataUpdates() {
        while (!tradesQueue.isEmpty()) {
            marketDataSubscribers.dispatch(MarketDataSubscriber::onTrade, tradesQueue.removeFirst());
        }

        final Quote quote = topOfTheBookQuoteSource.getQuote();
        if (quote != lastPublishedQuote) {
            lastPublishedQuote = quote;
            marketDataSubscribers.dispatch(MarketDataSubscriber::onQuote, lastPublishedQuote);
        }
    }

}
