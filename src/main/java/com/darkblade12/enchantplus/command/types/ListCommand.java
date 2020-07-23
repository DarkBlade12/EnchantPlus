package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ListCommand extends AbstractCommand<EnchantPlus> {
    @Override
    public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
        handler.displayPluginMessage(sender, "§bAll enchantments:" + getListString(Arrays.asList(Enchantment.values()), plugin.getSettings()));
    }

    public static String getListString(Collection<Enchantment> collection, Settings settings) throws IllegalArgumentException {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException("Collection cannot be empty");
        }
        StringBuilder builder = new StringBuilder();
        List<Enchantment> enchantments = new ArrayList<>(collection);
        enchantments.sort(Comparator.comparing(a -> a.getKey().getKey()));
        for (Enchantment enchantment : enchantments) {
            builder.append("\n§r §3\u25AA §7(§c").append(enchantment.getKey().getKey()).append("§7) ");
            int amount = 0;
            for (String name : settings.getNames(enchantment)) {
                if (amount != 0) {
                    builder.append("§2 / ");
                }
                builder.append("§a").append(name);
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
        return "Displays a list of all enchantments with their original and display name";
    }
}