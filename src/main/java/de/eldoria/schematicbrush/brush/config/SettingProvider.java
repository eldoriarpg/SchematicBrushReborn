package de.eldoria.schematicbrush.brush.config;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SettingProvider<T> {
    protected final String name;

    public SettingProvider(String name) {
        assert !name.isBlank();
        this.name = name;
    }

    public boolean isMatch(Arguments args) {
        return name.equalsIgnoreCase(args.asString(0));
    }

    public abstract T parse(Arguments args) throws CommandException;

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
