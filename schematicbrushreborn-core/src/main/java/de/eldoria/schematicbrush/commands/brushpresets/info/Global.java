/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brushpresets.info;

import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.util.BasePageCommand;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Global extends BasePageCommand implements IPlayerTabExecutor {

    private final StorageRegistry storage;
    private final BrushSettingsRegistry registry;

    public Global(Plugin plugin, StorageRegistry storage, MessageBlocker messageBlocker, BrushSettingsRegistry registry) {
        super(plugin, CommandMeta.builder("global")
                .addUnlocalizedArgument("page", false)
                .build(), messageBlocker);
        this.storage = storage;
        this.registry = registry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        int index = args.asInt(0, 0);
        storage.activeStorage().brushes().globalContainer().paged().whenComplete(Futures.whenComplete(paged -> {
            paged.page(index, PAGE_SIZE).whenComplete(Futures.whenComplete(entries -> {
                boolean delete = player.hasPermission(Permissions.Preset.GLOBAL);
                var composer = MessageComposer.create();
                addPageHeader(composer, "words.brushPreset", true);
                addEntries(composer, entries, e -> e.infoComponent(true, delete, registry));
                addPageFooter(composer, index, paged);
                send(composer, player);
            }, err -> handleCommandError(player, err)));
        }, err -> handleCommandError(player, err)));
    }

}
