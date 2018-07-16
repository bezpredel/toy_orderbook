package com.aratushn.toy_orderbook.impl.quotes;

import com.aratushn.toy_orderbook.api.events.*;
import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.primitives.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Stores price levels in two datastructures - a hash map for quick price lookup, and a heap for O(1) best price lookup
 * Because removal of arbitrary element from a heap is expensive, cleanup of finished levels is done lazily.
 *
 * There are more efficient ways to achieve more consistent and fast performance on inserts and deletes of
 * away-from-the-market orders, however they require more sophisticated (and potentially non-homogeneous) datastructures.
 * For now, this will have to do.
 */
class QuoteBookSide implements OrderEventVisitor<Void> {
    private final Instrument instrument;
    private final Side side;
    private final HashMap<Price, QuoteEntry> map;
    private final PriorityQueue<QuoteEntry> queue;

    /**
     * true = {@link #quote} needs to be recomputed
     */
    private boolean quoteDirty;
    /**
     * Current quote. Should not be null unless {@link #quoteDirty} is {@code true}
     */
    private SideQuote quote;

    private Instant quoteTimestamp;

    QuoteBookSide(Instrument instrument, Side side) {
        this.instrument = instrument;
        this.side = side;
        this.map = new HashMap<>();
        this.queue = new PriorityQueue<>(
                (o1, o2) -> (-o1.price.compareTo(o2.price, QuoteBookSide.this.side))
        );
        this.quoteDirty = true;
        this.quoteTimestamp = Instant.EPOCH; // never updated
    }

    boolean isDirty() {
        return quoteDirty;
    }

    SideQuote getBestQuote() {
        if (quoteDirty) {
            QuoteEntry top = getTop();

            if (top == null) {
                quote = new SideQuoteImpl(quoteTimestamp, null, instrument.getZeroQuantity());
            } else {
                quote = new SideQuoteImpl(quoteTimestamp, top.price, top.totalQuantity);
            }

            quoteDirty = false;
        }

        return quote;
    }

    private QuoteEntry getOrCreateEntry(Price limitPrice) {
        if (map.containsKey(limitPrice)) {
            return map.get(limitPrice);
        } else {
            QuoteEntry e = new QuoteEntry(limitPrice);
            map.put(limitPrice, e);
            queue.add(e);
            return e;
        }
    }

    private QuoteEntry getEntry(Price limitPrice) {
        return map.get(limitPrice);
    }

    private QuoteEntry getTop() {
        while (!queue.isEmpty()) {
            QuoteEntry top = queue.peek();
            assert top != null; // because queue is not empty

            if (top.totalQuantity.isZero()) {
                queue.poll();
                map.remove(top.price);
                continue;
            }

            return top;
        }
        return null;
    }

    private void updateDirtinessAndTimestamp(Price limitPrice, Instant time) {
        QuoteEntry top = getTop();

        if (top == null) {
            // there was some change to the book, and now there is no quote on this side at all. There must have
            // been some change
            quoteDirty = true;
            quote = null;
            quoteTimestamp = time;
        } else {
            // -1 = current top less aggressive than the order that has been touched
            // 1 = current top is more aggressive than the order that has been touched
            // 0 = current top is at te same price as the order that has been touched
            int cmp = top.price.compareTo(limitPrice, side);

            if (cmp <= 0) {
                // == means an order that belongs to the top quote has been touched
                // <  means an order above the current top has been touched (must have been removed).
                // In both cases it means the current top quote must have been updated due to this event,
                // so quoteTimestamp should be changed and quote dirtied
                quoteDirty = true;
                quote = null;
                quoteTimestamp = time;
            }
        }
    }

    @Override
    public Void visit(DisplayEvent event) {
        final Price limitPrice = event.getOrder().getOrderAttributes().getLimitPrice();
        final QuoteEntry entry = getOrCreateEntry(limitPrice);

        entry.process(event);

        updateDirtinessAndTimestamp(limitPrice, event.getEventTime());
        return null;
    }

    @Override
    public Void visit(CancelEvent event) {
        final Price limitPrice = event.getOrder().getOrderAttributes().getLimitPrice();
        final QuoteEntry entry = getEntry(limitPrice);
        if (entry == null) {
            return null;
        }

        entry.process(event);

        // at this point, entry may have no outstanding shares. Let's postpone cleanup till later anyway

        // lets update "quoteDirty" flag
        updateDirtinessAndTimestamp(limitPrice, event.getEventTime());
        return null;
    }

    @Override
    public Void visit(FillEvent event) {
        final Price limitPrice = event.getOrder().getOrderAttributes().getLimitPrice();
        final QuoteEntry entry = getEntry(limitPrice);
        if (entry == null) {
            return null;
        }

        entry.process(event);

        // at this point, entry may have no outstanding shares. Let's postpone cleanup till later anyway

        updateDirtinessAndTimestamp(limitPrice, event.getEventTime());
        return null;
    }

    @Override
    public Void visit(AcceptEvent event) {
        // do nothing
        return null;
    }

    /**
     * Entry
     */
    private static class QuoteEntry {
        private final Price price;

        private Quantity totalQuantity;

        QuoteEntry(Price price) {
            this.price = price;
        }

        void process(DisplayEvent event) {
            if (totalQuantity != null) {
                totalQuantity = totalQuantity.plus(event.getDisplayedQuantity());
            } else {
                totalQuantity = event.getDisplayedQuantity();
            }
        }

        void process(CancelEvent event) {
            if (event.getCancelledDisplayedQuantity().isZero()) {
                return;
            }

            if (totalQuantity != null) {
                totalQuantity = totalQuantity.minus(event.getCancelledDisplayedQuantity());
            } else {
                // todo: this is an error case, why are we cancelling uninitialized record
                assert false;
            }
        }

        void process(FillEvent event) {
            Fill fill = event.getFill();
            if (wasDisplayed(fill)) {
                if (totalQuantity != null) {
                    totalQuantity = totalQuantity.minus(fill.getFillQuantity());
                } else {
                    // todo: this is an error case, why is this fill marked as maker if the record is uninitialized?
                    assert false;
                }
            }
        }

        private boolean wasDisplayed(Fill fill) {
            return fill.getFillType() == FillType.MAKER;
        }
    }
}
