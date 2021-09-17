package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.commands.SchematicPreset;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Save extends AdvancedCommand implements IPlayerTabExecutor {
    private final SchematicPreset preset;
    private final Config config;
    private final SchematicCache cache;

    public Save(Plugin plugin, SchematicPreset preset, Config config, SchematicCache cache) {
        super(plugin, CommandMeta.builder("save")
                .withPermission("schematicbrush.preset.save")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("schematic_set", true)
                .build());
        this.preset = preset;
        this.config = config;
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        String name = args.asString(0);

        String[] brushArgs = args.args(1).toArray(new String[0]);

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, config, cache, brushArgs);

        if (!settings.isPresent()) {
            return;
        }

        List<String> schematicSets = SchematicPreset.getSchematicSets(settings.get());

        if (!preset.savePreset(player, name, schematicSets)) return;

        messageSender().sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                                            + "Preset contains " + schematicSets.size() + " schematic sets with "
                                            + settings.get().getSchematicCount() + " schematics.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 2) {
            if (config.presetExists(args.asString(1))) {
                return Collections.singletonList("This name is already in use!");
            }
            return Collections.singletonList("<name of preset>");
        }
        return TabUtil.getSchematicSetSyntax(args.asArray(), cache, config);
    }
}
