package com.darkblade12.enchantplus.settings;

import com.darkblade12.enchantplus.plugin.settings.PathProvider;

enum Section implements PathProvider {
    NATURAL_ENCHANTING("Natural_Enchanting"),
    LEVEL_LIMIT("Level_Limit", NATURAL_ENCHANTING),
    MULTIPLE_ENCHANTING("Multiple_Enchanting", NATURAL_ENCHANTING),
    LEVEL_STACKING("Level_Stacking", MULTIPLE_ENCHANTING),
    LEVEL_COST_INCREASE("Level_Cost_Increase", MULTIPLE_ENCHANTING),
    MANUAL_ENCHANTING("Manual_Enchanting"),
    POWER_SOURCE("Power_Source", MANUAL_ENCHANTING),
    LEVEL_RESTRICTION("Level_Restriction", MANUAL_ENCHANTING),
    LEVEL_COST("Level_Cost", MANUAL_ENCHANTING),
    LEVEL_REFUND("Level_Refund", LEVEL_COST);

    private final String path;
    private final Section parent;

    Section(String path, Section parent) {
        this.path = path;
        this.parent = parent;
    }

    Section(String path) {
        this(path, null);
    }

    @Override
    public String getPath() {
        return parent == null ? path : parent.getPath() + "." + path;
    }
}
