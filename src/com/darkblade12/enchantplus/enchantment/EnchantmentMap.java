package com.darkblade12.enchantplus.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;

public final class EnchantmentMap extends HashMap<Enchantment, Integer> {
	private static final long serialVersionUID = -1098212004496712186L;

	public EnchantmentMap() {
		super();
	}

	public EnchantmentMap(Map<Enchantment, Integer> map) {
		super(map);
	}

	public static EnchantmentMap fromItemStack(ItemStack item) {
		return new EnchantmentMap(item.getType() == Material.ENCHANTED_BOOK ? ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants() : item.getEnchantments());
	}

	public static boolean hasEnchantments(ItemStack item) {
		return !fromItemStack(item).isEmpty();
	}

	public static boolean hasEnchantment(ItemStack item, Enchantment enchantment, int level) {
		return fromItemStack(item).hasEnchantment(enchantment, level);
	}

	public static boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
		return fromItemStack(item).containsKey(enchantment);
	}
	
	public static boolean isEnchantmentApplicable(ItemStack item, Enchantment enchant) {
		Material mat = item.getType();
		return mat == Material.BOOK  || mat == Material.ENCHANTED_BOOK || enchant.canEnchantItem(item);
	}
	
	public static List<Enchantment> getApplicableEnchantments(ItemStack item) {
		List<Enchantment> applicable = new ArrayList<Enchantment>();
		for(Enchantment enchant : Enchantment.values()) {
			if(!enchant.isTreasure() && isEnchantmentApplicable(item, enchant)) {
				applicable.add(enchant);
			}
		}
		return applicable;
	}
	
	public static boolean isEnchantable(ItemStack item) {
		return !getApplicableEnchantments(item).isEmpty();
	}

	public Integer put(Enchantment key, Integer value, Player player, Settings settings) {
		if (settings != null) {
			if (settings.isLevelStackingEnabled(key) && containsKey(key)) {
				value += get(key);
			}
			int amount = settings.getLevelLimitAmount(player, key);
			if (value > amount) {
				value = amount;
			}
		}
		return super.put(key, value);
	}

	public void putAll(Map<? extends Enchantment, ? extends Integer> map, Player player, Settings settings) {
		for (Entry<? extends Enchantment, ? extends Integer> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue(), player, settings);
		}
	}

	public void addEnchantments(ItemStack item) {
		Enchanter.forItemStack(item).addEnchantments(this);
	}

	public boolean conflictsWith(Enchantment enchantment) {
		for(Enchantment itemEnchant : keySet()) {
			if(itemEnchant != enchantment && enchantment.conflictsWith(itemEnchant)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasEnchantment(Enchantment enchantment, int level) {
		return containsKey(enchantment) ? get(enchantment) == level : false;
	}

	public List<Enchantment> getConflicting(Enchantment enchantment) {
		List<Enchantment> conflicting = new ArrayList<Enchantment>();
		for(Enchantment itemEnchant : keySet()) {
			if(itemEnchant.conflictsWith(enchantment)) {
				conflicting.add(itemEnchant);
			}
		}
		return conflicting;
	}
}