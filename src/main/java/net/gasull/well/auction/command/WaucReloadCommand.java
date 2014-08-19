package net.gasull.well.auction.command;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.command.CommandSender;

/**
 * /wauc detach
 */
public class WaucReloadCommand extends WellCommand<CommandSender> {

	/** The plugin. */
	private WellAuction plugin;

	public WaucReloadCommand(WellAuction plugin) {
		this.plugin = plugin;
	}

	@Override
	public String handleCommand(CommandSender sender, String[] args) throws WellCommandException, WellPermissionException {
		plugin.onDisable();
		plugin.onEnable();

		return plugin.lang().get("command.reload.success");
	}

	@Override
	public String getName() {
		return "reload";
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
