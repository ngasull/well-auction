package net.gasull.well.auction.db.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.AuctionSalesCollection;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import com.avaje.ebean.validation.NotNull;

/**
 * The actual Auction Shop, shared all over the world for a given refItem
 * {@link ItemStack}.
 */
@Entity
@Table(name = "well_auction_shop")
public class AuctionShop {

	/** The id. */
	@Id
	private int id;

	/** The ref item. */
	@Transient
	private ItemStack refItem;

	/** The ref item serial. */
	@NotNull
	@Column(length = 2000)
	private String refItemSerial;

	/** The registered shop-entities. */
	@Transient
	private List<ShopEntity> registered = new ArrayList<>();

	/** The stack sizes. */
	@Transient
	private List<Integer> stackSizes;

	/** The sales. */
	@Transient
	private Collection<AuctionSale> sales;

	/**
	 * Instantiates a new auction shop.
	 */
	public AuctionShop() {
	}

	/**
	 * Instantiates a new auction shop.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param stack
	 *            the reference item
	 */
	public AuctionShop(WellAuction plugin, ItemStack stack) {
		setup(plugin);
		setRefItemSerial(new Yaml().dump(refItemFor(stack).serialize()));
	}

	/**
	 * Checks if the shop sells an item.
	 * 
	 * @param item
	 *            the item
	 * @return the check
	 */
	public boolean sells(ItemStack item) {
		return getRefItem().equals(refItemFor(item));
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the shop up.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public void setup(WellAuction plugin) {
		setStackSizes(plugin.wellConfig().getIntegerList("shop.buy.possibleStackSizes", Arrays.asList(1, 4, 8, 16, 32, 64)));
		setSales(new AuctionSalesCollection(this.stackSizes));
	}

	/**
	 * Gets the stack sizes.
	 * 
	 * @return the stack sizes
	 */
	public List<Integer> getStackSizes() {
		return stackSizes;
	}

	/**
	 * Sets the stack sizes.
	 * 
	 * @param stackSizes
	 *            the new stack sizes
	 */
	public void setStackSizes(List<Integer> stackSizes) {
		this.stackSizes = stackSizes;
	}

	/**
	 * Gets the ref item.
	 * 
	 * @return the ref item
	 */
	public ItemStack getRefItem() {
		return refItem;
	}

	/**
	 * Sets the ref item.
	 */
	public void setRefItem(ItemStack refItem) {
		this.refItem = refItem;
	}

	/**
	 * Gets a copy of the ref item.
	 * 
	 * @return the ref item
	 */
	public ItemStack getRefItemCopy() {
		return new ItemStack(getRefItem());
	}

	/**
	 * Gets the ref item serial.
	 * 
	 * @return the ref item serial
	 */
	public String getRefItemSerial() {
		return refItemSerial;
	}

	/**
	 * Sets the ref item serial.
	 * 
	 * @param refItemSerial
	 *            the new ref item serial
	 */
	@SuppressWarnings("unchecked")
	public void setRefItemSerial(String refItemSerial) {
		this.refItemSerial = refItemSerial;
		setRefItem((ItemStack) ConfigurationSerialization.deserializeObject((Map<String, ?>) new Yaml().load(refItemSerial), ItemStack.class));
	}

	/**
	 * Gets the registered shop entities for this shop.
	 * 
	 * @return the shop entities
	 */
	public List<ShopEntity> getRegistered() {
		return registered;
	}

	/**
	 * Sets the registered.
	 * 
	 * @param registered
	 *            the new registered
	 */
	public void setRegistered(List<ShopEntity> registered) {
		this.registered = registered;
	}

	/**
	 * Gets all the sales for the shop.
	 * 
	 * @return the sales
	 */
	public Collection<AuctionSale> getSales() {
		return sales;
	}

	/**
	 * Sets the sales.
	 * 
	 * @param sales
	 *            the new sales
	 */
	public void setSales(Collection<AuctionSale> sales) {
		this.sales = sales;
	}

	@Override
	public String toString() {
		return "AuctionShop [id=" + getId() + ", refItem=" + getRefItemCopy() + "]";
	}

	/**
	 * Provides the reference item for an item.
	 * 
	 * @param item
	 *            the item
	 * @return the reference item
	 */
	public static ItemStack refItemFor(ItemStack item) {
		ItemStack refItem = new ItemStack(item);

		if (refItem.hasItemMeta() && refItem.getItemMeta().getLore() != null && !refItem.getItemMeta().getLore().contains(AuctionSale.LORE_SEPARATOR)) {
			// TODO HANDLE CASE WITH LORE_SEPARATOR IN ACTUAL LORE
			// TODO HANDLE CASE WITH ITEMS HAVING ITEM META
			refItem.setItemMeta(null);
		}

		refItem.setAmount(1);
		return refItem;
	}
}
