package net.gasull.well.auction.inventory;

import java.util.List;
import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionSale;

import org.bukkit.inventory.ItemStack;

/**
 * The well-auction sell inventory.
 */
public class AuctionSellInventory {

	/**
	 * Generate contents.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param playerSales
	 *            the player sales
	 * @return the item stack[]
	 */
	public static ItemStack[] generateContents(WellAuction plugin, List<AuctionSale> playerSales) {
		int size = getSize(plugin);
		ItemStack[] contents = new ItemStack[size];

		if (playerSales != null) {
			int i = 0;
			for (AuctionSale sale : playerSales) {
				contents[i++] = sale.getTradeStack();

				if (i >= size) {
					break;
				}
			}
		}

		return contents;
	}

	/**
	 * Gets the size of sell inventory.
	 * 
	 * @param plugin
	 *            the plugin
	 * @return the size
	 */
	public static int getSize(WellAuction plugin) {
		int size = plugin.config().getInt("inventory.sell.size.default");

		if (size < 0 || size > 6) {
			size = 3;
			plugin.getLogger().log(Level.WARNING, "Sell inventory size should not be more than 6 lines or less than zero! Setting to 3 lines by default.");
		}

		return size * 9;
	}
}
