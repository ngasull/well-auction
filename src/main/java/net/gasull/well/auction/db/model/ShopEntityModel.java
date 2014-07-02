package net.gasull.well.auction.db.model;

import java.util.ArrayList;
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
	private List<AucEntityToShop> entityToShops = new ArrayList<>();

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
	 * @return true, if successful
	 */
	public boolean addShop(AuctionShop shop) {
		for (AucEntityToShop entityToShop : getEntityToShops()) {
			if (entityToShop.getShop() == shop) {
				return false;
			}
		}

		AucEntityToShop entityToShop = new AucEntityToShop();
		entityToShop.setEntity(this);
		entityToShop.setShop(shop);
		getEntityToShops().add(entityToShop);
		return true;
	}

	/**
	 * Removes the shop.
	 * 
	 * @param shop
	 *            the shop
	 * @return the auc entity to shop
	 */
	public AucEntityToShop removeShop(AuctionShop shop) {
		List<AucEntityToShop> entityToShops = new ArrayList<>(getEntityToShops());
		for (AucEntityToShop entityToShop : entityToShops) {
			if (entityToShop.getShop() == shop) {
				getEntityToShops().remove(entityToShop);
				return entityToShop;
			}
		}

		return null;
	}
}
