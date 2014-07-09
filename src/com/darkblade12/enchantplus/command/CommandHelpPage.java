package com.darkblade12.enchantplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.enchantplus.AbstractPlugin;

final class CommandHelpPage<P extends AbstractPlugin> {
	private static final String HEADER = "§8§m------------------------§8[§2Help§8]§8§m------------------------";
	private static final String FOOTER = "§8§m--------------------§8[§7Page <current_page> §7of §6§l<page_amount>§8]§m---------------------";
	private static final String COMMAND_FORMAT = "§a\u2022 <command>\n §7\u25BB <description>\n §7\u25BB Permission: §2<permission>";
	private final CommandHandler<P> handler;
	private final int pageCommands;

	public CommandHelpPage(CommandHandler<P> handler, int pageCommands) {
		this.handler = handler;
		this.pageCommands = pageCommands;
	}

	public void displayPage(CommandSender sender, String label, int page) {
		List<AbstractCommand<P>> visible = getVisibleCommands(sender);
		String header = HEADER.replace("<label>", label);
		StringBuilder builder = new StringBuilder(header);
		for (int index = (page - 1) * pageCommands; index <= page * pageCommands - 1; index++) {
			if (index > visible.size() - 1) {
				break;
			}
			builder.append("\n§r" + getInformation(visible.get(index), label));
		}
		int pages = getPages(sender);
		builder.append("\n§r" + FOOTER.replace("<current_page>", (page == pages ? "§6§l" : "§a§l") + Integer.toString(page)).replace("<page_amount>", Integer.toString(pages)));
		sender.sendMessage(builder.toString());
	}

	public boolean hasPage(CommandSender sender, int page) {
		return page > 0 && page <= getPages(sender);
	}

	private int getPages(CommandSender sender) {
		int total = getVisibleCommands(sender).size();
		int pages = total / pageCommands;
		return total % pageCommands == 0 ? pages : ++pages;
	}

	private List<AbstractCommand<P>> getVisibleCommands(CommandSender sender) {
		List<AbstractCommand<P>> visible = new ArrayList<AbstractCommand<P>>();
		for (AbstractCommand<P> command : handler.getCommands().values()) {
			if (command.hasPermission(sender)) {
				visible.add(command);
			}
		}
		return visible;
	}

	private String getInformation(AbstractCommand<P> command, String label) {
		return COMMAND_FORMAT.replace("<command>", handler.getUsage(command, label)).replace("<description>", command.getDescription()).replace("<permission>", command.getPermission().getNode());
	}
}