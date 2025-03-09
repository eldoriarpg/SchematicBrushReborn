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
import de.eldoria.eldoutilities.config.template.PluginBaseConfiguration;
import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.debug.UserData;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.localization.Localizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.metrics.EldoMetrics;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateChecker;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.eldoutilities.utils.Version;
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
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistryImpl;
import de.eldoria.schematicbrush.storage.YamlStorage;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.util.InternalLogger;
import de.eldoria.schematicbrush.util.Permissions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SchematicBrushRebornImpl extends SchematicBrushReborn {

    private static final String[] LANGUAGES = new String[]{"de_DE", "en_US", "zh_TW"};
    private BrushSettingsRegistryImpl settingsRegistry;
    private SchematicRegistryImpl schematics;
    private JacksonConfiguration configuration;
    private RenderService renderService;
    private StorageRegistryImpl storageRegistry;
    private SimpleModule sbrModule;

    public SchematicBrushRebornImpl() {
        configuration = new JacksonConfiguration(this);
    }

    public SchematicBrushRebornImpl(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        configuration = new JacksonConfiguration(this);
    }

    @Override
    public Level getLogLevel() {
        return configuration.secondary(PluginBaseConfiguration.KEY).logLevel();
    }

    @Override
    public void onPluginLoad() throws Throwable {
        InternalLogger.init(this);
        getLogger().config("Test");

        settingsRegistry = new BrushSettingsRegistryImpl();
        schematics = new SchematicRegistryImpl();

        settingsRegistry.registerDefaults(schematics);

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

        if (base.version() == 1) {
            // Required changes were made to language formats. reset files.
            Path path = getDataFolder().toPath().resolve("messages");
            for (String language : LANGUAGES) {
                var file = path.resolve("messages_%s.properties".formatted(language));
                Files.copy(file, path.resolve("messages_%s_old.properties".formatted(language)), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(file);
            }
            base.version(2);
            configuration.save();
        }

        var yamlStorage = new YamlStorage(configuration);
        storageRegistry = new StorageRegistryImpl(yamlStorage, configuration);
        storageRegistry.register(StorageRegistry.YAML, yamlStorage);
    }

    @Override
    public void onPluginEnable() {
        var localizer = Localizer.builder(this, configuration.general().language())
                                 .setIncludedLocales(LANGUAGES)
                                 .build();
        MessageSender.builder(this)
                     .prefix("<gold>[SB]")
                     .messageColor(NamedTextColor.AQUA)
                     .addTag(tags -> tags
                             .tag("heading", Tag.styling(NamedTextColor.GOLD))
                             .tag("name", Tag.styling(NamedTextColor.DARK_AQUA))
                             .tag("value", Tag.styling(NamedTextColor.DARK_GREEN))
                             .tag("change", Tag.styling(NamedTextColor.YELLOW))
                             .tag("remove", Tag.styling(NamedTextColor.RED))
                             .tag("add", Tag.styling(NamedTextColor.GREEN))
                             .tag("warn", Tag.styling(NamedTextColor.RED))
                             .tag("neutral", Tag.styling(NamedTextColor.AQUA))
                             .tag("confirm", Tag.styling(NamedTextColor.GREEN))
                             .tag("delete", Tag.styling(NamedTextColor.RED))
                             .tag("inactive", Tag.styling(NamedTextColor.GRAY))
                     )
                     .localizer(localizer)
                     .register();

        schematics.register(SchematicCache.STORAGE, new SchematicBrushCache(this, configuration));

        var notifyListener = new NotifyListener(this, configuration);
        renderService = new RenderService(this, configuration);

        reload();

        MessageBlocker messageBlocker;
        if (ServerVersion.CURRENT_VERSION.isOlder(Version.of(1, 19))) {
            messageBlocker = MessageBlockerAPI.builder(this).addWhitelisted("[SB]").build();
        } else {
            messageBlocker = MessageBlocker.dummy(this);
        }

        var brushCommand = new Brush(this, schematics, storageRegistry, settingsRegistry, messageBlocker);
        var presetCommand = new de.eldoria.schematicbrush.commands.Preset(this, storageRegistry, messageBlocker);
        var adminCommand = new Admin(this, schematics, storageRegistry);
        var settingsCommand = new Settings(this, configuration, renderService, notifyListener, messageBlocker);
        var brushPresetsCommand = new BrushPresets(this, storageRegistry, messageBlocker, settingsRegistry);
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
        storageRegistry.reload();
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
        return new EntryData[]{new EntryData("Rendering", Optional.ofNullable(renderService).map(RenderService::renderInfo).orElse("Not running"))};
    }

    private void enableMetrics() {
        var metrics = new Metrics(this, 7683);
        if (EldoMetrics.isEnabled(this)) {
            logger().info("§2Metrics enabled. Thank you <3");
        }

        metrics.addCustomChart(new SimplePie("schematic_count",
                () -> reduceMetricValue(schematics.schematicCount(), 1000, 50, 100, 250, 500, 1000)));

        metrics.addCustomChart(new SimplePie("directory_count",
                () -> reduceMetricValue(schematics.directoryCount(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("preset_count",
                () -> reduceMetricValue(storageRegistry.activeStorage().presets().count().join(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("brush_count",
                () -> reduceMetricValue(storageRegistry.activeStorage().brushes().count().join(), 100, 10, 50, 100)));

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
