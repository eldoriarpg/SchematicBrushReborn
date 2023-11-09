/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;

import java.util.function.Supplier;

/**
 * Represents a rotation.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface AdvancedRotation extends Shiftable<AdvancedRotation> {

    /**
     * Rotation represented as positive int value.
     *
     * @return rotation as positive integer
     */
    Supplier<Rotation> x();
    /**
     * Rotation represented as positive int value.
     *
     * @return rotation as positive integer
     */
    Supplier<Rotation> y();
    /**
     * Rotation represented as positive int value.
     *
     * @return rotation as positive integer
     */
    Supplier<Rotation> z();

    @Override
    default void value(AdvancedRotation value) {
    }

    @Override
    default AdvancedRotation value() {
        return this;
    }

    @Override
    default AdvancedRotation valueProvider() {
        return this;
    }
}
