package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.*;
import com.aratushn.toy_orderbook.api.events.*;
import com.aratushn.toy_orderbook.api.marketdata.MarketDataSubscriber;
import com.aratushn.toy_orderbook.api.marketdata.Quote;
import com.aratushn.toy_orderbook.api.orders.LimitOrder;
import com.aratushn.toy_orderbook.api.orders.LimitOrderAttributes;
import com.aratushn.toy_orderbook.api.primitives.*;
import com.aratushn.toy_orderbook.impl.orderbook.Book;
import com.aratushn.toy_orderbook.impl.orderbook.MatchingEngine;
import com.aratushn.toy_orderbook.impl.quotes.TopOfTheBookQuoteSource;
import com.aratushn.toy_orderbook.util.SubscriptionHandler;
import com.aratushn.toy_orderbook.util.SubscriptionStub;
import com.google.common.base.Preconditions;

import java.time.Instant;

public class OrderBookImpl implements OrderBook {
    private final Instrument instrument;
    private final OrderIdProvider orderIdProvider;

    private final Book book;
    private final MatchingEngine matchingEngine;

    private final SubscriptionHandler<OrderEventSubscriber> internalEventSubscribers = new SubscriptionHandler<>();
    private final OrderEventSubscriptionManager orderEventSubscriptionManager;

    private final MarketDataSubscriptionManager marketDataSubscriptionManager;

    public OrderBookImpl(Instrument instrument, OrderIdProvider orderIdProvider) {
        this.instrument = instrument;
        this.orderIdProvider = orderIdProvider;
        this.book = new Book();
        this.matchingEngine = new MatchingEngine(
                instrument,
                book,
                OrderBookImpl.this::createCrossTrade
        );

        final TopOfTheBookQuoteSource topOfTheBookQuoteSource = new TopOfTheBookQuoteSource(instrument);

        this.internalEventSubscribers.subscribe(topOfTheBookQuoteSource);
        this.marketDataSubscriptionManager = new MarketDataSubscriptionManager(topOfTheBookQuoteSource);
        orderEventSubscriptionManager = new OrderEventSubscriptionManager();
    }

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public Quote getCurrentQuote() {
        return marketDataSubscriptionManager.getLastPublishedQuote(); // note: kinda sux, can be null
    }

    @Override
    public LimitOrder build(LimitOrderAttributes attributes) {
        return new LimitOrderImpl(
                orderIdProvider.createOrderId(),
                attributes
        );
    }

    @Override
    public void place(LimitOrder orderObj) {
        LimitOrderImpl order = cast(orderObj);

        final AcceptEvent acceptEvent = order.getOrderStateUpdater().accept();

        dispatch(acceptEvent);

        // match the order
        matchingEngine.match(orderObj);

        if (orderObj.getOrderState().isOutstanding()) {
            // add the order to the book
            book.getSide(orderObj.getOrderAttributes().getSide()).add(orderObj);
            // dispatch "display" event
            final DisplayEvent displayEvent = order.getOrderStateUpdater().display();

            dispatch(displayEvent);
        }

        notifySubscribers();
    }

    @Override
    public void cancel(LimitOrder orderObj) {
        LimitOrderImpl order = cast(orderObj);

        final CancelEvent event = order.getOrderStateUpdater().cancel();

        dispatch(event);

        book.getSide(orderObj.getOrderAttributes().getSide()).remove(orderObj);

        notifySubscribers();
    }

    private void createCrossTrade(LimitOrder takerObj, LimitOrder makerObj, Quantity matchQty) {
        final LimitOrderImpl taker = cast(takerObj);
        final LimitOrderImpl maker = cast(makerObj);

        final Instant now = Instant.now();
        final Price crossPrice = maker.getOrderAttributes().getLimitPrice();

        final FillImpl takerFill = new FillImpl(
                now,
                taker,
                orderIdProvider.createFillId(taker.getOrderId()),
                crossPrice,
                matchQty,
                FillType.TAKER
        );

        final FillImpl makerFill = new FillImpl(
                now,
                maker,
                orderIdProvider.createFillId(maker.getOrderId()),
                crossPrice,
                matchQty,
                FillType.MAKER
        );

        validateFill(takerFill);
        validateFill(makerFill);

        final boolean takerBuys = taker.getOrderAttributes().getSide() == Side.BUY;
        final TradeImpl trade = new TradeImpl(
            takerBuys ? takerFill : makerFill,
            takerBuys ? makerFill : takerFill
        );

        FillEvent takerFillEvent = taker.getOrderStateUpdater().fill(takerFill);
        FillEvent makerFillEvent = maker.getOrderStateUpdater().fill(makerFill);

        dispatch(takerFillEvent);
        dispatch(makerFillEvent);

        marketDataSubscriptionManager.queueTrade(trade);
    }

    private void validateFill(FillImpl fill) {
        LimitOrder order = fill.getOrder();
        Price limitPrice = order.getOrderAttributes().getLimitPrice();
        Quantity outstandingQuantity = order.getOrderState().getOutstandingQuantity();
        Side side = order.getOrderAttributes().getSide();

        if (limitPrice.compareTo(fill.getFillPrice(), side) < 0) {
            throw new RuntimeException("Fill price more aggressive than limit price, cannot create fill " + fill);
        }

        if (outstandingQuantity.compareTo(fill.getFillQuantity()) < 0) {
            throw new RuntimeException("Fill quantity > outstanding quantity, cannot create fill " + fill);
        }
    }

    private LimitOrderImpl cast(LimitOrder limitOrder) {
        Preconditions.checkArgument(limitOrder instanceof LimitOrderImpl, "Only accept orders created via build() call to this instance");

        return (LimitOrderImpl) limitOrder;
    }

    private void notifySubscribers() {
        orderEventSubscriptionManager.publishOrderEvents();
        marketDataSubscriptionManager.publishMarketDataUpdates();
    }

    private void dispatch(OrderEvent event) {
        internalEventSubscribers.dispatch(OrderEventSubscriber::onOrderChange, event);
        orderEventSubscriptionManager.queueEvent(event);
    }

    @Override
    public SubscriptionStub addEventSubscriber(OrderEventSubscriber subscriber) {
        return orderEventSubscriptionManager.subscribe(subscriber);
    }

    @Override
    public SubscriptionStub addEventSubscriber(MarketDataSubscriber subscriber) {
        return marketDataSubscriptionManager.subscribe(subscriber);
    }
}
