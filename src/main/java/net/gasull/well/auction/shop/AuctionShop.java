package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * The actual Auction Shop, shared all over the world for a given refItem
 * {@link ItemStack}.
 */
public class AuctionShop {

	/** The ref item. */
	private ItemStack refItem;

	/** The registered shop-entities. */
	private List<ShopEntity> registered = new ArrayList<ShopEntity>();

	/** The sales. */
	private List<AuctionSale> sales = new ArrayList<AuctionSale>();

	/** The player sales. */
	private Map<UUID, List<AuctionSale>> playerSales = new HashMap<UUID, List<AuctionSale>>();

	/**
	 * Instantiates a new auction shop.
	 * 
	 * @param stack
	 *            the reference item
	 */
	AuctionShop(ItemStack stack) {
		this.refItem = refItemFor(stack);
	}

	/**
	 * Register entity for it to "contain" the shop.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 */
	void registerEntity(ShopEntity shopEntity) {
		registered.add(shopEntity);
	}

	/**
	 * Sell.
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 * @return the auction sale
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	AuctionSale sell(OfflinePlayer player, ItemStack item, double price) throws AuctionShopException {

		if (price < 0) {
			throw new AuctionShopException("Can't sell for a price less than 0");
		}

		AuctionSale sale = new AuctionSale(player, this, item, price);
		sales.add(sale);

		List<AuctionSale> pSales = playerSales.get(player.getUniqueId());
		if (pSales == null) {
			pSales = new ArrayList<AuctionSale>();
			playerSales.put(player.getUniqueId(), pSales);
		}

		pSales.add(sale);
		return sale;
	}

	/**
	 * Buy.
	 * 
	 * @param player
	 *            the player
	 * @param sale
	 *            the sale
	 * @return the bought stack
	 * @throws AuctionShopException
	 *             the auction shop exception
	 */
	ItemStack buy(OfflinePlayer player, AuctionSale sale) throws AuctionShopException {
		if (!sales.remove(sale)) {
			throw new AuctionShopException("Sale not found but should have been");
		}

		List<AuctionSale> pSales = playerSales.get(player.getUniqueId());
		if (pSales != null) {
			pSales.remove(sale);

			if (pSales.size() == 0) {
				playerSales.remove(player.getUniqueId());
			}
		}

		return sale.getItem();
	}

	/**
	 * Fetches a sale for stack.
	 * 
	 * @param saleStack
	 *            the sale stack
	 * @return the auction sale
	 */
	public AuctionSale saleForStack(ItemStack saleStack) {
		AuctionSale sale = null;

		for (AuctionSale s : sales) {
			if (s.isSellingStack(saleStack)) {
				sale = s;
				break;
			}
		}

		return sale;
	}

	/**
	 * Checks if the shop sells an item.
	 * 
	 * @param item
	 *            the item
	 * @return the check
	 */
	public boolean sells(ItemStack item) {
		return refItem.equals(refItemFor(item));
	}

	/**
	 * Gets the ref item.
	 * 
	 * @return the ref item
	 */
	public ItemStack getRefItem() {
		return new ItemStack(refItem);
	}

	/**
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	public List<AuctionSale> getSales() {
		return sales;
	}

	/**
	 * Gets the sales for a player.
	 * 
	 * @return the sales
	 */
	public List<AuctionSale> getSales(OfflinePlayer player) {
		return playerSales.get(player.getUniqueId());
	}

	/**
	 * Provides the reference item for an item.
	 * 
	 * @param item
	 *            the item
	 * @return the reference item
	 */
	public static ItemStack refItemFor(ItemStack item) {
		ItemStack refItem = new ItemStack(item);
		refItem.setAmount(1);
		return refItem;
	}
}
