package com.darkblade12.enchantplus.settings;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.enchantment.EnchantmentInformation;
import com.darkblade12.enchantplus.enchantment.EnchantmentMap;
import com.darkblade12.enchantplus.plugin.settings.InvalidValueException;
import com.darkblade12.enchantplus.plugin.settings.SettingsBase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Settings extends SettingsBase<EnchantPlus> {
    private static final Pattern ENCHANTMENT_NAME = Pattern.compile("[a-z_\\s]+-([a-z_\\s]+|\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_LIMIT_OVERRIDE = Pattern.compile("([a-z_\\s]+|\\d+)-\\d+", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_STACKING_OVERRIDE = Pattern.compile("([a-z_\\s]+|\\d+)-(true|false)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_RESTRICTION_OVERRIDE = Pattern.compile("([a-z_\\s]+|\\d+)-\\d+", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_COST_OVERRIDE = Pattern.compile("([a-z_\\s]+|\\d+)(-[br]\\d+){1,2}", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_COST_BASE = Pattern.compile("b(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_COST_REGULAR = Pattern.compile("r(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEVEL_REFUND_OVERRIDE = Pattern.compile("([a-z_\\s]+|\\d+)-\\d+", Pattern.CASE_INSENSITIVE);
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

    public Settings(EnchantPlus plugin) {
        super(plugin);
    }

    @Override
    public void load() throws InvalidValueException {
        FileConfiguration config = plugin.getConfig();
        loadEnchantmentNames(config);
        loadNaturalEnchanting(config);
        loadManualEnchanting(config);
    }

    @Override
    public void unload() {
        enchantments = null;
        levelLimitOverrides = null;
        levelStackingOverrides = null;
        levelRestrictionOverrides = null;
        levelCostOverrides = null;
        levelRefundOverrides = null;
    }

    @Override
    public void reload() throws InvalidValueException {
        unload();
        plugin.reloadConfig();
        load();
    }

    private void loadEnchantmentNames(Configuration config) throws InvalidValueException {
        enchantments = new LinkedHashMap<>();
        for (Enchantment enchant : Enchantment.values()) {
            addEnchantment(EnchantmentInformation.getMinecraftName(enchant), enchant);
            addEnchantment(EnchantmentInformation.getName(enchant), enchant);
        }

        List<String> enchantmentNames = config.getStringList(Setting.ENCHANTMENT_NAMES.getPath());
        for (String element : enchantmentNames) {
            if (!ENCHANTMENT_NAME.matcher(element).matches()) {
                throw new InvalidValueException(Setting.ENCHANTMENT_NAMES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[1]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.ENCHANTMENT_NAMES, element, "invalid enchantment name");
            }

            addEnchantment(values[0], enchant);
        }
    }

    private void loadNaturalEnchanting(FileConfiguration config) throws InvalidValueException {
        loadLevelLimit(config);
        loadMultipleEnchanting(config);
    }

    private void loadLevelLimit(FileConfiguration config) throws InvalidValueException {
        levelLimitEnabled = config.getBoolean(Setting.LEVEL_LIMIT_ENABLED.getPath());
        if (!levelLimitEnabled) {
            return;
        }

        levelLimitAmount = config.getInt(Setting.LEVEL_LIMIT_AMOUNT.getPath());
        if (levelLimitAmount < 0) {
            throw new InvalidValueException(Setting.LEVEL_LIMIT_AMOUNT, levelLimitAmount, "cannot be lower than 0");
        }

        levelLimitOverrides = new HashMap<>();
        List<String> overrides = config.getStringList(Setting.LEVEL_LIMIT_OVERRIDES.getPath());
        for (String element : overrides) {
            if (!LEVEL_LIMIT_OVERRIDE.matcher(element).matches()) {
                throw new InvalidValueException(Setting.LEVEL_LIMIT_OVERRIDES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[0]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.LEVEL_LIMIT_OVERRIDES, element, "invalid enchantment name");
            }

            int amount;
            try {
                amount = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                throw new InvalidValueException(Setting.LEVEL_LIMIT_OVERRIDES, element, "invalid amount value");
            }

            if (amount < 0) {
                throw new InvalidValueException(Setting.LEVEL_LIMIT_OVERRIDES, element, "amount cannot be lower than 0");
            }

            levelLimitOverrides.put(enchant, amount);
        }
    }

    private void loadMultipleEnchanting(FileConfiguration config) throws InvalidValueException {
        multipleEnchantingEnabled = config.getBoolean(Setting.MULTIPLE_ENCHANTING_ENABLED.getPath());
        if (!multipleEnchantingEnabled) {
            return;
        }

        multipleEnchantingPermissionEnabled = config.getBoolean(Setting.MULTIPLE_ENCHANTING_PERMISSION_ENABLED.getPath());
        multipleEnchantingConflictingEnabled = config.getBoolean(Setting.MULTIPLE_ENCHANTING_CONFLICTING_ENABLED.getPath());

        loadLevelStacking(config);
        loadLevelCostIncrease(config);
    }

    private void loadLevelStacking(FileConfiguration config) throws InvalidValueException {
        levelStackingEnabled = config.getBoolean(Setting.LEVEL_STACKING_ENABLED.getPath());
        if (!levelStackingEnabled) {
            return;
        }

        levelStackingOverrides = new HashMap<>();
        List<String> overrides = config.getStringList(Setting.LEVEL_STACKING_OVERRIDES.getPath());
        for (String element : overrides) {
            if (!LEVEL_STACKING_OVERRIDE.matcher(element).matches()) {
                throw new InvalidValueException(Setting.LEVEL_STACKING_OVERRIDES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[0]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.LEVEL_STACKING_OVERRIDES, element, "invalid enchantment name");
            }

            levelStackingOverrides.put(enchant, Boolean.parseBoolean(values[1]));
        }
    }

    private void loadLevelCostIncrease(FileConfiguration config) throws InvalidValueException {
        levelCostIncreaseEnabled = config.getBoolean(Setting.LEVEL_COST_INCREASE_ENABLED.getPath());
        if (!levelCostIncreaseEnabled) {
            return;
        }

        levelCostIncreaseAmount = config.getInt(Setting.LEVEL_COST_INCREASE_AMOUNT.getPath());
        if (levelCostIncreaseAmount < 1) {
            throw new InvalidValueException(Setting.LEVEL_COST_INCREASE_AMOUNT, levelCostIncreaseAmount, "cannot be lower than 1");
        }
    }

    private void loadManualEnchanting(FileConfiguration config) throws InvalidValueException {
        manualEnchantingEnabled = config.getBoolean(Setting.MANUAL_ENCHANTING_ENABLED.getPath());
        if (!manualEnchantingEnabled) {
            return;
        }

        manualEnchantingConflictingEnabled = config.getBoolean(Setting.MANUAL_ENCHANTING_CONFLICTING_ENABLED.getPath());
        manualEnchantingInapplicableEnabled = config.getBoolean(Setting.MANUAL_ENCHANTING_INAPPLICABLE_ENABLED.getPath());
        manualEnchantingAmountEnabled = config.getBoolean(Setting.MANUAL_ENCHANTING_AMOUNT_ENABLED.getPath());

        loadPowerSource(config);
        loadLevelRestriction(config);
        loadLevelCost(config);
    }

    private void loadPowerSource(FileConfiguration config) throws InvalidValueException {
        powerSourceEnabled = config.getBoolean(Setting.POWER_SOURCE_ENABLED.getPath());
        if (!powerSourceEnabled) {
            return;
        }

        powerSourceRange = config.getInt(Setting.POWER_SOURCE_RANGE.getPath());
        if (powerSourceRange < 1) {
            throw new InvalidValueException(Setting.POWER_SOURCE_RANGE, powerSourceRange, "cannot be lower than 1");
        }
    }

    private void loadLevelRestriction(FileConfiguration config) throws InvalidValueException {
        levelRestrictionEnabled = config.getBoolean(Setting.LEVEL_RESTRICTION_ENABLED.getPath());
        if (!levelRestrictionEnabled) {
            return;
        }

        levelRestrictionAmount = config.getInt(Setting.LEVEL_RESTRICTION_AMOUNT.getPath());
        if (levelRestrictionAmount < 0) {
            throw new InvalidValueException(Setting.LEVEL_RESTRICTION_AMOUNT, levelRestrictionAmount, "cannot be lower than 0");
        }

        levelRestrictionOverrides = new HashMap<>();
        List<String> overrides = config.getStringList(Setting.LEVEL_RESTRICTION_OVERRIDES.getPath());
        for (String element : overrides) {
            if (!LEVEL_RESTRICTION_OVERRIDE.matcher(element).matches()) {
                throw new InvalidValueException(Setting.LEVEL_RESTRICTION_OVERRIDES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[0]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.LEVEL_RESTRICTION_OVERRIDES, element, "invalid enchantment name");
            }

            int amount;
            try {
                amount = Integer.parseInt(values[1]);
            } catch (Exception exception) {
                throw new InvalidValueException(Setting.LEVEL_RESTRICTION_OVERRIDES, element, "invalid amount value");
            }

            if (amount < 0) {
                throw new InvalidValueException(Setting.LEVEL_RESTRICTION_OVERRIDES, element, "amount cannot be lower than 0");
            }

            levelRestrictionOverrides.put(enchant, amount);
        }
    }

    private void loadLevelCost(FileConfiguration config) throws InvalidValueException {
        levelCostEnabled = config.getBoolean(Setting.LEVEL_COST_ENABLED.getPath());
        if (!levelCostEnabled) {
            return;
        }

        levelCostBaseAmount = config.getInt(Setting.LEVEL_COST_BASE_AMOUNT.getPath());
        if (levelCostBaseAmount < 0) {
            throw new InvalidValueException(Setting.LEVEL_COST_BASE_AMOUNT, levelCostBaseAmount, "cannot be lower than 0");
        }

        levelCostRegularAmount = config.getInt(Setting.LEVEL_COST_REGULAR_AMOUNT.getPath());
        if (levelCostRegularAmount < 0) {
            throw new InvalidValueException(Setting.LEVEL_COST_REGULAR_AMOUNT, levelCostRegularAmount, "cannot be lower than 0");
        }

        levelCostOverrides = new HashMap<>();
        List<String> overrides = config.getStringList(Setting.LEVEL_COST_OVERRIDES.getPath());
        for (String element : overrides) {
            if (!LEVEL_COST_OVERRIDE.matcher(element).matches()) {
                throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[0]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "invalid enchantment name");
            }

            int[] amounts = new int[] { levelCostBaseAmount, levelCostRegularAmount };
            int argCount = Math.min(values.length, 3);
            for (int i = 1; i < argCount; i++) {
                Matcher matcher = LEVEL_COST_BASE.matcher(values[i]);
                if (matcher.matches()) {
                    try {
                        amounts[0] = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "invalid base amount");
                    }

                    if (amounts[0] < 0) {
                        throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "base amount cannot be lower than 0");
                    }
                    continue;
                }

                matcher = LEVEL_COST_REGULAR.matcher(values[i]);
                if (matcher.matches()) {
                    try {
                        amounts[1] = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "invalid regular amount");
                    }
                    if (amounts[1] < 0) {
                        throw new InvalidValueException(Setting.LEVEL_COST_OVERRIDES, element, "regular amount cannot be lower than 0");
                    }
                }
            }

            levelCostOverrides.put(enchant, amounts);
        }

        loadLevelRefund(config);
    }

    private void loadLevelRefund(FileConfiguration config) throws InvalidValueException {
        levelRefundEnabled = config.getBoolean(Setting.LEVEL_REFUND_ENABLED.getPath());
        if (!levelRefundEnabled) {
            return;
        }

        levelRefundAmount = config.getInt(Setting.LEVEL_REFUND_AMOUNT.getPath());
        if (levelRefundAmount < 0) {
            throw new InvalidValueException(Setting.LEVEL_REFUND_AMOUNT, levelRefundAmount, "cannot be lower than 0");
        }

        levelRefundOverrides = new HashMap<>();
        List<String> overrides = config.getStringList(Setting.LEVEL_REFUND_OVERRIDES.getPath());
        for (String element : overrides) {
            if (!LEVEL_REFUND_OVERRIDE.matcher(element).matches()) {
                throw new InvalidValueException(Setting.LEVEL_REFUND_OVERRIDES, element, "invalid format");
            }

            String[] values = element.split("-");
            Enchantment enchant = getEnchantment(values[0]);
            if (enchant == null) {
                throw new InvalidValueException(Setting.LEVEL_REFUND_OVERRIDES, element, "invalid enchantment name");
            }

            int amount;
            try {
                amount = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                throw new InvalidValueException(Setting.LEVEL_REFUND_OVERRIDES, element, "invalid amount");
            }

            if (amount < 0) {
                throw new InvalidValueException(Setting.LEVEL_REFUND_OVERRIDES, element, "amount cannot be lower than 0");
            }

            levelRefundOverrides.put(enchant, amount);
        }
    }

    private static String formatName(String name, char separator) {
        String[] split = name.toLowerCase().split("_|\\s");
        return Arrays.stream(split).map(StringUtils::capitalize).collect(Collectors.joining(String.valueOf(separator)));
    }

    private void addEnchantment(String name, Enchantment enchant) {
        name = formatName(name, ' ');
        if (!enchantments.containsKey(name)) {
            enchantments.put(name, enchant);
        }
    }

    public Enchantment getEnchantment(String name) {
        String finalName = name.replace('_', ' ');
        return enchantments.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(finalName)).map(Entry::getValue).findFirst()
                           .orElse(null);
    }

    public List<String> getNames(Enchantment enchant) {
        return enchantments.entrySet().stream().filter(e -> e.getValue().equals(enchant)).map(Entry::getKey)
                           .collect(Collectors.toList());
    }

    public int getLevelLimitAmount(Enchantment enchant) {
        if (!levelLimitEnabled) {
            return Short.MAX_VALUE;
        }

        int levelLimit = levelLimitOverrides.getOrDefault(enchant, levelLimitAmount);
        return levelLimit == 0 ? enchant.getMaxLevel() : levelLimit;
    }

    public int getLevelLimitAmount(Player player, Enchantment enchant) {
        return Permission.BYPASS_LIMIT.test(player) ? Short.MAX_VALUE : getLevelLimitAmount(enchant);
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

    public boolean isLevelStackingEnabled(Enchantment enchant) {
        return levelStackingOverrides.getOrDefault(enchant, levelStackingEnabled);
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

    public int getLevelRestrictionAmount(Enchantment enchant) {
        if (!levelRestrictionEnabled) {
            return Short.MAX_VALUE;
        }

        int levelRestriction = levelRestrictionOverrides.getOrDefault(enchant, levelRestrictionAmount);
        return levelRestriction == 0 ? enchant.getMaxLevel() : levelRestriction;
    }

    public int getLevelRestrictionAmount(Player player, Enchantment enchant) {
        return Permission.BYPASS_RESTRICTION.test(player) ? Short.MAX_VALUE : getLevelRestrictionAmount(enchant);
    }

    public int[] getLevelCostAmounts(Enchantment enchant) {
        if (!levelCostEnabled) {
            return new int[2];
        }

        return levelCostOverrides.containsKey(enchant)
               ? levelCostOverrides.get(enchant)
               : new int[] { levelCostBaseAmount, levelCostRegularAmount };
    }

    public int[] getLevelCostAmounts(Player player, Enchantment enchantment) {
        return Permission.BYPASS_COST.test(player) ? new int[2] : getLevelCostAmounts(enchantment);
    }

    private int getRawCost(Player player, Enchantment enchantment, int level) {
        int[] costs = getLevelCostAmounts(player, enchantment);
        return level == 0 ? 0 : costs[0] + costs[1] * (level - 1);
    }

    public int getCost(Player player, ItemStack item, Enchantment enchant, int level) throws IllegalArgumentException {
        EnchantmentMap map = EnchantmentMap.fromItemStack(item);
        int currentLevel = map.getLevel(enchant, 0);
        if (currentLevel == level) {
            throw new IllegalArgumentException("Current level equals the new level.");
        }

        if (currentLevel < level) {
            return getRawCost(player, enchant, level) - getRawCost(player, enchant, currentLevel);
        } else if (!levelRefundEnabled) {
            return 0;
        }

        int refundAmount = getLevelRefundAmount(enchant);
        return -(refundAmount == 0
                 ? Math.abs(getRawCost(player, enchant, currentLevel) - getRawCost(player, enchant, level))
                 : refundAmount * currentLevel);
    }

    public int getLevelRefundAmount(Enchantment enchantment) {
        return levelRefundEnabled ? (levelRefundOverrides.containsKey(enchantment)
                                     ? levelRefundOverrides.get(enchantment)
                                     : levelRefundAmount) : 0;
    }

    public boolean withdrawLevels(Player player, ItemStack item, Enchantment enchant, int level) {
        int cost = getCost(player, item, enchant, level);
        int playerLevel = player.getLevel();
        if (playerLevel < cost) {
            return false;
        }

        player.setLevel(playerLevel - cost);
        return true;
    }

    public void refundLevels(Player player, ItemStack item, Enchantment enchant) {
        withdrawLevels(player, item, enchant, 0);
    }

    public boolean hasPowerSource(Player player) {
        if (!powerSourceEnabled || Permission.BYPASS_POWER.test(player)) {
            return true;
        }

        Location location = player.getLocation();
        World world = player.getWorld();

        double currentX = location.getX();
        double currentY = location.getY();
        double currentZ = location.getZ();
        double bPow = Math.pow(powerSourceRange + 0.5D, 2);
        double xPow;
        double zPow;

        for (int z = -powerSourceRange; z <= powerSourceRange; z++) {
            zPow = Math.pow(z, 2);
            for (int x = -powerSourceRange; x <= powerSourceRange; x++) {
                xPow = Math.pow(x, 2);
                for (int y = -powerSourceRange; y <= powerSourceRange; y++) {
                    if ((xPow + Math.pow(y, 2) + zPow) <= bPow) {
                        if (world.getBlockAt((int) (currentX + x), (int) (currentY + y), (int) (currentZ + z))
                                 .getType() == Material.ENCHANTING_TABLE) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public String getEnchantmentList(Collection<Enchantment> enchantments, String messageKey) throws IllegalArgumentException {
        if (enchantments == null || enchantments.isEmpty()) {
            throw new IllegalArgumentException("Enchantments cannot be empty.");
        }

        StringBuilder builder = new StringBuilder();
        List<Enchantment> clone = new ArrayList<>(enchantments);
        clone.sort(Comparator.comparing(EnchantmentInformation::getName));

        for (Enchantment enchant : clone) {
            String altNames = getNames(enchant).stream().map(n -> "§6" + n).collect(Collectors.joining(" §e| "));
            builder.append('\n').append(plugin.formatMessage(messageKey, EnchantmentInformation.getName(enchant), altNames));
        }

        return builder.toString();
    }
}
