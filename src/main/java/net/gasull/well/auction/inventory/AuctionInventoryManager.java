/*
 * 
 */
package net.gasull.well.auction.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.AuctionSale;
import net.gasull.well.auction.shop.AuctionType;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
	private Map<AuctionType, Map<Player, Inventory>> sellInventories = new HashMap<AuctionType, Map<Player, Inventory>>();

	/** The buy inventories. */
	private Map<AuctionType, Map<Player, Inventory>> buyInventories = new HashMap<AuctionType, Map<Player, Inventory>>();

	/** The material for an open sell inventories. */
	private Map<Inventory, AuctionType> playerForSellInventory = new HashMap<Inventory, AuctionType>();

	/** The material for an open buy inventories. */
	private Map<Inventory, AuctionType> playerForBuyInventory = new HashMap<Inventory, AuctionType>();

	/** The auction inventory's title base (first part). */
	private final String TITLE_BASE;

	/** The sell sub view title. */
	private final String TITLE_SELL;

	/** The buy sub view title. */
	private final String TITLE_BUY;

	/** The separator between title base and sub view title. */
	private static final String TITLE_SEPARATOR = " - ";

	// FULL TMP
	private List<AuctionSale> sales = new ArrayList<AuctionSale>();

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
	 */
	public void openMenu(Player player, Material material) {
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
			Inventory buyInv = Bukkit.createInventory(player, AuctionBuyInventory.SIZE, TITLE_BUY);
			loadBuyInventory(buyInv, sales);
			openSubMenu(player, buyInv, refItem, buyInventories, playerForBuyInventory);
			break;
		case AuctionMenu.SALE_SLOT:
			Inventory sellInv = Bukkit.createInventory(player, AuctionSellInventory.SIZE, TITLE_SELL);
			// ! \\ HAS TO BE PLAYER'S SALES
			sellInv.setContents(AuctionSellInventory.generateContents(sales));
			openSubMenu(player, sellInv, refItem, sellInventories, playerForSellInventory);
			break;
		default:
			// Do nothing
		}
	}

	/**
	 * Checks a sale.
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
	public boolean checkSell(Inventory inv, int slot, Player player, ItemStack theItem) {
		AuctionType type = playerForSellInventory.get(inv);
		return type != null && type.equals(theItem);
	}

	/**
	 * Handle a sale.
	 * 
	 * @param inv
	 *            the inventory
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 */
	public void handleSell(Inventory inv, Player player, ItemStack theItem) {
		AuctionSale sale = new AuctionSale(player, theItem);
		sales.add(sale);
		inv.setContents(AuctionSellInventory.generateContents(sales));
		refreshBuyInventories(sale.getType(), sales);
	}

	/**
	 * Checks buy.
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
	public boolean checkBuy(Inventory inv, int slot, Player player, ItemStack theItem) {
		return sales.size() > 0;
	}

	public ItemStack handleBuy(Inventory inv, Player player, ItemStack theItem) {
		AuctionSale sale = sales.get(0);
		sales.remove(sale);
		inv.setContents(AuctionBuyInventory.generateContents(sales));
		// refreshSellInventories(theItem.getType(), sales);
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
			AuctionType type = playerForBuyInventory.remove(inventory);

			if (type != null) {
				buyInventories.get(type).remove(player);
			}
		} else if (isSellInventory(inventory)) {
			AuctionType type = playerForSellInventory.remove(inventory);

			if (type != null) {
				sellInventories.get(type).remove(player);
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
	 * @param refItem
	 *            the ref item
	 * @param inventoryMap
	 *            the inventory map
	 * @param playerForInventory
	 */
	private void openSubMenu(Player player, Inventory inv, ItemStack refItem, Map<AuctionType, Map<Player, Inventory>> inventoryMap,
			Map<Inventory, AuctionType> playerForInventory) {

		AuctionType type = AuctionType.get(refItem);
		Map<Player, Inventory> playerInventories = inventoryMap.get(type);

		if (playerInventories == null) {
			synchronized (type) {
				playerInventories = inventoryMap.get(type);

				if (playerInventories == null) {
					playerInventories = new HashMap<Player, Inventory>();
					inventoryMap.put(type, playerInventories);
				}
			}
		}

		// Always erase an existing open inventory for the same player
		playerInventories.put(player, inv);
		playerForInventory.put(inv, type);

		player.closeInventory();
		player.openInventory(inv);
	}

	/**
	 * Refresh buy inventories.
	 * 
	 * @param type
	 *            the type
	 * @param sales
	 *            the sales
	 */
	private void refreshBuyInventories(AuctionType type, List<AuctionSale> sales) {
		Map<Player, Inventory> invMap = buyInventories.get(type);

		if (invMap != null) {
			for (Inventory inv : invMap.values()) {
				loadBuyInventory(inv, sales);
			}
		}
	}

	/**
	 * Sets contents of an inventory for buy.
	 * 
	 * @param buyInv
	 *            the buy inv
	 * @param sales
	 *            the sales
	 */
	private void loadBuyInventory(Inventory buyInv, List<AuctionSale> sales) {
		buyInv.setContents(AuctionBuyInventory.generateContents(sales));
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
