package com.aratushn.toy_orderbook.impl.primitives;

import com.aratushn.toy_orderbook.api.primitives.Instrument;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import javax.annotation.concurrent.Immutable;

/**
 * Simple instrument implementation that has a name and uses {@link IntegerQuantity}
 */
@Immutable
public class SimpleInstrument implements Instrument {
    private final String name;

    public SimpleInstrument(String name) {
        this.name = name;
    }

    @Override
    public Quantity getZeroQuantity() {
        return IntegerQuantity.ZERO;
    }

    @Override
    public String toString() {
        return name;
    }
}
