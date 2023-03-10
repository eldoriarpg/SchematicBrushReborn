/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.util.Location;

/**
 * Representing a block change collector which provides {@link Changes}.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BlockChangeCollector {
    void location(Location location);

    /**
     * The collected changes
     *
     * @return changes
     */
    Changes changes();
}
