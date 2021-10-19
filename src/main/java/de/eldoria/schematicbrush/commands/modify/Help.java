package de.eldoria.schematicbrush.commands.modify;

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
        super(plugin, CommandMeta.builder("help").build());
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        messageSender().sendMessage(player,
                "This command allows you to modify a current used brush." + C.NEW_LINE
                + "§b/sbrm §na§r§bppend <schematic sets...>§r - Add one or more schematic sets to your brush." + C.NEW_LINE
                + "§b/sbrm §nr§r§bemove <id>§r - Remove a schematic set." + C.NEW_LINE
                + "§b/sbrm §ne§r§bdit <id> <brush>§r - Replace a brush with another brush." + C.NEW_LINE
                + "§b/sbrm §nrel§r§boad §r- Reload matching schematics, if new schematics were recently added."
                + "You may want to use §b/sbra reloadschematics§r first." + C.NEW_LINE
                + "§b/sbrm §ni§r§bnfo §r- Get all settings and a list of all schematic sets your brush uses." + C.NEW_LINE
                + "Use the id from the info command to change or remove a schematic set."
        );
    }
}
