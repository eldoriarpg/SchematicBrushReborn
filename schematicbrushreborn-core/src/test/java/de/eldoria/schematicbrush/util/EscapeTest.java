/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EscapeTest {
    private static final String HOSTILE = "a\\'><click:run_command:'/op me'><red>b</red>";
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Test
    void argumentKeepsTextInsideClickCommand() {
        var component = miniMessage.deserialize("<click:run_command:'/sbrbp info %s'>[Info]</click>".formatted(Escape.argument(HOSTILE)));

        var click = clickOf(component);
        assertEquals(ClickEvent.Action.RUN_COMMAND, click.action());
        assertEquals("/sbrbp info " + HOSTILE, ((ClickEvent.Payload.Text) click.payload()).value());
        assertEquals("[Info]", plain(component));
    }

    @Test
    void textIsShownAsWritten() {
        assertEquals(HOSTILE, plain(miniMessage.deserialize(Escape.text(HOSTILE))));
    }

    @Test
    void escapedTextStaysLiteralInsideHover() {
        var hover = "<gold>" + Escape.text(HOSTILE);
        var component = miniMessage.deserialize("<hover:show_text:'%s'>name</hover>".formatted(Escape.argument(hover)));

        var shown = (Component) hoverOf(component).value();
        assertEquals(HOSTILE, plain(shown));
    }

    private static ClickEvent clickOf(Component component) {
        if (component.clickEvent() != null) return component.clickEvent();
        return component.children().stream().map(EscapeTest::clickOf).filter(e -> e != null).findFirst().orElse(null);
    }

    private static HoverEvent<?> hoverOf(Component component) {
        if (component.hoverEvent() != null) return component.hoverEvent();
        return component.children().stream().map(EscapeTest::hoverOf).filter(e -> e != null).findFirst().orElse(null);
    }

    private static String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
