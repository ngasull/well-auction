package net.gasull.well.auction.event;

import net.gasull.well.auction.inventory.AuctionInventory;
import net.gasull.well.auction.shop.AuctionShopManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

	@EventHandler
	public void onPlayerMove(PlayerInteractEvent evt) {
		Player player = evt.getPlayer();
		new AuctionInventory(player).open(player);
	}
}
