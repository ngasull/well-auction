package net.gasull.well.auction.inventory;

import java.util.ArrayList;
import java.util.List;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionSellerData;
import net.gasull.well.auction.db.model.AuctionShop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents the menu to access Sell & Buy inventories.
 */
public class AuctionMenu {

	/** The plugin. */
	private final WellAuction plugin;

	/** The sell button. */
	private final ItemStack sellButton;

	/** The buy button. */
	private final ItemStack buyButton;

	/** The info. */
	private final ItemStack info;

	/** The msg sale no price. */
	private final String msgSaleNoPrice;

	/** The msg sale price. */
	private final String msgSalePrice;

	/** The msg best sale price. */
	private final String msgBestSalePrice;

	/** The Constant INFO_SLOT. */
	public static final int INFO_SLOT = 13;

	/** The Constant BUY_SLOT. */
	public static final int BUY_SLOT = 14;

	/** The Constant SALE_SLOT. */
	public static final int SALE_SLOT = 12;

	/** The Constant REFITEM_SLOT. */
	public static final int REFITEM_SLOT = 1;

	/** The size of the menu inventory. */
	static final int MENU_SIZE = 27;

	/**
	 * Instantiates a new auction menu.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public AuctionMenu(WellAuction plugin) {
		this.plugin = plugin;

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
		defaultInfo.add(ChatColor.AQUA + "Either click Sell button on the left");
		defaultInfo.add(ChatColor.AQUA + "or click Buy button on the right.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "You can setup defaut sell price by");
		defaultInfo.add(ChatColor.AQUA + "shift-clicking Sell button.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "You can setup the price of your");
		defaultInfo.add(ChatColor.AQUA + "individual sales by shift-clicking");
		defaultInfo.add(ChatColor.AQUA + "your sale.");
		defaultInfo.add(ChatColor.DARK_GRAY + "=============================");
		defaultInfo.add(ChatColor.AQUA + "You can remove your sales by");
		defaultInfo.add(ChatColor.AQUA + "right-clicking them.");

		itemMeta = Bukkit.getItemFactory().getItemMeta(info.getType());
		itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.info.title", "Welcome to the Auction House!"));
		itemMeta.setLore(plugin.wellConfig().getStringList("lang.inventory.menu.button.info.desc", defaultInfo));
		info.setItemMeta(itemMeta);
	}

	/**
	 * Gets the menu for type.
	 * 
	 * @param sellerData
	 *            the seller data
	 * @return the menu for shop
	 */
	public ItemStack[] getMenuForShop(AuctionSellerData sellerData, Double bestPrice) {

		AuctionShop shop = sellerData.getShop();
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
			} else if (isBuySlot(i)) {
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

		return contents;
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
	 * Checks if is buy slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if is buy slot
	 */
	public boolean isBuySlot(int slot) {
		return slot == BUY_SLOT;
	}

	/**
	 * Checks if is info slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return true, if is info slot
	 */
	public boolean isInfoSlot(int slot) {
		return slot == INFO_SLOT;
	}
}
