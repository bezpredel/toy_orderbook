package com.aratushn.toy_orderbook.impl.orderbook;

import com.aratushn.toy_orderbook.api.primitives.Side;

/**
 * Book (the primary source of truth for the set of outstanding orders that currently exists in the system)
 */
public class Book {
    private final BookSide bids;
    private final BookSide offers;

    public Book() {
        bids = new NaiveBookSideImpl(Side.BUY);
        offers = new NaiveBookSideImpl(Side.SELL);
    }

    public BookSide getSide(Side side) {
        return side == Side.BUY ? bids : offers;
    }
}
