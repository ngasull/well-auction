package net.gasull.well.auction.command;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;
import net.gasull.well.conf.WellPermissionManager.WellPermissionException;

import org.bukkit.entity.Player;

/**
 * /wauc detach
 */
public class WaucDetachCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	public WaucDetachCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		ShopEntity shopEntity = helper.getTargetShop(args, player);
		ShopEntityModel model = shopEntity.getModel();
		AucEntityToShop entityToShop = model.removeShop(helper.getShopFromHand(player));

		if (entityToShop != null) {
			plugin.db().delete(entityToShop);

			// Remove an empty shop
			if (model.getEntityToShops().isEmpty()) {
				plugin.db().deleteShopEntity(shopEntity);
			} else {
				shopEntity.register();
			}

			return plugin.lang().success("command.detach.success");
		}

		return null;
	}

	@Override
	public String getName() {
		return "detach";
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
