package net.gasull.well.auction;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Well Suite {@link Configuration} wrapper which sets default values if not in
 * config yet.
 */
public class WellConfig {

	/** The plugin. */
	private JavaPlugin plugin;

	/**
	 * Instantiates a new well config.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WellConfig(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	/**
	 * Gets the boolean.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the boolean
	 */
	public boolean getBoolean(final String key, final boolean def) {
		return setAndReturn(key, plugin.getConfig().getBoolean(key, def));
	}

	/**
	 * Gets the boolean.
	 * 
	 * @param key
	 *            the key
	 * @return the boolean
	 */
	public boolean getBoolean(final String key) {
		return plugin.getConfig().getBoolean(key);
	}

	/**
	 * Gets the color.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the color
	 */
	public Color getColor(final String key, final Color def) {
		return setAndReturn(key, plugin.getConfig().getColor(key, def));
	}

	/**
	 * Gets the color.
	 * 
	 * @param key
	 *            the key
	 * @return the color
	 */
	public Color getColor(final String key) {
		return plugin.getConfig().getColor(key);
	}

	/**
	 * Gets the double.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the double
	 */
	public double getDouble(final String key, final double def) {
		return setAndReturn(key, plugin.getConfig().getDouble(key, def));
	}

	/**
	 * Gets the double.
	 * 
	 * @param key
	 *            the key
	 * @return the double
	 */
	public double getDouble(final String key) {
		return plugin.getConfig().getDouble(key);
	}

	/**
	 * Gets the int.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the int
	 */
	public int getInt(final String key, final int def) {
		return setAndReturn(key, plugin.getConfig().getInt(key, def));
	}

	/**
	 * Gets the int.
	 * 
	 * @param key
	 *            the key
	 * @return the int
	 */
	public int getInt(final String key) {
		return plugin.getConfig().getInt(key);
	}

	/**
	 * Gets the item stack.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the item stack
	 */
	public ItemStack getItemStack(final String key, final ItemStack def) {
		return setAndReturn(key, plugin.getConfig().getItemStack(key, def));
	}

	/**
	 * Gets the item stack.
	 * 
	 * @param key
	 *            the key
	 * @return the item stack
	 */
	public ItemStack getItemStack(final String key) {
		return plugin.getConfig().getItemStack(key);
	}

	/**
	 * Gets the long.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the long
	 */
	public long getLong(final String key, final long def) {
		return setAndReturn(key, plugin.getConfig().getLong(key, def));
	}

	/**
	 * Gets the long.
	 * 
	 * @param key
	 *            the key
	 * @return the long
	 */
	public long getLong(final String key) {
		return plugin.getConfig().getLong(key);
	}

	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the default value
	 * @return the string
	 */
	public String getString(final String key, final String def) {
		return setAndReturn(key, plugin.getConfig().getString(key, def));
	}

	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public String getString(final String key) {
		return plugin.getConfig().getString(key);
	}

	/**
	 * Gets the boolean list.
	 * 
	 * @param key
	 *            the key
	 * @return the boolean list
	 */
	public List<Boolean> getBooleanList(final String key) {
		return plugin.getConfig().getBooleanList(key);
	}

	/**
	 * Gets the character list.
	 * 
	 * @param key
	 *            the key
	 * @return the character list
	 */
	public List<Character> getCharacterList(final String key) {
		return plugin.getConfig().getCharacterList(key);
	}

	/**
	 * Gets the double list.
	 * 
	 * @param key
	 *            the key
	 * @return the double list
	 */
	public List<Double> getDoubleList(final String key) {
		return plugin.getConfig().getDoubleList(key);
	}

	/**
	 * Gets the integer list.
	 * 
	 * @param key
	 *            the key
	 * @return the integer list
	 */
	public List<Integer> getIntegerList(final String key) {
		return plugin.getConfig().getIntegerList(key);
	}

	/**
	 * Gets the long list.
	 * 
	 * @param key
	 *            the key
	 * @return the long list
	 */
	public List<Long> getLongList(final String key) {
		return plugin.getConfig().getLongList(key);
	}

	/**
	 * Gets the short list.
	 * 
	 * @param key
	 *            the key
	 * @return the short list
	 */
	public List<Short> getShortList(final String key) {
		return plugin.getConfig().getShortList(key);
	}

	/**
	 * Gets the map list.
	 * 
	 * @param key
	 *            the key
	 * @return the map list
	 */
	public List<Map<?, ?>> getMapList(final String key) {
		return plugin.getConfig().getMapList(key);
	}

	/**
	 * Gets the string list.
	 * 
	 * @param key
	 *            the key
	 * @return the string list
	 */
	public List<String> getStringList(final String key) {
		return plugin.getConfig().getStringList(key);
	}

	/**
	 * Sets the conf value and returns it afterwards.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the t
	 */
	private <T> T setAndReturn(final String key, final T value) {
		plugin.getConfig().set(key, value);
		return value;
	}
}
