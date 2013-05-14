### v0.33

##### Play SDK

API

* Renamed `CurrentCustomer.updateCustomer` to `CurrentCustomer.update`.
* Renamed `CurrentCart.unsetShippingAddress[Async]` to `clearShippingAddress[Async]`.
* All service methods that used to accept the `(id, version)` pair now accept a `VersionedId`.
* Merged the class `SphereClient` into `Sphere`.
* All service methods that modify some backend state now have two versions: synchronous and asynchronous. This is to
make calling `service.update(...)` less error prone: previously such call would do nothing unless you said
`service.update(...).execute()`.

##### Java client

API

* Changed the type of variant id from `String` to `int`.
* Annotated all data object methods that are guaranteed to never return null with `@Nonnull`.
* Added a `getIdAndVersion` method to all versioned data objects. This method returns a `VersionedId` which is an id plus version.
Removed the `getVersion` method, but kept `getId` for convenience because it is often needed.
* All service methods that used to accept a `(id, version)` pair now accept a `VersionedId`.
* Removed `SphereClient` and `AppClient` that don't have a use (yet).
* Renamed `ShopClient` to `SphereClient`.
* Added `LineItemContainer.customerEmail`.
* Order now has a `getCurrency` method.
* Renamed `OrderService.orderCart` to `createOrder`.
* Removed methods to get individual parts of Customer's name. Use `Customer.getName`.
* Removed `ScaledImage.getScaleRatio`, added 'Image.isSizeAvailable(size)'.
* Renamed `[Comment|Review]Update.setAuthorName` to `setAuthor`.
* Renamed `[Comment|Review].getAuthorName` to `getAuthor`.
* Renamed `CustomerUpdate.unsetDefault[Shipping|Billing]Address` to `clearDefault[Shipping|Billing]Address`.

MISC

* Improved Javadoc

### v0.32

##### Play SDK

API

* Config key  `sphere.api.mode` renamed to `sphere.products.mode`. Valid values are `"published"`, `"staged"`.
* Config keys `sphere.cartCurrency`, `sphere.cartInventoryMode` are now `sphere.cart.currency`, `sphere.cart.inventoryMode`.
* Renamed `checkoutSummaryId` to `checkoutSnapshotId`.
* `resetPassword1 is now a method of CustomerService.

MISC

* Removed test dependency on Scalamock.

##### Java client

API

* `CustomerService.signup()` and `signupWithCart()` now accept a `CustomerName` object.

FIX

* Customer update actions now match the backend by referring to addresses by ids.

MISC

* Removed dependency on Scala standard library.
* Removed test dependency on Scalamock.