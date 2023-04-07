/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.AdvancedPie;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.config.template.PluginBaseConfiguration;
import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.debug.UserData;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateChecker;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.jacksonbukkit.JacksonBukkit;
import de.eldoria.jacksonbukkit.JacksonPaper;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistryImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshotImpl;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.commands.Admin;
import de.eldoria.schematicbrush.commands.Brush;
import de.eldoria.schematicbrush.commands.BrushPresets;
import de.eldoria.schematicbrush.commands.Modify;
import de.eldoria.schematicbrush.commands.Settings;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.JacksonConfiguration;
import de.eldoria.schematicbrush.config.LegacyConfiguration;
import de.eldoria.schematicbrush.config.sections.GeneralConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicSourceImpl;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushContainer;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresetContainer;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.config.serialization.deserilizer.BrushBuilderSnapshotDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.FlipDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.RotationDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.SchematicSetBuilderDeserializer;
import de.eldoria.schematicbrush.config.serialization.serializer.FlipSerializer;
import de.eldoria.schematicbrush.config.serialization.serializer.RotationSerializer;
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
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SchematicBrushRebornImpl extends SchematicBrushReborn {

    private BrushSettingsRegistryImpl settingsRegistry;
    private SchematicRegistryImpl schematics;
    private JacksonConfiguration configuration;
    private RenderService renderService;
    private StorageRegistryImpl storageRegistry;
    private Storage storage;
    private SimpleModule sbrModule;

    public SchematicBrushRebornImpl() {
    }

    public SchematicBrushRebornImpl(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onPluginLoad() throws Throwable {
        settingsRegistry = new BrushSettingsRegistryImpl();
        schematics = new SchematicRegistryImpl();

        settingsRegistry.registerDefaults(schematics);

        configuration = new JacksonConfiguration(this);
        PluginBaseConfiguration base = configuration.secondary(PluginBaseConfiguration.KEY);
        if (base.version() == 0) {
            var legacyConfiguration = new LegacyConfiguration(this);
            getLogger().log(Level.INFO, "Migrating configuration to jackson.");
            configuration.main().generalConfig((GeneralConfigImpl) legacyConfiguration.general());
            configuration.main().schematicConfig((SchematicConfigImpl) legacyConfiguration.schematicConfig());
            configuration.replace(JacksonConfiguration.BRUSHES, (YamlBrushes) legacyConfiguration.brushes());
            configuration.replace(JacksonConfiguration.PRESETS, (YamlPresets) legacyConfiguration.presets());
            base.version(1);
            base.lastInstalledVersion(this);
            configuration.save();
        }

        var yamlStorage = new YamlStorage(configuration);
        storageRegistry = new StorageRegistryImpl(yamlStorage, configuration);
        storageRegistry.register(StorageRegistry.YAML, yamlStorage);
    }

    @Override
    public void onPluginEnable() {
        MessageSender.create(this, "§6[SB]");
        ILocalizer.create(this, "en_US").setLocale("en_US");

        schematics.register(SchematicCache.STORAGE, new SchematicBrushCache(this, configuration));
        storage = storageRegistry.activeStorage();

        var notifyListener = new NotifyListener(this, configuration);
        renderService = new RenderService(this, configuration);

        reload();

        MessageBlocker messageBlocker;
        if (!ServerVersion.between(ServerVersion.MC_1_19, ServerVersion.MC_1_20, ServerVersion.CURRENT_VERSION)) {
            messageBlocker = MessageBlockerAPI.builder(this).addWhitelisted("[SB]").build();
        } else {
            messageBlocker = MessageBlocker.dummy(this);
        }

        var brushCommand = new Brush(this, schematics, storage, settingsRegistry, messageBlocker);
        var presetCommand = new de.eldoria.schematicbrush.commands.Preset(this, storage, messageBlocker);
        var adminCommand = new Admin(this, schematics, storageRegistry);
        var settingsCommand = new Settings(this, configuration, renderService, notifyListener, messageBlocker);
        var brushPresetsCommand = new BrushPresets(this, storage, messageBlocker, settingsRegistry);
        var modifyCommand = new Modify(this, settingsRegistry);

        enableMetrics();

        if (configuration.main().generalConfig().isCheckUpdates()) {
            LynaUpdateChecker.lyna(LynaUpdateData.builder(this, 1).build()).start();
        }

        getServer().getScheduler().runTaskTimer(this, renderService, 0, 1);
        registerListener(new BrushModifier(), renderService, notifyListener);

        registerCommand(brushCommand);
        registerCommand(presetCommand);
        registerCommand(adminCommand);
        registerCommand(settingsCommand);
        registerCommand(brushPresetsCommand);
        registerCommand(modifyCommand);
    }

    /**
     * Get the module for the current platform. Either a {@link JacksonBukkit} or {@link JacksonPaper} module.
     *
     * @return new module
     */
    @Override
    public Module platformModule() {
        if (!getServer().getName().toLowerCase(Locale.ROOT).contains("spigot")) {
            return JacksonPaper.builder()
                    .colorAsHex()
                    .build();
        }
        return JacksonBukkit.builder()
                .colorAsHex()
                .build();
    }

    /**
     * Returns the module used by schematic brush to de/serialize objects related to SBR.
     * <p>
     * This module is a mutable instance and can be used to register own serializer and deserializer.
     *
     * @return module.
     */
    @Override
    public SimpleModule schematicBrushModule() {
        if (sbrModule == null) {
            sbrModule = new SimpleModule();
            sbrModule.addSerializer(Flip.class, new FlipSerializer());
            sbrModule.addSerializer(Rotation.class, new RotationSerializer());
            sbrModule.addDeserializer(Flip.class, new FlipDeserializer());
            sbrModule.addDeserializer(Rotation.class, new RotationDeserializer());
            sbrModule.addDeserializer(SchematicSetBuilder.class, new SchematicSetBuilderDeserializer());
            sbrModule.addDeserializer(BrushBuilderSnapshot.class, new BrushBuilderSnapshotDeserializer());
        }
        return sbrModule;
    }

    /**
     * Configure an {@link ObjectMapper} to work with schematic brush and bukkit objects.
     *
     * @param builder builder
     * @return same builder instance
     */
    @Override
    public ObjectMapper configureMapper(MapperBuilder<?, ?> builder) {
        builder.addModule(platformModule())
                .typeFactory(TypeFactory.defaultInstance().withClassLoader(getClassLoader()))
                .addModule(schematicBrushModule())
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        if (builder instanceof YAMLMapper.Builder b) {
            b.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        }
        return builder
                .build()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
    }

    @Override
    public void onPostStart() throws Throwable {
        if (configuration.general().isCheckUpdates() && UserData.get(this).isPremium()) {
            Updater.lyna(LynaUpdateData.builder(this, 1)
                            .updateUrl("https://discord.gg/zRW9Vpu")
                            .notifyPermission(Permissions.Admin.RELOAD)
                            .notifyUpdate(true)
                            .build())
                    .start();
        }
    }

    public void reload() {
        schematics.reload();
        configuration.reload();
        renderService.restart();
    }

    @Override
    public void onPluginDisable() {
        configuration.save();
        schematics.shutdown();
        storageRegistry.shutdown();
    }

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        return new EntryData[]{new EntryData("Rendering", renderService.renderInfo())};
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
                () -> String.valueOf(UserData.get(this).isPremium())));

        metrics.addCustomChart(new AdvancedPie("installed_storage_type",
                () -> storageRegistry.registry().keySet().stream().collect(Collectors.toMap(Nameable::name, e -> 1))));

        metrics.addCustomChart(new AdvancedPie("installed_add_ons",
                () -> Arrays.stream(getServer().getPluginManager().getPlugins())
                        .filter(plugin -> {
                            var descr = plugin.getDescription();
                            return descr.getSoftDepend().contains("SchematicBrushReborn") || descr.getDepend()
                                    .contains("SchematicBrushReborn");
                        }).collect(Collectors.toMap(e -> e.getDescription().getName(), e -> 1))));

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

    public RenderService renderService() {
        return renderService;
    }
}
