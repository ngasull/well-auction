package net.gasull.well.auction.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.db.ShopEntityModel;
import net.gasull.well.auction.shop.entity.BlockShopEntity;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.Transaction;

/**
 * The AuctionShop manager.
 */
public class AuctionShopManager {

	/** The plugin. */
	private WellAuction plugin;

	/** The last save ok. */
	private boolean lastSaveOk = true;

	/** The registered shops by location. */
	private Map<ShopEntity, AuctionShop> shopsByLocation = new HashMap<ShopEntity, AuctionShop>();

	/** The registered shops by type. */
	private Map<ItemStack, AuctionShop> shops = new HashMap<ItemStack, AuctionShop>();

	/** The auction player. */
	private final Map<UUID, AuctionPlayer> auctionPlayers = new HashMap<>();

	/** The sell notification message. */
	private final String MSG_SELL_NOTIFY;

	/** The buy notification message. */
	private final String MSG_BUY_NOTIFY;

	/** The buy apologizing message. */
	private final String MSG_BUY_SORRY;

	/** The buy not enough money message. */
	private final String MSG_BUY_NO_MONEY;

	/** The message for set price success. */
	private final String msgSetPriceSuccess;

	/** The message for price unset. */
	private final String msgSetPriceUnset;

	/** The message for set default price success. */
	private final String msgSetDefaultPriceSuccess;

	/** The message for default price unset. */
	private final String msgSetDefaultPriceUnset;

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

		this.msgSetPriceSuccess = plugin.wellConfig().getString("lang.player.setPrice.success", "You're now selling %item% at %price%");
		this.msgSetPriceUnset = plugin.wellConfig().getString("lang.player.setPrice.unset", "You've unset the price of %item%");
		this.msgSetDefaultPriceSuccess = plugin.wellConfig().getString("lang.player.setPrice.success", "You're now selling %item% at %price% by default");
		this.msgSetDefaultPriceUnset = plugin.wellConfig().getString("lang.player.setPrice.unset", "You've unset the default price of %item%");
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

		AuctionSale sale = new AuctionSale(plugin, getAuctionPlayer(player).getSellerData(shop), theItem);
		shop.refreshPrice(sale);

		return sale;
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

					if (!shop.getSales().remove(sale)) {
						throw new AuctionShopException("Sale not found but should have been");
					}

					sale.getSeller().getSales(shop).remove(sale);
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
	 * Change sale price.
	 * 
	 * @param sale
	 *            the sale
	 * @param price
	 *            the price
	 */
	public void changeSalePrice(AuctionSale sale, Double price) {
		if (price < 0) {
			unsetSalePrice(sale);
		} else {
			sale.setPrice(price);
			sale.getSeller().sendMessage(
					ChatColor.BLUE + msgSetPriceSuccess.replace("%item%", sale.getItem().toString()).replace("%price%", plugin.economy().format(price)));
		}
	}

	/**
	 * Unset sale price.
	 * 
	 * @param sale
	 *            the sale
	 */
	public void unsetSalePrice(AuctionSale sale) {
		sale.setPrice(null);
		sale.getSeller().sendMessage(ChatColor.BLUE + msgSetPriceUnset.replace("%item%", sale.getItem().toString()));
	}

	/**
	 * Sets the default price.
	 * 
	 * @param sellerData
	 *            the seller data
	 * @param price
	 *            the price
	 */
	public void setDefaultPrice(AuctionSellerData sellerData, Double price) {
		if (price < 0) {
			unsetDefaultPrice(sellerData);
		} else {
			sellerData.setDefaultPrice(price);
			sellerData.getAuctionPlayer().sendMessage(
					ChatColor.BLUE
							+ msgSetDefaultPriceSuccess.replace("%item%", sellerData.getShop().getRefItem().toString()).replace("%price%",
									plugin.economy().format(price)));
		}
	}

	/**
	 * Unset default price.
	 * 
	 * @param sellerData
	 *            the seller data
	 */
	public void unsetDefaultPrice(AuctionSellerData sellerData) {
		sellerData.setDefaultPrice(null);
		sellerData.getAuctionPlayer().sendMessage(ChatColor.BLUE + msgSetDefaultPriceUnset.replace("%item%", sellerData.getShop().getRefItem().toString()));
	}

