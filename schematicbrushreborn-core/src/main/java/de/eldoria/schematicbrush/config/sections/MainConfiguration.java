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
    private UpdateSettings updates;

    @JsonCreator
    public MainConfiguration(@JsonProperty("schematicConfig") SchematicConfigImpl schematicConfig,
                             @JsonProperty("generalConfig") GeneralConfigImpl generalConfig,
                             @JsonProperty("updates") UpdateSettings updates) {
        this.schematicConfig = schematicConfig;
        this.generalConfig = generalConfig;
        this.updates = updates != null ? updates
                : new UpdateSettings(generalConfig == null || generalConfig.checkUpdates());
    }

    public MainConfiguration() {
        schematicConfig = new SchematicConfigImpl();
        generalConfig = new GeneralConfigImpl();
        updates = new UpdateSettings();
    }

    public SchematicConfigImpl schematicConfig() {
        return schematicConfig;
    }

    public GeneralConfigImpl generalConfig() {
        return generalConfig;
    }

    /**
     * Update settings. Configs from before they existed start with notices as their update switch was set.
     *
     * @return update settings
     */
    public UpdateSettings updates() {
        return updates;
    }

    public void schematicConfig(SchematicConfigImpl schematicConfig) {
        this.schematicConfig = schematicConfig;
    }

    public void generalConfig(GeneralConfigImpl generalConfig) {
        this.generalConfig = generalConfig;
    }
}
