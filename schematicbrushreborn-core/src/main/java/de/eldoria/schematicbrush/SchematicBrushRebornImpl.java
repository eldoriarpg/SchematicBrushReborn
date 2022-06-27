/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.AdvancedPie;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.spigotupdater.SpigotUpdateData;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistryImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshotImpl;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.provider.FilterProvider;
import de.eldoria.schematicbrush.brush.provider.FlipProvider;
import de.eldoria.schematicbrush.brush.provider.IncludeAirProvider;
import de.eldoria.schematicbrush.brush.provider.OffsetProvider;
import de.eldoria.schematicbrush.brush.provider.PlacementProvider;
import de.eldoria.schematicbrush.brush.provider.ReplaceAllProvider;
import de.eldoria.schematicbrush.brush.provider.RotationProvider;
import de.eldoria.schematicbrush.brush.provider.SelectorProviderImpl;
import de.eldoria.schematicbrush.commands.Admin;
import de.eldoria.schematicbrush.commands.Brush;
import de.eldoria.schematicbrush.commands.Settings;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.config.sections.GeneralConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicSourceImpl;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushContainer;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresetContainer;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.listener.BrushModifier;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.schematics.SchematicBrushCache;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.SchematicRegistryImpl;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistryImpl;
import de.eldoria.schematicbrush.storage.YamlStorage;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.UserData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SchematicBrushRebornImpl extends SchematicBrushReborn {

    private BrushSettingsRegistry settingsRegistry;
    private SchematicRegistryImpl schematics;
    private ConfigurationImpl configuration;
    private RenderService renderService;
    private StorageRegistryImpl storageRegistry;
    private Storage storage;

    public SchematicBrushRebornImpl() {
    }

    public SchematicBrushRebornImpl(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onPluginLoad() throws Throwable {
        settingsRegistry = new BrushSettingsRegistryImpl();
        registerDefaults();


        configuration = new ConfigurationImpl(this);

        var yamlStorage = new YamlStorage(configuration);
        storageRegistry = new StorageRegistryImpl(yamlStorage, configuration);
        storageRegistry.register(StorageRegistry.YAML, yamlStorage);

        schematics = new SchematicRegistryImpl();
    }

    @Override
    public void onPluginEnable() {
        MessageSender.create(this, "§6[SB]");
        ILocalizer.create(this, "en_US").setLocale("en_US");

        schematics.register(SchematicCache.STORAGE, new SchematicBrushCache(this, configuration));
        storage = storageRegistry.activeStorage();

        reload();

        var notifyListener = new NotifyListener(this, configuration);
        renderService = new RenderService(this, configuration);

        var messageBlocker = MessageBlockerAPI.builder(this).addWhitelisted("[SB]").build();

        var brushCommand = new Brush(this, schematics, storage, settingsRegistry, messageBlocker);
        var presetCommand = new de.eldoria.schematicbrush.commands.Preset(this, storage, messageBlocker);
        var adminCommand = new Admin(this, schematics, storageRegistry);
        var settingsCommand = new Settings(this, configuration, renderService, notifyListener, messageBlocker);

        enableMetrics();

        getServer().getScheduler().runTaskTimer(this, renderService, 0, 1);
        registerListener(new BrushModifier(), renderService, notifyListener);

        registerCommand(brushCommand);
        registerCommand(presetCommand);
        registerCommand(adminCommand);
        registerCommand(settingsCommand);

        if (configuration.general().isCheckUpdates() && UserData.get().isPremium()) {
            Updater.spigot(new SpigotUpdateData(this, Permissions.Admin.RELOAD, configuration.general().isCheckUpdates(),
                    UserData.get().resourceId())).start();
        }
    }

    public void reload() {
        schematics.reload();
        configuration.reload();
    }

    @Override
    public void onPluginDisable() {
        configuration.save();
        schematics.shutdown();
        storageRegistry.shutdown();
    }

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        return new EntryData[]{new EntryData("Customer Data", UserData.get().asString()),
                new EntryData("Performance", String.format("Render Time: %s ms%nRender Operation Queue: %s%nOperation Paket Count: %s",
                        renderService.renderTimeAverage(), renderService.paketQueueSize(), renderService.paketQueuePaketCount()))};
    }

    private void enableMetrics() {
        var metrics = new EldoMetrics(this, 7683);
        if (metrics.isEnabled()) {
            logger().info("§2Metrics enabled. Thank you <3");
        }

        metrics.addCustomChart(new SimplePie("schematic_count",
                () -> reduceMetricValue(schematics.schematicCount(), 1000, 50, 100, 250, 500, 1000)));

        metrics.addCustomChart(new SimplePie("directory_count",
                () -> reduceMetricValue(schematics.directoryCount(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("preset_count",
                () -> reduceMetricValue(storage.presets().count().join(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("brush_count",
                () -> reduceMetricValue(storage.brushes().count().join(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("premium",
                () -> String.valueOf(UserData.get().isPremium())));

        metrics.addCustomChart(new AdvancedPie("installed_storage_type",
                () -> storageRegistry.registry().keySet().stream().collect(Collectors.toMap(Nameable::name, e -> 1))));

        metrics.addCustomChart(new SimplePie("used_storage_type",
                () -> configuration.general().storageType().name()));

        metrics.addCustomChart(new SimplePie("world_edit_version",
                () -> getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit") ? "FAWE" : "WorldEdit"));
    }

    private String reduceMetricValue(int count, int baseValue, int... steps) {
        for (var step : steps) {
            if (count < step) {
                return "<" + step;
            }
        }
        var reduced = (int) Math.floor(count / (double) baseValue);
        return ">" + reduced * baseValue;
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(GeneralConfigImpl.class, Preset.class,
                SchematicConfigImpl.class, SchematicSourceImpl.class, YamlPresetContainer.class, YamlPresets.class, YamlBrushes.class, YamlBrushContainer.class,
                SchematicSetBuilderImpl.class, de.eldoria.schematicbrush.storage.brush.Brush.class, BrushBuilderSnapshotImpl.class);
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
    public StorageRegistry storageRegistry() {
        return storageRegistry;
    }

    @Override
    public Configuration config() {
        return configuration;
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

        settingsRegistry.registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.FIXED);
        settingsRegistry.registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.LIST);
        settingsRegistry.registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.RANGE);

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

        settingsRegistry.registerPlacementModifier(PlacementModifier.FILTER, FilterProvider.BLOCK_FILTER);
    }
}
