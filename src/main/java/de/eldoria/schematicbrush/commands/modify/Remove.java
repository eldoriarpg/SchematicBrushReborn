package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {

    public Remove(Plugin plugin) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("id", true)
                .build());
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        CommandAssertions.isTrue(schematicBrush.isPresent(), "This is not a schematic brush.");

        int id = args.asInt(0);
        CommandAssertions.isTrue(id < 1 || id > schematicBrush.get().getSettings().schematicSets().size(),
                "Invalid set id.");

        List<SchematicSet> schematicSets = schematicBrush.get().getSettings().schematicSets();
        SchematicSet remove = schematicSets.remove(id - 1);

        messageSender().sendMessage(player, "Set §b" + remove.arguments() + "§r removed!");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return Collections.singletonList("<schematic set id>");
    }
}
