/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.FlipSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.OffsetSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Placement;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.RotationSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {
    @Test
    void readsTheOriginalSyntax() throws ParseException {
        var brush = Parser.parse("tree*@90N:5^2#schematic rock@* house -incair -replaceall -place:DROP -yoff:3");

        var tree = brush.definitions().get(0);
        assertEquals("tree*", tree.selector().text());
        assertEquals(new RotationSpec.Degrees(List.of(90)), tree.rotation());
        assertEquals(new FlipSpec.Axes(List.of(Flip.NORTH_SOUTH)), tree.flip());
        assertEquals(5, tree.weight());
        assertEquals(new OffsetSpec.Fixed(2), tree.offset());
        assertEquals("schematic", tree.format());

        var rock = brush.definitions().get(1);
        assertEquals(new RotationSpec.Random(), rock.rotation());
        assertNull(rock.flip());

        var house = brush.definitions().get(2);
        assertNull(house.rotation());
        assertNull(house.weight());

        var options = brush.options();
        assertTrue(options.includeAir());
        assertTrue(options.replaceAll());
        assertEquals(Placement.DROP, options.placement());
        assertEquals(new OffsetSpec.Fixed(3), options.offset());
    }

    @Test
    void readsFlipWithoutRotationAndRandomBoth() throws ParseException {
        var brush = Parser.parse("a@W b@** c@*E");

        assertNull(brush.definitions().get(0).rotation());
        assertEquals(new FlipSpec.Axes(List.of(Flip.EAST_WEST)), brush.definitions().get(0).flip());
        assertEquals(new RotationSpec.Random(), brush.definitions().get(1).rotation());
        assertEquals(new FlipSpec.Random(), brush.definitions().get(1).flip());
        assertEquals(new FlipSpec.Axes(List.of(Flip.EAST_WEST)), brush.definitions().get(2).flip());
    }

    @Test
    void readsTheExtensions() throws ParseException {
        var brush = Parser.parse("^tree_[0-9]+@90,180,450NU^-2..5 rock^0,3,6 &g:forest -place:center -yoff:-1..1 -rot:* -flip:NE -filter:!air");

        var tree = brush.definitions().get(0);
        assertTrue(tree.regex());
        assertEquals("tree_[0-9]+", tree.selector().text());
        assertEquals(new RotationSpec.Degrees(List.of(90, 180)), tree.rotation());
        assertEquals(new FlipSpec.Axes(List.of(Flip.NORTH_SOUTH, Flip.UP_DOWN)), tree.flip());
        assertEquals(new OffsetSpec.Range(-2, 5), tree.offset());
        assertEquals(new OffsetSpec.Values(List.of(0, 3, 6)), brush.definitions().get(1).offset());
        assertEquals("g:forest", brush.presets().get(0).text());

        var options = brush.options();
        assertEquals(Placement.MIDDLE, options.placement());
        assertEquals(new OffsetSpec.Range(-1, 1), options.offset());
        assertEquals(new RotationSpec.Random(), options.rotation());
        assertEquals(new FlipSpec.Axes(List.of(Flip.NORTH_SOUTH, Flip.EAST_WEST)), options.flip());
        assertEquals("!air", options.filter().text());
        assertFalse(options.includeAir());
    }

    @Test
    void presetsAloneAreEnough() throws ParseException {
        assertEquals(1, Parser.parse("&forest").presets().size());
    }

    @Test
    void errorsPointAtTheFaultyPart() {
        assertError("stone@45", "commands.brush.parse.error.rotation", "45");
        assertError("stone@90X", "commands.brush.parse.error.flip", "X");
        assertError("stone:500", "commands.brush.parse.error.weight", "500");
        assertError("stone:heavy", "commands.brush.parse.error.number", "heavy");
        assertError("stone^200", "commands.brush.parse.error.offset", "200");
        assertError("stone@90@180", "commands.brush.parse.error.duplicate", "@");
        assertError("stone -place:sideways", "commands.brush.parse.error.placement", "sideways");
        assertError("stone -fly", "commands.brush.parse.error.option", "-fly");
        assertError("stone -incair:yes", "commands.brush.parse.error.noValue", "yes");
        assertError("stone -yoff", "commands.brush.parse.error.optionValue", "-yoff");
        assertError("stone -rot:45", "commands.brush.parse.error.rotation", "45");
        assertError("stone@90%", "commands.brush.parse.error.character", "%");
        assertError("& stone", "commands.brush.parse.error.emptyPreset", "&");
        assertError("-incair", "commands.brush.parse.error.empty", "-incair");
    }

    private static void assertError(String input, String message, String span) {
        var error = assertThrows(ParseException.class, () -> Parser.parse(input), input);
        assertEquals(message, error.getMessage(), input);
        assertEquals(span, input.substring(error.start(), Math.min(error.end(), input.length())), input);
    }
}
