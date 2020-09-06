package com.darkblade12.enchantplus.enchantment.enchanter;

import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Enchanter {
    protected final ItemStack item;
    protected final EnchantmentMap enchantments;

    protected Enchanter(ItemStack item) {
        this.item = item;
        enchantments = EnchantmentMap.fromItemStack(item);
    }

    public static Enchanter forItemStack(ItemStack item) {
        switch (item.getType()) {
            case BOOK:
            case ENCHANTED_BOOK:
                return new BookEnchanter(item);
            default:
                return new CommonEnchanter(item);
        }
    }

    protected abstract void addItemEnchantment(Enchantment enchant, int level);

    public final void addEnchantment(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        addItemEnchantment(enchant, enchantments.getLevel(enchant));
    }

    public final void addEnchantments(Iterable<Map.Entry<Enchantment, Integer>> enchantments) {
        enchantments.forEach(e -> addEnchantment(e.getKey(), e.getValue()));
    }

    public final void addAllEnchantments(Collection<Enchantment> enchantments, int level) {
        for (Enchantment enchant : enchantments) {
            addEnchantment(enchant, level == 0 ? enchant.getMaxLevel() : level);
        }
    }

    public final void addAllEnchantments(Collection<Enchantment> enchantments) {
        addAllEnchantments(enchantments, 0);
    }

    protected abstract void removeItemEnchantment(Enchantment enchant);

    public final void removeEnchantment(Enchantment enchant) {
        enchantments.remove(enchant);
        removeItemEnchantment(enchant);
    }

    public final void clearEnchantments() {
        Iterator<Map.Entry<Enchantment, Integer>> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            removeItemEnchantment(iterator.next().getKey());
            iterator.remove();
        }
    }

    public final ItemStack getItem() {
        return item;
    }

    public final Set<Enchantment> getEnchantments() {
        return enchantments.getEnchantments();
    }

    public final boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }
}
