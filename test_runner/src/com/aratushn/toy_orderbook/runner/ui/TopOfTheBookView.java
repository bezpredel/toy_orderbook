package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.OrderBook;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.marketdata.Trade;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

class TopOfTheBookView extends JPanel {
    private final TM model;
    private OrderBook book;

    TopOfTheBookView() {
        super(new BorderLayout());
        model = new TM();

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new TR(model));
        add(table, BorderLayout.CENTER);
        add(table.getTableHeader(), BorderLayout.NORTH);
    }

    public void replaceBook(OrderBook book) {
        this.model.clear();
        this.book = book;

        if (book != null) {
            book.addEventSubscriber(
                    new MarketDataSubscriber() {
                        @Override
                        public void onTrade(Trade trade) {

                        }

                        @Override
                        public void onQuote(Quote quote) {
                            updateModel(quote);
                        }
                    }
            );
        }

        updateModel(book != null ? book.getCurrentQuote() : null);
    }


    void checkpoint() {
       model.checkpoint(book.getCurrentQuote());
    }

    void refresh() {
       model.refresh();
    }

    void updateModel(Quote quote) {
        model.update(quote);
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
            if (row == 0) {
                switch (column) {
                    case 0:
                    case 1:
                        if (!Objects.equals(model.bid, model.prevBid)) {
                            setBackground(Color.GREEN);
                        }
                        break;
                    case 2:
                    case 3:
                        if (!Objects.equals(model.offer, model.prevOffer)) {
                            setBackground(Color.GREEN);
                        }
                        break;
                }
            }

            return cell;
        }
    }

    private static class TM extends AbstractTableModel {
        private SideQuote prevBid, prevOffer;
        private SideQuote bid, offer;

        void clear() {
            prevBid = prevOffer = bid = offer = null;
        }

        void update(Quote quote) {
            prevBid = bid;
            prevOffer = offer;
            
            bid = quote != null ? quote.getBestBid() : null;
            offer = quote != null ? quote.getBestOffer() : null;

            fireTableDataChanged();
        }

        void refresh() {
            fireTableDataChanged();
        }

        private void checkpoint(Quote quote) {
            prevBid = quote != null ? quote.getBestBid() : null;
            prevOffer = quote != null ? quote.getBestOffer() : null;
        }

        @Override
        public String getColumnName(int column) {
             switch (column) {
                 case 0: return "";
                 case 1: return "Best Bid";
                 case 2: return "Best Offer";
                 case 3: return "";
                 default: return null;
             }
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == 0) {
                switch (columnIndex) {
                    case 0: return bid != null ? bid.getQuoteTime() : null;
                    case 1: return bid != null ? bid.getQuantity() + "@" + bid.getPrice() : null;
                    case 2: return offer != null ? offer.getQuantity() + "@" + offer.getPrice() : null;
                    case 3: return offer != null ? offer.getQuoteTime() : null;
                }
            }
            return null;
        }
    }
}
