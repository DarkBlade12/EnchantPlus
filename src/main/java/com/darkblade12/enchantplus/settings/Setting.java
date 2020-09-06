package com.darkblade12.enchantplus.settings;

import com.darkblade12.enchantplus.plugin.settings.PathProvider;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

enum Setting implements PathProvider {
    ENCHANTMENT_NAMES("Enchantment_Names"),
    LEVEL_LIMIT_ENABLED("Enabled", Section.LEVEL_LIMIT),
    LEVEL_LIMIT_AMOUNT("Amount", Section.LEVEL_LIMIT),
    LEVEL_LIMIT_OVERRIDES("Overrides", Section.LEVEL_LIMIT),
    MULTIPLE_ENCHANTING_ENABLED("Enabled", Section.MULTIPLE_ENCHANTING),
    MULTIPLE_ENCHANTING_PERMISSION_ENABLED("Permission_Enabled", Section.MULTIPLE_ENCHANTING),
    MULTIPLE_ENCHANTING_CONFLICTING_ENABLED("Conflicting_Enabled", Section.MULTIPLE_ENCHANTING),
    LEVEL_STACKING_ENABLED("Enabled", Section.LEVEL_STACKING),
    LEVEL_STACKING_OVERRIDES("Overrides", Section.LEVEL_STACKING),
    LEVEL_COST_INCREASE_ENABLED("Enabled", Section.LEVEL_COST_INCREASE),
    LEVEL_COST_INCREASE_AMOUNT("Amount", Section.LEVEL_COST_INCREASE),
    MANUAL_ENCHANTING_ENABLED("Enabled", Section.MANUAL_ENCHANTING),
    MANUAL_ENCHANTING_CONFLICTING_ENABLED("Conflicting_Enabled", Section.MANUAL_ENCHANTING),
    MANUAL_ENCHANTING_INAPPLICABLE_ENABLED("Inapplicable_Enabled", Section.MANUAL_ENCHANTING),
    MANUAL_ENCHANTING_AMOUNT_ENABLED("Amount_Enabled", Section.MANUAL_ENCHANTING),
    POWER_SOURCE_ENABLED("Enabled", Section.POWER_SOURCE),
    POWER_SOURCE_RANGE("Range", Section.POWER_SOURCE),
    LEVEL_RESTRICTION_ENABLED("Enabled", Section.LEVEL_RESTRICTION),
    LEVEL_RESTRICTION_AMOUNT("Amount", Section.LEVEL_RESTRICTION),
    LEVEL_RESTRICTION_OVERRIDES("Overrides", Section.LEVEL_RESTRICTION),
    LEVEL_COST_ENABLED("Enabled", Section.LEVEL_COST),
    LEVEL_COST_BASE_AMOUNT("Base_Amount", Section.LEVEL_COST),
    LEVEL_COST_REGULAR_AMOUNT("Regular_Amount", Section.LEVEL_COST),
    LEVEL_COST_OVERRIDES("Overrides", Section.LEVEL_COST),
    LEVEL_REFUND_ENABLED("Enabled", Section.LEVEL_REFUND),
    LEVEL_REFUND_AMOUNT("Amount", Section.LEVEL_REFUND),
    LEVEL_REFUND_OVERRIDES("Overrides", Section.LEVEL_REFUND);

    private final String path;

    Setting(String path) {
        this.path = path;
    }

    Setting(String path, Section section) {
        this(section.getPath() + "." + path);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        String[] split = name().toLowerCase().split("_");
        return Arrays.stream(split).map(StringUtils::capitalize).collect(Collectors.joining("_"));
    }
}
