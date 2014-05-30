package net.gasull.well.auction.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

/**
 * The Class AuctionPlayer.
 */
public class AuctionPlayer {

	/** The player. */
	private OfflinePlayer player;

	/** The seller data map. */
	private Map<AuctionShop, AuctionSellerData> sellerDataMap = new HashMap<>();

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
	 * @param shop
	 *            the shop
	 * @return the sales
	 */
	public List<AuctionSale> getSales(AuctionShop shop) {
		return getSellerData(shop).getSales();
	}

	/**
	 * Gets the seller data.
	 * 
	 * @param shop
	 *            the auction shop
	 * @return the seller data
	 */
	public AuctionSellerData getSellerData(AuctionShop shop) {
		AuctionSellerData data = sellerDataMap.get(shop);

		if (data == null) {
			data = new AuctionSellerData(shop, this);
			sellerDataMap.put(shop, data);
		}

		return data;
	}
}
