package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FlipProvider extends ModifierProvider {

    public static final FlipProvider FIXED = new FlipProvider(FlipFixed.class,"fixed") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new FlipFixed(Flip.asFlip(args.asString(0)));
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

    public static final FlipProvider LIST = new FlipProvider(FlipList.class,"list") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Flip> flips = new ArrayList<>();
            for (var arg : args.args()) {
                flips.add(Flip.asFlip(arg));
            }
            return new FlipList(flips);
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

    public static final FlipProvider RANDOM = new FlipProvider(FlipRandom.class,"random") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new FlipRandom();
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

    public FlipProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
