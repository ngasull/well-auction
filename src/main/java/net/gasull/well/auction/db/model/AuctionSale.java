package net.gasull.well.auction.db.model;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.gasull.well.auction.WellAuction;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

/**
 * The Class AuctionSale.
 */
@Entity
@Table(name = "well_auction_sale")
public class AuctionSale implements Comparable<AuctionSale> {

	/** The sale id. */
	@Id
	private Integer id;

	/** The seller data. */
	@ManyToOne
	private AuctionSellerData sellerData;

	/** The plugin. */
	@Transient
	private WellAuction plugin;

	/** The item (stack). */
	@Transient
	private ItemStack item;

	/** The item serial. */
	@Column(length = 2000)
	private String itemSerial;

	/** The price. */
	private Double price;

	/** The creation date. */
	private Date created = new Date();

	/** The item displayed in shop. */
	@Transient
	private ItemStack tradeStack;

	/** The lock for operations on the sale (buy/sell/price set). */
	@Transient
	private AtomicBoolean lock = new AtomicBoolean(false);

	/** The Constant SALE_ID_PATTERN. */
	private static final Pattern SALE_ID_PATTERN = Pattern.compile(String.format("%s([0-9]+)", AuctionSale.N));

	/** Prefixes item id's. */
	public static final String N = "#";

	/** The Constant LORE_SEPARATOR. */
	public static final String LORE_SEPARATOR = "====================";

	/**
	 * Instantiates a new auction sale.
	 */
	public AuctionSale() {
	}

	/**
	 * Instantiates a new auction sale.
	 * 
	 * @param id
	 *            the id
	 * @param plugin
	 *            the plugin
	 * @param data
	 *            the data
	 * @param stack
	 *            the stack
	 */
	public AuctionSale(int id, WellAuction plugin, AuctionSellerData data, ItemStack stack) {
		this.id = id;
		this.plugin = plugin;
		this.item = stack;
		this.sellerData = data;
	}

	/**
	 * Checks if this sale matches the stack.
	 * 
	 * @param saleStack
	 *            the sale stack
	 * @return true, if is selling stack
	 */
	public boolean isSellingStack(ItemStack saleStack) {
		if (!tradeStack.equals(saleStack) || saleStack.getItemMeta() == null || saleStack.getItemMeta().getLore() == null) {
			return false;
		}

		// Test ID equality
		return tradeStack.getItemMeta().getLore().get(0).equals(saleStack.getItemMeta().getLore().get(0));
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the seller data.
	 * 
	 * @return the seller data
	 */
	public AuctionSellerData getSellerData() {
		return sellerData;
	}

	/**
	 * Sets the seller data.
	 * 
	 * @param sellerData
	 *            the new seller data
	 */
	public void setSellerData(AuctionSellerData sellerData) {
		this.sellerData = sellerData;
	}

	/**
	 * Sets the plugin.
	 * 
	 * @param plugin
	 *            the new plugin
	 */
	public void setPlugin(WellAuction plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * 
	 * @param item
	 *            the new item
	 */
	public void setItem(ItemStack item) {
		this.item = item;
	}

	/**
	 * Gets the item serial.
	 * 
	 * @return the item serial
	 */
	public String getItemSerial() {
		if (getItem() == null) {
			return null;
		}
		this.itemSerial = new Yaml().dump(getItem().serialize());
		return this.itemSerial;
	}

	/**
	 * Sets the item serial.
	 * 
	 * @param serial
	 *            the serial
	 * @return the item stack
	 */
	@SuppressWarnings("unchecked")
	public void setItemSerial(String serial) {
		this.itemSerial = serial;
		Map<String, Object> map = (Map<String, Object>) new Yaml().load(serial);
		ItemStack deserializeObject = (ItemStack) ConfigurationSerialization.deserializeObject(map, ItemStack.class);
		setItem(deserializeObject);
	}

	/**
	 * Gets the auction shop.
	 * 
	 * @return the auction shop
	 */
	public AuctionShop getShop() {
		return sellerData.getShop();
	}

	/**
	 * Gets the price.
	 * 
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 * 
	 * @param price
	 *            the new price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * Gets the created.
	 * 
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Sets the created.
	 * 
	 * @param created
	 *            the new created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * Gets the trade stack.
	 * 
	 * @return the trade stack
	 */
	public ItemStack getTradeStack() {
		return tradeStack;
	}

	/**
	 * Sets the trade stack.
	 * 
	 * @param tradeStack
	 *            the new trade stack
	 */
	public void setTradeStack(ItemStack tradeStack) {
		this.tradeStack = tradeStack;
	}

	/**
	 * Gets the trade stack.
	 * 
	 * @return the trade stack
	 */
	public Double getTradePrice() {
		if (getPrice() == null) {
			if (getSellerData().getDefaultPrice() != null) {
				return getSellerData().getDefaultPrice() * getItem().getAmount();
			}
		} else {
			return getPrice();
		}
		return null;
	}

	/**
	 * Gets the lock.
	 * 
	 * @return the lock
	 */
	public AtomicBoolean getLock() {
		return lock;
	}

	/**
	 * Lock.
	 * 
	 * @return true, if successful
	 */
	public boolean lock() {
		return getLock().compareAndSet(false, true);
	}

	/**
	 * Unlock.
	 */
	public void unlock() {
		getLock().set(false);
	}

	public static Integer idFromTradeStack(ItemStack tradeStack) {
		if (tradeStack.hasItemMeta() && tradeStack.getItemMeta().getLore().size() > 0) {
			String idString = tradeStack.getItemMeta().getLore().get(0);

			Matcher m = SALE_ID_PATTERN.matcher(idString);
			if (m.find()) {
				return Integer.valueOf(m.group(1));
			}
		}

		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getId() ^ (getId() >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AuctionSale)) {
			return false;
		}
		AuctionSale other = (AuctionSale) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AuctionSale sale) {
		Double thisPrice = this.getTradePrice();
		Double theirPrice = sale.getTradePrice();

		if (thisPrice == null) {
			if (theirPrice != null) {
				return 1;
			}
		} else if (theirPrice == null) {
			return -1;
		} else {
			// Ascending price per unit
			int comp = (int) (thisPrice / (double) this.getItem().getAmount() - theirPrice / (double) sale.getItem().getAmount());
			return comp == 0 ? this.getId() - sale.getId() : comp;
		}

		return 0;
	}
}
