package com.aratushn.toy_orderbook.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SubscriptionHandler<SUBSCRIPTION> {
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<SUBSCRIPTION> subs = new ArrayList<>();

    public <EVENT> void dispatch(BiConsumer<SUBSCRIPTION, EVENT> dispatcher, EVENT event) {
        // this is garbage-y, in a performance oriented system this would not be done
        //
        for (SUBSCRIPTION sub : subs) {
            try {
                dispatcher.accept(sub, event);
            } catch (Exception e) {
                LOGGER.error("Error while dispatching", e);
            }
        }
    }

    public SubscriptionStub subscribe(SUBSCRIPTION t) {
        subs.add(t);
        return () -> subs.removeIf(s -> s == t);
    }
}
