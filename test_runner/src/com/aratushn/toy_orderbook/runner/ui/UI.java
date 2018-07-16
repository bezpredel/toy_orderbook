package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.impl.ordermanager.OrderBookImpl;
import com.aratushn.toy_orderbook.impl.ordermanager.OrderIdProvider;
import com.aratushn.toy_orderbook.impl.primitives.SimpleInstrument;
import com.aratushn.toy_orderbook.runner.ScriptParser;
import com.aratushn.toy_orderbook.runner.ScriptRunner;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UI extends JFrame {

    private final ScriptRunnerView runnerView;
    private final EventsView eventsView;

    public UI() {
        super("Test driver UI");

        ScriptSelectorView scriptSelectorView = new ScriptSelectorView(this::selectScript);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        eventsView = new EventsView();

        runnerView = new ScriptRunnerView(
                (e) -> eventsView.checkpoint(),
                (e) -> eventsView.refresh()
        );

        JPanel controllerPanel = new JPanel(new BorderLayout());
        controllerPanel.add(runnerView, BorderLayout.CENTER);
        controllerPanel.add(scriptSelectorView, BorderLayout.NORTH);

        selectScript(scriptSelectorView.getSelectedScript());

        this.getContentPane().add(eventsView, BorderLayout.CENTER);
        this.getContentPane().add(controllerPanel, BorderLayout.WEST);


        this.setSize(1200, 600);
        this.setVisible(true);
    }

    private void selectScript(String selectedScript) {
        TestSet testSet = new TestSet(selectedScript);
        runnerView.setRunner(testSet.scriptRunner);
        eventsView.replaceBook(testSet.book);
    }

    static class TestSet {
        private final OrderBookImpl book;
        private final ScriptRunner scriptRunner;

        TestSet(String scenario) {
            List<ScriptParser.OrderAction> scenarios = new ScriptParser().parse(scenario);
            book = new OrderBookImpl(
                    new SimpleInstrument("FOO"),
                    new OrderIdProvider()
            );
            scriptRunner = new ScriptRunner(scenarios, book);
        }
    }

}
