package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class Reload extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final SchematicBrushCache cache;

    public Reload(Plugin plugin, Config config, SchematicBrushCache cache) {
        super(plugin, CommandMeta.builder("reload").build());
        this.config = config;
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var schematicBrush = WorldEditBrush.getSchematicBrush(player);
        CommandAssertions.isTrue(schematicBrush.isPresent(), "error.notABrush");
        var oldSettings = schematicBrush.get().getSettings();
        var configurationBuilder = BrushSettingsParser.buildBrushes(player,
                oldSettings.schematicSets().stream().map(SchematicSet::arguments).collect(Collectors.toList()),
                config, cache);

        if (configurationBuilder.isEmpty()) {
            return;
        }

        var builder = configurationBuilder.get();

        var configuration = builder.includeAir(oldSettings.isIncludeAir())
                .replaceAll(oldSettings.isReplaceAll())
                .withPlacementType(oldSettings.placement())
                .withYOffset(oldSettings.yOffset())
                .build();

        var oldCount = oldSettings.getSchematicCount();
        var newcount = configuration.getSchematicCount();
        var addedSchematics = newcount - oldCount;
        WorldEditBrush.setBrush(player, new SchematicBrush(plugin(), player, configuration));
        if (addedSchematics > 0) {
            messageSender().sendMessage(player, "Brush reloaded. Added §b" + addedSchematics + "§r schematics" + C.NEW_LINE
                                                + "Brush is now using §b" + newcount + "§r schematics.");
        } else if (addedSchematics < 0) {
            messageSender().sendMessage(player, "Brush reloaded. Removed §b" + addedSchematics + "§r schematics" + C.NEW_LINE
                                                + "Brush is now using §b" + newcount + "§r schematics.");

        } else {
            messageSender().sendMessage(player, "§cNo new schematics were found.§r " +
                                                "Maybe you have to reload the schematics first. Use §b/sbra reloadschematics§r");
        }
    }
}
