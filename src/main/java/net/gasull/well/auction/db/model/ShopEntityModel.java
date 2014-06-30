package net.gasull.well.auction.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.gasull.well.auction.shop.entity.ShopEntity;

import com.avaje.ebean.validation.NotNull;

/**
 * {@link ShopEntity}'s model.
 */
@Entity
@Table(name = "well_auction_shopentity")
public class ShopEntityModel {

	/** The id. */
	@Id
	private int id;

	/** The shop. */
	@ManyToOne
	private AuctionShop shop;

	/** The type. */
	@NotNull
	private String type;

	/** The data. */
	@Column(length = 3000)
	private String data;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the new data
	 */
	public void setData(String data) {
		this.data = data;
	}
}
