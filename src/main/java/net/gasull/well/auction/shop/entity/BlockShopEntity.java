package net.gasull.well.auction.shop.entity;

import net.gasull.well.auction.shop.AuctionShop;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * {@link ShopEntity} as a {@link Block}.
 */
public class BlockShopEntity extends ShopEntity {

	/** The block. */
	private Block block;

	/** The shop. */
	private AuctionShop shop;

	/** The Constant key for shop block {@link MetadataValue}. */
	public static final String META_KEY = "well-auction-block-shop";

	/**
	 * Instantiates a new block shop entity.
	 * 
	 * @param block
	 *            the block
	 */
	public BlockShopEntity(Block block) {
		this.block = block;
	}

	@Override
	public void register(JavaPlugin plugin, AuctionShop auctionShop) {
		this.shop = auctionShop;
		MetadataValue meta = new FixedMetadataValue(plugin, this);
		block.setMetadata(META_KEY, meta);
	}

	@Override
	public void unregister(JavaPlugin plugin) {
		block.removeMetadata(META_KEY, plugin);
	}

	/**
	 * Gets the block.
	 * 
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Gets the shop.
	 * 
	 * @return the shop
	 */
	public AuctionShop getShop() {
		return shop;
	}
}
