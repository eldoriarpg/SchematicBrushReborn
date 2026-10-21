/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.Input;

/**
 * The arguments of a command as they were typed.
 * <p>
 * {@link Arguments} ends its arguments at the first word that looks like a flag, such as {@code -incair}, so an
 * expression containing options has to be read from the raw arguments.
 */
final class RawArguments {
    private RawArguments() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Reads the raw arguments, falling back to the parsed ones if they cannot be read.
     *
     * @param args arguments
     * @return arguments as typed
     */
    static String[] of(Arguments args) {
        // TODO replace with an accessor once eldo-util exposes the raw arguments
        try {
            var field = Arguments.class.getDeclaredField("rawArgs");
            field.setAccessible(true);
            return ((String[]) field.get(args)).clone();
        } catch (ReflectiveOperationException | RuntimeException e) {
            return args.args().stream().map(Input::asString).toArray(String[]::new);
        }
    }
}
