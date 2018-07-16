package com.aratushn.toy_orderbook;

import com.aratushn.toy_orderbook.api.events.OrderEventSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.orders.LimitOrderAttributes;
import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.util.SubscriptionStub;

public interface OrderBook {
    Instrument getInstrument();
    Quote getCurrentQuote();

    /**
     * Build an order for this book. Does not yet submit this order
     *
     * Note: I've made a decision to introduce this step because the submitter of the order should not concern itself
     * with the mechanics of how the order state is kept.
     *
     * @param attributes attributes of the order that is being build
     * @return an order object in a virgin state
     */
    LimitOrder build(LimitOrderAttributes attributes);

    void place(LimitOrder order);
    void cancel(LimitOrder order);

    SubscriptionStub addEventSubscriber(OrderEventSubscriber subscriber);

    SubscriptionStub addEventSubscriber(MarketDataSubscriber subscriber);
}
