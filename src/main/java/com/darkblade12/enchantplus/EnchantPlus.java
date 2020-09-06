package com.darkblade12.enchantplus;

import com.darkblade12.enchantplus.command.PlusCommandHandler;
import com.darkblade12.enchantplus.enchantment.EnchantingManager;
import com.darkblade12.enchantplus.plugin.PluginBase;
import com.darkblade12.enchantplus.plugin.command.CommandRegistrationException;
import com.darkblade12.enchantplus.plugin.settings.InvalidValueException;
import com.darkblade12.enchantplus.settings.Settings;

import java.util.Locale;

public final class EnchantPlus extends PluginBase {
    private final Settings settings;
    private final PlusCommandHandler commandHandler;
    private final EnchantingManager enchantingManager;

    public EnchantPlus() {
        super(48784, 5366, "§8§l[§b§oEnchant§7§oPlus§8§l]§r");
        settings = new Settings(this);
        commandHandler = new PlusCommandHandler(this);
        enchantingManager = new EnchantingManager(this);
    }

    @Override
    public boolean load() {
        try {
            settings.load();
        } catch (Exception e) {
            logException(e, "Failed to load settings from the configuration file!");
            return false;
        }

        try {
            commandHandler.enable();
        } catch (CommandRegistrationException e) {
            logException(e, "Failed to register commands!");
            return false;
        }

        try {
            enchantingManager.enable();
        } catch (Exception e) {
            logException(e, "Failed to enable the enchanting manager!");
            return false;
        }

        return true;
    }

    @Override
    public void unload() {
        enchantingManager.disable();
    }

    @Override
    public boolean reload() {
        try {
            settings.reload();
        } catch (Exception e) {
            logException(e, "Failed to load settings from the configuration file!");
            return false;
        }

        enchantingManager.reload();
        return true;
    }

    @Override
    public Locale getCurrentLocale() {
        return Locale.ENGLISH;
    }

    public Settings getSettings() {
        return settings;
    }
}
