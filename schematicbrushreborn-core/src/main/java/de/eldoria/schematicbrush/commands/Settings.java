package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.messageblocker.blocker.MessageBlockerService;
import de.eldoria.schematicbrush.commands.settings.ChatBlock;
import de.eldoria.schematicbrush.commands.settings.Preview;
import de.eldoria.schematicbrush.commands.settings.ShowNames;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

public class Settings extends AdvancedCommand {
    public Settings(Plugin plugin, RenderService renderService, NotifyListener notifyListener, IMessageBlockerService messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbs")
                .withPermission(Permissions.Brush.USE)
                .withSubCommand(new Preview(plugin, renderService))
                .withSubCommand(new ShowNames(plugin, notifyListener))
                .withSubCommand(new ChatBlock(plugin, messageBlocker))
                .build());
    }
}
