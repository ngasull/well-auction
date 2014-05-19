package net.gasull.well.auction.inventory;

import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class AuctionPlayerInteractListener implements Listener {

	private AuctionShopManager shopManager;

	public AuctionPlayerInteractListener(AuctionShopManager shopManager) {
		this.shopManager = shopManager;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Location loc = event.getClickedBlock().getLocation();

		if (shopManager.getShop(loc) != null) {

		}
	}
}
