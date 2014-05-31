package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.inventory.ItemStack;

/**
 * Data for a seller, for a single shop.
 */
@Entity
@Table(name = "well_auction_sellerData")
public class AuctionSellerData {

	/** The sale id. */
	@Id
	private Integer id;

	/** The shop id. */
	private Integer shopId;

	/** The auction player id. */
	private UUID auctionPlayerId;

	/** The shop. */
	@Transient
	private AuctionShop shop;

	/** The auction player. */
	@Transient
	private AuctionPlayer auctionPlayer;

	/** The sales. */
	@Transient
	private List<AuctionSale> sales = new ArrayList<>();

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
		this.shopId = shop.getId();
		this.auctionPlayer = auctionPlayer;
		this.auctionPlayerId = auctionPlayer.getPlayerId();
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
	 * Gets the shop id.
	 * 
	 * @return the shop id
	 */
	public Integer getShopId() {
		return shopId;
	}

	/**
	 * Sets the shop id.
	 * 
	 * @param shopId
	 *            the new shop id
	 */
	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	/**
	 * Gets the auction player id.
	 * 
	 * @return the auction player id
	 */
	public UUID getAuctionPlayerId() {
		return auctionPlayerId;
	}

	/**
	 * Sets the auction player id.
	 * 
	 * @param auctionPlayerId
	 *            the new auction player id
	 */
	public void setAuctionPlayerId(UUID auctionPlayerId) {
		this.auctionPlayerId = auctionPlayerId;
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
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	public List<AuctionSale> getSales() {
		return sales;
	}

	/**
	 * Sets the sales.
	 * 
	 * @param sales
	 *            the new sales
	 */
	public void setSales(List<AuctionSale> sales) {
		this.sales = sales;
	}

	/**
	 * Gets the sale.
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
}
