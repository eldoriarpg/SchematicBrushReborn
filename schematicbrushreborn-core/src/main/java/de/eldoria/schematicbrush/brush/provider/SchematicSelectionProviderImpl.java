/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.provider.SchematicSelectionProvider;
import de.eldoria.schematicbrush.brush.config.schematics.LockedOrderedSelection;
import de.eldoria.schematicbrush.brush.config.schematics.LockedRandomSelection;
import de.eldoria.schematicbrush.brush.config.schematics.OrderedSelection;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SchematicSelectionProviderImpl extends SchematicSelectionProvider {
    public static final SchematicSelectionProvider LOCKED_ORDERED_SELECTION = new SchematicSelectionProviderImpl(LockedOrderedSelection.class,
            "locked_ordered",
            "components.provider.selection.lockedOrdered.name",
            "components.provider.selection.lockedOrdered.description") {
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
    };
    public static final SchematicSelectionProvider LOCKED_RANDOM_SELECTION = new SchematicSelectionProviderImpl(LockedRandomSelection.class,
            "locked_random",
            "components.provider.selection.lockedRandom.name",
            "components.provider.selection.lockedRandom.description") {
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
    };
    public static final SchematicSelectionProvider RANDOM_SELECTION = new SchematicSelectionProviderImpl(RandomSelection.class,
            "random",
            "components.provider.selection.random.name",
            "components.provider.selection.random.description") {
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
    };
    public static final SchematicSelectionProvider ORDERED_SELECTION = new SchematicSelectionProviderImpl(OrderedSelection.class,
            "ordered",
            "components.provider.selection.ordered.name",
            "components.provider.selection.ordered.description") {
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
    };

    /**
     * Create a new settings provider
     *
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     */
    public SchematicSelectionProviderImpl(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    public SchematicSelectionProviderImpl(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }
}
