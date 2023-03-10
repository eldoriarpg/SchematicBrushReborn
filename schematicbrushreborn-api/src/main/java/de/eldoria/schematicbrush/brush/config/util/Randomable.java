/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides a standard random instance.
 */
public interface Randomable {
    /**
     * Get a random integer based on {@link ThreadLocalRandom}
     *
     * @param bound upper exclusive bound
     * @return number in bounds
     */
    default int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    /**
     * Get the random instance of the calling thread
     *
     * @return random instance of the calling thread
     */
    default Random random() {
        return ThreadLocalRandom.current();
    }
}
