package net.gasull.well.auction.inventory;

import java.util.List;

import net.gasull.well.auction.shop.AuctionSale;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The well-auction sell inventory.
 */
public class AuctionSellInventory {

	/** The size of the selling inventory. */
	public static final int SIZE = 4 * 9;

	/** The item of reference's slot. */
	public static final int REFITEM_SLOT = SIZE - 1;

	public static ItemStack[] generateContents(Material refItem, List<AuctionSale> sales) {
		ItemStack[] contents = new ItemStack[SIZE];

		int i = 0;
		for (AuctionSale sale : sales) {
			if (i == REFITEM_SLOT) {
				continue;
			}

			contents[i++] = new ItemStack(sale.getItem(), sale.getQuantity());
		}

		contents[REFITEM_SLOT] = new ItemStack(refItem);
		return contents;
	}
}
