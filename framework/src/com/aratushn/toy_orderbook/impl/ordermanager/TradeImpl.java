package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.marketdata.Trade;
import com.aratushn.toy_orderbook.api.orders.Fill;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.api.primitives.Side;

import java.time.Instant;

class TradeImpl implements Trade {
    private final Fill buyer;
    private final Fill seller;

    TradeImpl(Fill buyer, Fill seller) {
        assert buyer.getFillPrice().equals(seller.getFillPrice());
        assert buyer.getFillQuantity().equals(seller.getFillQuantity());
        assert buyer.getFillTime().equals(seller.getFillTime());
        assert buyer.getOrder().getOrderAttributes().getSide() == Side.BUY;
        assert seller.getOrder().getOrderAttributes().getSide() == Side.SELL;

        this.buyer = buyer;
        this.seller = seller;
    }

    @Override
    public Instant getTradeTime() {
        return buyer.getFillTime();
    }

    @Override
    public Price getTradePrice() {
        return buyer.getFillPrice();
    }

    @Override
    public Quantity getTradeQuantity() {
        return buyer.getFillQuantity();
    }

    @Override
    public Fill getBuyer() {
        return buyer;
    }

    @Override
    public Fill getSeller() {
        return seller;
    }

    @Override
    public String toString() {
        return getTradeTime() + " " + getTradeQuantity() +"@" + getTradePrice() + ": " + getBuyer() + " vs " + getSeller();
    }
}
