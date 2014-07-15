package net.gasull.well.auction.shop;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionShop;

/**
 * Represents sales list for an {@link AuctionShop}. Iterates only on best
 * offers for specific stack sizes.
 */
public class AuctionSalesCollection implements Collection<AuctionSale> {

	/** The plugin. */
	private WellAuction plugin;

	/** The shop. */
	private AuctionShop shop;

	/** The best offers. */
	private final Map<Integer, AuctionSale> bestOffers = new TreeMap<>();

	/** The possible stack sizes. */
	private final List<Integer> stackSizes;

	/**
	 * Instantiates a new auction sales collection.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param shop
	 *            the shop
	 * @param stackSizes
	 *            the possible stack sizes
	 */
	public AuctionSalesCollection(WellAuction plugin, AuctionShop shop, List<Integer> stackSizes) {
		this.plugin = plugin;
		this.shop = shop;
		this.stackSizes = stackSizes;
	}

	@Override
	public int size() {
		return bestOffers.size();
	}

	@Override
	public boolean isEmpty() {
		return bestOffers.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (!(o instanceof AuctionSale)) {
			return false;
		}

		AuctionSale sale = (AuctionSale) o;
		return plugin.db().shopIsSelling(shop, sale);
	}

	@Override
	public Iterator<AuctionSale> iterator() {
		return bestOffers.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return bestOffers.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return bestOffers.values().toArray(a);
	}

	@Override
	public boolean add(AuctionSale sale) {
		// Ignore, updates are internal
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// Ignore, updates are internal
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends AuctionSale> c) {
		boolean ret = true;

		for (AuctionSale sale : c) {
			ret = ret && add(sale);
		}

		return ret;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// Not implemented
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// Not implemented
		return false;
	}

	@Override
	public void clear() {
		bestOffers.clear();
	}

	/**
	 * Refresh for a given sale (based on its item amount).
	 * 
	 * @param sale
	 *            the sale
	 */
	public void refresh(AuctionSale sale) {
		if (sale == null) {
			refreshAll();
		} else {
			refresh(sale.getItem().getAmount());
		}
	}

	/**
	 * Refreshes for an amount.
	 * 
	 * @param amount
	 *            the amount
	 */
	public void refresh(int givenAmount) {
		int amount = getTradeAmount(givenAmount);
		bestOffers.remove(amount);
		AuctionSale bestSale = plugin.db().findBestSaleForAmount(shop, amount, stackSizes);

		if (bestSale != null) {
			bestOffers.put(amount, bestSale);
		}
	}

	/**
	 * Refreshes for all the (valid) amounts.
	 */
	public void refreshAll() {

		// Clear valid amounts
		for (Integer i : stackSizes) {
			bestOffers.remove(i);
		}

		for (AuctionSale sale : plugin.db().findBestSales(shop, stackSizes)) {
			if (!bestOffers.containsKey(sale.getAmount())) {
				bestOffers.put(sale.getAmount(), sale);
			}
		}
	}

	/**
	 * Gets the trade amount.
	 * 
	 * @param actualAmount
	 *            the actual amount
	 * @return the trade amount
	 */
	private int getTradeAmount(int actualAmount) {
		return stackSizes.contains(actualAmount) ? actualAmount : -1;
	}
}
