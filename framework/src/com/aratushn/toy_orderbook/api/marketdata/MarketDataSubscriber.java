package com.aratushn.toy_orderbook.api.marketdata;

/**
 * Subscriber for market data.
 */
public interface MarketDataSubscriber {
    /**
     * This method will be called on every trade
     */
    void onTrade(Trade trade);

    /**
     * This method will be called when the top quote price or quantity may have changed on one or both sides of the
     * market
     */
    void onQuote(Quote quote);
}
