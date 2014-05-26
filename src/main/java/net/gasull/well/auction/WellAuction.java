package net.gasull.well.auction;

import net.gasull.well.auction.event.AuctionPlayerInteractListener;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.shop.AuctionShopManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {

	/** The well config. */
	private WellConfig wellConfig;

	/** The permission. */
	public WellPermissionManager permission;

	/** The economy. */
	public Economy economy;

	@Override
	public void onEnable() {
		setupVault();

		wellConfig = new WellConfig(this, "well-auction.yml");
		permission = new WellPermissionManager(this, wellConfig);

		AuctionShopManager shopManager = new AuctionShopManager(this);
		AuctionInventoryManager inventoryManager = new AuctionInventoryManager(this);
		AuctionPlayerInteractListener testListener = new AuctionPlayerInteractListener(this, shopManager, inventoryManager);
		getServer().getPluginManager().registerEvents(testListener, this);

		wellConfig.save();
	}

	/**
	 * Gets the Well suite's config.
	 * 
	 * @return the well config
	 */
	public WellConfig wellConfig() {
		return wellConfig;
	}

	/**
	 * Setup vault.
	 */
	private void setupVault() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

		if (economyProvider == null) {
			throw new RuntimeException("Couldn't initialize Vault's Economy. Is Vault in your plugins?");
		}

		economy = economyProvider.getProvider();
	}
}
