/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
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
     * Descriptor of component. Should provide the value of the component.
     *
     * @return value as string
     */
    String descriptor();
}
