/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyBrushMapperTest {
    private final LegacyBrushMapper mapper = new LegacyBrushMapper(null, null, null, "/");

    @Test
    void namesAndStarPatternsSelectByName() throws ParseException {
        assertEquals(new NameSelector("oak"), selector("oak"));
        assertEquals(new NameSelector("oak*"), selector("oak*"));
    }

    @Test
    void questionMarksBecomeARegex() throws ParseException {
        assertEquals(new NameSelector("^oak.\\.v.*"), selector("oak?.v*"));
    }

    @Test
    void caretMarksARegex() throws ParseException {
        assertEquals(new RegexSelector("^oak_[0-9]+"), selector("^oak_[0-9]+"));
    }

    @Test
    void pathsSelectWithinADirectory() throws ParseException {
        assertEquals(new DirectorySelector("nature/trees", "oak*"), selector("nature/trees/oak*"));
        assertEquals(new DirectorySelector("nature/trees", null), selector("nature/trees/"));
        assertEquals(new DirectorySelector("nature/trees*", null), selector("nature/trees/**"));
    }

    @Test
    void pathsUseTheConfiguredSeparator() throws ParseException {
        var dotted = new LegacyBrushMapper(null, null, null, ".");
        assertEquals(new DirectorySelector("nature.trees", "oak"), dotted.selector(Parser.parse("nature/trees/oak").definitions().get(0)));
    }

    private Selector selector(String input) throws ParseException {
        return mapper.selector(Parser.parse(input).definitions().get(0));
    }
}
