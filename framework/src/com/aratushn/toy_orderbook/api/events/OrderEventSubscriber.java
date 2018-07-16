package com.aratushn.toy_orderbook.api.events;


/**
 * Interface for listening to order events
 *
 * Note: Subscribers may not be notified of the events right away, and so if they call
 * {@code OrderEvent.getOrder().getOrderState()}, they may observe the state that has more than just this event applied.
 *
 * Also note that while the current design allows for the order actions to be taken from subscribers, it is probably not
 * optimal: event dispatch for market data and events is intermingled, and so there are no guarantees if proper relative
 * ordering. Also, there is no good control over stack depth. Perhaps a better design would be to make dispatch done on
 * a dedicated executor.
 *
 * TODO
 */
public interface OrderEventSubscriber {
    void onOrderChange(OrderEvent event);
}
