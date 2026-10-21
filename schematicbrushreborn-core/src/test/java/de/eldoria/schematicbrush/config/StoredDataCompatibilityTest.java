/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.config.sections.MainConfiguration;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.config.serialization.StorageMappers;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that files written by 2.7 are read completely and written back in the same format. The main configuration may
 * gain new keys, so for it every 2.7 value has to be preserved, except the keys it retired on purpose.
 * <p>
 * List, random and range mutators store their current value next to their configuration. That value is picked again
 * on load, so it is left out of the comparison.
 */
class StoredDataCompatibilityTest {
    private static final String FIXTURES = "/storage/2.7/";
    private static final List<String> VALUE_PICKING_SUFFIXES = List.of("List", "Random", "Range");
    private static final Set<String> PICKED_VALUES = Set.of("flip", "rotation", "offset");
    private static final Set<String> RETIRED = Set.of("/generalConfig/checkUpdates");

    private final ObjectMapper mapper = StorageMappers.configure(YAMLMapper.builder(), getClass().getClassLoader(),
            StorageMappers.platformModule(), StorageMappers.schematicBrushModule());

    @Test
    void readsAndRewritesBrushes() throws IOException {
        assertRoundTrip("brushes.yml", YamlBrushes.class);
    }

    @Test
    void readsAndRewritesPresets() throws IOException {
        assertRoundTrip("presets.yml", YamlPresets.class);
    }

    @Test
    void readsAndRewritesMainConfiguration() throws IOException {
        MainConfiguration loaded;
        try (var in = fixture("config.yml")) {
            loaded = mapper.readValue(in, MainConfiguration.class);
        }
        assertPreserved(fixtureTree("config.yml"), mapper.valueToTree(loaded), "");
    }

    @Test
    void writesBrushesInTheStoredFormat() throws IOException {
        assertEquals(comparable(fixtureTree("brushes.yml")), comparable(mapper.valueToTree(SampleStorage.brushes())));
    }

    @Test
    void writesPresetsInTheStoredFormat() throws IOException {
        assertEquals(comparable(fixtureTree("presets.yml")), comparable(mapper.valueToTree(SampleStorage.presets())));
    }

    @Test
    void writesMainConfigurationInTheStoredFormat() throws IOException {
        assertPreserved(fixtureTree("config.yml"), mapper.valueToTree(new MainConfiguration()), "");
    }

    private void assertRoundTrip(String file, Class<?> type) throws IOException {
        Object loaded;
        try (var in = fixture(file)) {
            loaded = mapper.readValue(in, type);
        }
        assertEquals(comparable(fixtureTree(file)), comparable(mapper.valueToTree(loaded)));
    }

    @Test
    void updateSwitchOfOldConfigsStartsTheNotice() throws IOException {
        var enabled = mapper.readValue("generalConfig:\n  checkUpdates: true\n", MainConfiguration.class);
        var disabled = mapper.readValue("generalConfig:\n  checkUpdates: false\n", MainConfiguration.class);

        assertTrue(enabled.updates().notice());
        assertFalse(disabled.updates().notice());
        assertFalse(disabled.updates().autoUpdate());
        assertTrue(disabled.updates().askForAutoUpdate());
    }

    @Test
    void storedUpdateSettingsWinOverTheOldSwitch() throws IOException {
        var loaded = mapper.readValue("generalConfig:\n  checkUpdates: false\nupdates:\n  notice: true\n  autoUpdate: true\n", MainConfiguration.class);

        assertTrue(loaded.updates().notice());
        assertTrue(loaded.updates().autoUpdate());
    }

    private static void assertPreserved(JsonNode expected, JsonNode actual, String path) {
        if (expected instanceof ObjectNode object) {
            for (var name : object.propertyNames()) {
                if (RETIRED.contains(path + "/" + name)) {
                    assertFalse(actual.has(name), "still written " + path + "/" + name);
                    continue;
                }
                assertTrue(actual.has(name), "missing " + path + "/" + name);
                assertPreserved(object.get(name), actual.get(name), path + "/" + name);
            }
            return;
        }
        assertEquals(expected, actual, path);
    }

    private JsonNode fixtureTree(String file) throws IOException {
        try (var in = fixture(file)) {
            return mapper.readTree(in);
        }
    }

    private InputStream fixture(String file) {
        return Objects.requireNonNull(getClass().getResourceAsStream(FIXTURES + file), file);
    }

    private static JsonNode comparable(JsonNode node) {
        var copy = node.deepCopy();
        removePickedValues(copy);
        return copy;
    }

    private static void removePickedValues(JsonNode node) {
        if (node instanceof ObjectNode object && object.has("@clazz")) {
            var clazz = object.get("@clazz").asText();
            if (VALUE_PICKING_SUFFIXES.stream().anyMatch(clazz::endsWith)) {
                object.remove(PICKED_VALUES);
            }
        }
        node.forEach(StoredDataCompatibilityTest::removePickedValues);
    }
}
