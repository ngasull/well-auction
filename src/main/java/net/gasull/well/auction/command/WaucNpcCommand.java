package net.gasull.well.auction.command;

import java.util.logging.Level;

import net.citizensnpcs.api.event.NPCCreateEvent.NPCCreateReason;
import net.citizensnpcs.resources.npclib.NPCManager;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * /wauc npc creates a NPC shop.
 */
public class WaucNpcCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/**
	 * Instantiates a new wauc attach command.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WaucNpcCommand(WellAuction plugin) {
		this.plugin = plugin;
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
			plugin.getLogger().log(Level.WARNING,
					String.format("%s attempted to create a Citizen, but Citizens plugin doesn't exist in this server.", player.getName()));
			throw new WellCommandException(plugin.lang().get("command.npc.error.citizens"));
		}

		NPCManager.register(plugin.config().getString("inventory.menu.title"), player.getLocation(), player.getName(), NPCCreateReason.SPAWN);
		return plugin.lang().success("command.npc.success");
	}

	@Override
	public String getName() {
		return "npc";
	}

	@Override
	public String[] getRequiredArgs() {
		return null;
	}

	@Override
	public String[] getOptionalArgs() {
		return null;
	}

	@Override
	public String getPermission() {
		return null;
	}
}
