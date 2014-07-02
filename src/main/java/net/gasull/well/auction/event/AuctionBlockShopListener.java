package net.gasull.well.auction.event;

import java.util.List;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.inventory.AuctionInventoryManager;
import net.gasull.well.auction.shop.entity.BlockShopEntity;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

/**
 * The listener interface for receiving auctionBlockShop events. The class that
 * is interested in processing a auctionBlockShop event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addAuctionBlockShopListener<code> method. When
 * the auctionBlockShop event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see AuctionBlockShopEvent
 */
public class AuctionBlockShopListener implements Listener {

	/** The plugin. */
	private WellAuction plugin;

	/** The inventory manager. */
	private AuctionInventoryManager inventoryManager;

	/**
	 * Instantiates a new auction player interact listener.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param inventoryManager
	 *            the inventory manager
	 */
	public AuctionBlockShopListener(WellAuction plugin, AuctionInventoryManager inventoryManager) {
		this.plugin = plugin;
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

		if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockShopEntity shopBlock = getShopForBlock(evt.getClickedBlock());

			if (shopBlock != null) {
				inventoryManager.openMenu(evt.getPlayer(), shopBlock.getShops());
				evt.setCancelled(true);
			}
		}
	}

	/**
	 * On block break.
	 * 
	 * @param evt
	 *            the evt
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt) {

		BlockShopEntity shopBlock = getShopForBlock(evt.getBlock());

		if (shopBlock != null) {
			shopBlock.unregister(plugin);
			plugin.db().delete(shopBlock.getModel());
		}
	}

	/**
	 * Gets the shop for block.
	 * 
	 * @param block
	 *            the block
	 * @return the shop entity for block
	 */
	private BlockShopEntity getShopForBlock(Block block) {
		BlockShopEntity shop = null;

		if (block != null) {
			List<MetadataValue> meta = block.getMetadata(BlockShopEntity.META_KEY);

			if (meta != null && !meta.isEmpty()) {
				shop = (BlockShopEntity) meta.get(0).value();
			}
		}

		return shop;
	}
}
