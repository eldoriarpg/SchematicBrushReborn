/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.ILocalizer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.List;
import java.util.Objects;

/**
 * Base class for the provider classes.
 * <p>
 * See implementation to use them.
 *
 * @param <T> type of provided class. Must be of type ConfigurationSerializable
 * @see ModifierProvider implementation to provide modifier
 * @see SelectorProvider implementation to provide selector
 */
public abstract class SettingProvider<T extends ConfigurationSerializable> {
    /**
     * Name of the provider
     */
    protected final String name;
    protected final String description;
    private final String localizedName;
    private final Class<? extends ConfigurationSerializable> clazz;

    /**
     * Create a new settings provider
     *
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     * @deprecated Use {@link #SettingProvider(Class, String, String, String)} and provide a localized name and description
     */
    @Deprecated(forRemoval = true)
    public SettingProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        this(clazz, name, null, null);
    }

    /**
     * Create a new settings provider
     *
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     * @param description   A description. Might be a string or a property key
     * @deprecated Use {@link #SettingProvider(Class, String, String, String)} and provide a localized name and description
     */
    @Deprecated(forRemoval = true)
    public SettingProvider(Class<? extends ConfigurationSerializable> clazz, String name, String description) {
        this(clazz, name, null, description);
    }

    /**
     * Create a new settings provider
     *
     * @param clazz         which is returned by the provider
     * @param name          name. Must be unique inside the provider.
     * @param localizedName The property key for the name
     * @param description   A description. Might be a string or a property key
     */
    public SettingProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        this.clazz = clazz;
        this.description = description;
        this.localizedName = Objects.requireNonNullElse(localizedName, name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name of provider can not be blank");
        }
        this.name = name;
    }

    /**
     * Checks if the first argument matches the name.
     *
     * @param args arguments
     * @return true if the first argument is equal to the name, ignoring case.
     */
    public boolean isMatch(Arguments args) {
        return name.equalsIgnoreCase(args.asString(0));
    }

    /**
     * class which will be returned by the provider.
     *
     * @return the class
     */
    public Class<? extends ConfigurationSerializable> serializationClass() {
        return clazz;
    }

    /**
     * Parse the arguments to the provided class if possible
     *
     * @param args args to parse
     * @return instance of the provided class
     * @throws CommandException if the arguments can't be parsed.
     */
    public abstract T parse(Arguments args) throws CommandException;

    /**
     * Return the required and optional arguments to parse this setting.
     *
     * @return the arguments of the setting
     */
    public Argument[] arguments() {
        return new Argument[0];
    }

    /**
     * Defines whether the provider requires arguments or not.
     *
     * @return true if arguments are required
     */
    public boolean hasArguments() {
        return arguments().length != 0;
    }

    /**
     * Returns the command type to use when building buttons. Will differ between suggesting and running a command.
     *
     * @return string to build command.
     */
    public String commandType() {
        return hasArguments() ? "suggest_command" : "run_command";
    }

    /**
     * Method to complete the arguments for the class provided by this provider
     *
     * @param args   args to complete
     * @param player player which requests completion
     * @return list of strings for the current argument
     */
    public abstract List<String> complete(Arguments args, Player player) throws CommandException;

    /**
     * Name of this provider
     *
     * @return unique name
     */
    public String name() {
        return name;
    }

    public String localizedName() {
        if (ILocalizer.isLocaleCode(localizedName)) {
            return ILocalizer.escape(localizedName);
        }
        return localizedName;
    }

    /**
     * Provides a default instance for this type
     *
     * @return instance of default setting
     */
    public abstract T defaultSetting();

    /**
     * Returns the permission for this setting.
     * <p>
     * Leave blank when no permission is required.
     *
     * @return the permission
     */
    public String permission() {
        return "";
    }

    /**
     * Checks if the provider requires a permission
     *
     * @return true if a permission is required.
     */
    public boolean hasPermission() {
        return !permission().isBlank();
    }

    /**
     * Checks if the {@link Permissible} can use this setting.
     *
     * @param permissible the permissible to check
     * @return true if it has the permission.
     */
    public boolean hasPermission(Permissible permissible) {
        if (!hasPermission()) return true;
        return permissible.hasPermission(permission());
    }

    /**
     * Provides a short description of the setting.
     *
     * @return the setting description
     */
    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingProvider<?> provider)) return false;

        return name.equals(provider.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String localizedDescription() {
        if (ILocalizer.isLocaleCode(description())) {
            return ILocalizer.escape(description());
        }
        return description();
    }
}
