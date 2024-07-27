/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Migrate extends AdvancedCommand implements ITabExecutor {

    private final StorageRegistry storageRegistry;

    public Migrate(Plugin plugin, StorageRegistry storageRegistry) {
        super(plugin, CommandMeta.builder("migrate")
                .addUnlocalizedArgument("source", true)
                .addUnlocalizedArgument("target", true)
                .withPermission(Permissions.Admin.MIGRATE)
                .build());
        this.storageRegistry = storageRegistry;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var sourceNameable = Nameable.of(args.asString(0));
        var targetNameable = Nameable.of(args.asString(1));

        var source = storageRegistry.get(sourceNameable);
        var target = storageRegistry.get(targetNameable);

        CommandAssertions.isFalse(source == null, "commands.admin.migrate.sourceTypeInvalid",
                Replacement.create("type", sourceNameable.name()));
        CommandAssertions.isFalse(target == null, "commands.admin.migrate.targetTypeInvalid",
                Replacement.create("type", targetNameable.name()));


        messageSender().sendMessage(sender, "commands.admin.migrate.migrationStarted");
        storageRegistry.migrate(sourceNameable, targetNameable)
                .thenRun(() -> messageSender().sendMessage(sender, "commands.admin.migrate.migrationDone"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return Completion.complete(args.asString(0), storageRegistry.registry().keySet()
                    .stream()
                    .map(Nameable::name));
        }
        if (args.sizeIs(2)) {
            return Completion.complete(args.asString(1), storageRegistry.registry().keySet()
                    .stream()
                    .map(Nameable::name));
        }

        return Collections.emptyList();
    }
}
