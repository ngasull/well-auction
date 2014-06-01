package net.gasull.well.auction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Well Suite {@link Configuration} wrapper which sets default values if not in
 * config yet.
 */
public class WellConfig {

	/** The plugin. */
	private JavaPlugin plugin;

	/** The conf. */
	private final FileConfiguration conf;

	/** The file's relative path. */
	private final String filePath;

	/**
	 * Instantiates a new well config.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param filePath
	 *            the file's relative path
	 */
	public WellConfig(JavaPlugin plugin, String filePath) {
		this.plugin = plugin;
		this.filePath = filePath;
		this.conf = new YamlConfiguration();
	}

	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	public FileConfiguration getConfig() {
		return conf;
	}

	/**
	 * Saves the config.
	 */
	public void save() {
		try {
			conf.save(new File(plugin.getDataFolder(), filePath));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't save config file well-auction.yml", e);
		}
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
		return setAndReturn(key, conf.getBoolean(key, def));
	}

	/**
	 * Gets the boolean.
	 * 
	 * @param key
	 *            the key
	 * @return the boolean
	 */
	public boolean getBoolean(final String key) {
		return conf.getBoolean(key);
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
		return setAndReturn(key, conf.getColor(key, def));
	}

	/**
	 * Gets the color.
	 * 
	 * @param key
	 *            the key
	 * @return the color
	 */
	public Color getColor(final String key) {
		return conf.getColor(key);
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
		return setAndReturn(key, conf.getDouble(key, def));
	}

	/**
	 * Gets the double.
	 * 
	 * @param key
	 *            the key
	 * @return the double
	 */
	public double getDouble(final String key) {
		return conf.getDouble(key);
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
		return setAndReturn(key, conf.getInt(key, def));
	}

	/**
	 * Gets the int.
	 * 
	 * @param key
	 *            the key
	 * @return the int
	 */
	public int getInt(final String key) {
		return conf.getInt(key);
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
		return setAndReturn(key, conf.getItemStack(key, def));
	}

	/**
	 * Gets the item stack.
	 * 
	 * @param key
	 *            the key
	 * @return the item stack
	 */
	public ItemStack getItemStack(final String key) {
		return conf.getItemStack(key);
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
		return setAndReturn(key, conf.getLong(key, def));
	}

	/**
	 * Gets the long.
	 * 
	 * @param key
	 *            the key
	 * @return the long
	 */
	public long getLong(final String key) {
		return conf.getLong(key);
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
		return setAndReturn(key, conf.getString(key, def));
	}

	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public String getString(final String key) {
		return conf.getString(key);
	}

	/**
	 * Gets the boolean list.
	 * 
	 * @param key
	 *            the key
	 * @return the boolean list
	 */
	public List<Boolean> getBooleanList(final String key) {
		return conf.getBooleanList(key);
	}

	/**
	 * Gets the boolean list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the boolean list
	 */
	public List<Boolean> getBooleanList(final String key, final List<Boolean> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getBooleanList(key);
	}

	/**
	 * Gets the character list.
	 * 
	 * @param key
	 *            the key
	 * @return the character list
	 */
	public List<Character> getCharacterList(final String key) {
		return conf.getCharacterList(key);
	}

	/**
	 * Gets the character list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the character list
	 */
	public List<Character> getCharacterList(final String key, final List<Character> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getCharacterList(key);
	}

	/**
	 * Gets the double list.
	 * 
	 * @param key
	 *            the key
	 * @return the double list
	 */
	public List<Double> getDoubleList(final String key) {
		return conf.getDoubleList(key);
	}

	/**
	 * Gets the double list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the double list
	 */
	public List<Double> getDoubleList(final String key, final List<Double> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getDoubleList(key);
	}

	/**
	 * Gets the integer list.
	 * 
	 * @param key
	 *            the key
	 * @return the integer list
	 */
	public List<Integer> getIntegerList(final String key) {
		return conf.getIntegerList(key);
	}

	/**
	 * Gets the integer list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the integer list
	 */
	public List<Integer> getIntegerList(final String key, final List<Integer> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getIntegerList(key);
	}

	/**
	 * Gets the long list.
	 * 
	 * @param key
	 *            the key
	 * @return the long list
	 */
	public List<Long> getLongList(final String key) {
		return conf.getLongList(key);
	}

	/**
	 * Gets the long list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the long list
	 */
	public List<Long> getLongList(final String key, final List<Long> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getLongList(key);
	}

	/**
	 * Gets the short list.
	 * 
	 * @param key
	 *            the key
	 * @return the short list
	 */
	public List<Short> getShortList(final String key) {
		return conf.getShortList(key);
	}

	/**
	 * Gets the short list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the short list
	 */
	public List<Short> getShortList(final String key, final List<Short> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getShortList(key);
	}

	/**
	 * Gets the map list.
	 * 
	 * @param key
	 *            the key
	 * @return the map list
	 */
	public List<Map<?, ?>> getMapList(final String key) {
		return conf.getMapList(key);
	}

	/**
	 * Gets the map list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the map list
	 */
	public List<Map<?, ?>> getMapList(final String key, final List<Map<?, ?>> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getMapList(key);
	}

	/**
	 * Gets the string list.
	 * 
	 * @param key
	 *            the key
	 * @return the string list
	 */
	public List<String> getStringList(final String key) {
		return conf.getStringList(key);
	}

	/**
	 * Gets the string list.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return the string list
	 */
	public List<String> getStringList(final String key, final List<String> def) {
		if (!conf.contains(key)) {
			conf.set(key, def);
		}
		return conf.getStringList(key);
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
		conf.set(key, value);
		return value;
	}
}
