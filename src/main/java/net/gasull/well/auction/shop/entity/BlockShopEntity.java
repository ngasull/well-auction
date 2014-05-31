package net.gasull.well.auction.shop.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.gasull.well.auction.db.ShopEntityModel;
import net.gasull.well.auction.shop.AuctionShop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

/**
 * {@link ShopEntity} as a {@link Block}.
 */
public class BlockShopEntity extends ShopEntity {

	/** The model. */
	private ShopEntityModel model;

	/** The block. */
	private Block block;

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
		this.model = new ShopEntityModel();
		this.model.setType("block");
	}

	/**
	 * Instantiates a new block shop entity.
	 * 
	 * @param model
	 *            the model
	 */
	public BlockShopEntity(ShopEntityModel model) {
		this.model = model;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register(JavaPlugin plugin, AuctionShop auctionShop) {
		model.setShop(auctionShop);

		if (block == null) {
			Map<String, Object> dataMap = (Map<String, Object>) new Yaml().load(model.getData());
			World world = Bukkit.getWorld((String) dataMap.get("w"));
			Location loc = new Location(world, (Integer) dataMap.get("x"), (Integer) dataMap.get("y"), (Integer) dataMap.get("z"));
			block = loc.getBlock();

			if (block == null) {
				plugin.getLogger().log(Level.SEVERE, "Couldn't find block at pos" + loc.toString());
			}
		}

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
		return model.getShop();
	}

	@Override
	public String toString() {
		return String.format("%s, x:%.0f z:%.0f y:%.0f (world %s)", block.getType(), block.getLocation().getX(), block.getLocation().getZ(), block
				.getLocation().getY(), block.getWorld().getName());
	}

	@Override
	public ShopEntityModel getModel() {
		model.setData(serialize());
		return model;
	}

	private String serialize() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("w", block.getWorld().getName());
		dataMap.put("x", block.getX());
		dataMap.put("y", block.getY());
		dataMap.put("z", block.getZ());
		return new Yaml().dump(dataMap);
	}
}
