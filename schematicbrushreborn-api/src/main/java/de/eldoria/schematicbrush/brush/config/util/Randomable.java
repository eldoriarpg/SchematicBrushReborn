/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
