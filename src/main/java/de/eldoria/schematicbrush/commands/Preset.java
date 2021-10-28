package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.commands.preset.Descr;
import de.eldoria.schematicbrush.commands.preset.Help;
import de.eldoria.schematicbrush.commands.preset.Info;
import de.eldoria.schematicbrush.commands.preset.List;
import de.eldoria.schematicbrush.commands.preset.Remove;
import de.eldoria.schematicbrush.config.Config;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class Preset extends AdvancedCommand {
    private final Config config;

    public Preset(Plugin plugin, Config config) {
        super(plugin);
        meta(CommandMeta.builder("sbp")
                .buildSubCommands((cmds, builder) -> {
                    var help = new Help(plugin);
                    builder.withDefaultCommand(help);
                    cmds.add(help);
                    cmds.add(new Descr(plugin, config));
                    cmds.add(new Info(plugin, config));
                    cmds.add(new List(plugin, config));
                    cmds.add(new Remove(plugin, config));
                }).build());
        this.config = config;
    }
}
