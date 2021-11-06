/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
import de.eldoria.eldoutilities.bstats.charts.SimplePie;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistryImpl;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
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
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.config.sections.GeneralConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicSourceImpl;
import de.eldoria.schematicbrush.config.sections.presets.PresetContainerImpl;
import de.eldoria.schematicbrush.config.sections.presets.PresetImpl;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistryImpl;
import de.eldoria.schematicbrush.listener.BrushModifier;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.schematics.SchematicBrushCache;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.SchematicRegistryImpl;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class SchematicBrushRebornImpl extends SchematicBrushReborn {

    private BrushSettingsRegistry settingsRegistry;
    private SchematicRegistry schematics;
    private ConfigurationImpl config;
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

        schematics = new SchematicRegistryImpl();

        settingsRegistry = new BrushSettingsRegistryImpl();
        registerDefaults();

        config = new ConfigurationImpl(this);

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
                () -> reduceMetricValue(schematics.schematicCount(), 1000, 50, 100, 250, 500, 1000)));
        metrics.addCustomChart(new SimplePie("directory_count",
                () -> reduceMetricValue(schematics.directoryCount(), 100, 10, 50, 100)));
        metrics.addCustomChart(new SimplePie("preset_count",
                () -> reduceMetricValue(config.presets().count(), 100, 10, 50, 100)));

        metrics.addCustomChart(new SimplePie("world_edit_version",
                () -> {
                    if (getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                        return "FAWE";
                    }
                    return "WorldEdit";
                }));
    }

    private String reduceMetricValue(int count, int baseValue, int... steps) {
        for (var step : steps) {
            if (count < step) {
                return "<" + count;
            }
        }
        var reduced = (int) Math.floor(count / (double) baseValue);
        return ">" + reduced * baseValue;
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(GeneralConfigImpl.class, PresetImpl.class,
                SchematicConfigImpl.class, SchematicSourceImpl.class, PresetContainerImpl.class, PresetRegistryImpl.class,
                SchematicSetBuilderImpl.class);
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
    public Configuration config() {
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
