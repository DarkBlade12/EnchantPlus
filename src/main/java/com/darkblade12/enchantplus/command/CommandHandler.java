package com.darkblade12.enchantplus.command;

import com.darkblade12.enchantplus.AbstractPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CommandHandler<P extends AbstractPlugin> implements CommandExecutor {
    private static final String UNKNOWN_COMMAND = "§cUnknown command, take a look at the help pages with\n§6<usage>";
    private static final String INVALID_USAGE = "§cInvalid usage, try §6<usage>";
    private static final String NO_CONSOLE_EXECUTOR = "§cThis command can't be executed as console!";
    private static final String NO_PERMISSION = "§cYou don't have permission for this command!";
    private final P plugin;
    private final String defaultLabel;
    private final Map<String, AbstractCommand<P>> commands;

    @SafeVarargs
    public CommandHandler(P plugin, String defaultLabel, int pageCommands, AbstractCommand<P>... commands) throws IllegalArgumentException {
        this.plugin = plugin;
        this.defaultLabel = defaultLabel;
        this.commands = new LinkedHashMap<>();
        for (AbstractCommand<P> command : commands) {
            String name = command.getName();
            if (this.commands.containsKey(name) || name.equals("help")) {
                throw new IllegalArgumentException("Duplicate command names");
            }
            this.commands.put(name, command);
        }
        this.commands.put("help", new HelpCommand(this, pageCommands));
    }

    public void register() throws IllegalArgumentException {
        PluginCommand command = plugin.getCommand(defaultLabel);
        if (command == null) {
            throw new IllegalArgumentException("The command '" + defaultLabel + "' is not registered in the plugin.yml");
        } else {
            command.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] arguments) {
        if (arguments.length == 0) {
            displayUnknownCommand(sender, label);
            return true;
        }
        AbstractCommand<P> command = commands.get(arguments[0].toLowerCase());
        if (command == null) {
            displayUnknownCommand(sender, label);
            return true;
        }
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        if (!command.isExecutableAsConsole() && !(sender instanceof Player)) {
            sender.sendMessage(NO_CONSOLE_EXECUTOR);
            return true;
        }
        if (!command.hasPermission(sender)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (!checkUsage(command, parameters)) {
            displayInvalidUsage(sender, command, label);
            return true;
        }
        command.execute(plugin, this, sender, label, parameters);
        return true;
    }

    public void displayPluginMessage(CommandSender sender, String message) {
        sender.sendMessage(plugin.getPrefix() + " " + message);
    }

    private void displayUnknownCommand(CommandSender sender, String label) {
        sender.sendMessage(UNKNOWN_COMMAND.replace("<usage>", getUsage(commands.get("help"), label)));
    }

    public void displayInvalidUsage(CommandSender sender, AbstractCommand<P> command, String label) {
        sender.sendMessage(INVALID_USAGE.replace("<usage>", getUsage(command, label)));
    }

    private boolean checkUsage(AbstractCommand<P> command, String[] parameters) {
        String[] commandParameters = command.getParameters();
        int[] limits = new int[2];
        for (String parameter : commandParameters) {
            limits[1]++;
            if (!parameter.matches("\\[.*\\]")) {
                limits[0]++;
            }
        }
        return parameters.length >= limits[0] && (command.hasInfiniteParameters() || parameters.length <= limits[1]);
    }

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public Map<String, AbstractCommand<P>> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public String getUsage(AbstractCommand<P> command, String label) {
        StringBuilder builder = new StringBuilder("/" + label + " " + command.getName());
        String[] parameters = command.getParameters();
        for (String parameter : parameters) {
            builder.append(" ").append(parameter);
        }
        return builder.toString();
    }

    public String getUsage(AbstractCommand<P> command) {
        return getUsage(command, defaultLabel);
    }

    private final class HelpCommand extends AbstractCommand<P> {
        private static final String INVALID_PAGE = "§cThis help page doesn't exist!";
        private static final String INVALID_INPUT = "§6<input> §cisn't numeric!";
        private final CommandHelpPage<P> helpPage;

        public HelpCommand(CommandHandler<P> handler, int pageCommands) {
            helpPage = new CommandHelpPage<>(handler, pageCommands);
        }

        @Override
        public void execute(P plugin, CommandHandler<P> handler, CommandSender sender, String label, String[] parameters) {
            int page = 1;
            if (parameters.length == 1) {
                String input = parameters[0];
                try {
                    page = Integer.parseInt(input);
                } catch (NumberFormatException exception) {
                    handler.displayPluginMessage(sender, INVALID_INPUT.replace("<input>", input));
                    return;
                }
                if (!helpPage.hasPage(sender, page)) {
                    handler.displayPluginMessage(sender, INVALID_PAGE);
                    return;
                }
            }
            helpPage.displayPage(sender, label, page);
        }

        @Override
        public String getName() {
            return "help";
        }

        @Override
        public String[] getParameters() {
            return new String[] { "[page]" };
        }

        @Override
        public String getDescription() {
            return "Displays a help page";
        }
    }
}