package com.darkblade12.enchantplus.section.exception;

import com.darkblade12.enchantplus.section.IndependentConfigurationSection;

public final class InvalidSectionException extends Exception {
    private static final long serialVersionUID = 7273599698787710310L;

    public InvalidSectionException(String name, IndependentConfigurationSection parent, String description) {
        super("The section '" + name + "' in parent section '" + parent.getName() + "' " + description);
    }
}