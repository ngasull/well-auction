package net.gasull.well.auction.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Class AuctionSale.
 */
public class AuctionSale {

	/** The player. */
	private Player player;

	/** The item type. */
	private AuctionType type;

	/** The item (stack). */
	private ItemStack item;

	/** The item displayed in shop. */
	private ItemStack tradeStack;

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
		this.type = AuctionType.get(stack);
		this.item = stack;
		this.tradeStack = new ItemStack(stack);
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
	 * Gets the item type.
	 * 
	 * @return the item type
	 */
	public AuctionType getType() {
		return type;
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
	 * Gets the trade stack.
	 * 
	 * @return the trade stack
	 */
	public ItemStack getTradeStack() {
		return tradeStack;
	}
}
