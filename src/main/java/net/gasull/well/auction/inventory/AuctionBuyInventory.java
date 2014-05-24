package net.gasull.well.auction.inventory;

import java.util.List;

import net.gasull.well.auction.shop.AuctionSale;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The well-auction buy inventory.
 */
public class AuctionBuyInventory {

	/** The size of the selling inventory. */
	public static final int SIZE = 6 * 9;

	/** The item of reference's slot. */
	public static final int REFITEM_SLOT = SIZE - 10;

	/**
	 * Generate contents.
	 * 
	 * @param refItem
	 *            the ref item
	 * @param sales
	 *            the sales
	 * @return the item stack[]
	 */
	public static ItemStack[] generateContents(Material refItem, List<AuctionSale> sales) {
		ItemStack[] contents = new ItemStack[SIZE];

		int i = 0;
		for (AuctionSale sale : sales) {
			if (i == REFITEM_SLOT) {
				continue;
			}

			contents[i++] = sale.getTradeStack();
		}

		contents[REFITEM_SLOT] = new ItemStack(refItem);
		return contents;
	}

	/**
	 * Checks if is sale slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if is sale slot
	 */
	public static boolean isSaleSlot(int slot) {
		return slot == REFITEM_SLOT;
	}
}
