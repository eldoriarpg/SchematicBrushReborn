package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.messageblocker.blocker.MessageBlockerService;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.util.Colors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class List extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final IMessageBlockerService messageBlocker;
    private final MiniMessage miniMessage;
    private final BukkitAudiences audiences;

    public List(Plugin plugin, Config config, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("list")
                .build());
        this.config = config;
        this.messageBlocker = messageBlocker;
        miniMessage = MiniMessage.get();
        audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        messageBlocker.blockPlayer(player);
        var global = config.presets().getPresets()
                .stream()
                .map(preset -> preset.infoComponent(true))
                .collect(Collectors.joining("\n"));
        var local = config.presets().getPresets(player)
                .stream()
                .map(preset -> preset.infoComponent(false))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>Presets:%n%s%n<%s>Global:%s", Colors.HEADING, local, Colors.HEADING, global);
        message = messageBlocker.ifEnabled(message, m -> m + String.format("%n<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.sender(player).sendMessage(miniMessage.parse(message));
    }
}
