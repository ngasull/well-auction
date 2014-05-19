package net.gasull.well.auction.shop;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * The AuctionShop manager.
 */
public class AuctionShopManager {

	/** The registered shops by location. */
	private Map<Location, AuctionShop> shopsByLocation;

	/** The registered shops by material. */
	private Map<Material, AuctionShop> shops;

	/**
	 * Gets the shop for a given {@link Material}.
	 * 
	 * @param material
	 *            the material
	 * @return the shop if exists for the associated material, null otherwise.
	 */
	public AuctionShop getShop(Material material) {
		return shops.get(material);
	}

	/**
	 * Gets the shop for a given {@link Location}.
	 * 
	 * @param location
	 *            the location
	 * @return the shop if exists for the associated location, null otherwise.
	 */
	public AuctionShop getShop(Location location) {
		return shopsByLocation.get(location);
	}

	/**
	 * Registers a {@link ShopEntity} as a shop. Instantiates a new
	 * {@link AuctionShop} if none exist for shopEntity.
	 * 
	 * @param material
	 *            the material
	 * @param shopEntity
	 *            the shop entity
	 * @return the auction shop
	 */
	public AuctionShop registerEntityAsShop(Material material, Location location) {
		AuctionShop shop = shopsByLocation.get(location);

		if (shop == null) {
			shop = new AuctionShop(material);
			shops.put(material, shop);
			shopsByLocation.put(location, shop);
		}

		shop.registerEntity(new ShopEntity(location));
		return shop;
	}
}
