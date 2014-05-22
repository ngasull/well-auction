package net.gasull.well.auction.event;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.inventory.AuctionInventory;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class AuctionPlayerInteractListener implements Listener {

	private WellAuction plugin;
	private AuctionShopManager shopManager;
	private AuctionInventory inventory;

	public AuctionPlayerInteractListener(WellAuction plugin, AuctionShopManager shopManager) {
		this.plugin = plugin;
		this.shopManager = shopManager;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Location loc = event.getClickedBlock().getLocation();

		if (shopManager.getShop(loc) != null) {

		}
	}

	@EventHandler
	public void onPlayerMove(PlayerInteractEvent evt) {

		if (evt.getAction() == Action.LEFT_CLICK_AIR) {
			Player player = evt.getPlayer();

			if (inventory == null) {
				inventory = new AuctionInventory(player);
			}

			player.openInventory(inventory);
		}
	}

	@EventHandler
	public void onInventoryDrag(final InventoryDragEvent evt) {
		if (AuctionInventory.TITLE.equals(evt.getInventory().getTitle())) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent evt) {
		if (AuctionInventory.TITLE.equals(evt.getInventory().getTitle())) {

			switch (evt.getAction()) {
			case PICKUP_ALL:
			case PICKUP_HALF:
			case PICKUP_ONE:
			case PICKUP_SOME:
				break;
			case PLACE_ALL:
			case PLACE_SOME:
			case PLACE_ONE:
				if (evt.getRawSlot() < evt.getInventory().getSize()) {
					inventory.addItem(evt.getCursor());
					evt.setCancelled(true);
				}
				break;
			default:
				evt.setCancelled(true);
			}
		}
	}
}
