package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * The Class AuctionPlayer.
 */
public class AuctionPlayer {

	/** The player. */
	private OfflinePlayer player;

	/** The player sales. */
	private List<AuctionSale> sales = new ArrayList<>();

	/** The default prices. */
	private Map<AuctionShop, Double> defaultPrices = new HashMap<>();

	/**
	 * Instantiates a new auction player.
	 * 
	 * @param player
	 *            the player
	 */
	AuctionPlayer(OfflinePlayer player) {
		this.player = player;
	}

	/**
	 * Fetches a sale from an associated item.
	 * 
	 * @param theItem
	 *            the the item
	 * @return the sale
	 */
	public AuctionSale getSale(ItemStack theItem) {

		for (AuctionSale sale : sales) {
			if (sale.isSellingStack(theItem)) {
				return sale;
			}
		}
		return null;
	}

	/**
	 * Gets the player.
	 * 
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	public List<AuctionSale> getSales() {
		return sales;
	}

	/**
	 * Gets the default prices.
	 * 
	 * @return the default prices
	 */
	public Map<AuctionShop, Double> getDefaultPrices() {
		return defaultPrices;
	}

	/**
	 * Sets the default price.
	 * 
	 * @param shop
	 *            the shop
	 * @param price
	 *            the new default price
	 */
	public void setDefaultPrice(AuctionShop shop, double price) {
		defaultPrices.put(shop, price);
	}
}
