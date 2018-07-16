package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.events.OrderEventSubscriber;
import com.aratushn.toy_orderbook.util.SubscriptionStub;
import com.aratushn.toy_orderbook.api.events.OrderEvent;
import com.aratushn.toy_orderbook.util.SubscriptionHandler;

import java.util.ArrayDeque;

class OrderEventSubscriptionManager {
    private final SubscriptionHandler<OrderEventSubscriber> eventSubscribers = new SubscriptionHandler<>();

    private final ArrayDeque<OrderEvent> events = new ArrayDeque<>();

    SubscriptionStub subscribe(OrderEventSubscriber sub) {
        return eventSubscribers.subscribe(sub);
    }

    void queueEvent(OrderEvent event) {
        events.addLast(event);
    }

    void publishOrderEvents() {
        while (!events.isEmpty()) {
            eventSubscribers.dispatch(OrderEventSubscriber::onOrderChange, events.removeFirst());
        }
    }
}
