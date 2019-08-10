package com.darkblade12.enchantplus.command.types;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantmentCalculator;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.permission.Permission;

public final class AddCommand extends AbstractCommand<EnchantPlus> {
	private static final Pattern NUMBER = Pattern.compile("\\d+");

	@Override
	public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
		Player player = (Player) sender;
		Settings settings = plugin.getSettings();
		if (!settings.isManualEnchantingEnabled()) {
			handler.displayPluginMessage(sender, "§cEnchanting commands are currently disabled!");
			return;
		}
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() == Material.AIR) {
			handler.displayPluginMessage(sender, "§cYou have to hold an item in your hand!");
			return;
		}
		if (settings.isManualEnchantingAmountEnabled() && item.getAmount() > 1 && !Permission.AMOUNT_BYPASS.has(sender)) {
			handler.displayPluginMessage(sender, "§cYou can't enchant multiple items!");
			return;
		}
		Enchantment enchantment = settings.getEnchantment(parameters[0]);
		if (enchantment == null) {
			handler.displayPluginMessage(sender, "§cAn enchantment with this identifier doesn't exist!");
			return;
		}
		if (settings.isManualEnchantingInapplicableEnabled() && !enchantment.canEnchantItem(item) && !Permission.INAPPLICABLE_BYPASS.has(sender)) {
			handler.displayPluginMessage(sender, "§cThis enchantment is inapplicable for the item in your hand!");
			return;
		}
		EnchantmentMap map = EnchantmentMap.fromItemStack(item);
		if (settings.isManualEnchantingConflictingEnabled() && map.conflictsWith(enchantment) && !Permission.CONFLICTING_BYPASS.has(sender)) {
			handler.displayPluginMessage(sender, "§cThis enchantment conflicts with another enchantment on the item in your hand!");
			return;
		}
		String parameter = parameters[1];
		short level;
		if (parameter.equalsIgnoreCase("natural")) {
			level = (short) enchantment.getMaxLevel();
		} else {
			if (!NUMBER.matcher(parameter).matches()) {
				handler.displayPluginMessage(sender, "§6" + parameter + " §cisn't a valid parameter!");
				return;
			}
			try {
				level = Short.parseShort(parameter);
			} catch (Exception exception) {
				handler.displayPluginMessage(sender, "§cThe level can't be higher than §632767§c!");
				return;
			}
			if (level < 1) {
				handler.displayPluginMessage(sender, "§cThe level can't be lower than 1!");
				return;
			}
		}
		int restriction = settings.getLevelRestrictionAmount(player, enchantment);
		if (level > restriction) {
			handler.displayPluginMessage(sender, "§cThe level can't be higher than §6" + restriction + " §cfor this enchantment!");
			return;
		}
		if (map.hasEnchantment(enchantment, level)) {
			handler.displayPluginMessage(sender, "§cThis enchantment is already applied at this level to the item in your hand!");
			return;
		}
		if (!hasPowerSource(player, settings)) {
			handler.displayPluginMessage(sender, "§cYou have to be near a power source (enchantment table) to be able to execute this command!");
			return;
		}
		EnchantmentCalculator calculator = plugin.getCalculator();
		if (!calculator.transact(player, item, enchantment, level)) {
			handler.displayPluginMessage(sender, "§cYou need to have §6" + calculator.getCost(player, item, enchantment, level) + " §cexp levels to add this enchantment at level §e" + level + " §cto the item in your hand!");
			return;
		}
		Enchanter.forItemStack(item).addEnchantment(enchantment, level);
		handler.displayPluginMessage(sender, "§aThe §6" + EnchantmentInformation.getMinecraftName(enchantment) + " §aenchantment was added to the item in your hand at level §e" + level + "§a.");
	}

	public static boolean hasPowerSource(Player player, Settings settings) {
		if (!settings.isPowerSourceEnabled() || Permission.POWER_BYPASS.has(player)) {
			return true;
		}
		Location location = player.getLocation();
		World world = player.getWorld();
		int range = settings.getPowerSourceRange();
		double currentX = location.getX();
		double currentY = location.getY();
		double currentZ = location.getZ();
		double bPow = Math.pow(range + 0.5D, 2);
		double xPow;
		double zPow;
		for (int z = -range; z <= range; z++) {
			zPow = Math.pow(z, 2);
			for (int x = -range; x <= range; x++) {
				xPow = Math.pow(x, 2);
				for (int y = -range; y <= range; y++) {
					if ((xPow + Math.pow(y, 2) + zPow) <= bPow) {
						if (world.getBlockAt((int) (currentX + x), (int) (currentY + y), (int) (currentZ + z)).getType() == Material.ENCHANTING_TABLE) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "add";
	}

	@Override
	public String[] getParameters() {
		return new String[] { "<name>", "<level/natural>" };
	}

	@Override
	public Permission getPermission() {
		return Permission.ADD_COMMAND;
	}

	@Override
	public boolean isExecutableAsConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Adds an enchantment to the item in your hand";
	}
}