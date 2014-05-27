package net.gasull.well.auction;

import net.gasull.well.auction.event.AuctionShopInventoryListener;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.shop.AuctionShopManager;
import net.gasull.well.auction.shop.ShopEntity;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {

	/** The well config. */
	private WellConfig wellConfig;

	/** The permission. */
	private WellPermissionManager permission;

	/** The command handler. */
	private WellAuctionCommandHandler commandHandler;

	/** The economy. */
	public Economy economy;

	@Override
	public void onEnable() {
		setupVault();

		wellConfig = new WellConfig(this, "well-auction.yml");
		permission = new WellPermissionManager(this, wellConfig);
		commandHandler = new WellAuctionCommandHandler(this);

		AuctionShopManager shopManager = new AuctionShopManager(this);
		AuctionInventoryManager inventoryManager = new AuctionInventoryManager(this);

		// Listeners
		getServer().getPluginManager().registerEvents(new AuctionShopInventoryListener(this, shopManager, inventoryManager), this);

		// Init for testing
		shopManager.registerEntityAsShop(shopManager.getShop(new ItemStack(Material.STICK)), new ShopEntity(null));

		wellConfig.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return commandHandler.handle(sender, cmd, label, args);
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
	 * Permission.
	 * 
	 * @return the well permission manager
	 */
	public WellPermissionManager permission() {
		return permission;
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
