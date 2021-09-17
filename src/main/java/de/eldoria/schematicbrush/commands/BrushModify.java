package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.commands.modify.Append;
import de.eldoria.schematicbrush.commands.modify.Edit;
import de.eldoria.schematicbrush.commands.modify.Help;
import de.eldoria.schematicbrush.commands.modify.Info;
import de.eldoria.schematicbrush.commands.modify.Reload;
import de.eldoria.schematicbrush.commands.modify.Remove;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.plugin.Plugin;

/**
 * Command to modify a current used brush.
 */
public class BrushModify extends AdvancedCommand implements Randomable {
    public BrushModify(Plugin plugin, SchematicCache schematicCache, Config config) {
        super(plugin, CommandMeta.builder("sbm")
                .withPermission("schematicbrush.brush.use")
                .buildSubCommands((cmds, builder) -> {
                    cmds.add(new Append(plugin, config, schematicCache));
                    cmds.add(new Edit(plugin, config, schematicCache));
                    Help help = new Help(plugin);
                    cmds.add(help);
                    builder.withDefaultCommand(help);
                    cmds.add(new Info(plugin));
                    cmds.add(new Reload(plugin, config, schematicCache));
                    cmds.add(new Remove(plugin));
                })
                .build());
    }
}
