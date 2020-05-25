package de.eldoria.schematicbrush.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides a standard random instance.
 */
public interface Randomable {
    public final Random RANDOM = ThreadLocalRandom.current();

    default int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
