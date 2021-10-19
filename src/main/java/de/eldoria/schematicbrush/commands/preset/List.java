package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class List extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;

    public List(Plugin plugin, Config config) {
        super(plugin, CommandMeta.builder("list")
                .withPermission("schematicbrush.brush.use")
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var presetString = config.getPresets().stream()
                .map(preset -> "§bID: §r" + preset.getName() + C.NEW_LINE + "  §bDesc:§r " + preset.getDescription())
                .collect(Collectors.joining("\n"));
        messageSender().sendMessage(player, presetString);
    }
}
