package net.gasull.well.auction.shop.entity;

import java.util.ArrayList;
import java.util.Collection;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;

/**
 * Modelizes what can be a clickable shop.
 */
public abstract class ShopEntity {

	/**
	 * Register the shop for this entity.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public void register(WellAuction plugin) {
		for (AucEntityToShop entityToShop : getModel().getEntityToShops()) {
			entityToShop.getShop().getRegistered().add(this);
		}
	}

	/**
	 * Unregister.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public void unregister(WellAuction plugin) {
		for (AucEntityToShop entityToShop : getModel().getEntityToShops()) {
			entityToShop.getShop().getRegistered().remove(this);
		}
	}

	/**
	 * Gets the shops.
	 * 
	 * @return the shops
	 */
	public Collection<AuctionShop> getShops() {
		Collection<AuctionShop> shops = new ArrayList<>();

		for (AucEntityToShop entityToShop : getModel().getEntityToShops()) {
			shops.add(entityToShop.getShop());
		}

		return shops;
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public abstract ShopEntityModel getModel();
}
