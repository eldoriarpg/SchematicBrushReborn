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
import java.util.Objects;
import java.util.stream.Collectors;

public class RemoveSet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;

    public RemoveSet(Plugin plugin, Config config) {
        super(plugin, CommandMeta.builder("removeSet")
                .withPermission("schematicbrush.preset.modify")
                .addUnlocalizedArgument("name", true)
                .addUnlocalizedArgument("id", true)
                .build());
        this.config = config;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        var ids = args.args(1);

        var preset = config.getPreset(name);
        CommandAssertions.isTrue(preset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

        var schematicSets = preset.get().getFilter();

        for (var id : ids) {
            try {
                var i = Integer.parseInt(id);
                if (i > schematicSets.size() || i < 1) {
                    messageSender().sendError(player, "§b" + id + "§r is not a valid id.");
                    return;
                }
                schematicSets.set(i - 1, null);
            } catch (NumberFormatException e) {
                messageSender().sendError(player, "§b" + id + "§r is not a valid id.");
                return;
            }
        }
        preset.get().setFilter(schematicSets.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        config.save();
        messageSender().sendMessage(player, "Removed schematic set from preset " + name);
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
        if (args.size() == 3) {

            return Collections.singletonList("<id of schematic set>");
        }
        return Collections.emptyList();
    }
}
