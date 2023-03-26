/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MainConfiguration {
    private SchematicConfigImpl schematicConfig;
    private GeneralConfigImpl generalConfig;

    @JsonCreator
    public MainConfiguration(@JsonProperty("schematicConfig") SchematicConfigImpl schematicConfig,
                             @JsonProperty("generalConfig") GeneralConfigImpl generalConfig) {
        this.schematicConfig = schematicConfig;
        this.generalConfig = generalConfig;
    }

    public MainConfiguration() {
        schematicConfig = new SchematicConfigImpl();
        generalConfig = new GeneralConfigImpl();
    }

    public SchematicConfigImpl schematicConfig() {
        return schematicConfig;
    }

    public GeneralConfigImpl generalConfig() {
        return generalConfig;
    }

    public void schematicConfig(SchematicConfigImpl schematicConfig) {
        this.schematicConfig = schematicConfig;
    }

    public void generalConfig(GeneralConfigImpl generalConfig) {
        this.generalConfig = generalConfig;
    }
}
