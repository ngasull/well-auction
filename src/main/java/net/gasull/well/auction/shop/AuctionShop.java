package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The actual Auction Shop, shared all over the world for a given
 * {@link Material}.
 */
public class AuctionShop {

	/** The auction type. */
	private AuctionType type;

	/** The registered shop-entities. */
	private List<ShopEntity> registered = new ArrayList<ShopEntity>();

	/** The sales. */
	private List<AuctionSale> sales = new ArrayList<AuctionSale>();

	/**
	 * Instantiates a new auction shop.
	 * 
	 * @param type
	 *            the auction type
	 */
	public AuctionShop(AuctionType type) {
		this.type = type;

		// TODO Create its Inventory here
	}

	/**
	 * Register entity for it to "contain" the shop.
	 * 
	 * @param shopEntity
	 *            the shop entity
	 */
	public void registerEntity(ShopEntity shopEntity) {
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
	public AuctionSale sell(Player player, ItemStack item, double price) throws AuctionShopException {

		if (price < 0) {
			throw new AuctionShopException("Can't sell for a price less than 0");
		}

		AuctionSale sale = new AuctionSale(player.getName(), item, price);
		sales.add(sale);

		return sale;
	}

	public ItemStack buy(Player player, AuctionSale sale) throws AuctionShopException {
		if (!sales.remove(sale)) {
			throw new AuctionShopException("Sale not found but should have been");
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
	 * Gets the auction type.
	 * 
	 * @return the auction type
	 */
	public AuctionType getType() {
		return type;
	}

	/**
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	public List<AuctionSale> getSales() {
		return sales;
	}
}
