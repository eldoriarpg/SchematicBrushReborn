/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a named key.
 */
public class Nameable {
    private final String name;

    protected Nameable(String name) {
        this.name = name;
    }

    /**
     * Creates a new nameable with the name
     *
     * @param name name of nameable
     * @return new nameable instance
     */
    public static Nameable of(String name) {
        return new Nameable(name) {
        };
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
