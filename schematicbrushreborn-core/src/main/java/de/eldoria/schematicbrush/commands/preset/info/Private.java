package de.eldoria.schematicbrush.commands.preset.info;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.util.Colors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Private extends AdvancedCommand implements IPlayerTabExecutor {
    public static final String RIGHT_ARROW = "»»»";
    public static final String LEFT_ARROW = "«««";
    private static final int PAGE_SIZE = 10;

    private final Storage storage;
    private final MiniMessage miniMessage;
    private final BukkitAudiences audiences;
    private MessageBlocker messageBlocker;

    public Private(Plugin plugin, Storage storage, MessageBlocker messageBlocker) {
        super(plugin, CommandMeta.builder("private")
                .addUnlocalizedArgument("page", false)
                .build());
        this.storage = storage;
        this.messageBlocker = messageBlocker;
        miniMessage = MiniMessage.miniMessage();
        audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        messageBlocker.blockPlayer(player);

        var paged = storage.presets().playerContainer(player).paged();
        int index = args.asInt(0, 0);
        paged.page(index, PAGE_SIZE).whenComplete(Futures.whenComplete(entries -> {
            var page = entries.stream()
                    .map(preset -> "  " + preset.infoComponent(false, true))
                    .toList();
            var composer = MessageComposer.create()
                    .text("Presets").newLine()
                    .text("<%s><click:run_command:'/sbrp list global'>[Global]", Colors.CHANGE).space().text("<%s>[Private]", Colors.ADD)
                    .newLine()
                    .text(page)
                    .newLine();

            if (index == 0) {
                composer.text("<%s>%s", Colors.INACTIVE, LEFT_ARROW);
            } else {
                composer.text("<click:run_command:'/sbrp list private %s'><%s>%s</click>", index - 1, Colors.CHANGE, LEFT_ARROW);
            }
            composer.text(" %s / %s ", index + 1, paged.pages(PAGE_SIZE));

            if (index + 1 == paged.pages(PAGE_SIZE)) {
                composer.text("<%s>%s", Colors.INACTIVE, RIGHT_ARROW);
            } else {
                composer.text("<click:run_command:'/sbrp list private %s'>%s</click>", index + 1, RIGHT_ARROW);
            }
            messageBlocker.ifEnabled(() -> composer.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
            messageBlocker.announce(player, "[x]");
            audiences.sender(player).sendMessage(miniMessage.deserialize(composer.build()));
        }, err -> {

        }));
    }
}
