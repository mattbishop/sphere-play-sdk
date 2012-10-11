package de.commercetools.sphere.client.model;

import net.jcip.annotations.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.FluentIterable;

import java.util.List;

/** Information about a date range facet, returned as a part of {@link SearchResult}. */
@Immutable
public class DateTimeRangeFacetResult implements FacetResult {
    private ImmutableList<DateTimeRangeFacetItem> items;

    /** A list of individual items for this date range facet and their respective counts. */
    public List<DateTimeRangeFacetItem> getItems() {
        return items;
    }

    /** Parses dates returned by the backend as milliseconds into joda.LocalDate instances. */
    static DateTimeRangeFacetResult fromMilliseconds(RangeFacetResult facetResult) {
        return new DateTimeRangeFacetResult(
                FluentIterable.from(facetResult.getItems()).transform(DateTimeRangeFacetItem.fromMilliseconds).toImmutableList());
    }

    private DateTimeRangeFacetResult(ImmutableList<DateTimeRangeFacetItem> items) {
        this.items = items;
    }
}