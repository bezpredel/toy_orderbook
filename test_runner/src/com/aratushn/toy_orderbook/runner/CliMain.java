package com.aratushn.toy_orderbook.runner;

import com.aratushn.toy_orderbook.OrderBook;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.marketdata.Trade;
import com.aratushn.toy_orderbook.impl.ordermanager.OrderBookImpl;
import com.aratushn.toy_orderbook.impl.ordermanager.OrderIdProvider;
import com.aratushn.toy_orderbook.impl.primitives.SimpleInstrument;

import java.util.List;

public class CliMain {
    public static void main(String[] args) {
        List<ScriptParser.OrderAction> scenarios = new ScriptParser().parse("scripts/scenario-1.txt");
        OrderBook ob = new OrderBookImpl(
                new SimpleInstrument("FOO"),
                new OrderIdProvider()
        );

        ob.addEventSubscriber(
                event -> System.out.println("\t\tEVENT: " + event)
        );

        ScriptRunner scriptRunner = new ScriptRunner(scenarios, ob);

        ob.addEventSubscriber(new MarketDataSubscriber() {
            @Override
            public void onTrade(Trade trade) {
                System.out.println("TRADE: " + trade);
            }

            @Override
            public void onQuote(Quote quote) {
                System.out.println("QUOTE: " + quote);
                String error = scriptRunner.checkQuote(quote);

                if (error != null) {
                    System.err.println("\t\tMISMATCH: " + error);
                }
            }
        });



        while (scriptRunner.hasNext()) {
            scriptRunner.next();
        }
    }
}
