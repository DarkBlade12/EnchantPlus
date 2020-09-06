package com.darkblade12.enchantplus.enchantment.enchanter;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class CommonEnchanter extends Enchanter {
    public CommonEnchanter(ItemStack item) {
        super(item);
    }

    @Override
    protected void addItemEnchantment(Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
    }

    @Override
    protected void removeItemEnchantment(Enchantment enchant) {
        item.removeEnchantment(enchant);
    }
}
