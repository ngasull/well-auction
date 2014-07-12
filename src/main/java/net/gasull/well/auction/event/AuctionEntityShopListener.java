package net.gasull.well.auction.event;

import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.entity.AucShopEntityManager;
import net.gasull.well.auction.shop.entity.EntityShopEntity;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class AuctionEntityShopListener implements Listener {

	/** The plugin. */
	private WellAuction plugin;

	/** The shop entity manager. */
	private AucShopEntityManager shopEntityManager;

	/**
	 * Instantiates a new auction player interact listener.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shopEntityManager
	 *            the shop entity manager
	 */
	public AuctionEntityShopListener(WellAuction plugin, AucShopEntityManager shopEntityManager) {
		this.plugin = plugin;
		this.shopEntityManager = shopEntityManager;
	}

	/**
	 * On player interact.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent evt) {

		EntityShopEntity shopEntity = EntityShopEntity.forEntity(evt.getRightClicked());

		if (shopEntity != null) {
			evt.setCancelled(true);
			Player player = evt.getPlayer();

			try {
				shopEntityManager.open(shopEntity, player);
			} catch (WellPermissionException e) {
				plugin.getLogger().log(Level.INFO, String.format("%s couldn't open auction house (%s)", player.getName(), e.getKey()));
			}
		}
	}

	/**
	 * On NPC shops damage, prevent.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onDamage(EntityDamageEvent evt) {

		EntityShopEntity shopEntity = EntityShopEntity.forEntity(evt.getEntity());

		if (shopEntity != null) {
			evt.setCancelled(true);
		}
	}

	/**
	 * On entity die, remove associated shop if exists.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onEntityDie(EntityDeathEvent evt) {

		EntityShopEntity shopEntity = EntityShopEntity.forEntity(evt.getEntity());

		if (shopEntity != null) {
			plugin.db().deleteShopEntity(shopEntity);
		}
	}
}
