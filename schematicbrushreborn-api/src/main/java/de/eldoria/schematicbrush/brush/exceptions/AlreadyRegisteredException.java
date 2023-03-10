/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.exceptions;

import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Exception which indicated that something is already registered and can not be registered again.
 */
public class AlreadyRegisteredException extends RuntimeException {
    /**
     * Creates a new exception with a message
     *
     * @param message message
     */
    public AlreadyRegisteredException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message based on the provider name
     *
     * @param provider provider
     */
    public AlreadyRegisteredException(SettingProvider<?> provider) {
        super("Provider for " + provider.name() + " is already registered");
    }

    /**
     * Creates a new exception with a message based on the nameable and provider name
     *
     * @param nameable nameable
     * @param provider provider
     */
    public AlreadyRegisteredException(Nameable nameable, SettingProvider<?> provider) {
        super("Provider for " + provider.name() + "@" + nameable.name() + " is already registered");
    }
}
