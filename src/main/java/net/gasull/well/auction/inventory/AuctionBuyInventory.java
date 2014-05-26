package net.gasull.well.auction.inventory;

import java.util.List;

import net.gasull.well.auction.shop.AuctionSale;

import org.bukkit.inventory.ItemStack;

/**
 * The well-auction buy inventory.
 */
public class AuctionBuyInventory {

	/** The size of the selling inventory. */
	public static final int SIZE = 6 * 9;

	/**
	 * Generate contents.
	 * 
	 * @param sales
	 *            the sales
	 * @return the item stack[]
	 */
	public static ItemStack[] generateContents(List<AuctionSale> sales) {
		ItemStack[] contents = new ItemStack[SIZE];

		if (sales != null) {
			int i = 0;
			for (AuctionSale sale : sales) {
				contents[i++] = sale.getTradeStack();
			}
		}

		return contents;
	}
}
