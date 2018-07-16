package com.aratushn.toy_orderbook.api.primitives;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents quantity (of an order, a fill, or a school of imaginary fish)
 *
 * On the one hand, one could just use longs for quantity. It would be more GC-friendly and easier to deal with.
 * On the other hand, not everything is traded in integer units, so there is some value in abstracting it out. Plus it
 * is nice to imagine a world where things are strongly typed.
 *
 * <p/>
 * NOTE: Only quantities of compatible types can be compared or added/subtracted
 * <p/>
 * NOTE: Negative quantity should probably not be defined.
 */
@Immutable
public interface Quantity extends Comparable<Quantity> {
    /**
     * @return quantity that is a sum of this quantity and that quantity
     */
    @Nonnull Quantity plus(@Nonnull Quantity that);

    /**
     * @return quantity that is a difference of this quantity and that quantity.
     */
    @Nonnull Quantity minus(@Nonnull Quantity that);

    /**
     * @return true if this value is zero
     */
    boolean isZero();

    /**
     * @return smaller of the two arguments
     */
    public static @Nonnull Quantity min(@Nonnull Quantity a, @Nonnull Quantity b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    /**
     * @return bigger of the two arguments
     */
    public static @Nonnull Quantity max(@Nonnull Quantity a, @Nonnull Quantity b) {
        return a.compareTo(b) > 0 ? a : b;
    }
}
