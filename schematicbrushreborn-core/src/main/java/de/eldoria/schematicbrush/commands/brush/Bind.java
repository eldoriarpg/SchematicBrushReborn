/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.util.Colors;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Bind extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final MessageBlocker messageBlocker;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final BukkitAudiences audiences;

    public Bind(Plugin plugin, Sessions sessions, MessageBlocker messageBlocker) {
        super(plugin, CommandMeta.builder("bind")
                .hidden()
                .build());
        this.sessions = sessions;
        this.messageBlocker = messageBlocker;
        audiences = BukkitAudiences.builder(plugin).build();
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);



        CommandAssertions.isFalse(session.getSchematicCount() == 0, "Brush is empty.");
        var brush = session.build(plugin(), player);

        if (!WorldEditBrush.setBrush(player, brush)) {
            return;
        }

        var schematicCount = brush.settings().getSchematicCount();
        var setcount = brush.settings().schematicSets().size();
        var message = String.format("<%s>Brush bound. Using <%s>%s<%s> Schematics in <%s>%s<%s> Sets. <%s><click:run_command:'/sbr'>[Edit]</click>",
                Colors.NEUTRAL, Colors.VALUE, schematicCount, Colors.NEUTRAL, Colors.VALUE, setcount, Colors.NEUTRAL, Colors.CHANGE);
        messageBlocker.unblockPlayer(player).thenRun(() -> audiences.sender(player).sendMessage(miniMessage.deserialize(message)));
    }
}
