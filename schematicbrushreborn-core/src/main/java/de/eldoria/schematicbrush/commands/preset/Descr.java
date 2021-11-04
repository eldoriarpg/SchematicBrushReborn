package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Descr extends AdvancedCommand implements IPlayerTabExecutor {
    private final ConfigurationImpl config;

    public Descr(Plugin plugin, ConfigurationImpl config) {
        super(plugin, CommandMeta.builder("descr")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("descr", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);

        if (name.startsWith("g:")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
        }

        var preset = config.presets().getPreset(player, name);

        CommandAssertions.isTrue(preset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

        preset.get().description(args.join(1));
        messageSender().sendMessage(player, "Changed description of preset §b" + name + "§r!");
        config.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return config.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
