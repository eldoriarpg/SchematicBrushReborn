/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.util.Colors;
import de.eldoria.schematicbrush.util.Permissions;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class List extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;
    private final IMessageBlockerService messageBlocker;
    private final MiniMessage miniMessage;
    private final BukkitAudiences audiences;

    public List(Plugin plugin, Configuration configuration, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("list")
                .build());
        this.configuration = configuration;
        this.messageBlocker = messageBlocker;
        miniMessage = MiniMessage.miniMessage();
        audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        messageBlocker.blockPlayer(player);
        var global = configuration.presets().getPresets()
                .stream()
                .map(preset -> "  " + preset.infoComponent(true, player.hasPermission(Permissions.Preset.GLOBAL)))
                .collect(Collectors.joining("\n"));
        var local = configuration.presets().getPresets(player)
                .stream()
                .map(preset -> "  " + preset.infoComponent(false, true))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>Presets:%n%s%n<%s>Global:%n%s", Colors.HEADING, local, Colors.HEADING, global);
        message = messageBlocker.ifEnabled(message, mess -> mess + String.format("%n<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.sender(player).sendMessage(miniMessage.deserialize(message));
    }
}
