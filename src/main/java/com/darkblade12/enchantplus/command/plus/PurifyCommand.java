package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.settings.Settings;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PurifyCommand extends CommandBase<EnchantPlus> {
    public PurifyCommand() {
        super("purify", Permission.COMMAND_PURIFY);
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Settings settings = plugin.getSettings();
        if (!settings.isManualEnchantingEnabled()) {
            plugin.sendMessage(sender, "enchanting.commandsDisabled");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.sendMessage(sender, "enchanting.noItem");
            return;
        }

        if (!EnchantmentMap.hasEnchantments(item)) {
            plugin.sendMessage(sender, "command.plus.purify.noEnchantments");
            return;
        }

        if (!settings.hasPowerSource(player)) {
            plugin.sendMessage(sender, "enchanting.noPowerSource");
            return;
        }

        Enchanter enchanter = Enchanter.forItemStack(item);
        for (Enchantment enchant : enchanter.getEnchantments()) {
            settings.refundLevels(player, item, enchant);
        }

        enchanter.clearEnchantments();
        plugin.sendMessage(sender, "command.plus.purify.succeeded");
    }
}
