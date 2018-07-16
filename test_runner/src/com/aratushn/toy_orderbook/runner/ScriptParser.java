package com.aratushn.toy_orderbook.runner;

import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.primitives.Side;
import com.aratushn.toy_orderbook.impl.primitives.FixedPrecisionPrice;
import com.aratushn.toy_orderbook.impl.primitives.IntegerQuantity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptParser {
    public static final List<String> KNOWN_SCRIPT_NAMES = Arrays.asList(
            "scripts/scenario-1.txt",
            "scripts/scenario-2.txt"
    );

    public List<OrderAction> parse(String resource) {
        InputStream is = ScriptParser.class.getClassLoader().getResourceAsStream(resource);
        try {
            return parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<OrderAction> parse(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        ArrayList<OrderAction> retVal = new ArrayList<>();
        String s;
        while((s = r.readLine())!=null) {
            if (s.startsWith("#") || s.isEmpty()) {
                continue;
            }

            String[] parts = s.split("\\|");

            ExpectedQuote expectedQuote = null;

            if (parts.length > 1) {
                expectedQuote = parseExpectedQuote(parts[1].trim());
            }

            OrderAction action = parseOrderActionSpec(expectedQuote, parts[0].trim());
            retVal.add(action);
        }

        return retVal;
    }

    private ExpectedQuote parseExpectedQuote(String s) {
        String[] ss = s.split("x");
        if (ss.length != 2) {
            return null;
        }
        String bidS = ss[0].trim();
        String offerS = ss[1].trim();

        ExpectedQuote.ExpectedQuoteSide expectedBid = null;
        ExpectedQuote.ExpectedQuoteSide expectedOffer = null;

        if (bidS.contains("@")) {
            expectedBid = parseQuote(bidS);
        }
        if (offerS.contains("@")) {
            expectedOffer = parseQuote(offerS);
        }

        return new ExpectedQuote(expectedBid, expectedOffer);
    }

    /**
     * @return if input is in size@price format, parses it into quote side, otherwise returns null
     */
    private ExpectedQuote.ExpectedQuoteSide parseQuote(String quoteStr) {
        ExpectedQuote.ExpectedQuoteSide expectedBid;
        String[] ss = quoteStr.split("@");
        expectedBid = new ExpectedQuote.ExpectedQuoteSide(
                Long.parseLong(ss[0].trim()),
                Double.parseDouble(ss[1].trim())
        );
        return expectedBid;
    }

    private OrderAction parseOrderActionSpec(ExpectedQuote expectedQuote, String x) {
        String[] ss = x.split("\\s+");

        OrderAction action;

        if (ss[0].equalsIgnoreCase("CANCEL")) {
            action = new OrderAction(Action.CANCEL, 0, 0, null, ss[1], expectedQuote);
        } else {
            Side side = Side.valueOf(ss[0].toUpperCase());
            String[] sss = ss[1].split("@");
            int qty = Integer.parseInt(sss[0]);
            double price = Double.parseDouble(sss[1]);
            String label = ss.length > 2 ? ss[2] : null;

            action = new OrderAction(Action.SEND, qty, price, side, label, expectedQuote);
        }
        return action;
    }

    enum Action {
        SEND, CANCEL
    }

    public static class OrderAction {
        final Action action;
        final Quantity qty;
        final Price price;
        final Side side;

        final String refId;

        final ExpectedQuote expectedQuote;

        OrderAction(Action action, int qty, double price, Side side, String refId, ExpectedQuote expectedQuote) {
            this.action = action;
            this.qty = qty(qty);
            this.price = price(price);
            this.side = side;
            this.refId = refId;
            this.expectedQuote = expectedQuote;
        }

        @Override
        public String toString() {
            String s;
            if (action == Action.SEND) {
                s = action + " " + side + " " + qty + "@" + price + (refId != null ? " " + refId : "");
            } else {
                s = action + " " + refId;
            }

            if (expectedQuote != null) {
                s += " expecting quote " + expectedQuote;
            }

            return s;
        }
    }

    static class ExpectedQuote {
        final ExpectedQuoteSide expectedBid, expectedOffer;

        public ExpectedQuote(ExpectedQuoteSide expectedBid, ExpectedQuoteSide expectedOffer) {
            this.expectedBid = expectedBid;
            this.expectedOffer = expectedOffer;
        }

        static class ExpectedQuoteSide {
            final Quantity expectedSize;
            final Price expectedPrice;

            public ExpectedQuoteSide(long expectedSize, double expectedPrice) {
                this.expectedSize = qty(expectedSize);
                this.expectedPrice = price(expectedPrice);
            }
        }

        @Override
        public String toString() {
            return String.format(
                    "%15s x %-15s",
                    (expectedBid != null ? (expectedBid.expectedSize + "@" + expectedBid.expectedPrice) : "---"),
                    (expectedOffer != null ? (expectedOffer.expectedSize + "@" + expectedOffer.expectedPrice) : "---")
            );
        }

        public String toShortString() {
            return (expectedBid != null ? expectedBid.expectedSize + "@" + expectedBid.expectedPrice : "--")
                    + " x "
                    + (expectedOffer != null ? expectedOffer.expectedSize + "@" + expectedOffer.expectedPrice : "--");
        }
    }

    private static Price price(double price) {
        byte precision = 2;
        long roundedPrice = Math.round(price * 100);

        return new FixedPrecisionPrice(roundedPrice, precision);
    }

    private static Quantity qty(long qty) {
        return IntegerQuantity.valueOf(qty);
    }
}
