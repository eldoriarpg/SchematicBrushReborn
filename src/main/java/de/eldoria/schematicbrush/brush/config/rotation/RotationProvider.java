package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RotationProvider extends ModifierProvider {
    private static final String[] ROTATIONS = {"0", "90", "180", "270"};

    public RotationProvider(String name) {
        super(name);
    }

    @Override
    public SchematicMutator defaultSetting() {
        return new RotationFixed(Rotation.ROT_ZERO);
    }

    public static final RotationProvider FIXED = new RotationProvider("fixed") {
        @Override
        public SchematicMutator parse(Arguments args) throws CommandException {
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

    public static final RotationProvider LIST = new RotationProvider("list") {
        @Override
        public SchematicMutator parse(Arguments args) throws CommandException {
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

    public static final RotationProvider RANDOM = new RotationProvider("random") {
        @Override
        public SchematicMutator parse(Arguments args) throws CommandException {
            return new RotationRandom();
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return Collections.emptyList();
        }
    };
}
