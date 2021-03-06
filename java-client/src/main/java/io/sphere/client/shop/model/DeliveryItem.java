package io.sphere.client.shop.model;

/**
 * Represents a line item or a custom line item in a {@link io.sphere.client.shop.model.Delivery}.
 */
public final class DeliveryItem {

    private String id;
    private int quantity;

    public DeliveryItem(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    //for JSON mapper
    protected DeliveryItem() {
    }

    /** Creates a DeliveryItem from a line item assuming that all items can be shipped. */
    public DeliveryItem(final LineItem lineItem) {
        this(lineItem.getId(), lineItem.getQuantity());
    }

    /** Creates a DeliveryItem from a custom line item assuming that all items can be shipped. */
    public DeliveryItem(final CustomLineItem customLineItem) {
        this(customLineItem.getId(), customLineItem.getQuantity());
    }

    /** the id of a line item or a custom line item in the order. */
    public String getId() {
        return id;
    }

    /** the number of items present in the delivery */
    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliveryItem that = (DeliveryItem) o;

        if (quantity != that.quantity) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + quantity;
        return result;
    }

    @Override
    public String toString() {
        return "DeliveryItem{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
