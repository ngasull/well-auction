package net.gasull.well.auction.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionPlayer;
import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.avaje.ebean.EbeanServer;

/**
 * The Class WellAuctionDao.
 */
public class WellAuctionDao {

	/** The plugin. */
	private final WellAuction plugin;

	/** The actual db object. */
	private final EbeanServer db;

	/** The shops by id. */
	private Map<Integer, AuctionShop> shopById = new HashMap<>();

	/** The Constant SALE_ID_PATTERN. */
	private static final Pattern SALE_ID_PATTERN = Pattern.compile(String.format("%s([0-9]+)", AuctionSale.N));

	/**
	 * Instantiates a new well auction dao.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WellAuctionDao(WellAuction plugin) {
		this.plugin = plugin;
		this.db = plugin.getDatabase();
	}

	/**
	 * Save.
	 * 
	 * @param model
	 *            the model
	 */
	public void save(Object model) {
		this.db.save(model);
	}

	/**
	 * Delete.
	 * 
	 * @param model
	 *            the model
	 */
	public void delete(Object model) {
		this.db.delete(model);
	}

	/**
	 * Find auction player.
	 * 
	 * @param p
	 *            the player
	 * @return the auction player
	 */
	public AuctionPlayer findAuctionPlayer(OfflinePlayer p) {
		AuctionPlayer ap = db.find(AuctionPlayer.class).where("playerId=:uuid").setParameter("uuid", p.getUniqueId()).findUnique();

		if (ap == null) {
			ap = new AuctionPlayer(p);
			save(ap);
		}

		return ap;
	}

	/**
	 * Find seller data.
	 * 
	 * @param p
	 *            the player
	 * @param shop
	 *            the shop
	 * @return the auction seller data
	 */
	public AuctionSellerData findSellerData(OfflinePlayer p, AuctionShop shop) {
		AuctionSellerData sellerData = db.find(AuctionSellerData.class).where("auctionPlayer=:uuid and shop=:shopId").setParameter("uuid", p.getUniqueId())
				.setParameter("shopId", shop.getId()).findUnique();

		if (sellerData == null) {
			sellerData = new AuctionSellerData(shop, findAuctionPlayer(p));
			save(sellerData);
		} else {
			sellerData.setShop(shop);
		}

		return sellerData;
	}

	/**
	 * Find sales.
	 * 
	 * @param shop
	 *            the shop
	 * @return the list
	 */
	public List<AuctionSale> findSales(AuctionShop shop) {
		List<AuctionSale> sales = db.find(AuctionSale.class).where().eq("sellerData.shop", shop).findList();

		for (AuctionSale sale : sales) {
			sale.getSellerData().setShop(shop);
			refreshSale(sale);
		}

		return sales;
	}

	/**
	 * Find sales.
	 * 
	 * @param sellerData
	 *            the seller data
	 * @return the list
	 */
	public List<AuctionSale> findSales(AuctionSellerData sellerData) {
		List<AuctionSale> sales = db.find(AuctionSale.class).where().eq("sellerData", sellerData).findList();

		for (AuctionSale sale : sales) {
			AuctionShop shop = shopById.get(sale.getShop().getId());
			sale.getSellerData().setShop(shop);
			refreshSale(sale);
		}

		return sales;
	}

	/**
	 * Gets the sales of.
	 * 
	 * @param shop
	 *            the shop
	 * @param p
	 *            the player
	 * @return the sales of
	 */
	public List<AuctionSale> getSalesOf(AuctionShop shop, OfflinePlayer p) {
		List<AuctionSale> sales = db.find(AuctionSale.class).where("sellerData.auctionPlayer=:playerId and sellerData.shop=:shop")
				.setParameter("playerId", p.getUniqueId()).setParameter("shop", shop.getId()).findList();

		for (AuctionSale sale : sales) {
			sale.getSellerData().setShop(shop);
			refreshSale(sale);
		}

		return sales;
	}

	/**
	 * Gets the sale.
	 * 
	 * @param theItem
	 *            the the item
	 * @return the sale
	 */
	public AuctionSale getSale(ItemStack theItem) {
		if (theItem.hasItemMeta() && theItem.getItemMeta().getLore().size() > 0) {
			String idString = theItem.getItemMeta().getLore().get(0);

			Matcher m = SALE_ID_PATTERN.matcher(idString);
			if (m.find()) {
				Integer saleId = Integer.valueOf(m.group(1));
				AuctionSale sale = db.find(AuctionSale.class).where("id=:id").setParameter("id", saleId).fetch("sellerData").fetch("sellerData.shop")
						.findUnique();

				sale.getSellerData().setShop(shopById.get(sale.getSellerData().getShop().getId()));
				refreshSale(sale);
				return sale;
			}
		}

		throw new IllegalArgumentException("Provided item isn't recognozed as an Auction Sale");
	}

	/**
	 * Refresh the displayed stack.
	 * 
	 * @param sale
	 *            the sale
	 */
	public void refreshSale(AuctionSale sale) {
		if (plugin == null || sale.getSellerData() == null) {
			return;
		}

		ItemStack tradeStack = new ItemStack(sale.getItem());
		ItemMeta realMeta = tradeStack.getItemMeta();
		ItemMeta meta = sale.getItem().getItemMeta();

		if (!tradeStack.hasItemMeta()) {
			meta = Bukkit.getItemFactory().getItemMeta(tradeStack.getType());
		}

		List<String> desc;
		if (tradeStack.hasItemMeta() && realMeta.getLore() != null && !realMeta.getLore().isEmpty()) {
			desc = realMeta.getLore();
			desc.add(AuctionSale.LORE_SEPARATOR);
		} else {
			desc = new ArrayList<>();
		}

		desc.add(ChatColor.DARK_GRAY + AuctionSale.N + sale.getId());

		if (sale.getTradePrice() == null) {
			desc.add(plugin.wellConfig().getString("lang.shop.item.noPrice", "No price set up yet!"));
		} else {
			desc.add(ChatColor.GREEN + plugin.economy().format(sale.getTradePrice()));

			String pricePerUnit = plugin.wellConfig().getString("lang.shop.item.pricePerUnit", "%price% p.u.");
			desc.add(ChatColor.DARK_GREEN
					+ pricePerUnit.replace("%price%", plugin.economy().format(sale.getTradePrice() / (double) sale.getItem().getAmount())));
		}

		String playerName = sale.getSellerData().getAuctionPlayer().getName();
		desc.add(ChatColor.BLUE
				+ plugin.wellConfig().getString("lang.shop.item.soldBy", "Sold by %player%").replace("%player%", playerName == null ? "???" : playerName));

		meta.setLore(desc);
		tradeStack.setItemMeta(meta);
		sale.setTradeStack(tradeStack);
	}

	/**
	 * Gets the shop.
	 * 
	 * @param id
	 *            the id
	 * @return the shop
	 */
	public AuctionShop getShop(Integer id) {
		return shopById.get(id);
	}

	/**
	 * Register shop for singleton fetching id-fetching.
	 * 
	 * @param shop
	 *            the shop
	 */
	public void registerShop(AuctionShop shop) {
		shopById.put(shop.getId(), shop);
	}

	/**
	 * Find shop entities.
	 * 
	 * @param shop
	 *            the shop
	 * @return the list
	 */
	public List<ShopEntityModel> findShopEntities(AuctionShop shop) {
		return db.find(ShopEntityModel.class).fetch("shop").where().eq("shop", shop).findList();
	}

	/**
	 * List shops.
	 * 
	 * @return the list
	 */
	public List<AuctionShop> listShops() {
		return db.find(AuctionShop.class).findList();
	}
}
