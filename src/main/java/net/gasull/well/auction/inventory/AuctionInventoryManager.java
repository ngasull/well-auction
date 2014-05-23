package net.gasull.well.auction.inventory;

import net.gasull.well.auction.WellAuction;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * The well-auction's Invntory manager.
 */
public class AuctionInventoryManager {

	/** The plugin. */
	private WellAuction plugin;

	/** The auction menu. */
	private AuctionMenu auctionMenu;

	/** The auction inventory's title base (first part). */
	private final String TITLE_BASE;

	/** The sell sub view title. */
	private final String TITLE_SELL;

	/** The buy sub view title. */
	private final String TITLE_BUY;

	/** The separator between title base and sub view title. */
	private static final String TITLE_SEPARATOR = " - ";

	/**
	 * Instantiates a new auction inventory manager.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public AuctionInventoryManager(WellAuction plugin) {
		this.plugin = plugin;
		this.auctionMenu = new AuctionMenu(plugin);
		this.TITLE_BASE = plugin.wellConfig().getString("inventory.menu.title", "Auction House");
		this.TITLE_SELL = TITLE_BASE + TITLE_SEPARATOR + plugin.wellConfig().getString("inventory.sell.title", "Sell");
		this.TITLE_BUY = TITLE_BASE + TITLE_SEPARATOR + plugin.wellConfig().getString("inventory.buy.title", "Buy");
	}

	/**
	 * Open an Auction House for a {@link Player}.
	 * 
	 * @param player
	 *            the player
	 */
	public void open(Player player, Material material) {
		Inventory inv = Bukkit.createInventory(player, AuctionMenu.MENU_SIZE, TITLE_BASE);
		inv.setContents(auctionMenu.getMaterialMenu(material));
		player.openInventory(inv);
	}

	/**
	 * Handle menu click.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if successful
	 */
	public void handleMenuClick(int slot, Player player) {
		switch (slot) {
		case AuctionMenu.BUY_SLOT:
		case AuctionMenu.SALE_SLOT:
			player.closeInventory();

			Inventory inv = Bukkit.createInventory(player, 9);
			player.openInventory(inv);
			break;
		default:
			// Do nothing
		}
	}

	/**
	 * Checks if player opening sell menu and handles the action if needed.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	public boolean handleOpenSell(InventoryClickEvent evt) {
		return false;
	}

	/**
	 * Checks if player opening buy menu and handles the action if needed.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	public boolean handleOpenBuy(InventoryClickEvent event) {
		return false;
	}

	/**
	 * Checks if player closing current menu and handles the action if needed.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	public boolean handleCloseMenu(InventoryClickEvent event) {
		return false;
	}

	/**
	 * Checks if is auction inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory
	 */
	public boolean isAuctionInventory(Inventory inventory) {
		return inventory.getTitle().startsWith(TITLE_BASE);
	}

	/**
	 * Checks if is auction menu inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory menu
	 */
	public boolean isMenuInventory(Inventory inventory) {
		return inventory.getTitle().equals(TITLE_BASE);
	}

	/**
	 * Checks if is auction sell inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory sell
	 */
	public boolean isSellInventory(Inventory inventory) {
		return inventory.getTitle().equals(TITLE_SELL);
	}

	/**
	 * Checks if is auction buy inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory buy
	 */
	public boolean isBuyInventory(Inventory inventory) {
		return inventory.getTitle().equals(TITLE_BUY);
	}

	/**
	 * Checks if is top inventory event.
	 * 
	 * @param evt
	 *            the evt
	 * @return true, if is top inventory event
	 */
	public boolean isTopInventoryEvent(InventoryClickEvent evt) {
		return evt.getRawSlot() < evt.getView().getTopInventory().getSize();
	}

	/**
	 * Gets the auction menu template.
	 * 
	 * @return the menu
	 */
	public AuctionMenu getMenu() {
		return auctionMenu;
	}
}
