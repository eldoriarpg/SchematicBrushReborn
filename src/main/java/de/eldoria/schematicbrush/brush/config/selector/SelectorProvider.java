package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.SettingProvider;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class SelectorProvider extends SettingProvider<Selector> {

    public static final Function<SchematicCache, SelectorProvider> DIRECTORY = cache ->
            new SelectorProvider("directory", cache) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new DirectorySelector(args.asString(0), args.asString(1));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return cache().getMatchingDirectories(player, args.asString(0), 50);
                    }

                    if (args.size() == 2) {
                        return Collections.singletonList("<name filter>");
                    }

                    return Collections.emptyList();
                }

            };
    public static final Function<SchematicCache, SelectorProvider> NAME = cache ->
            new SelectorProvider("name", cache) {
                @Override
                public Selector parse(Arguments args) throws CommandException {
                    return new NameSelector(args.asString(0));
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return cache().getMatchingSchematics(player, args.asString(0), 50);
                    }
                    return Collections.emptyList();
                }
            };
    public static final Function<SchematicCache, SelectorProvider> REGEX = cache ->
            new SelectorProvider("regex", cache) {
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
    private final SchematicCache cache;

    protected SelectorProvider(String name, SchematicCache cache) {
        super(name);
        this.cache = cache;
    }

    @Override
    public Selector defaultSetting() {
        return new NameSelector("*");
    }

    public SchematicCache cache() {
        return cache;
    }
}
