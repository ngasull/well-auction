package net.gasull.well.auction;

import net.gasull.well.auction.command.WaucAttachCommand;
import net.gasull.well.auction.command.WaucCommandHelper;
import net.gasull.well.auction.command.WaucDetachCommand;
import net.gasull.well.auction.command.WaucListCommand;
import net.gasull.well.auction.command.WaucPresetCommand;
import net.gasull.well.auction.command.WaucRemoveCommand;
import net.gasull.well.auction.db.WellAuctionDao;
import net.gasull.well.auction.event.AuctionBlockShopListener;
import net.gasull.well.auction.event.AuctionEntityShopListener;
import net.gasull.well.auction.event.AuctionShopInventoryListener;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.shop.AuctionShopManager;
import net.gasull.well.auction.shop.entity.AucShopEntityManager;
import net.gasull.well.command.WellCommandHandler;
import net.gasull.well.conf.WellConfig;
import net.gasull.well.conf.WellLanguageConfig;
import net.gasull.well.conf.WellPermissionManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WellAuction, this is it!
 */
public class WellAuction extends JavaPlugin {

	/** The well config. */
	private WellConfig config;

	/** The lang. */
	private WellLanguageConfig lang;

	/** The permission manager. */
	private WellPermissionManager permission;

	/** The database access object for Well Auction . */
	private WellAuctionDao db;

	/** The shop manager. */
	private AuctionShopManager shopManager;

	/** The shop entity manager. */
	private AucShopEntityManager shopEntityManager;

	/** The inventory manager. */
	private AuctionInventoryManager inventoryManager;

	/** The economy. */
	private Economy economy;

	@Override
	public void onEnable() {
		setupConf();
		setupVault();

		config = new WellConfig(this, "well-auction.yml", true);
		lang = new WellLanguageConfig(this, config.getString("language"));
		permission = new WellPermissionManager(this, lang);

		if (shopManager == null) {
			this.db = new WellAuctionDao(this);

			shopEntityManager = new AucShopEntityManager(this);
			shopManager = new AuctionShopManager(this, shopEntityManager);
			inventoryManager = new AuctionInventoryManager(this, shopManager);
			shopManager.load();
			shopManager.enable();
		}

		// Listeners
		getServer().getPluginManager().registerEvents(new AuctionShopInventoryListener(this, shopManager, inventoryManager, shopEntityManager), this);
		getServer().getPluginManager().registerEvents(new AuctionBlockShopListener(this, shopEntityManager), this);
		getServer().getPluginManager().registerEvents(new AuctionEntityShopListener(this, shopEntityManager), this);

		config.save();
		setupCommands();
	}

	@Override
	public void onDisable() {
		if (shopManager != null) {
			shopManager.disable();
			shopEntityManager.clean();
		}
	}

	/**
	 * Gets the Well suite's config.
	 * 
	 * @return the well config
	 */
	public WellConfig config() {
		return config;
	}

	/**
	 * Gets the localization.
	 * 
	 * @return the well language config
	 */
	public WellLanguageConfig lang() {
		return lang;
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
	 * Returns the plugin's DAO.
	 * 
	 * @return the well auction dao
	 */
	public WellAuctionDao db() {
		return db;
	}

	/**
	 * Economy.
	 * 
	 * @return Vault's {@link Economy}
	 */
	public Economy economy() {
		return economy;
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

	/**
	 * Setup conf.
	 */
	private void setupConf() {
		saveResource("presets.yml", false);
	}

	/**
	 * Setup commands.
	 */
	private void setupCommands() {
		WaucCommandHelper helper = new WaucCommandHelper(this, shopEntityManager);
		WellCommandHandler.bind(this, "wellauction").attach(new WaucAttachCommand(this, helper)).attach(new WaucDetachCommand(this, helper))
				.attach(new WaucRemoveCommand(this, helper)).attach(new WaucListCommand(this)).attach(new WaucPresetCommand(this, helper));
	}
}
