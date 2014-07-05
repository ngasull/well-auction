package net.gasull.well.auction.command;

import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /wauc attach Attaches an held item to the targeted shop.
 */
public class WaucAttachCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/** "sale already at this place" message */
	private final String ERR_SALE_ALREADY_HERE;

	/** Shop creation success message. */
	private final String SUCC_ATTACH;

	/**
	 * Instantiates a new wauc attach command.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WaucAttachCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;

		this.ERR_SALE_ALREADY_HERE = ChatColor.DARK_RED
				+ plugin.wellConfig().getString("lang.command.error.saleAlreadyHere", "This is already being on sale here");
		this.SUCC_ATTACH = ChatColor.GREEN + plugin.wellConfig().getString("lang.command.creation.success", "Successfully attached for sale %item%");
	}

	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		ShopEntity shopEntity = helper.getTargetShop(args, player);
		AuctionShop shop = helper.getShopFromHand(player);

		if (shopEntity.getModel().addShop(shop)) {
			shopEntity.register();
			plugin.db().save(shopEntity.getModel());
			plugin.db().save(shopEntity.getModel().getEntityToShops());

			return SUCC_ATTACH.replace("%item%", shop.getRefItemCopy().toString());
		} else {
			throw new WellCommandException(ERR_SALE_ALREADY_HERE.replace("%item%", shop.getRefItemCopy().toString()));
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
