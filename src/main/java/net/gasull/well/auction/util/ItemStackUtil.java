package net.gasull.well.auction.util;

import org.bukkit.inventory.ItemStack;

/**
 * {@link ItemStack} associated utilities.
 */
public class ItemStackUtil {

	/**
	 * Instantiates a new item stack util.
	 */
	private ItemStackUtil() {
	}

	/**
	 * As string.
	 * 
	 * @param item
	 *            the item
	 * @return the string
	 */
	public static String asString(ItemStack item) {
		return item == null ? "?!" : String.format("%dx %s", item.getAmount(), item.getType().name().toLowerCase().replace("_", " "));
	}
}
