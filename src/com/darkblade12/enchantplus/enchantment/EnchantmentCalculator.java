package com.darkblade12.enchantplus.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.enchantplus.Settings;

public final class EnchantmentCalculator {
	private final Settings settings;

	public EnchantmentCalculator(Settings settings) {
		this.settings = settings;
	}

	private int getRawCost(Player player, Enchantment enchantment, int level) {
		int[] amounts = settings.getLevelCostAmounts(player, enchantment);
		return level == 0 ? 0 : amounts[0] + amounts[1] * (level - 1);
	}

	public int getCost(Player player, ItemStack item, Enchantment enchantment, int level) throws IllegalArgumentException {
		EnchantmentMap map = EnchantmentMap.fromItemStack(item);
		int currentLevel = map.containsKey(enchantment) ? map.get(enchantment) : 0;
		if (currentLevel == level) {
			throw new IllegalArgumentException("Current level equals enchanting level");
		}
		if (currentLevel < level) {
			return getRawCost(player, enchantment, level) - getRawCost(player, enchantment, currentLevel);
		}
		int refundAmount = settings.getLevelRefundAmount(enchantment);
		return settings.isLevelRefundEnabled() ? -(refundAmount == 0 ? Math.abs(getRawCost(player, enchantment, currentLevel) - getRawCost(player, enchantment, level)) : refundAmount * currentLevel) : 0;
	}

	public boolean transact(Player player, ItemStack item, Enchantment enchantment, int level) {
		int cost = getCost(player, item, enchantment, level);
		int playerLevel = player.getLevel();
		if (playerLevel < cost) {
			return false;
		}
		player.setLevel(playerLevel - cost);
		return true;
	}

	public void refund(Player player, ItemStack item, Enchantment enchantment) {
		transact(player, item, enchantment, 0);
	}
}