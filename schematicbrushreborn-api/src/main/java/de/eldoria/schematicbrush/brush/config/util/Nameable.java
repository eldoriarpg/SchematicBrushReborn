/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a named key.
 */
public class Nameable {
    private final String name;

    /**
     * Create a new nameable
     *
     * @param name name
     */
    @JsonCreator
    public Nameable(@JsonProperty("name") String name) {
        this.name = name;
    }

    /**
     * Creates a new nameable with the name
     *
     * @param name name of nameable
     * @return new nameable instance
     */
    public static Nameable of(String name) {
        return new Nameable(name);
    }

    /**
     * name of the nameable
     *
     * @return name
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nameable nameable)) return false;

        return name.equals(nameable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
