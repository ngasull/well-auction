package net.gasull.well.auction.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Links a {@link ShopEntityModel} to an {@link AuctionShop}. Provides thus a
 * n-n relationship between them.
 */
@Entity
@Table(name = "well_auction_entity2shop")
public class AucEntityToShop {

	/** The id. */
	@Id
	private int id;

	/** The shop entity model. */
	@ManyToOne
	private ShopEntityModel entity;

	/** The shop. */
	@ManyToOne
	private AuctionShop shop;

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
	 * Gets the entity.
	 * 
	 * @return the entity
	 */
	public ShopEntityModel getEntity() {
		return entity;
	}

	/**
	 * Sets the entity.
	 * 
	 * @param entity
	 *            the new entity
	 */
	public void setEntity(ShopEntityModel entity) {
		this.entity = entity;
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
}
