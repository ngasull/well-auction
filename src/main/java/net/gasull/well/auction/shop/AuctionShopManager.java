package net.gasull.well.auction.shop;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * The AuctionShop manager.
 */
public class AuctionShopManager {

	/** The registered shops by location. */
	private Map<ShopEntity, AuctionShop> shopsByLocation;

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
	 * @param entity
	 *            the entity to be the shop
	 * @return the auction shop
	 */
	public AuctionShop registerEntityAsShop(Material material, Entity entity) {
		return registerEntityAsShop(material, new ShopEntity(entity.getLocation()));
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
	private AuctionShop registerEntityAsShop(Material material, ShopEntity shopEntity) {
		AuctionShop shop = shopsByLocation.get(shopEntity);

		if (shop == null) {
			shop = new AuctionShop(material);
			shops.put(material, shop);
			shopsByLocation.put(shopEntity, shop);
		}

		shop.registerEntity(shopEntity);
		return shop;
	}
}
