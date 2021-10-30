package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.provider.SelectorProvider;
import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicBrushCache;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class SelectorProviderImpl extends SelectorProvider {

    public static final Function<SchematicRegistry, SelectorProvider> DIRECTORY = registry ->
            new SelectorProviderImpl(DirectorySelector.class, "directory", registry) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new DirectorySelector(args.asString(0), args.asString(1));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().getCache(SchematicCache.DEFAULT_CACHE).getMatchingDirectories(player, args.asString(0), 50);
                    }

                    if (args.size() == 2) {
                        return Collections.singletonList("<name filter>");
                    }

                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> NAME = registry ->
            new SelectorProviderImpl(NameSelector.class, "name", registry) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new NameSelector(args.asString(0));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().getCache(SchematicCache.DEFAULT_CACHE).getMatchingSchematics(player, args.asString(0), 50);
                    }
                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> REGEX = registry ->
            new SelectorProviderImpl(RegexSelector.class, "regex", registry) {
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

    public SelectorProviderImpl(Class<? extends Selector> clazz, String name, SchematicRegistry registry) {
        super(clazz, name, registry);
    }

    @Override
    public Selector defaultSetting() {
        return new NameSelector("*");
    }
}
