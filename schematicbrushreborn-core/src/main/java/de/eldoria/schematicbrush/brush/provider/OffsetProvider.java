/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.offset.AOffset;
import de.eldoria.schematicbrush.brush.config.offset.OffsetFixed;
import de.eldoria.schematicbrush.brush.config.offset.OffsetList;
import de.eldoria.schematicbrush.brush.config.offset.OffsetRange;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class OffsetProvider extends ModifierProvider {

    public static final OffsetProvider FIXED = new OffsetProvider(OffsetFixed.class, "Fixed") {
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
        public String description() {
            return "A fixed offset value";
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            if (args.size() == 1) {
                return TabCompleteUtil.completeInt(args.asString(0), -100, 100);
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetFixed(0);
        }
    };

    public static final OffsetProvider LIST = new OffsetProvider(OffsetList.class, "List") {
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
        public String description() {
            return "A list of possible offset values which will be choosen by random.";
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            return TabCompleteUtil.completeInt(args.asString(-1), -100, 100);
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetList(Collections.singletonList(0));
        }
    };

    public static final OffsetProvider RANGE = new OffsetProvider(OffsetRange.class, "Range") {
        private final Argument[] arguments = {Argument.unlocalizedInput("offset_min", true), Argument.unlocalizedInput("offset_max", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            var lower = args.asInt(0);
            var upper = args.asInt(1);
            return AOffset.range(lower, upper);
        }

        @Override
        public String description() {
            return "A offset range where the values will be between the min and max value (both inclusive)";
        }

        @Override
        public Argument[] arguments() {
            return arguments;
        }

        @Override
        public List<String> complete(Arguments args, Player player) throws CommandException {
            if (!args.isEmpty() && args.size() < 3) {
                return TabCompleteUtil.completeInt(args.asString(-1), -100, 100);
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetRange(0, 0);
        }
    };

    public OffsetProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
