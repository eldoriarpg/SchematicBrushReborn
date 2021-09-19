package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.commands.Preset;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AppendSet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final SchematicCache cache;

    public AppendSet(Plugin plugin, Config config, SchematicCache cache) {
        super(plugin, CommandMeta.builder("appendSet")
                .withPermission("schematicbrush.preset.modify")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("schematic set", true)
                .build());
        this.config = config;
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        var brushArgs = args.args(1);
        var settings = BrushSettingsParser.parseBrush(player, config, cache, brushArgs.toArray(new String[0]));

        if (settings.isEmpty()) {
            return;
        }

        var preset = config.getPreset(name);
        CommandAssertions.isTrue(preset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

        preset.get().getFilter().addAll(Preset.getSchematicSets(settings.get()));
        config.save();

        messageSender().sendMessage(player, "Preset " + name + " changed!" + C.NEW_LINE
                                            + "Added §b" + settings.get().schematicSets().size() + "§r schematic sets with §b"
                                            + settings.get().getSchematicCount() + "§r schematics.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 2 && args.asString(1).isEmpty()) {
            var presets = TabUtil.getPresets(args.asString(1), 50, config);
            presets.add("<name of preset>");
            return presets;
        }
        if (args.size() == 2) {
            return TabUtil.getPresets(args.asString(1), 50, config);
        }

        return TabUtil.getSchematicSetSyntax(player, args.asArray(), cache, config);
    }
}
