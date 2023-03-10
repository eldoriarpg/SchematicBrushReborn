/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.schematicbrush.brush.config.provider.SelectorProvider;
import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class SelectorProviderImpl extends SelectorProvider {

    public static final Function<SchematicRegistry, SelectorProvider> DIRECTORY = registry ->
            new SelectorProviderImpl(DirectorySelector.class, "Directory", registry) {
                private final Argument[] arguments = {Argument.unlocalizedInput("Directory", true),
                        Argument.unlocalizedInput("name_filter", false)};

                @Override
                public Selector parse(Arguments args) {
                    return new DirectorySelector(args.asString(0), args.asString(1, "*"));
                }

                @Override
                public Argument[] arguments() {
                    return arguments;
                }

                @Override
                public String description() {
                    return "Select schematics in a directory.\nUse dirname/* to select subdirectories as well.\nUse the second argument name filter to filter schematics in these directories";
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().get(SchematicCache.STORAGE).getMatchingDirectories(player, args.asString(0), 50);
                    }

                    if (args.size() == 2) {
                        return Collections.singletonList("<name filter>");
                    }

                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> NAME = registry ->
            new SelectorProviderImpl(NameSelector.class, "Name", registry) {
                private final Argument[] arguments = {Argument.unlocalizedInput("Name", true)};

                @Override
                public Selector parse(Arguments args) {
                    return new NameSelector(args.asString(0));
                }

                @Override
                public Argument[] arguments() {
                    return arguments;
                }

                @Override
                public String description() {
                    return "Select schematics by name.\nUse a * as a wildcard.";
                }

                @Override
                public List<String> complete(Arguments args, Player player) {
                    if (args.size() == 1) {
                        return registry().get(SchematicCache.STORAGE).getMatchingSchematics(player, args.asString(0), 50);
                    }
                    return Collections.emptyList();
                }
            };

    public static final Function<SchematicRegistry, SelectorProvider> REGEX = registry ->
            new SelectorProviderImpl(RegexSelector.class, "Regex", registry) {
                private final Argument[] arguments = {Argument.unlocalizedInput("Regex", true)};

                @Override
                public Selector parse(Arguments args) {
                    return new RegexSelector(args.asString(0));
                }

                @Override
                public Argument[] arguments() {
                    return arguments;
                }

                @Override
                public String description() {
                    return "Select schematics with a regex.\nWanna try it out first? Use regex101.com";
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
