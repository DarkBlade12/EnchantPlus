package com.darkblade12.enchantplus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public abstract class AbstractPlugin extends JavaPlugin {
    protected final Logger logger;

    public AbstractPlugin() {
        logger = getLogger();
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract boolean onReload();

    @Override
    public FileConfiguration getConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        return super.getConfig();
    }

    public abstract String getPrefix();
}