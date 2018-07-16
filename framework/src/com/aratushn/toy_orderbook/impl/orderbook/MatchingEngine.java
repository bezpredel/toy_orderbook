package com.aratushn.toy_orderbook.impl.orderbook;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

/**
 * Strategy how to match aggressive orders against book
 */
public class MatchingEngine {
    private final Instrument instrument;
    private final Book book;
    private final TradeBuilder tradeBuilder;

    public MatchingEngine(Instrument instrument, Book book, TradeBuilder tradeBuilder) {
        this.instrument = instrument;
        this.book = book;
        this.tradeBuilder = tradeBuilder;
    }

    public void match(LimitOrder takerOrder) {
        assert takerOrder.getOrderState().isOutstanding();

        final BookSide oppositeBookSide = book.getSide(takerOrder.getOrderAttributes().getSide().opposite());
        final Price limitPrice = takerOrder.getOrderAttributes().getLimitPrice();

        while (true) {
            final LimitOrder makerOrder = oppositeBookSide.next(limitPrice);

            if (makerOrder == null) {
                // Done matching
                break;
            }

            final Quantity matchQty = computeMatchQuantity(takerOrder, makerOrder);

            tradeBuilder.buildCross(takerOrder, makerOrder, matchQty);

            final boolean isMakerOrderFinished = makerOrder.getOrderState().getOutstandingQuantity().compareTo(zero()) <= 0;
            final boolean isTakerOrderFinished = takerOrder.getOrderState().getOutstandingQuantity().compareTo(zero()) <= 0;

            if (isMakerOrderFinished) {
                oppositeBookSide.remove(makerOrder);
            }

            if (isTakerOrderFinished) {
                // there is nothing more to do here, since the taker order is done
                return;
            } else {
                if (!isMakerOrderFinished) {
                    // this is an error condition, we must not continue, or there is a risk of an infinite loop:
                    // if the maker order is not finished, it will be returned again by oppositeBookSide.next()
                    break;
                }
            }
        }

        assert takerOrder.getOrderState().isOutstanding();
    }

    private Quantity computeMatchQuantity(LimitOrder a, LimitOrder b) {
        final Quantity takerOutstandingQty = a.getOrderState().getOutstandingQuantity();
        final Quantity makerOutstandingQty = b.getOrderState().getOutstandingQuantity();

        return Quantity.min(takerOutstandingQty, makerOutstandingQty);
    }

    private Quantity zero() {
        return instrument.getZeroQuantity();
    }

    /**
     * Pluggable strategy for actually building a trade
     */
    public interface TradeBuilder {
        /**
         * Build a cross for a given number of shares between given two crossing orders
         *
         * @param taker aggressive order
         * @param maker passive order
         * @param matchQty quantity
         */
        void buildCross(LimitOrder taker, LimitOrder maker, Quantity matchQty);
    }
}
