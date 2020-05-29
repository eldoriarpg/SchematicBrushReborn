package de.eldoria.schematicbrush;

import de.eldoria.schematicbrush.commands.BrushAdminCommand;
import de.eldoria.schematicbrush.commands.BrushCommand;
import de.eldoria.schematicbrush.commands.BrushModifyCommand;
import de.eldoria.schematicbrush.commands.SchematicPresetCommand;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SchematicBrushReborn extends JavaPlugin {

    private SchematicCache schematics;
    private static Logger logger;

    public static Logger logger() {
        return logger;
    }

    @Override
    public void onDisable() {

    }

    public void onReload() {
        schematics.reload();
    }

    @Override
    public void onEnable() {
        logger = getLogger();

        Metrics metrics = new Metrics(this, 7683);

        if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            logger.warning("WorldEdit is not installed on this Server!");
            return;
        }

        saveDefaultConfig();
        schematics = new SchematicCache(this);
        schematics.init();

        BrushCommand brushCommand = new BrushCommand(this, schematics);
        BrushModifyCommand brushModifyCommand = new BrushModifyCommand(this, schematics);
        SchematicPresetCommand schematicPresetCommand = new SchematicPresetCommand(this, schematics);
        BrushAdminCommand brushAdminCommand = new BrushAdminCommand(this, schematics);

        getCommand("sbr").setExecutor(brushCommand);
        getCommand("sbrm").setExecutor(brushModifyCommand);
        getCommand("sbrp").setExecutor(schematicPresetCommand);
        getCommand("sbra").setExecutor(brushAdminCommand);
    }
}
