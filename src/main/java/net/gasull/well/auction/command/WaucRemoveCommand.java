package net.gasull.well.auction.command;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.entity.Player;

/**
 * /wauc detach
 */
public class WaucRemoveCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/**
	 * Instantiates a new wauc remove command.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param helper
	 *            the helper
	 */
	public WaucRemoveCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		ShopEntity shopEntity = helper.getTargetShop(args, player);
		if (shopEntity.getModel().getId() == 0) {
			return null;
		}

		shopEntity.unregister();

		for (AucEntityToShop entityToShop : shopEntity.getModel().getEntityToShops()) {
			plugin.db().delete(entityToShop);
		}
		plugin.db().delete(shopEntity.getModel());

		return plugin.lang().success("command.remove.success");
	}

	@Override
	public String getName() {
		return "remove";
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
