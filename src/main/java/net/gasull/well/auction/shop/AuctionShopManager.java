package net.gasull.well.auction.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
	private Map<ItemStack, AuctionShop> shops = new HashMap<ItemStack, AuctionShop>();

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

		plugin.permission().can(player, "sell items", "well.auction.sell");
		AuctionShop shop = getShop(theItem);

		if (shop == null) {
			throw new AuctionShopException("No registered shop for item " + theItem);
		}

		AuctionPlayer seller = shop.getAuctionPlayer(player);
		return shop.sell(seller, theItem);
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

		plugin.permission().can(player, "buy items", "well.auction.buy");
		AuctionShop shop = getShop(saleStack);

		if (shop == null) {
			throw new AuctionShopException("No registered shop for type " + saleStack.getType());
		}

		AuctionSale sale = shop.saleForStack(saleStack);
		double money = plugin.economy().getBalance(player);

		// Double check to avoid systematic synchronized
		if (sale != null && money >= sale.getPrice()) {

			synchronized (sale) {

				sale = shop.saleForStack(saleStack);
				money = plugin.economy().getBalance(player);
				if (sale != null && money >= sale.getPrice()) {

					shop.buy(player, sale);
					ItemStack item = sale.getItem();

					// Notify both players
					OfflinePlayer seller = sale.getSeller().getPlayer();
					String priceStr = plugin.economy().format(sale.getPrice());
					player.sendMessage(ChatColor.DARK_GREEN
							+ MSG_BUY_NOTIFY.replace("%item%", item.toString()).replace("%player%", seller.getName()).replace("%price%", priceStr));

					if (seller.isOnline() && seller instanceof Player) {
						((Player) seller).sendMessage(ChatColor.BLUE
								+ MSG_SELL_NOTIFY.replace("%item%", item.toString()).replace("%player%", player.getName()).replace("%price%", priceStr));
					}

					plugin.economy().withdrawPlayer(player, sale.getPrice());
					plugin.economy().depositPlayer(seller, sale.getPrice());

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
	 * Gets the shop from a given item.
	 * 
	 * @param type
	 *            the auction type
	 * @return the shop if exists for the associated material, null otherwise.
	 */
	public AuctionShop getShop(ItemStack type) {
		ItemStack refType = AuctionShop.refItemFor(type);
		AuctionShop singletonShop = shops.get(refType);

		if (singletonShop == null) {
			AuctionShop shop = new AuctionShop(plugin, refType);
			shops.put(refType, shop);
			return shop;
		} else {
			return singletonShop;
		}
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
	 * Gets the shops.
	 * 
	 * @return the shops
	 */
	public Collection<AuctionShop> getShops() {
		return shops.values();
	}

	/**
	 * Registers a {@link ShopEntity} as a shop. Instantiates a new
	 * {@link AuctionShop} if none exist for shopEntity.
	 * 
	 * @param shop
	 *            the shop
	 * @param shopEntity
	 *            the shop entity
	 * @return the auction shop
	 */
	public AuctionShop registerEntityAsShop(AuctionShop shop, ShopEntity shopEntity) {
		shopsByLocation.put(shopEntity, shop);
		shop.registerEntity(plugin, shopEntity);
		return shop;
	}

	/**
	 * Register entity as shop.
	 * 
	 * @param refItem
	 *            the ref item
	 * @param shopEntity
	 *            the shop entity
	 * @return the auction shop
	 */
	public AuctionShop registerEntityAsShop(ItemStack refItem, ShopEntity shopEntity) {
		return registerEntityAsShop(getShop(refItem), shopEntity);
	}

	public void unregister(ShopEntity shopEntity) {
		shopEntity.unregister(plugin);
		shopsByLocation.remove(shopEntity);
	}

	/**
	 * Clean.
	 */
	public void clean() {
		for (ShopEntity shopEntity : shopsByLocation.keySet()) {
			unregister(shopEntity);
		}
	}
}
