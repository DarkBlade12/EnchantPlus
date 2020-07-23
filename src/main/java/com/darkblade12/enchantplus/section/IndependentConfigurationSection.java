package com.darkblade12.enchantplus.section;

import com.darkblade12.enchantplus.section.exception.SectionNotFoundException;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public final class IndependentConfigurationSection {
    private final String path;
    private final String name;

    public IndependentConfigurationSection(String path) {
        this.path = path;
        name = path.substring(path.lastIndexOf('.') + 1);
    }

    public IndependentConfigurationSection(IndependentConfigurationSection parent, String name) {
        this(parent.getPath() + "." + name);
    }

    public static IndependentConfigurationSection fromConfigurationSection(ConfigurationSection section) {
        return new IndependentConfigurationSection(section.getCurrentPath());
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public ConfigurationSection getConfigurationSection(Configuration root, boolean validate) throws SectionNotFoundException {
        ConfigurationSection section = root.getConfigurationSection(path);
        if (validate && section == null) {
            throw new SectionNotFoundException(this);
        }
        return section;
    }

    public ConfigurationSection getConfigurationSection(Configuration root) throws SectionNotFoundException {
        return getConfigurationSection(root, true);
    }
}