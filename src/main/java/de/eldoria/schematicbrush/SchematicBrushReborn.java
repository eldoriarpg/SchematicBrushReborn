package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.Admin;
import de.eldoria.schematicbrush.commands.Brush;
import de.eldoria.schematicbrush.commands.Modify;
import de.eldoria.schematicbrush.commands.Preset;
import de.eldoria.schematicbrush.commands.Settings;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.ConfigUpdater;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.listener.BrushModifier;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;

public class SchematicBrushReborn extends EldoPlugin {

    private BrushSettingsRegistry settingsRegistry;
    private SchematicRegistry schematics;
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
            schematics = new SchematicRegistry();
            var cache = new SchematicBrushCache(this, config);
            schematics.register(SchematicBrushCache.key, cache);
            if (config.getGeneral().isCheckUpdates()) {
                Updater.butler(
                        new ButlerUpdateData(this, "schematicbrush.admin.reload", config.getGeneral().isCheckUpdates(),
                                false, 12, ButlerUpdateData.HOST)).start();
            }
        } else {
            schematics.reload();
        }

        if(settingsRegistry == null){
            settingsRegistry = new BrushSettingsRegistry();
            settingsRegistry.registerDefault(schematics);
        }
    }

    @Override
    public void onPluginEnable(boolean reload) {
        if (!getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            logger().warning("WorldEdit is not installed on this Server!");
            return;
        }

        MessageSender.create(this, "§6[SB]");
        var iLocalizer = ILocalizer.create(this, "en_US");
        iLocalizer.setLocale("en_US");

        reload();

        var brushCommand = new Brush(this, schematics, config);
        var modifyCommand = new Modify(this, schematics, config);
        var presetCommand = new Preset(this, schematics, config);
        var adminCommand = new Admin(this, schematics);

        var notifyListener = new NotifyListener(this, config);

        enableMetrics();

        var renderService = new RenderService(this, config);
        var settingsCommand = new Settings(this, renderService, notifyListener);
        getServer().getScheduler().runTaskTimer(this, renderService, 0, 1);
        registerListener(new BrushModifier(), renderService, notifyListener);

        registerCommand("sbr", brushCommand);
        registerCommand("sbrm", modifyCommand);
        registerCommand("sbrp", presetCommand);
        registerCommand("sbra", adminCommand);
        registerCommand("sbrs", settingsCommand);
    }

    private void enableMetrics() {
        var metrics = new EldoMetrics(this, 7683);
        if (metrics.isEnabled()) {
            logger().info("§2Metrics enabled. Thank you <3");
        }
        metrics.addCustomChart(new SimplePie("schematic_count",
                () -> {
                    var sCount = schematics.schematicCount();
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    if (sCount < 250) return "<250";
                    if (sCount < 500) return "<500";
                    if (sCount < 1000) return "<1000";
                    var count = (int) Math.floor(sCount / 1000d);
                    return ">" + count * 1000;
                }));
        metrics.addCustomChart(new SimplePie("directory_count",
                () -> {
                    var sCount = schematics.directoryCount();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    var count = (int) Math.floor(sCount / 100d);
                    return ">" + count * 100;
                }));
        metrics.addCustomChart(new SimplePie("preset_count",
                () -> {
                    var sCount = getConfig().getStringList("presets").size();
                    if (sCount < 10) return "<10";
                    if (sCount < 50) return "<50";
                    if (sCount < 100) return "<100";
                    var count = (int) Math.floor(sCount / 100d);
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
        return Arrays.asList(GeneralConfig.class, de.eldoria.schematicbrush.config.sections.Preset.class, SchematicConfig.class, SchematicSource.class);
    }

    public SchematicRegistry schematics() {
        return schematics;
    }

    public BrushSettingsRegistry brushSettingsRegistry(){
        return
    }

    public Config config() {
        return config;
    }
}
