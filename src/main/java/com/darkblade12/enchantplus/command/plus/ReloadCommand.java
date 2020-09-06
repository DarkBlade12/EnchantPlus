package com.darkblade12.enchantplus.command.plus;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.Permission;
import com.darkblade12.enchantplus.plugin.command.CommandBase;
import org.bukkit.command.CommandSender;

public final class ReloadCommand extends CommandBase<EnchantPlus> {
    public ReloadCommand() {
        super("reload", Permission.COMMAND_RELOAD);
    }

    @Override
    public void execute(EnchantPlus plugin, CommandSender sender, String label, String[] args) {
        long time = System.currentTimeMillis();
        if (!plugin.onReload()) {
            plugin.sendMessage(sender, "command.plus.reload.failed");
            return;
        }

        long duration = System.currentTimeMillis() - time;
        plugin.sendMessage(sender, "command.plus.reload.succeeded", plugin.getVersion(), duration);
    }
}
