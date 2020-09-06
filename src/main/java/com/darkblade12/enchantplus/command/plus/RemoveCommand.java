package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import com.darkblade12.enchantplus.settings.Settings;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public final class RemoveCommand extends CommandBase<EnchantPlus> {
    public RemoveCommand() {
        super("remove", false, Permission.COMMAND_REMOVE, "<name>");
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

        String name = args[0];
        Enchantment enchant = settings.getEnchantment(name);
        if (enchant == null) {
            plugin.sendMessage(sender, "enchanting.enchantmentNotFound", name);
            return;
        }
        name = EnchantmentInformation.getMinecraftName(enchant);

        if (!EnchantmentMap.hasEnchantment(item, enchant)) {
            plugin.sendMessage(sender, "command.plus.remove.notApplied", name);
            return;
        }

        if (!plugin.getSettings().hasPowerSource(player)) {
            plugin.sendMessage(sender, "enchanting.noPowerSource");
            return;
        }

        settings.refundLevels(player, item, enchant);
        Enchanter.forItemStack(item).removeEnchantment(enchant);
        plugin.sendMessage(sender, "command.plus.remove.succeeded", name);
    }

    @Override
    public List<String> getSuggestions(EnchantPlus plugin, CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }

        ItemStack item = sender instanceof Player ? ((Player) sender).getInventory().getItemInMainHand() : null;
        if (item == null || item.getType() == Material.AIR) {
            return EnchantmentInformation.getNames();
        }

        return EnchantmentMap.fromItemStack(item).getEnchantments().stream().map(EnchantmentInformation::getName)
                             .collect(Collectors.toList());
    }
}
