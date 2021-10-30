package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;

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
    private final Class<? extends ConfigurationSerializable> clazz;

    /**
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     */
    public SettingProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        this.clazz = clazz;
        assert !name.isBlank();
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
     * Parse the argumenmts to the provided class if possible
     *
     * @param args args to parse
     * @return instance of the provided class
     * @throws CommandException if the arguments can't be parsed.
     */
    public abstract T parse(Arguments args) throws CommandException;

    /**
     * Defines whether the provider requires arguments or not.
     *
     * @return true if arguments are required
     */
    public boolean hasArguments() {
        return true;
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
    public abstract List<String> complete(Arguments args, Player player);

    /**
     * Name of this provider
     *
     * @return unique name
     */
    public String name() {
        return name;
    }

    /**
     * Provides a default instance for this type
     *
     * @return instance of default setting
     */
    public abstract T defaultSetting();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingProvider)) return false;

        var that = (SettingProvider<?>) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
