package com.darkblade12.enchantplus.enchantment;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.settings.Settings;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.plugin.Manager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public final class EnchantingManager extends Manager<EnchantPlus> {
    private static final Random RANDOM = new Random();

    public EnchantingManager(EnchantPlus plugin) {
        super(plugin);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }


    private int getEnchantingLevel(int slot, int bonus) {
        if (bonus > 15) {
            bonus = 15;
        }

        int base = RANDOM.nextInt(8) + 1 + (bonus >> 1) + RANDOM.nextInt(bonus + 1);
        switch (slot) {
            case 1:
                return base * 2 / 3 + 1;
            case 2:
                return Math.max(base, bonus * 2);
            default:
                return Math.max(base / 3, 1);
        }
    }

    private List<Enchantment> getRemainingEnchantments(Player player, ItemStack item) {
        List<Enchantment> remaining = new ArrayList<>();
        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        Settings settings = plugin.getSettings();

        for (Enchantment enchantment : EnchantmentMap.getApplicableEnchantments(item)) {
            if (map.contains(enchantment) && map.getLevel(enchantment) >= settings.getLevelLimitAmount(player, enchantment)) {
                continue;
            }

            if (!settings.isMultipleEnchantingConflictingEnabled() && !Permission.BYPASS_CONFLICTING.test(player) && map
                    .isConflicting(enchantment)) {
                continue;
            }

            remaining.add(enchantment);
        }

        return remaining;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        Settings settings = plugin.getSettings();
        ItemStack item = event.getItem();
        boolean permission = !settings.isMultipleEnchantingPermissionEnabled() || Permission.MULTIPLE_ENCHANTING.test(player);
        if (!settings.isMultipleEnchantingEnabled() || !permission || !EnchantmentMap.isEnchantable(item) ||
            !EnchantmentMap.hasEnchantments(item)) {
            return;
        }

        List<Enchantment> remaining = getRemainingEnchantments(player, item);
        if (remaining.isEmpty()) {
            return;
        }

        event.setCancelled(false);
        EnchantmentOffer[] offers = event.getOffers();
        int bonus = event.getEnchantmentBonus();
        int offerCount = remaining.size();
        if (offerCount > 3) {
            offerCount = 3;
        }

        int[] costs = new int[offerCount];
        for (int index = 0; index < offerCount; index++) {
            costs[index] = getEnchantingLevel(index, bonus);
        }

        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        if (settings.isLevelCostIncreaseEnabled() && !map.isEmpty()) {
            int costIncrease = map.size() * settings.getLevelCostIncreaseAmount();
            for (int index = 0; index < offerCount; index++) {
                costs[index] += costIncrease;
            }
        }

        for (int index = 0; index < offerCount; index++) {
            offers[index] = new EnchantmentOffer(remaining.get(index), 1, costs[index]);
        }

        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        Settings settings = plugin.getSettings();
        if (map.isEmpty()) {
            Map<Enchantment, Integer> additions = event.getEnchantsToAdd();
            for (Entry<Enchantment, Integer> entry : additions.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int limit = settings.getLevelLimitAmount(player, enchantment);
                if (entry.getValue() > limit) {
                    entry.setValue(limit);
                }
            }
            return;
        }

        event.setCancelled(true);
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setLevel(player.getLevel() - event.getExpLevelCost());
        }

        List<Enchantment> remaining = getRemainingEnchantments(player, item);
        Collections.shuffle(remaining);
        int maxOffers = Math.min(remaining.size(), 3);
        for (int i = 0; i < maxOffers; i++) {
            Enchantment enchant = remaining.get(i);
            int level = i == 0 ? 1 : 0;
            int limit = settings.getLevelLimitAmount(player, enchant);
            while (RANDOM.nextDouble() < (level >= 2 ? 0.3 : 0.5) && level < limit) {
                level++;
            }

            if (level > 0) {
                map.put(enchant, level, player, settings);
            }
        }

        Enchanter.forItemStack(item).addEnchantments(map);
        if (player.getGameMode() != GameMode.CREATIVE) {
            EnchantingInventory inv = (EnchantingInventory) event.getInventory();
            ItemStack lapis = inv.getSecondary();
            if (lapis != null) {
                lapis.setAmount(lapis.getAmount() - (event.whichButton() + 1));
            }
        }

        event.getInventory().setItem(0, item);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getView().getTopInventory();
        if (inventory.getType() != InventoryType.ANVIL) {
            return;
        }

        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot > 1 || slot == 0 && inventory.getItem(1) == null || slot == 1 && inventory.getItem(0) == null) {
            return;
        }

        ItemStack source = slot == 0 ? inventory.getItem(1) : cursor;
        if (source == null) {
            return;
        }

        final EnchantmentMap map = EnchantmentMap.fromItemStack(source);
        if (map.isEmpty()) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack target = inventory.getItem(2);
                if (target == null) {
                    return;
                }

                Enchanter enchanter = Enchanter.forItemStack(target);
                for (Entry<Enchantment, Integer> entry : map) {
                    Enchantment enchantment = entry.getKey();
                    int level = entry.getValue();
                    if (level > enchantment.getMaxLevel()) {
                        enchanter.addEnchantment(enchantment, level);
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }
}
