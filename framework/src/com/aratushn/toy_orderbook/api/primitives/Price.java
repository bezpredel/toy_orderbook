package com.aratushn.toy_orderbook.api.primitives;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Represents a price of a limit order / trade
 */
@Immutable
public interface Price {
    /**
     * @param that price to compare to
     * @param side side for which the comparison is to be made
     * @return -1 if {@code this} price is less aggressive than {@code that} price,
     * +1 if {@code this} is more aggressive than {@code that} price, 0 if they are equal
     */
    int compareTo(@Nonnull Price that, @Nonnull Side side);

    /**
     * Price Comparator for a given side that sorts prices from least aggressive to most aggressive
     * (aggressiveness for bids is defined as higher bids are more aggressive, for offers lower offers are more aggressive)
     */
    static Comparator<Price> leastToMostAggressiveComparatorFor(@Nonnull Side side) {
        return side == Side.BUY
                ? (a, b) -> a.compareTo(b, Side.BUY)
                : (a, b) -> a.compareTo(b, Side.SELL);
    }

    /**
     * Price Comparator for a given side that sorts prices from most aggressive to least aggressive
     * (aggressiveness for bids is defined as higher bids are more aggressive, for offers lower offers are more aggressive)
     */
    public static Comparator<Price> mostToLeastAggressiveComparatorFor(@Nonnull Side side) {
        return side == Side.BUY
                ? (a, b) -> -a.compareTo(b, Side.BUY)
                : (a, b) -> -a.compareTo(b, Side.SELL);
    }
}
