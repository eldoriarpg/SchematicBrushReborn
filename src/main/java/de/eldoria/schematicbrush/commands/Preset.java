package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.preset.AppendSet;
import de.eldoria.schematicbrush.commands.preset.Descr;
import de.eldoria.schematicbrush.commands.preset.Help;
import de.eldoria.schematicbrush.commands.preset.Info;
import de.eldoria.schematicbrush.commands.preset.Remove;
import de.eldoria.schematicbrush.commands.preset.RemoveSet;
import de.eldoria.schematicbrush.commands.preset.Save;
import de.eldoria.schematicbrush.commands.preset.SaveCurrent;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Brush to create and modify brush presets.
 */
public class Preset extends AdvancedCommand {
    private final Config config;

    public Preset(Plugin plugin, SchematicCache cache, Config config) {
        super(plugin);
        meta(CommandMeta.builder("sbp")
                .buildSubCommands((cmds, builder) -> {
                    Help help = new Help(plugin);
                    builder.withDefaultCommand(help);
                    cmds.add(help);
                    cmds.add(new AppendSet(plugin, config, cache));
                    cmds.add(new Descr(plugin, config));
                    cmds.add(new Info(plugin, config));
                    cmds.add(new de.eldoria.schematicbrush.commands.preset.List(plugin, config));
                    cmds.add(new Remove(plugin, config));
                    cmds.add(new RemoveSet(plugin, config));
                    cmds.add(new Save(plugin, this, config, cache));
                    cmds.add(new SaveCurrent(plugin, this, config));
                }).build());
        this.config = config;
    }

    public static List<String> getSchematicSets(BrushSettings brush) {
        return brush.schematicSets().stream()
                .map(SchematicSet::arguments)
                .collect(Collectors.toList());
    }

    /**
     * Saves a list of schematic sets to a preset.
     *
     * @param player        player for error handling
     * @param presetName    name of preset
     * @param schematicSets schematic sets
     */
    public boolean savePreset(Player player, String presetName, List<String> schematicSets) {
        if (config.presetExists(presetName)) {
            messageSender().sendError(player, "Preset §b" + presetName + "§r does already exist.");
            return false;
        }
        config.addPreset(new de.eldoria.schematicbrush.config.sections.Preset(presetName, schematicSets));
        config.save();
        return true;
    }
}
