/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchematicSourceImplTest {
    private static final Path ROOT = Path.of("/srv/schematics");

    private static SchematicSourceImpl excluding(String... excluded) {
        return new SchematicSourceImpl(ROOT.toString(), "srv", false, List.of(excluded));
    }

    @Test
    void exactEntryExcludesOnlyThatDirectory() {
        var source = excluding("old");

        assertTrue(source.isExcluded(ROOT.resolve("old")));
        assertTrue(source.isExcluded(ROOT.resolve("OLD")));
        assertFalse(source.isExcluded(ROOT.resolve("old/unused")));
    }

    @Test
    void wildcardIncludesTheDirectoryItself() {
        var source = excluding("old*");

        assertTrue(source.isExcluded(ROOT.resolve("old")));
        assertTrue(source.isExcluded(ROOT.resolve("old/unused")));
        assertFalse(source.isExcluded(ROOT.resolve("new")));
    }

    @Test
    void wildcardAfterSeparatorExcludesOnlySubdirectories() {
        var source = excluding("old/*");

        assertFalse(source.isExcluded(ROOT.resolve("old")));
        assertTrue(source.isExcluded(ROOT.resolve("old/unused")));
    }
}
