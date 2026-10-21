/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.updates;

import de.eldoria.eldoutilities.updater.AutoUpdateDecision;
import de.eldoria.schematicbrush.config.sections.UpdateSettings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdatesTest {
    private final UpdateSettings settings = new UpdateSettings();
    private final AtomicInteger saves = new AtomicInteger();
    private final Updates updates = new Updates(null, () -> settings, saves::incrementAndGet, true);

    @Test
    void enablingAutoUpdateIsSaved() {
        updates.decided(AutoUpdateDecision.ENABLED);

        assertTrue(settings.autoUpdate());
        assertTrue(settings.askForAutoUpdate());
        assertEquals(1, saves.get());
    }

    @Test
    void neverAskingIsSaved() {
        updates.decided(AutoUpdateDecision.NEVER_ASK);

        assertFalse(settings.askForAutoUpdate());
        assertFalse(settings.autoUpdate());
        assertEquals(1, saves.get());
    }
}
