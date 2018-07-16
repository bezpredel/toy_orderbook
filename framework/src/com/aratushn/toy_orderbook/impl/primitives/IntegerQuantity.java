package com.aratushn.toy_orderbook.impl.primitives;

import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Simple integer quantity
 */
@Immutable
public class IntegerQuantity implements Quantity {
    static final IntegerQuantity ZERO = new IntegerQuantity(0);

    private final long qty;

    public static Quantity valueOf(long qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be less than 0");
        } else if (qty == 0) {
            return ZERO;
        } else {
            return new IntegerQuantity(qty);
        }
    }

    private IntegerQuantity(long qty) {
        this.qty = qty;
    }

    @Nonnull
    @Override
    public Quantity plus(@Nonnull Quantity that) {
        return valueOf(this.qty + cast(that).qty);
    }

    @Nonnull
    @Override
    public Quantity minus(@Nonnull Quantity that) {
        return valueOf(this.qty - cast(that).qty);
    }

    @Override
    public boolean isZero() {
        return qty == 0;
    }

    @Override
    public int compareTo(@Nonnull Quantity that) {
        return Long.compare(this.qty, cast(that).qty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerQuantity that = (IntegerQuantity) o;
        return qty == that.qty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qty);
    }

    public String toString() {
        return Long.toString(qty);
    }

    private IntegerQuantity cast(Quantity that) {
        Objects.requireNonNull(that);
        Preconditions.checkArgument(that instanceof IntegerQuantity, "Incompatible quantity type");
        return (IntegerQuantity) that;
    }
}
