package com.darkblade12.enchantplus.enchantment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;

public enum ExclusiveEnchantment {
	PROTECTION_ENVIROMENTAL("PROTECTION_EXPLOSIONS|PROTECTION_FIRE|PROTECTION_PROJECTILE"),
	PROTECTION_EXPLOSIONS("PROTECTION_ENVIROMENTAL|PROTECTION_FIRE|PROTECTION_PROJECTILE"),
	PROTECTION_FIRE("PROTECTION_ENVIROMENTAL|PROTECTION_EXPLOSIONS|PROTECTION_PROJECTILE"),
	PROTECTION_PROJECTILE("PROTECTION_ENVIROMENTAL|PROTECTION_EXPLOSIONS|PROTECTION_FIRE"),
	DAMAGE_ALL("DAMAGE_UNDEAD|DAMAGE_ARTHROPODS"),
	DAMAGE_UNDEAD("DAMAGE_ALL|DAMAGE_ARTHROPODS"),
	DAMAGE_ARTHROPODS("DAMAGE_ALL|DAMAGE_UNDEAD"),
	LOOT_BONUS_BLOCKS("SILK_TOUCH"),
	SILK_TOUCH("LOOT_BONUS_BLOCKS");

	private final Pattern conflicting;

	private ExclusiveEnchantment(String conflicting) {
		this.conflicting = Pattern.compile(conflicting);
	}

	public boolean conflictsWith(Enchantment enchantment) {
		return conflicting.matcher(enchantment.getName()).matches();
	}

	public boolean conflictsWith(Collection<Enchantment> collection) {
		for (Enchantment enchantment : collection) {
			if (conflictsWith(enchantment)) {
				return true;
			}
		}
		return false;
	}

	public Pattern getConflicting() {
		return conflicting;
	}

	public List<Enchantment> getConflicting(Collection<Enchantment> collection) {
		List<Enchantment> conflicting = new ArrayList<Enchantment>();
		for (Enchantment enchantment : collection) {
			if (conflictsWith(enchantment)) {
				conflicting.add(enchantment);
			}
		}
		return conflicting;
	}

	public static ExclusiveEnchantment fromEnchantment(Enchantment enchantment) {
		try {
			return valueOf(enchantment.getName());
		} catch (Exception exception) {
			return null;
		}
	}
}