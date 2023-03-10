/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.flip.AFlip;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.flip.FlipFixed;
import de.eldoria.schematicbrush.brush.config.flip.FlipList;
import de.eldoria.schematicbrush.brush.config.flip.FlipRandom;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FlipProvider extends ModifierProvider {

    public static final FlipProvider FIXED = new FlipProvider(FlipFixed.class, "Fixed") {
        private final Argument[] arguments = {Argument.unlocalizedInput("flip", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return AFlip.fixed(Flip.asFlip(args.asString(0)));
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public String description() {
            return "A fixed flip value";
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.complete(args.asString(0), Arrays.stream(Flip.values()).map(Flip::name));
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new FlipFixed(Flip.NONE);
        }
    };

    public static final FlipProvider LIST = new FlipProvider(FlipList.class, "List") {
        private final Argument[] arguments = {Argument.unlocalizedInput("flip...", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Flip> flips = new ArrayList<>();
            for (var arg : args.args()) {
                flips.add(Flip.asFlip(arg.asString()));
            }
            return AFlip.list(flips);
        }

        @Override
        public String description() {
            return "A list of possible flip values which will be choosen by random";
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return TabCompleteUtil.complete(args.asString(-1), Arrays.stream(Flip.values()).map(Flip::name));
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new FlipList(Collections.singletonList(Flip.NONE));
        }
    };

    public static final FlipProvider RANDOM = new FlipProvider(FlipRandom.class, "Random") {
        @Override
        public Mutator<?> parse(Arguments args) {
            return AFlip.random();
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Collections.emptyList();
        }

        @Override
        public String description() {
            return "A random flip falue of all flip values except \"up\"";
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new FlipRandom();
        }

        @Override
        public boolean hasArguments() {
            return false;
        }
    };

    public FlipProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
