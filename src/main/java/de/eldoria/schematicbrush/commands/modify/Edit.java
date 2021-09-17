package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Edit extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final SchematicCache cache;

    public Edit(Plugin plugin, Config config, SchematicCache cache) {
        super(plugin, CommandMeta.builder("edit")
                .addUnlocalizedArgument("id", true)
                .addUnlocalizedArgument("schematic set", true)
                .build());
        this.config = config;
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        CommandAssertions.isTrue(schematicBrush.isPresent(), "This is not a schematic brush.");

        Optional<BrushSettings> brushConfiguration = BrushSettingsParser
                .parseBrush(player, config, cache, args.args(1).toArray(new String[0]));


        if (!brushConfiguration.isPresent()) {
            return;
        }

        int id = args.asInt(0);
        CommandAssertions.isTrue(id < 1 || id > schematicBrush.get().getSettings().schematicSets().size(),
                "Invalid set id.");

        List<SchematicSet> schematicSets = schematicBrush.get().getSettings().schematicSets();
        SchematicSet remove = schematicSets.remove(id - 1);
        WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(brushConfiguration.get()));
        messageSender().sendMessage(player, "Set §b" + remove.arguments() + "§r changed to §b"
                                            + brushConfiguration.get().schematicSets().get(0).arguments() + "§r.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {

            return Collections.singletonList("<schematic set id> <schematic set>");
        }
        if (args.size() == 2) {
            return Collections.singletonList("<schematic set>");
        }
        return TabUtil.getSchematicSetSyntax(args.asArray(), cache, config);

    }
}
