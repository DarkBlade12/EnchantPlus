package com.darkblade12.enchantplus.enchantment;

import com.darkblade12.enchantplus.settings.Settings;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class EnchantmentMap implements Iterable<Map.Entry<Enchantment, Integer>> {
    private final Map<Enchantment, Integer> enchantments;

    public EnchantmentMap(Map<Enchantment, Integer> enchantments) {
        this.enchantments = new HashMap<>(enchantments);
    }

    public static EnchantmentMap fromItemStack(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            return new EnchantmentMap(((EnchantmentStorageMeta) meta).getStoredEnchants());
        }

        return new EnchantmentMap(item.getEnchantments());
    }

    public static boolean hasEnchantments(ItemStack item) {
        return !fromItemStack(item).isEmpty();
    }

    public static boolean hasEnchantment(ItemStack item, Enchantment enchant) {
        return fromItemStack(item).contains(enchant);
    }

    public static boolean isEnchantmentApplicable(ItemStack item, Enchantment enchant) {
        Material mat = item.getType();
        return mat == Material.BOOK || mat == Material.ENCHANTED_BOOK || enchant.canEnchantItem(item);
    }

    public static List<Enchantment> getApplicableEnchantments(ItemStack item) {
        return Arrays.stream(Enchantment.values()).filter(e -> !e.isTreasure() && isEnchantmentApplicable(item, e))
                     .collect(Collectors.toList());
    }

    public static boolean isEnchantable(ItemStack item) {
        return !getApplicableEnchantments(item).isEmpty();
    }

    public void put(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
    }

    public void put(Enchantment enchant, int level, Player player, Settings settings) {
        if (settings != null) {
            if (settings.isLevelStackingEnabled(enchant) && enchantments.containsKey(enchant)) {
                level += enchantments.get(enchant);
            }

            int levelLimit = settings.getLevelLimitAmount(player, enchant);
            if (level > levelLimit) {
                level = levelLimit;
            }
        }

        enchantments.put(enchant, level);
    }

    public void remove(Enchantment enchant) {
        enchantments.remove(enchant);
    }

    public int getLevel(Enchantment enchant) {
        return enchantments.getOrDefault(enchant, 0);
    }

    public Set<Enchantment> getEnchantments() {
        return enchantments.keySet();
    }

    public boolean isConflicting(Enchantment enchant) {
        return enchantments.keySet().stream().anyMatch(e -> !e.equals(enchant) && e.conflictsWith(enchant));
    }

    public boolean contains(Enchantment enchant) {
        return enchantments.containsKey(enchant);
    }

    public boolean contains(Enchantment enchant, int level) {
        return enchantments.getOrDefault(enchant, 0) == level;
    }

    public int size() {
        return enchantments.size();
    }

    public boolean isEmpty() {
        return enchantments.isEmpty();
    }

    @Override
    public Iterator<Map.Entry<Enchantment, Integer>> iterator() {
        return enchantments.entrySet().iterator();
    }
}
