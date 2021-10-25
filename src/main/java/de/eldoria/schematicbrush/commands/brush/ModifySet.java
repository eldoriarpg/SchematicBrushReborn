package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ModifySet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BrushSettingsRegistry registry;

    public ModifySet(Plugin plugin, Sessions sessions, BrushSettingsRegistry registry) {
        super(plugin, CommandMeta.builder("modifySet")
                .addUnlocalizedArgument("id", true)
                .addUnlocalizedArgument("type", true)
                .addUnlocalizedArgument("value", false)
                .build());
        this.sessions = sessions;
        this.registry = registry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var builder = sessions.getOrCreateSession(player);
        var set = builder.getSchematicSet(args.asInt(0));
        if (set.isEmpty()) {
            messageSender().send(MessageChannel.ACTION_BAR, MessageType.ERROR, player, "Invalid set");
            return;
        }
        //TODO: Add selector parsing
        var mutator = registry.parseSchematicModifier(args.subArguments());
        set.get().withMutator(mutator.first, mutator.second);
        sessions.showSet(player, args.asInt(0));
    }
}
