/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.eldoria.eldoutilities.serialization.wrapper.MapEntry;
import de.eldoria.eldoutilities.serialization.wrapper.YamlContainer;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.storage.brush.Brush;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@SuppressWarnings("ALL")
@Disabled
public class TestBrush {
    private static ServerMock server;
    private static SchematicBrushRebornImpl load;

    @BeforeAll
    public static void load() {
        try {
            server = MockBukkit.mock();
            MockBukkit.createMockPlugin("WorldEdit");
            load = MockBukkit.load(SchematicBrushRebornImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void testSerialization() throws IOException, InvalidConfigurationException {
        var someone = server.addPlayer("Someone");
        var brushBuilder = new BrushBuilderImpl(someone, new RandomSelection(), load.brushSettingsRegistry(), load.schematics());
        var some = new Brush("some", brushBuilder);

        //This is only required for the sake of unit testing.
        ConfigurationSerialization.registerClass(MapEntry.class);

        String yamlBrush = YamlContainer.objectToYaml(some);

        System.out.printf(yamlBrush);
        Brush brush = YamlContainer.yamlToObject(yamlBrush, Brush.class);

        Assertions.assertEquals(some, brush);
    }
}
