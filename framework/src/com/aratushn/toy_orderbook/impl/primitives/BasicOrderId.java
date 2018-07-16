package com.aratushn.toy_orderbook.impl.primitives;

import com.aratushn.toy_orderbook.api.primitives.OrderId;

import javax.annotation.concurrent.Immutable;

/**
 * Naive int based impl of OrderId
 */
@Immutable
public class BasicOrderId implements OrderId {
    // real life would probably require source system id as well
    private final long seqNum;

    public BasicOrderId(long seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public String toString() {
        return "OrderId#" + seqNum;
    }
}
