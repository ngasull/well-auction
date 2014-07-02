package net.gasull.well.auction.db.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

	/** The shops relationship. */
	@OneToMany(mappedBy = "entity")
	private List<AucEntityToShop> entityToShops;

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
	 * Gets the entity to shops.
	 * 
	 * @return the entity to shops
	 */
	public List<AucEntityToShop> getEntityToShops() {
		return entityToShops;
	}

	/**
	 * Sets the entity to shops.
	 * 
	 * @param entityToShops
	 *            the new entity to shops
	 */
	public void setEntityToShops(List<AucEntityToShop> entityToShops) {
		this.entityToShops = entityToShops;
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

	/**
	 * Adds a related shop.
	 * 
	 * @param shop
	 *            the shop
	 */
	public void addShop(AuctionShop shop) {
		AucEntityToShop entityToShop = new AucEntityToShop();
		entityToShop.setEntity(this);
		entityToShop.setShop(shop);
		getEntityToShops().add(entityToShop);
	}
}
