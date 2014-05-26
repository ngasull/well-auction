package net.gasull.well.auction.inventory;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.shop.AuctionShop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents the menu to access Sell & Buy inventories.
 */
public class AuctionMenu {

	/** The plugin. */
	private WellAuction plugin;

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
	 */
	public AuctionMenu(WellAuction plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the menu for type.
	 * 
	 * @param shop
	 *            the shop
	 * @return the menu for shop
	 */
	@SuppressWarnings("deprecation")
	public ItemStack[] getMenuForShop(AuctionShop shop) {

		ItemStack[] contents = new ItemStack[MENU_SIZE];

		for (int i = 0; i < MENU_SIZE; i++) {
			if (isSaleSlot(i)) {
				contents[i] = new ItemStack(plugin.wellConfig().getInt("inventory.menu.button.sell.item", Material.GOLD_INGOT.getId()));

				ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(contents[i].getType());
				itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.sell.title", "Sell"));
				contents[i].setItemMeta(itemMeta);
			} else if (isBuySlot(i)) {
				contents[i] = new ItemStack(plugin.wellConfig().getInt("inventory.menu.button.buy.item", Material.EMERALD.getId()));

				ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(contents[i].getType());
				itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.buy.title", "Buy"));
				contents[i].setItemMeta(itemMeta);
			} else if (isInfoSlot(i)) {
				contents[i] = new ItemStack(plugin.wellConfig().getInt("inventory.menu.button.info.item", Material.PAPER.getId()));

				ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(contents[i].getType());
				itemMeta.setDisplayName(plugin.wellConfig().getString("lang.inventory.menu.button.info.title", "Info"));
				contents[i].setItemMeta(itemMeta);
				// contents[i].getItemMeta().setDisplayName(plugin.wellConfig().getString("lang.inventory.buy",
				// "Buy"));
			} else {
				switch (i) {
				case REFITEM_SLOT:
				case 7:
				case 9:
				case 17:
				case 19:
				case 25:
					contents[i] = new ItemStack(shop.getRefItem());
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
