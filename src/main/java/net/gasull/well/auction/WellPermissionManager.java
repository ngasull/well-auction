package net.gasull.well.auction;

import java.util.logging.Level;

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

		if (notAllowed == null || !notAllowed.contains("%s")) {
			this.plugin.getLogger().log(Level.WARNING, "Not allowed permission message not containing \"%thing%\" sequence, restoring default value");
			config.getConfig().set(notAllowedKey, defaultNotAllowed);
		}

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
			throw new WellPermissionException();
		}
	}

	/**
	 * Happens when a permission isn't matched.
	 */
	public class WellPermissionException extends Exception {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

	}
}
