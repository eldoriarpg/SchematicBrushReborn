/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
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
    public static final RotationProvider RANDOM = new RotationProvider(RotationRandom.class, "Random") {
        @Override
        public Mutator<?> parse(Arguments args) {
            return ARotation.random();
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
            return false;
        }
    };
    private static final String[] ROTATIONS = {"0", "90", "180", "270"};
    public static final RotationProvider FIXED = new RotationProvider(RotationFixed.class, "Fixed") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotation", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return ARotation.fixed(Rotation.parse(args.asString(0)));
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

    public static final RotationProvider LIST = new RotationProvider(RotationList.class, "List") {
        private final Argument[] arguments = {Argument.unlocalizedInput("rotations...", true)};

        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Rotation> values = new ArrayList<>();
            for (var arg : args) {
                values.add(Rotation.parse(arg));
            }
            return ARotation.list(values);
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

    public RotationProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    @Override
    public Mutator<?> defaultSetting() {
        return new RotationFixed(Rotation.ROT_ZERO);
    }
}
