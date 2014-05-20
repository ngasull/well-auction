package net.gasull.well.auction.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

/**
 * The Class AuctionInventory.
 */
public class AuctionInventory extends CustomInventory {

	/**
	 * Instantiates a new auction inventory.
	 * 
	 * @param player
	 *            the player
	 */
	public AuctionInventory(Player player) {
		super(player, InventoryType.CHEST, "DO THIS");
	}
}
