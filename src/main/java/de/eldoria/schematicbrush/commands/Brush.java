package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Command which is used to create a new brush. Rewrite of old schbr command.
 */
public class Brush extends AdvancedCommand implements Randomable, IPlayerTabExecutor {
    private final SchematicCache schematicCache;
    private final Config config;

    public Brush(Plugin plugin, SchematicCache schematicCache, Config config) {
        super(plugin, CommandMeta.builder("sbr")
                .withPermission("schematicbrush.brush.use")
                .build());
        this.schematicCache = schematicCache;
        this.config = config;
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
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        var last = args.asString(args.size() - 1);
        if (TabUtil.isFlag(args.asArray())) {
            return TabUtil.getFlagComplete(last);
        }

        return TabUtil.getSchematicSetSyntax(args.asArray(), schematicCache, config);
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.isEmpty()) {
            help(player);
            return;
        }

        var settings = BrushSettingsParser.parseBrush(player, config, schematicCache, args.asArray());

        if (!settings.isPresent()) return;

        com.sk89q.worldedit.command.tool.brush.Brush schematicBrush = new SchematicBrush(plugin(), player, settings.get());

        var success = WorldEditBrushAdapter.setBrush(player, schematicBrush);
        if (success) {
            messageSender().sendMessage(player,
                    "Brush using " + settings.get().getSchematicCount() + " schematics created.");
        }
    }
}
