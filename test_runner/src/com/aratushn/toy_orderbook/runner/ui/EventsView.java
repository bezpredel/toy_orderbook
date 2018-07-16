package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.OrderBook;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public class EventsView extends JPanel {
    private final BookView bookView = new BookView();
    private final TopOfTheBookView topOfTheBookView = new TopOfTheBookView();
    private final BlotterView blotterView = new BlotterView();

    public EventsView() {
        super(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(topOfTheBookView, BorderLayout.NORTH);
        top.add(bookView, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, blotterView);
        split.addAncestorListener(
                new AncestorListener() {
                    @Override
                    public void ancestorAdded(AncestorEvent event) {
                        split.setDividerLocation(0.6);
                    }

                    @Override
                    public void ancestorRemoved(AncestorEvent event) {

                    }

                    @Override
                    public void ancestorMoved(AncestorEvent event) {

                    }
                }
        );
        split.setResizeWeight(0.6);

        this.add(split, BorderLayout.CENTER);
    }

    public void replaceBook(OrderBook book) {
        topOfTheBookView.replaceBook(book);
        bookView.replaceBook(book);
        blotterView.replaceBook(book);

    }

    public void refresh() {
        invalidate();
        repaint();
    }

    public void checkpoint() {
        blotterView.checkpoint();
        bookView.checkpoint();
        topOfTheBookView.checkpoint();
    }
}
