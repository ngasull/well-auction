package net.gasull.well.auction.conf;

/**
 * Enumerates Metadata keys for well-auction.
 */
public enum AuctionMetaKey {

	/** The shop. */
	SHOP("shop");

	/** The plugin prefix for every metadata keys of the plugin. */
	private static final String PREFIX = "well-auction-";

	/** The key. */
	private final String key;

	/**
	 * Instantiates a new auction meta key.
	 * 
	 * @param value
	 *            the value
	 */
	private AuctionMetaKey(String value) {
		this.key = PREFIX + value;
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}
