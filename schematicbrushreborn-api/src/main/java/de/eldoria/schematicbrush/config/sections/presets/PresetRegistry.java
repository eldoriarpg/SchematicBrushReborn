package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.config.PresetContainer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PresetRegistry implements ConfigurationSerializable {

    private Map<UUID, PresetContainer> playerPresets = new HashMap<>();
    private PresetContainer globalPresets = new PresetContainer();

    public PresetRegistry(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        playerPresets = map.getMap("playerPresets", (k, v) -> UUID.fromString(k));
        globalPresets = map.getValueOrDefault("globalPresets", new PresetContainer());
    }

    public PresetRegistry() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerPresets", playerPresets, (k, v) -> k.toString())
                .add("globalPresets", globalPresets)
                .build();
    }

    public boolean presetExists(Player player, String name) {
        return getPreset(player, name).isPresent();
    }

    private Optional<PresetContainer> getPlayerPresets(Player player) {
        return Optional.ofNullable(playerPresets.get(player.getUniqueId()));
    }

    private PresetContainer getOrCreatePlayerPresets(Player player) {
        return playerPresets.computeIfAbsent(player.getUniqueId(), k -> new PresetContainer());
    }

    public Optional<Preset> getPreset(Player player, String name) {
        if (name.startsWith("g:")) {
            return globalPresets.getPreset(name.substring(2));
        }
        return getPlayerPresets(player).flatMap(p -> p.getPreset(name));
    }

    public void addPreset(Player player, Preset preset) {
        getOrCreatePlayerPresets(player).addPreset(preset);
    }

    public void addPreset(Preset preset) {
        globalPresets.addPreset(preset);
    }

    public boolean removePreset(Player player, String name) {
        return getPlayerPresets(player).map(p -> p.remove(name)).orElse(false);
    }

    public boolean removePreset(String name) {
        return globalPresets.remove(name);
    }

    public Collection<Preset> getPresets(Player player) {
        return getPlayerPresets(player).map(PresetContainer::getPresets).orElse(Collections.emptyList());
    }

    public Collection<Preset> getPresets() {
        return globalPresets.getPresets();
    }

    public List<String> complete(Player player, String arg) {
        if (arg.startsWith("g:")) {
            return TabCompleteUtil.complete(arg.substring(2), globalPresets.names()).stream().map(m -> "g:" + m).collect(Collectors.toList());
        }
        var names = getPlayerPresets(player).map(PresetContainer::names).orElse(Collections.emptySet());
        return TabCompleteUtil.complete(arg, names);
    }
}
