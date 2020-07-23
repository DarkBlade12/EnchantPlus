package com.darkblade12.enchantplus.command.types;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.command.AbstractCommand;
import com.darkblade12.enchantplus.command.CommandHandler;
import com.darkblade12.enchantplus.permission.Permission;
import org.bukkit.command.CommandSender;

public final class ReloadCommand extends AbstractCommand<EnchantPlus> {
    @Override
    public void execute(EnchantPlus plugin, CommandHandler<EnchantPlus> handler, CommandSender sender, String label, String[] parameters) {
        long time = System.currentTimeMillis();
        if (!plugin.onReload()) {
            handler.displayPluginMessage(sender, "§cFailed to reload the plugin, check your server log for more information!");
            return;
        }
        handler.displayPluginMessage(sender, "§7Plugin was successfully reloaded. (" + (System.currentTimeMillis() - time) + " ms)");
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public Permission getPermission() {
        return Permission.RELOAD_COMMAND;
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }
}