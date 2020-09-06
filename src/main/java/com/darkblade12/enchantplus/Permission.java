package com.darkblade12.enchantplus;

import com.darkblade12.enchantplus.plugin.command.PermissionProvider;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Permission implements PermissionProvider {
    ALL("enchantplus.*"),
    COMMAND_ALL("enchantplus.command.*", ALL),
    COMMAND_ADD("enchantplus.command.add", COMMAND_ALL),
    COMMAND_MIGHTY("enchantplus.command.mighty", COMMAND_ALL),
    COMMAND_REMOVE("enchantplus.command.remove", COMMAND_ALL),
    COMMAND_PURIFY("enchantplus.command.purify", COMMAND_ALL),
    COMMAND_LIST("enchantplus.command.list", COMMAND_ALL),
    COMMAND_DESCRIPTION("enchantplus.command.description", COMMAND_ALL),
    COMMAND_APPLICABLE("enchantplus.command.applicable", COMMAND_ALL),
    COMMAND_RELOAD("enchantplus.command.reload", COMMAND_ALL),
    BYPASS_ALL("enchantplus.bypass.*", ALL),
    BYPASS_LIMIT("enchantplus.bypass.limit", BYPASS_ALL),
    BYPASS_CONFLICTING("enchantplus.bypass.conflicting", BYPASS_ALL),
    BYPASS_INAPPLICABLE("enchantplus.bypass.inapplicable", BYPASS_ALL),
    BYPASS_AMOUNT("enchantplus.bypass.amount", BYPASS_ALL),
    BYPASS_POWER("enchantplus.bypass.power", BYPASS_ALL),
    BYPASS_RESTRICTION("enchantplus.bypass.restriction", BYPASS_ALL),
    BYPASS_COST("enchantplus.bypass.cost", BYPASS_ALL),
    MULTIPLE_ENCHANTING("enchantplus.enchanting.multiple", ALL);

    private final String name;
    private final Permission parent;

    Permission(String name, Permission parent) {
        this.name = name;
        this.parent = parent;
    }

    Permission(String name) {
        this(name, null);
    }

    @Override
    public boolean test(CommandSender sender) {
        return sender.hasPermission(name) || testParent(sender);
    }

    public boolean testParent(CommandSender sender) {
        return parent != null && parent.test(sender);
    }

    @Override
    public String getName() {
        return name;
    }

    public Permission getParent() {
        return parent;
    }

    public List<Permission> getChildren() {
        return Arrays.stream(values()).filter(p -> p.getParent() == this).collect(Collectors.toList());
    }
}
