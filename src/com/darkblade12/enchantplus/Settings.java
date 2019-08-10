package com.darkblade12.enchantplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.permission.Permission;
import com.darkblade12.enchantplus.section.IndependantConfigurationSection;
import com.darkblade12.enchantplus.section.exception.InvalidValueException;
import com.darkblade12.enchantplus.section.exception.SectionNotFoundException;

public final class Settings {
	private static final IndependantConfigurationSection NATURAL_ENCHANTING = new IndependantConfigurationSection("Natural_Enchanting");
	private static final IndependantConfigurationSection LEVEL_LIMIT = new IndependantConfigurationSection(NATURAL_ENCHANTING, "Level_Limit");
	private static final IndependantConfigurationSection MULTIPLE_ENCHANTING = new IndependantConfigurationSection(NATURAL_ENCHANTING, "Multiple_Enchanting");
	private static final IndependantConfigurationSection LEVEL_STACKING = new IndependantConfigurationSection(MULTIPLE_ENCHANTING, "Level_Stacking");
	private static final IndependantConfigurationSection LEVEL_COST_INCREASE = new IndependantConfigurationSection(MULTIPLE_ENCHANTING, "Level_Cost_Increase");
	private static final IndependantConfigurationSection MANUAL_ENCHANTING = new IndependantConfigurationSection("Manual_Enchanting");
	private static final IndependantConfigurationSection POWER_SOURCE = new IndependantConfigurationSection(MANUAL_ENCHANTING, "Power_Source");
	private static final IndependantConfigurationSection LEVEL_RESTRICTION = new IndependantConfigurationSection(MANUAL_ENCHANTING, "Level_Restriction");
	private static final IndependantConfigurationSection LEVEL_COST = new IndependantConfigurationSection(MANUAL_ENCHANTING, "Level_Cost");
	private static final IndependantConfigurationSection LEVEL_REFUND = new IndependantConfigurationSection(LEVEL_COST, "Level_Refund");
	private static final Pattern ENCHANTMENT_NAME = Pattern.compile("[a-zA-Z_\\s]+-([a-zA-Z_\\s]+|\\d+)");
	private static final Pattern LEVEL_LIMIT_OVERRIDE = Pattern.compile("([a-zA-Z_\\s]+|\\d+)-\\d+");
	private static final Pattern LEVEL_STACKING_OVERRIDE = Pattern.compile("([a-zA-Z_\\s]+|\\d+)-([tT][rR][uU][eE]|[fF][aA][lL][sS][eE])");
	private static final Pattern LEVEL_RESTRICTION_OVERRIDE = Pattern.compile("([a-zA-Z_\\s]+|\\d+)-\\d+");
	private static final Pattern LEVEL_COST_OVERRIDE = Pattern.compile("([a-zA-Z_\\s]+|\\d+)(-([bB]|[rR])\\d+){1,2}");
	private static final Pattern LEVEL_COST_BASE = Pattern.compile("[bB]\\d+");
	private static final Pattern LEVEL_COST_REGULAR = Pattern.compile("[rR]\\d+");
	private static final Pattern LEVEL_REFUND_OVERRIDE = Pattern.compile("([a-zA-Z_\\s]+|\\d+)-\\d+");
	private final AbstractPlugin plugin;
	private Map<String, Enchantment> enchantments;
	private boolean levelLimitEnabled;
	private int levelLimitAmount;
	private Map<Enchantment, Integer> levelLimitOverrides;
	private boolean multipleEnchantingEnabled;
	private boolean multipleEnchantingPermissionEnabled;
	private boolean multipleEnchantingConflictingEnabled;
	private boolean levelStackingEnabled;
	private Map<Enchantment, Boolean> levelStackingOverrides;
	private boolean levelCostIncreaseEnabled;
	private int levelCostIncreaseAmount;
	private boolean manualEnchantingEnabled;
	private boolean manualEnchantingConflictingEnabled;
	private boolean manualEnchantingInapplicableEnabled;
	private boolean manualEnchantingAmountEnabled;
	private boolean powerSourceEnabled;
	private int powerSourceRange;
	private boolean levelRestrictionEnabled;
	private int levelRestrictionAmount;
	private Map<Enchantment, Integer> levelRestrictionOverrides;
	private boolean levelCostEnabled;
	private int levelCostBaseAmount;
	private int levelCostRegularAmount;
	private Map<Enchantment, int[]> levelCostOverrides;
	private boolean levelRefundEnabled;
	private int levelRefundAmount;
	private Map<Enchantment, Integer> levelRefundOverrides;

