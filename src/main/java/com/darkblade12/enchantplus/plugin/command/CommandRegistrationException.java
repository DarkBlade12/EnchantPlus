package com.darkblade12.enchantplus.plugin.command;

public class CommandRegistrationException extends Exception {
    public CommandRegistrationException(String message, String name) {
        super(String.format(message, name));
    }

    public CommandRegistrationException(String message, String name, Throwable cause) {
        super(String.format(message, name), cause);
    }
}
