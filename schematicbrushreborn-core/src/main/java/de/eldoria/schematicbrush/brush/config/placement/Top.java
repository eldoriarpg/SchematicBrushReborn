/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

public class Top extends APlacement {
    public Top() {
    }
    @Override
    public int find(Clipboard clipboard) {
        return clipboard.getDimensions().y();
    }

    @Override
    public String name() {
        return "Top";
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Top();
    }
}
