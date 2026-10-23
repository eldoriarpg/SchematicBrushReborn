/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OffsetListTest {
    @Test
    void shiftWrapsAroundAfterTheLastValue() {
        var offsets = new OffsetList(List.of(1, 2, 3));
        offsets.value(3);

        offsets.shift();

        assertEquals(1, offsets.value());
    }

    @Test
    void shiftMovesToTheNextValue() {
        var offsets = new OffsetList(List.of(1, 2, 3));
        offsets.value(1);

        offsets.shift();

        assertEquals(2, offsets.value());
    }
}
