/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A loaded schematic which allows to load a schematic into a clipboard
 */
public class Schematic {
    /**
     * Regex which matches the end of a filename.
     */
    private static final String EXTENSION = "\\..+$";
    /**
     * Format of the schematic.
     */
    private final ClipboardFormat format;
    /**
     * Reference to file ot the schematic.
     */
    private final File file;
    private final String name;

    /**
     * Creates a new schematic from a file.
     *
     * @param format schematic format
     * @param file   file
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    private Schematic(ClipboardFormat format, File file) throws InvalidClipboardFormatException {
        if (format == null) {
            throw new InvalidClipboardFormatException("Could not determine schematic type of " + file.toPath());
        }
        this.format = format;
        this.file = file;
        name = file.toPath().getFileName().toString().replaceAll(EXTENSION, "");
    }

    /**
     * Create a schematic by file
     *
     * @param file file
     * @return schematic
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    public static Schematic of(File file) throws InvalidClipboardFormatException {
        return new Schematic(ClipboardFormats.findByFile(file), file);
    }

    /**
     * Create a schematic by path
     *
     * @param path path
     * @return schematic
     * @throws InvalidClipboardFormatException when the format could not be determined
     */
    public static Schematic of(Path path) throws InvalidClipboardFormatException {
        return new Schematic(ClipboardFormats.findByFile(path.toFile()), path.toFile());
    }

    /**
     * Matches a pattern against the file name.
     *
     * @param pattern pattern to match
     * @return true if the pattern matches the file name with or without extension
     */
    public boolean isSchematic(Pattern pattern) {
        var matcher = pattern.matcher(file.toPath().getFileName().toString());
        var matcherExtension = pattern.matcher(name);

        return matcherExtension.find() || matcher.find();
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
        try (var reader = format.getReader(new FileInputStream(file))) {
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
}
