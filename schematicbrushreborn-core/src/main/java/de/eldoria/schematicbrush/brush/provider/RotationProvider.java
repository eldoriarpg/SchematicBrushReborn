/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.ARotation;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.rotation.RotationFixed;
import de.eldoria.schematicbrush.brush.config.rotation.RotationList;
import de.eldoria.schematicbrush.brush.config.rotation.RotationRandom;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RotationProvider extends ModifierProvider {
    public static final RotationProvider RANDOM = new RotationProvider(RotationRandom.class,
            "Random",
            "components.provider.rotation.random.name",
            "components.provider.rotation.random.description") {
        @Override
        public Mutator<?> parse(Arguments args) {
            return ARotation.random();
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Collections.emptyList();
        }

        @Override
        public boolean hasArguments() {
            return false;
        }
    };
    private static final String[] ROTATIONS = {"0", "90", "180", "270"};
    public static final RotationProvider FIXED = new RotationProvider(RotationFixed.class,
            "Fixed",
            "components.provider.rotation.fixed.name",
            "components.provider.rotation.fixed.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotation", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return ARotation.fixed(Rotation.parse(args.asString(0)));
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return Completion.complete(args.asString(0), ROTATIONS);
            }
            return Collections.emptyList();
        }
    };

    public static final RotationProvider LIST = new RotationProvider(RotationList.class,
            "List",
            "components.provider.rotation.list.name",
            "components.provider.rotation.list.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotations...", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Rotation> values = new ArrayList<>();
            for (var arg : args) {
                values.add(Rotation.parse(arg.asString()));
            }
            return ARotation.list(values);
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Completion.complete(args.asString(-1), ROTATIONS);
        }
    };

    public RotationProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }

    @Override
    public Mutator<?> defaultSetting() {
        return new RotationFixed(Rotation.ROT_ZERO);
    }
}
