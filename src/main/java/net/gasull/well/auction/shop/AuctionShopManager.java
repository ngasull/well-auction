package net.gasull.well.auction.shop;

import java.util.HashMap;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.WellPermissionManager.WellPermissionException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The AuctionShop manager.
 */
public class AuctionShopManager {

	/** The plugin. */
	private WellAuction plugin;

	/** The registered shops by location. */
	private Map<ShopEntity, AuctionShop> shopsByLocation = new HashMap<ShopEntity, AuctionShop>();

	/** The registered shops by type. */
	private Map<AuctionType, AuctionShop> shops = new HashMap<AuctionType, AuctionShop>();

	/** The sell notification message. */
	private final String MSG_SELL_NOTIFY;

	/** The buy notification message. */
	private final String MSG_BUY_NOTIFY;

	/** The buy apologizing message. */
	private final String MSG_BUY_SORRY;

	/** The buy not enough money message. */
	private final String MSG_BUY_NO_MONEY;

	/**
	 * Instantiates a new auction shop manager.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public AuctionShopManager(WellAuction plugin) {
		this.plugin = plugin;

		this.MSG_SELL_NOTIFY = plugin.wellConfig().getString("lang.sell.notification", "You've just sold %item% to %player% for %price%");
		this.MSG_BUY_NOTIFY = plugin.wellConfig().getString("lang.buy.notification", "You've just bought %item% to %player% for %price%");
		this.MSG_BUY_SORRY = plugin.wellConfig().getString("lang.buy.sorry", "Sorry, %item% is not available in the shop any more..!");
		this.MSG_BUY_NO_MONEY = plugin.wellConfig().getString("lang.buy.noMoney", "You don't have enough money to buy %item%");
	}

	/**
	 * Sell.
	 * 
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return the auction sale
	 * @throws AuctionShopException
	 *             the auction shop exception
	 * @throws WellPermissionException
	 *             the well permission exception
	 */
	public AuctionSale sell(Player player, ItemStack theItem) throws AuctionShopException, WellPermissionException {

		plugin.permission.can("sell items", player, "well.auction.sell");

		AuctionType type = AuctionType.get(theItem);
		AuctionShop shop = shops.get(type);

		if (shop == null) {
			throw new AuctionShopException("No registered shop for type " + type);
		}

		// TODO Fetch amounts
		return shop.sell(player, theItem, 200);
	}

	/**
	 * Buy.
	 * 
	 * @param player
	 *            the player
	 * @param saleStack
	 *            the {@link ItemStack} on sale
	 * @return the sale
	 * @throws AuctionShopException
	 *             the auction shop exception
	 * @throws WellPermissionException
	 *             the well permission exception
	 */
	public AuctionSale buy(Player player, ItemStack saleStack) throws AuctionShopException, WellPermissionException {

		plugin.permission.can("buy items", player, "well.auction.buy");
		AuctionShop shop = shops.get(AuctionType.get(saleStack));

		if (shop == null) {
			throw new AuctionShopException("No registered shop for type " + saleStack.getType());
		}

		AuctionSale sale = shop.saleForStack(saleStack);
		double money = plugin.economy.getBalance(player.getName());

		// Double check to avoid systematic synchronized
		if (sale != null && money >= sale.getPrice()) {

			synchronized (sale) {

				sale = shop.saleForStack(saleStack);
				money = plugin.economy.getBalance(player.getName());
				if (sale != null && money >= sale.getPrice()) {

					shop.buy(player, sale);
					ItemStack item = sale.getItem();

					// Notify both players
					player.sendMessage(ChatColor.DARK_GREEN
							+ MSG_BUY_NOTIFY.replace("%item%", item.toString()).replace("%player%", sale.getSeller())
									.replace("%price%", String.valueOf(sale.getPrice())));
					player.sendMessage(ChatColor.BLUE
							+ MSG_SELL_NOTIFY.replace("%item%", item.toString()).replace("%player%", sale.getSeller())
									.replace("%price%", String.valueOf(sale.getPrice())));

					plugin.economy.withdrawPlayer(player.getName(), sale.getPrice());
					plugin.economy.depositPlayer(sale.getSeller(), sale.getPrice());

					return sale;
				}
			}
		}

		// Handle failure here
		String msg;
		if (sale == null) {
			msg = MSG_BUY_SORRY.replace("%item%", saleStack.toString());
		} else {
			msg = MSG_BUY_NO_MONEY.replace("%item%", saleStack.toString());
		}

		player.sendMessage(ChatColor.RED + msg);
		throw new AuctionShopException("To " + player.getName() + " : " + msg);
	}

	/**
	 * Gets the shop for a given {@link AuctionType}.
	 * 
	 * @param type
	 *            the auction type
	 * @return the shop if exists for the associated material, null otherwise.
	 */
	public AuctionShop getShop(AuctionType type) {
		return shops.get(type);
	}

	/**
	 * Gets the shop for a given {@link Location}.
	 * 
	 * @param location
	 *            the location
	 * @return the shop if exists for the associated location, null otherwise.
	 */
	public AuctionShop getShop(Location location) {
		return shopsByLocation.get(location);
	}

	/**
	 * Registers a {@link ShopEntity} as a shop. Instantiates a new
	 * {@link AuctionShop} if none exist for shopEntity.
	 * 
	 * @param type
	 *            the type
	 * @param shopEntity
	 *            the shop entity
	 * @return the auction shop
	 */
	public AuctionShop registerEntityAsShop(AuctionType type, ShopEntity shopEntity) {
		AuctionShop shop = shopsByLocation.get(shopEntity);

		if (shop == null) {
			shop = new AuctionShop(type);
			shops.put(type, shop);
			shopsByLocation.put(shopEntity, shop);
		}

		shop.registerEntity(shopEntity);
		return shop;
	}
}
