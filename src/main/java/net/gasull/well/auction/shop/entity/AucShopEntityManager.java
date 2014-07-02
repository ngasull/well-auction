package net.gasull.well.auction.shop.entity;

import java.util.HashMap;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;

/**
 * WellAuction's {@link ShopEntity} Manager.
 */
public class AucShopEntityManager {

	/** The plugin. */
	private final WellAuction plugin;

	/**
	 * {@link ShopEntityModel} to {@link ShopEntity} mapping (indexed by model's
	 * ID).
	 */
	private final Map<Integer, ShopEntity> modelToEntity = new HashMap<>();

	/**
	 * Instantiates a new auc shop entity manager.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public AucShopEntityManager(WellAuction plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the.
	 * 
	 * @param model
	 *            the model
	 * @return the shop entity
	 */
	public ShopEntity get(ShopEntityModel model) {
		ShopEntity entity = modelToEntity.get(model.getId());

		if (entity == null) {
			// Evaluate shop to the enriched ones in memory
			for (AucEntityToShop entityToShop : model.getEntityToShops()) {
				AuctionShop shop = plugin.db().getShop(
						entityToShop.getShop().getId());
				entityToShop.setShop(shop);
			}

			switch (model.getType()) {
			case "block":
				entity = new BlockShopEntity(model);
				break;
			default:
				return null;
			}

			entity.register(plugin);
			modelToEntity.put(model.getId(), entity);
		}

		return entity;
	}

	/**
	 * Cleanup.
	 */
	public void clean() {
		for (ShopEntity entity : modelToEntity.values()) {
			entity.unregister(plugin);
		}
	}
}
