/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrushMapper;
import de.eldoria.schematicbrush.brush.legacy.LegacyCompletion;
import de.eldoria.schematicbrush.commands.brush.AddBrushModifier;
import de.eldoria.schematicbrush.commands.brush.AddPreset;
import de.eldoria.schematicbrush.commands.brush.AddSet;
import de.eldoria.schematicbrush.commands.brush.AddSetModifier;
import de.eldoria.schematicbrush.commands.brush.Bind;
import de.eldoria.schematicbrush.commands.brush.Clear;
import de.eldoria.schematicbrush.commands.brush.Create;
import de.eldoria.schematicbrush.commands.brush.LoadBrush;
import de.eldoria.schematicbrush.commands.brush.Modify;
import de.eldoria.schematicbrush.commands.brush.ModifySet;
import de.eldoria.schematicbrush.commands.brush.Parse;
import de.eldoria.schematicbrush.commands.brush.Redo;
import de.eldoria.schematicbrush.commands.brush.RefreshSchematics;
import de.eldoria.schematicbrush.commands.brush.RemoveBrushModifier;
import de.eldoria.schematicbrush.commands.brush.RemoveSet;
import de.eldoria.schematicbrush.commands.brush.RemoveSetModifier;
import de.eldoria.schematicbrush.commands.brush.SaveBrush;
import de.eldoria.schematicbrush.commands.brush.SavePreset;
import de.eldoria.schematicbrush.commands.brush.Sessions;
import de.eldoria.schematicbrush.commands.brush.Show;
import de.eldoria.schematicbrush.commands.brush.ShowSet;
import de.eldoria.schematicbrush.commands.brush.ShowSets;
import de.eldoria.schematicbrush.commands.brush.Undo;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.editor.EditorPreferences;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

/**
 * Command which is used to create a new brush. Rewrite of old schbr command.
 */
public class Brush extends AdvancedCommand {
    public Brush(Plugin plugin, SchematicRegistry schematics, StorageRegistry storage, BrushSettingsRegistry setting, EditorPreferences preferences, Configuration configuration) {
        super(plugin, CommandMeta.builder("sbr")
                .withPermission(Permissions.Brush.USE)
                .buildSubCommands((cmds, self) -> {
                    var sessions = new Sessions(plugin, setting, schematics, preferences);
                    var create = new Create(plugin, sessions);
                    self.withDefaultCommand(create);
                    cmds.add(new AddSet(plugin, sessions));
                    cmds.add(new AddBrushModifier(plugin, sessions, setting));
                    cmds.add(new AddSetModifier(plugin, sessions, setting));
                    cmds.add(new RemoveBrushModifier(plugin, sessions, setting));
                    cmds.add(new RemoveSetModifier(plugin, sessions, setting));
                    var bind = new Bind(plugin, sessions);
                    cmds.add(bind);
                    var separator = configuration.schematicConfig().pathSeparator();
                    cmds.add(new Parse(plugin, sessions, bind, storage,
                            new LegacyBrushMapper(plugin, setting, schematics, separator),
                            new LegacyCompletion(plugin, schematics, storage, separator)));
                    cmds.add(new Clear(plugin, sessions));
                    cmds.add(new Undo(plugin, sessions));
                    cmds.add(new Redo(plugin, sessions));
                    cmds.add(create);
                    cmds.add(new Modify(plugin, sessions, setting));
                    cmds.add(new ModifySet(plugin, sessions, setting, schematics));
                    cmds.add(new RemoveSet(plugin, sessions));
                    cmds.add(new Show(plugin, sessions));
                    cmds.add(new ShowSet(plugin, sessions));
                    cmds.add(new ShowSets(plugin, sessions));
                    cmds.add(new AddPreset(plugin, sessions, storage));
                    cmds.add(new SavePreset(plugin, sessions, storage));
                    cmds.add(new RefreshSchematics(plugin, sessions, setting, schematics));
                    cmds.add(new LoadBrush(plugin, sessions, storage));
                    cmds.add(new SaveBrush(plugin, sessions, storage));
                })
                .build());
    }
}
