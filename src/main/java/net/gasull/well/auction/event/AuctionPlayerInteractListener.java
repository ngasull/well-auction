package net.gasull.well.auction.event;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
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

public class AuctionPlayerInteractListener implements Listener {

	private WellAuction plugin;
	private AuctionShopManager shopManager;
	private AuctionInventoryManager inventoryManager;

	public AuctionPlayerInteractListener(WellAuction plugin, AuctionShopManager shopManager, AuctionInventoryManager inventoryManager) {
		this.plugin = plugin;
		this.shopManager = shopManager;
		this.inventoryManager = inventoryManager;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent evt) {

		if (evt.getAction() == Action.LEFT_CLICK_AIR) {
			inventoryManager.openMenu(evt.getPlayer(), Material.STICK);
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
			inventoryManager.handleClose(evt.getInventory(), (Player) evt.getPlayer());
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

		// If current view is the buy view
		if (inventoryManager.isBuyInventory(evt.getInventory())) {
			ItemStack theItem = theItem(evt, action);
			Player player = (Player) evt.getWhoClicked();

			if (inventoryManager.checkBuy(evt.getInventory(), evt.getRawSlot(), player, theItem)) {

				// DO THE BUSINESS

				ItemStack bought = inventoryManager.handleBuy(evt.getInventory(), player, theItem);
				evt.setCurrentItem(bought);
				plugin.getLogger().info(player.getName() + " successfully bought " + theItem);
			} else {
				evt.setCancelled(true);
			}
		}
		// If current view is the sell view
		else if (inventoryManager.isSellInventory(evt.getInventory())) {
			evt.setCancelled(true);
		}
		// Otherwise, it's the menu
		else {
			evt.setCancelled(true);
			inventoryManager.handleMenuClick(evt.getInventory(), evt.getRawSlot(), (Player) evt.getWhoClicked());
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

			if (inventoryManager.checkSell(evt.getInventory(), evt.getRawSlot(), player, theItem)) {

				// DO THE BUSINESS

				inventoryManager.handleSell(evt.getInventory(), player, theItem);
				plugin.getLogger().info(player.getName() + " successfully put on sale " + theItem);
				removeTheItem(evt, action);
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
