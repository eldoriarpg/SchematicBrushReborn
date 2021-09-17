package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Info extends AdvancedCommand implements IPlayerTabExecutor {
    public Info(Plugin plugin) {
        super(plugin, CommandMeta.builder("info")
                .build());
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        CommandAssertions.isTrue(schematicBrush.isPresent(), "This is not a schematic brush.");

        BrushSettings settings = schematicBrush.get().getSettings();
        List<SchematicSet> schematicSets = settings.schematicSets();

        List<String> schematicSetStrings = new ArrayList<>();
        for (int i = 0; i < schematicSets.size(); i++) {
            String arguments = schematicSets.get(i).arguments();
            schematicSetStrings.add("§b" + (i + 1) + "|§r " + arguments);
        }

        String schematicSetList = String.join(C.NEW_LINE, schematicSetStrings);
        messageSender().sendMessage(player,
                "§bTotal schematics:§r " + settings.getSchematicCount() + C.NEW_LINE
                + "§bPlacement:§r " + settings.placement().toString() + C.NEW_LINE
                + "§bY-Offset:§r " + settings.yOffset() + C.NEW_LINE
                + "§bPaste air:§r " + settings.isIncludeAir() + C.NEW_LINE
                + "§bReplace all blocks:§r " + settings.isReplaceAll() + C.NEW_LINE
                + "§bSchematic sets:§r" + C.NEW_LINE
                + schematicSetList);

    }
}
