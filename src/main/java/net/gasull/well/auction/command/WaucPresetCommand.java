package net.gasull.well.auction.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import net.gasull.well.WellPermissionManager.WellPermissionException;
import net.gasull.well.auction.WellAuction;
import net.gasull.well.auction.db.model.ShopEntityModel;
import net.gasull.well.auction.shop.entity.ShopEntity;
import net.gasull.well.command.WellCommand;
import net.gasull.well.command.WellCommandException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /wauc detach
 */
public class WaucPresetCommand extends WellCommand<Player> {

	/** The plugin. */
	private WellAuction plugin;

	/** The helper. */
	private WaucCommandHelper helper;

	/** The user's presets file. */
	private final File userPresetsFile;

	/** The error message when a problem in presets file loading happens. */
	private final String ERR_PRESETS_FILE;

	/** The error message for presets key in conf. */
	private final String ERR_PRESETS_KEY;

	/** The warning message for invalid material. */
	private final String WARN_INVALID_MATERIAL;

	/** The success preset loading message. */
	private final String SUCC_PRESET;

	public WaucPresetCommand(WellAuction plugin, WaucCommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
		this.userPresetsFile = new File(plugin.getDataFolder(), "presets.yml");
		this.ERR_PRESETS_FILE = ChatColor.DARK_RED
				+ plugin.wellConfig().getString("lang.command.error.presets.file",
						"Couldn't load presets file. Please try to restart the server or reload plugins.");
		this.ERR_PRESETS_KEY = ChatColor.DARK_RED + plugin.wellConfig().getString("lang.command.error.presets.key", "Couldn't find presets for key %key%");
		this.WARN_INVALID_MATERIAL = ChatColor.DARK_AQUA
				+ plugin.wellConfig().getString("lang.command.error.presets.invalidMaterial", "Warning: %material% is an unknown material");
		this.SUCC_PRESET = ChatColor.GREEN + plugin.wellConfig().getString("lang.command.presets.success", "Successfully loaded preset");
	}

	@SuppressWarnings({ "deprecation" })
	@Override
	public String handleCommand(Player player, String[] args) throws WellCommandException, WellPermissionException {

		ShopEntity shopEntity = helper.getTargetShop(args, player);
		ShopEntityModel model = shopEntity.getModel();
		String presetKey = args[0];
		try {
			FileConfiguration presetsConf = new YamlConfiguration();
			presetsConf.load(userPresetsFile);

			if (!presetsConf.contains(presetKey)) {
				throw new WellCommandException(ERR_PRESETS_KEY.replace("%key%", presetKey));
			}

			// Adding presets in the shop entity
			List<String> presets = presetsConf.getStringList(presetKey);
			for (String preset : presets) {
				String[] split = preset.split(":");
				String itemName = split[0];
				Byte itemData = null;
				Material mat = Material.matchMaterial(itemName);

				if (mat == null) {
					player.sendMessage(ChatColor.DARK_AQUA + WARN_INVALID_MATERIAL.replace("%material%", itemName));
				} else {
					// Try to get data value
					if (mat.getData() != null && split.length > 1) {
						try {
							itemData = Byte.valueOf(split[1]);
						} catch (NumberFormatException e) {
							// Ignore
						}
					}

					ItemStack refItem;
					if (itemData == null) {
						refItem = new ItemStack(mat);
					} else {
						refItem = new ItemStack(mat, 0, (short) 0, itemData);
					}

					model.addShop(plugin.db().getShop(refItem));
				}
			}

			shopEntity.register();
			plugin.db().save(shopEntity.getModel());
			plugin.db().save(shopEntity.getModel().getEntityToShops());

			return SUCC_PRESET;
		} catch (FileNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't load presets: file not found", e);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't load presets", e);
		} catch (InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't load presets: invalid configuration", e);
		}

		return ERR_PRESETS_FILE;
	}

	@Override
	public String getName() {
		return "preset";
	}

	@Override
	public String[] getRequiredArgs() {
		return new String[] { "key" };
	}

	@Override
	public String[] getOptionalArgs() {
		return null;
	}

	@Override
	public String getPermission() {
		return null;
	}

}
