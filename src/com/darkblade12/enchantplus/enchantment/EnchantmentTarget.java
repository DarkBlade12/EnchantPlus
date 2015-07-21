package com.darkblade12.enchantplus.enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum EnchantmentTarget {
	SWORD(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.WOOD_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD || material == Material.GOLD_SWORD || material == Material.DIAMOND_SWORD;
		}
	},
	AXE(Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD) {
		@Override
		public boolean includes(Material material) {
			return material == Material.WOOD_AXE || material == Material.STONE_AXE || material == Material.GOLD_AXE || material == Material.IRON_AXE || material == Material.DIAMOND_AXE;
		}
	},
	BOW(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE, Enchantment.ARROW_INFINITE, Enchantment.ARROW_KNOCKBACK, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.BOW;
		}
	},
	HELMET(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_PROJECTILE, Enchantment.OXYGEN, Enchantment.WATER_WORKER, Enchantment.THORNS, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.LEATHER_HELMET || material == Material.GOLD_HELMET || material == Material.CHAINMAIL_HELMET || material == Material.IRON_HELMET || material == Material.DIAMOND_HELMET;
		}
	},
	CHESTPLATE(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_PROJECTILE, Enchantment.THORNS, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.LEATHER_CHESTPLATE || material == Material.GOLD_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE || material == Material.IRON_CHESTPLATE || material == Material.DIAMOND_CHESTPLATE;
		}
	},
	LEGGINGS(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_PROJECTILE, Enchantment.THORNS, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.LEATHER_LEGGINGS || material == Material.GOLD_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS || material == Material.IRON_LEGGINGS || material == Material.DIAMOND_LEGGINGS;
		}
	},
	BOOTS(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_FALL, Enchantment.THORNS, Enchantment.DURABILITY, Enchantment.DEPTH_STRIDER) {
		@Override
		public boolean includes(Material material) {
			return material == Material.LEATHER_BOOTS || material == Material.GOLD_BOOTS || material == Material.CHAINMAIL_BOOTS || material == Material.IRON_BOOTS || material == Material.DIAMOND_BOOTS;
		}
	},
	TOOL(Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY, Enchantment.LOOT_BONUS_BLOCKS) {
		@Override
		public boolean includes(Material material) {
			return material == Material.WOOD_PICKAXE || material == Material.STONE_PICKAXE || material == Material.GOLD_PICKAXE || material == Material.IRON_PICKAXE || material == Material.DIAMOND_PICKAXE || material == Material.WOOD_SPADE || material == Material.STONE_SPADE
					|| material == Material.GOLD_SPADE || material == Material.IRON_SPADE || material == Material.DIAMOND_SPADE;
		}
	},
	BREAKABLE(Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.WOOD_HOE || material == Material.STONE_HOE || material == Material.GOLD_HOE || material == Material.IRON_HOE || material == Material.DIAMOND_HOE || material == Material.CARROT_STICK || material == Material.FLINT_AND_STEEL;
		}
	},
	SHEARS(Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.SHEARS;
		}
	},
	FISHING_ROD(Enchantment.LUCK, Enchantment.LURE, Enchantment.DURABILITY) {
		@Override
		public boolean includes(Material material) {
			return material == Material.FISHING_ROD;
		}
	},
	ALL(Enchantment.values()) {
		@Override
		public boolean includes(Material material) {
			return material == Material.BOOK || material == Material.ENCHANTED_BOOK;
		}
	},
	NONE() {
		@Override
		public boolean includes(Material material) {
			return false;
		}
	};

	private final List<Enchantment> enchantments;

	private EnchantmentTarget(Enchantment... enchantments) {
		this.enchantments = Arrays.asList(enchantments);
	}

	public abstract boolean includes(Material material);

	public boolean includes(ItemStack item) {
		return includes(item.getType());
	}

	public boolean includes(Enchantment enchantment) {
		return enchantments.contains(enchantment);
	}

	public List<Enchantment> getEnchantments() {
		return enchantments;
	}

	public static EnchantmentTarget fromMaterial(Material material) {
		if (material != null) {
			for (EnchantmentTarget target : values()) {
				if (target.includes(material)) {
					return target;
				}
			}
		}
		return NONE;
	}

	public static EnchantmentTarget fromItemStack(ItemStack item) {
		return item == null ? NONE : fromMaterial(item.getType());
	}

	public static boolean isEnchantable(Material material) {
		EnchantmentTarget target = fromMaterial(material);
		return target != BREAKABLE && target != SHEARS && target != NONE;
	}

	public static boolean isEnchantable(ItemStack item) {
		return item == null ? false : isEnchantable(item.getType());
	}

	public static List<Enchantment> getEnchantments(Material material) {
		return fromMaterial(material).enchantments;
	}

	public static List<Enchantment> getEnchantments(ItemStack item) {
		return item == null ? new ArrayList<Enchantment>() : getEnchantments(item.getType());
	}
}