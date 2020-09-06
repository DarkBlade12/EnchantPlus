package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.settings.Settings;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
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

public final class AddCommand extends CommandBase<EnchantPlus> {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public AddCommand() {
        super("add", false, Permission.COMMAND_ADD, "<name>", "<level|natural>");
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

        if (settings.isManualEnchantingAmountEnabled() && item.getAmount() > 1 && !Permission.BYPASS_AMOUNT.test(sender)) {
            plugin.sendMessage(sender, "command.plus.add.noMultiple");
            return;
        }

        String name = args[0];
        Enchantment enchant = settings.getEnchantment(name);
        if (enchant == null) {
            plugin.sendMessage(sender, "enchanting.enchantmentNotFound", name);
            return;
        }
        name = EnchantmentInformation.getMinecraftName(enchant);

        if (settings.isManualEnchantingInapplicableEnabled() && !enchant.canEnchantItem(item) &&
            !Permission.BYPASS_INAPPLICABLE.test(sender)) {
            plugin.sendMessage(sender, "command.plus.add.inapplicable", name);
            return;
        }

        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        if (settings.isManualEnchantingConflictingEnabled() && map.isConflicting(enchant) && !Permission.BYPASS_CONFLICTING.test(sender)) {
            plugin.sendMessage(sender, "command.plus.add.conflicting", name);
            return;
        }

        String value = args[1];
        int level;
        if (value.equalsIgnoreCase("natural")) {
            level = enchant.getMaxLevel();
        } else {
            if (!NUMBER_PATTERN.matcher(value).matches()) {
                plugin.sendMessage(sender, "enchanting.level.noNumber", value);
                return;
            }

            try {
                level = Short.parseShort(value);
            } catch (NumberFormatException e) {
                plugin.sendMessage(sender, "enchanting.level.notHigherThan", Short.MAX_VALUE);
                return;
            }

            if (level < 1) {
                plugin.sendMessage(sender, "enchanting.level.notLowerThan", 1);
                return;
            }
        }

        int restriction = settings.getLevelRestrictionAmount(player, enchant);
        if (level > restriction) {
            plugin.sendMessage(sender, "enchanting.level.notHigherThanEnchantment", name, restriction);
            return;
        }

        if (map.contains(enchant, level)) {
            plugin.sendMessage(sender, "command.plus.add.alreadyApplied", name, level);
            return;
        }

        if (!settings.hasPowerSource(player)) {
            plugin.sendMessage(sender, "enchanting.noPowerSource");
            return;
        }

        if (!settings.withdrawLevels(player, item, enchant, level)) {
            int cost = settings.getCost(player, item, enchant, level);
            plugin.sendMessage(sender, "command.plus.add.notEnoughLevels", cost, name, level);
            return;
        }

        Enchanter.forItemStack(item).addEnchantment(enchant, level);
        plugin.sendMessage(sender, "command.plus.add.succeeded", name, level);
    }

    @Override
    public List<String> getSuggestions(EnchantPlus plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.stream(Enchantment.values()).map(e -> e.getKey().getKey()).collect(Collectors.toList());
            case 2:
                return Arrays.asList("natural", "1", "2", "3", "4", "5");
            default:
                return null;
        }
    }
}
