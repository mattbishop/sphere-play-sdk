package io.sphere.internal.command;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Address;
import io.sphere.client.shop.model.CustomerName;
import net.jcip.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonFilter;

/** Commands sent to HTTP endpoints for working with customers. */
public class CustomerCommands {
    @Immutable
    public static class CreateCustomer implements Command {
        private final String email;
        private final String password;
        private final String firstName;
        private final String lastName;
        private final String middleName;
        private final String title;
        @Nullable
        private final String anonymousCartId;
        @Nullable
        private final String customerNumber;
        @Nullable
        private final String externalId;
        
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getMiddleName() { return middleName; }
        public String getTitle() { return title; }
        public String getAnonymousCartId() { return anonymousCartId; }
        public String getCustomerNumber() { return customerNumber; }
        public String getExternalId() { return externalId; }

        public CreateCustomer(String email, String password, String firstName, String lastName, String middleName,
                              String title, Optional<String> anonymousCartId, Optional<String> customerNumber, Optional<String> externalId) {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.title = title;
            this.anonymousCartId = anonymousCartId.orNull();
            this.customerNumber = customerNumber.orNull();
            this.externalId = externalId.orNull();
        }

        public CreateCustomer(String email, String password, String firstName, String lastName, String middleName,
                              String title) {
            this(email, password, firstName, lastName, middleName, title, Optional.<String>absent(),
                    Optional.<String>absent(), Optional.<String>absent());
        }
    }

    public static class SignIn implements Command {
        private final String email;
        private final String password;

        public SignIn(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
    
    public static final class SignInWithCart extends SignIn {
        private final String anonymousCartId;

        public SignInWithCart(String email, String password, String anonymousCartId) {
            super(email, password);
            this.anonymousCartId = anonymousCartId;
        }

        public String getAnonymousCartId() { return anonymousCartId; }
    }

    @Immutable
    public static final class ChangePassword extends CommandBase {
        private final String currentPassword;
        private final String newPassword;
        public String getCurrentPassword() { return currentPassword; }
        public String getNewPassword() { return newPassword; }

        public ChangePassword(String id, int version, String currentPassword, String newPassword) {
            super(id, version);
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }
    }

    @Immutable
    public static final class ResetCustomerPassword extends CommandBase {
        private final String tokenValue;
        private final String newPassword;
        public String getTokenValue() { return tokenValue; }
        public String getNewPassword() { return newPassword; }

        public ResetCustomerPassword(String id, int version, String tokenValue, String newPassword) {
            super(id, version);
            this.tokenValue = tokenValue;
            this.newPassword = newPassword;
        }
    }

    @Immutable
    public static final class CreatePasswordResetToken implements Command {
        private final String email;
        private final Integer ttlMinutes;

        public CreatePasswordResetToken(String email, Integer ttlMinutes) {
            this.email = email;
            this.ttlMinutes = ttlMinutes;
        }

        public CreatePasswordResetToken(String email) {
            this(email, null);
        }

        public String getEmail() { return email; }

        public Integer getTtlMinutes() { return ttlMinutes; }
    }

    @Immutable
    public static final class CreateEmailVerificationToken extends CommandBase {
        @JsonProperty("ttlMinutes") private final int ttlMinutes;
        public int getTTLMinutes() { return ttlMinutes; }

        public CreateEmailVerificationToken(String id, int version, int ttlMinutes) {
            super(id, version);
            this.ttlMinutes = ttlMinutes;
        }
    }

    @Immutable
    public static final class VerifyCustomerEmail extends CommandBase {
        private final String tokenValue;
        public String getTokenValue() { return tokenValue; }

        public VerifyCustomerEmail(String id, int version, String tokenValue) {
            super(id, version);
            this.tokenValue = tokenValue;
        }
    }

    // -------------------------
    // Update
    // -------------------------

    public static abstract class CustomerUpdateAction extends UpdateAction {
        public CustomerUpdateAction(String action) {
            super(action);
        }
    }

    @Immutable
    public static final class ChangeName extends CustomerUpdateAction {
        private final String firstName;
        private final String lastName;
        private final String middleName;
        private final String title;
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getMiddleName() { return middleName; }
        public String getTitle() { return title; }

        public ChangeName(CustomerName name) {
            super("changeName");
            this.firstName = name.getFirstName();
            this.lastName = name.getLastName();
            this.middleName = name.getMiddleName();
            this.title = name.getTitle();
        }
    }

    @Immutable
    public static final class ChangeEmail extends CustomerUpdateAction {
        private final String email;
        public String getEmail() { return email; }

        public ChangeEmail(String email) {
            super("changeEmail");
            this.email = email;
        }
    }

    @Immutable
    public static final class AddAddress extends CustomerUpdateAction {
        private final Address address;
        public Address getAddress() { return address; }

        public AddAddress(Address address) {
            super("addAddress");
            this.address = address;
        }
    }

    @Immutable
    public static final class ChangeAddress extends CustomerUpdateAction {
        private final String addressId;
        private final Address address;
        public String getAddressId() { return addressId; }
        public Address getAddress() { return address; }

        public ChangeAddress(String addressId, Address address) {
            super("changeAddress");
            this.addressId = addressId;
            this.address = address;
        }
    }

    @Immutable
    public static final class RemoveAddress extends CustomerUpdateAction {
        private final String addressId;
        public String getAddressId() { return addressId; }

        public RemoveAddress(String addressId) {
            super("removeAddress");
            this.addressId = addressId;
        }
    }

    @Immutable
    public static final class SetDefaultShippingAddress extends CustomerUpdateAction {
        private final String addressId;
        public String getAddressId() { return addressId; }

        public SetDefaultShippingAddress(@Nullable String addressId) {
            super("setDefaultShippingAddress");
            this.addressId = addressId;
        }
    }

    @Immutable
    public static final class SetDefaultBillingAddress extends CustomerUpdateAction {
        private final String addressId;
        public String getAddressId() { return addressId; }

        public SetDefaultBillingAddress(@Nullable String addressId) {
            super("setDefaultBillingAddress");
            this.addressId = addressId;
        }
    }

    @Immutable
    public static final class SetExternalId extends CustomerUpdateAction {
        private final String externalId;

        public SetExternalId(String externalId) {
            super("setExternalId");
            this.externalId = externalId;
        }

        public String getExternalId() {
            return externalId;
        }
    }
}
