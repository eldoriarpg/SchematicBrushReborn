package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;

    public Remove(Plugin plugin, Config config) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("name", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        if (args.hasFlag("g")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
            CommandAssertions.isTrue(config.presets().removePreset(name), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));
        } else {
            CommandAssertions.isTrue(config.presets().removePreset(player, name), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));
        }

        messageSender().sendMessage(player, "Preset §b" + name + "§r deleted!");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return config.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
