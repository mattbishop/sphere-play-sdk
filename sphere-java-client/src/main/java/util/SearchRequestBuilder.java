package de.commercetools.sphere.client.util;

import de.commercetools.sphere.client.BackendException;
import de.commercetools.sphere.client.model.SearchQueryResult;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;

/** Represents a search request to the Sphere backend.
 *  Use {@link #fetch} or {@link #fetchAsync} to execute the request. */
public interface SearchRequestBuilder<T> {
    /** Executes the request to the Sphere backend and returns result. */
    SearchQueryResult<T> fetch() throws BackendException;

    /** Creates a future that allows you to be notified when the results from the Sphere backend arrived.
     *  Does not make a request immediately. To be notified, add a listener to the future. */
    ListenableFuture<SearchQueryResult<T>> fetchAsync() throws BackendException;

    /** Sets the maximum number of resources to be returned. */
    SearchRequestBuilder<T> limit(int limit);

    /** Sets the paging offset. */
    SearchRequestBuilder<T> offset(int offset);

    /** Requests references to be expanded in the returned JSON documents.
     *  Expanded references contain the full target objects they link to.
     *
     *  @param paths The paths to be expanded, e.g. 'vendor', 'categories[*]' or 'variants[*].vendor'. */
    SearchRequestBuilder<T> expand(String... paths);

    /** Adds a filter.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     * 
     * @param path The path to be matched,
     *             e.g. 'categories.id', 'attributes.color', or 'variant.attributes.color'.
     * @param value The value to search for. */
    SearchRequestBuilder<T> filter(String path, String value);

    /** Adds a filter.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     *
     * @param path The expression to be matched,
     *             e.g. 'categories.id', 'attributes.color', or 'variant.attributes.color'.
     * @param value The value to search for. */
    SearchRequestBuilder<T> filter(String path, double value);

    /** Adds a filter.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     *
     * @param path The expression to be matched,
     *             e.g. 'categories.id', 'attributes.color', or 'variant.attributes.color'.
     * @param value The value to search for. */
    SearchRequestBuilder<T> filter(String path, int value);

    /** Adds a multiple value OR filter.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     *
     * @param path The path to be matched,
     *             e.g. 'categories.id', 'attributes.color', or 'variant.attributes.color'.
     * @param values Search for any of these values. */
    SearchRequestBuilder<T> filter(String path, Collection<String> values);

    /** Searches for values in a given range. This filter does nothing if both bounds of the range are null.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     *
     * @param path The path to be matched,
     *             e.g. 'price', 'attributes.height', or 'variant.attributes.height'.
     * @param from The lower bound of the range to search for, inclusive. Pass null if none.
     * @param to The upper bound of the range to search for, inclusive. Pass null if none. */
    SearchRequestBuilder<T> filterRange(String path, Integer from, Integer to);

    /** Searches for money values in a given range. This filter does nothing if both bounds of the range are null.
     * A filter filters the results of a query after facets have been calculated
     * and thus does not influence facet counts.
     *
     * @param path The path to be matched,
     *             e.g. 'price', 'attributes.height', or 'variant.attributes.height'.
     * @param from The lower bound of the range to search for, inclusive. Pass null if none.
     * @param to The upper bound of the range to search for, inclusive. Pass null if none. */
    SearchRequestBuilder<T> filterMoneyRange(String path, Integer from, Integer to);

    /** Requests that the result contain aggregated counts of search results matching given facet expression.
     *
     * @param expression The facet expression for which aggregated counts of search results should be calculated,
     *                   e.g. 'attributes.color', or 'variant.attributes.color'. */
    SearchRequestBuilder<T> facet(String expression);
}