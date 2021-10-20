package de.eldoria.schematicbrush.brush.config.includeair;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class IncludeAirProvider extends ModifierProvider {
    public IncludeAirProvider(String name) {
        super(name);
    }

    public static final IncludeAirProvider FIXED = new IncludeAirProvider("fixed") {
        @Override
        public SchematicMutator parse(Arguments args) throws CommandException {
            return new IncludeAir(args.asBoolean(0));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.completeBoolean(args.asString(0));
            }
            return Collections.emptyList();
        }

        @Override
        public SchematicMutator defaultSetting() {
            return new IncludeAir(false);
        }
    };
}
