package com.darkblade12.enchantplus.permission;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Permission {
    NONE("None") {
        @Override
        public boolean has(CommandSender sender) {
            return true;
        }
    },
    LIMIT_BYPASS("EnchantPlus.bypass.limit"),
    CONFLICTING_BYPASS("EnchantPlus.bypass.conflicting"),
    INAPPLICABLE_BYPASS("EnchantPlus.bypass.inapplicable"),
    AMOUNT_BYPASS("EnchantPlus.bypass.amount"),
    POWER_BYPASS("EnchantPlus.bypass.power"),
    RESTRICTION_BYPASS("EnchantPlus.bypass.restriction"),
    COST_BYPASS("EnchantPlus.bypass.cost"),
    BYPASS_WILDCARD("EnchantPlus.bypass.*", LIMIT_BYPASS, CONFLICTING_BYPASS, INAPPLICABLE_BYPASS, AMOUNT_BYPASS, POWER_BYPASS, RESTRICTION_BYPASS, COST_BYPASS),
    MULTIPLE_MECHANIC("EnchantPlus.mechanic.multiple"),
    ADD_COMMAND("EnchantPlus.command.add"),
    MIGHTY_COMMAND("EnchantPlus.command.mighty"),
    REMOVE_COMMAND("EnchantPlus.command.remove"),
    PURFIY_COMMAND("EnchantPlus.command.purify"),
    LIST_COMMAND("EnchantPlus.command.list"),
    DESCRIPTION_COMMAND("EnchantPlus.command.description"),
    APPLICABLE_COMMAND("EnchantPlus.command.applicable"),
    RELOAD_COMMAND("EnchantPlus.command.reload"),
    COMMAND_WILDCARD("EnchantPlus.command.*", ADD_COMMAND, MIGHTY_COMMAND, REMOVE_COMMAND, PURFIY_COMMAND, LIST_COMMAND, DESCRIPTION_COMMAND, APPLICABLE_COMMAND, RELOAD_COMMAND),
    PLUGIN_WILDCARD("EnchantPlus.*", BYPASS_WILDCARD, MULTIPLE_MECHANIC, COMMAND_WILDCARD);

    private static final Map<String, Permission> NAME_MAP = new HashMap<>();
    private static final Map<String, Permission> NODE_MAP = new HashMap<>();
    private final String node;
    private final List<Permission> inherited;

    static {
        for (Permission permission : values()) {
            NAME_MAP.put(permission.name(), permission);
            if (permission != NONE) {
                NODE_MAP.put(permission.node, permission);
            }
        }
    }

    Permission(String node, Permission... inherited) {
        this.node = node;
        this.inherited = Arrays.asList(inherited);
    }

    public String getNode() {
        return node;
    }

    public List<Permission> getInherited() {
        return inherited;
    }

    public boolean has(CommandSender sender) {
        return sender.hasPermission(node) || sender.hasPermission(node.toLowerCase()) || hasParent(sender);
    }

    public boolean hasParent(CommandSender sender) {
        for (Permission permission : values()) {
            if (permission.getInherited().contains(this) && permission.has(sender)) {
                return true;
            }
        }
        return false;
    }

    public static Permission fromName(String name) {
        return name == null ? null : NAME_MAP.get(name.toUpperCase());
    }

    public static Permission fromNode(String node) {
        return node == null ? null : NAME_MAP.get(node);
    }
}