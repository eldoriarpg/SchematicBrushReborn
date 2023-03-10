/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.util;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.util.Colors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Function;

public class BasePageCommand extends AdvancedCommand {
    public final int PAGE_SIZE = 15;
    public final String RIGHT_ARROW = "»»»";
    public final String LEFT_ARROW = "«««";
    private final MessageBlocker messageBlocker;
    private final MiniMessage miniMessage;
    private final BukkitAudiences audiences;

    public BasePageCommand(Plugin plugin, CommandMeta meta, MessageBlocker messageBlocker) {
        super(plugin, meta);
        this.messageBlocker = messageBlocker;
        miniMessage = MiniMessage.miniMessage();
        audiences = BukkitAudiences.create(plugin);
    }

    protected void addPageHeader(MessageComposer composer, String title, boolean global) {
        var baseCommand = "/" + meta().parent().meta().createCommandCall();
        composer.text("<%s>", Colors.HEADING).text(global ? "Global" : "Private").space().text(title).newLine()
                .text(runCommand(baseCommand, !global))
                .newLine();
    }

    private String runCommand(String baseCommand, boolean global) {
        var type = global ? "Global" : "Private";
        return String.format("<%s><click:run_command:'%s %s'>[%s]</click>", Colors.CHANGE, baseCommand, type.toLowerCase(), type);
    }

    protected <T> void addEntries(MessageComposer composer, List<T> entries, Function<T, String> map) {
        var page = entries.stream().map(entry -> String.format("  %s", map.apply(entry))).toList();
        composer.text(page).newLine();
    }

    protected void addPageFooter(MessageComposer composer, int index, ContainerPagedAccess<?> paged) {
        var baseCommand = "/" +meta().createCommandCall();
        if (index == 0) {
            composer.text("<%s>%s", Colors.INACTIVE, LEFT_ARROW);
        } else {
            composer.text("<click:run_command:'%s %s'><%s>%s</click>", baseCommand, index - 1, Colors.CHANGE, LEFT_ARROW);
        }
        composer.text(" <%s>%s / %s ", Colors.NEUTRAL, index + 1, Math.max(1, paged.pages(PAGE_SIZE)));

        if (index + 1 >= paged.pages(PAGE_SIZE)) {
            composer.text("<%s>%s", Colors.INACTIVE, RIGHT_ARROW);
        } else {
            composer.text("<click:run_command:'%s %s'><%s>%s</click>", baseCommand, index + 1, Colors.CHANGE, RIGHT_ARROW);
        }
    }

    protected void send(MessageComposer composer, Player player) {
        composer.prependLines(20);
        messageBlocker.blockPlayer(player);
        messageBlocker.ifEnabled(() -> composer.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.sender(player).sendMessage(miniMessage.deserialize(composer.build()));
    }
}
