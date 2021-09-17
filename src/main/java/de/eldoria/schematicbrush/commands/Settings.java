package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.commands.settings.Preview;
import de.eldoria.schematicbrush.commands.settings.ShowNames;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import org.bukkit.plugin.Plugin;

public class Settings extends AdvancedCommand {
    public Settings(Plugin plugin, RenderService renderService, NotifyListener notifyListener) {
        super(plugin);
        meta(CommandMeta.builder("sbs")
                .withSubCommand(new Preview(plugin,renderService ))
                .withSubCommand(new ShowNames(plugin, notifyListener))
                .build());
    }
}
