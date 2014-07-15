package net.gasull.well.auction.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.gasull.well.db.WellRawSql;

/**
 * Data for a seller, for a single shop.
 */
@Entity
@Table(name = "well_auction_sellerData")
public class AuctionSellerData {

	/** The sale id. */
	@Id
	private Integer id;

	/** The shop. */
	@ManyToOne
	private AuctionShop shop;

	/** The auction player. */
	@ManyToOne
	private AuctionPlayer auctionPlayer;

	/** The default price. */
	private Double defaultPrice;

	/**
	 * Instantiates a new auction seller data.
	 */
	public AuctionSellerData() {
	}

	/**
	 * Instantiates a new auction seller data.
	 * 
	 * @param shop
	 *            the shop
	 * @param auctionPlayer
	 *            the auction player
	 */
	public AuctionSellerData(AuctionShop shop, AuctionPlayer auctionPlayer) {
		this.shop = shop;
		this.auctionPlayer = auctionPlayer;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the shop.
	 * 
	 * @return the shop
	 */
	public AuctionShop getShop() {
		return shop;
	}

	/**
	 * Sets the shop.
	 * 
	 * @param shop
	 *            the new shop
	 */
	public void setShop(AuctionShop shop) {
		this.shop = shop;
	}

	/**
	 * Gets the auction player.
	 * 
	 * @return the auction player
	 */
	public AuctionPlayer getAuctionPlayer() {
		return auctionPlayer;
	}

	/**
	 * Sets the auction player.
	 * 
	 * @param auctionPlayer
	 *            the new auction player
	 */
	public void setAuctionPlayer(AuctionPlayer auctionPlayer) {
		this.auctionPlayer = auctionPlayer;
	}

	/**
	 * Gets the default price.
	 * 
	 * @return the default price
	 */
	public Double getDefaultPrice() {
		return defaultPrice;
	}

	/**
	 * Sets the default price.
	 * 
	 * @param defaultPrice
	 *            the new default price
	 */
	public void setDefaultPrice(Double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	/**
	 * Map raw sql.
	 * 
	 * @param a
	 *            the table alias
	 * @return the well raw sql
	 */
	public static WellRawSql mapRawSql(String a) {
		return new WellRawSql().mapColumn(a, "id", "id").mapColumn(a, "default_price", "defaultPrice");
	}
}
