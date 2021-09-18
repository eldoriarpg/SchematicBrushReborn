package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.Preset;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Info extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;

    public Info(Plugin plugin, Config config) {
        super(plugin, CommandMeta.builder("info")
                .withPermission("schematicbrush.brush.use")
                .addUnlocalizedArgument("name", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);

        var optPreset = config.getPreset(name);

        CommandAssertions.isTrue(optPreset.isPresent(), "Preset §b" + name + "§r does not exist.");

        var preset = optPreset.get();
        var schematicSets = preset.getFilter();
        List<String> schematicSetsList = new ArrayList<>();
        for (var i = 0; i < schematicSets.size(); i++) {
            schematicSetsList.add("§b" + (i + 1) + "| §r" + schematicSets.get(i));
        }

        messageSender().sendMessage(player, "Information about preset §b" + preset.getName() + "§r" + C.NEW_LINE
                                            + "§bDescription:§r " + preset.getDescription() + C.NEW_LINE
                                            + "§bSchematic sets (" + schematicSetsList.size() + ")§r:" + C.NEW_LINE
                                            + String.join(C.NEW_LINE, schematicSetsList));
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
