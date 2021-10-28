package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Help extends AdvancedCommand implements IPlayerTabExecutor {
    public Help(Plugin plugin) {
        super(plugin, CommandMeta.builder("help")
                .withPermission("schematicbrush.preset.use")
                .build());
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        //TODO rewrite this funny stuff or just delete it...
        messageSender().sendMessage(player,
                "This command allows you to save and modify schematic set presets." + C.NEW_LINE
                + "§b/sbrp save§nc§r§burrent <id> §r- Save your current equiped schematic set as a preset." + C.NEW_LINE
                + "§b/sbrp §ns§r§bave <id> <schematic sets...> §r- Save one or more schematic sets as a preset." + C.NEW_LINE
                + "§b/sbrp §nd§r§bescr §r- Set a description for a preset." + C.NEW_LINE
                + "§b/sbrp §na§r§bppend§ns§r§bet <id> <schematic sets...> §r- Add one or more schematic sets to a preset." + C.NEW_LINE
                + "§b/sbrp §nr§r§bemove§ns§r§bet <id> <id> §r- Remove schematic set from a preset." + C.NEW_LINE
                + "§b/sbrp §nr§r§bemove <id> §r- Remove a preset." + C.NEW_LINE
                + "§b/sbrp §ni§r§bnfo <id> §r- Get a list of schmematic sets inside a preset." + C.NEW_LINE
                + "§b/sbrp §nl§r§bist §r- Get a list of all presets with description." + C.NEW_LINE
                + "Use the id from the info command to change or remove a schematic set."
        );
    }
}
