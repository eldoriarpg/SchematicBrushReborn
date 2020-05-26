package de.eldoria.schematicbrush;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.eldoria.schematicbrush.commands.BrushCommand;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SchematicBrushReborn extends JavaPlugin {

    private SchematicCache schematics;
    private static Logger logger;
    private WorldEditPlugin worldEditPlugin;
    private WorldEdit worldEdit;

    public static Logger logger() {
        return logger;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        logger = getLogger();
        Plugin weCheck = this.getServer().getPluginManager().getPlugin("WorldEdit");

        if (weCheck == null) {
            logger.warning("WorldEdit is not installed on this Server!");
            return;
        }

        worldEditPlugin = (WorldEditPlugin) weCheck;
        worldEdit = worldEditPlugin.getWorldEdit();

        saveDefaultConfig();
        schematics = new SchematicCache(this);
        schematics.init();

        BrushCommand brushCommand = new BrushCommand(this, schematics);

        getCommand("schematicbrush").setExecutor(brushCommand);
    }
}
