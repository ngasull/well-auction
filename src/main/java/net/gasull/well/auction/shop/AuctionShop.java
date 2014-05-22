package net.gasull.well.auction.shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The actual Auction Shop, shared all over the world for a given
 * {@link Material}.
 */
public class AuctionShop {

	/** The material being sold. */
	private Material material;

	/** The registered shop-entities. */
	private List<ShopEntity> registered;

	/** The sales. */
	private List<AuctionSale> sales;

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
	 * Register entity for it to "contain" the shop.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 */
	public void registerEntity(ShopEntity shopEntity) {
		registered.add(shopEntity);
	}

	public void sell(Player player, ItemStack item) throws AuctionShopException {

	}

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
}
