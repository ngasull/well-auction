/*
 * 
 */
package net.gasull.well.auction.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.AuctionSale;
import net.gasull.well.auction.shop.AuctionShop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
	private Map<AuctionShop, Map<Player, InventoryView>> sellInventories = new HashMap<AuctionShop, Map<Player, InventoryView>>();

	/** The buy inventories. */
	private Map<AuctionShop, Map<Player, InventoryView>> buyInventories = new HashMap<AuctionShop, Map<Player, InventoryView>>();

	/** The shop for sell inventory. */
	private Map<InventoryView, AuctionShop> shopForSellInventory = new HashMap<InventoryView, AuctionShop>();

	/** The shop for buy inventory. */
	private Map<InventoryView, AuctionShop> shopForBuyInventory = new HashMap<InventoryView, AuctionShop>();

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
		this.TITLE_SELL = TITLE_BASE + TITLE_SEPARATOR + plugin.wellConfig().getString("lang.inventory.sell.title", "Sell");
		this.TITLE_BUY = TITLE_BASE + TITLE_SEPARATOR + plugin.wellConfig().getString("lang.inventory.buy.title", "Buy");
	}

	/**
	 * Open an Auction House for a {@link Player}.
	 * 
	 * @param player
	 *            the player
	 * @param shop
	 *            the auction shop
	 */
	public void openMenu(Player player, AuctionShop shop) {
		Inventory inv = Bukkit.createInventory(player, AuctionMenu.MENU_SIZE, TITLE_BASE);
		inv.setContents(auctionMenu.getMenuForShop(shop));
		player.openInventory(inv);
	}

	/**
	 * Open sell menu.
	 * 
	 * @param player
	 *            the player
	 * @param shop
	 *            the auction shop
	 */
	public void openSell(Player player, AuctionShop shop) {
		Inventory sellInv = Bukkit.createInventory(player, AuctionSellInventory.SIZE, TITLE_SELL);
		loadSellInventory(sellInv, shop.getSales(player));
		openSubMenu(player, sellInv, shop, sellInventories, shopForSellInventory);
	}

	/**
	 * Open buy menu.
	 * 
	 * @param player
	 *            the player
	 * @param shop
	 *            the shop
	 */
	public void openBuy(Player player, AuctionShop shop) {
		Inventory buyInv = Bukkit.createInventory(player, AuctionBuyInventory.SIZE, TITLE_BUY);
		loadBuyInventory(buyInv, shop.getSales());
		openSubMenu(player, buyInv, shop, buyInventories, shopForBuyInventory);
	}

	/**
	 * Checks a sale.
	 * 
	 * @param view
	 *            the inventory view
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return true, if successful
	 */
	public boolean checkSell(InventoryView view, Player player, ItemStack theItem) {
		AuctionShop shop = shopForSellInventory.get(view);
		return shop != null && shop.sells(theItem);
	}

	/**
	 * Checks buy.
	 * 
	 * @param view
	 *            the inventory view
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return true, if successful
	 */
	public boolean checkBuy(InventoryView view, Player player, ItemStack theItem) {
		AuctionShop shop = shopForBuyInventory.get(view);
		return shop != null && shop.sells(theItem);
	}

	/**
	 * Handle a sale.
	 * 
	 * @param inv
	 *            the inventory
	 * @param shop
	 *            the shop
	 * @param player
	 *            the player
	 */
	public void handleSell(Inventory inv, AuctionShop shop, Player player) {
		loadSellInventory(inv, shop.getSales(player));
		refreshBuyInventories(shop);
	}

	/**
	 * Handle buy.
	 * 
	 * @param sale
	 *            the sale
	 * @return the item stack
	 */
	public ItemStack handleBuy(AuctionSale sale) {
		AuctionShop shop = sale.getShop();
		refreshBuyInventories(shop);
		refreshSellInventories(shop);

		return sale.getItem();
	}

	/**
	 * Properly closes the inventory.
	 * 
	 * @param inventoryView
	 *            the inventory view
	 * @param player
	 *            the player
	 */
	public void handleClose(InventoryView inventoryView, Player player) {

		AuctionShop shop = shopForBuyInventory.remove(inventoryView);
		if (shop != null) {
			buyInventories.get(shop).remove(player);
		}

		shop = shopForSellInventory.remove(inventoryView);
		if (shop != null) {
			sellInventories.get(shop).remove(player);
		}
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
	 * @param inventory
	 *            the inventory
	 * @param slot
	 *            the slot
	 * @return true, if is top inventory event
	 */
	public boolean isTopInventoryEvent(Inventory inventory, int slot) {
		return slot < inventory.getSize();
	}

	/**
	 * Open sub menu.
	 * 
	 * @param player
	 *            the player
	 * @param inv
	 *            the inventory
	 * @param shop
	 *            the auction shop
	 * @param viewMap
	 *            the inventory views map
	 * @param shopForView
	 *            the shop for inventory view
	 */
	private void openSubMenu(Player player, Inventory inv, AuctionShop shop, Map<AuctionShop, Map<Player, InventoryView>> viewMap,
			Map<InventoryView, AuctionShop> shopForView) {

		Map<Player, InventoryView> playerForView = viewMap.get(shop);

		if (playerForView == null) {
			synchronized (shop) {
				playerForView = viewMap.get(shop);

				if (playerForView == null) {
					playerForView = new HashMap<Player, InventoryView>();
					viewMap.put(shop, playerForView);
				}
			}
		}

		player.closeInventory();
		InventoryView view = player.openInventory(inv);

		// Always erase an existing open inventory view for the same player
		playerForView.put(player, view);
		shopForView.put(view, shop);
	}

	/**
	 * Refresh buy inventories.
	 * 
	 * @param shop
	 *            the shop
	 */
	private void refreshBuyInventories(AuctionShop shop) {
		Map<Player, InventoryView> viewMap = buyInventories.get(shop);
		Inventory inv;

		if (viewMap != null) {
			for (Entry<Player, InventoryView> pair : viewMap.entrySet()) {
				inv = pair.getValue().getTopInventory();
				loadBuyInventory(inv, shop.getSales());
			}
		}
	}

	/**
	 * Refresh sell inventories.
	 * 
	 * @param shop
	 *            the shop
	 */
	private void refreshSellInventories(AuctionShop shop) {
		Map<Player, InventoryView> viewMap = sellInventories.get(shop);
		Inventory inv;

		if (viewMap != null) {
			for (Entry<Player, InventoryView> pair : viewMap.entrySet()) {
				inv = pair.getValue().getTopInventory();
				loadSellInventory(inv, shop.getSales(pair.getKey()));
			}
		}
	}

	/**
	 * Sets contents of an inventory for buy.
	 * 
	 * @param buyInv
	 *            the buy inventory
	 * @param sales
	 *            the sales
	 */
	private void loadBuyInventory(Inventory buyInv, List<AuctionSale> sales) {
		buyInv.setContents(AuctionBuyInventory.generateContents(sales));
	}

	/**
	 * Sets contents of an inventory for buy.
	 * 
	 * @param sellInv
	 *            the sell inventory
	 * @param sales
	 *            the sales
	 */
	private void loadSellInventory(Inventory sellInv, List<AuctionSale> sales) {
		sellInv.setContents(AuctionSellInventory.generateContents(sales));
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
