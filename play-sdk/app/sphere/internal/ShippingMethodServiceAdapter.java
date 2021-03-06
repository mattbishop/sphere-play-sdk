package sphere.internal;

import java.util.Currency;
import java.util.List;
import javax.annotation.Nonnull;
import io.sphere.client.shop.model.Location;
import io.sphere.client.shop.model.ShippingMethod;
import net.jcip.annotations.Immutable;
import sphere.FetchRequest;
import sphere.QueryRequest;
import sphere.ShippingMethodService;
import sphere.util.Async;

/** ShippingMethodService with Play-specific async methods. */
@Immutable
public class ShippingMethodServiceAdapter implements ShippingMethodService {
    private final io.sphere.client.shop.ShippingMethodService service;
    public ShippingMethodServiceAdapter(@Nonnull io.sphere.client.shop.ShippingMethodService service) {
        if (service == null) throw new NullPointerException("service");
        this.service = service;
    }

    @Override public FetchRequest<ShippingMethod> byId(String id) {
        return Async.adapt(service.byId(id));
    }

    @Deprecated
    @Override public QueryRequest<ShippingMethod> all() {
        return query();
    }

    @Override public QueryRequest<ShippingMethod> query() {
        return Async.adapt(service.query());
    }

    @Override
    public FetchRequest<List<ShippingMethod>> forLocation(Location location, Currency currency) {
        return Async.adapt(service.forLocation(location, currency));
    }

    @Override
    public FetchRequest<List<ShippingMethod>> forCart(String cartId) {
        return Async.adapt(service.forCart(cartId));
    }

}
