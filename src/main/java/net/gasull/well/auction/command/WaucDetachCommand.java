package net.gasull.well.auction.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;

/**
 * /wauc detach
 */
public class WaucDetachCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/** Shop detach success message. */
	private final String SUCC_DETACH;

	public WaucDetachCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
		this.SUCC_DETACH = ChatColor.GREEN + plugin.wellConfig().getString("lang.command.detach.success", "Successfully detached an item");
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
				shopEntity.unregister();
				plugin.db().delete(model);
			} else {
				shopEntity.register();
			}

			return SUCC_DETACH;
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
