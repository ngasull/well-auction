package net.gasull.well.auction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.gasull.well.auction.shop.AuctionShop;
import net.gasull.well.auction.shop.AuctionShopManager;
import net.gasull.well.auction.shop.entity.BlockShopEntity;
import net.gasull.well.auction.shop.entity.ShopEntity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

/**
 * The Class WellAuctionCommandHandler.
 */
public class WellAuctionCommandHandler {

	/** The plugin. */
	private WellAuction plugin;

	/** The shop manager. */
	private AuctionShopManager shopManager;

	/** "unknown command" error message. */
	private final String ERR_UNKNOWN_CMD;

	/** "must be a player" error message. */
	private final String ERR_MUST_BE_PLAYER;

	/** "not looking at a block" error message. */
	private final String ERR_NO_BLOCK_SEEN;

	/** "can't sell air" error message. */
	private final String ERR_CANT_SELL_AIR;

	/** Shop creation success message. */
	private final String SUCC_CREATION;

	/** Shop listing message for no shops registered. */
	private final String LIST_NO_SHOP;

	/**
	 * Instantiates a new well auction command handler.
	 * 
	 * @param plugin
	 *            the well auction plugin
	 */
	public WellAuctionCommandHandler(WellAuction plugin, AuctionShopManager shopManager) {
		this.plugin = plugin;
		this.shopManager = shopManager;

		this.ERR_UNKNOWN_CMD = ChatColor.DARK_RED + plugin.wellConfig().getString("lang.command.error.unknownCommand", "You specified an unknown command");
		this.ERR_MUST_BE_PLAYER = ChatColor.DARK_RED
				+ plugin.wellConfig().getString("lang.command.error.mustBePlayer", "You must be a player to run this command");
		this.ERR_NO_BLOCK_SEEN = ChatColor.DARK_RED + plugin.wellConfig().getString("lang.command.error.notBlockSeen", "You must be looking at a block");
		this.ERR_CANT_SELL_AIR = ChatColor.DARK_RED + plugin.wellConfig().getString("lang.command.error.cantSellAir", "You can't put air on sale!");

		this.SUCC_CREATION = ChatColor.GREEN + plugin.wellConfig().getString("lang.command.creation.success", "Successfully created an AuctionShop for %item%");
		this.LIST_NO_SHOP = ChatColor.YELLOW + plugin.wellConfig().getString("lang.command.list.noShop", "No AuctionShop registered yet");
	}

	/**
	 * Handle.
	 * 
	 * @param sender
	 *            the sender
	 * @param cmd
	 *            the cmd
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	public boolean handle(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("wellauction")) {

			if (args.length == 0) {
				// TODO Display help
			} else {
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

				switch (args[0]) {
				case "create":
					handleCreate(sender, subArgs);
					break;
				case "list":
					handleList(sender);
					break;
				default:
					sender.sendMessage(ERR_UNKNOWN_CMD);
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Handle create command, that creates an Auction Shop.
	 * 
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args for the sub-command
	 */
	/**
	 * @param sender
	 * @param args
	 */
	private void handleCreate(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ERR_MUST_BE_PLAYER);
			return;
		}

		Player player = (Player) sender;
		ShopEntity shopEntity = null;
		ItemStack refItem = null;

		if (args.length > 0) {
			player.sendMessage("NOT SUPPORTED YET!");
			return;
		}

		// Take the block seen by default as a shop
		if (shopEntity == null) {
			Block solidBlock = null;
			Block block = null;
			BlockIterator blockIterator = new BlockIterator(player, 3);

			while (blockIterator.hasNext() && solidBlock == null) {
				block = blockIterator.next();
				if (block.getType() != Material.AIR) {
					solidBlock = block;
				}
			}
			if (solidBlock == null) {
				player.sendMessage(ERR_NO_BLOCK_SEEN);
				return;
			}

			shopEntity = new BlockShopEntity(solidBlock);
		}

		// Take the item in hand as default sale
		if (refItem == null) {
			refItem = player.getItemInHand();
		}

		if (refItem == null || refItem.getType() == Material.AIR) {
			player.sendMessage(ERR_CANT_SELL_AIR);
			return;
		}

		AuctionShop shop = shopManager.registerEntityAsShop(refItem, shopEntity);
		player.sendMessage(SUCC_CREATION.replace("%item%", shop.getRefItem().toString()));
	}

	/**
	 * List Auction Houses to the sender.
	 * 
	 * @param sender
	 *            the sender
	 */
	private void handleList(CommandSender sender) {
		StringBuilder msg;
		Collection<AuctionShop> shops = shopManager.getShops();

		if (shops.isEmpty()) {
			sender.sendMessage(LIST_NO_SHOP);
		} else {
			for (AuctionShop shop : shops) {
				msg = new StringBuilder().append(ChatColor.YELLOW).append(shop).append(": ").append("\n");

				int i = 0;
				String[] alterColor = new String[] { ChatColor.AQUA.toString(), ChatColor.BLUE.toString() };
				List<ShopEntity> registeredEntities = shop.getRegistered();

				for (ShopEntity shopEntity : registeredEntities) {
					msg.append(alterColor[i % 2]).append(shopEntity);

					if (++i < registeredEntities.size()) {
						msg.append(", ");
					}
				}

				sender.sendMessage(msg.toString());
			}
		}
	}
}
