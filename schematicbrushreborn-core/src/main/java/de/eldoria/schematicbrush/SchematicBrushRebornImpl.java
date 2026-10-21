/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.config.template.PluginBaseConfiguration;
import de.eldoria.eldoutilities.debug.UserData;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.localization.Localizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.metrics.EldoMetrics;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateChecker;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.jacksonbukkit.JacksonPaper;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistryImpl;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.commands.Admin;
import de.eldoria.schematicbrush.commands.Brush;
import de.eldoria.schematicbrush.commands.BrushPresets;
import de.eldoria.schematicbrush.commands.Modify;
import de.eldoria.schematicbrush.commands.Settings;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.JacksonConfiguration;
import de.eldoria.schematicbrush.config.serialization.StorageMappers;
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
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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
            if (isPreJacksonInstall()) {
                throw new IllegalStateException("The configuration was written by a version older than 2.5.0. Update to the latest 2.7 release first, start the server once, then update to this version.");
            }
            base.version(2);
            base.lastInstalledVersion(this);
            configuration.save();
        }

        if (base.version() == 1) {
            // Required changes were made to language formats. reset files.
            Path path = getDataFolder().toPath().resolve("messages");
            for (String language : LANGUAGES) {
                var file = path.resolve("messages_%s.properties".formatted(language));
                if (path.toFile().exists()) {
                    Files.copy(file, path.resolve("messages_%s_old.properties".formatted(language)), StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(file);
                }
            }
            base.version(2);
            configuration.save();
        }

        var yamlStorage = new YamlStorage(configuration);
        storageRegistry = new StorageRegistryImpl(yamlStorage, configuration);
        storageRegistry.register(StorageRegistry.YAML, yamlStorage);
    }

    /**
     * Checks whether the data folder still holds a configuration in the Bukkit serialization format used before 2.5.0.
     *
     * @return true if the main configuration contains Bukkit serialization markers
     * @throws IOException if the configuration file can not be read
     */
    private boolean isPreJacksonInstall() throws IOException {
        var config = getDataFolder().toPath().resolve("config.yml");
        return Files.exists(config) && Files.readString(config).contains("==:");
    }

    @Override
    public void onPluginEnable(boolean reload) {
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

        if (reload) reload();

        var brushCommand = new Brush(this, schematics, storageRegistry, settingsRegistry);
        var presetCommand = new de.eldoria.schematicbrush.commands.Preset(this, storageRegistry);
        var adminCommand = new Admin(this, schematics, storageRegistry);
        var settingsCommand = new Settings(this, configuration, renderService, notifyListener);
        var brushPresetsCommand = new BrushPresets(this, storageRegistry, settingsRegistry);
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
     * Get the {@link JacksonPaper} module for the platform.
     *
     * @return new module
     */
    @Override
    public JacksonModule platformModule() {
        return StorageMappers.platformModule();
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
            sbrModule = StorageMappers.schematicBrushModule();
        }
        return sbrModule;
    }

    /**
     * Configure an {@link ObjectMapper} to work with schematic brush and bukkit objects.
     *
     * @param builder builder
     * @return the mapper built from the configured builder
     */
    @Override
    public ObjectMapper configureMapper(MapperBuilder<?, ?> builder) {
        return StorageMappers.configure(builder, getClassLoader(), platformModule(), schematicBrushModule());
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
        if (renderService != null) renderService.shutdown();
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
