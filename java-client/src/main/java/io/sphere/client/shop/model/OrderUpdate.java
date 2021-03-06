package io.sphere.client.shop.model;

import com.google.common.base.Optional;
import io.sphere.internal.command.OrderCommands;
import io.sphere.internal.command.Update;
import io.sphere.internal.command.UpdateAction;

import java.util.List;

public class OrderUpdate extends Update<UpdateAction> {
    public OrderUpdate setPaymentState(PaymentState paymentState) {
        add(new OrderCommands.UpdatePaymentState(paymentState));
        return this;
    }

    public OrderUpdate setShipmentState(ShipmentState shipmentState) {
        add(new OrderCommands.UpdateShipmentState(shipmentState));
        return this;
    }

    /**
     * See {@link io.sphere.client.shop.model.Delivery}.
     * It is necessary that a shipping method is set to add a delivery.
     */
    public OrderUpdate addDelivery(List<DeliveryItem> items) {
        add(new OrderCommands.AddDelivery(items));
        return this;
    }

    public OrderUpdate addParcelToDelivery(String deliveryId, Optional<ParcelMeasurements> measurements, Optional<TrackingData> trackingData) {
        add(new OrderCommands.AddParcelToDelivery(deliveryId, measurements, trackingData));
        return this;
    }

    public OrderUpdate updateSyncInfo(final SyncInfo syncInfo) {
        add(new OrderCommands.UpdateSyncInfo(syncInfo));
        return this;
    }
}
