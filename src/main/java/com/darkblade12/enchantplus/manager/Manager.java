package com.darkblade12.enchantplus.manager;

import com.darkblade12.enchantplus.AbstractPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Manager<P extends AbstractPlugin> implements Listener {
    protected final P plugin;

    public Manager(P plugin) {
        this.plugin = plugin;
    }

    public abstract void onEnable() throws Exception;

    public void onDisable() {
    }

    public void onReload() throws Exception {
        onDisable();
        onEnable();
    }

    protected final void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected final void unregisterAll() {
        HandlerList.unregisterAll(this);
    }
}