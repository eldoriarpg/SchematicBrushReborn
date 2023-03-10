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
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Descr extends AdvancedCommand implements IPlayerTabExecutor {
    private final Storage storage;

    public Descr(Plugin plugin, Storage storage) {
        super(plugin, CommandMeta.builder("descr")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("descr", true)
                .hidden()
                .build());
        this.storage = storage;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);

        if (name.startsWith("g:")) CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);

        var strippedName = name.replaceAll("^g:", "");
        var container = storage.presets().containerByName(player, name);
        container.get(strippedName)
                .whenComplete(Futures.whenComplete(preset -> {
                    CommandAssertions.isTrue(preset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

                    preset.get().description(args.join(1));
                    container.add(preset.get());
                    messageSender().sendMessage(player, "Changed description of preset §b" + name + "§r!");
                }, err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return storage.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
