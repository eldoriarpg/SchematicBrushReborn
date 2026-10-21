/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.flip.FlipFixed;
import de.eldoria.schematicbrush.brush.config.flip.FlipList;
import de.eldoria.schematicbrush.brush.config.includeair.IncludeAir;
import de.eldoria.schematicbrush.brush.config.offset.OffsetFixed;
import de.eldoria.schematicbrush.brush.config.offset.OffsetList;
import de.eldoria.schematicbrush.brush.config.offset.OffsetRange;
import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.replaceall.ReplaceAll;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.rotation.RotationFixed;
import de.eldoria.schematicbrush.brush.config.rotation.RotationList;
import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.provider.FlipProvider;
import de.eldoria.schematicbrush.brush.provider.IncludeAirProvider;
import de.eldoria.schematicbrush.brush.provider.OffsetProvider;
import de.eldoria.schematicbrush.brush.provider.ReplaceAllProvider;
import de.eldoria.schematicbrush.brush.provider.RotationProvider;
import de.eldoria.schematicbrush.brush.provider.SelectorProviderImpl;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DialogInputsTest {
    @Test
    void untouchedInputsParseToTheCurrentValue() throws CommandException {
        assertRoundTrip(FlipProvider.FIXED, new FlipFixed(Flip.UP_DOWN));
        assertRoundTrip(FlipProvider.LIST, new FlipList(List.of(Flip.NORTH_SOUTH, Flip.EAST_WEST)));
        assertRoundTrip(RotationProvider.FIXED, new RotationFixed(Rotation.ROT_HALF));
        assertRoundTrip(RotationProvider.LIST, new RotationList(List.of(Rotation.ROT_ZERO, Rotation.ROT_RIGHT)));
        assertRoundTrip(OffsetProvider.FIXED, new OffsetFixed(-12));
        assertRoundTrip(OffsetProvider.LIST, new OffsetList(List.of(-3, 0, 7)));
        assertRoundTrip(OffsetProvider.RANGE, new OffsetRange(-5, 20));
        assertRoundTrip(IncludeAirProvider.FIXED, new IncludeAir(true));
        assertRoundTrip(ReplaceAllProvider.FIXED, new ReplaceAll(true));
        assertRoundTrip(SelectorProviderImpl.NAME.apply(null), new NameSelector("tree"));
        assertRoundTrip(SelectorProviderImpl.REGEX.apply(null), new RegexSelector("tree_[0-9]+"));
        assertRoundTrip(SelectorProviderImpl.DIRECTORY.apply(null), new DirectorySelector("nature/trees", "oak*"));
    }

    @Test
    void textIsSplitIntoWords() {
        var inputs = List.<SettingInput>of(SettingInput.text("label", ""));

        assertEquals(List.of("1", "2", "3"), DialogInputs.tokens(inputs, new Response(Map.of("input_0", " 1  2 3 "))));
        assertEquals(List.of(), DialogInputs.tokens(inputs, new Response(Map.of("input_0", "  "))));
    }

    @Test
    void numbersAreRoundedToWholeNumbers() {
        var inputs = List.<SettingInput>of(SettingInput.numeric("label", -100, 100, 0));

        assertEquals(List.of("-4"), DialogInputs.tokens(inputs, new Response(Map.of("input_0", -4.0001f))));
    }

    @Test
    void selectionKeepsTheOrderOfTheOptions() {
        var inputs = List.<SettingInput>of(SettingInput.selection("label", List.of("a", "b", "c"), List.of()));

        assertEquals(List.of("a", "c"), DialogInputs.tokens(inputs, new Response(Map.of("input_0_2", true, "input_0_0", true, "input_0_1", false))));
    }

    @Test
    void completesTheLastWordAfterTheEarlierInputs() {
        var inputs = List.<SettingInput>of(SettingInput.text("dir", "nature/trees"), SettingInput.text("filter", "oak bir"));

        assertEquals(List.of("nature/trees", "oak", "bir"), DialogInputs.completionTokens(inputs, 1));
        assertEquals(List.of("nature/trees", ""), DialogInputs.completionTokens(List.of(inputs.get(0), SettingInput.text("filter", "")), 1));
    }

    @Test
    void suggestionReplacesOnlyTheLastWord() {
        var inputs = List.<SettingInput>of(SettingInput.text("filter", "oak bir").withSuggestions());

        assertEquals(new SettingInput.Text("filter", "oak birch", true), DialogInputs.withSuggestion(inputs, 0, "birch").get(0));
        assertEquals(new SettingInput.Text("filter", "oak birch", true),
                DialogInputs.withSuggestion(List.of(SettingInput.text("filter", "oak ").withSuggestions()), 0, "birch").get(0));
    }

    private static <T> void assertRoundTrip(SettingProvider<T> provider, T current) throws CommandException {
        var inputs = provider.inputs(current);
        var tokens = DialogInputs.tokens(inputs, initialResponse(inputs));
        assertEquals(current, provider.parse(Arguments.create(null, null, tokens.toArray(String[]::new))), provider.name());
    }

    private static Response initialResponse(List<SettingInput> inputs) {
        var values = new HashMap<String, Object>();
        for (var index = 0; index < inputs.size(); index++) {
            var key = "input_" + index;
            switch (inputs.get(index)) {
                case SettingInput.Choice choice -> values.put(key, choice.initial());
                case SettingInput.Toggle toggle -> values.put(key, toggle.initial());
                case SettingInput.Numeric numeric -> values.put(key, (float) numeric.initial());
                case SettingInput.Text text -> values.put(key, text.initial());
                case SettingInput.Selection selection -> {
                    for (var option = 0; option < selection.options().size(); option++) {
                        values.put(key + "_" + option, selection.initial().contains(selection.options().get(option)));
                    }
                }
            }
        }
        return new Response(values);
    }

    private record Response(Map<String, ?> values) implements DialogResponseView {
        @Override
        public BinaryTagHolder payload() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable String getText(String key) {
            return values.get(key) instanceof String text ? text : null;
        }

        @Override
        public @Nullable Boolean getBoolean(String key) {
            return values.get(key) instanceof Boolean bool ? bool : null;
        }

        @Override
        public @Nullable Float getFloat(String key) {
            return values.get(key) instanceof Float number ? number : null;
        }
    }
}
