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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Function;

public class BasePageCommand extends AdvancedCommand {
    public final int PAGE_SIZE = 15;
    public final String RIGHT_ARROW = "»»»";
    public final String LEFT_ARROW = "«««";
    private final MessageBlocker messageBlocker;

    public BasePageCommand(Plugin plugin, CommandMeta meta, MessageBlocker messageBlocker) {
        super(plugin, meta);
        this.messageBlocker = messageBlocker;
    }

    protected void addPageHeader(MessageComposer composer, String title, boolean global) {
        var baseCommand = "/" + meta().parent().meta().createCommandCall();
        composer.text("<heading>").localeCode(global ? "words.global" : "words.private").space().localeCode(title).newLine()
                .text(runCommand(baseCommand, !global))
                .newLine();
    }

    private String runCommand(String baseCommand, boolean global) {
        var type = global ? "words.global" : "words.private";
        return MessageComposer.create()
                              .text("<change><click:run_command:'%s %s'>[", baseCommand, global ? "global" : "private")
                              .localeCode(type)
                              .text("]</click>")
                              .build();
    }

    protected <T> void addEntries(MessageComposer composer, List<T> entries, Function<T, String> map) {
        var page = entries.stream().map(entry -> String.format("  %s", map.apply(entry))).toList();
        composer.text(page).newLine();
    }

    protected void addPageFooter(MessageComposer composer, int index, ContainerPagedAccess<?> paged) {
        var baseCommand = "/" + meta().createCommandCall();
        if (index == 0) {
            composer.text("<inactive>%s", LEFT_ARROW);
        } else {
            composer.text("<click:run_command:'%s %s'><change>%s</click>", baseCommand, index - 1, LEFT_ARROW);
        }
        composer.text(" <neutral>%s / %s ", index + 1, Math.max(1, paged.pages(PAGE_SIZE)));

        if (index + 1 >= paged.pages(PAGE_SIZE)) {
            composer.text("<inactive>%s", RIGHT_ARROW);
        } else {
            composer.text("<click:run_command:'%s %s'><change>%s</click>", baseCommand, index + 1, RIGHT_ARROW);
        }
    }

    protected void send(MessageComposer composer, Player player) {
        composer.prependLines(20);
        messageBlocker.blockPlayer(player);
        messageBlocker.ifEnabled(() -> composer.newLine().text("<click:run_command:'/sbrs chatblock false'><remove>[x]</click>"));
        messageBlocker.announce(player, "[x]");
        messageSender().sendMessage(player, composer.build());
    }
}
