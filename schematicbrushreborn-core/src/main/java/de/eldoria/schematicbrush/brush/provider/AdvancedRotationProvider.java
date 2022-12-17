/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.Input;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.advancedrotation.AAdvancedRotation;
import de.eldoria.schematicbrush.brush.config.advancedrotation.AdvancedRotation;
import de.eldoria.schematicbrush.brush.config.advancedrotation.AdvancedRotationFixed;
import de.eldoria.schematicbrush.brush.config.advancedrotation.AdvancedRotationList;
import de.eldoria.schematicbrush.brush.config.advancedrotation.AdvancedRotationRandom;
import de.eldoria.schematicbrush.brush.config.advancedrotation.RandomRotationSupplier;
import de.eldoria.schematicbrush.brush.config.advancedrotation.RotationSupplier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.ARotation;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.rotation.RotationFixed;
import de.eldoria.schematicbrush.brush.config.rotation.RotationList;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AdvancedRotationProvider extends ModifierProvider {
    public static final AdvancedRotationProvider RANDOM = new AdvancedRotationProvider(AdvancedRotationRandom.class, "Random") {
        @Override
        public Mutator<?> parse(Arguments args) {
            // y x z
            RandomRotationSupplier y = parseArg(args.asString(0, "*"));
            RandomRotationSupplier x = parseArg(args.asString(1, "*"));
            RandomRotationSupplier z = parseArg(args.asString(2, "*"));
            return AAdvancedRotation.random(x, y, z);
        }

        private RandomRotationSupplier parseArg(String argument) {
            if (argument == null || "*".equals(argument)) {
                return new RandomRotationSupplier(List.of(Rotation.ROT_HALF, Rotation.ROT_LEFT, Rotation.ROT_RIGHT, Rotation.ROT_ZERO));
            }
            List<Rotation> rotations = new ArrayList<>();
            for (String rot : argument.split(",")) {
                rotations.add(Rotation.parse(rot));
            }
            return new RandomRotationSupplier(rotations);
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Collections.emptyList();
        }

        @Override
        public String description() {
            return "A random rotation containing all allowed 90° rotations.";
        }

        @Override
        public boolean hasArguments() {
            return true;
        }
    };

    private static final String[] ROTATIONS = {"0", "90", "180", "270"};
    public static final AdvancedRotationProvider FIXED = new AdvancedRotationProvider(AdvancedRotationFixed.class, "Fixed") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotation", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            String[] split = args.get(0).asString().split(",");
            var x = Rotation.parse(split.length > 0 ? split[0] : "0");
            var y = Rotation.parse(split.length > 1 ? split[1] : "0");
            var z = Rotation.parse(split.length > 2 ? split[2] : "0");

            return AAdvancedRotation.fixed(x,y,z);
        }

        @Override
        public String description() {
            return "A fixed rotation.";
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.complete(args.asString(0), ROTATIONS);
            }
            return Collections.emptyList();
        }
    };

    public static final AdvancedRotationProvider LIST = new AdvancedRotationProvider(AdvancedRotationList.class, "List") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotations...", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<RotationSupplier> values = new ArrayList<>();
            for (var arg : args) {
                String[] split = args.get(0).asString().split(",");
                var x = Rotation.parse(split.length > 0 ? split[0] : "0");
                var y = Rotation.parse(split.length > 1 ? split[1] : "0");
                var z = Rotation.parse(split.length > 2 ? split[2] : "0");
                new RotationSupplier(x,y,z);
            }
            return AAdvancedRotation.list(values);
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public String description() {
            return "A list of rotations which will be choosen by random";
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return TabCompleteUtil.complete(args.asString(-1), ROTATIONS);
        }
    };

    static AdvancedRotation parse(String rotation) {

    }

    public AdvancedRotationProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    @Override
    public Mutator<?> defaultSetting() {
        return new RotationFixed(Rotation.ROT_ZERO);
    }
}
