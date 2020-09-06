package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MightyCommand extends CommandBase<EnchantPlus> {
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public MightyCommand() {
        super("mighty", false, Permission.COMMAND_MIGHTY, "<level|natural>", "[applicable]");
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (!plugin.getSettings().isManualEnchantingEnabled()) {
            plugin.sendMessage(sender, "enchanting.commandsDisabled");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.sendMessage(sender, "enchanting.noItem");
            return;
        }


        boolean applicable = false;
        if (args.length == 2) {
            String applicableValue = args[1];
            if (!BOOLEAN_PATTERN.matcher(applicableValue).matches()) {
                plugin.sendMessage(sender, "command.plus.mighty.noBoolean", applicableValue);
                return;
            }

            applicable = Boolean.parseBoolean(applicableValue);
            if (applicable && !EnchantmentMap.isEnchantable(item)) {
                plugin.sendMessage(sender, "enchanting.noApplicable");
                return;
            }
        }

        Enchanter enchanter = Enchanter.forItemStack(item);
        List<Enchantment> enchants;
        if (applicable) {
            enchants = EnchantmentMap.getApplicableEnchantments(item);
        } else {
            enchants = Arrays.stream(Enchantment.values()).filter(e -> !e.isTreasure()).collect(Collectors.toList());
        }

        String levelValue = args[0];
        if (levelValue.equalsIgnoreCase("natural")) {
            enchanter.clearEnchantments();
            enchanter.addAllEnchantments(enchants);
            plugin.sendMessage(sender, "command.plus.mighty." + (applicable ? "applicable" : "all") + "Natural");
            return;
        }

        if (!NUMBER_PATTERN.matcher(levelValue).matches()) {
            plugin.sendMessage(sender, "enchanting.level.noNumber", levelValue);
            return;
        }

        int level;
        try {
            level = Short.parseShort(levelValue);
        } catch (Exception exception) {
            plugin.sendMessage(sender, "enchanting.level.notHigherThan", Short.MAX_VALUE);
            return;
        }

        if (level < 1) {
            plugin.sendMessage(sender, "enchanting.level.notLowerThan", 1);
            return;
        }

        enchanter.clearEnchantments();
        enchanter.addAllEnchantments(enchants, level);
        plugin.sendMessage(sender, "command.plus.mighty." + (applicable ? "applicable" : "all") + "Level", level);
    }

    @Override
    public List<String> getSuggestions(EnchantPlus plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("natural", "1", "2", "3", "4", "5");
            case 2:
                return Arrays.asList("true", "false");
            default:
                return null;
        }
    }
}
