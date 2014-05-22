package net.gasull.well.auction;

import net.gasull.well.auction.event.AuctionPlayerInteractListener;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("Enabling well-auction");

		AuctionShopManager shopManager = new AuctionShopManager();
		AuctionPlayerInteractListener testListener = new AuctionPlayerInteractListener(this, shopManager);
		getServer().getPluginManager().registerEvents(testListener, this);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling well-auction");
	}
}
