package net.gasull.well.auction.shop.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.ShopEntityModel;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.NPC;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.yaml.snakeyaml.Yaml;

/**
 * The Class EntityShopEntity.
 */
public class EntityShopEntity extends ShopEntity {

	/** The plugin. */
	private WellAuction plugin;

	/** The entity. */
	private Entity entity;

	/** The model. */
	private ShopEntityModel model;

	/** The Constant key for shop entity {@link MetadataValue}. */
	public static final String META_KEY = "well-auction-entity-shop";

	/**
	 * Instantiates a new entity shop entity.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param entity
	 *            the entity
	 */
	public EntityShopEntity(WellAuction plugin, Entity entity) {
		super(plugin);
		this.plugin = plugin;
		this.entity = entity;
		this.model = new ShopEntityModel();

		model.setType("entity");
		model.setData(serialize());
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public EntityShopEntity(WellAuction plugin, ShopEntityModel model) {
		super(plugin);
		this.plugin = plugin;
		this.model = new ShopEntityModel();

		Map<String, Object> dataMap = (Map<String, Object>) new Yaml().load(model.getData());
		World world = Bukkit.getWorld(UUID.fromString((String) dataMap.get("w")));
		int id = (Integer) dataMap.get("id");
		EntityType type = EntityType.fromName((String) dataMap.get("t"));

		for (Entity entity : world.getEntitiesByClass(type.getEntityClass())) {
			if (id == entity.getEntityId()) {
				this.entity = entity;
				return;
			}
		}

		plugin.getLogger().log(Level.WARNING, String.format("Couldn't find entity with id %d in world %s", id, world.getName()));
	}

	@Override
	public void register() {
		if (entity != null) {
			MetadataValue meta = new FixedMetadataValue(plugin, this);
			entity.setMetadata(META_KEY, meta);
		}
		super.register();
	}

	@Override
	public void unregister() {
		super.unregister();

		if (entity != null) {
			entity.removeMetadata(META_KEY, plugin);
		}
	}

	@Override
	public ShopEntityModel getModel() {
		return model;
	}

	/**
	 * Gets the entity.
	 * 
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Serialize.
	 * 
	 * @return the string
	 */
	private String serialize() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("w", entity.getWorld().getUID().toString());
		dataMap.put("id", entity.getEntityId());
		dataMap.put("t", entity.getType().name());
		return new Yaml().dump(dataMap);
	}

	/**
	 * Checks if an entity can be shop.
	 * 
	 * @param entity
	 *            the entity
	 * @return true, if successful
	 */
	public static boolean canBeShop(Entity entity) {
		return entity instanceof Animals || entity instanceof NPC;
	}

	/**
	 * Gets the shop for an entity.
	 * 
	 * @param entity
	 *            the entity
	 * @return the shop for entity
	 */
	public static EntityShopEntity forEntity(Entity entity) {
		EntityShopEntity shop = null;

		if (entity != null) {
			List<MetadataValue> meta = entity.getMetadata(EntityShopEntity.META_KEY);

			if (meta != null && !meta.isEmpty()) {
				shop = (EntityShopEntity) meta.get(0).value();
			}
		}

		return shop;
	}
}
