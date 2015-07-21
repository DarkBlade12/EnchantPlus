package com.darkblade12.enchantplus.manager.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Settings;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.enchantment.EnchantmentTarget;
import com.darkblade12.enchantplus.enchantment.enchanter.Enchanter;
import com.darkblade12.enchantplus.manager.Manager;
import com.darkblade12.enchantplus.permission.Permission;

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
		int amount = RANDOM.nextInt(8) + 1 + (bonus >> 1) + RANDOM.nextInt(bonus + 1);
		return slot == 0 ? Math.max(amount / 3, 1) : (slot == 1 ? amount * 2 / 3 + 1 : Math.max(amount, bonus * 2));
	}

	private List<Enchantment> getRemainingEnchantments(Player player, ItemStack item) {
		List<Enchantment> remaining = new ArrayList<Enchantment>();
		EnchantmentMap map = EnchantmentMap.fromItemStack(item);
		Settings settings = plugin.getSettings();
		for (Enchantment enchantment : EnchantmentTarget.fromItemStack(item).getEnchantments()) {
			if (!map.containsKey(enchantment) || map.get(enchantment) < settings.getLevelLimitAmount(player, enchantment)) {
				if (settings.isMultipleEnchantingConflictingEnabled() || Permission.CONFLICTING_BYPASS.has(player) || !map.conflictsWith(enchantment)) {
					remaining.add(enchantment);
				}
			}
		}
		return remaining;
	}

	private boolean hasRemainingEnchantments(Player player, ItemStack item) {
		return getRemainingEnchantments(player, item).size() > 0;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
		Player player = event.getEnchanter();
		Settings settings = plugin.getSettings();
		ItemStack item = event.getItem();
		boolean permission = settings.isMultipleEnchantingPermissionEnabled() ? Permission.MULTIPLE_MECHANIC.has(player) : true;
		if (!settings.isMultipleEnchantingEnabled() || !EnchantmentTarget.isEnchantable(item) || !hasRemainingEnchantments(player, item) || !permission) {
			return;
		}
		event.setCancelled(false);
		int[] costs = event.getExpLevelCostsOffered();
		if (item.getType() == Material.ENCHANTED_BOOK) {
			int bonus = event.getEnchantmentBonus();
			for (int index = 0; index < costs.length; index++) {
				costs[index] = getEnchantingLevel(index, bonus);
			}
		}
		if (!settings.isLevelCostIncreaseEnabled()) {
			return;
		}
		EnchantmentMap map = EnchantmentMap.fromItemStack(item);
		if (map.isEmpty()) {
			return;
		}
		int amount = map.size() * settings.getLevelCostIncreaseAmount();
		for (int index = 0; index < costs.length; index++) {
			costs[index] += amount;
		}
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
		boolean creative = player.getGameMode() == GameMode.CREATIVE;
		if (!creative) {
			player.setLevel(player.getLevel() - event.getExpLevelCost());
		}
		List<Enchantment> remaining = getRemainingEnchantments(player, item);
		Collections.shuffle(remaining);
		int size = remaining.size();
		for (int index = 0; index < (size < 3 ? size : 3); index++) {
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
		Inventory inventory = event.getInventory();
		inventory.setItem(0, item);
		if (!creative) {
			int amount = event.whichButton() + 1;
			ItemStack lapis = inventory.getItem(1);
			int remainingAmount = lapis.getAmount() - amount;
			if (remainingAmount == 0) {
				inventory.setItem(1, null);
			} else {
				lapis.setAmount(remainingAmount);
			}
		}
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
				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
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