package net.gasull.well.auction.shop;

import org.bukkit.inventory.ItemStack;

/**
 * The Class AuctionSale.
 */
public class AuctionSale {

	/** The sale id. */
	private long id;

	/** The seller. */
	private String seller;

	/** The item shop. */
	private AuctionShop shop;

	/** The item (stack). */
	private ItemStack item;

	/** The price. */
	private double price;

	/** The item displayed in shop. */
	private ItemStack tradeStack;

	/**
	 * Instantiates a new auction sale.
	 * 
	 * @param seller
	 *            the seller
	 * @param shop
	 *            the shop
	 * @param stack
	 *            the stack
	 * @param price
	 *            the price
	 */
	public AuctionSale(String seller, AuctionShop shop, ItemStack stack, double price) {
		this.seller = seller;
		this.shop = shop;
		this.item = stack;
		this.price = price;
		this.tradeStack = new ItemStack(stack);
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
	public String getSeller() {
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
	 * Gets the trade stack.
	 * 
	 * @return the trade stack
	 */
	public ItemStack getTradeStack() {
		return tradeStack;
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
