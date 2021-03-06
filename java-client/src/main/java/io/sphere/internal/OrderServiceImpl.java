package io.sphere.internal;

import com.google.common.base.Function;
import io.sphere.client.*;
import io.sphere.client.exceptions.OutOfStockException;
import io.sphere.client.exceptions.PriceChangedException;
import io.sphere.client.exceptions.SphereBackendException;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.QueryResult;
import io.sphere.client.model.VersionedId;
import io.sphere.client.shop.ApiMode;
import io.sphere.client.shop.CreateOrderBuilder;
import io.sphere.client.shop.OrderService;
import io.sphere.client.shop.model.Order;
import io.sphere.client.shop.model.PaymentState;
import io.sphere.client.shop.model.ShipmentState;
import io.sphere.client.shop.model.*;
import io.sphere.internal.command.*;
import io.sphere.internal.request.RequestFactory;
import com.google.common.base.Optional;
import org.codehaus.jackson.type.TypeReference;

import static io.sphere.internal.util.Util.*;

import javax.annotation.Nullable;

public class OrderServiceImpl extends ProjectScopedAPI<Order> implements OrderService {
    public OrderServiceImpl(RequestFactory requestFactory, ProjectEndpoints endpoints) {
        super(requestFactory, endpoints, new TypeReference<Order>() {}, new TypeReference<QueryResult<Order>>() { });
    }

    @Override public FetchRequest<Order> byId(String id) {
        return requestFactory.createFetchRequest(
                endpoints.orders.byId(id),
                Optional.<ApiMode>absent(),
                new TypeReference<Order>() {});
    }

    @Deprecated
    @Override public QueryRequest<Order> all() {
        return query();
    }

    @Override public QueryRequest<Order> query() {
        return queryImpl(endpoints.orders.root());
    }

    @Override public QueryRequest<Order> forCustomer(String customerId) {
        return requestFactory.createQueryRequest(
                endpoints.orders.queryByCustomerId(customerId),
                Optional.<ApiMode>absent(),
                new TypeReference<QueryResult<Order>>() {});
    }

    @Deprecated
    @Override public CommandRequest<Order> updatePaymentState(VersionedId orderId, PaymentState paymentState) {
        return updateOrder(orderId, new OrderUpdate().setPaymentState(paymentState));
    }

    @Deprecated
    @Override public CommandRequest<Order> updateShipmentState(VersionedId orderId, ShipmentState shipmentState) {
        return updateOrder(orderId, new OrderUpdate().setShipmentState(shipmentState));
    }

    @Override
    public CommandRequest<Order> updateOrder(VersionedId orderId, OrderUpdate orderUpdate) {
        return update(orderId, orderUpdate);
    }

    private CommandRequest<Order> update(VersionedId orderId, OrderUpdate orderUpdate) {
        return createCommandRequest(
                endpoints.orders.byId(orderId.getId()),
                new UpdateCommand<UpdateAction>(orderId.getVersion(), orderUpdate));
    }

    @Override public CommandRequest<Order> createOrder(VersionedId cartId, PaymentState paymentState) {
        return createOrder(new CreateOrderBuilder(cartId, paymentState));
    }

    @Override public CommandRequest<Order> createOrder(VersionedId cartId) {
        return createOrder(cartId, null);
    }

    @Override public CommandRequest<Order> createOrder(CreateOrderBuilder builder) {
        return createOrder(builder.build());
    }

    private CommandRequest<Order> createOrder(CartCommands.OrderCart createCustomerCommand) {
        return requestFactory.createCommandRequest(
                endpoints.orders.root(), createCustomerCommand, new TypeReference<Order>() {}).
                withErrorHandling(new Function<SphereBackendException, SphereException>() {
                    public SphereException apply(@Nullable SphereBackendException e) {
                        SphereError.OutOfStock outOfStockError = getError(e, SphereError.OutOfStock.class);
                        if (outOfStockError != null)
                            return new OutOfStockException(outOfStockError.getLineItemIds());
                        SphereError.PriceChanged priceChangedError = getError(e, SphereError.PriceChanged.class);
                        if (priceChangedError != null)
                            return new PriceChangedException(priceChangedError.getLineItemIds());
                        return null;
                    }
                });
    }
}
