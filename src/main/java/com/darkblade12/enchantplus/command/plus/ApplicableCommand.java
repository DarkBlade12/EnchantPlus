package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public final class ApplicableCommand extends CommandBase<EnchantPlus> {
    public ApplicableCommand() {
        super("applicable", false, Permission.COMMAND_APPLICABLE);
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.sendMessage(sender, "enchanting.noItem");
            return;
        }

        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        List<Enchantment> applicable = EnchantmentMap.getApplicableEnchantments(item).stream().filter(e -> !map.isConflicting(e))
                                                     .collect(Collectors.toList());
        if (applicable.isEmpty()) {
            plugin.sendMessage(sender, "enchanting.noApplicable");
            return;
        }

        String list = plugin.getSettings().getEnchantmentList(applicable, "command.plus.applicable.line");
        plugin.sendMessage(sender, "command.plus.applicable.message", list);
    }
}
