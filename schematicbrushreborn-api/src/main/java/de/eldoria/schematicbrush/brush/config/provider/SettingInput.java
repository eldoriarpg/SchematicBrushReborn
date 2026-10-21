/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import java.util.List;

/**
 * An input the dialog editor shows for one argument of a {@link SettingProvider}.
 * <p>
 * The values entered are turned back into the argument tokens the provider parses, in the order the inputs are
 * declared. Labels and option names may be locale codes.
 */
public sealed interface SettingInput permits SettingInput.Choice, SettingInput.Toggle, SettingInput.Numeric,
        SettingInput.Text, SettingInput.Selection {

    /**
     * Label shown next to the input.
     *
     * @return text or locale code
     */
    String label();

    /**
     * Exactly one of the options. Produces the chosen option as one token.
     *
     * @param label   label
     * @param options options as they are parsed
     * @param initial option chosen at first
     * @return input
     */
    static Choice choice(String label, List<String> options, String initial) {
        return new Choice(label, options, initial);
    }

    /**
     * On or off. Produces {@code true} or {@code false} as one token.
     *
     * @param label   label
     * @param initial initial state
     * @return input
     */
    static Toggle toggle(String label, boolean initial) {
        return new Toggle(label, initial);
    }

    /**
     * A whole number within bounds. Produces the number as one token.
     *
     * @param label   label
     * @param min     lowest value
     * @param max     highest value
     * @param initial initial value
     * @return input
     */
    static Numeric numeric(String label, int min, int max, int initial) {
        return new Numeric(label, min, max, initial);
    }

    /**
     * Free text. Produces one token per word, none if left empty. Offers no suggestions unless
     * {@link Text#withSuggestions()} is called.
     *
     * @param label   label
     * @param initial initial text
     * @return input
     */
    static Text text(String label, String initial) {
        return new Text(label, initial, false);
    }

    /**
     * Any number of the options. Produces one token per selected option, in the order of the options.
     *
     * @param label   label
     * @param options options as they are parsed
     * @param initial options selected at first
     * @return input
     */
    static Selection selection(String label, List<String> options, List<String> initial) {
        return new Selection(label, options, initial);
    }

    /**
     * Exactly one of the options.
     *
     * @param label   label
     * @param options options as they are parsed
     * @param initial option chosen at first
     */
    record Choice(String label, List<String> options, String initial) implements SettingInput {
    }

    /**
     * On or off.
     *
     * @param label   label
     * @param initial initial state
     */
    record Toggle(String label, boolean initial) implements SettingInput {
    }

    /**
     * A whole number within bounds.
     *
     * @param label   label
     * @param min     lowest value
     * @param max     highest value
     * @param initial initial value
     */
    record Numeric(String label, int min, int max, int initial) implements SettingInput {
    }

    /**
     * Free text.
     *
     * @param label       label
     * @param initial     initial text
     * @param suggestions whether the editor offers the completions of the provider for this input
     */
    record Text(String label, String initial, boolean suggestions) implements SettingInput {
        /**
         * Offers the completions of the provider for this input. Use it where the completions are actual values,
         * not placeholders.
         *
         * @return input offering suggestions
         */
        public Text withSuggestions() {
            return new Text(label, initial, true);
        }
    }

    /**
     * Any number of the options.
     *
     * @param label   label
     * @param options options as they are parsed
     * @param initial options selected at first
     */
    record Selection(String label, List<String> options, List<String> initial) implements SettingInput {
    }
}
