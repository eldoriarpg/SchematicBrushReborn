/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

public class Original extends APlacement {
    public Original() {
    }
    @Override
    public int find(Clipboard clipboard) {
        return 0;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        // do nothing
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Original();
    }

    @Override
    public String name() {
        return "Original";
    }
}
