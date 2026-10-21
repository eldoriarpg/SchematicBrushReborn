/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.config.sections.EditorType;
import de.eldoria.schematicbrush.editor.EditorPreferences;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Switches the brush editor of a player between dialogs and chat. Toggles without an argument.
 */
public class Editor extends AdvancedCommand implements IPlayerTabExecutor {
    private final EditorPreferences preferences;

    public Editor(Plugin plugin, EditorPreferences preferences) {
        super(plugin, CommandMeta.builder("editor")
                .addUnlocalizedArgument("type", false)
                .withPermission(Permissions.Brush.USE)
                .build());
        this.preferences = preferences;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var editor = args.isEmpty() ? toggled(preferences.editor(player)) : args.asEnum(0, EditorType.class);
        preferences.choose(player, editor);
        if (preferences.editor(player) != editor) {
            messageSender().sendError(player, "commands.settings.editor.unsupported");
            return;
        }
        messageSender().sendMessage(player, "commands.settings.editor." + (editor == EditorType.DIALOG ? "dialog" : "chat"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.sizeIs(1)) {
            return Completion.complete(args.asString(0), "dialog", "chat");
        }
        return Collections.emptyList();
    }

    private static EditorType toggled(EditorType editor) {
        return editor == EditorType.DIALOG ? EditorType.CHAT : EditorType.DIALOG;
    }
}
