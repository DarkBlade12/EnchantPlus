package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ApplicableCommand extends AbstractCommand<EnchantPlus> {
    @Override
    public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            handler.displayPluginMessage(sender, "§cYou have to hold an item in your hand!");
            return;
        }
        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        List<Enchantment> applicable = new ArrayList<>();
        for (Enchantment enchantment : EnchantmentMap.getApplicableEnchantments(item)) {
            if (!map.isConflicting(enchantment)) {
                applicable.add(enchantment);
            }
        }
        if (applicable.isEmpty()) {
            handler.displayPluginMessage(sender, "§cThis item doesn't have any applicable enchantments!");
            return;
        }
        handler.displayPluginMessage(sender, "§aApplicable enchantments:" + ListCommand.getListString(applicable, plugin.getSettings()));
    }

    @Override
    public String getName() {
        return "applicable";
    }

    @Override
    public Permission getPermission() {
        return Permission.APPLICABLE_COMMAND;
    }

    @Override
    public boolean isExecutableAsConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Displays a list of enchantments that can be applied to the item in your hand";
    }
}