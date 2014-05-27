package net.gasull.well.auction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The Class WellAuctionCommandHandler.
 */
public class WellAuctionCommandHandler {

	/** The plugin. */
	private WellAuction plugin;

	/** The command's name. */
	private final String CMD_NAME;

	/**
	 * Instantiates a new well auction command handler.
	 * 
	 * @param plugin
	 *            the well auction plugin
	 */
	public WellAuctionCommandHandler(WellAuction plugin) {
		this.plugin = plugin;
		this.CMD_NAME = plugin.wellConfig().getString("command.name", "auchouse");
	}

	/**
	 * Handle.
	 * 
	 * @param sender
	 *            the sender
	 * @param cmd
	 *            the cmd
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	public boolean handle(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase(CMD_NAME)) {
			// doSomething
			return true;
		}

		return false;
	}

}
