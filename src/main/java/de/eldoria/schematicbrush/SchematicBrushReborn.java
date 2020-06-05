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
    private static boolean debug;

    public static Logger logger() {
        return logger;
    }

    public static boolean debugMode() {
        return debug;
    }

    @Override
    public void onDisable() {

    }

    public void reload() {
        saveDefaultConfig();
        this.reloadConfig();
        ConfigUpdater.validateConfig(this);
        debug = getConfig().getBoolean("debug");

        if (schematics == null) {
            schematics = new SchematicCache(this);
            schematics.init();
        } else {
            schematics.reload();
        }

    }

    @Override
    public void onEnable() {
        logger = getLogger();

        Metrics metrics = new Metrics(this, 7683);

        if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            logger.warning("WorldEdit is not installed on this Server!");
            return;
        }

        reload();

        BrushCommand brushCommand = new BrushCommand(this, schematics);
        BrushModifyCommand modifyCommand = new BrushModifyCommand(this, schematics);
        SchematicPresetCommand presetCommand = new SchematicPresetCommand(this, schematics);
        BrushAdminCommand adminCommand = new BrushAdminCommand(this, schematics);

        getCommand("sbr").setExecutor(brushCommand);
        getCommand("sbrm").setExecutor(modifyCommand);
        getCommand("sbrp").setExecutor(presetCommand);
        getCommand("sbra").setExecutor(adminCommand);
    }
}
