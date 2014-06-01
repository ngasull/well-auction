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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.Yaml;

/**
 * The Class AuctionSale.
 */
@Entity
@Table(name = "well_auction_sale")
public class AuctionSale {

	/** The sale id. */
	@Id
	private Integer id;

	/** The seller data id. */
	private Integer sellerDataId;

	/** The seller data. */
	@Transient
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

	/** The item displayed in shop. */
	@Transient
	private ItemStack tradeStack;

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
	 * @param plugin
	 *            the plugin
	 * @param seller
	 *            the seller
	 * @param auctionShop
	 *            the shop
	 * @param stack
	 *            the stack
	 */
	public AuctionSale(WellAuction plugin, AuctionSellerData data, ItemStack stack) {
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
		return tradeStack.equals(saleStack);
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
	 * Gets the seller data id.
	 * 
	 * @return the seller data id
	 */
	public Integer getSellerDataId() {
		return sellerDataId;
	}

	/**
	 * Sets the seller data id.
	 * 
	 * @param sellerDataId
	 *            the new seller data id
	 */
	public void setSellerDataId(Integer sellerDataId) {
		this.sellerDataId = sellerDataId;
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

		if (sellerData != null) {
			sellerData.getShop().refreshPrice(this);
		}
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

	/**
	 * Refresh the displayed stack.
	 */
	void refresh() {
		if (plugin == null || sellerData == null) {
			return;
		}

		this.tradeStack = new ItemStack(this.item);
		ItemMeta realMeta = this.tradeStack.getItemMeta();
		ItemMeta meta = this.item.getItemMeta();

		if (!this.tradeStack.hasItemMeta()) {
			meta = Bukkit.getItemFactory().getItemMeta(this.tradeStack.getType());
		}

		List<String> desc;
		if (this.tradeStack.hasItemMeta() && realMeta.getLore() != null && !realMeta.getLore().isEmpty()) {
			desc = realMeta.getLore();
			desc.add(LORE_SEPARATOR);
		} else {
			desc = new ArrayList<>();
		}

		desc.add(ChatColor.DARK_GRAY + "#" + id);

		if (getTradePrice() == null) {
			desc.add(this.plugin.wellConfig().getString("lang.shop.item.noPrice", "No price set up yet!"));
		} else {
			desc.add(ChatColor.GREEN + this.plugin.economy().format(getTradePrice()));
		}

		String playerName = sellerData.getAuctionPlayer().getName();
		desc.add(ChatColor.BLUE
				+ this.plugin.wellConfig().getString("lang.shop.item.soldBy", "Sold by %player%").replace("%player%", playerName == null ? "???" : playerName));

		meta.setLore(desc);
		this.tradeStack.setItemMeta(meta);
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
}
