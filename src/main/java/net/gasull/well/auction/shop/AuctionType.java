package net.gasull.well.auction.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

/**
 * Defines a type of sale from an {@link ItemStack}
 */
public class AuctionType {

	private ItemStack refItem;

	/** The singleton map. Guarantees the exact same type is used */
	private static Map<AuctionType, AuctionType> singletonMap = new HashMap<AuctionType, AuctionType>();

	/**
	 * Instantiates a new auction type.
	 * 
	 * @param stack
	 *            the stack
	 */
	private AuctionType(ItemStack stack) {
		this.refItem = new ItemStack(stack);
		this.refItem.setAmount(1);
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
	 * Gets the unique {@link AuctionType}.
	 * 
	 * @param stack
	 *            the stack
	 * @return the auction type
	 */
	public static AuctionType get(ItemStack stack) {
		AuctionType type = new AuctionType(stack);
		AuctionType singletonType = singletonMap.get(type);

		if (singletonType == null) {
			singletonMap.put(type, type);
			return type;
		} else {
			return singletonType;
		}
	}

	/**
	 * Clear static mapping.
	 */
	public static void clear() {
		singletonMap.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || refItem == null) {
			return false;
		}

		if (obj instanceof AuctionType) {
			AuctionType other = (AuctionType) obj;
			return refItem.isSimilar(other.refItem);
		} else if (obj instanceof ItemStack) {
			ItemStack other = (ItemStack) obj;
			return refItem.isSimilar(other);
		}

		return false;
	}
}
