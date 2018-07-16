package com.aratushn.toy_orderbook.api.events;

public interface OrderEventVisitor<T> {
    T visit(AcceptEvent event);
    T visit(CancelEvent event);
    T visit(DisplayEvent event);
    T visit(FillEvent event);
}
