package net.gasull.well.auction.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.gasull.well.auction.db.model.AuctionSale;
import net.gasull.well.auction.db.model.AuctionShop;

import org.bukkit.inventory.ItemStack;

/**
 * Represents sales list for an {@link AuctionShop}. Iterates only on best
 * offers for specific stack sizes.
 */
public class AuctionSalesCollection implements Collection<AuctionSale> {

	/** The sales by stack. */
	private final Map<Integer, Collection<AuctionSale>> salesByStack = new HashMap<>();

	/** The best offers. */
	private final Set<AuctionSale> bestOffers = new TreeSet<>();

	/**
	 * Instantiates a new auction sales collection.
	 * 
	 * @param stackSizes
	 *            the possible stack sizes
	 */
	public AuctionSalesCollection(List<Integer> stackSizes) {

		for (Integer stackQty : stackSizes) {
			salesByStack.put(stackQty, new TreeSet<AuctionSale>());
		}

		salesByStack.put(-1, new TreeSet<AuctionSale>());
	}

	@Override
	public int size() {
		return salesByStack.keySet().size();
	}

	@Override
	public boolean isEmpty() {
		for (Collection<AuctionSale> sales : salesByStack.values()) {
			if (!sales.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean contains(Object o) {
		if (!(o instanceof AuctionSale)) {
			return false;
		}

		AuctionSale sale = (AuctionSale) o;
		ItemStack item = sale.getItem();
		return getSales(item.getAmount()).contains(sale);
	}

	@Override
	public Iterator<AuctionSale> iterator() {
		return new AuctionSalesIterator();
	}

	@Override
	public Object[] toArray() {
		return bestOffers.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return bestOffers.toArray(a);
	}

	@Override
	public boolean add(AuctionSale sale) {
		Collection<AuctionSale> sales = getSales(sale.getItem().getAmount());

		if (sales.contains(sale) || sale == null) {
			return false;
		}

		if (sales.size() > 0) {
			AuctionSale bestOffer = sales.iterator().next();

			if (sale.compareTo(bestOffer) < 0) {
				bestOffers.remove(bestOffer);
				bestOffers.add(sale);
			}
		} else {
			bestOffers.add(sale);
		}

		sales.add(sale);
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof AuctionSale) || !contains(o)) {
			return false;
		}

		AuctionSale sale = (AuctionSale) o;
		int amount = sale.getItem().getAmount();

		if (!salesByStack.keySet().contains(amount)) {
			amount = -1;
		}

		salesByStack.get(amount).remove(sale);

		if (bestOffers.remove(sale)) {
			if (salesByStack.get(amount).size() > 0) {
				bestOffers.add(salesByStack.get(amount).iterator().next());
			}
		}

		return true;
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
		boolean ret = true;

		for (Object o : c) {
			ret = ret && remove(o);
		}

		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean ret = false;

		for (Object o : c) {
			if (contains(o)) {
				remove(o);
				ret = true;
			}
		}

		return ret;
	}

	@Override
	public void clear() {
		bestOffers.clear();
		salesByStack.clear();
	}

	/**
	 * Gets the sales for a given stack size.
	 * 
	 * @param stackSize
	 *            the stack size
	 * @return the sales
	 */
	private Collection<AuctionSale> getSales(final Integer stackSize) {
		Collection<AuctionSale> sales;

		if (salesByStack.keySet().contains(stackSize)) {
			sales = salesByStack.get(stackSize);
		} else {
			sales = salesByStack.get(-1);
		}

		return sales;
	}

	/**
	 * {@link AuctionSalesCollection}'s {@link Iterator}.
	 */
	private class AuctionSalesIterator implements Iterator<AuctionSale> {

		/** The set iterator. */
		private final Iterator<AuctionSale> setIterator;

		/** The current sale. */
		private AuctionSale sale;

		/**
		 * Instantiates a new auction sales iterator.
		 */
		private AuctionSalesIterator() {
			this.setIterator = bestOffers.iterator();
		}

		@Override
		public boolean hasNext() {
			return setIterator.hasNext();
		}

		@Override
		public AuctionSale next() {
			sale = setIterator.next();
			return sale;
		}

		@Override
		public void remove() {
			// Disabled
		}
	}
}
