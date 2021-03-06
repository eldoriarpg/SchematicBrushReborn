package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.command.tool.brush.Brush;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Command which is used to create a new brush. Rewrite of old schbr command.
 */
public class BrushCommand extends EldoCommand implements Randomable {
    private final Plugin plugin;
    private final SchematicCache schematicCache;
    private final Config config;

    public BrushCommand(Plugin plugin, SchematicCache schematicCache, Config config) {
        super(plugin);
        this.plugin = plugin;
        this.schematicCache = schematicCache;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("schematicbrush.brush.use")) {
            messageSender().sendError(player, "You don't have the permission to do this!");
            return true;
        }
        if (args.length == 0) {
            help(player);
            return true;
        }

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, config, schematicCache, args);

        if (!settings.isPresent()) {
            return true;
        }

        Brush schematicBrush = new SchematicBrush(plugin, player, settings.get());

        boolean success = WorldEditBrushAdapter.setBrush(player, schematicBrush);
        if (success) {
            messageSender().sendMessage(player, "Brush using "
                    + settings.get().getSchematicCount() + " schematics created.");
        }
        return true;
    }

    private void help(Player player) {
        messageSender().sendMessage(player,
                "A brush consists of one or more schematic set. A schematic set is defined by a §bselector§r and §bmodifiers§r." + C.NEW_LINE
                        + "A schematic set contains a §bselector§r and a optional §brotation§r, §bflip§r and §bweight§r value:" + C.NEW_LINE
                        + "  §b<selector>@rotation!flip:weight§r" + C.NEW_LINE
                        + "§b§l§nSELECTOR§r" + C.NEW_LINE
                        + "§bSelector§r can be a §bname§r with a optional wildcard §b(*)§r, §b$directory§r, §b$preset§r or §b^regex§r" + C.NEW_LINE
                        + "§bRotation§r is the rotation of the schematic. §b*§r is a random rotation." + C.NEW_LINE
                        + "§bFlip§r is the flip of the schematic. §b*§r is a random flip." + C.NEW_LINE
                        + "§bWeight§r is the chance that this schematic set is selected, if the brush contains more than one set." + C.NEW_LINE
                        + "§b§l§nFLAGS§r" + C.NEW_LINE
                        + "When you use a brush you can also add some §bflags§r for a better behaviour." + C.NEW_LINE
                        + "-§bincludeair §r- Air in the schematic will replace blocks. Default: §bfalse§r" + C.NEW_LINE
                        + "-§breplaceAll §r- Existing blocks will be replaced. Default: §bfalse§r" + C.NEW_LINE
                        + "-§byoffset:number §r- Will move the schematic up or down on pasting. Default: §b0§r" + C.NEW_LINE
                        + "-§bplacement:type §r- Choose in which way a schematic will be placed. Default: §bdrop§r" + C.NEW_LINE
                        + "  - §bmiddle §r- Origin in center." + C.NEW_LINE
                        + "  - §bbottom§r - Origin on bottom of schematic." + C.NEW_LINE
                        + "  - §bdrop §r- Origin on lowest non air block in schematic." + C.NEW_LINE
                        + "  - §btop §r- Origin on top of schematic." + C.NEW_LINE
                        + "  - §braise §r- Origin on lowest non air block in schematic." + C.NEW_LINE
        );
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String last = args[args.length - 1];
        if (TabUtil.isFlag(args)) {
            return TabUtil.getFlagComplete(last);
        }

        return TabUtil.getSchematicSetSyntax(args, schematicCache, config);
    }
}
