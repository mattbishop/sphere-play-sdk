package de.commercetools.internal;

import de.commercetools.sphere.client.ProjectEndpoints;
import de.commercetools.sphere.client.RequestBuilder;
import de.commercetools.sphere.client.model.QueryResult;
import de.commercetools.sphere.client.shop.Customers;
import de.commercetools.sphere.client.shop.model.Address;
import de.commercetools.sphere.client.shop.model.Customer;
import de.commercetools.sphere.client.shop.model.CustomerUpdate;
import de.commercetools.sphere.client.util.CommandRequestBuilder;

import org.codehaus.jackson.type.TypeReference;


public class CustomersImpl extends ProjectScopedAPI implements Customers {
    private final RequestFactory requestFactory;

    public CustomersImpl(RequestFactory requestFactory, ProjectEndpoints endpoints) {
        super(endpoints);
        this.requestFactory = requestFactory;
    }

    /** {@inheritDoc}  */
    public RequestBuilder<Customer> byId(String id) {
        return requestFactory.createQueryRequest(endpoints.customers.byId(id), new TypeReference<Customer>() {});
    }

    /** {@inheritDoc}  */
    public RequestBuilder<QueryResult<Customer>> all() {
        return requestFactory.createQueryRequest(endpoints.customers.root(), new TypeReference<QueryResult<Customer>>() {});
    }

    /** {@inheritDoc}  */
    public RequestBuilder<Customer> login(String email, String password) {
        return requestFactory.createQueryRequest(endpoints.customers.login(email, password), new TypeReference<Customer>() {});
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> signup(String email, String password, String firstName, String lastName, String middleName, String title) {
        return createCommandRequest(
                endpoints.customers.root(),
                new CustomerCommands.CreateCustomer(email, password, firstName, lastName, middleName, title));
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> changePassword(String customerId, int customerVersion, String currentPassword, String newPassword) {
        return createCommandRequest(
                endpoints.customers.changePassword(),
                new CustomerCommands.ChangePassword(customerId, customerVersion, currentPassword, newPassword));
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> changeShippingAddress(String customerId, int customerVersion, int addressIndex, Address address) {
        return createCommandRequest(
                endpoints.customers.changeShippingAddress(),
                new CustomerCommands.ChangeShippingAddress(customerId, customerVersion, addressIndex, address));
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> removeShippingAddress(String customerId, int customerVersion, int addressIndex) {
        return createCommandRequest(
                endpoints.customers.removeShippingAddress(),
                new CustomerCommands.RemoveShippingAddress(customerId, customerVersion, addressIndex));
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> setDefaultShippingAddress(String customerId, int customerVersion, int addressIndex) {
        return createCommandRequest(
                endpoints.customers.setDefaultShippingAddress(),
                new CustomerCommands.SetDefaultShippingAddress(customerId, customerVersion, addressIndex));
    }

    /** {@inheritDoc}  */
    public CommandRequestBuilder<Customer> updateCustomer(String customerId, int customerVersion, CustomerUpdate customerUpdate) {
        return createCommandRequest(
                endpoints.customers.updateCustomer(),
                new CustomerCommands.UpdateCustomer(customerId, customerVersion, customerUpdate));
    }

    /** Helper to save some repetitive code in this class. */
    private CommandRequestBuilder<Customer> createCommandRequest(String url, Command command) {
        return requestFactory.<Customer>createCommandRequest(url, command, new TypeReference<Customer>() {});
    }
}