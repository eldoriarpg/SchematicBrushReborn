package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {

    public Remove(Plugin plugin) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("id", true)
                .build());
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var schematicBrush = WorldEditBrush.getSchematicBrush(player);

        CommandAssertions.isTrue(schematicBrush.isPresent(), "error.notABrush");

        var id = args.asInt(0);
        CommandAssertions.isTrue(id < 1 || id > schematicBrush.get().getSettings().schematicSets().size(),
                "Invalid set id.");

        var schematicSets = schematicBrush.get().getSettings().schematicSets();
        var remove = schematicSets.remove(id - 1);

        messageSender().sendMessage(player, "Set §b" + remove.arguments() + "§r removed!");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return Collections.singletonList("<schematic set id>");
    }
}
