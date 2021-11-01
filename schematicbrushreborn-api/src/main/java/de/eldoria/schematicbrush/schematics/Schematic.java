package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

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

    public Schematic(ClipboardFormat format, File file) {
        this.format = format;
        this.file = file;
        this.name = file.toPath().getFileName().toString().replaceAll(EXTENSION, "");
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

    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(format.getName(), file.getPath(), name);
    }
}
