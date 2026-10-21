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
import de.eldoria.schematicbrush.brush.config.flip.AFlip;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.flip.FlipFixed;
import de.eldoria.schematicbrush.brush.config.flip.FlipList;
import de.eldoria.schematicbrush.brush.config.flip.FlipRandom;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FlipProvider extends ModifierProvider {
    private static final List<String> FLIPS = Arrays.stream(Flip.values()).map(Flip::name).toList();

    public static final FlipProvider FIXED = new FlipProvider("Fixed",
            "components.provider.flip.fixed.name",
            "components.provider.flip.fixed.description") {
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
        public List<SettingInput> inputs(@Nullable Mutator<?> current) {
            var initial = current instanceof FlipFixed fixed ? fixed.value() : Flip.NONE;
            return List.of(SettingInput.choice("components.input.flip", FLIPS, initial.name()));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return Completion.complete(args.asString(0), Arrays.stream(Flip.values()).map(Flip::name));
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new FlipFixed(Flip.NONE);
        }
    };

    public static final FlipProvider LIST = new FlipProvider("List",
            "components.provider.flip.list.name",
            "components.provider.flip.list.description") {
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
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<SettingInput> inputs(@Nullable Mutator<?> current) {
            var initial = current instanceof FlipList list ? list.values().stream().map(Flip::name).toList() : List.of(Flip.NONE.name());
            return List.of(SettingInput.selection("components.input.flips", FLIPS, initial));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Completion.complete(args.asString(-1), Arrays.stream(Flip.values()).map(Flip::name));
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new FlipList(Collections.singletonList(Flip.NONE));
        }
    };

    public static final FlipProvider RANDOM = new FlipProvider("Random",
            "components.provider.flip.random.name",
            "components.provider.flip.random.description") {


        @Override
        public Mutator<?> parse(Arguments args) {
            return AFlip.random();
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Collections.emptyList();
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

    public FlipProvider(String name, String localizedName, String description) {
        super(name, localizedName, description);
    }
}
