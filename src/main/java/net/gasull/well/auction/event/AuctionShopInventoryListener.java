package net.gasull.well.auction.event;

import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.inventory.AuctionMenu;
import net.gasull.well.auction.shop.AuctionSale;
import net.gasull.well.auction.shop.AuctionShop;
import net.gasull.well.auction.shop.AuctionShopException;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The listener interface for receiving auctionPlayerInteract events. The class
 * that is interested in processing a auctionPlayerInteract event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addAuctionPlayerInteractListener<code> method. When
 * the auctionPlayerInteract event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see AuctionPlayerInteractEvent
 */
public class AuctionShopInventoryListener implements Listener {

	/** The plugin. */
	private WellAuction plugin;

	/** The shop manager. */
	private AuctionShopManager shopManager;

	/** The inventory manager. */
	private AuctionInventoryManager inventoryManager;

	/**
	 * Instantiates a new auction player interact listener.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shopManager
	 *            the shop manager
	 * @param inventoryManager
	 *            the inventory manager
	 */
	public AuctionShopInventoryListener(WellAuction plugin, AuctionShopManager shopManager, AuctionInventoryManager inventoryManager) {
		this.plugin = plugin;
		this.shopManager = shopManager;
		this.inventoryManager = inventoryManager;
	}

	/**
	 * On player interact.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent evt) {

		if (evt.getAction() == Action.LEFT_CLICK_AIR) {
			inventoryManager.openMenu(evt.getPlayer(), shopManager.getShop(new ItemStack(Material.STICK)));
		}
	}

	/**
	 * Cancel inventory drags in Auction Houses.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onInventoryDrag(final InventoryDragEvent evt) {
		if (inventoryManager.isAuctionInventory(evt.getInventory())) {
			for (int slot : evt.getRawSlots()) {
				if (slot < evt.getView().getTopInventory().getSize()) {
					evt.setCancelled(true);
					return;
				}
			}
		}
	}

	/**
	 * Handle Auction inventory clicks.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent evt) {
		if (inventoryManager.isAuctionInventory(evt.getInventory())) {
			AuctionInventoryAction action;

			/*
			 * Enumerate all cases where player-side inventory action shouldn't
			 * be canceled for a more robust check for next Bukkit versions.
			 * Acts as a first filter.
			 */
			switch (evt.getAction()) {
			case PICKUP_ALL:
			case PICKUP_HALF:
			case PICKUP_ONE:
			case PICKUP_SOME:
				action = AuctionInventoryAction.PICKUP;
				break;
			case PLACE_ALL:
			case PLACE_SOME:
			case PLACE_ONE:
				action = AuctionInventoryAction.PLACE;
				break;
			case DROP_ALL_CURSOR:
			case DROP_ALL_SLOT:
			case DROP_ONE_CURSOR:
			case DROP_ONE_SLOT:
				action = AuctionInventoryAction.DROP;
				break;
			case SWAP_WITH_CURSOR:
				action = AuctionInventoryAction.SWAP;
				break;
			case MOVE_TO_OTHER_INVENTORY:
				action = AuctionInventoryAction.MOVE;
				break;
			case NOTHING:
				return;
			default:
				evt.setCancelled(true);
				return;
			}

