package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Append extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final SchematicCache cache;

    public Append(Plugin plugin, Config config, SchematicCache cache) {
        super(plugin, CommandMeta.builder("append")
                .build());
        this.config = config;
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var settings = BrushSettingsParser.parseBrush(player, config, cache, args.asArray());

        if (!settings.isPresent()) {
            return;
        }

        var schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        CommandAssertions.isTrue(schematicBrush.isPresent(), "This is not a schematic brush.");

        var combinedBrush = schematicBrush.get().combineBrush(settings.get());
        var success = WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(settings.get()));
        if (success) {
            messageSender().sendMessage(player, "Schematic set appended. Using §b"
                                                + combinedBrush.getSettings().getSchematicCount() + "§r schematics.");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return TabUtil.getSchematicSetSyntax(args.asArray(), cache, config);
    }
}
