/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.util.Map;

public class Top extends APlacement {
    public Top() {
    }

    public Top(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        return clipboard.getDimensions().getY();
    }

    @Override
    public String name() {
        return "Top";
    }
}
