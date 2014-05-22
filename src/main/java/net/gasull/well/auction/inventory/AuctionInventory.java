package net.gasull.well.auction.inventory;

import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

/**
 * The Class AuctionInventory.
 */
public class AuctionInventory extends CraftInventoryCustom {

	public static final String TITLE = "WellAuction";

	/**
	 * Instantiates a new auction inventory.
	 * 
	 * @param player
	 *            the player
	 */
	public AuctionInventory(Player player) {
		super(player, InventoryType.CHEST, "WellAuction");
	}
}
