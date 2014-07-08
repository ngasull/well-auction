package net.gasull.well.auction.command;

import java.util.Collection;
import java.util.List;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * /wauc detach
 */
public class WaucListCommand extends WellCommand<CommandSender> {

	/** The plugin. */
	private WellAuction plugin;

	public WaucListCommand(WellAuction plugin) {
		this.plugin = plugin;
	}

	@Override
	public String handleCommand(CommandSender sender, String[] args) throws WellCommandException, WellPermissionException {
		StringBuilder msg;
		Collection<AuctionShop> shops = plugin.db().listShops();

		if (shops.isEmpty()) {
			sender.sendMessage(plugin.lang().warn("command.list.noShop"));
		} else {
			for (AuctionShop shop : shops) {
				msg = new StringBuilder().append(ChatColor.YELLOW).append(shop).append(": ").append("\n");

				int i = 0;
				String[] alterColor = new String[] { ChatColor.AQUA.toString(), ChatColor.BLUE.toString() };
				List<ShopEntity> registeredEntities = shop.getRegistered();

				for (ShopEntity shopEntity : registeredEntities) {
					msg.append(alterColor[i % 2]).append(shopEntity);

					if (++i < registeredEntities.size()) {
						msg.append(", ");
					}
				}

				sender.sendMessage(msg.toString());
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return "list";
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
