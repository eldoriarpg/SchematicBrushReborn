package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.SettingProvider;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class SelectorProvider extends SettingProvider<Selector> {

    public static final Function<SchematicRegistry, SelectorProvider> DIRECTORY = registry ->
            new SelectorProvider(DirectorySelector.class, "directory", registry) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new DirectorySelector(args.asString(0), args.asString(1));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().getCache(SchematicBrushCache.key).getMatchingDirectories(player, args.asString(0), 50);
                    }

                    if (args.size() == 2) {
                        return Collections.singletonList("<name filter>");
                    }

                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> NAME = registry ->
            new SelectorProvider(NameSelector.class, "name", registry) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new NameSelector(args.asString(0));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().getCache(SchematicBrushCache.key).getMatchingSchematics(player, args.asString(0), 50);
                    }
                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> REGEX = registry ->
            new SelectorProvider(RegexSelector.class, "regex", registry) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new NameSelector(args.asString(0));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return Collections.singletonList("<regex>");
                    }
                    return Collections.emptyList();
                }
            };

    private final SchematicRegistry registry;

    public SelectorProvider(Class<? extends ConfigurationSerializable> clazz, String name, SchematicRegistry registry) {
        super(clazz, name);
        this.registry = registry;
    }

    @Override
    public Selector defaultSetting() {
        return new NameSelector("*");
    }

    public SchematicRegistry registry() {
        return registry;
    }
}
