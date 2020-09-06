package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class MightyCommand extends AbstractCommand<EnchantPlus> {
    private static final Pattern BOOLEAN = Pattern.compile("[tT][rR][uU][eE]|[fF][aA][lL][sS][eE]");
    private static final Pattern NUMBER = Pattern.compile("\\d+");

    @Override
    public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
        Player player = (Player) sender;
        if (!plugin.getSettings().isManualEnchantingEnabled()) {
            handler.displayPluginMessage(sender, "§cEnchanting commands are currently disabled!");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            handler.displayPluginMessage(sender, "§cYou have to hold an item in your hand!");
            return;
        }
        String parameter = parameters[0];
        boolean applicable = false;
        if (parameters.length == 2) {
            String value = parameters[1];
            if (!BOOLEAN.matcher(value).matches()) {
                handler.displayPluginMessage(sender, "§6" + value + " §cisn't a boolean value! (true/false)");
                return;
            }
            applicable = Boolean.parseBoolean(value);
            if (applicable && !EnchantmentMap.isEnchantable(item)) {
                handler.displayPluginMessage(sender, "§cThe item in your hand doesn't have any applicable enchantments!");
                return;
            }
        }
        Enchanter enchanter = Enchanter.forItemStack(item);
        List<Enchantment> enchants;
        if (applicable) {
            enchants = EnchantmentMap.getApplicableEnchantments(item);
        } else {
            enchants = new ArrayList<>();
            for (Enchantment enchant : Enchantment.values()) {
                if (!enchant.isTreasure()) {
                    enchants.add(enchant);
                }
            }
        }
        if (parameter.equalsIgnoreCase("natural")) {
            enchanter.clearEnchantments();
            enchanter.addAllEnchantments(enchants);
            handler.displayPluginMessage(sender, "§aThe item in your hand is now mighty and has all" + (applicable ? " applicable" : "") + " enchantments at their natural level limit.");
            return;
        }
        short level;
        if (!NUMBER.matcher(parameter).matches()) {
            handler.displayPluginMessage(sender, "§6" + parameter + " §cisn't a valid parameter!");
            return;
        }
        try {
            level = Short.parseShort(parameter);
        } catch (Exception exception) {
            handler.displayPluginMessage(sender, "§cThe level can't be higher than §6" + Short.MAX_VALUE + "§c!");
            return;
        }
        if (level < 1) {
            handler.displayPluginMessage(sender, "§cThe level can't be lower than 1!");
            return;
        }
        enchanter.clearEnchantments();
        enchanter.addAllEnchantments(enchants, level);
        handler.displayPluginMessage(sender, "§aThe item in your hand is now mighty and has all" + (applicable ? " applicable" : "") + " enchantments at level §6" + level + "§a.");
    }

    @Override
    public String getName() {
        return "mighty";
    }

    @Override
    public String[] getParameters() {
        return new String[] { "<level/natural>", "[applicable]" };
    }

    @Override
    public Permission getPermission() {
        return Permission.MIGHTY_COMMAND;
    }

    @Override
    public boolean isExecutableAsConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Adds all enchantments to the item in your hand";
    }
}