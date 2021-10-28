package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.commands.admin.Debug;
import de.eldoria.schematicbrush.commands.admin.Info;
import de.eldoria.schematicbrush.commands.admin.Reload;
import de.eldoria.schematicbrush.commands.admin.ReloadSchematics;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;

public class Admin extends AdvancedCommand {
    public Admin(SchematicBrushReborn instance,
                 SchematicRegistry cache) {
        super(instance);
        meta(CommandMeta.builder("sba")
                .buildSubCommands((cmds, builder) -> {
                    var info = new Info(instance);
                    builder.withDefaultCommand(info);
                    cmds.add(info);
                    cmds.add(new Debug(instance));
                    cmds.add(new Reload(instance));
                    cmds.add(new ReloadSchematics(instance, cache));
                })
                .build());
    }
}
