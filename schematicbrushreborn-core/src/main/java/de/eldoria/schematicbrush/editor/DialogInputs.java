/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Turns the {@link SettingInput}s of a provider into dialog inputs, and the response of the dialog back into the
 * argument tokens the provider parses.
 * <p>
 * Inputs are keyed by their position, so the keys stay valid whatever the provider calls its arguments.
 */
final class DialogInputs {
    static final int WIDTH = 300;
    private static final int MAX_TEXT = 256;

    private DialogInputs() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Builds the dialog inputs.
     *
     * @param inputs inputs of the provider
     * @param text   renders a label or option name
     * @return dialog inputs in the same order
     */
    static List<DialogInput> create(List<SettingInput> inputs, Function<String, Component> text) {
        var result = new ArrayList<DialogInput>();
        for (var index = 0; index < inputs.size(); index++) {
            var key = key(index);
            switch (inputs.get(index)) {
                case SettingInput.Choice choice -> result.add(DialogInput.singleOption(key, text.apply(choice.label()),
                                                                 choice.options().stream()
                                                                       .map(option -> SingleOptionDialogInput.OptionEntry.create(option, text.apply(option), option.equals(choice.initial())))
                                                                       .toList())
                                                         .width(WIDTH)
                                                         .build());
                case SettingInput.Toggle toggle -> result.add(DialogInput.bool(key, text.apply(toggle.label()))
                                                                         .initial(toggle.initial())
                                                                         .build());
                case SettingInput.Numeric numeric -> result.add(DialogInput.numberRange(key, text.apply(numeric.label()), numeric.min(), numeric.max())
                                                                           .width(WIDTH)
                                                                           .step(1f)
                                                                           .initial((float) Math.clamp(numeric.initial(), numeric.min(), numeric.max()))
                                                                           .build());
                case SettingInput.Text input -> result.add(DialogInput.text(key, text.apply(input.label()))
                                                                      .width(WIDTH)
                                                                      .maxLength(MAX_TEXT)
                                                                      .initial(input.initial().length() > MAX_TEXT ? input.initial().substring(0, MAX_TEXT) : input.initial())
                                                                      .build());
                case SettingInput.Selection selection -> {
                    for (var option = 0; option < selection.options().size(); option++) {
                        var name = selection.options().get(option);
                        var label = option == 0 ? text.apply(selection.label()).appendSpace().append(text.apply(name)) : text.apply(name);
                        result.add(DialogInput.bool(key + "_" + option, label)
                                              .initial(selection.initial().contains(name))
                                              .build());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Reads the entered values as argument tokens.
     *
     * @param inputs   inputs of the provider, as passed to {@link #create(List, Function)}
     * @param response response of the dialog
     * @return tokens in argument order
     */
    static List<String> tokens(List<SettingInput> inputs, DialogResponseView response) {
        return tokens(entered(inputs, response));
    }

    /**
     * Turns the initial values of the inputs into argument tokens.
     *
     * @param inputs inputs
     * @return tokens in argument order
     */
    static List<String> tokens(List<SettingInput> inputs) {
        var tokens = new ArrayList<String>();
        for (var input : inputs) {
            switch (input) {
                case SettingInput.Choice choice -> tokens.add(choice.initial());
                case SettingInput.Toggle toggle -> tokens.add(String.valueOf(toggle.initial()));
                case SettingInput.Numeric numeric -> tokens.add(String.valueOf(numeric.initial()));
                case SettingInput.Text text -> tokens.addAll(words(text.initial()));
                case SettingInput.Selection selection -> selection.options().stream()
                                                                  .filter(selection.initial()::contains)
                                                                  .forEach(tokens::add);
            }
        }
        return tokens;
    }

    /**
     * Copies the inputs with the values entered in the dialog as their initial values, so the dialog can be shown
     * again as the player left it.
     *
     * @param inputs   inputs of the provider, as passed to {@link #create(List, Function)}
     * @param response response of the dialog
     * @return inputs holding the entered values
     */
    static List<SettingInput> entered(List<SettingInput> inputs, DialogResponseView response) {
        var entered = new ArrayList<SettingInput>();
        for (var index = 0; index < inputs.size(); index++) {
            var key = key(index);
            entered.add(switch (inputs.get(index)) {
                case SettingInput.Choice choice -> new SettingInput.Choice(choice.label(), choice.options(),
                        Objects.requireNonNullElse(response.getText(key), choice.initial()));
                case SettingInput.Toggle toggle -> new SettingInput.Toggle(toggle.label(),
                        Objects.requireNonNullElse(response.getBoolean(key), toggle.initial()));
                case SettingInput.Numeric numeric -> {
                    var value = response.getFloat(key);
                    yield new SettingInput.Numeric(numeric.label(), numeric.min(), numeric.max(),
                            value == null ? numeric.initial() : Math.round(value));
                }
                case SettingInput.Text text -> new SettingInput.Text(text.label(),
                        Objects.requireNonNullElse(response.getText(key), text.initial()), text.suggestions());
                case SettingInput.Selection selection -> {
                    var selected = new ArrayList<String>();
                    for (var option = 0; option < selection.options().size(); option++) {
                        if (Boolean.TRUE.equals(response.getBoolean(key + "_" + option))) {
                            selected.add(selection.options().get(option));
                        }
                    }
                    yield new SettingInput.Selection(selection.label(), selection.options(), selected);
                }
            });
        }
        return entered;
    }

    /**
     * The arguments to complete the last word of a text input: the tokens of the inputs before it, followed by the
     * words of the text input, ending in an empty word if the text is empty or ends with a space.
     *
     * @param inputs inputs holding the entered values
     * @param index  index of the text input
     * @return tokens to complete
     */
    static List<String> completionTokens(List<SettingInput> inputs, int index) {
        var text = ((SettingInput.Text) inputs.get(index)).initial();
        var tokens = new ArrayList<>(tokens(inputs.subList(0, index)));
        tokens.addAll(words(text));
        if (text.isBlank() || Character.isWhitespace(text.charAt(text.length() - 1))) tokens.add("");
        return tokens;
    }

    /**
     * Replaces the last word of a text input with a suggestion.
     *
     * @param inputs     inputs holding the entered values
     * @param index      index of the text input
     * @param suggestion the chosen suggestion
     * @return inputs with the suggestion in place
     */
    static List<SettingInput> withSuggestion(List<SettingInput> inputs, int index, String suggestion) {
        var text = (SettingInput.Text) inputs.get(index);
        var current = text.initial();
        var lastWord = current.isBlank() || Character.isWhitespace(current.charAt(current.length() - 1))
                ? current.length()
                : current.stripTrailing().lastIndexOf(' ') + 1;
        var result = new ArrayList<>(inputs);
        result.set(index, new SettingInput.Text(text.label(), current.substring(0, lastWord) + suggestion, text.suggestions()));
        return result;
    }

    private static List<String> words(String text) {
        var value = text.strip();
        return value.isEmpty() ? List.of() : List.of(value.split("\\s+"));
    }

    private static String key(int index) {
        return "input_" + index;
    }
}
