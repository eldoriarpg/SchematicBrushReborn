package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Preview extends AdvancedCommand implements IPlayerTabExecutor {
    private final RenderService renderService;

    public Preview(Plugin plugin, RenderService renderService) {
        super(plugin, CommandMeta.builder("preview")
                .withPermission(Permissions.Brush.PREVIEW)
                .addUnlocalizedArgument("state", true)
                .build());
        this.renderService = renderService;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (!renderService.isActive()) {
            messageSender().sendMessage(player, "The preview can not be used with fawe.");
            return;
        }
        var state = args.asBoolean(0);
        renderService.setState(player, state);
        if (state) {
            messageSender().sendMessage(player, "Preview active.");
        } else {
            messageSender().sendMessage(player, "Preview disabled.");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return TabCompleteUtil.completeBoolean(args.asString(0));
    }
}
