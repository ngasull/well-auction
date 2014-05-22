package net.gasull.well.auction.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Class AuctionSale.
 */
public class AuctionSale {

	/** The player. */
	private Player player;

	/** The item. */
	private Material item;

	/** The quantity. */
	private int quantity;

	/**
	 * Instantiates a new auction sale.
	 * 
	 * @param player
	 *            the player
	 * @param stack
	 *            the stack
	 */
	public AuctionSale(Player player, ItemStack stack) {
		this.player = player;
		this.item = stack.getType();
		this.quantity = stack.getAmount();
	}

	/**
	 * Gets the player.
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	public Material getItem() {
		return item;
	}

	/**
	 * Gets the quantity.
	 * 
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
}
