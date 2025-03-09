/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.eldoutilities.utils.Consumers;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Info extends AdvancedCommand implements IPlayerTabExecutor {
    private final StorageRegistry storage;
    private final BukkitAudiences audiences;
    private final MessageBlocker messageBlocker;

    public Info(Plugin plugin, StorageRegistry storage, MessageBlocker messageBlocker) {
        super(plugin, CommandMeta.builder("info")
                .addUnlocalizedArgument("name", true)
                .hidden()
                .build());
        this.storage = storage;
        audiences = BukkitAudiences.create(plugin);
        this.messageBlocker = messageBlocker;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        var strippedName = name.replaceAll("^g:", "");
        storage.activeStorage().presets().containerByName(player, name).get(strippedName)
                .whenComplete(Futures.whenComplete(res -> {
                    CommandAssertions.isTrue(res.isPresent(), "error.unknownPreset", Replacement.create("name", strippedName));
                    var preset = res.get();

                    var global = name.startsWith("g:");
                    var composer = MessageComposer.create()
                            .text(preset.detailComponent(global))
                            .newLine()
                            .text("<click:run_command:'/sbrp list %s'><change>[<i18n:words.back>]</click>", global ? "global" : "private")
                            .prependLines(20);
                    messageBlocker.ifEnabled(composer, comp -> comp.newLine().text("<click:run_command:'/sbrs chatblock false'><remove>[x]</click>"));
                    messageBlocker.announce(player, "[x]");
                    messageSender().sendMessage(player, composer.build());
                }, err -> handleCommandError(player, err)))
                .whenComplete(Futures.whenComplete(Consumers.emptyConsumer(), err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return storage.activeStorage().presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
