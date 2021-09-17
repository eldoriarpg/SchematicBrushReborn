package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.commands.Preset;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SaveCurrent extends AdvancedCommand implements IPlayerTabExecutor {
    private final Preset preset;
    private final Config config;

    public SaveCurrent(Plugin plugin, Preset preset, Config config) {
        super(plugin, CommandMeta.builder("saveCurrent")
                .withPermission("schematicbrush.preset.save")
                .addUnlocalizedArgument("name", true)
                .build());
        this.preset = preset;
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        String name = args.asString(0);

        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);
        CommandAssertions.isTrue(schematicBrush.isPresent(), "This is not a schematic brush.");
        SchematicBrush brush = schematicBrush.get();

        List<String> schematicSets = Preset.getSchematicSets(brush.getSettings());

        config.presetExists(name);

        if (!preset.savePreset(player, name, schematicSets)) return;

        messageSender().sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                                            + "Preset contains " + schematicSets.size() + " schematic sets with "
                                            + brush.getSettings().getSchematicCount() + " schematics.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (config.presetExists(args.asString(0))) {
            return Collections.singletonList("This name is already in use!");
        }
        return Collections.singletonList("<name of preset>");

    }
}
