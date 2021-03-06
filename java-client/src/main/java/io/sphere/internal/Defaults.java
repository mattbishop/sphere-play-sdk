package io.sphere.internal;

import io.sphere.client.filters.expressions.FilterType;
import io.sphere.client.shop.ApiMode;
import io.sphere.client.shop.model.Cart;

/** Default values of various config values / method parameters. */
public class Defaults {
    /** Default page size when paging through results. */
    public static final int pageSize = 25;

    /** Amount of time indicating that an OAuth token is about to expire and should be refreshed.
     *  See {@link io.sphere.internal.oauth.SphereClientCredentials}. */
    public static final long tokenAboutToExpireMs = 60*1000L;  // 1 minute

    /** Size of a single chunk that should be enough to fetch all categories from the backend.
     *  See {@link CategoryTreeImpl}. */
    public static final int maxNumberOfCategoriesToFetchAtOnce = 0;

    /** Filter type used if not explicitly specified. */
    public static final FilterType filterType = FilterType.RESULTS_AND_FACETS;

    /** API mode used when none specified. */
    public static final ApiMode apiMode = ApiMode.Published;

    /** Inventory mode used when none specified. */
    public static final Cart.InventoryMode cartInventoryMode = Cart.InventoryMode.None;

    public static final String coreHttpServiceUrl = "https://api-v0.sphere.io/";
    public static final String authHttpServiceUrl = "https://auth-v0.sphere.io/";
}
