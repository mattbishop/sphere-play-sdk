package sphere;

import java.util.Currency;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.sphere.internal.util.Log;
import io.sphere.internal.util.Util;
import io.sphere.client.shop.*;
import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Customer;
import io.sphere.client.shop.model.CustomerName;
import play.libs.F.Promise;
import sphere.internal.*;
import sphere.util.Async;
import sphere.util.IdWithVersion;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import net.jcip.annotations.ThreadSafe;

import javax.annotation.Nullable;

/** Client for accessing all Sphere APIs.
 *
 *  <p>To obtain an instance of this class designed to be shared by all the controllers in your application,
 *  use the static method {@link Sphere#getClient}. */
@ThreadSafe
public class SphereClient {
    private final ShopClient shopClient;
    private final Currency cartCurrency;
    private final Cart.InventoryMode cartInventoryMode;

    /** Sphere HTTP API for working with products in a given project.. */
    public final sphere.ProductService products;
    /** All categories in the project, represented as an in-memory tree. */
    public final CategoryTree categories;
    /** API for working with orders. */
    public final sphere.OrderService orders;
    /** Sphere HTTP API for working with customers in a given project.
     *  Use {@link #currentCustomer()} for working with the currently logged in customer. */
    public final sphere.CustomerService customers;
    /** API for product reviews. */
    public final sphere.ReviewService reviews;
    /** API for product comments. */
    public final sphere.CommentService comments;
    /** API for product inventory. */
    public final sphere.InventoryService inventory;

    SphereClient(Config sphereConfig, ShopClient shopClient) {
        cartCurrency = sphereConfig.cartCurrency();
        cartInventoryMode = sphereConfig.cartInventoryMode();
        this.shopClient = shopClient;
        categories = this.shopClient.categories();
        products = new ProductServiceAdapter(this.shopClient.products());
        orders = new OrderServiceAdapter(this.shopClient.orders());
        customers = new CustomerServiceAdapter(this.shopClient.customers());
        comments = new CommentServiceAdapter(this.shopClient.comments());
        reviews = new ReviewServiceAdapter(this.shopClient.reviews());
        inventory = new InventoryServiceAdapter(this.shopClient.inventory());
    }

    /** Cart object for the current session.
     *
     *  @return A cart object if a customer is logged in. Dummy cart object with default values otherwise. */
    public CurrentCart currentCart() {
        return new CurrentCart(shopClient.carts(), cartCurrency, cartInventoryMode);
    }

    /** Returns true if a customer is currently logged in, false otherwise.
     *
     * This is equivalent to checking for {@code currentCustomer() != null} but more readable. */
    public boolean isLoggedIn() {
        return currentCustomer() != null;
    }

    /** Customer object for the current session.
     *
     *  @return The current customer if a customer is logged in, null otherwise. */
    public CurrentCustomer currentCustomer() {
       return CurrentCustomer.createFromSession(
               shopClient.customers(), shopClient.orders(),
               shopClient.comments(), shopClient.reviews());
    }

    /** Authenticates a customer and stores customer id in the session.
     *
     *  If login was successful, {@link #currentCustomer()} will always return a {@link CurrentCustomer} instance,
     *  until {@link #logout()} is called.
     *
     *  @return True if a customer with given email and password exists, false otherwise. */
    public boolean login(String email, String password) {
        return Async.await(loginAsync(email, password)).isPresent();
    }

    /** Authenticates an existing customer asynchronously and store customer id in the session when finished. */
    public Promise<Optional<AuthenticatedCustomerResult>> loginAsync(String email, String password) {
        if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Please provide a non-empty email and password.");
        }
        Log.trace(String.format("[login] Logging in user with email %s.", email));
        Session session = Session.current();
        IdWithVersion sessionCartId = session.getCartId();
        ListenableFuture<Optional<AuthenticatedCustomerResult>> future;
        if (sessionCartId == null) {
            future = shopClient.customers().byCredentials(email, password).fetchAsync();
        } else {
            future = shopClient.carts().loginWithAnonymousCart(
                    sessionCartId.getId(), sessionCartId.getVersion(), email, password).executeAsync();
        }
        return Async.asPlayPromise(Session.withCustomerAndCartOptional(future, session));
    }

    /** Creates a new customer and authenticates the customer (you don't need  {@link #login}). */
    public Customer signup(String email, String password, CustomerName customerName) {
        return Async.await(signupAsync(email, password, customerName));
    }

    /** Creates a new customer asynchronously and authenticates the customer (calls {@link #login}). */
    public Promise<Customer> signupAsync(String email, String password, CustomerName customerName) {
        Log.trace(String.format("[signup] Signing up user with email %s.", email));
        Session session = Session.current();
        IdWithVersion sessionCartId = session.getCartId();
        ListenableFuture<Customer> customerFuture;
        if (sessionCartId == null) {
             customerFuture = Session.withCustomerIdAndVersion(
                     shopClient.customers().signup(
                             email,
                             password,
                             customerName.getFirstName(),
                             customerName.getLastName(),
                             customerName.getMiddleName(),
                             customerName.getTitle()
                     ).executeAsync(),
                     session);
        } else {
            ListenableFuture<AuthenticatedCustomerResult> signupFuture = Session.withCustomerAndCart(
                    shopClient.customers().signupWithCart(
                            email,
                            password,
                            customerName.getFirstName(),
                            customerName.getLastName(),
                            customerName.getMiddleName(),
                            customerName.getTitle(),
                            sessionCartId.getId(),
                            sessionCartId.getVersion()
                    ).executeAsync(),
                    session);
            customerFuture = Futures.transform(signupFuture, new Function<AuthenticatedCustomerResult, Customer>() {
                public Customer apply(@Nullable AuthenticatedCustomerResult result) {
                    assert result != null;
                    return result.getCustomer();
                }
            });
        }
        return Async.asPlayPromise(customerFuture);
    }

    /** Removes the customer and cart information from the session.
     *
     *  <p>After calling logout, {@link #currentCustomer()} will return null.
     *  Make sure you don't keep old {@link CurrentCustomer} instances around as they become invalid after logout. */
    public void logout() {
        Session session = Session.current();
        session.clearCustomer();
        session.clearCart();
    }
}
