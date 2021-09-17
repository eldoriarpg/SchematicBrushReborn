package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.debug.DebugSettings;
import de.eldoria.eldoutilities.debug.DebugUtil;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.commands.admin.Debug;
import de.eldoria.schematicbrush.commands.admin.Info;
import de.eldoria.schematicbrush.commands.admin.Reload;
import de.eldoria.schematicbrush.commands.admin.ReloadSchematics;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Admin extends AdvancedCommand {
    public Admin(SchematicBrushReborn instance,
                 SchematicCache cache) {
        super(instance);
        meta(CommandMeta.builder("sba")
                .buildSubCommands((cmds, builder) ->{
                    Info info = new Info(instance);
                    builder.withDefaultCommand(info);
                    cmds.add(info);
                    cmds.add(new Debug(instance));
                    cmds.add(new Reload(instance));
                    cmds.add(new ReloadSchematics(instance, cache));
                })
                .build());
    }
}
