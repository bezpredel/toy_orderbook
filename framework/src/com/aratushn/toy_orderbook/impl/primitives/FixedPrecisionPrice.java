package com.aratushn.toy_orderbook.impl.primitives;

import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Side;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Simple fixed-decimal-point price with a decimal precision that is underpinned by a long,
 * and is only compatible with prices of the same precision
 */
@Immutable
public class FixedPrecisionPrice implements Price {
    private final long price;
    private final byte precision;

    public FixedPrecisionPrice(long price, byte precision) {
        this.price = price;
        this.precision = precision;
    }

    private FixedPrecisionPrice cast(Price price) {
        Preconditions.checkArgument(price instanceof FixedPrecisionPrice, "Incomparable price types");
        FixedPrecisionPrice that = (FixedPrecisionPrice) price;

        // this is not fundamentally true, but makes things easier
        Preconditions.checkArgument(this.precision == that.precision, "Cannot compare prices of different precision");

        return that;
    }

    @Override
    public int compareTo(@Nonnull Price thatObj,@Nonnull Side side) {
        FixedPrecisionPrice that = cast(thatObj);

        if (side == Side.BUY) {
            return Long.compare(this.price, that.price);
        } else /* if (side == Side.SELL) */{
            return Long.compare(that.price, this.price);
        }
    }

    @Override
    public String toString() {
        // this is far from efficient but most expedient. NumberFormat would be another choice but its not thread-safe.
        // toString() method should not really be used for performance-critical paths anyway IMHO, as in general there is
        // no contract guaranteeing any performance characteristics
        return String.format("%." + precision + "f", price * Math.pow(10, -precision));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedPrecisionPrice that = (FixedPrecisionPrice) o;
        return price == that.price &&
                precision == that.precision;
    }

    @Override
    public int hashCode() {

        return Objects.hash(price, precision);
    }
}
