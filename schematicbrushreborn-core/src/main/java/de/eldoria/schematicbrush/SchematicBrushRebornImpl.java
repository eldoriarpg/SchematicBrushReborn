package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.provider.FlipProvider;
import de.eldoria.schematicbrush.brush.provider.IncludeAirProvider;
import de.eldoria.schematicbrush.brush.provider.OffsetProvider;
import de.eldoria.schematicbrush.brush.provider.PlacementProvider;
import de.eldoria.schematicbrush.brush.provider.ReplaceAllProvider;
import de.eldoria.schematicbrush.brush.provider.RotationProvider;
import de.eldoria.schematicbrush.brush.provider.SelectorProviderImpl;
import de.eldoria.schematicbrush.commands.Admin;
import de.eldoria.schematicbrush.commands.Brush;
import de.eldoria.schematicbrush.commands.Preset;
import de.eldoria.schematicbrush.commands.Settings;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.PresetContainer;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;
import de.eldoria.schematicbrush.listener.BrushModifier;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.schematics.SchematicBrushCache;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class SchematicBrushRebornImpl extends SchematicBrushReborn {

    private BrushSettingsRegistry settingsRegistry;
    private SchematicRegistry schematics;
    private Config config;
    private SchematicBrushCache cache;

    @Override
    public void onPluginEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            logger().warning("WorldEdit is not installed on this Server!");
            return;
        }

        MessageSender.create(this, "§6[SB]");
        var iLocalizer = ILocalizer.create(this, "en_US");
        iLocalizer.setLocale("en_US");

        schematics = new SchematicRegistry();

        settingsRegistry = new BrushSettingsRegistry();
        registerDefaults();

        saveDefaultConfig();
        config = new Config(this);

        cache = new SchematicBrushCache(this, config);
        schematics.register(SchematicCache.DEFAULT_CACHE, cache);

        reload();

        var notifyListener = new NotifyListener(this, config);
        var renderService = new RenderService(this, config);

        var messageBlocker = MessageBlockerAPI.builder(this).addWhitelisted("[SB]").build();

        var brushCommand = new Brush(this, schematics, config, settingsRegistry, messageBlocker);
        var presetCommand = new Preset(this, config, messageBlocker);
        var adminCommand = new Admin(this, schematics);
        var settingsCommand = new Settings(this, renderService, notifyListener, messageBlocker);

        enableMetrics();

        getServer().getScheduler().runTaskTimer(this, renderService, 0, 1);
        registerListener(new BrushModifier(), renderService, notifyListener);

        registerCommand("sbr", brushCommand);
        registerCommand("sbrp", presetCommand);
        registerCommand("sbra", adminCommand);
        registerCommand("sbrs", settingsCommand);
    }

    public void reload() {
        schematics.reload();
        config.reload();

        if (config.general().isCheckUpdates()) {
            Updater.butler(
                    new ButlerUpdateData(this, "schematicbrush.admin.reload", config.general().isCheckUpdates(),
                            false, 12, ButlerUpdateData.HOST)).start();
        }
    }

    @Override
    public void onPluginDisable() {
        cache.shutdown();
    }

    private void enableMetrics() {
        var metrics = new EldoMetrics(this, 7683);
        if (metrics.isEnabled()) {
            logger().info("§2Metrics enabled. Thank you <3");
        }

        // TODO refactor
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
                    if (getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                        return "FAWE";
                    }
                    return "WorldEdit";
                }));
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(GeneralConfig.class, de.eldoria.schematicbrush.config.sections.presets.Preset.class,
                SchematicConfig.class, SchematicSource.class, PresetContainer.class, PresetRegistry.class,
                SchematicSetBuilder.class);
    }

    @Override
    public SchematicRegistry schematics() {
        return schematics;
    }

    @Override
    public BrushSettingsRegistry brushSettingsRegistry() {
        return settingsRegistry;
    }

    @Override
    public Config config() {
        return config;
    }

    private void registerDefaults() {
        // SELECTORS
        settingsRegistry.registerSelector(SelectorProviderImpl.NAME.apply(schematics));
        settingsRegistry.registerSelector(SelectorProviderImpl.REGEX.apply(schematics));
        settingsRegistry.registerSelector(SelectorProviderImpl.DIRECTORY.apply(schematics));

        // SCHEMATIC MODIFIER
        settingsRegistry.registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.FIXED);
        settingsRegistry.registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.LIST);
        settingsRegistry.registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.RANDOM);

        settingsRegistry.registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.FIXED);
        settingsRegistry.registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.LIST);
        settingsRegistry.registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.RANDOM);

        // PLACEMENT MODIFIER
        settingsRegistry.registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.FIXED);
        settingsRegistry.registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.LIST);
        settingsRegistry.registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.RANGE);

        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.BOTTOM);
        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.DROP);
        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.MIDDLE);
        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.ORIGINAL);
        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.RAISE);
        settingsRegistry.registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.TOP);

        settingsRegistry.registerPlacementModifier(PlacementModifier.INCLUDE_AIR, IncludeAirProvider.FIXED);

        settingsRegistry.registerPlacementModifier(PlacementModifier.REPLACE_ALL, ReplaceAllProvider.FIXED);
    }
}
