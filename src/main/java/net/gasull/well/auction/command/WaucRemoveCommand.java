package net.gasull.well.auction.command;

import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /wauc detach
 */
public class WaucRemoveCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/** Shop remove success message. */
	private final String SUCC_REMOVE;

	public WaucRemoveCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
		this.SUCC_REMOVE = ChatColor.GREEN + plugin.wellConfig().getString("lang.command.remove.success", "Successfully removed a shop");
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

		return SUCC_REMOVE;
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
