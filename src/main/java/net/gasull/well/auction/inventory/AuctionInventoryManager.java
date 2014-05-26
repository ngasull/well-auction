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
import org.bukkit.event.inventory.InventoryEvent;
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
	private Map<AuctionShop, Map<Player, Inventory>> sellInventories = new HashMap<AuctionShop, Map<Player, Inventory>>();

	/** The buy inventories. */
	private Map<AuctionShop, Map<Player, Inventory>> buyInventories = new HashMap<AuctionShop, Map<Player, Inventory>>();

	/** The shop for sell inventory. */
	private Map<Inventory, AuctionShop> shopForSellInventory = new HashMap<Inventory, AuctionShop>();

	/** The shop for buy inventory. */
	private Map<Inventory, AuctionShop> shopForBuyInventory = new HashMap<Inventory, AuctionShop>();

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
	 * @param inv
	 *            the inv
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return true, if successful
	 */
	public boolean checkSell(Inventory inv, Player player, ItemStack theItem) {
		AuctionShop shop = shopForSellInventory.get(inv);
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
	 * Checks buy.
	 * 
	 * @param inv
	 *            the inv
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return true, if successful
	 */
	public boolean checkBuy(Inventory inv, Player player, ItemStack theItem) {
		AuctionShop shop = shopForBuyInventory.get(inv);
		return shop != null && shop.sells(theItem);
	}

	/**
	 * Handle buy.
	 * 
	 * @param inv
	 *            the inv
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 * @return the item stack
	 */
	public ItemStack handleBuy(Inventory inv, Player player, AuctionSale sale) {
		AuctionShop shop = sale.getShop();
		Map<Player, Inventory> invMap = buyInventories.get(shop);

		if (invMap != null) {
			invMap.put(player, inv);
			refreshBuyInventories(shop);
			refreshSellInventories(shop);
		}

		return sale.getItem();
	}

	/**
	 * Properly closes the inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @param player
	 *            the player
	 */
	public void handleClose(Inventory inventory, Player player) {
		if (isBuyInventory(inventory)) {
			AuctionShop shop = shopForBuyInventory.remove(inventory);

			if (shop != null) {
				buyInventories.get(shop).remove(player);
			}
		} else if (isSellInventory(inventory)) {
			AuctionShop shop = shopForSellInventory.remove(inventory);

			if (shop != null) {
				sellInventories.get(shop).remove(player);
			}
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
	 * @param inventoryMap
	 *            the inventory map
	 * @param shopForInventory
	 *            the shop for inventory
	 */
	private void openSubMenu(Player player, Inventory inv, AuctionShop shop, Map<AuctionShop, Map<Player, Inventory>> inventoryMap,
			Map<Inventory, AuctionShop> shopForInventory) {

		Map<Player, Inventory> playerInventories = inventoryMap.get(shop);

		if (playerInventories == null) {
			synchronized (shop) {
				playerInventories = inventoryMap.get(shop);

				if (playerInventories == null) {
					playerInventories = new HashMap<Player, Inventory>();
					inventoryMap.put(shop, playerInventories);
				}
			}
		}

		// Always erase an existing open inventory for the same player
		playerInventories.put(player, inv);
		shopForInventory.put(inv, shop);

		player.closeInventory();
		player.openInventory(inv);
	}

	/**
	 * Re open sub menu.
	 * 
	 * @param player
	 *            the player
	 * @param inv
	 *            the inv
	 * @param shop
	 *            the shop
	 * @param inventoryMap
	 *            the inventory map
	 * @param shopForInventory
	 *            the shop for inventory
	 */
	private void reOpenSubMenu(Player player, Inventory inv, AuctionShop shop, Map<AuctionShop, Map<Player, Inventory>> inventoryMap,
			Map<Inventory, AuctionShop> shopForInventory) {
		Map<Player, Inventory> playerInventories = inventoryMap.get(shop);

		if (playerInventories != null) {
			playerInventories.remove(player);
		}

		shopForInventory.remove(inv);
		openSubMenu(player, inv, shop, inventoryMap, shopForInventory);
	}

	/**
	 * Refresh buy inventories.
	 * 
	 * @param shop
	 *            the shop
	 */
	private void refreshBuyInventories(AuctionShop shop) {
		Map<Player, Inventory> invMap = buyInventories.get(shop);

		if (invMap != null) {
			for (Entry<Player, Inventory> pair : invMap.entrySet()) {
				loadBuyInventory(pair.getValue(), shop.getSales());
// TODO VIEWERS ---> this not needed, simplify everything with 1 inv per shop --> Bukkit.getPluginManager().callEvent(new InventoryEvent(pair.getValue().getViewers());
//				reOpenSubMenu(pair.getKey(), pair.getValue(), shop, buyInventories, shopForBuyInventory);
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
	 * Refresh sell inventories.
	 * 
	 * @param shop
	 *            the shop
	 */
	private void refreshSellInventories(AuctionShop shop) {
		Map<Player, Inventory> invMap = sellInventories.get(shop);

		if (invMap != null) {
			for (Entry<Player, Inventory> pair : invMap.entrySet()) {
				loadSellInventory(pair.getValue(), shop.getSales(pair.getKey()));
			}
		}
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
