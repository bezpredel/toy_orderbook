package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.primitives.FillId;
import com.aratushn.toy_orderbook.api.primitives.OrderId;
import com.aratushn.toy_orderbook.impl.primitives.BasicFillId;
import com.aratushn.toy_orderbook.impl.primitives.BasicOrderId;

// not thread-safe, in real life would be partitioned by thread, and ids would contain some part identifying individual fibers of execution
public class OrderIdProvider {
    private long nextOrderId;
    private long nextFillId;

    OrderId createOrderId() {
        return new BasicOrderId(nextOrderId++);
    }

    FillId createFillId(OrderId oid) {
        return new BasicFillId(oid, nextFillId++);
    }
}
