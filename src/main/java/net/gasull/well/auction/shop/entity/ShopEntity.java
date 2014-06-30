package net.gasull.well.auction.shop.entity;

import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;

import org.bukkit.plugin.java.JavaPlugin;

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
	public abstract void register(JavaPlugin plugin, AuctionShop auctionShop);

	/**
	 * Unregister.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public abstract void unregister(JavaPlugin plugin);

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public abstract ShopEntityModel getModel();
}
