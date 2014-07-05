package net.gasull.well.auction.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionPlayer;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * Represents the menu to access Sell & Buy inventories.
 */
public class AuctionMenu {

	/** The plugin. */
	private final WellAuction plugin;

	/** The shop entity. */
	private final ShopEntity shopEntity;

	/** The sell button. */
	private final ItemStack sellButton;

	/** The buy button. */
	private final ItemStack buyButton;

	/** The info. */
	private final ItemStack info;

	/** The info to display in chat (for multi shops). */
	private final String[] chatInfo;

	/**
	 * The last help display cache to avoid player chat spam. Mapped to UUID,
	 * but could have been any non null value.
	 */
	private final Cache<UUID, UUID> lastHelpCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).build(new CacheLoader<UUID, UUID>() {
		@Override
		public UUID load(UUID arg0) throws Exception {
			return arg0;
		}
	});

	/** The msg sale no price. */
	private final String msgSaleNoPrice;

	/** The msg sale price. */
	private final String msgSalePrice;

	/** The msg best sale price. */
	private final String msgBestSalePrice;

	/** Binds inventory slots to shops for multiple type. */
	private AuctionShop[] shopSlots;

	/** The Constant INFO_SLOT. */
	private static final int INFO_SLOT = 13;

	/** The Constant BUY_SLOT. */
	private static final int BUY_SLOT = 14;

	/** The Constant SALE_SLOT. */
	public static final int SALE_SLOT = 12;

	/** The Constant REFITEM_SLOT. */
	public static final int REFITEM_SLOT = 1;

	/** The size of the menu inventory. */
	private static final int MENU_SIZE = 27;

	/**
	 * Instantiates a new auction menu.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shopEntity
	 *            the shop entity
	 */
	public AuctionMenu(WellAuction plugin, ShopEntity shopEntity) {
		this.plugin = plugin;
		this.shopEntity = shopEntity;

		sellButton = new ItemStack(Material.matchMaterial(plugin.wellConfig().getString("inventory.menu.button.sell.item", Material.GOLD_INGOT.name())));
		buyButton = new ItemStack(Material.matchMaterial(plugin.wellConfig().getString("inventory.menu.button.buy.item", Material.EMERALD.name())));
		info = new ItemStack(Material.matchMaterial(plugin.wellConfig().getString("inventory.menu.button.info.item", Material.PAPER.name())));

		ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(sellButton.getType());
		itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.sell.title", "Sell"));
		sellButton.setItemMeta(itemMeta);

		itemMeta = Bukkit.getItemFactory().getItemMeta(buyButton.getType());
		itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.buy.title", "Buy"));
		buyButton.setItemMeta(itemMeta);

		msgSaleNoPrice = plugin.wellConfig().getString("lang.inventory.menu.button.sell.noPrice", "No price set up yet!");
		msgSalePrice = plugin.wellConfig().getString("lang.inventory.menu.button.sell.price", "Your price: %price% p.u.");
		msgBestSalePrice = plugin.wellConfig().getString("lang.inventory.menu.button.buy.bestSale", "Best price: %price% p.u");

		List<String> defaultInfo = new ArrayList<>();
		defaultInfo.add(ChatColor.AQUA + "Welcome to the Auction House!");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "Either left-click to buy items");
		defaultInfo.add(ChatColor.AQUA + "or right-click to sell items.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "You can setup default sell price");
		defaultInfo.add(ChatColor.AQUA + "per unit by shift-clicking a category.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "Shift-clicking also works for");
		defaultInfo.add(ChatColor.AQUA + "individual sales.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "You can remove your sales by");
		defaultInfo.add(ChatColor.AQUA + "right-clicking them.");

		itemMeta = Bukkit.getItemFactory().getItemMeta(info.getType());
		itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.info.title", "Welcome to the Auction House!"));
		itemMeta.setLore(plugin.wellConfig().getStringList("lang.inventory.menu.button.info.desc", defaultInfo));
		info.setItemMeta(itemMeta);

		defaultInfo = new ArrayList<>();
		defaultInfo.add(ChatColor.AQUA + "Welcome to the Auction House!");
		defaultInfo.add(ChatColor.AQUA + "Either left-click to buy items or right-click to sell items.");
		defaultInfo.add(ChatColor.AQUA + "Setup default sell price per unit by shift-clicking a category.");
		defaultInfo.add(ChatColor.AQUA + "Shift-clicking also works for individual sales.");
		defaultInfo.add(ChatColor.AQUA + "You can remove your sales by right-clicking them.");
		List<String> stringList = plugin.wellConfig().getStringList("lang.inventory.menu.button.info.chatCesc", defaultInfo);
		chatInfo = stringList.toArray(new String[stringList.size()]);
	}

	/**
	 * Gets the menu for type.
	 * 
	 * @param player
	 *            the player
	 */
	public void open(Player player) {

		Collection<AuctionShop> shops = shopEntity.getShops();
		Inventory inv;

		if (isSingle()) {
			inv = createInventorySingle(shops.iterator().next(), player);
		} else {
			inv = createInventoryMulti(shops, player);
		}

		player.openInventory(inv);
	}

	/**
	 * Creates the inventory single-item shop.
	 * 
	 * @param shop
	 *            the shop
	 * @param player
	 *            the player
	 * @return the inventory
	 */
	private Inventory createInventorySingle(AuctionShop shop, Player player) {
		AuctionSellerData sellerData = plugin.db().findSellerData(player, shop);
		Double bestPrice = shop.getBestPrice();
		ItemStack[] contents = new ItemStack[MENU_SIZE];

		for (int i = 0; i < MENU_SIZE; i++) {
			if (isSaleSlot(i)) {
				contents[i] = new ItemStack(sellButton);
				ItemMeta itemMeta = contents[i].getItemMeta();
				List<String> desc = new ArrayList<>();

				if (sellerData.getDefaultPrice() == null) {
					desc.add(ChatColor.YELLOW + msgSaleNoPrice);
				} else {
					;
					desc.add(ChatColor.GREEN + msgSalePrice.replace("%price%", plugin.economy().format(sellerData.getDefaultPrice())));
				}

				itemMeta.setLore(desc);
				contents[i].setItemMeta(itemMeta);
			} else if (shopSlot(i) != null) {
				contents[i] = new ItemStack(buyButton);
				ItemMeta itemMeta = contents[i].getItemMeta();
				List<String> desc = new ArrayList<>();

				if (bestPrice != null) {
					desc.add(ChatColor.YELLOW + msgBestSalePrice.replace("%price%", plugin.economy().format(bestPrice)));
					itemMeta.setLore(desc);
				}

				itemMeta.setLore(desc);
				contents[i].setItemMeta(itemMeta);
			} else if (isInfoSlot(i)) {
				contents[i] = info;
			} else {
				switch (i) {
				case REFITEM_SLOT:
				case 7:
				case 9:
				case 17:
				case 19:
				case 25:
					contents[i] = new ItemStack(shop.getRefItemCopy());
					break;
				}
			}
		}

		Inventory inv = Bukkit.createInventory(player, AuctionMenu.MENU_SIZE, plugin.wellConfig().getString("inventory.menu.title"));
		inv.setContents(contents);
		return inv;
	}

	/**
	 * Creates the inventory multi.
	 * 
	 * @param shops
	 *            the shops
	 * @param player
	 *            the player
	 * @return the inventory
	 */
	private Inventory createInventoryMulti(Collection<AuctionShop> shops, Player player) {

		final int menuSize = (((int) (shops.size() / 9)) + 1) * 9;
		AuctionShop[] lastShopSlots = new AuctionShop[6 * 9];
		AuctionPlayer aucPlayer = plugin.db().findAuctionPlayer(player);
		Map<Integer, AuctionSellerData> sellerDataMap = plugin.db().mapShopsToSellerData(aucPlayer, shops);

		ItemStack[] contents = new ItemStack[menuSize];
		int i = 0;
		for (AuctionShop shop : shops) {
			AuctionSellerData sellerData = sellerDataMap.get(shop.getId());
			Double bestPrice = shop.getBestPrice();

			lastShopSlots[i] = shop;
			contents[i] = new ItemStack(shop.getRefItem());
			ItemMeta itemMeta = contents[i].getItemMeta();
			List<String> desc = new ArrayList<>();

			if (bestPrice != null) {
				desc.add(ChatColor.YELLOW + msgBestSalePrice.replace("%price%", plugin.economy().format(bestPrice)));
				itemMeta.setLore(desc);
			}
			if (sellerData.getDefaultPrice() != null) {
				desc.add(ChatColor.GREEN + msgSalePrice.replace("%price%", plugin.economy().format(sellerData.getDefaultPrice())));
			}

			itemMeta.setLore(desc);
			contents[i].setItemMeta(itemMeta);

			i++;
		}

		// Display help each HELP_DISPLAY_DELAY milliseconds
		try {
			if (!lastHelpCache.asMap().containsKey(aucPlayer.getPlayerId())) {
				player.sendMessage(chatInfo);
				lastHelpCache.get(aucPlayer.getPlayerId());
			}
		} catch (ExecutionException e) {
			plugin.getLogger().log(Level.WARNING, "Couldn't determine if we had to display help to the player", e);
		}

		shopSlots = lastShopSlots;
		Inventory inv = Bukkit.createInventory(player, menuSize, plugin.wellConfig().getString("inventory.menu.title"));
		inv.setContents(contents);
		return inv;
	}

	/**
	 * Checks if is single-item shop menu.
	 * 
	 * @return true, if is single
	 */
	public boolean isSingle() {
		return shopEntity.getShops().size() == 1;
	}

	/**
	 * Checks if is sale slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if is sale slot
	 */
	public boolean isSaleSlot(int slot) {
		return slot == SALE_SLOT;
	}

	/**
	 * Checks if is slot for shop.
	 * 
	 * @param slot
	 *            the slot
	 * @return the auction shop
	 */
	public AuctionShop shopSlot(int slot) {
		if (isSingle()) {
			return slot == BUY_SLOT ? shopEntity.getShops().iterator().next() : null;
		} else {
			return shopSlots[slot];
		}
	}

	/**
	 * Checks if is info slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if is info slot
	 */
	public boolean isInfoSlot(int slot) {
		return isSingle() && slot == INFO_SLOT;
	}
}
