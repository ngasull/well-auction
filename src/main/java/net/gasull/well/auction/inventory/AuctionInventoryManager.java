/*
 * 
 */
package net.gasull.well.auction.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.AuctionSale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The well-auction's Invntory manager.
 */
public class AuctionInventoryManager {

	/** The plugin. */
	private WellAuction plugin;

	/** The auction menu. */
	private AuctionMenu auctionMenu;

	/** The sell inventories. */
	private Map<Material, Map<Player, Inventory>> sellInventories = new HashMap<Material, Map<Player, Inventory>>();

	/** The buy inventories. */
	private Map<Material, Map<Player, Inventory>> buyInventories = new HashMap<Material, Map<Player, Inventory>>();

	/** The auction inventory's title base (first part). */
	private final String TITLE_BASE;

	/** The sell sub view title. */
	private final String TITLE_SELL;

	/** The buy sub view title. */
	private final String TITLE_BUY;

	/** The separator between title base and sub view title. */
	private static final String TITLE_SEPARATOR = " - ";

	// FULL TMP
	private ArrayList<AuctionSale> sales = new ArrayList<AuctionSale>();

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
	 * @param inv
	 *            the calling inventory
	 * @param slot
	 *            the slot
	 * @param player
	 *            the player
	 * @return true, if successful
	 */
	public void handleMenuClick(Inventory inv, int slot, Player player) {
		ItemStack refItem = inv.getItem(AuctionMenu.REFITEM_SLOT);

		switch (slot) {
		case AuctionMenu.BUY_SLOT:
			break;
		case AuctionMenu.SALE_SLOT:
			Inventory sellInv = Bukkit.createInventory(player, AuctionSellInventory.SIZE, TITLE_SELL);
			sellInv.setContents(AuctionSellInventory.generateContents(refItem.getType(), sales));

			player.closeInventory();
			player.openInventory(sellInv);
			break;
		default:
			// Do nothing
		}
	}

	/**
	 * Handle sell.
	 * 
	 * @param inv
	 *            the inv
	 * @param slot
	 *            the slot
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return true, if successful
	 */
	public boolean handleSell(Inventory inv, int slot, Player player, ItemStack theItem) {

		if (slot != AuctionSellInventory.REFITEM_SLOT) {
			ItemStack refItem = inv.getItem(AuctionSellInventory.REFITEM_SLOT);

			if (refItem.isSimilar(theItem)) {
				sales.add(new AuctionSale(player, theItem));
				inv.setContents(AuctionSellInventory.generateContents(refItem.getType(), sales));
				return true;
			}
		}

		return false;
	}

	public void handleBuy(Inventory inv, int slot, Player player) {

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
