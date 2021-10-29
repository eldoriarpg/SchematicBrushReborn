package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.config.sections.presets.Preset;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PresetContainer implements ConfigurationSerializable {
    private final Map<String, Preset> presets = new HashMap<>();

    public PresetContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Preset> presetList = map.getValue("presets");
        presetList.forEach(p -> presets.put(p.name(), p));
    }

    public PresetContainer() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("presets", new ArrayList<>(presets.values()))
                .build();
    }

    public boolean presetExists(String name) {
        return getPreset(name).isPresent();
    }

    public Optional<Preset> getPreset(String name) {
        return Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT)));
    }

    public void addPreset(Preset preset) {
        presets.put(preset.name(), preset);
    }

    public boolean remove(String name) {
        return presets.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    public Collection<Preset> getPresets() {
        return presets.values();
    }

    public Set<String> names() {
        return presets.keySet();
    }
}
