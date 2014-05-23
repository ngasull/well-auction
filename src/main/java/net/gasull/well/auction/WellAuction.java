package net.gasull.well.auction;

import net.gasull.well.auction.event.AuctionPlayerInteractListener;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {

	/** The well config. */
	private WellConfig wellConfig;

	@Override
	public void onEnable() {
		getLogger().info("Enabling well-auction");

		wellConfig = new WellConfig(this);
		AuctionShopManager shopManager = new AuctionShopManager();
		AuctionInventoryManager inventoryManager = new AuctionInventoryManager(this);
		AuctionPlayerInteractListener testListener = new AuctionPlayerInteractListener(this, shopManager, inventoryManager);
		getServer().getPluginManager().registerEvents(testListener, this);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling well-auction");
	}

	/**
	 * Gets the Well suite's config.
	 * 
	 * @return the well config
	 */
	public WellConfig wellConfig() {
		return wellConfig;
	}
}
