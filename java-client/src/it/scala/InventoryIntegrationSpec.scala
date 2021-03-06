package sphere

import org.scalatest._
import io.sphere.client.shop.model._
import sphere.IntegrationTest._
import org.joda.time.DateTime
import com.google.common.base.Optional
import io.sphere.client.exceptions.{SphereBackendException, DuplicateSkuException}
import io.sphere.client.shop.model.ChannelRoles.InventorySupply
import sphere.Fixtures._
import io.sphere.client.model.{Reference, EmptyReference}
import scala.collection.JavaConversions._
import com.google.common.collect.Sets._

class InventoryIntegrationSpec extends WordSpec with MustMatchers {
  implicit lazy val client = IntegrationTestClient()
  val quantity1 = 500
  def randomSku() = "sku-" + randomString
  def createEntry: InventoryEntry = client.inventory.createInventoryEntry(randomSku, quantity1).execute()

  val data = new AnyRef {
    val quantityOnStock = 3
    val restockableInDays = 5
    val expectedDelivery = new DateTime().plusDays(4)
  }

  "sphere client" must {
    "Add an inventory entry" in {
      val sku = randomSku
      val inventoryEntry = client.inventory.createInventoryEntry(sku, quantity1).execute()
      inventoryEntry.getSku  must be (sku)
      inventoryEntry.getQuantityOnStock must be (quantity1)
      inventoryEntry.getAvailableQuantity must be (quantity1)
    }

    "Add an inventory entry with restockableInDays and expectedDelivery" in {
      val sku = randomSku
      val quantityOnStock = 3
      val restockableInDays = 5
      val expectedDelivery = new DateTime().plusDays(4)
      val inventoryEntry = client.inventory.createInventoryEntry(sku, quantityOnStock, restockableInDays, expectedDelivery).execute()
      inventoryEntry.getQuantityOnStock must be (quantityOnStock)
      inventoryEntry.getAvailableQuantity must be (quantityOnStock)
      inventoryEntry.getSku must be (sku)
      inventoryEntry.getRestockableInDays must be (Optional.of(5))
      inventoryEntry.getExpectedDelivery.getMillis must be (expectedDelivery.getMillis)
      inventoryEntry.getSupplyChannel must be (Channel.emptyReference())
    }

    "Add an inventory entry with supply channel" in {
      val supplyChannel = newSupplyChannel
      val sku = randomSku
      import data._
      val inventoryEntry = client.inventory.createInventoryEntry(sku, quantityOnStock, restockableInDays, expectedDelivery, supplyChannel.getId).execute()
      inventoryEntry.getQuantityOnStock must be (quantityOnStock)
      inventoryEntry.getAvailableQuantity must be (quantityOnStock)
      inventoryEntry.getSku must be (sku)
      inventoryEntry.getRestockableInDays must be (Optional.of(restockableInDays))
      inventoryEntry.getExpectedDelivery.getMillis must be (expectedDelivery.getMillis)
      inventoryEntry.getSupplyChannel must be (Reference.create("channel", supplyChannel.getId))
    }

    "Don't allow to insert inventory with already existing SKU" in {
      val sku = randomSku
      def executeCreateCommand {
        client.inventory.createInventoryEntry(sku, 1).execute()
      }
      executeCreateCommand
      intercept[DuplicateSkuException] {
        executeCreateCommand
      }
    }

    "Don't allow to insert inventory with already existing SKU in the same supply channel" in {
      val sku = randomSku
      val supplyChannel = newSupplyChannel
      import data._
      def executeCreateCommand {
        client.inventory.createInventoryEntry(sku, quantityOnStock, restockableInDays, expectedDelivery, supplyChannel.getId).execute()
      }
      executeCreateCommand
      intercept[DuplicateSkuException] {
        executeCreateCommand
      }
    }

    "Add stock to inventory entry" in {
      val inventoryEntry = client.inventory.createInventoryEntry(randomSku, 1).execute()
      inventoryEntry.getAvailableQuantity must be(1)
      val updatedInventoryEntry = client.inventory.updateInventoryEntry(inventoryEntry.getIdAndVersion,
        new InventoryEntryUpdate().addQuantity(4)).execute()
      updatedInventoryEntry.getAvailableQuantity must be(5)
    }

    "Remove stock from inventory entry" in {
      val inventoryEntry = client.inventory.createInventoryEntry(randomSku, 5).execute()
      inventoryEntry.getAvailableQuantity must be(5)
      val updatedInventoryEntry = client.inventory.updateInventoryEntry(inventoryEntry.getIdAndVersion,
        new InventoryEntryUpdate().removeQuantity(3)).execute()
      updatedInventoryEntry.getAvailableQuantity must be(2)
    }

    "Set restockableInDays" in {
      val inventoryEntry = client.inventory.createInventoryEntry(randomSku, 5).execute()
      inventoryEntry.getRestockableInDays must be(Optional.absent())
      val updatedInventoryEntry = client.inventory.updateInventoryEntry(inventoryEntry.getIdAndVersion,
        new InventoryEntryUpdate().setRestockableInDays(3)).execute()
      updatedInventoryEntry.getRestockableInDays must be(Optional.of(3))
    }

    "Set expectedDelivery" in {
      val inventoryEntry = client.inventory.createInventoryEntry(randomSku, 5).execute()
      inventoryEntry.getExpectedDelivery must be(null)
      val date = new DateTime().plusDays(3)
      val updatedInventoryEntry = client.inventory.updateInventoryEntry(inventoryEntry.getIdAndVersion,
        new InventoryEntryUpdate().setExpectedDelivery(date)).execute()
      updatedInventoryEntry.getExpectedDelivery.getMillis must be(date.getMillis)
    }

    "Get inventory entry by id" in {
      val inventoryEntry = client.inventory.createInventoryEntry(Fixtures.randomString(), 10).execute()
      client.inventory.query.where(s"""id="${inventoryEntry.getId}"""").fetch().getResults.get(0) must be (inventoryEntry)
    }

    "List inventory entries by SKU" in {
      import Fixtures._
      val uniqueString = Fixtures.randomString()//otherwise SKU/channel key is already in use
      val wh1 = Fixtures.newChannel("WH1" + uniqueString, newHashSet(InventorySupply))
      val wh2 = Fixtures.newChannel("WH2" + uniqueString, newHashSet(InventorySupply))


      val a = newInventoryEntry("S1" + uniqueString, 11)
      val b =newInventoryEntry("S1" + uniqueString, 23, wh1)
      newInventoryEntry("S2" + uniqueString, 15, wh1)
      val c = newInventoryEntry("S1" + uniqueString, 11, wh2)
      newInventoryEntry("S3" + uniqueString, 1, wh2)

      val results = client.inventory.queryBySku("S1" + uniqueString).fetch.getResults
      results must have size(3)
      results must contain(a)
      results must contain(b)
      results must contain(c)
    }

    "find an inventory entry by sku without having a supply channel" in {
      val createdEntry = createEntry
      val inventoryEntry = client.inventory.bySku(createdEntry.getSku).fetch.get
      inventoryEntry must beSimilar[InventoryEntry](createdEntry, _.getSku, _.getAvailableQuantity, _.getQuantityOnStock)
    }
    
    "find an inventory entry by sku in a supply channel" in {
      val sku = randomSku
      val supplyChannel = newSupplyChannel
      import data._
      val inventoryEntryWithChannel = client.inventory.createInventoryEntry(sku, 2, restockableInDays, expectedDelivery, supplyChannel.getId).execute()
      client.inventory.createInventoryEntry(sku, 1, 1, expectedDelivery).execute() //test to distract searcher with same item without channel
                 
      val inventoryEntry = client.inventory.bySku(inventoryEntryWithChannel.getSku, supplyChannel.getId).fetch.get
      inventoryEntryWithChannel must beSimilar[InventoryEntry](inventoryEntry, _.getSku, _.getAvailableQuantity, _.getQuantityOnStock)
    }

    "find all inventory entries of all channels by sku" in {
      import data._

      val supplyChannelA = newSupplyChannel
      val supplyChannelB = newSupplyChannel //channel to test if search does not simply return all
      def createInventoryEntry(sku: String, channel: Option[Channel]) = client.inventory.createInventoryEntry(sku, 2, restockableInDays, expectedDelivery, channel.map(_.getId).getOrElse(null)).execute()

      val sku1 = randomSku
      val sku2 = randomSku
      Seq(sku1, sku2).foreach { sku =>
        createInventoryEntry(sku, None)
        createInventoryEntry(sku, Option(supplyChannelA))
        createInventoryEntry(sku, Option(supplyChannelB))
      }
      val results = client.inventory().queryBySku(sku1).fetch().getResults
      results.size must be (3)
      results.filter(_.getSku == sku1).size must be (3)
      results.filter(_.getSku == sku2).size must be (0)
      results.map(_.getId).toSet.size must be (3)
    }

    "Add a channel" in {
      val key = "CHANNEL-" + randomString
      val supplyChannel = client.channels.create(key).execute()
      supplyChannel.getKey must be(key)
      supplyChannel.getIdAndVersion must not be(null)
      supplyChannel.getRoles.head must be(InventorySupply)
    }

    "Set a new key" in {
      val supplyChannel = newSupplyChannel
      val newKey = "CHANNEL-" + randomString
      val updated = client.channels().updateChannel(supplyChannel.getIdAndVersion,
        new ChannelUpdate().changeKey(newKey)).execute()
      updated.getKey must be(newKey)
    }

    "Add a role to a channel" in {
      val channel = Fixtures.newChannel()
      channel.getRoles must be (newHashSet(ChannelRoles.InventorySupply))
      val role = ChannelRoles.Primary
      val updated = client.channels().updateChannel(channel.getIdAndVersion, new ChannelUpdate().addRole(role)).execute()
      updated.getRoles must be (newHashSet(role, ChannelRoles.InventorySupply))
    }

    "Remove a role" in {
      val initialRoles = newHashSet(ChannelRoles.Primary, ChannelRoles.InventorySupply)
      val channel = Fixtures.newChannel(initialRoles)
      channel.getRoles must be (initialRoles)
      val updated = client.channels().updateChannel(channel.getIdAndVersion, new ChannelUpdate().removeRole(ChannelRoles.Primary)).execute()
      updated.getRoles must be (newHashSet(ChannelRoles.InventorySupply))
    }

    "Set roles" in {
      val channel = Fixtures.newChannel()
      channel.getRoles must be (newHashSet(ChannelRoles.InventorySupply))
      val role = ChannelRoles.Primary
      val updated = client.channels().updateChannel(channel.getIdAndVersion, new ChannelUpdate().setRole(role)).execute()
      updated.getRoles must be (newHashSet(role))
    }

    "Add supply Channel to cart and order (master variant)" in {
      val supplyChannel = newSupplyChannel
      val product = oneProduct
      val cart = newCart

      val updatedCart = client.carts().updateCart(cart.getIdAndVersion, new CartUpdate().addLineItem(1, product.getId, supplyChannel.getId)).execute()
      val lineItem = updatedCart.getLineItems()(0)
      lineItem.getSupplyChannel.getId must be(supplyChannel.getId)
    }

    "Add supply Channel to cart and order (variant)" in {
      val supplyChannel = newSupplyChannel
      val product = oneProduct
      val cart = newCart
      val variantId = product.getVariants.head.getId
      val updatedCart = client.carts().updateCart(cart.getIdAndVersion, new CartUpdate().addLineItem(1, product.getId, variantId, supplyChannel.getId)).execute()
      val lineItem = updatedCart.getLineItems()(0)
      lineItem.getSupplyChannel.getId must be(supplyChannel.getId)
    }
  }

}
