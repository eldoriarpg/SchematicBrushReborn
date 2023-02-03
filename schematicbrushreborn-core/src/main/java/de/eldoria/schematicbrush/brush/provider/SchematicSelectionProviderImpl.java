/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.provider.SchematicSelectionProvider;
import de.eldoria.schematicbrush.brush.config.schematics.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SchematicSelectionProviderImpl extends SchematicSelectionProvider {
    /**
     * Create a new settings provider
     *
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     */
    public SchematicSelectionProviderImpl(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    public static final SchematicSelectionProvider LOCKED_ORDERED_SELECTION = new SchematicSelectionProviderImpl(LockedOrderedSelection.class, "locked_ordered") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new LockedOrderedSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new LockedOrderedSelection();
        }

        @Override
        public String description() {
            return "A selector which only skips to the next schematic when prompted. Next schematic will be the next in the order.";
        }
    };

    public static final SchematicSelectionProvider LOCKED_RANDOM_SELECTION = new SchematicSelectionProviderImpl(LockedRandomSelection.class, "locked_random") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new LockedRandomSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new LockedRandomSelection();
        }

        @Override
        public String description() {
            return "A selector which only skips to the next schematic when prompted. Next schematic will be random.";
        }
    };
    public static final SchematicSelectionProvider RANDOM_SELECTION = new SchematicSelectionProviderImpl(RandomSelection.class, "random") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new RandomSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new RandomSelection();
        }

        @Override
        public String description() {
            return "A selector which skips to the next schematic.";
        }
    };
    public static final SchematicSelectionProvider ORDERED_SELECTION = new SchematicSelectionProviderImpl(OrderedSelection.class, "ordered") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new RandomSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new RandomSelection();
        }

        @Override
        public String description() {
            return "A selector which skips to the next schematic in the current order.";
        }
    };
}
