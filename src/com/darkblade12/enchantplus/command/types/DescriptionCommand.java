package com.darkblade12.enchantplus.command.types;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.permission.Permission;

public final class DescriptionCommand extends AbstractCommand<EnchantPlus> {
	@Override
	public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
		Enchantment enchantment = plugin.getSettings().getEnchantment(parameters[0]);
		if (enchantment == null) {
			handler.displayPluginMessage(sender, "§cAn enchantment with this identifier doesn't exist!");
			return;
		}
		handler.displayPluginMessage(sender, "§aDescription of §2" + EnchantmentInformation.getMinecraftName(enchantment) + "§a:" + getDescriptionString(enchantment));
	}

	public static String getDescriptionString(Enchantment enchantment) {
		StringBuilder builder = new StringBuilder();
		for (String line : EnchantmentInformation.getDescription(enchantment)) {
			builder.append("\n§r §7\u25BB §e" + line);
		}
		return builder.toString();
	}

	@Override
	public String getName() {
		return "description";
	}

	@Override
	public String[] getParameters() {
		return new String[] { "<name/id>" };
	}

	@Override
	public Permission getPermission() {
		return Permission.DESCRIPTION_COMMAND;
	}

	@Override
	public String getDescription() {
		return "Displays a detailed description of an enchantment";
	}
}