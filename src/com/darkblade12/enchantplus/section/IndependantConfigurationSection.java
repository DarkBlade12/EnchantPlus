package com.darkblade12.enchantplus.section;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import com.darkblade12.enchantplus.section.exception.SectionNotFoundException;

public final class IndependantConfigurationSection {
	private final String path;
	private final String name;

	public IndependantConfigurationSection(String path) {
		this.path = path;
		name = path.substring(path.lastIndexOf('.') + 1);
	}

	public IndependantConfigurationSection(IndependantConfigurationSection parent, String name) {
		this(parent.getPath() + "." + name);
	}

	public static IndependantConfigurationSection fromConfigurationSection(ConfigurationSection section) {
		return new IndependantConfigurationSection(section.getCurrentPath());
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