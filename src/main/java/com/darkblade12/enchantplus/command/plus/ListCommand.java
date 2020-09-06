package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;

public final class ListCommand extends CommandBase<EnchantPlus> {
    public ListCommand() {
        super("list", Permission.COMMAND_LIST);
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        String list = plugin.getSettings().getEnchantmentList(Arrays.asList(Enchantment.values()), "command.plus.list.line");
        plugin.sendMessage(sender, "command.plus.list.message", list);
    }
}
