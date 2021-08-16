package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.schematicbrush.commands.BrushAdminCommand;
import de.eldoria.schematicbrush.commands.BrushCommand;
import de.eldoria.schematicbrush.commands.BrushModifyCommand;
import de.eldoria.schematicbrush.commands.SchematicPresetCommand;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.ConfigUpdater;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.Preset;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;

public class SchematicBrushReborn extends EldoPlugin {

    private SchematicCache schematics;
    private Config config;

    @Override
    public void onPluginDisable() {

    }

    public void reload() {
        // Nothing to be proud of...
        // Needs to be reworked.
        saveDefaultConfig();
        this.reloadConfig();
        ConfigUpdater.validateConfig(this);

        if (config == null) {
            config = new Config(this);
        } else {
            config.reload();
        }

        if (schematics == null) {
            schematics = new SchematicCache(this, config);
            schematics.init();
            Updater.butler(
                    new ButlerUpdateData(this, "schematicbrush.admin.reload", config.getGeneral().isCheckUpdates(),
                            false, 12, ButlerUpdateData.HOST)).start();
        } else {
            schematics.reload();
        }

    }

    @Override
    public void onPluginEnable(boolean reload) {
        if (!getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            logger().warning("WorldEdit is not installed on this Server!");
            return;
        }

        MessageSender.create(this, "§6[SB]");

        reload();

        BrushCommand brushCommand = new BrushCommand(this, schematics, config);
        BrushModifyCommand modifyCommand = new BrushModifyCommand(this, schematics, config);
        SchematicPresetCommand presetCommand = new SchematicPresetCommand(this, schematics, config);
        BrushAdminCommand adminCommand = new BrushAdminCommand(this, schematics);

        registerCommand("sbr", brushCommand);
        registerCommand("sbrm", modifyCommand);
        registerCommand("sbrp", presetCommand);
        registerCommand("sbra", adminCommand);

        enableMetrics();
    }

    private void enableMetrics() {
        EldoMetrics metrics = new EldoMetrics(this, 7683);
        if (metrics.isEnabled()) {
            logger().info("§2Metrics enabled. Thank you <3");
        }
        metrics.addCustomChart(new SimplePie("schematic_count",
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
        metrics.addCustomChart(new SimplePie("directory_count",
                () -> {
                    int sCount = schematics.directoryCount();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    int count = (int) Math.floor(sCount / 100d);
                    return ">" + count * 100;
                }));
        metrics.addCustomChart(new SimplePie("preset_count",
                () -> {
                    int sCount = getConfig().getStringList("presets").size();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    int count = (int) Math.floor(sCount / 100d);
                    return ">" + count * 100;
                }));

        metrics.addCustomChart(new SimplePie("world_edit_version",
                () -> {
                    if (this.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                        return "FAWE";
                    }
                    return "WorldEdit";
                }));
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(GeneralConfig.class, Preset.class, SchematicConfig.class, SchematicSource.class);
    }
}
