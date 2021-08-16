package de.eldoria.schematicbrush.commands.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParsingUtilTest {
    @Test
    public void legacySelectorParserTest() {
        Assertions.assertEquals("$test", ParsingUtil.parseToLegacySelector("dir:test"));
        Assertions.assertEquals("$test*", ParsingUtil.parseToLegacySelector("dir:test*"));
        Assertions.assertEquals("^test.+?", ParsingUtil.parseToLegacySelector("regex:test.+?"));
        Assertions.assertEquals("&test", ParsingUtil.parseToLegacySelector("preset:test"));
        Assertions.assertEquals("test", ParsingUtil.parseToLegacySelector("test"));
    }

    @Test
    public void legacyModifierParserTest() {
        Assertions.assertEquals("@180", ParsingUtil.parseToLegacyModifier("-rotate:180"));
        Assertions.assertEquals("@*", ParsingUtil.parseToLegacyModifier("-rotate:random"));
        Assertions.assertEquals("!NS", ParsingUtil.parseToLegacyModifier("-flip:NS"));
        Assertions.assertEquals("!N", ParsingUtil.parseToLegacyModifier("-flip:N"));
        Assertions.assertEquals(":10", ParsingUtil.parseToLegacyModifier("-weight:10"));
    }

    @Test
    public void legaceSchematicSetParserTest() {
        Assertions.assertEquals("&test@180:10", ParsingUtil.parseToLegacySyntax(new String[] {"\"p:test", "-rotate:180", "-weight:10\""})[0]);
        Assertions.assertEquals("&test@180!NE", ParsingUtil.parseToLegacySyntax(new String[] {"\"p:test", "-rotate:180","-flip:NE\""})[0]);
    }
}
