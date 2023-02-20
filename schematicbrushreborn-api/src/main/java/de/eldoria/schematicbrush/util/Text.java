/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Text {
    public static String inlineEntries(Collection<?> entries, int entriesPerRow) {
        List<String> strings = entries.stream().map(String::valueOf).toList();
        int max = strings.stream().mapToInt(String::length).max().orElse(0);
        String format = "%%-%ss".formatted(max);
        List<String> result = new ArrayList<>();
        int curr = 0;
        while (curr < entries.size()) {
            result.add(strings.subList(curr, Math.min(curr + entriesPerRow, strings.size())).stream()
                    .map(format::formatted)
                    .collect(Collectors.joining(" ")));
            curr += entriesPerRow;
        }
        return String.join("\n", result);
    }
}
