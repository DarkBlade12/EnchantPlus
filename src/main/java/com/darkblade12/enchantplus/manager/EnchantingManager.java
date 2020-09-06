package com.darkblade12.enchantplus.manager;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.permission.Permission;
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
    public void onEnable() throws Exception {
        registerEvents();
    }

    @Override
    public void onDisable() {
        unregisterAll();
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
            if (!map.contains(enchantment) || map.getLevel(enchantment) < settings.getLevelLimitAmount(player, enchantment)) {
                if (settings.isMultipleEnchantingConflictingEnabled() || Permission.CONFLICTING_BYPASS.has(player) || !map
                        .isConflicting(enchantment)) {
                    remaining.add(enchantment);
                }
            }
        }
        return remaining;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        Settings settings = plugin.getSettings();
        ItemStack item = event.getItem();
        boolean permission = !settings.isMultipleEnchantingPermissionEnabled() || Permission.MULTIPLE_MECHANIC.has(player);
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
                    additions.put(enchantment, limit);
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
        int size = remaining.size();
        for (int index = 0; index < Math.min(size, 3); index++) {
            Enchantment enchantment = remaining.get(index);
            int level = index == 0 ? 1 : 0;
            int limit = settings.getLevelLimitAmount(player, enchantment);
            while (RANDOM.nextDouble() < (level >= 2 ? 0.3 : 0.5) && level < limit) {
                level++;
            }
            if (level > 0) {
                map.put(enchantment, level, player, settings);
            }
        }
        Enchanter.forItemStack(item).addEnchantments(map);
        if (player.getGameMode() != GameMode.CREATIVE) {
            EnchantingInventory inv = (EnchantingInventory) event.getInventory();
            ItemStack lapis = inv.getSecondary();
            lapis.setAmount(lapis.getAmount() - (event.whichButton() + 1));
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
        if (cursor.getType() == Material.AIR) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot < 0 || slot > 1 || slot == 0 && inventory.getItem(1) == null || slot == 1 && inventory.getItem(0) == null) {
            return;
        }
        final EnchantmentMap map = EnchantmentMap.fromItemStack(slot == 0 ? inventory.getItem(1) : cursor);
        if (map.isEmpty()) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack item = inventory.getItem(2);
                if (item == null) {
                    return;
                }
                Enchanter enchanter = Enchanter.forItemStack(item);
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