package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
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
                .withPermission("schematicbrush.preset.remove")
                .addUnlocalizedArgument("name", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        CommandAssertions.isTrue(config.removePreset(name), "Preset §b" + name + "§r does not exist.");
        messageSender().sendMessage(player, "Preset §b" + name + "§r deleted!");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 2 && args.asString(1).isEmpty()) {
            var presets = TabUtil.getPresets(args.asString(1), 50, config);
            presets.add("<name of preset>");
            return presets;
        }
        if (args.size() == 2) {
            return TabUtil.getPresets(args.asString(0), 50, config);
        }
        return Collections.emptyList();
    }
}
