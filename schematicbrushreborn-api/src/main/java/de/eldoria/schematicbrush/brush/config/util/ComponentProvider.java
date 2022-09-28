/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

/**
 * Interface which provides components
 */
public interface ComponentProvider {
    /**
     * Name of component
     *
     * @return name as string
     */
    String name();

    /**
     * Locale key of the component
     *
     * @return name as string
     */
    default String localeKey() {
        return name();
    }

    /**
     * Descriptor of component. Should provide the value of the component.
     *
     * @return value as string
     */
    String descriptor();
}
