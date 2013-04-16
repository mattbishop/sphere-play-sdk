package io.sphere.internal.command;

import io.sphere.client.shop.model.Address;
import io.sphere.client.shop.model.CustomerName;
import net.jcip.annotations.Immutable;

/** Commands issued against the HTTP endpoints for working with shopping customers. */
public class CustomerCommands {

    @Immutable
    public static class CreateCustomer implements Command {
        private final String email;
        private final String password;
        private final String firstName;
        private final String lastName;
        private final String middleName;
        private final String title;


        public CreateCustomer(String email, String password, String firstName, String lastName, String middleName, String title) {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.title = title;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getMiddleName() { return middleName; }
        public String getTitle() { return title; }
    }

    public static final class CreateCustomerWithCart extends CreateCustomer {
        private final String cartId;
        private final int cartVersion;

        public CreateCustomerWithCart(
                String email,
                String password,
                String firstName,
                String lastName,
                String middleName,
                String title,
                String cartId,
                int cartVersion) {
            super(email, password, firstName, lastName, middleName, title);
            this.cartId = cartId;
            this.cartVersion = cartVersion;
        }

        public String getCartId() {
            return cartId;
        }

        public int getCartVersion() {
            return cartVersion;
        }
    }

    @Immutable
    public static final class ChangePassword extends CommandBase {
        private final String currentPassword;
        private final String newPassword;

        public ChangePassword(String id, int version, String currentPassword, String newPassword) {
            super(id, version);
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        public String getCurrentPassword() { return currentPassword; }
        public String getNewPassword() { return newPassword; }
    }

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

        public ChangeName(CustomerName name) {
            super("changeName");
            this.firstName = name.getFirstName();
            this.lastName = name.getLastName();
            this.middleName = name.getMiddleName();
            this.title = name.getTitle();
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getMiddleName() { return middleName; }
        public String getTitle() { return title; }
    }

    @Immutable
    public static final class ChangeEmail extends CustomerUpdateAction {
        private final String email;

        public ChangeEmail(String email) {
            super("changeEmail");
            this.email = email;
        }

        public String getEmail() { return email; }
    }

    @Immutable
    public static final class AddAddress extends CustomerUpdateAction {
        private final Address address;

        public AddAddress(Address address) {
            super("addAddress");
            this.address = address;
        }

        public Address getAddress() { return address; }
    }

    @Immutable
    public static final class ChangeAddress extends CustomerUpdateAction {
        private final String addressIndex;
        private final Address address;

        public ChangeAddress(String addressIndex, Address address) {
            super("changeAddress");
            this.addressIndex = addressIndex;
            this.address = address;
        }

        public String getAddressIndex() { return addressIndex; }
        public Address getAddress() { return address; }

    }

    @Immutable
    public static final class RemoveAddress extends CustomerUpdateAction {
        private final String addressIndex;

        public RemoveAddress(String addressIndex) {
            super("removeAddress");
            this.addressIndex = addressIndex;
        }

        public String getAddressIndex() { return addressIndex; }
    }

    @Immutable
    public static final class SetDefaultShippingAddress extends CustomerUpdateAction {
        private final String addressIndex;

        public SetDefaultShippingAddress(String addressIndex) {
            super("setDefaultShippingAddress");
            this.addressIndex = addressIndex;
        }

        public String getAddressIndex() { return addressIndex; }
    }

    @Immutable
    public static final class SetDefaultBillingAddress extends CustomerUpdateAction {
        private final String addressIndex;

        public SetDefaultBillingAddress(String addressIndex) {
            super("setDefaultBillingAddress");
            this.addressIndex = addressIndex;
        }

        public String getAddressIndex() { return addressIndex; }
    }

    @Immutable
    public static final class ResetCustomerPassword extends CommandBase {
        private final String tokenValue;
        private final String newPassword;

        public ResetCustomerPassword(String id, int version, String tokenValue, String newPassword) {
            super(id, version);
            this.tokenValue = tokenValue;
            this.newPassword = newPassword;
        }

        public String getTokenValue() {
            return tokenValue;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }

    @Immutable
    public static final class CreatePasswordResetToken implements Command {
        private final String email;

        public CreatePasswordResetToken(String email) {

            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    @Immutable
    public static final class CreateEmailVerificationToken extends CommandBase {
        private final int ttlMinutes;

        public CreateEmailVerificationToken(String id, int version, int ttlMinutes) {
            super(id, version);
            this.ttlMinutes = ttlMinutes;
        }

        public int getTTLMinutes() {
            return ttlMinutes;
        }
    }

    @Immutable
    public static final class VerifyCustomerEmail extends CommandBase {
        private final String tokenValue;

        public VerifyCustomerEmail(String id, int version, String tokenValue) {

            super(id, version);
            this.tokenValue = tokenValue;
        }

        public String getTokenValue() {
            return tokenValue;
        }
    }
}
