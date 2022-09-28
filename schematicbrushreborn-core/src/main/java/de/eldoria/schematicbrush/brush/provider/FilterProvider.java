/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.InputArgument;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.blockfilter.BlockFilter;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class FilterProvider extends ModifierProvider {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    public static final FilterProvider BLOCK_FILTER = new FilterProvider(BlockFilter.class, "BlockFilter", "provider.filter.blockFilter.name") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            if ("none".equalsIgnoreCase(args.asString(0))) {
                return defaultSetting();
            }
            try {
                var actor = BukkitAdapter.adapt((Player) args.sender());
                WORLD_EDIT.getMaskFactory().parseFromInput(args.join(), PasteMutation.createContext(actor, actor.getWorld(), actor.getWorld()));
                return new BlockFilter(args.join());
            } catch (InputParseException e) {
                throw CommandException.message(e.getMessage());
            }
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return WORLD_EDIT.getMaskFactory().getSuggestions(args.asString(args.size() - 1));
        }

        @Override
        public Argument[] arguments() {
            return new Argument[]{InputArgument.input("mask", true)};
        }

        @Override
        public boolean hasArguments() {
            return true;
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new BlockFilter("");
        }

        @Override
        public String description() {
            return "provider.filter.blockFilter.description";
        }
    };

    /**
     * Default constructor
     *
     * @param clazz class which is provided
     * @param name  name of provider
     */
    public FilterProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localeKey) {
        super(clazz, name, localeKey);
    }
}
