package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.avaje.ebean.validation.NotNull;

/**
 * The actual Auction Shop, shared all over the world for a given refItem
 * {@link ItemStack}.
 */
@Entity
@Table(name = "well_auction_shop")
public class AuctionShop {

	/** The plugin. */
	@Transient
	private WellAuction plugin;

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
	private List<AuctionSellerData> sellerData = new ArrayList<>();

	/** The registered shop-entities. */
	@Transient
	private List<ShopEntity> registered = new ArrayList<>();

	/** The sales. */
	@Transient
	private List<AuctionSale> sales = new ArrayList<>();

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
	AuctionShop(WellAuction plugin, ItemStack stack) {
		this.plugin = plugin;
		this.refItem = refItemFor(stack);
		this.refItemSerial = new Yaml().dump(refItem.serialize());
	}

	/**
	 * Register entity for it to "contain" the shop.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shopEntity
	 *            the shop entity
	 */
	void registerEntity(JavaPlugin plugin, ShopEntity shopEntity) {
		shopEntity.register(plugin, this);
		registered.add(shopEntity);
	}

	/**
	 * Sell.
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 * @return the auction sale
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	AuctionSale sell(AuctionPlayer player, ItemStack item) throws AuctionShopException {

		Double defaultPrice = player.getSellerData(this).getDefaultPrice();
		if (defaultPrice != null && defaultPrice < 0) {
			throw new AuctionShopException("Can't sell for a price less than 0");
		}

		AuctionSale sale = new AuctionSale(plugin, player.getSellerData(this), item);
		player.getSales(this).add(sale);

		if (defaultPrice != null) {
			sale.setPrice(defaultPrice * sale.getItem().getAmount());
		}

		return sale;
	}

	/**
	 * Buy.
	 * 
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 * @return the bought stack
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	ItemStack buy(OfflinePlayer player, AuctionSale sale) throws AuctionShopException {
		if (!sales.remove(sale)) {
			throw new AuctionShopException("Sale not found but should have been");
		}

		sales.remove(sale);
		sale.getSeller().getSales(this).remove(sale);

		return sale.getItem();
	}

	/**
	 * Fetches a sale for stack.
	 * 
	 * @param saleStack
	 *            the sale stack
	 * @return the auction sale
	 */
	public AuctionSale saleForStack(ItemStack saleStack) {
		AuctionSale sale = null;

		for (AuctionSale s : sales) {
			if (s.isSellingStack(saleStack)) {
				sale = s;
				break;
			}
		}

		return sale;
	}

	/**
	 * Checks if the shop sells an item.
	 * 
	 * @param item
	 *            the item
	 * @return the check
	 */
	public boolean sells(ItemStack item) {
		return refItem.equals(refItemFor(item));
	}

	/**
	 * Sets the new sale price for a player.
	 * 
	 * @param player
	 *            the player
	 * @param price
	 *            the price
	 */
	public void setDefaultPrice(AuctionPlayer player, double price) {
		player.getSellerData(this).setDefaultPrice(price);
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
	 * Sets the plugin.
	 * 
	 * @param plugin
	 *            the new plugin
	 */
	void setPlugin(WellAuction plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the ref item.
	 * 
	 * @return the ref item
	 */
	public ItemStack getRefItem() {
		return new ItemStack(refItem);
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
		this.refItem = (ItemStack) ConfigurationSerialization.deserializeObject((Map<String, ?>) new Yaml().load(refItemSerial), ItemStack.class);
	}

	/**
	 * Gets the seller data.
	 * 
	 * @return the seller data
	 */
	public List<AuctionSellerData> getSellerData() {
		return sellerData;
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
	public List<AuctionSale> getSales() {
		return sales;
	}

	/**
	 * Gets the sales of a player.
	 * 
	 * @param player
	 *            the player
	 * @return the sales
	 */
	public List<AuctionSale> getSalesOf(AuctionPlayer player) {
		return player.getSales(this);
	}

	@Override
	public String toString() {
		return "AuctionShop [id=" + id + ", refItem=" + refItem + "]";
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
