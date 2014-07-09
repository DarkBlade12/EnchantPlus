package com.darkblade12.enchantplus.command;

import org.bukkit.command.CommandSender;

import com.darkblade12.enchantplus.AbstractPlugin;
import com.darkblade12.enchantplus.permission.Permission;

public abstract class AbstractCommand<P extends AbstractPlugin> {
	public abstract void execute(P plugin, CommandHandler<P> handler, CommandSender sender, String label, String[] parameters);

	public abstract String getName();

	public String[] getParameters() {
		return new String[0];
	}

	public boolean hasInfiniteParameters() {
		return false;
	}

	public Permission getPermission() {
		return Permission.NONE;
	}

	public final boolean hasPermission(CommandSender sender) {
		return getPermission().has(sender);
	}

	public boolean isExecutableAsConsole() {
		return true;
	}

	public String getDescription() {
		return "No description available";
	}
}