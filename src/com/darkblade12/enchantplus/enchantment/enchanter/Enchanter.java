package com.darkblade12.enchantplus.enchantment.enchanter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.enchanter.types.BookEnchanter;
import com.darkblade12.enchantplus.enchantment.enchanter.types.NormalEnchanter;

public abstract class Enchanter {
	protected final ItemStack item;
	protected EnchantmentMap enchantments;

	public Enchanter(ItemStack item) {
		this.item = item;
		enchantments = EnchantmentMap.fromItemStack(item);
	}

	public static Enchanter forItemStack(ItemStack item) {
		Material material = item.getType();
		return material == Material.BOOK || material == Material.ENCHANTED_BOOK ? new BookEnchanter(item) : new NormalEnchanter(item);
	}

	protected abstract void addItemEnchantment(Enchantment enchantment, int level);

	public final void addEnchantment(Enchantment enchantment, int level) {
		enchantments.put(enchantment, level);
		addItemEnchantment(enchantment, enchantments.get(enchantment));
	}

	public final void addEnchantments(Map<Enchantment, Integer> map) {
		for (Entry<Enchantment, Integer> entry : map.entrySet()) {
			addEnchantment(entry.getKey(), entry.getValue());
		}
	}

	public final void addAllEnchantments(Collection<Enchantment> collection, int level) {
		for (Enchantment enchantment : collection) {
			addEnchantment(enchantment, level == 0 ? enchantment.getMaxLevel() : level);
		}
	}

	public final void addAllEnchantments(Collection<Enchantment> collection) {
		addAllEnchantments(collection, 0);
	}

	protected abstract void removeItemEnchantment(Enchantment enchantment);

	public final void removeEnchantment(Enchantment enchantment) {
		enchantments.remove(enchantment);
		removeItemEnchantment(enchantment);
	}

	public final void removeEnchantments(Collection<Enchantment> collection) {
		List<Enchantment> list = new ArrayList<Enchantment>(collection);
		for (int index = 0; index < list.size(); index++) {
			removeEnchantment(list.get(index));
		}
	}

	public final void removeEnchantments(Enchantment... enchantments) {
		removeEnchantments(Arrays.asList(enchantments));
	}

	public final void removeAllEnchantments() {
		removeEnchantments(enchantments.keySet());
	}

	public final ItemStack getItem() {
		return item;
	}

	public final Map<Enchantment, Integer> getEnchantments() {
		return Collections.unmodifiableMap(enchantments);
	}

	public final boolean hasEnchantments() {
		return !enchantments.isEmpty();
	}
}