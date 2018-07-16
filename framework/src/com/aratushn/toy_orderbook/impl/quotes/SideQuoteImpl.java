package com.aratushn.toy_orderbook.impl.quotes;

import com.aratushn.toy_orderbook.api.marketdata.SideQuote;
import com.aratushn.toy_orderbook.api.primitives.Price;
import com.aratushn.toy_orderbook.api.primitives.Quantity;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;

@Immutable
class SideQuoteImpl implements SideQuote {
    private final Instant quoteTime;
    private final Price price;
    private final Quantity quantity;

    SideQuoteImpl(Instant quoteTime, Price price, Quantity quantity) {
        this.quoteTime = quoteTime;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    @Nonnull
    public Instant getQuoteTime() {
        return quoteTime;
    }

    @Override
    @Nonnull
    public Price getPrice() {
        return price;
    }

    @Override
    @Nonnull
    public Quantity getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SideQuoteImpl sideQuote = (SideQuoteImpl) o;
        return Objects.equals(quoteTime, sideQuote.quoteTime) &&
                Objects.equals(price, sideQuote.price) &&
                Objects.equals(quantity, sideQuote.quantity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(quoteTime, price, quantity);
    }
}
