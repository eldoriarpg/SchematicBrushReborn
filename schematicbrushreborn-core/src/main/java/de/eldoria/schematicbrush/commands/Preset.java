package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.commands.preset.Descr;
import de.eldoria.schematicbrush.commands.preset.Info;
import de.eldoria.schematicbrush.commands.preset.List;
import de.eldoria.schematicbrush.commands.preset.Remove;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class Preset extends AdvancedCommand {
    public Preset(Plugin plugin, Config config, IMessageBlockerService messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbp")
                .withPermission(Permissions.Preset.USE)
                .buildSubCommands((cmds, builder) -> {
                    cmds.add(new Descr(plugin, config));
                    cmds.add(new Info(plugin, config, messageBlocker));
                    cmds.add(new List(plugin, config, messageBlocker));
                    cmds.add(new Remove(plugin, config));
                }).build());
    }
}
