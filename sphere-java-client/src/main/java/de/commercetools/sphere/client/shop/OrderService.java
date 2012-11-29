package de.commercetools.sphere.client.shop;

import de.commercetools.sphere.client.CommandRequest;
import de.commercetools.sphere.client.FetchRequest;
import de.commercetools.sphere.client.QueryRequest;
import de.commercetools.sphere.client.shop.model.*;
import de.commercetools.sphere.client.model.QueryResult;

/** Sphere HTTP API for working with orders in a given project. */
public interface OrderService {
    /** Creates a request that finds an order by given id. */
    FetchRequest<Order> byId(String id);

    /** Creates a request that queries all orders. */
    QueryRequest<Order> all();

    /** Creates a request builder that queries all orders of the given customer. */
    public QueryRequest<Order> byCustomerId(String customerId);

    /** Sets the payment state of the order. */
    public CommandRequest<Order> updatePaymentState(String orderId, int orderVersion, PaymentState paymentState);

    /** Sets the shipment state of the order. */
    public CommandRequest<Order> updateShipmentState(String orderId, int orderVersion, ShipmentState shipmentState);
}