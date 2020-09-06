package com.darkblade12.enchantplus.plugin.command;

import org.bukkit.command.CommandSender;

public interface PermissionProvider {
    PermissionProvider NONE = new PermissionProvider() {
        @Override
        public boolean test(CommandSender sender) {
            return true;
        }

        @Override
        public String getName() {
            return "none";
        }
    };

    boolean test(CommandSender sender);

    String getName();
}
