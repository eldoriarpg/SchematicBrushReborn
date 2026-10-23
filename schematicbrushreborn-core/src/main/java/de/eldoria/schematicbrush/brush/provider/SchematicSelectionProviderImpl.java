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
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SchematicSelectionProviderImpl extends SchematicSelectionProvider {
    public static final SchematicSelectionProvider LOCKED_ORDERED_SELECTION = new SchematicSelectionProviderImpl("locked_ordered",
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
    public static final SchematicSelectionProvider LOCKED_RANDOM_SELECTION = new SchematicSelectionProviderImpl("locked_random",
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
    public static final SchematicSelectionProvider RANDOM_SELECTION = new SchematicSelectionProviderImpl("random",
            "components.provider.selection.random.name",
            "components.provider.selection.random.description") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new OrderedSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new OrderedSelection();
        }
    };
    public static final SchematicSelectionProvider ORDERED_SELECTION = new SchematicSelectionProviderImpl("ordered",
            "components.provider.selection.ordered.name",
            "components.provider.selection.ordered.description") {
        @Override
        public SchematicSelection parse(Arguments args) throws CommandException {
            return new OrderedSelection();
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Collections.emptyList();
        }

        @Override
        public SchematicSelection defaultSetting() {
            return new OrderedSelection();
        }
    };

    public SchematicSelectionProviderImpl(String name, String localizedName, String description) {
        super(name, localizedName, description);
    }
}
