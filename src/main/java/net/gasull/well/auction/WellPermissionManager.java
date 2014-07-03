package net.gasull.well.auction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Class WellPermissionManager.
 */
public class WellPermissionManager {

	/** The plugin. */
	private JavaPlugin plugin;

	/** The message template for not allowed permissions. */
	private final String notAllowedMsg;

	/**
	 * Instantiates a new well permission manager.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param config
	 *            the config
	 */
	public WellPermissionManager(JavaPlugin plugin, WellConfig config) {
		this.plugin = plugin;

		// Preparing "not allowed" messages
		String notAllowedKey = "lang.permission.notAllowed";
		String defaultNotAllowed = "You're not allowed to %thing%";
		String notAllowed = config.getString(notAllowedKey, defaultNotAllowed);

		this.notAllowedMsg = notAllowed;
	}

	/**
	 * Determines if a player can do something.
	 * 
	 * @param player
	 *            the player
	 * @param thing
	 *            an expression describing the thing to do
	 * @param key
	 *            the permission key
	 * @throws WellPermissionException
	 *             the well permission exception
	 */
	public void can(Player player, String thing, String key) throws WellPermissionException {
		if (!player.hasPermission(key)) {
			player.sendMessage(ChatColor.DARK_RED + notAllowedMsg.replace("%thing%", thing));
			throw new WellPermissionException(key);
		}
	}

	/**
	 * Happens when a permission isn't matched.
	 */
	public class WellPermissionException extends Exception {

		/** The key. */
		private final String key;

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new well permission exception.
		 * 
		 * @param key
		 *            the key
		 */
		public WellPermissionException(String key) {
			this.key = key;
		}

		/**
		 * Gets the key.
		 * 
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

	}
}
