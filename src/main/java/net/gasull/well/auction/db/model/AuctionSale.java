package net.gasull.well.auction.db.model;

import java.util.Date;
import java.util.Map;

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
	 * Gets the seller.
	 * 
	 * @return the seller
	 */
	public AuctionPlayer getSeller() {
		return sellerData.getAuctionPlayer();
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
		if (item == null) {
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
		if (price == null) {
			if (sellerData.getDefaultPrice() != null) {
				return sellerData.getDefaultPrice() * item.getAmount();
			}
		} else {
			return price;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (id != other.id) {
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
			int comp = (int) (thisPrice / (double) this.item.getAmount() - theirPrice / (double) sale.item.getAmount());
			return comp == 0 ? this.id - sale.id : comp;
		}

		return 0;
	}
}
