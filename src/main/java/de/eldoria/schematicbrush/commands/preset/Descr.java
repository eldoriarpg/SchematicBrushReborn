package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.Preset;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        String name = args.asString(0);

        Optional<Preset> preset = config.getPreset(name);
        CommandAssertions.isTrue(preset.isPresent(), "Preset §b" + name + "§r does not exist.");

        preset.get().setDescription(args.join(1));
        messageSender().sendMessage(player, "Changed description of preset §b" + name + "§r!");
        config.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 2 && args.asString(1).isEmpty()) {
            List<String> presets = TabUtil.getPresets(args.asString(1), 50, config);
            presets.add("<name of preset>");
            return presets;
        }
        if (args.size() == 2) {
            return TabUtil.getPresets(args.asString(1), 50, config);
        }

        return Collections.singletonList("<description>");

    }
}
