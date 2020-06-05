package de.eldoria.schematicbrush.commands.parser;

import org.junit.Assert;
import org.junit.Test;

public class ParsingUtilTest {
    @Test
    public void legacySelectorParserTest() {
        Assert.assertEquals("$test", ParsingUtil.parseToLegacySelector("dir:test"));
        Assert.assertEquals("$test*", ParsingUtil.parseToLegacySelector("dir:test*"));
        Assert.assertEquals("^test.+?", ParsingUtil.parseToLegacySelector("regex:test.+?"));
        Assert.assertEquals("&test", ParsingUtil.parseToLegacySelector("preset:test"));
        Assert.assertEquals("test", ParsingUtil.parseToLegacySelector("test"));
    }

    @Test
    public void legacyModifierParserTest() {
        Assert.assertEquals("@180", ParsingUtil.parseToLegacyModifier("-rotate:180"));
        Assert.assertEquals("@*", ParsingUtil.parseToLegacyModifier("-rotate:random"));
        Assert.assertEquals("!NS", ParsingUtil.parseToLegacyModifier("-flip:NS"));
        Assert.assertEquals("!N", ParsingUtil.parseToLegacyModifier("-flip:N"));
        Assert.assertEquals(":10", ParsingUtil.parseToLegacyModifier("-weight:10"));
    }

    @Test
    public void legaceSchematicSetParserTest() {
        Assert.assertEquals("&test@180:10", ParsingUtil.parseToLegacySyntax(new String[] {"\"p:test", "-rotate:180", "-weight:10\""})[0]);
        Assert.assertEquals("&test@180!NE", ParsingUtil.parseToLegacySyntax(new String[] {"\"p:test", "-rotate:180","-flip:NE\""})[0]);
    }
}
