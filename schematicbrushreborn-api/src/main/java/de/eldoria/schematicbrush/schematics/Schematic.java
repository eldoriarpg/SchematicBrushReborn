/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BaseBlock;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.util.Clipboards;
import de.eldoria.schematicbrush.util.FAWE;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * A loaded schematic which allows to load a schematic into a clipboard
 */
public class Schematic implements Comparable<Schematic> {
    private static final Pattern numEnd = Pattern.compile("(?<name>.+?)(?<num>\\d+?)$");
    private static final Set<BaseBlock> SIZE_EXCLUSION = Set.of(BukkitAdapter.adapt(Material.AIR.createBlockData()).toBaseBlock());
    /**
     * Regex which matches the end of a filename.
     */
    private static final String EXTENSION = "\\..+$";
    private final Map<Material, Integer> materialMap = new EnumMap<>(Material.class);
    /**
     * Format of the schematic.
     */
    private final ClipboardFormat format;
    /**
     * Reference to file ot the schematic.
     */
    private final File file;
    private final String name;
    private final String effectiveName;
    private final long number;
    private int effectiveSize = -1;
    private int size = -1;

    /**
     * Creates a new schematic from a file.
     *
     * @param format schematic format
     * @param file   file
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    private Schematic(ClipboardFormat format, File file, String name) throws InvalidClipboardFormatException {
        this.format = format;
        this.file = file;
        this.name = name;
        var matcher = numEnd.matcher(name);
        if (matcher.matches()) {
            effectiveName = matcher.group("name");
            var number = 0L;
            try {
                number = Long.parseLong(matcher.group("num"));
            } catch (ArithmeticException | NumberFormatException e) {
                SchematicBrushReborn.logger().info("Could not read number of schematic " + file.getPath() + " at ");
            }
            this.number = number;
        } else {
            effectiveName = name;
            number = 0;
        }
    }

    private static Schematic create(ClipboardFormat format, File file) {
        if (format == null) {
            throw new InvalidClipboardFormatException("Could not determine schematic type of " + file.toPath());
        }
        var name = file.toPath().getFileName().toString().replaceAll(EXTENSION, "");
        return new Schematic(format, file, name);
    }

    /**
     * Create a schematic by file
     *
     * @param file file
     * @return schematic
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    public static Schematic of(File file) throws InvalidClipboardFormatException {
        return create(ClipboardFormats.findByFile(file), file);
    }

    /**
     * Create a schematic by path
     *
     * @param path path
     * @return schematic
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    public static Schematic of(Path path) throws InvalidClipboardFormatException {
        return create(ClipboardFormats.findByFile(path.toFile()), path.toFile());
    }

    /**
     * Matches a pattern against the file name.
     *
     * @param pattern pattern to match
     * @return true if the pattern matches the file name with or without extension
     */
    public boolean isSchematic(Pattern pattern) {
        var matcherExtension = pattern.matcher(name);
        return matcherExtension.matches();
    }

    /**
     * Get the path to the file.
     *
     * @return path of file as string
     */
    public String path() {
        return file.toPath().toString();
    }

    /**
     * Get the name of the file without file extension
     *
     * @return name of file
     */
    public String name() {
        return name;
    }

    /**
     * Load the schematic from file.
     *
     * @return the schematic wrapped in a clipboard object
     * @throws IOException if the file could not be loaded. This should only happen, if the schematic was deleted or
     *                     moved.
     */
    @SuppressWarnings("OverlyBroadThrowsClause")
    public Clipboard loadSchematic() throws IOException {
        try (var in = new FileInputStream(file); var reader = format.getReader(in)) {
            return reader.read();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var schematic = (Schematic) o;
        return format.getName().equals(schematic.format.getName()) &&
               file.getPath().equals(schematic.file.getPath()) &&
               name.equals(schematic.name);
    }

    /**
     * Schematic file
     *
     * @return file
     */
    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(format.getName(), file.getPath(), name);
    }

    /**
     * The effective size of the schematic without air.
     *
     * @return effective schematic size
     */
    public int effectiveSize() {
        if (effectiveSize != -1) {
            return effectiveSize;
        }
        effectiveSize = calcEffectiveSize();
        return effectiveSize;
    }

    private int calcEffectiveSize() {
        if (FAWE.isFawe()) {
            try (var clipboard = loadSchematic()) {
                var max = clipboard.getMaximumPoint();
                var min = clipboard.getMinimumPoint();
                var region = new CuboidRegion(min, max);
                var air = clipboard.countBlocks(region, SIZE_EXCLUSION);
                return region.size() - air;
            } catch (IOException e) {
                return -1;
            }
        }
        try {
            var clipboard = loadSchematic();
            var count = new AtomicInteger();
            Clipboards.iterate(clipboard).forEachRemaining(pos -> {
                if (SIZE_EXCLUSION.contains(clipboard.getBlock(pos).toBaseBlock())) {
                    count.incrementAndGet();
                }
            });
            return size() - count.get();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * The total block size of the schematic. Including air blocks.
     *
     * @return block count of schematics
     */
    public int size() {
        if (size != -1) {
            return size;
        }
        size = calcSize();
        return size;
    }

    private int calcSize() {
        if (FAWE.isFawe()) {
            try (var clipboard = loadSchematic()) {
                var max = clipboard.getMaximumPoint();
                var min = clipboard.getMinimumPoint();
                return new CuboidRegion(min, max).size();
            } catch (IOException e) {
                return -1;
            }
        }
        try {
            var clipboard = loadSchematic();
            var min = clipboard.getMinimumPoint();
            var max = clipboard.getMaximumPoint();
            return EMath.diff(min.getBlockX(), max.getBlockX() + 1)
                   * EMath.diff(min.getBlockY(), max.getBlockY() + 1)
                   * EMath.diff(min.getBlockZ(), max.getBlockZ() + 1);
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Calculates and returns a map which contains the block count of every material in this schematic.
     *
     * @return map with block counts
     */
    public Map<Material, Integer> blockCount() {
        if (!materialMap.isEmpty()) {
            return Collections.unmodifiableMap(materialMap);
        }

        if (FAWE.isFawe()) {
            try (var clipboard = loadSchematic()) {
                for (var type : clipboard.getBlockDistribution(clipboard.getRegion())) {
                    materialMap.put(BukkitAdapter.adapt(type.getID()), type.getAmount());
                }
            } catch (IOException e) {
                return Collections.emptyMap();
            }
        } else {
            try {
                var clipboard = loadSchematic();
                clipboard.getRegion().iterator()
                         .forEachRemaining(pos -> {
                             var mat = BukkitAdapter.adapt(clipboard.getBlock(pos)).getMaterial();
                             materialMap.compute(mat, (key, v) -> v == null ? 1 : v + 1);
                         });
            } catch (IOException e) {
                return Collections.emptyMap();
            }
        }

        return Collections.unmodifiableMap(materialMap);
    }

    @Override
    public int compareTo(@NotNull Schematic other) {
        var name = effectiveName.compareTo(other.effectiveName);
        if (name == 0) {
            return Long.compare(number, other.number);
        }
        return name;
    }

    public ClipboardFormat format() {
        return format;
    }

    @Override
    public String toString() {
        return "Schematic{" +
                "file=" + file +
                ", name='" + name + '\'' +
                '}';
    }
}
