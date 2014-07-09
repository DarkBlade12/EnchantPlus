package com.darkblade12.enchantplus;

import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.command.types.AddCommand;
import com.darkblade12.enchantplus.command.types.ApplicableCommand;
import com.darkblade12.enchantplus.command.types.DescriptionCommand;
import com.darkblade12.enchantplus.command.types.ListCommand;
import com.darkblade12.enchantplus.command.types.MightyCommand;
import com.darkblade12.enchantplus.command.types.PurifyCommand;
import com.darkblade12.enchantplus.command.types.ReloadCommand;
import com.darkblade12.enchantplus.command.types.RemoveCommand;
import com.darkblade12.enchantplus.enchantment.EnchantmentCalculator;
import com.darkblade12.enchantplus.manager.types.EnchantingManager;
import com.darkblade12.enchantplus.metrics.MetricsLite;

public final class EnchantPlus extends AbstractPlugin {
	private final Settings settings;
	private final EnchantmentCalculator calculator;
	private final CommandHandler<EnchantPlus> commandHandler;
	private final EnchantingManager enchantingManager;

	public EnchantPlus() {
		settings = new Settings(this);
		calculator = new EnchantmentCalculator(settings);
		commandHandler = new CommandHandler<EnchantPlus>(this, "plus", 6, new AddCommand(), new MightyCommand(), new RemoveCommand(), new PurifyCommand(), new ListCommand(), new DescriptionCommand(), new ApplicableCommand(), new ReloadCommand());
		enchantingManager = new EnchantingManager(this);
	}

	@Override
	public void onEnable() {
		long time = System.currentTimeMillis();
		try {
			settings.onLoad();
		} catch (Exception exception) {
			logger.warning("An error occurred while loading the settings from config.yml, plugin will disable! Cause: " + exception.getMessage());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			commandHandler.register();
		} catch (Exception exception) {
			logger.warning("An error occurred while registering the command handler, plugin will disable! Cause: " + exception.getMessage());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			enchantingManager.onEnable();
		} catch (Exception exception) {
			logger.warning("An error occurred while enabling the enchanting manager, plugin will disable! Cause: " + exception.getMessage());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			MetricsLite metrics = new MetricsLite(this);
			if (metrics.isOptOut()) {
				logger.warning("Metrics are disabled!");
			} else {
				logger.info("This plugin is using Metrics by Hidendra!");
				metrics.start();
			}
		} catch (Exception exception) {
			logger.info("An error occurred while enabling Metrics!");
		}
		logger.info("Plugin version " + getDescription().getVersion() + " activated! (" + (System.currentTimeMillis() - time) + " ms)");
	}

	@Override
	public void onDisable() {
		enchantingManager.onDisable();
		logger.info("Plugin deactivated!");
	}

	@Override
	public boolean onReload() {
		try {
			settings.onReload();
		} catch (Exception exception) {
			logger.warning("An error occurred while reloading the settings form config.yml, plugin will disable! Cause: " + exception.getMessage());
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		try {
			enchantingManager.onReload();
		} catch (Exception exception) {
			logger.warning("An error occurred while reloading the enchanting manager, plugin will disable! Cause: " + exception.getMessage());
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}

	@Override
	public String getPrefix() {
		return "§8§l[§b§oEnchant§7§oPlus§8§l]§r";
	}

	public Settings getSettings() {
		return settings;
	}

	public EnchantmentCalculator getCalculator() {
		return calculator;
	}

	public CommandHandler<EnchantPlus> getCommandHandler() {
		return commandHandler;
	}

	public EnchantingManager getEnchantingManager() {
		return enchantingManager;
	}
}