package net.gasull.well.auction.db.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	public AuctionPlayer(OfflinePlayer player) {
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
		return Bukkit.getOfflinePlayer(getPlayerId());
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
