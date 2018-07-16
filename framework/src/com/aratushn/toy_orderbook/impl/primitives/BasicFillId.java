package com.aratushn.toy_orderbook.impl.primitives;

import com.aratushn.toy_orderbook.api.primitives.FillId;
import com.aratushn.toy_orderbook.api.primitives.OrderId;

import javax.annotation.concurrent.Immutable;

/**
 * Naive int based impl of FillId
 */
@Immutable
public class BasicFillId implements FillId {
    // real life would probably require source system id as well
    private final OrderId orderId;
    private final long seqNum;

    public BasicFillId(OrderId orderId, long seqNum) {
        this.orderId = orderId;
        this.seqNum = seqNum;
    }

    @Override
    public String toString() {
        return "FillId#" + orderId + "#" + seqNum;
    }
}
