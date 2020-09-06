package com.darkblade12.enchantplus.enchantment.enchanter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public final class BookEnchanter extends Enchanter {
    public BookEnchanter(ItemStack item) {
        super(item);
    }

    @Override
    protected void addItemEnchantment(Enchantment enchant, int level) {
        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.addStoredEnchant(enchant, level, true);
        item.setItemMeta(meta);
    }

    @Override
    protected void removeItemEnchantment(Enchantment enchant) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.removeStoredEnchant(enchant);
        item.setItemMeta(meta);

        if (item.getType() == Material.ENCHANTED_BOOK && !hasEnchantments()) {
            item.setType(Material.BOOK);
        }
    }
}