	/**
	 * Checks if is last save ok.
	 * 
	 * @return true, if is last save ok
	 */
	public boolean isLastSaveOk() {
		return lastSaveOk;
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
	 * Gets the auction player, creating it if unknown.
	 * 
	 * @param player
	 *            the player
	 * @return the auction player
	 */
	public AuctionPlayer getAuctionPlayer(OfflinePlayer player) {
		AuctionPlayer auctionPlayer = auctionPlayers.get(player.getUniqueId());

		if (auctionPlayer == null) {
			auctionPlayer = new AuctionPlayer(player);
			auctionPlayers.put(player.getUniqueId(), auctionPlayer);
		}

		return auctionPlayer;
	}

	/**
	 * Gets the seller data.
	 * 
	 * @param player
	 *            the player
	 * @param refItem
	 *            the ref item
	 * @return the seller data
	 */
	public AuctionSellerData getSellerData(Player player, ItemStack refItem) {
		return getAuctionPlayer(player).getSellerData(getShop(refItem));
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
		shopEntity.register(plugin, shop);
		shop.getRegistered().add(shopEntity);
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
			shopEntity.unregister(plugin);
		}
		shopsByLocation.clear();
	}

	/**
	 * Load the shop manager from DB.
	 */
	public void load() {
		List<AuctionPlayer> players = plugin.getDatabase().find(AuctionPlayer.class).findList();
		List<AuctionShop> dbShops = plugin.getDatabase().find(AuctionShop.class).findList();
		Map<Integer, AuctionShop> shopById = new HashMap<>();

		for (AuctionPlayer player : players) {
			this.auctionPlayers.put(player.getPlayerId(), player);
		}

		for (AuctionShop shop : dbShops) {
			shopById.put(shop.getId(), shop);
			shop.setPlugin(plugin);
			shops.put(shop.getRefItem(), shop);

			List<ShopEntityModel> registered = plugin.getDatabase().find(ShopEntityModel.class).where().eq("shopId", shop.getId()).findList();

			for (ShopEntityModel shopEntityModel : registered) {
				shopEntityModel.setShop(shop);
				ShopEntity shopEntity;

				switch (shopEntityModel.getType()) {
				case "block":
					shopEntity = new BlockShopEntity(shopEntityModel);
					break;
				default:
					continue;
				}

				shopEntity.register(plugin, shop);
				shop.getRegistered().add(shopEntity);
				shopsByLocation.put(shopEntity, shop);
			}

			List<AuctionSellerData> sellerData = plugin.getDatabase().find(AuctionSellerData.class).where().eq("shopId", shop.getId()).findList();

			for (AuctionSellerData data : sellerData) {
				shop.getSellerData().add(data);

				data.setShop(shop);
				data.setAuctionPlayer(auctionPlayers.get(data.getAuctionPlayerId()));

				AuctionPlayer player = data.getAuctionPlayer();
				player.getSellerData().add(data);
				this.auctionPlayers.put(player.getPlayerId(), player);

				List<AuctionSale> sales = plugin.getDatabase().find(AuctionSale.class).where().eq("sellerDataId", data.getId()).findList();

				for (AuctionSale sale : sales) {
					data.getSales().add(sale);

					sale.setPlugin(plugin);
					sale.setSellerData(data);

					shop.refreshPrice(sale);
				}
			}
		}

	}

	/**
	 * Save the shop manager to DB.
	 */
	public void save() {
		lastSaveOk = false;
		Transaction t = plugin.getDatabase().beginTransaction();

		for (AuctionPlayer player : auctionPlayers.values()) {
			plugin.getDatabase().save(player);
		}

		for (AuctionShop shop : shops.values()) {
			plugin.getDatabase().save(shop);

			for (ShopEntity shopEntity : shop.getRegistered()) {
				shopEntity.getModel().setShopId(shop.getId());
				plugin.getDatabase().save(shopEntity.getModel());
			}

			for (AuctionPlayer player : auctionPlayers.values()) {
				AuctionSellerData sellerData = player.getSellerData(shop);
				sellerData.setShopId(shop.getId());
				plugin.getDatabase().save(sellerData);

				for (AuctionSale sale : player.getSales(shop)) {
					sale.setSellerDataId(sellerData.getId());
					plugin.getDatabase().save(sale);
				}
			}
		}

		t.commit();
		lastSaveOk = true;
	}
}
