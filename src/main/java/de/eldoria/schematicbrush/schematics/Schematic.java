package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schematic {
    ClipboardFormat format;
    private File file;

    Schematic(ClipboardFormat format, File file) {
        this.format = format;
        this.file = file;
    }

    public boolean isSchematic(Pattern pattern) {
        Matcher matcher = pattern.matcher(file.toPath().getFileName().toString());
        return matcher.find();
    }

    public Clipboard getSchematic() throws IOException {
        try (var reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        }
    }
}
