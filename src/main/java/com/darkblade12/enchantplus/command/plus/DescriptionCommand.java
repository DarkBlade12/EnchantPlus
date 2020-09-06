package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class DescriptionCommand extends CommandBase<EnchantPlus> {
    public DescriptionCommand() {
        super("description", Permission.COMMAND_DESCRIPTION, "<name>");
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        Enchantment enchant = plugin.getSettings().getEnchantment(name);
        if (enchant == null) {
            plugin.sendMessage(sender, "enchanting.enchantmentNotFound", name);
            return;
        }
        name = EnchantmentInformation.getMinecraftName(enchant);

        StringBuilder builder = new StringBuilder();
        for (String line : EnchantmentInformation.getDescription(enchant)) {
            builder.append('\n').append(plugin.formatMessage("command.plus.description.line", line));
        }

        plugin.sendMessage(sender, "command.plus.description.message", name, builder.toString());
    }

    @Override
    public List<String> getSuggestions(EnchantPlus plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? Arrays.stream(Enchantment.values()).map(e -> e.getKey().getKey()).collect(Collectors.toList()) : null;
    }
}
