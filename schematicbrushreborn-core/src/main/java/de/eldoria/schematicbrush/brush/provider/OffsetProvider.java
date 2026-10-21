/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.offset.AOffset;
import de.eldoria.schematicbrush.brush.config.offset.OffsetFixed;
import de.eldoria.schematicbrush.brush.config.offset.OffsetList;
import de.eldoria.schematicbrush.brush.config.offset.OffsetRange;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class OffsetProvider extends ModifierProvider {

    public static final OffsetProvider FIXED = new OffsetProvider("Fixed",
            "components.provider.offset.fixed.name",
            "components.provider.offset.fixed.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("offset", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            CommandAssertions.range(args.asInt(0), -100, 100);
            return AOffset.fixed(args.asInt(0));
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<SettingInput> inputs(@Nullable Mutator<?> current) {
            var initial = current instanceof OffsetFixed fixed ? fixed.value() : 0;
            return List.of(SettingInput.numeric("components.input.offset", -100, 100, initial));
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            if (args.size() == 1) {
                return Completion.completeInt(args.asString(0), -100, 100);
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetFixed(0);
        }
    };

    public static final OffsetProvider LIST = new OffsetProvider("List",
            "components.provider.offset.list.name",
            "components.provider.offset.list.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("offsets...", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Integer> values = new ArrayList<>();
            for (var i = 0; i < args.size(); i++) {
                CommandAssertions.range(args.asInt(i), -100, 100);
                values.add(args.asInt(i));
            }
            return AOffset.list(values);
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<SettingInput> inputs(@Nullable Mutator<?> current) {
            var initial = current instanceof OffsetList list
                    ? list.values().stream().map(String::valueOf).collect(Collectors.joining(" "))
                    : "0";
            return List.of(SettingInput.text("components.input.offsets", initial));
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return Completion.completeInt(args.asString(-1), -100, 100);
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetList(Collections.singletonList(0));
        }
    };

    public static final OffsetProvider RANGE = new OffsetProvider("Range",
            "components.provider.offset.range.name",
            "components.provider.offset.range.description") {
        private final Argument[] arguments = {Argument.unlocalizedInput("offset_min", true), Argument.unlocalizedInput("offset_max", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            var lower = args.asInt(0);
            var upper = args.asInt(1);
            return AOffset.range(lower, upper);
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<SettingInput> inputs(@Nullable Mutator<?> current) {
            var min = current instanceof OffsetRange range ? range.min() : 0;
            var max = current instanceof OffsetRange range ? range.max() : 0;
            return List.of(SettingInput.numeric("components.input.offsetMin", -100, 100, min),
                    SettingInput.numeric("components.input.offsetMax", -100, 100, max));
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            if (!args.isEmpty() && args.size() < 3) {
                return Completion.completeInt(args.asString(-1), -100, 100);
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetRange(0, 0);
        }
    };

    public OffsetProvider(String name, String localizedName, String description) {
        super(name, localizedName, description);
    }
}
