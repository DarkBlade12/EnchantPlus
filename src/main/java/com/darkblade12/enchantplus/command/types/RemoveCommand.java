package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.permission.Permission;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class RemoveCommand extends AbstractCommand<EnchantPlus> {
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
        Enchantment enchantment = settings.getEnchantment(parameters[0]);
        if (enchantment == null) {
            handler.displayPluginMessage(sender, "§cAn enchantment with this identifier doesn't exist!");
            return;
        }
        if (!EnchantmentMap.fromItemStack(item).containsKey(enchantment)) {
            handler.displayPluginMessage(sender, "§cThis enchantment isn't applied to the item in your hand!");
            return;
        }
        if (!AddCommand.hasPowerSource(player, plugin.getSettings())) {
            handler.displayPluginMessage(sender, "§cYou have to be near a power source (enchantment table) to be able to execute this command!");
            return;
        }
        plugin.getCalculator().refund(player, item, enchantment);
        Enchanter.forItemStack(item).removeEnchantment(enchantment);
        handler.displayPluginMessage(sender, "§aThe §6" + EnchantmentInformation.getMinecraftName(enchantment) + " §aenchantment was removed from the item in your hand.");
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String[] getParameters() {
        return new String[] { "<name>" };
    }

    @Override
    public Permission getPermission() {
        return Permission.REMOVE_COMMAND;
    }

    @Override
    public boolean isExecutableAsConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Removes an enchantment from the item in your hand";
    }
}