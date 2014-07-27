package net.gasull.well.auction.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gasull.well.WellCore;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.AuctionPlayer;
import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.EntityShopEntity;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.db.WellDao;
import net.gasull.well.db.WellDatabase;
import net.gasull.well.db.WellDatabaseSimple;
import net.gasull.well.db.WellDbTableAlter;
import net.gasull.well.version.WellUpgrade;
import net.gasull.well.version.WellVersionable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.RawSql;

/**
 * The Class WellAuctionDao.
 */
public class WellAuctionDao extends WellDao implements WellVersionable {

	/** The plugin. */
	private final WellAuction plugin;

	/** The well database object. */
	private final WellDatabase wellDatabase;

	/** The actual database object. */
	private final EbeanServer db;

	/** The registered shops by type. */
	private Map<ItemStack, AuctionShop> shops = new HashMap<ItemStack, AuctionShop>();

	/** The shops by id. */
	private Map<Integer, AuctionShop> shopById = new HashMap<>();

	/** The post upgrades. */
	private List<Runnable> postUpgrades = new ArrayList<>();

	/**
	 * Instantiates a new well auction dao.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WellAuctionDao(WellAuction plugin) {
		this.plugin = plugin;
		this.wellDatabase = new WellDatabase(plugin) {
			protected java.util.List<Class<?>> getDatabaseClasses() {
				List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(AuctionShop.class);
				list.add(ShopEntityModel.class);
				list.add(AucEntityToShop.class);
				list.add(AuctionPlayer.class);
				list.add(AuctionSellerData.class);
				list.add(AuctionSale.class);
				return list;
			};
		};

		WellCore.checkVersion(this);
		wellDatabase.initializeIfNotInit(false);
		this.db = wellDatabase.getDatabase();

		for (Runnable postUpgrade : postUpgrades) {
			postUpgrade.run();
		}
	}

	@Override
	public List<WellUpgrade> getVersionChanges() {
		List<WellUpgrade> versions = new ArrayList<>();

		versions.add(new WellUpgrade(null) {
			@Override
			public void handleUpgrade() {
				wellDatabase.initializeDatabase(true);
			}
		});

		versions.add(new WellUpgrade("0.5.0") {
			@Override
			public void handleUpgrade() {
				plugin.config().getConfig().set("inventory.sell.size.default", 4);
				plugin.config().save();

				try (WellDatabaseSimple simpleDb = new WellDatabaseSimple(plugin, plugin.config().getConfig())) {

					new WellDbTableAlter(simpleDb.getConnection(), "well_auction_sale").addColumn("amount", Integer.class, null, 0)
							.addColumn("unit_price", Double.class, null, null).execute();

				} catch (SQLException | IOException e) {
					throw new RuntimeException("Couldn't add amount and unit_price columns", e);
				}

				postUpgrades.add(new Runnable() {
					@Override
					public void run() {
						List<AuctionSale> sales = db.find(AuctionSale.class).fetch("sellerData").findList();

						for (AuctionSale sale : sales) {
							sale.setAmount(sale.getItem().getAmount());

							if (sale.getPrice() == null) {
								Double defaultPrice = sale.getSellerData().getDefaultPrice();

								if (defaultPrice != null) {
									sale.setUnitPrice(defaultPrice);
								}
							} else {
								sale.setUnitPrice(sale.getPrice() / (double) sale.getAmount());
							}

						}

						db.save(sales);
					}
				});
			}
		});

		return versions;
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
				.setParameter("shopId", shop.getId()).fetch("auctionPlayer").findUnique();

		if (sellerData == null) {
			sellerData = new AuctionSellerData(shop, findAuctionPlayer(p));
			save(sellerData);
		} else {
			sellerData.setShop(shop);
		}

		return sellerData;
	}

	/**
	 * Find seller data.
	 * 
	 * @param aucPlayer
	 *            the auction player
	 * @param shops
	 *            the shops
	 * @return the map
	 */
	public Map<Integer, AuctionSellerData> mapShopsToSellerData(AuctionPlayer aucPlayer, Collection<AuctionShop> shops) {

		OfflinePlayer player = aucPlayer.getPlayer();
		Map<Integer, AuctionShop> missingShopIds = new HashMap<>();
		Map<Integer, AuctionSellerData> mapping = new HashMap<>();

		for (AuctionShop shop : shops) {
			missingShopIds.put(shop.getId(), shop);
		}

		List<AuctionSellerData> sellerDatas = db.find(AuctionSellerData.class).fetch("auctionPlayer").where().eq("auctionPlayer", aucPlayer).in("shop", shops)
				.findList();

		for (AuctionSellerData sellerData : sellerDatas) {
			int shopId = sellerData.getShop().getId();
			missingShopIds.remove(shopId);
			mapping.put(shopId, sellerData);
		}

		// Add, optionally create, missing data
		for (AuctionShop shop : missingShopIds.values()) {
			AuctionSellerData sellerData = findSellerData(player, shop);
			sellerDatas.add(sellerData);
			mapping.put(shop.getId(), sellerData);
		}

		return mapping;
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
	 * Shop is selling a specific sale.
	 * 
	 * @param shop
	 *            the shop
	 * @param sale
	 *            the sale
	 * @return true, if successful
	 */
	public boolean shopIsSelling(AuctionShop shop, AuctionSale sale) {
		return db.find(AuctionSale.class).where().eq("sellerData.shop", shop).eq("id", sale.getId()).findUnique() != null;
	}

	/**
	 * Find the best price by shop.
	 * 
	 * @param shop
	 *            the shop
	 * @return the auction sale
	 */
	public AuctionSale findBestSaleByShop(AuctionShop shop) {

		String bestPriceReq = String
				.format("select min(unit_price) bestPrice from well_auction_sale bs inner join well_auction_sellerData bsd on bsd.id = bs.seller_data_id where bsd.shop_id=%d",
						shop.getId());

		String sql = "from well_auction_sale s inner join (" + bestPriceReq + ") bpr on s.unit_price = bpr.bestPrice "
				+ "left outer join well_auction_sellerData sd on sd.id = s.seller_data_id ";

		RawSql rawSql = AuctionSale.mapRawSql("s").mapJoin("sellerData", AuctionSellerData.mapRawSql("sd")).create(sql);

		return refreshSale(db.find(AuctionSale.class).setRawSql(rawSql).where("sd.shop_id=:shop").setParameter("shop", shop.getId()).order().asc("created")
				.setMaxRows(1).findUnique());
	}

	/**
	 * Find best sale for amount.
	 * 
	 * @param shop
	 *            the shop
	 * @param amount
	 *            the amount
	 * @param stackSizes
	 *            the stack sizes
	 * @return the auction sale
	 */
	public AuctionSale findBestSaleForAmount(AuctionShop shop, int amount, List<Integer> stackSizes) {
		String amountCondition;

		if (amount > 0) {
			amountCondition = String.format("= %d", amount);
		} else {
			amountCondition = String.format("not in (%s)", StringUtils.join(stackSizes, ","));
		}

		String bestPriceReq = String
				.format("select bs.amount, min(unit_price) bestPrice from well_auction_sale bs inner join well_auction_sellerData bsd on bsd.id = bs.seller_data_id where bsd.shop_id=%d and bs.amount %s",
						shop.getId(), amountCondition);

		String sql = "from well_auction_sale s inner join (" + bestPriceReq + ") bpr on s.amount=bpr.amount and s.unit_price = bpr.bestPrice "
				+ "left outer join well_auction_sellerData sd on sd.id = s.seller_data_id  ";

		RawSql rawSql = AuctionSale.mapRawSql("s").mapJoin("sellerData", AuctionSellerData.mapRawSql("sd")).create(sql);

		return refreshSale(db.find(AuctionSale.class).setRawSql(rawSql).where(String.format("sd.shop_id=:shop and amount %s", amountCondition))
				.setParameter("shop", shop.getId()).order().asc("created").setMaxRows(1).findUnique());
	}

	/**
	 * Find best sales.
	 * 
	 * @param shop
	 *            the shop
	 * @param stackSizes
	 *            the stack sizes
	 * @return the list
	 */
	public List<AuctionSale> findBestSales(AuctionShop shop, List<Integer> stackSizes) {
		String bestPriceReq = String
				.format("select bs.amount, min(unit_price) bestPrice from well_auction_sale bs inner join well_auction_sellerData bsd on bsd.id = bs.seller_data_id where bsd.shop_id=%d group by bs.amount",
						shop.getId());

		String sql = "from well_auction_sale s inner join (" + bestPriceReq + ") bpr on s.amount=bpr.amount and s.unit_price = bpr.bestPrice "
				+ "left outer join well_auction_sellerData sd on sd.id = s.seller_data_id  ";

		RawSql rawSql = AuctionSale.mapRawSql("s").mapJoin("sellerData", AuctionSellerData.mapRawSql("sd")).create(sql);

		return refreshSales(db.find(AuctionSale.class).setRawSql(rawSql).where("sd.shop_id=:shop and amount in (:amount)").setParameter("shop", shop.getId())
				.setParameter("amount", stackSizes).order().asc("created").findList());
	}

	/**
	 * Gets a sale from a sale stack {@link ItemStack}.
	 * 
	 * @param theItem
	 *            the the item
	 * @return the sale
	 */
	public AuctionSale saleFromSaleStack(ItemStack theItem) {

		Integer saleId = AuctionSale.idFromTradeStack(theItem);

		if (saleId != null) {
			AuctionSale sale = db.find(AuctionSale.class).where("id=:id").setParameter("id", saleId).fetch("sellerData").fetch("sellerData.shop").findUnique();

			if (sale == null) {
				return null;
			}

			sale.getSellerData().setShop(shopById.get(sale.getSellerData().getShop().getId()));
			refreshSale(sale);
			return sale;
		}

		throw new IllegalArgumentException("Provided item isn't recognozed as an Auction Sale");
	}

	/**
	 * Refresh the displayed stack.
	 * 
	 * @param sale
	 *            the sale
	 * @return the auction sale
	 */
	public AuctionSale refreshSale(AuctionSale sale) {
		if (plugin == null || sale == null || sale.getSellerData() == null) {
			return null;
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
			desc.add(plugin.lang().get("shop.item.noPrice"));
		} else {
			desc.add(ChatColor.GREEN + plugin.economy().format(sale.getTradePrice()));

			String pricePerUnit = plugin.lang().get("shop.item.pricePerUnit");
			desc.add(ChatColor.DARK_GREEN
					+ pricePerUnit.replace("%price%", plugin.economy().format(sale.getTradePrice() / (double) sale.getItem().getAmount())));
		}

		String playerName = sale.getSellerData().getAuctionPlayer().getName();
		desc.add(ChatColor.BLUE + plugin.lang().get("shop.item.soldBy").replace("%player%", playerName == null ? "???" : playerName));

		meta.setLore(desc);
		tradeStack.setItemMeta(meta);
		sale.setTradeStack(tradeStack);
		return sale;
	}

	/**
	 * Refresh sales.
	 * 
	 * @param sales
	 *            the sales
	 * @return the list of sales
	 */
	public List<AuctionSale> refreshSales(List<AuctionSale> sales) {
		for (AuctionSale sale : sales) {
			refreshSale(sale);
		}

		return sales;
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
			save(shop);
			registerShop(shop);
			shops.put(refType, shop);
			return shop;
		} else {
			return singletonShop;
		}
	}

