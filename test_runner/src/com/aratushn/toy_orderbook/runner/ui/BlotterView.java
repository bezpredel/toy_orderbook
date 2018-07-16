package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.OrderBook;
import com.aratushn.toy_orderbook.api.events.*;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.marketdata.Trade;
import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.orders.OrderComparator;
import com.aratushn.toy_orderbook.api.primitives.Side;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class BlotterView extends JPanel {
    private final TM model;
    private OrderBook book;

    BlotterView() {
        super(new BorderLayout());
        model = new TM();

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new TR(model));

        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(180);
        table.getColumnModel().getColumn(0).setMinWidth(180);
        table.getColumnModel().getColumn(1).setMaxWidth(180);
        table.getColumnModel().getColumn(1).setMinWidth(180);
    }

    public void replaceBook(OrderBook book) {
        this.book = book;
        model.clear();

        if (book != null) {
            book.addEventSubscriber(model::addEvent);
            book.addEventSubscriber(new MarketDataSubscriber() {
                @Override
                public void onTrade(Trade trade) {
                     model.addTrade(trade);
                }

                @Override
                public void onQuote(Quote quote) {

                }
            });
        }
    }

    public void checkpoint() {
        model.checkpoint();
    }


    private static class TR extends DefaultTableCellRenderer {
        private final TM model;

        public TR(TM model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            row = model.events.size() - row - 1;
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(null);
            Object event = model.events.get(row);

            if (model.changedRecently.contains(event)) {
                cell.setBackground(Color.GREEN);
            }

            return cell;
        }
    }

    private static class TM extends AbstractTableModel {
        private final List<Object> events = new ArrayList<>();
        private final Set<Object> changedRecently = new HashSet<>();

        public void addEvent(OrderEvent event) {
            changedRecently.add(event);
            events.add(event);

            fireTableDataChanged();
        }

        public void addTrade(Trade trade) {
            changedRecently.add(trade);
            events.add(trade);

            fireTableDataChanged();
        }

        public void checkpoint() {
            changedRecently.clear();
        }

        @Override
        public String getColumnName(int column) {
             switch (column) {
                 case 0: return "Time";
                 case 1: return "Type";
                 case 2: return "Details";
                 default: return null;
             }
        }

        @Override
        public int getRowCount() {
            return events.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            rowIndex = events.size() - rowIndex - 1;

            Object obj = events.get(rowIndex);

            if (obj instanceof Trade) {
                Trade trade = (Trade) obj;
                if (columnIndex == 0) {
                    return trade.getTradeTime();
                } else if (columnIndex == 1) {
                    return "TRADE";
                } else {
                    return trade.getTradeQuantity() + "@" + trade.getTradePrice() +
                            " between " + trade.getBuyer().getOrder().getOrderId() + " (" + trade.getBuyer().getFillType() + ") and " +
                            trade.getSeller().getOrder().getOrderId() + " (" + trade.getSeller().getFillType() + ")";
                }
            } else if (obj instanceof OrderEvent) {
                OrderEvent event = (OrderEvent) obj;
                if (columnIndex == 0) {
                    return event.getEventTime();
                } else if (columnIndex == 1) {
                    return "ORDER " + event.getType();
                } else {
                    return event.visit(EventDescVisitor.INSTANCE);
                }
            }
            return null;
        }

        enum EventDescVisitor implements OrderEventVisitor<String> {
            INSTANCE;

            @Override
            public String visit(AcceptEvent event) {
                LimitOrder order = event.getOrder();
                return order.getOrderId() + " "+ order.getOrderAttributes().getSide() + " " + order.getOrderAttributes().getSizeUnsigned() + "@" + order.getOrderAttributes().getLimitPrice();
            }

            @Override
            public String visit(CancelEvent event) {
                LimitOrder order = event.getOrder();
                return order.getOrderId() + " " + event.getCancelledQuantity() + " cancelled";
            }

            @Override
            public String visit(DisplayEvent event) {
                LimitOrder order = event.getOrder();
                return order.getOrderId() + " " + event.getDisplayedQuantity() + "@" + order.getOrderAttributes().getLimitPrice() + " displayed";
            }

            @Override
            public String visit(FillEvent event) {
                LimitOrder order = event.getOrder();
                Fill fill = event.getFill();
                return order.getOrderId() + " " + order.getOrderState().getStatus() + " fill [" + fill.getFillId() + "] " +
                        fill.getFillType() + " " + fill.getFillQuantity() + "@" + fill.getFillPrice();
            }
        }


        public void clear() {
            events.clear();
            changedRecently.clear();

            fireTableDataChanged();
        }
    }
}
