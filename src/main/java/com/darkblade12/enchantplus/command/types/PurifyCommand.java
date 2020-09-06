package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PurifyCommand extends AbstractCommand<EnchantPlus> {
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
        if (!EnchantmentMap.hasEnchantments(item)) {
            handler.displayPluginMessage(sender, "§cNo enchantment is applied to the item in your hand!");
            return;
        }
        if (!AddCommand.hasPowerSource(player, plugin.getSettings())) {
            handler.displayPluginMessage(sender, "§cYou have to be near a power source (enchantment table) to be able to execute this command!");
            return;
        }
        Enchanter enchanter = Enchanter.forItemStack(item);
        for (Enchantment enchantment : enchanter.getEnchantments()) {
            settings.refundLevels(player, item, enchantment);
        }
        enchanter.clearEnchantments();
        handler.displayPluginMessage(sender, "§aAll enchantments were removed from the item in your hand.");
    }

    @Override
    public String getName() {
        return "purify";
    }

    @Override
    public Permission getPermission() {
        return Permission.PURFIY_COMMAND;
    }

    @Override
    public boolean isExecutableAsConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Removes all enchantments from the item in your hand";
    }
}