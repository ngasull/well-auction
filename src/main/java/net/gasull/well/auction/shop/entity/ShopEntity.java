package net.gasull.well.auction.shop.entity;

import net.gasull.well.auction.WellAuction;
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
	 * @param auctionShop
	 *            the auction shop
	 */
	public void register(WellAuction plugin, AuctionShop auctionShop) {
		getModel().setShop(auctionShop);
		auctionShop.getRegistered().add(this);
	}

	/**
	 * Unregister.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public void unregister(WellAuction plugin) {
		getModel().getShop().getRegistered().remove(this);
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public abstract ShopEntityModel getModel();
}
