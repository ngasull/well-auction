package net.gasull.well.auction.shop;

import org.bukkit.Location;

/**
 * Modelizes what can be a clickable shop.
 */
public class ShopEntity {

	/** The actual Bukkit entity. */
	private Location location;

	/**
	 * Instantiates a new shop entity.
	 * 
	 * @param location
	 *            the location
	 */
	public ShopEntity(Location location) {
		this.location = location;
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
}
