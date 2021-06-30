package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schematic {
    /**
     * Regex wich matches the end of a filename.
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

    Schematic(ClipboardFormat format, File file) {
        this.format = format;
        this.file = file;
        this.name = file.toPath().getFileName().toString().replaceAll(EXTENSION, "");
    }

    /**
     * Matches a pattern agains the file name.
     *
     * @param pattern pattern to match
     * @return true if the pattern matches the file name with or without extension
     */
    public boolean isSchematic(Pattern pattern) {
        Matcher matcher = pattern.matcher(file.toPath().getFileName().toString());
        Matcher matcherExtension = pattern.matcher(name);

        return matcherExtension.find() || matcher.find();
    }

    /**
     * Get the path to the file.
     *
     * @return path of file as string
     */
    public String getPath() {
        return file.toPath().toString();
    }

    /**
     * Get the name of the file without file extension
     *
     * @return name of file
     */
    public String getName() {
        return name;
    }

    /**
     * Load the schematic from file.
     *
     * @return the schematic wrapped in a clipboard object
     * @throws IOException if the file could not be loaded. This should only happen, if the schematic was deletet or
     *                     moved.
     */
    public Clipboard getSchematic() throws IOException {
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schematic schematic = (Schematic) o;
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
