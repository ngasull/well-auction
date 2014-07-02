package net.gasull.well.auction.shop.entity;

import java.util.HashMap;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.ShopEntityModel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.yaml.snakeyaml.Yaml;

/**
 * {@link ShopEntity} as a {@link Block}.
 */
public class BlockShopEntity extends ShopEntity {

	/** The plugin. */
	private WellAuction plugin;

	/** The model. */
	private ShopEntityModel model;

	/** The block. */
	private Block block;

	/** The Constant key for shop block {@link MetadataValue}. */
	public static final String META_KEY = "well-auction-block-shop";

	/**
	 * Instantiates a new block shop entity.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param block
	 *            the block
	 */
	public BlockShopEntity(WellAuction plugin, Block block) {
		super(plugin);
		this.plugin = plugin;
		this.block = block;
		this.model = new ShopEntityModel();
		this.model.setType("block");
	}

	/**
	 * Instantiates a new block shop entity.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param model
	 *            the model
	 */
	public BlockShopEntity(WellAuction plugin, ShopEntityModel model) {
		super(plugin);
		this.plugin = plugin;
		this.model = model;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register() {
		if (block == null) {
			Map<String, Object> dataMap = (Map<String, Object>) new Yaml().load(model.getData());
			World world = Bukkit.getWorld((String) dataMap.get("w"));
			Location loc = new Location(world, (Integer) dataMap.get("x"), (Integer) dataMap.get("y"), (Integer) dataMap.get("z"));
			block = loc.getBlock();

			if (block == null) {
				throw new RuntimeException("Couldn't find block at pos" + loc.toString());
			}
		}

		MetadataValue meta = new FixedMetadataValue(plugin, this);
		block.setMetadata(META_KEY, meta);

		super.register();
	}

	@Override
	public void unregister() {
		super.unregister();
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
