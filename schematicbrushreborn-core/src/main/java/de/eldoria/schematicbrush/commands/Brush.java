package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.brush.AddPreset;
import de.eldoria.schematicbrush.commands.brush.AddSet;
import de.eldoria.schematicbrush.commands.brush.Bind;
import de.eldoria.schematicbrush.commands.brush.Clear;
import de.eldoria.schematicbrush.commands.brush.Create;
import de.eldoria.schematicbrush.commands.brush.Modify;
import de.eldoria.schematicbrush.commands.brush.ModifySet;
import de.eldoria.schematicbrush.commands.brush.RemoveSet;
import de.eldoria.schematicbrush.commands.brush.SavePreset;
import de.eldoria.schematicbrush.commands.brush.Sessions;
import de.eldoria.schematicbrush.commands.brush.Show;
import de.eldoria.schematicbrush.commands.brush.ShowSet;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

/**
 * Command which is used to create a new brush. Rewrite of old schbr command.
 */
public class Brush extends AdvancedCommand {
    public Brush(Plugin plugin, SchematicRegistry schematics, Config config, BrushSettingsRegistry registry, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("sbr")
                .withPermission(Permissions.Brush.USE)
                .buildSubCommands((cmds, self) -> {
                    var sessions = new Sessions(plugin, registry, schematics, messageBlocker);
                    var create = new Create(plugin, sessions);
                    self.withDefaultCommand(create);
                    cmds.add(new AddSet(plugin, sessions));
                    cmds.add(new Bind(plugin, sessions, messageBlocker));
                    cmds.add(new Clear(plugin, sessions));
                    cmds.add(create);
                    cmds.add(new Modify(plugin, sessions, registry));
                    cmds.add(new ModifySet(plugin, sessions, registry, schematics));
                    cmds.add(new RemoveSet(plugin, sessions));
                    cmds.add(new Show(plugin, sessions));
                    cmds.add(new ShowSet(plugin, sessions));
                    cmds.add(new AddPreset(plugin, sessions, config));
                    cmds.add(new SavePreset(plugin, sessions, config));
                })
                .build());
    }
}
