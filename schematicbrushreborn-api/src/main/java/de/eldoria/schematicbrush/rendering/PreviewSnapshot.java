/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The blocks of a brush preview, grouped by the 16x16x16 section they lie in.
 * <p>
 * A snapshot is immutable. Only blocks that differ from the world at capture time are contained.
 */
public final class PreviewSnapshot {
    private static final PreviewSnapshot EMPTY = new PreviewSnapshot(null, Map.of(), 0);

    @Nullable
    private final Location origin;
    private final Map<Long, Map<Long, BlockData>> sections;
    private final int size;

    private PreviewSnapshot(@Nullable Location origin, Map<Long, Map<Long, BlockData>> sections, int size) {
        this.origin = origin;
        this.sections = sections;
        this.size = size;
    }

    /**
     * A snapshot without blocks.
     *
     * @return the empty snapshot
     */
    public static PreviewSnapshot empty() {
        return EMPTY;
    }

    /**
     * Creates a builder for a snapshot.
     *
     * @param origin the location the preview is rendered at
     * @return new builder
     */
    public static Builder builder(Location origin) {
        return new Builder(origin);
    }

    /**
     * The location the preview is rendered at.
     *
     * @return the origin or null if the snapshot is empty
     */
    @Nullable
    public Location origin() {
        return origin;
    }

    /**
     * The id of the world the preview belongs to.
     *
     * @return the world id or null if the snapshot is empty
     */
    @Nullable
    public UUID world() {
        if (origin == null) return null;
        World world = origin.getWorld();
        return world == null ? null : world.getUID();
    }

    /**
     * The keys of all sections holding preview blocks.
     *
     * @return section keys as created by {@link BlockKey#section(long)}
     */
    public Set<Long> sections() {
        return sections.keySet();
    }

    /**
     * The preview blocks of a section.
     *
     * @param sectionKey section key
     * @return map of block key to block data, empty if the section holds no preview blocks
     */
    public Map<Long, BlockData> section(long sectionKey) {
        return sections.getOrDefault(sectionKey, Map.of());
    }

    /**
     * The amount of preview blocks.
     *
     * @return block count
     */
    public int size() {
        return size;
    }

    /**
     * Checks whether the snapshot holds no blocks.
     *
     * @return true if no blocks are contained
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Collects the blocks of a snapshot.
     */
    public static final class Builder {
        private final Location origin;
        private final Map<Long, Map<Long, BlockData>> sections = new HashMap<>();
        private int size;

        private Builder(Location origin) {
            this.origin = origin;
        }

        /**
         * Adds a preview block. A block added twice keeps the last data.
         *
         * @param x    x coordinate
         * @param y    y coordinate
         * @param z    z coordinate
         * @param data block data shown at this position
         * @return this builder
         */
        public Builder add(int x, int y, int z, BlockData data) {
            var section = sections.computeIfAbsent(BlockKey.section(x, y, z), key -> new HashMap<>());
            if (section.put(BlockKey.of(x, y, z), data) == null) size++;
            return this;
        }

        /**
         * Builds the immutable snapshot.
         *
         * @return the snapshot, or {@link #empty()} if no block was added
         */
        public PreviewSnapshot build() {
            if (size == 0) return EMPTY;
            var copy = new HashMap<Long, Map<Long, BlockData>>(sections.size());
            sections.forEach((key, blocks) -> copy.put(key, Map.copyOf(blocks)));
            return new PreviewSnapshot(origin.clone(), Map.copyOf(copy), size);
        }
    }
}
