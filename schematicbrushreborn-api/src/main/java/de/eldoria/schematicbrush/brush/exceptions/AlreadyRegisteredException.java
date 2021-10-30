package de.eldoria.schematicbrush.brush.exceptions;

import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(String message) {
        super(message);
    }

    public AlreadyRegisteredException(SettingProvider<?> provider) {
        super("Provider for " + provider.name() + " is already registered");
    }

    public AlreadyRegisteredException(Nameable nameable, SettingProvider<?> provider) {
        super("Provider for " + provider.name() + "@" + nameable.name() + " is already registered");
    }
}
