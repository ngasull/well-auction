package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.List;

import net.gasull.well.auction.WellAuction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The Class AuctionSale.
 */
public class AuctionSale {

	private static long TMP_INC = 0;

	/** The sale id. */
	private long id;

	/** The seller. */
	private AuctionPlayer seller;

	/** The plugin. */
	private WellAuction plugin;

	/** The item shop. */
	private AuctionShop shop;

	/** The item (stack). */
	private ItemStack item;

	/** The price. */
	private Double price;

	/** The item displayed in shop. */
	private ItemStack tradeStack;

	/** The Constant LORE_SEPARATOR. */
	public static final String LORE_SEPARATOR = "====================";

	/**
	 * Instantiates a new auction sale.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param seller
	 *            the seller
	 * @param shop
	 *            the shop
	 * @param stack
	 *            the stack
	 * @param price
	 *            the price
	 */
	public AuctionSale(WellAuction plugin, AuctionPlayer seller, AuctionShop shop, ItemStack stack, double price) {
		this(plugin, seller, shop, stack);
		setPrice(price);
	}

	/**
	 * Instantiates a new auction sale.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param seller
	 *            the seller
	 * @param shop
	 *            the shop
	 * @param stack
	 *            the stack
	 */
	public AuctionSale(WellAuction plugin, AuctionPlayer seller, AuctionShop shop, ItemStack stack) {
		this.plugin = plugin;
		this.id = TMP_INC++;
		this.seller = seller;
		this.shop = shop;
		this.item = stack;

		refresh();
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
	public long getId() {
		return id;
	}

	/**
	 * Gets the seller.
	 * 
	 * @return the seller
	 */
	public AuctionPlayer getSeller() {
		return seller;
	}

	/**
	 * Gets the auction shop.
	 * 
	 * @return the auction shop
	 */
	public AuctionShop getShop() {
		return shop;
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
	 * Gets the price.
	 * 
	 * @return the price
	 */
	public double getPrice() {
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
		refresh();

		if (shop.getSales().contains(this)) {
			shop.getSales().remove(this);
		}

		if (price != null && price >= 0) {
			shop.getSales().add(this);
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
	 * Refresh the displayed stack.
	 */
	private void refresh() {
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

		if (this.price == null) {
			desc.add(this.plugin.wellConfig().getString("lang.shop.item.noPrice", "No price set up yet!"));
		} else {
			desc.add(ChatColor.GREEN + this.plugin.economy().format(this.price));
		}

		desc.add(ChatColor.BLUE
				+ this.plugin.wellConfig().getString("lang.shop.item.soldBy", "Sold by %player%").replace("%player%", this.seller.getPlayer().getName()));

		meta.setLore(desc);
		this.tradeStack.setItemMeta(meta);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
