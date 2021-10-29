package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class OffsetProvider extends ModifierProvider {

    public static final OffsetProvider FIXED = new OffsetProvider(OffsetFixed.class,"fixed") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            return new OffsetFixed(args.asInt(0));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.completeInt(args.asString(0), -100, 100, ILocalizer.getPluginLocalizer(SchematicBrushReborn.class));
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetFixed(0);
        }
    };
    public static final OffsetProvider LIST = new OffsetProvider(OffsetList.class,"list") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            List<Integer> values = new ArrayList<>();
            for (var i = 0; i < args.size(); i++) {
                values.add(args.asInt(i));
            }
            return new OffsetList(values);
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            return TabCompleteUtil.completeInt(args.asString(-1), -100, 100, ILocalizer.getPluginLocalizer(SchematicBrushReborn.class));
        }

        @Override
        public Mutator<?> defaultSetting() {
            return new OffsetList(Collections.singletonList(0));
        }
    };
    public static final OffsetProvider RANGE = new OffsetProvider(OffsetRange.class, "range") {
        @Override
        public Mutator<?> parse(Arguments args) throws CommandException {
            var lower = args.asInt(0);
            var upper = args.asInt(1);
            return new OffsetRange(lower, upper);
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (!args.isEmpty() && args.size() < 3) {
                return TabCompleteUtil.completeInt(args.asString(-1), -100, 100, ILocalizer.getPluginLocalizer(SchematicBrushReborn.class));
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
