package net.gasull.well.auction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.gasull.well.auction.db.model.AucEntityToShop;
import net.gasull.well.auction.db.model.AuctionShop;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.AucShopEntityManager;
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

	/** The shop entity manager. */
	private AucShopEntityManager shopEntityManager;

	/** "unknown command" error message. */
	private final String ERR_UNKNOWN_CMD;

	/** "must be a player" error message. */
	private final String ERR_MUST_BE_PLAYER;

	/** "not looking at a block" error message. */
	private final String ERR_NO_BLOCK_SEEN;

	/** "Shop already exists" error message. */
	private final String ERR_ENTITY_EXISTS;

	/** "can't sell air" error message. */
	private final String ERR_CANT_SELL_AIR;

	/** Shop creation success message. */
	private final String SUCC_CREATION;

	/** Shop detach success message. */
	private final String SUCC_DETACH;

	/** Shop listing message for no shops registered. */
	private final String LIST_NO_SHOP;

	/**
	 * Instantiates a new well auction command handler.
	 * 
	 * @param plugin
	 *            the well auction plugin
	 * @param shopEntityManager
	 *            the shop entity manager
	 */
	public WellAuctionCommandHandler(WellAuction plugin, AucShopEntityManager shopEntityManager) {
		this.plugin = plugin;
		this.shopEntityManager = shopEntityManager;

		this.ERR_UNKNOWN_CMD = ChatColor.DARK_RED
				+ plugin.wellConfig().getString(
						"lang.command.error.unknownCommand",
						"You specified an unknown command");
		this.ERR_MUST_BE_PLAYER = ChatColor.DARK_RED
				+ plugin.wellConfig().getString(
						"lang.command.error.mustBePlayer",
						"You must be a player to run this command");
		this.ERR_NO_BLOCK_SEEN = ChatColor.DARK_RED
				+ plugin.wellConfig().getString(
						"lang.command.error.notBlockSeen",
						"You must be looking at a block");
		this.ERR_ENTITY_EXISTS = ChatColor.DARK_RED
				+ plugin.wellConfig().getString(
						"lang.command.error.shopEntityExists",
						"A shop already exists here");
		this.ERR_CANT_SELL_AIR = ChatColor.DARK_RED
				+ plugin.wellConfig().getString(
						"lang.command.error.cantSellAir",
						"You can't put air on sale!");

		this.SUCC_CREATION = ChatColor.GREEN
				+ plugin.wellConfig().getString(
						"lang.command.creation.success",
						"Successfully created an AuctionShop for %item%");
		this.SUCC_DETACH = ChatColor.GREEN
				+ plugin.wellConfig().getString("lang.command.detach.success",
						"Successfully detached a shop");
		this.LIST_NO_SHOP = ChatColor.YELLOW
				+ plugin.wellConfig().getString("lang.command.list.noShop",
						"No AuctionShop registered yet");
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
	public boolean handle(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("wellauction")) {

			if (args.length == 0) {
				// TODO Display help
			} else {
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

				switch (args[0]) {
				case "attach":
					if (isPlayerCheck(sender)) {
						handleCreate(sender, subArgs);
					}
					break;
				case "detach":
					if (isPlayerCheck(sender)) {
						handleDelete(sender, subArgs);
					}
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
	private void handleCreate(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		ItemStack refItem = null;
		ShopEntity shopEntity = getTargetShop(args, player);

		if (shopEntity == null) {
			return;
		}

		if (plugin.db().findSimilarShopEntity(shopEntity) != null) {
			player.sendMessage(ERR_ENTITY_EXISTS);
			return;
		}

		// Take the item in hand as default sale
		if (refItem == null) {
			refItem = player.getItemInHand();
		}

		if (refItem == null || refItem.getType() == Material.AIR) {
			player.sendMessage(ERR_CANT_SELL_AIR);
			return;
		}

		// FIXME NOTHING OK HERE!
		AuctionShop shop = plugin.db().getShop(refItem);
		shopEntity.register(plugin);
		plugin.db().save(shopEntity.getModel());

		player.sendMessage(SUCC_CREATION.replace("%item%", shop
				.getRefItemCopy().toString()));
	}

	/**
	 * Handle delete.
	 * 
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 */
	private void handleDelete(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		ShopEntity shopEntity = getTargetShop(args, player);

		if (shopEntity == null) {
			return;
		}

		shopEntity.unregister(plugin);

		for (AucEntityToShop entityToShop : shopEntity.getModel()
				.getEntityToShops()) {
			plugin.db().delete(entityToShop);
		}
		plugin.db().delete(shopEntity.getModel());

		player.sendMessage(SUCC_DETACH);
	}

	/**
	 * List Auction Houses to the sender.
	 * 
	 * @param sender
	 *            the sender
	 */
	private void handleList(CommandSender sender) {
		StringBuilder msg;
		Collection<AuctionShop> shops = plugin.db().listShops();

		if (shops.isEmpty()) {
			sender.sendMessage(LIST_NO_SHOP);
		} else {
			for (AuctionShop shop : shops) {
				msg = new StringBuilder().append(ChatColor.YELLOW).append(shop)
						.append(": ").append("\n");

				int i = 0;
				String[] alterColor = new String[] { ChatColor.AQUA.toString(),
						ChatColor.BLUE.toString() };
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

	/**
	 * Checks if is player.
	 * 
	 * @param sender
	 *            the sender
	 * @return true, if is player check
	 */
	private boolean isPlayerCheck(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ERR_MUST_BE_PLAYER);
			return false;
		}

		return true;
	}

	/**
	 * Gets the target shop.
	 * 
	 * @param args
	 *            the args
	 * @param player
	 *            the player
	 * @return the target shop
	 */
	private ShopEntity getTargetShop(String[] args, Player player) {
		ShopEntity shopEntity = null;

		if (args.length > 0) {
			player.sendMessage("NOT SUPPORTED YET!");
			return null;
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
				return null;
			}

			shopEntity = new BlockShopEntity(solidBlock);

		}
		
		if (shopEntity != null) {
			ShopEntityModel similarEntity = plugin.db().findSimilarShopEntity(shopEntity);
			
			if (similarEntity != null) {
				shopEntity = shopEntityManager.get(similarEntity);
			}
		}

		return shopEntity;
	}
}
