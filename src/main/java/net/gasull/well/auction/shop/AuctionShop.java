package net.gasull.well.auction.shop;

import java.util.List;

import org.bukkit.Material;

/**
 * The actual Auction Shop, shared all over the world for a given
 * {@link Material}.
 */
public class AuctionShop {

	/** The material. */
	private Material material;

	private List<ShopEntity> registered;

	/**
	 * Instantiates a new auction shop.
	 * 
	 * @param material
	 *            the material
	 */
	public AuctionShop(Material material) {
		this.material = material;

		// TODO Create its Inventory here
	}

	/**
	 * Register entity.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 */
	public void registerEntity(ShopEntity shopEntity) {
		registered.add(shopEntity);
	}

	public Material getMaterial() {
		return material;
	}
}
