package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.OrderBook;
import com.aratushn.toy_orderbook.api.events.OrderEvent;
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

class BookView extends JPanel {
    private final TM model;
    private OrderBook book;

    BookView() {
        super(new BorderLayout());
        model = new TM();

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new TR(model));
        add(table, BorderLayout.CENTER);
        add(table.getTableHeader(), BorderLayout.NORTH);
    }

    public void replaceBook(OrderBook book) {
        this.book = book;
        model.clear();

        if (book != null) {
            book.addEventSubscriber(model::addEvent);
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
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(null);
            LimitOrder order = model.getOrderAtCell(row, column);

            if (model.changedRecently.contains(order)) {
                cell.setBackground(Color.GREEN);
            }

            return cell;
        }
    }

    private static class TM extends AbstractTableModel {
        private final List<LimitOrder> bids = new ArrayList<>();
        private final List<LimitOrder> offers = new ArrayList<>();

        private final Set<LimitOrder> changedRecently = new HashSet<>();

        public void addEvent(OrderEvent event) {
            LimitOrder order = event.getOrder();
            Side side = order.getOrderAttributes().getSide();
            List<LimitOrder> sideList = side==Side.BUY ? bids : offers;

            if (order.getOrderState().isOutstanding()) {
                if (!sideList.contains(order)) {
                    sideList.add(order);
                    sideList.sort(OrderComparator.forSide(side));
                }
            } else {
                sideList.remove(order);
            }

            changedRecently.add(order);

            fireTableDataChanged();
        }

        public void checkpoint() {
            changedRecently.clear();
        }

        @Override
        public String getColumnName(int column) {
             switch (column) {
                 case 0: return "";
                 case 1: return "Bids";
                 case 2: return "Offers";
                 case 3: return "";
                 default: return null;
             }
        }

        @Override
        public int getRowCount() {
            return Math.max(bids.size(), offers.size());
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LimitOrder order = getOrderAtCell(rowIndex, columnIndex);

            if (order == null) {
                return null;
            } else {
                if (columnIndex == 1 || columnIndex == 2) {
                    return order.getOrderState().getOutstandingQuantity() + "@" + order.getOrderAttributes().getLimitPrice();
                } else if (columnIndex == 0){
                    return order.getOrderState().getOrderTime() + " " + order.getOrderId();
                } else if (columnIndex == 3) {
                    return order.getOrderId() + " " + order.getOrderState().getOrderTime();
                }
            }
            return null;
        }

        private LimitOrder getOrderAtCell(int rowIndex, int columnIndex) {
            Side side = columnIndex == 0 || columnIndex == 1 ? Side.BUY : Side.SELL;
            List<LimitOrder> list = side == Side.BUY ? bids : offers;
            return rowIndex < list.size() ? list.get(rowIndex) : null;
        }

        public void clear() {
            bids.clear();
            offers.clear();
            changedRecently.clear();

            fireTableDataChanged();
        }
    }
}
