package net.gasull.well.auction.command;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * /wauc npc creates a NPC shop.
 */
public class WaucNpcCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/**
	 * Instantiates a new wauc attach command.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WaucNpcCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
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
