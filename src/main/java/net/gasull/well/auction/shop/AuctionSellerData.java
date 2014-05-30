package net.gasull.well.auction.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Data for a seller, for a single shop.
 */
public class AuctionSellerData {

	/** The sales. */
	private final List<AuctionSale> sales = new ArrayList<>();

	/** The other players' sales, filtered from the current shop's sales. */
	private final Collection<AuctionSale> otherPlayersSales;

	/** The default price. */
	private Double defaultPrice;

	/**
	 * Instantiates a new auction seller data.
	 * 
	 * @param shop
	 *            the shop
	 * @param auctionPlayer
	 *            the auction player
	 */
	public AuctionSellerData(final AuctionShop shop, final AuctionPlayer auctionPlayer) {
		this.otherPlayersSales = Collections2.filter(shop.getSales(), new Predicate<AuctionSale>() {
			public boolean apply(@Nullable AuctionSale input) {
				return !auctionPlayer.equals(input.getSeller());
			}

			@Override
			public boolean equals(@Nullable Object object) {
				return super.equals(object);
			}
		});
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
	 * Gets the sale.
	 * 
	 * @param theItem
	 *            the the item
	 * @return the sale
	 */
	public AuctionSale getSale(ItemStack theItem) {

		for (AuctionSale sale : sales) {
			if (sale.isSellingStack(theItem)) {
				return sale;
			}
		}
		return null;
	}

	/**
	 * Gets the other players sales.
	 * 
	 * @return the other players sales
	 */
	public Collection<AuctionSale> getOtherPlayersSales() {
		return otherPlayersSales;
	}

	/**
	 * Gets the default price.
	 * 
	 * @return the default price
	 */
	public Double getDefaultPrice() {
		return defaultPrice;
	}

	/**
	 * Sets the default price.
	 * 
	 * @param defaultPrice
	 *            the new default price
	 */
	public void setDefaultPrice(Double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
}
