package net.gasull.well.auction.command;

import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;

import org.bukkit.entity.Player;

/**
 * /wauc attach Attaches an held item to the targeted shop.
 */
public class WaucAttachCommand extends WellCommand<Player> {

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
	public WaucAttachCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		ShopEntity shopEntity = helper.getTargetShop(args, player);
		AuctionShop shop = helper.getShopFromHand(player);

		if (shopEntity.getModel().addShop(shop)) {
			shopEntity.register();
			plugin.db().save(shopEntity.getModel());
			plugin.db().save(shopEntity.getModel().getEntityToShops());

			return plugin.lang().success("command.creation.success").replace("%item%", shop.getRefItemCopy().toString());
		} else {
			throw new WellCommandException(plugin.lang().error("command.error.saleAlreadyHere").replace("%item%", shop.getRefItemCopy().toString()));
		}
	}

	@Override
	public String getName() {
		return "attach";
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
