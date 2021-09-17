package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import org.bukkit.plugin.Plugin;

public class BrushSettings extends AdvancedCommand {
    public BrushSettings(Plugin plugin) {
        super(plugin);
        meta(CommandMeta.builder("sbs")
                .build());
    }
}
