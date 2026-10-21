/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

/**
 * Packs block and section coordinates into a single {@code long}.
 * <p>
 * x and z use 26 bits each, y uses 12 bits, which covers the world border and every supported build height. The same
 * layout is used for section keys, which hold the section coordinates ({@code coordinate >> 4}).
 */
public final class BlockKey {
    private BlockKey() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Packs coordinates into a key.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return packed key
     */
    public static long of(int x, int y, int z) {
        return ((long) x & 0x3FFFFFF) << 38 | ((long) z & 0x3FFFFFF) << 12 | (long) y & 0xFFF;
    }

    /**
     * Returns the key of the section that contains a block.
     *
     * @param blockKey packed block key
     * @return packed section key
     */
    public static long section(long blockKey) {
        return of(x(blockKey) >> 4, y(blockKey) >> 4, z(blockKey) >> 4);
    }

    /**
     * Returns the key of the section that contains a block.
     *
     * @param x block x coordinate
     * @param y block y coordinate
     * @param z block z coordinate
     * @return packed section key
     */
    public static long section(int x, int y, int z) {
        return of(x >> 4, y >> 4, z >> 4);
    }

    /**
     * Unpacks the x coordinate.
     *
     * @param key packed key
     * @return x coordinate
     */
    public static int x(long key) {
        return (int) (key >> 38);
    }

    /**
     * Unpacks the y coordinate.
     *
     * @param key packed key
     * @return y coordinate
     */
    public static int y(long key) {
        return (int) (key << 52 >> 52);
    }

    /**
     * Unpacks the z coordinate.
     *
     * @param key packed key
     * @return z coordinate
     */
    public static int z(long key) {
        return (int) (key << 26 >> 38);
    }
}
