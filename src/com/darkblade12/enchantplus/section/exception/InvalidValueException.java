package com.darkblade12.enchantplus.section.exception;

import com.darkblade12.enchantplus.section.IndependantConfigurationSection;

public final class InvalidValueException extends Exception {
	private static final long serialVersionUID = 1256236386484655224L;

	public InvalidValueException(String name, IndependantConfigurationSection section, String description) {
		super("The value of '" + name + "' in section '" + section.getName() + "' " + description);
	}
	
	public InvalidValueException(String name, String description) {
		super("The value of '" + name + "' " + description);
	}
}