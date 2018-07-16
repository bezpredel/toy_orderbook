package com.aratushn.toy_orderbook.runner;

import com.aratushn.toy_orderbook.OrderBook;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.marketdata.Trade;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.impl.ordermanager.LimitOrderAttributesImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ScriptRunner {
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<ScriptParser.OrderAction> inputs;
    private final OrderBook orderBook;

    private ScriptParser.OrderAction lastAction;

    private final Map<String, LimitOrder> ordersByKey = new HashMap<>();

    public ScriptRunner(List<ScriptParser.OrderAction> inputs, OrderBook orderBook) {
        this.inputs = new LinkedList<>(inputs);
        this.orderBook = orderBook;
    }



    /**
     *
     * @return null if matches, error if not
     */
    String checkQuote(Quote quote) {
        ScriptParser.ExpectedQuote expected = lastAction.expectedQuote;

        if (expected == null) return null;

        boolean mismatch = compareSide(quote.getBestBid(), expected.expectedBid)
                || compareSide(quote.getBestOffer(), expected.expectedOffer);

        if (mismatch) {
            return String.format("expected [%s], actual [%s]", expected.toShortString(), quote.toShortString());
        } else {
            return null;
        }
    }
    private boolean compareSide(SideQuote actual, ScriptParser.ExpectedQuote.ExpectedQuoteSide expected) {
        if (actual == null) {
            return expected != null;
        } else {
            return expected == null
                    || !Objects.equals(actual.getQuantity(), expected.expectedSize)
                    || !Objects.equals(actual.getPrice(), expected.expectedPrice);
        }
    }

    public List<ScriptParser.OrderAction> getInputs() {
        return inputs;
    }

    public boolean hasNext() {
        return !inputs.isEmpty();
    }

    public void next() {
        ScriptParser.OrderAction next = inputs.remove(0);
        lastAction = next;

        switch (next.action) {
            case SEND: {
                LimitOrderAttributesImpl a = new LimitOrderAttributesImpl(
                        orderBook.getInstrument(),
                        next.side,
                        next.price,
                        next.qty
                );
                LimitOrder order = orderBook.build(a);
                
                if (next.refId != null) {
                    ordersByKey.put(next.refId, order);
                }
                orderBook.place(order);
                break;
            }
            case CANCEL: {
                LimitOrder order = ordersByKey.get(next.refId);
                if (order == null) throw new RuntimeException("Script error: cant cancel unknown order '" + next.refId + "'");
                orderBook.cancel(order);
            }

        }
    }
}
