package com.aratushn.toy_orderbook.impl.ordermanager;

import com.aratushn.toy_orderbook.api.events.*;
import com.aratushn.toy_orderbook.api.orders.*;
import com.aratushn.toy_orderbook.api.primitives.OrderId;
import com.aratushn.toy_orderbook.api.primitives.Quantity;
import com.aratushn.toy_orderbook.impl.events.AcceptEventImpl;
import com.aratushn.toy_orderbook.impl.events.CancelEventImpl;
import com.aratushn.toy_orderbook.impl.events.DisplayEventImpl;
import com.aratushn.toy_orderbook.impl.events.FillEventImpl;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LimitOrderImpl implements LimitOrder {
    private final OrderId orderId;
    private final LimitOrderAttributes attributes;
    private final OrderStateImpl state;

    public LimitOrderImpl(OrderId orderId, LimitOrderAttributes attributes) {
        this.orderId = orderId;
        this.attributes = attributes;
        this.state = new OrderStateImpl();
    }

    @Override
    public OrderId getOrderId() {
        return orderId;
    }

    @Override
    public LimitOrderAttributes getOrderAttributes() {
        return attributes;
    }

    @Override
    public OrderState getOrderState() {
        return state;
    }

    OrderStateUpdater getOrderStateUpdater() {
        return state;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", orderId)
                .add("attributes", attributes)
                .add("state", state)
                .toString();
    }

    interface OrderStateUpdater {
        CancelEvent cancel();
        AcceptEvent accept();
        FillEvent fill(Fill fill);
        DisplayEvent display();
    }

    class OrderStateImpl implements OrderState, OrderStateUpdater {
        private final List<OrderEvent> events = new ArrayList<>();
        private final List<OrderEvent> eventsView = Collections.unmodifiableList(events);

        // cached values (could be figured out from events)
        private final List<Fill> fills = new ArrayList<>();
        private final List<Fill> fillsView = Collections.unmodifiableList(fills);

        /** if not placed, it means this order has not yet been accepted by the exchange*/
        private boolean isPlaced;
        /** the time this order is placed */
        private Instant orderTime;
        /** if the order is not fully matched against the book, its displayed */
        private boolean isDisplayed;

        private Quantity outstandingQuantity;
        private Quantity filledQuantity;
        private Quantity cancelledQuantity;

        private OrderStateImpl() {
            orderTime = null;
            isPlaced = false;
            isDisplayed = false;
            outstandingQuantity = zero();
            filledQuantity = zero();
            cancelledQuantity = zero();
        }

        @Override
        public AcceptEvent accept() {
            Preconditions.checkArgument(!isPlaced, "This order has already been placed");

            isPlaced = true;
            orderTime = Instant.now();
            outstandingQuantity = attributes.getSizeUnsigned();
            AcceptEventImpl event = new AcceptEventImpl(LimitOrderImpl.this);
            events.add(event);
            return event;
        }


        @Override
        public DisplayEvent display() {
            Preconditions.checkArgument(isPlaced, "This order has not been placed yet");
            Preconditions.checkArgument(!isDisplayed, "This order has already been displayed");
            Preconditions.checkArgument(isOutstanding(), "Non-outstanding order cant be displayed");

            isDisplayed = true;
            DisplayEventImpl event = new DisplayEventImpl(LimitOrderImpl.this, Instant.now(), getOutstandingQuantity());
            events.add(event);
            return event;
        }

        @Override
        public FillEvent fill(Fill fill) {
            Preconditions.checkArgument(isPlaced, "This order has not been placed yet");
            Preconditions.checkArgument(!fills.contains(fill), "This fill already exists");

            filledQuantity = filledQuantity.plus(fill.getFillQuantity());
            outstandingQuantity = outstandingQuantity.minus(fill.getFillQuantity());

            FillEventImpl event = new FillEventImpl(
                    LimitOrderImpl.this,
                    fill,
                    outstandingQuantity
            );
            events.add(event);
            fills.add(fill);

            return event;
        }

        @Override
        public CancelEvent cancel() {
            Preconditions.checkArgument(isPlaced, "This order has not been placed yet");

            if (outstandingQuantity.compareTo(zero()) <= 0) {
                throw new IllegalStateException("No longer outstanding");
            }

            CancelEventImpl event = new CancelEventImpl(
                    LimitOrderImpl.this,
                    outstandingQuantity,
                    isDisplayed ? outstandingQuantity : zero(),
                    Instant.now()
            );

            cancelledQuantity = outstandingQuantity;
            outstandingQuantity = zero();

            events.add(event);

            return event;
        }

        @Override
        public Instant getOrderTime() {
            assert !isPlaced || orderTime != null;
            return orderTime;
        }

        @Override
        public OrderStatus getStatus() {
//            PrePlacement, Working, Filled, Cancelled, PartialCancel, Filled_Partial;

            if (isPlaced) {
                boolean hasFills = filledQuantity.compareTo(zero()) > 0;
                boolean hasCancel = cancelledQuantity.compareTo(zero()) > 0;
                boolean isOutstanding = outstandingQuantity.compareTo(zero()) > 0;

                if (!hasFills && !hasCancel) {
                    assert isOutstanding;
                    if (isDisplayed) {
                        return OrderStatus.PostedPassively;
                    } else {
                        // means we are not finished matching it against the book yet
                        return OrderStatus.Working;
                    }
                } else if (hasFills) {
                    if (isOutstanding) {
                        return OrderStatus.PartiallyFilled;
                    } else if (hasCancel) {
                        return OrderStatus.PartiallyFilledAndCanceled;
                    } else {
                        return OrderStatus.Filled;
                    }
                } else /*if (hasCancel)*/ {
                    return OrderStatus.Cancelled;
                }

            } else {
                return OrderStatus.PrePlacement;
            }
        }

        @Override
        public Quantity getOutstandingQuantity() {
            return outstandingQuantity;
        }

        @Override
        public Quantity getFilledQuantity() {
            return filledQuantity;
        }

        @Override
        public Quantity getCancelledQuantity() {
            return cancelledQuantity;
        }

        @Override
        public boolean isOutstanding() {
            return outstandingQuantity.compareTo(zero()) > 0;
        }

        @Override
        public boolean isDisplayed() {
            return isDisplayed;
        }

        @Override
        public List<Fill> getFills() {
            return fillsView;
        }

        @Override
        public List<OrderEvent> getEvents() {
            return eventsView;
        }

        private Quantity zero() {
            return attributes.getInstrument().getZeroQuantity();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("isPlaced", isPlaced)
                    .add("orderTime", orderTime)
                    .add("isDisplayed", isDisplayed)
                    .add("outstandingQuantity", outstandingQuantity)
                    .add("filledQuantity", filledQuantity)
                    .add("cancelledQuantity", cancelledQuantity)
                    .toString();
        }
    }

}
