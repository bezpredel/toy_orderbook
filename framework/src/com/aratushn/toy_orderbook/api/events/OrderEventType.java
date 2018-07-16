package com.aratushn.toy_orderbook.api.events;

public enum OrderEventType {
    // in real world, there would probably be also CANCEL_REQUEST, ACCEPT, REJECT, CANCEL_REJECT; and maybe also BREAK, CORRECT, and DONT_KNOW?
    ACCEPT,
    DISPLAY,
    FILL,
    CANCEL
}
