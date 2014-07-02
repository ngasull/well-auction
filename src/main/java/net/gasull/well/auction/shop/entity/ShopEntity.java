package net.gasull.well.auction.shop.entity;

import java.util.ArrayList;
import java.util.Collection;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.inventory.AuctionMenu;

/**
 * Modelizes what can be a clickable shop.
 */
public abstract class ShopEntity {

	/** The menu. */
	private AuctionMenu menu;

	/**
	 * Instantiates a new shop entity.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	protected ShopEntity(WellAuction plugin) {
		this.menu = new AuctionMenu(plugin, this);
	}

	/**
	 * Register the shop for this entity.
	 */
	public void register() {
		for (AucEntityToShop entityToShop : getModel().getEntityToShops()) {
			entityToShop.getShop().getRegistered().add(this);
		}
	}

	/**
	 * Unregister.
	 */
	public void unregister() {
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
	 * Gets the menu.
	 * 
	 * @return the menu
	 */
	public AuctionMenu getMenu() {
		return menu;
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public abstract ShopEntityModel getModel();
}