			// Clicked inside auction shop
			if (inventoryManager.isTopInventoryEvent(evt.getInventory(), evt.getRawSlot())) {
				switch (action) {
				case MOVE:
				case PICKUP:
					doBuyAction(evt, action);
					break;
				case PLACE:
				case SWAP:
					doSellAction(evt, action);
					break;
				default:
					// Do nothing
				}
			}
			// Clicked anywhere else
			else {
				switch (action) {
				case MOVE:
					doSellAction(evt, action);
					break;
				default:
					// Do nothing
				}
			}
		}
	}

	/**
	 * Handle Auction inventory closes.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent evt) {
		if (inventoryManager.isAuctionInventory(evt.getInventory()) && evt.getPlayer() instanceof Player) {
			inventoryManager.handleClose(evt.getView(), (Player) evt.getPlayer());
		}
	}

	/**
	 * Do buy action (of the type auction shop to player's inventory).
	 * 
	 * @param evt
	 *            the evt
	 * @param action
	 *            the action
	 */
	private void doBuyAction(final InventoryClickEvent evt, final AuctionInventoryAction action) {

		// Operations manually managed
		evt.setCancelled(true);

		// If current view is the buy view
		if (inventoryManager.isBuyInventory(evt.getInventory())) {
			ItemStack theItem = theItem(evt, action);
			Player player = (Player) evt.getWhoClicked();

			if (inventoryManager.checkBuy(evt.getView(), player, theItem)) {

				try {
					AuctionSale sale = shopManager.buy(player, theItem);
					ItemStack bought = inventoryManager.handleBuy(sale);
					player.setItemOnCursor(bought);
					plugin.getLogger().info(player.getName() + " successfully bought " + bought);
				} catch (AuctionShopException e) {
					plugin.getLogger().log(Level.WARNING, player.getName() + " couldn't buy " + theItem, e);
				} catch (WellPermissionException e) {
					plugin.getLogger().log(Level.INFO, player.getName() + " was not allowed to buy " + theItem);
				}
			}
		}
		// If current view is the sell view
		else if (inventoryManager.isSellInventory(evt.getInventory())) {
			// Do nothing
		}
		// Otherwise, it's the menu
		else {
			evt.setCancelled(true);
			ItemStack refItem = evt.getInventory().getItem(AuctionMenu.REFITEM_SLOT);
			AuctionShop shop = shopManager.getShop(refItem);
			Player player = (Player) evt.getWhoClicked();

			switch (evt.getRawSlot()) {
			case AuctionMenu.BUY_SLOT:
				inventoryManager.openBuy(player, shop);
				break;
			case AuctionMenu.SALE_SLOT:
				inventoryManager.openSell(player, shop);
				break;
			default:
				// Do nothing
			}
		}
	}

	/**
	 * Do sell action (of the type player's inventory to auction shop).
	 * 
	 * @param evt
	 *            the evt
	 * @param action
	 *            the action
	 */
	private void doSellAction(final InventoryClickEvent evt, final AuctionInventoryAction action) {

		/*
		 * If sell-like (or anything else) action, cancel. Operations will be
		 * manually managed.
		 */
		evt.setCancelled(true);

		// If current view if the sell view
		if (inventoryManager.isSellInventory(evt.getInventory()) && evt.getWhoClicked() instanceof Player) {
			ItemStack theItem = theItem(evt, action);
			Player player = (Player) evt.getWhoClicked();

			if (inventoryManager.checkSell(evt.getView(), player, theItem)) {

				try {
					AuctionSale sale = shopManager.sell(player, theItem);
					inventoryManager.handleSell(evt.getInventory(), sale.getShop(), player);
					plugin.getLogger().info(player.getName() + " successfully put on sale " + theItem);
					removeTheItem(evt, action);
				} catch (AuctionShopException e) {
					plugin.getLogger().log(Level.WARNING, player.getName() + " couldn't sell " + theItem, e);
				} catch (WellPermissionException e) {
					plugin.getLogger().log(Level.INFO, player.getName() + " was not allowed to sell " + theItem);
				}
			}
		}
	}

	/**
	 * Determines the item being traded depending on the action.
	 * 
	 * @param evt
	 *            the evt
	 * @param action
	 *            the action
	 * @return the item stack
	 */
	private ItemStack theItem(final InventoryClickEvent evt, final AuctionInventoryAction action) {
		switch (action) {
		case MOVE:
		case PICKUP:
			return evt.getCurrentItem();
		case PLACE:
		case SWAP:
		case DROP:
			return evt.getCursor();
		default:
			throw new IllegalArgumentException("Can't get item from action " + action.name());
		}
	}

	/**
	 * Removes the item being traded depending on the action.
	 * 
	 * @param evt
	 *            the evt
	 * @param action
	 *            the action
	 * @return the item stack
	 */
	private void removeTheItem(final InventoryClickEvent evt, final AuctionInventoryAction action) {
		switch (action) {
		case MOVE:
			if (inventoryManager.isSellInventory(evt.getInventory())) {
				evt.getView().getBottomInventory().setItem(evt.getSlot(), null);
			}
			break;
		case PICKUP:
			evt.getInventory().setItem(evt.getRawSlot(), null);
			break;
		case PLACE:
		case SWAP:
		case DROP:
			((Player) evt.getWhoClicked()).setItemOnCursor(null);
			break;
		default:
			throw new IllegalArgumentException("Can't get item from action " + action.name());
		}
	}
}