	/**
	 * Register shop for singleton fetching id-fetching.
	 * 
	 * @param shop
	 *            the shop
	 */
	public void registerShop(AuctionShop shop) {
		shops.put(shop.getRefItemCopy(), shop);
		shopById.put(shop.getId(), shop);
	}

	/**
	 * List shops.
	 * 
	 * @return the list
	 */
	public List<AuctionShop> listShops() {
		return db.find(AuctionShop.class).findList();
	}

	/**
	 * Get shops already loaded shops
	 * 
	 * @return the list
	 */
	public Collection<AuctionShop> getShops() {
		return shops.values();
	}

	/**
	 * List shop entities.
	 * 
	 * @return the list
	 */
	public List<ShopEntityModel> listShopEntities() {
		return db.find(ShopEntityModel.class).fetch("entityToShops").fetch("entityToShops.shop").order("id").findList();
	}

	/**
	 * Find similar shop entity.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 * @return the shop entity model
	 */
	public ShopEntityModel findSimilarShopEntity(ShopEntity shopEntity) {
		return db.find(shopEntity.getModel().getClass()).where().eq("data", shopEntity.getModel().getData()).findUnique();
	}

	/**
	 * Delete shop entity.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 */
	public void deleteShopEntity(ShopEntity shopEntity) {
		shopEntity.unregister();

		for (AucEntityToShop entityToShop : shopEntity.getModel().getEntityToShops()) {
			delete(entityToShop);
		}

		if (shopEntity instanceof EntityShopEntity) {
			EntityShopEntity casted = (EntityShopEntity) shopEntity;
			casted.getEntity().remove();
		}

		delete(shopEntity.getModel());
	}

	@Override
	public EbeanServer getDb() {
		return db;
	}

	@Override
	public JavaPlugin getPlugin() {
		return plugin;
	}
}
