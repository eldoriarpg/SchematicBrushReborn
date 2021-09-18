package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Descr extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;

    public Descr(Plugin plugin, Config config) {
        super(plugin, CommandMeta.builder("descr")
                .withPermission("schematicbrush.preset.modify")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("descr", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);

        var preset = config.getPreset(name);
        CommandAssertions.isTrue(preset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

        preset.get().setDescription(args.join(1));
        messageSender().sendMessage(player, "Changed description of preset §b" + name + "§r!");
        config.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 2 && args.asString(1).isEmpty()) {
            var presets = TabUtil.getPresets(args.asString(1), 50, config);
            presets.add("<name of preset>");
            return presets;
        }
        if (args.size() == 2) {
            return TabUtil.getPresets(args.asString(1), 50, config);
        }

        return Collections.singletonList("<description>");

    }
}
