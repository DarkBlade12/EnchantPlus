package com.darkblade12.enchantplus.enchantment.enchanter.types;

import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class NormalEnchanter extends Enchanter {
    public NormalEnchanter(ItemStack item) {
        super(item);
    }

    @Override
    protected void addItemEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
    }

    @Override
    protected void removeItemEnchantment(Enchantment enchantment) {
        item.removeEnchantment(enchantment);
    }
}