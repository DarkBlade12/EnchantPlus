package com.darkblade12.enchantplus.enchantment.enchanter.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;

public final class BookEnchanter extends Enchanter {
	private boolean enchanted;

	public BookEnchanter(ItemStack item) {
		super(item);
		enchanted = item.getType() == Material.ENCHANTED_BOOK;
	}

	@Override
	protected void addItemEnchantment(Enchantment enchantment, int level) {
		if (!enchanted) {
			item.setType(Material.ENCHANTED_BOOK);
			enchanted = true;
		}
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment, level, true);
		item.setItemMeta(meta);
	}

	@Override
	protected void removeItemEnchantment(Enchantment enchantment) {
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.removeStoredEnchant(enchantment);
		item.setItemMeta(meta);
		if (enchanted && !hasEnchantments()) {
			item.setType(Material.BOOK);
			enchanted = false;
		}
	}

	public boolean isEnchanted() {
		return enchanted;
	}
}