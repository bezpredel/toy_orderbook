package com.aratushn.toy_orderbook.impl.orderbook;

import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Side;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Naive implementation of {@link BookSide} that uses a {@link TreeMap} (O(log(n)) structure) to store price levels,
 * and an {@link ArrayDeque} to store orders per level.
 * This makes {@link #remove} operation {@code O(n) + O(log(m))} where {@code n} is a number of orders on a given level,
 * and {@code m} is a number of levels.
 */
class NaiveBookSideImpl implements BookSide {
    private final Side side;
    private final TreeMap<Price, Level> levels;

    NaiveBookSideImpl(Side side) {
        this.side = side;

        this.levels = new TreeMap<>(
                Price.mostToLeastAggressiveComparatorFor(side)
        );
    }

    @Override
    public void add(LimitOrder order) {
        if (!order.getOrderState().isOutstanding()) {
            return;
        }
        Price limitPrice = order.getOrderAttributes().getLimitPrice();
        Level level = levels.get(limitPrice);
        if (level == null) {
            levels.put(limitPrice, new Level(order));
        } else {
            level.add(order);
        }
    }

    @Override
    public void remove(LimitOrder order) {
        Price limitPrice = order.getOrderAttributes().getLimitPrice();
        Level level = levels.get(limitPrice);
        if (level == null) return;

        level.orders.remove(order);
    }

    @Override
    public LimitOrder next(@Nonnull Price upToPrice) {
        final Iterator<Map.Entry<Price, Level>> levelIterator = levels.entrySet().iterator();

        while (levelIterator.hasNext()) {
            final Map.Entry<Price, Level> mostAggressive = levelIterator.next();

            final Price mostAggressiveDisplayedPrice = mostAggressive.getKey();
            if (mostAggressiveDisplayedPrice.compareTo(upToPrice, side) < 0) {
                // the most aggressive book level is less aggressive than the given price limit
                return null;
            }

            final Level level = mostAggressive.getValue();

            while (!level.orders.isEmpty()) {
                LimitOrder o = level.orders.getFirst();
                if (!o.getOrderState().isOutstanding()) {
                    level.orders.removeFirst();
                    continue;
                }

                return o;
            }

            assert level.orders.isEmpty();
            levelIterator.remove();
        }

        // we ran out of order levels
        return null;
    }

    /**
     * Orders on one price level, in time order
     */
    private static class Level {
        final ArrayDeque<LimitOrder> orders;

        Level(LimitOrder order) {
            // time priority queue. since we control how orders enter the queu, we can rely on the fact that they get
            // added in time priority, and do not need to use a sorted datastructure
            orders = new ArrayDeque<>();
            orders.addLast(order);
        }

        void add(LimitOrder order) {
            orders.addLast(order);
        }
    }
}
