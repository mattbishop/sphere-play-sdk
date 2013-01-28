package de.commercetools.sphere.client.shop;

import de.commercetools.sphere.client.FetchRequest;
import de.commercetools.sphere.client.model.Reference;
import de.commercetools.sphere.client.shop.model.Catalog;
import de.commercetools.sphere.client.shop.model.InventoryEntry;

/** Sphere HTTP API for retrieving product inventory information. */
public interface InventoryService {

    /** Creates a request that finds an inventory entry by id. */
    FetchRequest<InventoryEntry> byId(String id);

    /** Creates a request that finds an inventory entry by product id, variant id and catalog. */
    FetchRequest<InventoryEntry> byProductIdVariantIdCatalog(String productId, String variantId, Reference<Catalog> catalog);
}