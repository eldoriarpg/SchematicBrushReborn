/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.Map;

public class Bottom extends APlacement {
    public Bottom() {
    }

    public Bottom(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        return 0;
    }

    @Override
    public String name() {
        return "Bottom";
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Bottom();
    }
}
