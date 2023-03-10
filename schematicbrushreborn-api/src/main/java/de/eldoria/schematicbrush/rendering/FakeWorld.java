/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

/**
 * A Fake world which wrapps and delegates to a world.
 */
public interface FakeWorld extends BlockChangeCollector {

    /**
     * Changes applied to the world.
     *
     * @return changes
     */
    Changes changes();
}
