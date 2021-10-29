package de.eldoria.schematicbrush.brush.config.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides a standard random instance.
 */
public interface Randomable {
    Random RANDOM = ThreadLocalRandom.current();

    default int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    default Random random() {
        return ThreadLocalRandom.current();
    }
}
