package net.gasull.well.auction.inventory;

import java.util.List;

import net.gasull.well.auction.shop.AuctionSale;

import org.bukkit.inventory.ItemStack;

/**
 * The well-auction sell inventory.
 */
public class AuctionSellInventory {

	/** The size of the selling inventory. */
	public static final int SIZE = 4 * 9;

	/**
	 * Generate contents.
	 * 
	 * @param playerSales
	 *            the player sales
	 * @return the item stack[]
	 */
	public static ItemStack[] generateContents(List<AuctionSale> playerSales) {
		ItemStack[] contents = new ItemStack[SIZE];

		if (playerSales != null) {
			int i = 0;
			for (AuctionSale sale : playerSales) {
				contents[i++] = sale.getTradeStack();
			}
		}

		return contents;
	}
}
