package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The Class AuctionPlayer.
 */
@Entity
@Table(name = "well_auction_player")
public class AuctionPlayer {

	/** The player id. */
	@Id
	private UUID playerId;

	/** The seller data. */
	@Transient
	private List<AuctionSellerData> sellerData = new ArrayList<>();

	/**
	 * Instantiates a new auction player.
	 */
	public AuctionPlayer() {
	}

	/**
	 * Instantiates a new auction player.
	 * 
	 * @param player
	 *            the player
	 */
	AuctionPlayer(OfflinePlayer player) {
		this.playerId = player.getUniqueId();
	}

	/**
	 * Gets the player id.
	 * 
	 * @return the player id
	 */
	public UUID getPlayerId() {
		return playerId;
	}

	/**
	 * Sets the player id.
	 * 
	 * @param playerId
	 *            the new player id
	 */
	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	/**
	 * Gets the player.
	 * 
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(this.playerId);
	}

	/**
	 * Gets the player.
	 * 
	 * @return the player
	 */
	public String getName() {
		OfflinePlayer player = getPlayer();

		if (player == null) {
			return null;
		}
		return player.getName();
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
	 * @return the seller data
	 */
	public List<AuctionSellerData> getSellerData() {
		return sellerData;
	}

	/**
	 * Gets the seller data.
	 * 
	 * @param shop
	 *            the auction shop
	 * @return the seller data
	 */
	public AuctionSellerData getSellerData(AuctionShop shop) {
		for (AuctionSellerData d : sellerData) {
			if (d.getShop().equals(shop)) {
				return d;
			}
		}

		AuctionSellerData data = new AuctionSellerData(shop, this);
		getSellerData().add(data);
		return data;
	}

	/**
	 * Sends a message to an AuctionPlayer.
	 * 
	 * @param message
	 *            the message
	 */
	public void sendMessage(String message) {
		OfflinePlayer offPlayer = getPlayer();
		Player player = offPlayer.getPlayer();
		if (player != null) {
			player.sendMessage(message);
		}
	}
}
