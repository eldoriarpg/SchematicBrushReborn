/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Placement;
import de.eldoria.schematicbrush.brush.provider.FilterProvider;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Completes the word of an expression the cursor is in.
 * <p>
 * Suggestions are whole words, so the typed start of the word is kept: schematic names and directories for a
 * selector, rotations after {@code @}, flips after a rotation, formats after {@code #}, option names after {@code -},
 * their values after {@code :}, presets after {@code &}. Numbers get a hint.
 */
public final class LegacyCompletion {
    private static final int LIMIT = 50;
    private static final List<String> OPTIONS = List.of("-incair", "-replaceall", "-place:", "-yoff:", "-rot:", "-flip:", "-filter:");
    private static final List<String> ROTATIONS = List.of("0", "90", "180", "270", "*");
    private static final List<String> FLIPS = List.of("N", "E", "U", "*");
    private static final List<String> FORMATS = List.of("schem", "schematic");

    private final Plugin plugin;
    private final SchematicRegistry schematics;
    private final StorageRegistry storage;
    private final String pathSeparator;

    public LegacyCompletion(Plugin plugin, SchematicRegistry schematics, StorageRegistry storage, String pathSeparator) {
        this.plugin = plugin;
        this.schematics = schematics;
        this.storage = storage;
        this.pathSeparator = pathSeparator;
    }

    /**
     * Completes a word.
     *
     * @param player player completing
     * @param word   the word up to the cursor
     * @return suggestions replacing the word
     */
    public List<String> complete(Player player, String word) {
        if (word.startsWith("&")) return prefixed("&", storage.activeStorage().presets().complete(player, word.substring(1)));
        if (word.startsWith("-") && (word.length() == 1 || Character.isLetter(word.charAt(1)))) return option(player, word);
        var partStart = partStart(word);
        if (partStart < 0) return selector(player, word);
        var head = word.substring(0, partStart + 1);
        var rest = word.substring(partStart + 1);
        return switch (word.charAt(partStart)) {
            case '@' -> orientation(head, rest);
            case '#' -> prefixed(head, startingWith(FORMATS, rest));
            case ':' -> List.of(head + "<1-100>");
            case '^' -> List.of(head + "<offset>");
            default -> List.of();
        };
    }

    private List<String> option(Player player, String word) {
        var colon = word.indexOf(':');
        if (colon < 0) return startingWith(OPTIONS, word);
        var head = word.substring(0, colon + 1);
        var value = word.substring(colon + 1);
        return switch (word.substring(1, colon).toLowerCase(Locale.ROOT)) {
            case "place" -> prefixed(head, startingWith(Arrays.stream(Placement.values()).flatMap(placement -> placement.names().stream()).toList(), value));
            case "rot" -> prefixed(head, rotations(value));
            case "flip" -> prefixed(head, startingWith(FLIPS, value));
            case "yoff" -> List.of(head + "<offset>");
            case "filter" -> prefixed(head, mask(player, value));
            default -> List.of();
        };
    }

    private List<String> orientation(String head, String rest) {
        var flipStart = rest.startsWith("*") ? 1 : degreesLength(rest);
        var rotation = rest.substring(0, flipStart);
        if (flipStart < rest.length()) return List.of(head + rest);
        if (rotation.isEmpty()) return prefixed(head, Stream.concat(ROTATIONS.stream(), FLIPS.stream()).distinct().toList());
        var last = rotation.substring(rotation.lastIndexOf(',') + 1);
        if (!"*".equals(rotation) && !ROTATIONS.subList(0, 4).contains(last)) return prefixed(head, rotations(rotation));
        var next = "*".equals(rotation) ? FLIPS.stream() : Stream.concat(FLIPS.stream(), Stream.of(","));
        return Stream.concat(Stream.of(head + rest), next.map(value -> head + rest + value)).toList();
    }

    private static int degreesLength(String value) {
        var length = 0;
        while (length < value.length() && (Character.isDigit(value.charAt(length)) || value.charAt(length) == ',')) length++;
        return length;
    }

    private static List<String> rotations(String value) {
        var comma = value.lastIndexOf(',');
        var head = value.substring(0, comma + 1);
        return prefixed(head, startingWith(comma < 0 ? ROTATIONS : ROTATIONS.subList(0, 4), value.substring(comma + 1)));
    }

    private List<String> selector(Player player, String word) {
        if (word.startsWith("^")) return List.of(word);
        try {
            var cache = schematics.get(SchematicCache.STORAGE);
            var slash = word.lastIndexOf('/');
            var directories = cache.getMatchingDirectories(player, word.replace("/", pathSeparator), LIMIT).stream()
                                   .map(directory -> directory.replace(pathSeparator, "/") + "/");
            if (slash < 0) {
                return Stream.concat(cache.getMatchingSchematics(player, word, LIMIT).stream(), directories).distinct().toList();
            }
            var directory = word.substring(0, slash + 1);
            var inDirectory = cache.getSchematicsByDirectory(player, word.substring(0, slash).replace("/", pathSeparator), null).stream()
                                   .map(schematic -> directory + schematic.name())
                                   .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(word.toLowerCase(Locale.ROOT)));
            return Stream.of(Stream.of(directory + "**"), inDirectory, directories).flatMap(stream -> stream)
                         .filter(name -> name.startsWith(word)).distinct().limit(LIMIT).toList();
        } catch (RuntimeException e) {
            return List.of();
        }
    }

    private List<String> mask(Player player, String value) {
        try {
            return FilterProvider.BLOCK_FILTER.complete(Arguments.create(plugin, player, new String[]{value}), player);
        } catch (RuntimeException e) {
            return List.of();
        }
    }

    private static int partStart(String word) {
        for (var index = word.length() - 1; index > 0; index--) {
            if ("@:^#".indexOf(word.charAt(index)) >= 0) return index;
        }
        return -1;
    }

    private static List<String> startingWith(List<String> values, String start) {
        var lower = start.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }

    private static List<String> prefixed(String head, List<String> values) {
        var result = new ArrayList<String>(values.size());
        for (var value : values) result.add(head + value);
        return result;
    }
}
