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

        if (getConfig().getBoolean("metrics")) {
            enableMetrics();
        }
    }

    private void enableMetrics() {
        Metrics metrics = new Metrics(this, 7683);
        metrics.addCustomChart(new Metrics.SimplePie("schematic_count",
                () -> {
                    int sCount = schematics.schematicCount();
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    if (sCount < 250) return "<250";
                    if (sCount < 500) return "<500";
                    if (sCount < 1000) return "<1000";
                    int count = (int) Math.floor(sCount / 1000d);
                    return ">" + count * 1000;
                }));
        metrics.addCustomChart(new Metrics.SimplePie("directory_count",
                () -> {
                    int sCount = schematics.directoryCount();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    int count = (int) Math.floor(sCount / 100d);
                    return ">" + count * 100;
                }));
        metrics.addCustomChart(new Metrics.SimplePie("preset_count",
                () -> {
                    int sCount = getConfig().getStringList("presets").size();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    int count = (int) Math.floor(sCount / 100d);
                    return ">" + count * 100;
                }));

        metrics.addCustomChart(new Metrics.SimplePie("world_edit_version",
                () -> {
                    if (this.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                        return "FAWE";
                    }
                    return "WorldEdit";
                }));
    }
}
