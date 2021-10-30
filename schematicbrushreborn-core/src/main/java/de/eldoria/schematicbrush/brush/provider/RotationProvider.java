package de.eldoria.schematicbrush.brush.provider;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
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
    public static final RotationProvider RANDOM = new RotationProvider(RotationRandom.class, "random") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new RotationRandom();
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
    public static final RotationProvider FIXED = new RotationProvider(RotationFixed.class, "fixed") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new RotationFixed(Rotation.asRotation(args.asString(0)));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.complete(args.asString(0), ROTATIONS);
            }
            return Collections.emptyList();
        }
    };

    public static final RotationProvider LIST = new RotationProvider(RotationList.class, "list") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Rotation> values = new ArrayList<>();
            for (var arg : args) {
                values.add(Rotation.asRotation(arg));
            }
            return new RotationList(values);
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
