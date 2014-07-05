package net.gasull.well.auction.shop;

import java.util.List;

import net.gasull.well.WellCore;
import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.AucShopEntityManager;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
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

	/** The shop entity manager. */
	private AucShopEntityManager shopEntityManager;

	/** The enabled, to avoid using this between reloads. */
	private boolean enabled = false;

	/** The max sale id. */
	private int maxSaleId = 0;

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
	 * @param shopEntityManager
	 *            the shop entity manager
	 */
	public AuctionShopManager(WellAuction plugin, AucShopEntityManager shopEntityManager) {
		this.plugin = plugin;
		this.shopEntityManager = shopEntityManager;

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

		WellCore.permission().can(player, "sell items", "well.auction.sell");
		checkEnabled(player);
		AuctionShop shop = plugin.db().getShop(theItem);

		if (shop == null) {
			throw new AuctionShopException("No registered shop for item " + theItem);
		}
		if (!shop.getStackSizes().contains(theItem.getAmount())) {
			String msg = plugin.wellConfig().getString("lang.sell.invalidStackSize", "You can't sell %amount% of this item. Valid amounts: %amounts%");
			msg = msg.replace("%amount%", String.valueOf(theItem.getAmount())).replace("%amounts%", StringUtils.join(shop.getStackSizes(), ", "));
			player.sendMessage(ChatColor.RED + msg);
			throw new AuctionShopException(String.format("To %s : %s", player.getName(), msg));
		}

		Transaction t = plugin.db().transaction();

		try {
			AuctionSellerData sellerData = plugin.db().findSellerData(player, shop);
			AuctionSale sale = new AuctionSale(++maxSaleId, plugin, sellerData, theItem);
			plugin.db().save(sale);
			refreshPrice(shop, sale);

			t.commit();
			return sale;
		} finally {
			t.end();
		}
	}

	/**
	 * Unsell.
	 * 
	 * @param player
	 *            the player
	 * @param theItem
	 *            the the item
	 * @return the item stack
	 * @throws AuctionShopException
	 *             the auction shop exception
	 * @throws WellPermissionException
	 *             the well permission exception
	 */
	public ItemStack unsell(Player player, ItemStack theItem) throws AuctionShopException, WellPermissionException {
		WellCore.permission().can(player, "sell items", "well.auction.sell");
		checkEnabled(player);
		AuctionShop shop = plugin.db().getShop(theItem);

		if (shop == null) {
			throw new AuctionShopException("No registered shop for item " + theItem);
		}

		AuctionSellerData sellerData = plugin.db().findSellerData(player, shop);
		AuctionSale sale = getSale(sellerData, theItem);

		// Lock the sale for the removal
		if (sale != null && sale.lock()) {
			Transaction t = plugin.db().transaction();

			try {
				ItemStack item = removeSale(shop, sale);
				sale.unlock();
				t.commit();
				return item;
			} finally {
				t.end();
			}
		}

		// Handle failure here
		String msg = MSG_BUY_SORRY.replace("%item%", theItem.toString());

		player.sendMessage(ChatColor.RED + msg);
		throw new AuctionShopException("To " + player.getName() + " : " + msg);
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

		WellCore.permission().can(player, "buy items", "well.auction.buy");
		checkEnabled(player);
		AuctionSale sale = plugin.db().saleFromSaleStack(saleStack);

		if (sale != null && sale.lock()) {

			double money = plugin.economy().getBalance(player);
			Double price = sale.getTradePrice();

			if (money >= sale.getTradePrice()) {
				Transaction t = plugin.db().transaction();

				try {
					ItemStack item = removeSale(sale.getShop(), sale);

					// Notify both players
					OfflinePlayer seller = sale.getSellerData().getAuctionPlayer().getPlayer();
					String priceStr = plugin.economy().format(price);
					player.sendMessage(ChatColor.DARK_GREEN
							+ MSG_BUY_NOTIFY.replace("%item%", item.toString()).replace("%player%", seller.getName()).replace("%price%", priceStr));

					if (seller.isOnline() && seller instanceof Player) {
						((Player) seller).sendMessage(ChatColor.BLUE
								+ MSG_SELL_NOTIFY.replace("%item%", item.toString()).replace("%player%", player.getName()).replace("%price%", priceStr));
					}

					plugin.economy().withdrawPlayer(player, price);
					plugin.economy().depositPlayer(seller, price);

					t.commit();
					return sale;
				} finally {
					t.end();
				}
			}

			sale.unlock();
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
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 * @param price
	 *            the price
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	public void changeSalePrice(Player player, AuctionSale sale, Double price) throws AuctionShopException {
		if (price < 0) {
			unsetSalePrice(player, sale);
		} else {
			checkEnabled(player);

			if (sale.lock()) {
				Transaction t = plugin.db().transaction();

				try {
					changePrice(sale, price);
					sale.unlock();
					sale.getSellerData()
							.getAuctionPlayer()
							.sendMessage(
									ChatColor.BLUE
											+ msgSetPriceSuccess.replace("%item%", sale.getItem().toString())
													.replace("%price%", plugin.economy().format(price)));
					t.commit();
				} finally {
					t.end();
				}
			}
		}
	}

	/**
	 * Unset sale price.
	 * 
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	public void unsetSalePrice(Player player, AuctionSale sale) throws AuctionShopException {
		checkEnabled(player);

		if (sale.lock()) {
			Transaction t = plugin.db().transaction();

			try {
				changePrice(sale, null);
				sale.unlock();
				sale.getSellerData().getAuctionPlayer().sendMessage(ChatColor.BLUE + msgSetPriceUnset.replace("%item%", sale.getItem().toString()));
				t.commit();
			} finally {
				t.end();
			}
		}
	}

	/**
	 * Change price. Not directly changing setter directly because of Avaje
	 * wrapping.
	 * 
	 * @param sale
	 *            the sale
	 * @param price
	 *            the price
	 */
	private void changePrice(AuctionSale sale, Double price) {
		sale.getSellerData().getShop().getSales().remove(sale);
		sale.setPrice(price);
		plugin.db().save(sale);

		refreshPrice(sale.getSellerData().getShop(), sale);
	}

	/**
	 * Sets the default price.
	 * 
	 * @param player
	 *            the player
	 * @param sellerData
	 *            the seller data
	 * @param price
	 *            the price
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	public void setDefaultPrice(Player player, AuctionSellerData sellerData, Double price) throws AuctionShopException {
		if (price < 0) {
			unsetDefaultPrice(player, sellerData);
		} else {
			setDefaultPrice(sellerData, price);
			sellerData.getAuctionPlayer().sendMessage(
					ChatColor.BLUE
							+ msgSetDefaultPriceSuccess.replace("%item%", sellerData.getShop().getRefItemCopy().toString()).replace("%price%",
									plugin.economy().format(price)));
		}
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
		sellerData.setDefaultPrice(price);
		plugin.db().save(sellerData);

		// Refresh the price of sales that depend on default price
		AuctionShop shop = sellerData.getShop();
		if (shop != null) {
			List<AuctionSale> sales = plugin.db().findSales(sellerData);
			for (AuctionSale sale : sales) {
				refreshPrice(shop, sale);
			}
		}
	}

	/**
	 * Unset default price.
	 * 
	 * @param player
	 *            the player
	 * @param sellerData
	 *            the seller data
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	public void unsetDefaultPrice(Player player, AuctionSellerData sellerData) throws AuctionShopException {
		checkEnabled(player);
		setDefaultPrice(sellerData, null);
		sellerData.getAuctionPlayer().sendMessage(ChatColor.BLUE + msgSetDefaultPriceUnset.replace("%item%", sellerData.getShop().getRefItemCopy().toString()));
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enable.
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Disable.
	 */
	public void disable() {
		enabled = false;
	}

	/**
	 * Check enabled.
	 * 
	 * @param player
	 *            the player
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	public void checkEnabled(Player player) throws AuctionShopException {
		if (!isEnabled()) {
			player.sendMessage(ChatColor.RED + plugin.wellConfig().getString("lang.db.error.sync", "Sorry, Auction Houses are syncing. Please try again!"));
			throw new AuctionShopException("");
		}
	}

	/**
	 * Gets the sale.
	 * 
	 * @param sellerData
	 *            the seller data
	 * @param theItem
	 *            the the item
	 * @return the sale
	 */
	public AuctionSale getSale(AuctionSellerData sellerData, ItemStack theItem) {
		List<AuctionSale> sales = plugin.db().findSales(sellerData);
		for (AuctionSale sale : sales) {
			if (sale.isSellingStack(theItem)) {
				return sale;
			}
		}
		return null;
	}

	/**
	 * Refresh price.
	 * 
	 * @param sale
	 *            the sale
	 */
	public void refreshPrice(AuctionShop shop, AuctionSale sale) {
		plugin.db().refreshSale(sale);

		Double price = sale.getTradePrice();

		if (price != null && price >= 0) {
			if (!shop.getSales().contains(sale)) {
				shop.getSales().add(sale);
			}
		} else if (shop.getSales().contains(sale)) {
			shop.getSales().remove(sale);
		}
	}

	/**
	 * Removes the sale.
	 * 
	 * @param shop
	 *            the shop
	 * @param sale
	 *            the sale
	 * @return the item stack
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	private ItemStack removeSale(AuctionShop shop, AuctionSale sale) throws AuctionShopException {

		if (!shop.getSales().remove(sale)) {
			throw new AuctionShopException("Sale not found but should have been");
		}

		plugin.db().delete(sale);
		return sale.getItem();
	}

	/**
	 * Load the shop manager from DB.
	 */
	public void load() {
		List<AuctionShop> dbShops = plugin.db().listShops();

		for (AuctionShop shop : dbShops) {
			shop.setup(plugin);
			plugin.db().registerShop(shop);

			List<AuctionSale> sales = plugin.db().findSales(shop);
			for (AuctionSale sale : sales) {
				refreshPrice(shop, sale);
			}

		}

		List<ShopEntityModel> registered = plugin.db().listShopEntities();

		for (ShopEntityModel shopEntityModel : registered) {
			shopEntityManager.get(shopEntityModel).register();
		}

		AuctionSale lastSale = plugin.getDatabase().find(AuctionSale.class).order("id desc").setMaxRows(1).findUnique();

		if (lastSale != null) {
			maxSaleId = lastSale.getId();
		}
	}
}
