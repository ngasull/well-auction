package net.gasull.well.auction;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {
	
	@Override
	public void onEnable() {
		getLogger().info("Enabling well-auction");
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling well-auction");
	}
}
