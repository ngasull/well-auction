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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
			inventoryManager.open(evt.getPlayer(), Material.STICK);
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
			if (inventoryManager.isTopInventoryEvent(evt)) {
				switch (action) {
				case MOVE:
				case PICKUP:
					doBuyAction(evt, action);
					break;
				case PLACE:
					doSellAction(evt, action);
					break;
				case SWAP:
					evt.setCancelled(true);
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
			// TODO here
		}
		// If current view is the sell view
		else if (inventoryManager.isSellInventory(evt.getInventory())) {
			evt.setCancelled(true);
		}
		// Otherwise, it's the menu
		else {
			evt.setCancelled(true);
			inventoryManager.handleMenuClick(evt.getRawSlot(), (Player) evt.getWhoClicked());
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

		// If current view if the sell view
		if (inventoryManager.isSellInventory(evt.getInventory())) {
			// TODO here
		} else {
			evt.setCancelled(true);
		}
	}
}
