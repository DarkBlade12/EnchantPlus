package com.darkblade12.enchantplus.command.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.permission.Permission;

public final class ListCommand extends AbstractCommand<EnchantPlus> {
	@Override
	public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
		handler.displayPluginMessage(sender, "§bAll enchantments:" + getListString(Arrays.asList(Enchantment.values()), plugin.getSettings()));
	}

	@SuppressWarnings("deprecation")
	public static String getListString(Collection<Enchantment> collection, Settings settings) throws IllegalArgumentException {
		if (collection == null || collection.size() == 0) {
			throw new IllegalArgumentException("Collection cannot be empty");
		}
		StringBuilder builder = new StringBuilder();
		List<Enchantment> enchantments = new ArrayList<Enchantment>(collection);
		Collections.sort(enchantments, new Comparator<Enchantment>() {
			@Override
			public int compare(Enchantment first, Enchantment second) {
				return Integer.compare(first.getId(), second.getId());
			}
		});
		for (Enchantment enchantment : enchantments) {
			builder.append("\n§r §3\u25AA §7(§c" + enchantment.getId() + "§7) ");
			int amount = 0;
			for (String name : settings.getNames(enchantment)) {
				if (amount != 0) {
					builder.append("§2 / ");
				}
				builder.append("§a" + name);
				amount++;
			}
		}
		return builder.toString();
	}

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public Permission getPermission() {
		return Permission.LIST_COMMAND;
	}

	@Override
	public String getDescription() {
		return "Displays a list of all enchantments with their name and id";
	}
}