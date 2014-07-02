/*
 * 
 */
package net.gasull.well.auction.inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionPlayer;
import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.shop.AuctionShopException;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	/** The shop manager. */
	private AuctionShopManager shopManager;

	/** The sell inventories. */
	private Map<AuctionShop, Map<Player, InventoryView>> sellInventories = new HashMap<>();

	/** The buy inventories. */
	private Map<AuctionShop, Map<Player, InventoryView>> buyInventories = new HashMap<>();

	/** The shop for sell inventory. */
	private Map<InventoryView, AuctionShop> shopForSellInventory = new HashMap<>();

	/** The shop for buy inventory. */
	private Map<InventoryView, AuctionShop> shopForBuyInventory = new HashMap<>();

	/** The scheduled timouts for price setting tasks by player. */
	private Map<Player, AuctionSetPriceCancelTask> setPriceTasks = new HashMap<>();

	/** The auction inventory's title base (first part). */
	private final String titleBase;

	/** The sell sub view title. */
	private final String titleSell;

	/** The buy sub view title. */
	private final String titleBuy;

	/** The separator between title base and sub view title. */
	private static final String TITLE_SEPARATOR = " - ";

	/** The message for set price please. */
	private final String msgSetPricePlease;

	/** The message set invalid price. */
	private final String msgSetPriceInvalid;

	/** The message set price canceled. */
	private final String msgSetPriceCanceled;

	/** The set price delay. */
	private final long setPriceTimeout;

	/**
	 * Instantiates a new auction inventory manager.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shopManager
	 *            the shop manager
	 */
	public AuctionInventoryManager(WellAuction plugin, AuctionShopManager shopManager) {
		this.plugin = plugin;
		this.shopManager = shopManager;
		this.titleBase = plugin.wellConfig().getString("inventory.menu.title", "Auction House");
		this.titleSell = titleBase + TITLE_SEPARATOR + plugin.wellConfig().getString("lang.inventory.sell.title", "Sell");
		this.titleBuy = titleBase + TITLE_SEPARATOR + plugin.wellConfig().getString("lang.inventory.buy.title", "Buy");
		this.setPriceTimeout = plugin.wellConfig().getLong("player.setPrice.timeout", 140);

		this.msgSetPricePlease = plugin.wellConfig().getString("lang.player.setPrice.please", "Please type in the chat the price you want to sell %item% at");
		this.msgSetPriceInvalid = plugin.wellConfig().getString("lang.player.setPrice.invalid", "Invalid price, operation canceled");
		this.msgSetPriceCanceled = plugin.wellConfig().getString("lang.player.setPrice.canceled", "Price set canceled");
	}

	/**
	 * Open default price setting procedure.
	 * 
	 * @param player
	 *            the player
	 * @param auctionSellerData
	 *            the auction seller data
	 */
	public void openDefaultPriceSet(Player player, AuctionSellerData auctionSellerData) {

		// Cancel any pending task for the player
		AuctionSetPriceCancelTask task = setPriceTasks.remove(player);
		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.id);
		}

		// Create the new task
		task = new AuctionSetPriceCancelTask(player, auctionSellerData);
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, setPriceTimeout);
		task.id = taskId;

		setPriceTasks.put(player, task);
		player.closeInventory();
		player.sendMessage(msgSetPricePlease.replace("%item%", auctionSellerData.getShop().getRefItemCopy().toString()));
	}

	/**
	 * Open price setting procedure.
	 * 
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 */
	public void openPriceSet(Player player, AuctionSale sale) {

		// Cancel any pending task for the player
		AuctionSetPriceCancelTask task = setPriceTasks.remove(player);
		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.id);
		}

		// Create the new task
		task = new AuctionSetPriceCancelTask(player, sale);
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, setPriceTimeout);
		task.id = taskId;

		setPriceTasks.put(player, task);
		player.closeInventory();
		player.sendMessage(msgSetPricePlease.replace("%item%", sale.getItem().toString()));
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
		Inventory sellInv = Bukkit.createInventory(player, AuctionSellInventory.SIZE, titleSell);
		loadSellInventory(sellInv, plugin.db().getSalesOf(shop, player));
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
		Inventory buyInv = Bukkit.createInventory(player, AuctionBuyInventory.SIZE, titleBuy);
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
		loadSellInventory(inv, plugin.db().getSalesOf(shop, player));
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
	 * Handle price set.
	 * 
	 * @param player
	 *            the player
	 * @param message
	 *            the message
	 * @return true, if a price set actually has been handled.
	 */
	public boolean handlePriceSet(Player player, String message) {
		AuctionSetPriceCancelTask task = setPriceTasks.remove(player);

		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.id);
			Double price = checkPriceSet(player, message);

			if (price != null) {
				try {
					if (task.sale == null) {
						shopManager.setDefaultPrice(player, task.sellerData, price);
					} else {
						shopManager.changeSalePrice(player, task.sale, price);
					}

					refreshBuyInventories(task.shop);
					reOpenSell(task);
				} catch (AuctionShopException e) {
					plugin.getLogger().log(Level.WARNING, "Error while changing price", e);
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Cancel price set.
	 * 
	 * @param player
	 *            the player
	 * @param reopenIfPossible
	 *            reopen sell inventory if possible
	 * @return true, if successful
	 */
	public boolean cancelPriceSet(Player player, boolean reopenIfPossible) {
		AuctionSetPriceCancelTask task = setPriceTasks.remove(player);

		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.id);

			if (reopenIfPossible) {
				reOpenSell(task);
			}

			player.sendMessage(ChatColor.YELLOW + msgSetPriceCanceled);
			return true;
		}
		return false;
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
		return inventory.getTitle().startsWith(titleBase);
	}

	/**
	 * Checks if is auction menu inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory menu
	 */
	public boolean isMenuInventory(Inventory inventory) {
		return inventory.getTitle().equals(titleBase);
	}

	/**
	 * Checks if is auction sell inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory sell
	 */
	public boolean isSellInventory(Inventory inventory) {
		return inventory.getTitle().equals(titleSell);
	}

	/**
	 * Checks if is auction buy inventory.
	 * 
	 * @param inventory
	 *            the inventory
	 * @return true, if is auction inventory buy
	 */
	public boolean isBuyInventory(Inventory inventory) {
		return inventory.getTitle().equals(titleBuy);
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
				loadSellInventory(inv, plugin.db().getSalesOf(shop, pair.getKey()));
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
	private void loadBuyInventory(Inventory buyInv, Collection<AuctionSale> sales) {
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
	 * Check price set.
	 * 
	 * @param player
	 *            the player
	 * @param price
	 *            the price
	 * @return the double
	 */
	private Double checkPriceSet(Player player, String price) {
		try {
			return Double.valueOf(Integer.valueOf(price).doubleValue());
		} catch (NumberFormatException e) {
			switch (price) {
			case "unset":
			case "reset":
			case "-":
				// Arbitrary negative value will unset price
				return -1d;
			default:
				player.sendMessage(ChatColor.DARK_RED + msgSetPriceInvalid);
			}
			return null;
		}
	}

	/**
	 * Reopen after price set.
	 * 
	 * @param task
	 *            the task
	 */
	private void reOpenSell(AuctionSetPriceCancelTask task) {
		openSell(task.player, task.shop);
	}

	/**
	 * The task that cancels a registered action to set price from player's
	 * chat.
	 */
	public class AuctionSetPriceCancelTask implements Runnable {

		/** The task id. */
		private int id;

		/** The player. */
		private final Player player;

		/** The player. */
		private final AuctionPlayer auctionPlayer;

		/** The shop. */
		private final AuctionShop shop;

		/** The seller data. */
		private AuctionSellerData sellerData;

		/** The sale. */
		private AuctionSale sale;

		/**
		 * Instantiates a new auction set price cancel task.
		 * 
		 * @param player
		 *            the player
		 * @param sellerData
		 *            the seller data
		 */
		private AuctionSetPriceCancelTask(Player player, AuctionSellerData sellerData) {
			this.player = player;
			this.sellerData = sellerData;
			this.auctionPlayer = sellerData.getAuctionPlayer();
			this.shop = sellerData.getShop();
		}

		/**
		 * Instantiates a new auction set price cancel task.
		 * 
		 * @param player
		 *            the player
		 * @param sale
		 *            the sale
		 */
		private AuctionSetPriceCancelTask(Player player, AuctionSale sale) {
			this.player = player;
			this.sale = sale;
			this.auctionPlayer = sale.getSellerData().getAuctionPlayer();
			this.shop = sale.getShop();
		}

		@Override
		public void run() {
			if (setPriceTasks.remove(player) != null) {
				auctionPlayer.sendMessage(ChatColor.YELLOW + msgSetPriceCanceled);
			}
		}
	}
}
