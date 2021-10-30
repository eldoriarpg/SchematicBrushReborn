package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.util.Colors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Info extends AdvancedCommand implements IPlayerTabExecutor {
    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.get();
    private final BukkitAudiences audiences;
    private final IMessageBlockerService messageBlocker;

    public Info(Plugin plugin, Config config, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("info")
                .withPermission("schematicbrush.brush.use")
                .addUnlocalizedArgument("name", true)
                .build());
        this.config = config;
        audiences = BukkitAudiences.create(plugin);
        this.messageBlocker = messageBlocker;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);

        var optPreset = config.presets().getPreset(player, name);

        CommandAssertions.isTrue(optPreset.isPresent(), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));

        var preset = optPreset.get();
        var message = messageBlocker.ifEnabled(preset.detailComponent(), m -> m + String.format("%n<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(message));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return config.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
