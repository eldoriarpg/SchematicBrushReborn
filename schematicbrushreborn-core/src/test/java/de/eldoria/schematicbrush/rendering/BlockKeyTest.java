/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BlockKeyTest {
    @Test
    void unpacksWhatWasPacked() {
        int[][] positions = {{0, 0, 0}, {-1, -64, -1}, {29_999_999, 319, -29_999_999}, {-29_999_999, -2048, 29_999_999}, {17, 2047, -33}};
        for (var position : positions) {
            var key = BlockKey.of(position[0], position[1], position[2]);
            assertEquals(position[0], BlockKey.x(key));
            assertEquals(position[1], BlockKey.y(key));
            assertEquals(position[2], BlockKey.z(key));
        }
    }

    @Test
    void sectionUsesFloorDivisionBySixteen() {
        var section = BlockKey.section(BlockKey.of(-1, -1, 16));
        assertEquals(-1, BlockKey.x(section));
        assertEquals(-1, BlockKey.y(section));
        assertEquals(1, BlockKey.z(section));
        assertEquals(section, BlockKey.section(-1, -1, 16));
    }

    @Test
    void neighbouringBlocksHaveDifferentKeys() {
        assertNotEquals(BlockKey.of(0, 0, 0), BlockKey.of(0, 1, 0));
        assertNotEquals(BlockKey.of(0, 0, 0), BlockKey.of(1, 0, 0));
        assertNotEquals(BlockKey.of(0, 0, 0), BlockKey.of(0, 0, 1));
    }
}