	protected Settings(AbstractPlugin plugin) {
		this.plugin = plugin;
	}

	protected void onLoad() throws SectionNotFoundException, InvalidValueException {
		Configuration config = plugin.getConfig();
		loadEnchantmentNames(config);
		loadNaturalEnchanting(config);
		loadManualEnchanting(config);
	}

	@SuppressWarnings("deprecation")
	private void loadEnchantmentNames(Configuration config) throws InvalidValueException {
		enchantments = new LinkedHashMap<String, Enchantment>();
		for (Enchantment enchantment : Enchantment.values()) {
			addEnchantment(EnchantmentInformation.getMinecraftName(enchantment), enchantment);
			addEnchantment(enchantment.getName(), enchantment);
			addEnchantment(enchantment.getKey().getKey(), enchantment);
		}
		List<String> enchantmentNames = config.getStringList("Enchantment_Names");
		if (enchantmentNames == null) {
			return;
		}
		int position = 1;
		for (String element : enchantmentNames) {
			if (!ENCHANTMENT_NAME.matcher(element).matches()) {
				throw new InvalidValueException("Enchantment_Names", "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[1]);
			if (enchantment == null) {
				throw new InvalidValueException("Enchantment_Names", "contains an element with an invalid enchantment identifier (Position: " + position + ")");
			}
			addEnchantment(split[0], enchantment);
			position++;
		}
	}

	private void loadNaturalEnchanting(Configuration config) throws SectionNotFoundException, InvalidValueException {
		loadLevelLimit(config);
		loadMultipleEnchanting(config);
	}

	private void loadLevelLimit(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelLimit = LEVEL_LIMIT.getConfigurationSection(config, false);
		if (levelLimit == null) {
			return;
		}
		levelLimitEnabled = levelLimit.getBoolean("Enabled");
		if (!levelLimitEnabled) {
			return;
		}
		levelLimitAmount = levelLimit.getInt("Amount");
		if (levelLimitAmount < 0) {
			throw new InvalidValueException("Amount", LEVEL_LIMIT, "is lower than 0");
		}
		levelLimitOverrides = new HashMap<Enchantment, Integer>();
		List<String> overrides = levelLimit.getStringList("Overrides");
		if (overrides == null) {
			return;
		}
		int position = 1;
		for (String element : overrides) {
			if (!LEVEL_LIMIT_OVERRIDE.matcher(element).matches()) {
				throw new InvalidValueException("Overrides", LEVEL_LIMIT, "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[0]);
			if (enchantment == null) {
				throw new InvalidValueException("Overrides", LEVEL_LIMIT, "contains an element with an invalid enchantment name (Position: " + position + ")");
			}
			int amount;
			try {
				amount = Integer.parseInt(split[1]);
			} catch (Exception exception) {
				throw new InvalidValueException("Overrides", LEVEL_LIMIT, "contains an element with an invalid amount (Position: " + position + ")");
			}
			if (amount < 0) {
				throw new InvalidValueException("Overrides", LEVEL_LIMIT, "contains an element with an amount lower than 0 (Position: " + position + ")");
			}
			levelLimitOverrides.put(enchantment, amount);
			position++;
		}
	}

	private void loadMultipleEnchanting(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection multipleEnchanting = MULTIPLE_ENCHANTING.getConfigurationSection(config, false);
		if (multipleEnchanting == null) {
			return;
		}
		multipleEnchantingEnabled = multipleEnchanting.getBoolean("Enabled");
		if (!multipleEnchantingEnabled) {
			return;
		}
		multipleEnchantingPermissionEnabled = multipleEnchanting.getBoolean("Permission_Enabled");
		multipleEnchantingConflictingEnabled = multipleEnchanting.getBoolean("Conflicting_Enabled");
		manualEnchantingAmountEnabled = multipleEnchanting.getBoolean("Amount_Enabled");
		loadLevelStacking(config);
		loadLevelCostIncrease(config);
	}

	private void loadLevelStacking(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelStacking = LEVEL_STACKING.getConfigurationSection(config, false);
		if (levelStacking == null) {
			return;
		}
		levelStackingEnabled = levelStacking.getBoolean("Enabled");
		if (!levelStackingEnabled) {
			return;
		}
		levelStackingOverrides = new HashMap<Enchantment, Boolean>();
		List<String> overrides = levelStacking.getStringList("Overrides");
		if (overrides == null) {
			return;
		}
		int position = 1;
		for (String element : overrides) {
			if (!LEVEL_STACKING_OVERRIDE.matcher(element).matches()) {
				throw new InvalidValueException("Overrides", LEVEL_STACKING, "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[0]);
			if (enchantment == null) {
				throw new InvalidValueException("Overrides", LEVEL_STACKING, "contains an element with an invalid enchantment name (Position: " + position + ")");
			}
			levelStackingOverrides.put(enchantment, Boolean.parseBoolean(split[1]));
			position++;
		}
	}

	private void loadLevelCostIncrease(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelCostIncrease = LEVEL_COST_INCREASE.getConfigurationSection(config, false);
		if (levelCostIncrease == null) {
			return;
		}
		levelCostIncreaseEnabled = levelCostIncrease.getBoolean("Enabled");
		if (!levelCostIncreaseEnabled) {
			return;
		}
		levelCostIncreaseAmount = levelCostIncrease.getInt("Amount");
		if (levelCostIncreaseAmount < 1) {
			throw new InvalidValueException("Amount", LEVEL_COST_INCREASE, "is lower than 1");
		}
	}

	private void loadManualEnchanting(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection manualEnchanting = MANUAL_ENCHANTING.getConfigurationSection(config, false);
		if (manualEnchanting == null) {
			return;
		}
		manualEnchantingEnabled = manualEnchanting.getBoolean("Enabled");
		if (!manualEnchantingEnabled) {
			return;
		}
		manualEnchantingConflictingEnabled = manualEnchanting.getBoolean("Conflicting_Enabled");
		manualEnchantingInapplicableEnabled = manualEnchanting.getBoolean("Inapplicable_Enabled");
		loadPowerSource(config);
		loadLevelRestriction(config);
		loadLevelCost(config);
	}

	private void loadPowerSource(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection powerSource = POWER_SOURCE.getConfigurationSection(config, false);
		if (powerSource == null) {
			return;
		}
		powerSourceEnabled = powerSource.getBoolean("Enabled");
		if (!powerSourceEnabled) {
			return;
		}
		powerSourceRange = powerSource.getInt("Range");
		if (powerSourceRange < 1) {
			throw new InvalidValueException("Range", POWER_SOURCE, "is lower than 1");
		}
	}

	private void loadLevelRestriction(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelRestriction = LEVEL_RESTRICTION.getConfigurationSection(config, false);
		if (levelRestriction == null) {
			return;
		}
		levelRestrictionEnabled = levelRestriction.getBoolean("Enabled");
		if (!levelRestrictionEnabled) {
			return;
		}
		levelRestrictionAmount = levelRestriction.getInt("Amount");
		if (levelRestrictionAmount < 0) {
			throw new InvalidValueException("Amount", LEVEL_RESTRICTION, "is lower than 0");
		}
		levelRestrictionOverrides = new HashMap<Enchantment, Integer>();
		List<String> overrides = levelRestriction.getStringList("Overrides");
		if (overrides == null) {
			return;
		}
		int position = 1;
		for (String element : overrides) {
			if (!LEVEL_RESTRICTION_OVERRIDE.matcher(element).matches()) {
				throw new InvalidValueException("Overrides", LEVEL_RESTRICTION, "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[0]);
			if (enchantment == null) {
				throw new InvalidValueException("Overrides", LEVEL_RESTRICTION, "contains an element with an invalid enchantment name (Position: " + position + ")");
			}
			int amount;
			try {
				amount = Integer.parseInt(split[1]);
			} catch (Exception exception) {
				throw new InvalidValueException("Overrides", LEVEL_RESTRICTION, "contains an element with an invalid amount (Position: " + position + ")");
			}
			if (amount < 0) {
				throw new InvalidValueException("Overrides", LEVEL_RESTRICTION, "contains an element with an amount lower than 0 (Position: " + position + ")");
			}
			levelRestrictionOverrides.put(enchantment, amount);
			position++;
		}
	}

	private void loadLevelCost(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelCost = LEVEL_COST.getConfigurationSection(config, false);
		if (levelCost == null) {
			return;
		}
		levelCostEnabled = levelCost.getBoolean("Enabled");
		if (!levelCostEnabled) {
			return;
		}
		levelCostBaseAmount = levelCost.getInt("Base_Amount");
		if (levelCostBaseAmount < 0) {
			throw new InvalidValueException("Base_Amount", LEVEL_COST, "is lower than 0");
		}
		levelCostRegularAmount = levelCost.getInt("Regular_Amount");
		if (levelCostRegularAmount < 0) {
			throw new InvalidValueException("Regular_Amount", LEVEL_COST, "is lower than 0");
		}
		levelCostOverrides = new HashMap<Enchantment, int[]>();
		List<String> overrides = levelCost.getStringList("Overrides");
		if (overrides == null) {
			return;
		}
		int position = 1;
		for (String element : overrides) {
			if (!LEVEL_COST_OVERRIDE.matcher(element).matches()) {
				throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[0]);
			if (enchantment == null) {
				throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with an invalid enchantment name (Position: " + position + ")");
			}
			int[] amounts = new int[] { levelCostBaseAmount, levelCostRegularAmount };
			int length = split.length;
			for (int index = 1; index < (length > 3 ? 3 : length); index++) {
				String part = split[index];
				if (LEVEL_COST_BASE.matcher(part).matches()) {
					try {
						amounts[0] = Integer.parseInt(part.replaceAll("[bB]", ""));
					} catch (Exception exception) {
						throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with an invalid base amount (Position: " + position + ")");
					}
					if (amounts[0] < 0) {
						throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with a base amount lower than 0 (Position: " + position + ")");
					}
				} else if (LEVEL_COST_REGULAR.matcher(part).matches()) {
					try {
						amounts[1] = Integer.parseInt(part.replaceAll("[rR]", ""));
					} catch (Exception exception) {
						throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with an invalid regular amount (Position: " + position + ")");
					}
					if (amounts[1] < 0) {
						throw new InvalidValueException("Overrides", LEVEL_COST, "contains an element with a regular amount lower than 0 (Position: " + position + ")");
					}
				}
			}
			levelCostOverrides.put(enchantment, amounts);
			position++;
		}
		loadLevelRefund(config);
	}

	private void loadLevelRefund(Configuration config) throws SectionNotFoundException, InvalidValueException {
		ConfigurationSection levelRefund = LEVEL_REFUND.getConfigurationSection(config, false);
		if (levelRefund == null) {
			return;
		}
		levelRefundEnabled = levelRefund.getBoolean("Enabled");
		if (!levelRefundEnabled) {
			return;
		}
		levelRefundAmount = levelRefund.getInt("Amount");
		if (levelRefundAmount < 0) {
			throw new InvalidValueException("Amount", LEVEL_REFUND, "is lower than 0");
		}
		levelRefundOverrides = new HashMap<Enchantment, Integer>();
		List<String> overrides = levelRefund.getStringList("Overrides");
		if (overrides == null) {
			return;
		}
		int position = 1;
		for (String element : overrides) {
			if (!LEVEL_REFUND_OVERRIDE.matcher(element).matches()) {
				throw new InvalidValueException("Overrides", LEVEL_REFUND, "contains an element with an invalid format (Position: " + position + ")");
			}
			String[] split = element.split("-");
			Enchantment enchantment = getEnchantment(split[0]);
			if (enchantment == null) {
				throw new InvalidValueException("Overrides", LEVEL_REFUND, "contains an element with an invalid enchantment name (Position: " + position + ")");
			}
			int amount;
			try {
				amount = Integer.parseInt(split[1]);
			} catch (Exception exception) {
				throw new InvalidValueException("Overrides", LEVEL_REFUND, "contains an element with an invalid amount (Position: " + position + ")");
			}
			if (amount < 0) {
				throw new InvalidValueException("Overrides", LEVEL_REFUND, "contains an element with an amount lower than 0 (Position: " + position + ")");
			}
			levelRefundOverrides.put(enchantment, amount);
			position++;
		}
	}

	protected void onReload() throws SectionNotFoundException, InvalidValueException {
		plugin.reloadConfig();
		onLoad();
	}

	public static String formatName(String name, char seperator) {
		StringBuilder builder = new StringBuilder();
		String[] split = name.split("_|\\s");
		for (int index = 0; index < split.length; index++) {
			if (builder.length() > 0) {
				builder.append(seperator);
			}
			String part = split[index];
			builder.append(Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase());
		}
		return builder.toString();
	}

	private void addEnchantment(String name, Enchantment enchantment) {
		name = formatName(name, ' ');
		if (!enchantments.containsKey(name)) {
			enchantments.put(name, enchantment);
		}
	}

	public Enchantment getEnchantment(String name) throws IllegalArgumentException {
		name = name.replace('_', ' ');
		for (Entry<String, Enchantment> entry : enchantments.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public List<String> getNames(Enchantment enchantment) {
		List<String> names = new ArrayList<String>();
		for (Entry<String, Enchantment> entry : enchantments.entrySet()) {
			if (entry.getValue().equals(enchantment)) {
				names.add(entry.getKey());
			}
		}
		return names;
	}

	public boolean isLevelLimitEnabled() {
		return levelLimitEnabled;
	}

	public int getLevelLimitAmount() {
		return levelLimitAmount;
	}

	public int getLevelLimitAmount(Enchantment enchantment) {
		int amount = levelLimitEnabled ? (levelLimitOverrides.containsKey(enchantment) ? levelLimitOverrides.get(enchantment) : levelLimitAmount) : Short.MAX_VALUE;
		return amount == 0 ? enchantment.getMaxLevel() : amount;
	}

	public int getLevelLimitAmount(Player player, Enchantment enchantment) {
		return Permission.LIMIT_BYPASS.has(player) ? Short.MAX_VALUE : getLevelLimitAmount(enchantment);
	}

	public boolean isMultipleEnchantingEnabled() {
		return multipleEnchantingEnabled;
	}

	public boolean isMultipleEnchantingPermissionEnabled() {
		return multipleEnchantingPermissionEnabled;
	}

	public boolean isMultipleEnchantingConflictingEnabled() {
		return multipleEnchantingConflictingEnabled;
	}

	public boolean isLevelStackingEnabled() {
		return levelStackingEnabled;
	}

	public boolean isLevelStackingEnabled(Enchantment enchantment) {
		return levelStackingOverrides.containsKey(enchantment) ? levelStackingOverrides.get(enchantment) : levelStackingEnabled;
	}

	public boolean isLevelCostIncreaseEnabled() {
		return levelCostIncreaseEnabled;
	}

	public int getLevelCostIncreaseAmount() {
		return levelCostIncreaseAmount;
	}

	public boolean isManualEnchantingEnabled() {
		return manualEnchantingEnabled;
	}

	public boolean isManualEnchantingConflictingEnabled() {
		return manualEnchantingConflictingEnabled;
	}

	public boolean isManualEnchantingInapplicableEnabled() {
		return manualEnchantingInapplicableEnabled;
	}

	public boolean isManualEnchantingAmountEnabled() {
		return manualEnchantingAmountEnabled;
	}

	public boolean isPowerSourceEnabled() {
		return powerSourceEnabled;
	}

	public int getPowerSourceRange() {
		return powerSourceRange;
	}

	public boolean isLevelRestrictionEnabled() {
		return levelRestrictionEnabled;
	}

	public int getLevelRestrictionAmount() {
		return levelRestrictionAmount;
	}

	public int getLevelRestrictionAmount(Enchantment enchantment) {
		int amount = levelRestrictionEnabled ? (levelRestrictionOverrides.containsKey(enchantment) ? levelRestrictionOverrides.get(enchantment) : levelRestrictionAmount) : Short.MAX_VALUE;
		return amount == 0 ? enchantment.getMaxLevel() : amount;
	}

	public int getLevelRestrictionAmount(Player player, Enchantment enchantment) {
		return Permission.RESTRICTION_BYPASS.has(player) ? Short.MAX_VALUE : getLevelRestrictionAmount(enchantment);
	}

	public boolean isLevelCostEnabled() {
		return levelCostEnabled;
	}

	public int getLevelCostBaseAmount() {
		return levelCostBaseAmount;
	}

	public int getLevelCostRegularAmount() {
		return levelCostRegularAmount;
	}

	public int[] getLevelCostAmounts(Enchantment enchantment) {
		return levelCostEnabled ? (levelCostOverrides.containsKey(enchantment) ? levelCostOverrides.get(enchantment) : new int[] { levelCostBaseAmount, levelCostRegularAmount }) : new int[2];
	}

	public int[] getLevelCostAmounts(Player player, Enchantment enchantment) {
		return Permission.COST_BYPASS.has(player) ? new int[2] : getLevelCostAmounts(enchantment);
	}

	public int getLevelCostBaseAmount(Enchantment enchantment) {
		return getLevelCostAmounts(enchantment)[0];
	}

	public int getLevelCostRegularAmount(Enchantment enchantment) {
		return getLevelCostAmounts(enchantment)[1];
	}

	public boolean isLevelRefundEnabled() {
		return levelRefundEnabled;
	}

	public int getLevelRefundAmount() {
		return levelRefundAmount;
	}

	public int getLevelRefundAmount(Enchantment enchantment) {
		return levelRefundEnabled ? (levelRefundOverrides.containsKey(enchantment) ? levelRefundOverrides.get(enchantment) : levelRefundAmount) : 0;
	}
}