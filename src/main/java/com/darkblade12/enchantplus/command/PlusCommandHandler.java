package com.darkblade12.enchantplus.command;

import com.darkblade12.enchantplus.EnchantPlus;
import com.darkblade12.enchantplus.command.plus.AddCommand;
import com.darkblade12.enchantplus.command.plus.ApplicableCommand;
import com.darkblade12.enchantplus.command.plus.DescriptionCommand;
import com.darkblade12.enchantplus.command.plus.ListCommand;
import com.darkblade12.enchantplus.command.plus.MightyCommand;
import com.darkblade12.enchantplus.command.plus.PurifyCommand;
import com.darkblade12.enchantplus.command.plus.ReloadCommand;
import com.darkblade12.enchantplus.command.plus.RemoveCommand;
import com.darkblade12.enchantplus.plugin.command.CommandHandler;
import com.darkblade12.enchantplus.plugin.command.CommandRegistrationException;

public final class PlusCommandHandler extends CommandHandler<EnchantPlus> {
    public PlusCommandHandler(EnchantPlus plugin) {
        super(plugin, "plus", 6);
    }

    @Override
    protected void registerCommands() throws CommandRegistrationException {
        registerCommand(AddCommand.class);
        registerCommand(MightyCommand.class);
        registerCommand(RemoveCommand.class);
        registerCommand(PurifyCommand.class);
        registerCommand(ListCommand.class);
        registerCommand(DescriptionCommand.class);
        registerCommand(ApplicableCommand.class);
        registerCommand(ReloadCommand.class);
    }
}
