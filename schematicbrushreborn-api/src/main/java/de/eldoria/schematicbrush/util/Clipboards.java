/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;

import java.util.Iterator;

/**
 * Utility for {@link Clipboard} interactions
 */
public final class Clipboards {
    private Clipboards() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static Iterator<BlockVector3> iterate(Clipboard clipboard) {
        return clipboard.getRegion().iterator();
    }
}
