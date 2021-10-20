package de.eldoria.schematicbrush.brush.config.replaceall;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.Mutator;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class ReplaceAllProvider extends ModifierProvider {

    public static final ReplaceAllProvider FIXED = new ReplaceAllProvider("fixed") {
        @Override
        public Mutator parse(Arguments args) throws CommandException {
            return new ReplaceAll(args.asBoolean(0));
        }

        @Override
        public List<String> complete(Arguments args, Player player) {
            if (args.size() == 1) {
                return TabCompleteUtil.completeBoolean(args.asString(0));
            }
            return Collections.emptyList();
        }

        @Override
        public Mutator defaultSetting() {
            return new ReplaceAll(false);
        }
    };

    public ReplaceAllProvider(String name) {
        super(name);
    }
}
