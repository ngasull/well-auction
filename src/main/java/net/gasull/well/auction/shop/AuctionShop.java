package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The actual Auction Shop, shared all over the world for a given refItem
 * {@link ItemStack}.
 */
public class AuctionShop {

	/** The id. */
	private UUID id = UUID.randomUUID();

	/** The ref item. */
	private ItemStack refItem;

	/** The registered shop-entities. */
	private List<ShopEntity> registered = new ArrayList<>();

	/** The sales. */
	private List<AuctionSale> sales = new ArrayList<>();

	/** The auction player. */
	private Map<UUID, AuctionPlayer> auctionPlayers = new HashMap<>();

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
	 * @param plugin
	 *            the plugin
	 * @param shopEntity
	 *            the shop entity
	 */
	void registerEntity(JavaPlugin plugin, ShopEntity shopEntity) {
		shopEntity.register(plugin, this);
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
	AuctionSale sell(AuctionPlayer player, ItemStack item) throws AuctionShopException {

		Double defaultPrice = player.getDefaultPrices().get(this);
		if (defaultPrice != null && defaultPrice < 0) {
			throw new AuctionShopException("Can't sell for a price less than 0");
		}

		AuctionSale sale = new AuctionSale(player, this, item);
		sale.setPrice(defaultPrice);
		player.getSales().add(sale);

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

		sales.remove(sale);
		getAuctionPlayer(player).getSales().remove(sale);

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
	 * Sets the new sale price for a player.
	 * 
	 * @param player
	 *            the player
	 * @param price
	 *            the price
	 */
	public void setDefaultPrice(Player player, double price) {
		getAuctionPlayer(player).setDefaultPrice(this, price);
	}

	/**
	 * Gets the unique id of the shop.
	 * 
	 * @return the id
	 */
	public UUID getId() {
		return id;
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
	 * Gets the registered shop entities for this shop.
	 * 
	 * @return the shop entities
	 */
	public List<ShopEntity> getRegistered() {
		return registered;
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
		return getAuctionPlayer(player).getSales();
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

	@Override
	public String toString() {
		return "AuctionShop [id=" + id + ", refItem=" + refItem + "]";
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
