/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.commands.brush.Sessions;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Escape;
import de.eldoria.schematicbrush.util.Permissions;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The brush editor as Minecraft dialogs.
 * <p>
 * Every button runs a callback on the server that edits the session through {@link Sessions#edit(Player, Sessions.Edit)}
 * and shows the next dialog. Values are entered in the inputs a provider declares with
 * {@link SettingProvider#inputs(Object)} and parsed by the provider like command arguments, so validation and messages
 * match the commands. A rejected edit shows the same dialog again with the error on top.
 * <p>
 * Binding, loading and saving run the matching {@code /sbr} command after closing the dialog, since they report in chat
 * or finish asynchronously.
 */
public final class DialogEditor implements EditorView {
    private static final ClickCallback.Options CALLBACK = ClickCallback.Options.builder()
                                                                               .uses(1)
                                                                               .lifetime(Duration.ofMinutes(30))
                                                                               .build();
    private static final int BUTTON_WIDTH = 150;
    private static final int COLUMNS = 2;
    private static final int MAX_SUGGESTIONS = 30;

    private final Plugin plugin;
    private final Sessions sessions;
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematics;
    private final MessageSender messageSender;

    public DialogEditor(Plugin plugin, Sessions sessions, BrushSettingsRegistry registry, SchematicRegistry schematics) {
        this.plugin = plugin;
        this.sessions = sessions;
        this.registry = registry;
        this.schematics = schematics;
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    @Override
    public void showBrush(Player player) {
        showBrush(player, null);
    }

    @Override
    public void showSets(Player player) {
        showSets(player, null);
    }

    @Override
    public void showSet(Player player, int id) {
        showSet(player, id, null);
    }

    private void showBrush(Player player, @Nullable Component error) {
        Screen self = err -> showBrush(player, err);
        var builder = sessions.getOrCreateSession(player);
        var body = body(error);
        body.add(line(player, "<heading><i18n:words.schematicSets>: <value>%d".formatted(builder.schematicSets().size())));
        for (var set : builder.schematicSets()) {
            body.add(line(player, "  " + BuildUtil.renderProvider(set.selector())));
        }
        var placement = builder.placementModifier();

        var buttons = new ArrayList<ActionButton>();
        buttons.add(button(player, "<i18n:words.schematicSets>", null, self, response -> showSets(player, null)));
        for (var entry : registry.placementModifier().entrySet()) {
            var type = entry.getKey();
            var current = placement.get(type);
            if (current != null) {
                body.add(modifierLine(player, type.getLocalizedName(), current));
                buttons.add(button(player, type.getLocalizedName(), type.description(), self,
                        response -> showModifier(player, () -> placementSlot(player, type), null)));
            } else {
                buttons.add(button(player, "<add>+ " + type.getLocalizedName(), type.description(), self, response -> {
                    sessions.edit(player, edited -> edited.setPlacementModifier(type, entry.getValue().get(0).defaultSetting()));
                    showBrush(player, null);
                }));
            }
        }
        buttons.add(button(player, "<add><i18n:words.bind>", null, self, response -> command(player, "sbr bind")));
        buttons.add(button(player, "<remove><i18n:words.clear>", null, self, response -> {
            sessions.edit(player, BrushBuilder::clear);
            showBrush(player, null);
        }));
        if (sessions.canUndo(player)) {
            buttons.add(button(player, "<change><i18n:words.undo>", null, self, response -> {
                sessions.undo(player);
                showBrush(player, null);
            }));
        }
        if (sessions.canRedo(player)) {
            buttons.add(button(player, "<change><i18n:words.redo>", null, self, response -> {
                sessions.redo(player);
                showBrush(player, null);
            }));
        }
        if (player.hasPermission(Permissions.BrushPreset.USE)) {
            buttons.add(button(player, "<i18n:commands.brush.sessions.loadBrush>", null, self,
                    response -> showName(player, NameRequest.LOAD_BRUSH, null)));
            buttons.add(button(player, "<i18n:commands.brush.sessions.saveBrush>", null, self,
                    response -> showName(player, NameRequest.SAVE_BRUSH, null)));
        }

        show(player, "commands.brush.sessions.menu", body, List.of(),
                menu(buttons, button(player, "<i18n:words.close>", null, self, response -> player.closeDialog())));
    }

    private void showSets(Player player, @Nullable Component error) {
        Screen self = err -> showSets(player, err);
        var sets = sessions.getOrCreateSession(player).schematicSets();
        var body = body(error);
        var buttons = new ArrayList<ActionButton>();
        for (var id = 0; id < sets.size(); id++) {
            var set = sets.get(id);
            var setId = id;
            body.add(line(player, "<name>#%d</name> %s <value>(%d <i18n:words.schematics>)".formatted(id, BuildUtil.renderProvider(set.selector()), set.schematicCount())));
            buttons.add(button(player, "<change><i18n:words.edit> #%d".formatted(id), set.infoComponent(), self,
                    response -> showSet(player, setId, null)));
        }
        buttons.add(button(player, "<add><i18n:words.add>", null, self, response -> {
            var builder = sessions.getOrCreateSession(player);
            sessions.edit(player, BrushBuilder::createSchematicSet);
            showSet(player, builder.schematicSets().size() - 1, null);
        }));
        buttons.add(button(player, "<i18n:commands.brush.sessions.refreshSchematics>", null, self, response -> {
            sessions.edit(player, BrushBuilder::refresh);
            showSets(player, null);
        }));
        if (player.hasPermission(Permissions.Preset.USE)) {
            buttons.add(button(player, "<i18n:commands.brush.sessions.addPreset>", null, self,
                    response -> showName(player, NameRequest.ADD_PRESET, null)));
            buttons.add(button(player, "<i18n:commands.brush.sessions.savePreset>", null, self,
                    response -> showName(player, NameRequest.SAVE_PRESET, null)));
        }

        show(player, "words.schematicSets", body, List.of(),
                menu(buttons, button(player, "<i18n:words.back>", null, self, response -> showBrush(player, null))));
    }

    private void showSet(Player player, int id, @Nullable Component error) {
        Screen self = err -> showSet(player, id, err);
        var optSet = sessions.getOrCreateSession(player).getSchematicSet(id);
        if (optSet.isEmpty()) {
            showSets(player, messageSender.serializeError(player, "error.invalidSetId"));
            return;
        }
        var set = optSet.get();
        var body = body(error);
        body.add(line(player, "<heading><i18n:words.selector>: " + BuildUtil.renderProvider(set.selector())));
        body.add(line(player, "<heading><i18n:words.schematics>: <value>" + set.schematicCount()));
        var modifiers = set.schematicModifier();

        var buttons = new ArrayList<ActionButton>();
        buttons.add(button(player, "<i18n:words.selector>", null, self,
                response -> showModifier(player, () -> selectorSlot(player, id), null)));
        for (var entry : registry.schematicModifier().entrySet()) {
            var type = entry.getKey();
            var current = modifiers.get(type);
            if (current != null) {
                body.add(modifierLine(player, type.getLocalizedName(), current));
                buttons.add(button(player, type.getLocalizedName(), type.description(), self,
                        response -> showModifier(player, () -> setModifierSlot(player, id, type), null)));
            } else {
                buttons.add(button(player, "<add>+ " + type.getLocalizedName(), type.description(), self, response -> {
                    sessions.edit(player, edited -> set(player, id).setModifier(type, entry.getValue().get(0).defaultSetting()));
                    showSet(player, id, null);
                }));
            }
        }
        body.add(line(player, "<heading><i18n:words.weight>: <value>" + set.weight()));
        buttons.add(button(player, "<i18n:words.weight>", "commands.brush.sessions.weight", self,
                response -> showWeight(player, id, null)));
        buttons.add(button(player, "<remove><i18n:words.remove>", null, self, response -> {
            set(player, id);
            sessions.edit(player, edited -> edited.removeSchematicSet(id));
            showSets(player, null);
        }));

        show(player, "words.schematicSets", body, List.of(),
                menu(buttons, button(player, "<i18n:words.back>", null, self, response -> showSets(player, null))));
    }

    private void showWeight(Player player, int id, @Nullable Component error) {
        Screen self = err -> showWeight(player, id, err);
        SchematicSetBuilder set;
        try {
            set = set(player, id);
        } catch (CommandException e) {
            showSets(player, error(player, e));
            return;
        }
        var body = body(error);
        body.add(line(player, "commands.brush.dialog.weightAuto"));
        var weight = SettingInput.numeric("words.weight", 0, 100, Math.max(set.weight(), 0));
        var inputs = List.<SettingInput>of(weight);

        var apply = button(player, "<confirm><i18n:words.apply>", null, self, response -> {
            var value = Integer.parseInt(DialogInputs.tokens(inputs, response).get(0));
            sessions.edit(player, edited -> set(player, id).withWeight(value == 0 ? -1 : value));
            showSet(player, id, null);
        });
        var back = button(player, "<i18n:words.back>", null, self, response -> showSet(player, id, null));
        show(player, "words.weight", body, DialogInputs.create(inputs, text -> text(player, text)), DialogType.confirmation(apply, back));
    }

    private <T> void showModifier(Player player, SlotSource<T> source, @Nullable Component error) {
        Screen self = err -> showModifier(player, source, err);
        Slot<T> slot;
        try {
            slot = source.get();
        } catch (CommandException e) {
            showSets(player, error(player, e));
            return;
        }
        var body = body(error);
        if (slot.description() != null) body.add(line(player, "<neutral>" + slot.description()));
        if (slot.current() instanceof ComponentProvider current) body.add(line(player, BuildUtil.renderProvider(current)));

        var buttons = new ArrayList<ActionButton>();
        for (var provider : slot.providers()) {
            if (!provider.hasPermission(player)) continue;
            if (provider.hasArguments()) {
                buttons.add(button(player, provider.localizedName() + " …", provider.localizedDescription(), self,
                        response -> showProvider(player, source, provider, null, null)));
            } else {
                buttons.add(button(player, provider.localizedName(), provider.localizedDescription(), self,
                        response -> slot.apply().apply(parse(player, provider, List.of()))));
            }
        }
        if (slot.remove() != null) {
            buttons.add(button(player, "<remove><i18n:words.remove>", null, self, response -> slot.remove().run(response)));
        }

        show(player, slot.title(), body, List.of(),
                menu(buttons, button(player, "<i18n:words.back>", null, self, response -> slot.back().show(null))));
    }

    private <T> void showProvider(Player player, SlotSource<T> source, SettingProvider<T> provider,
                                  @Nullable List<SettingInput> entered, @Nullable Component error) {
        Slot<T> slot;
        try {
            slot = source.get();
        } catch (CommandException e) {
            showSets(player, error(player, e));
            return;
        }
        var inputs = entered != null ? entered : provider.inputs(slot.current());
        Screen self = err -> showProvider(player, source, provider, inputs, err);
        var body = body(error);
        body.add(line(player, "<neutral>" + provider.localizedDescription()));

        var buttons = new ArrayList<ActionButton>();
        buttons.add(button(player, "<confirm><i18n:words.apply>", null, self,
                response -> slot.apply().apply(parse(player, provider, DialogInputs.tokens(inputs, response)))));
        for (var index = 0; index < inputs.size(); index++) {
            if (!(inputs.get(index) instanceof SettingInput.Text text) || !text.suggestions()) continue;
            var field = index;
            buttons.add(button(player, "<change><i18n:words.suggestions>: " + localized(text.label()), null, self,
                    response -> showSuggestions(player, source, provider, DialogInputs.entered(inputs, response), field, null)));
        }
        var back = button(player, "<i18n:words.back>", null, self, response -> showModifier(player, source, null));
        show(player, provider.localizedName(), body, DialogInputs.create(inputs, text -> text(player, text)), menu(buttons, back));
    }

    private <T> void showSuggestions(Player player, SlotSource<T> source, SettingProvider<T> provider,
                                     List<SettingInput> entered, int field, @Nullable Component error) {
        Screen self = err -> showSuggestions(player, source, provider, entered, field, err);
        Screen form = err -> showProvider(player, source, provider, entered, err);
        List<String> suggestions;
        try {
            var tokens = DialogInputs.completionTokens(entered, field);
            suggestions = provider.complete(Arguments.create(plugin, player, tokens.toArray(String[]::new)), player);
        } catch (CommandException e) {
            form.show(error(player, e));
            return;
        }
        var body = body(error);
        var buttons = new ArrayList<ActionButton>();
        for (var suggestion : suggestions.stream().limit(MAX_SUGGESTIONS).toList()) {
            buttons.add(button(player, "<value>" + Escape.text(suggestion), null, self,
                    response -> showProvider(player, source, provider, DialogInputs.withSuggestion(entered, field, suggestion), null)));
        }
        if (buttons.isEmpty()) body.add(line(player, "commands.brush.dialog.noSuggestions"));
        if (suggestions.size() > MAX_SUGGESTIONS) body.add(line(player, "commands.brush.dialog.moreSuggestions"));

        var back = button(player, "<i18n:words.back>", null, self, response -> form.show(null));
        show(player, "words.suggestions", body, List.of(), buttons.isEmpty() ? DialogType.notice(back) : menu(buttons, back));
    }

    private void showName(Player player, NameRequest request, @Nullable Component error) {
        Screen self = err -> showName(player, request, err);
        var body = body(error);
        body.add(line(player, request.hint()));
        var inputs = new ArrayList<DialogInput>();
        inputs.add(DialogInput.text("name", text(player, "words.name")).width(DialogInputs.WIDTH).maxLength(64).build());
        if (request.saves()) {
            if (player.hasPermission(request.globalPermission())) {
                inputs.add(DialogInput.bool("global", text(player, "words.global")).build());
            }
            inputs.add(DialogInput.bool("force", text(player, "words.overwrite")).build());
        }

        var apply = button(player, "<confirm><i18n:words.apply>", null, self, response -> {
            var name = response.getText("name") == null ? "" : response.getText("name").strip();
            CommandAssertions.isFalse(name.isEmpty() || name.chars().anyMatch(Character::isWhitespace), "commands.brush.dialog.invalidName");
            var line = new StringBuilder(request.command()).append(' ').append(name);
            if (Boolean.TRUE.equals(response.getBoolean("global"))) line.append(" -g");
            if (Boolean.TRUE.equals(response.getBoolean("force"))) line.append(" -f");
            command(player, line.toString());
        });
        var back = button(player, "<i18n:words.back>", null, self, response -> {
            if (request.fromSets()) showSets(player, null);
            else showBrush(player, null);
        });
        show(player, request.title(), body, inputs, DialogType.confirmation(apply, back));
    }

    private Slot<Mutator<?>> placementSlot(Player player, PlacementModifier type) {
        return new Slot<>(type.getLocalizedName(), type.description(), registry.placementModifier().get(type),
                sessions.getOrCreateSession(player).placementModifier().get(type),
                value -> {
                    sessions.edit(player, edited -> edited.setPlacementModifier(type, value));
                    showBrush(player, null);
                },
                type.required() ? null : response -> {
                    sessions.edit(player, edited -> edited.removePlacementModifier(type));
                    showBrush(player, null);
                },
                err -> showBrush(player, err));
    }

    private Slot<Mutator<?>> setModifierSlot(Player player, int id, SchematicModifier type) throws CommandException {
        return new Slot<>(type.getLocalizedName(), type.description(), registry.schematicModifier().get(type),
                set(player, id).schematicModifier().get(type),
                value -> {
                    sessions.edit(player, edited -> set(player, id).setModifier(type, value));
                    showSet(player, id, null);
                },
                type.required() ? null : response -> {
                    sessions.edit(player, edited -> set(player, id).removeModifier(type));
                    showSet(player, id, null);
                },
                err -> showSet(player, id, err));
    }

    private Slot<Selector> selectorSlot(Player player, int id) throws CommandException {
        return new Slot<>("words.selector", null, registry.selector(), set(player, id).selector(),
                value -> {
                    CommandAssertions.isFalse(value.select(player, schematics).isEmpty(), "error.noMatchSelector");
                    sessions.edit(player, edited -> {
                        var set = set(player, id);
                        set.selector(value);
                        set.refreshSchematics(player, schematics);
                    });
                    showSet(player, id, null);
                },
                null,
                err -> showSet(player, id, err));
    }

    private SchematicSetBuilder set(Player player, int id) throws CommandException {
        return sessions.getOrCreateSession(player).getSchematicSet(id)
                       .orElseThrow(() -> CommandException.message("error.invalidSetId"));
    }

    private <T> T parse(Player player, SettingProvider<T> provider, List<String> tokens) throws CommandException {
        if (provider.hasPermission()) {
            CommandAssertions.permission(player, false, provider.permission());
        }
        var args = Arguments.create(plugin, player, tokens.toArray(String[]::new));
        CommandAssertions.invalidArguments(args, provider.arguments());
        return provider.parse(args);
    }

    private void command(Player player, String command) {
        player.closeDialog();
        player.performCommand(command);
    }

    private ActionButton button(Player player, String label, @Nullable String tooltip, Screen onError, Action action) {
        return ActionButton.builder(text(player, label))
                           .tooltip(tooltip == null ? null : text(player, tooltip))
                           .width(BUTTON_WIDTH)
                           .action(DialogAction.customClick((response, audience) -> run(player, onError, action, response), CALLBACK))
                           .build();
    }

    private void run(Player player, Screen onError, Action action, DialogResponseView response) {
        if (!plugin.getServer().isPrimaryThread()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> run(player, onError, action, response));
            return;
        }
        if (!player.isOnline()) return;
        try {
            action.run(response);
        } catch (CommandException e) {
            if (e.isSilent()) {
                player.closeDialog();
                return;
            }
            onError.show(error(player, e));
        }
    }

    private static DialogType menu(List<ActionButton> buttons, ActionButton exit) {
        return DialogType.multiAction(buttons).exitAction(exit).columns(COLUMNS).build();
    }

    private void show(Player player, String title, List<DialogBody> body, List<DialogInput> inputs, DialogType type) {
        var base = DialogBase.builder(text(player, title))
                             .canCloseWithEscape(true)
                             .pause(false)
                             .afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                             .body(body)
                             .inputs(inputs)
                             .build();
        player.showDialog(Dialog.create(factory -> factory.empty().base(base).type(type)));
    }

    private List<DialogBody> body(@Nullable Component error) {
        var body = new ArrayList<DialogBody>();
        if (error != null) body.add(DialogBody.plainMessage(error, DialogInputs.WIDTH));
        return body;
    }

    private DialogBody modifierLine(Player player, String name, Mutator<?> current) {
        return line(player, "<heading>%s: %s".formatted(name, BuildUtil.renderProvider(current)));
    }

    private DialogBody line(Player player, String message) {
        return DialogBody.plainMessage(text(player, message), DialogInputs.WIDTH);
    }

    private static String localized(String text) {
        return ILocalizer.isLocaleCode(text) ? ILocalizer.escape(text) : text;
    }

    private Component text(Player player, String message) {
        return messageSender.serializeMessage(player, message);
    }

    private Component error(Player player, CommandException e) {
        return messageSender.serializeError(player, e.getMessage(), e.replacements());
    }

    /**
     * Shows a screen, with an error on top if one is given.
     */
    @FunctionalInterface
    private interface Screen {
        void show(@Nullable Component error);
    }

    /**
     * What a button does when clicked.
     */
    @FunctionalInterface
    private interface Action {
        void run(DialogResponseView response) throws CommandException;
    }

    /**
     * Stores a value chosen for a slot.
     *
     * @param <T> type of the value
     */
    @FunctionalInterface
    private interface Apply<T> {
        void apply(T value) throws CommandException;
    }

    /**
     * Reads a slot afresh, so a dialog always shows the current state of the session.
     *
     * @param <T> type of the value
     */
    @FunctionalInterface
    private interface SlotSource<T> {
        Slot<T> get() throws CommandException;
    }

    /**
     * A setting of the brush that one of several providers fills: a placement modifier, a set modifier or the selector
     * of a set.
     *
     * @param title       title of the dialog
     * @param description description of the setting, if any
     * @param providers   providers to choose from
     * @param current     current value, if any
     * @param apply       stores a new value and shows the next dialog
     * @param remove      removes the setting, null if it is required
     * @param back        the dialog the setting belongs to
     * @param <T>         type of the value
     */
    private record Slot<T>(String title, @Nullable String description, List<? extends SettingProvider<T>> providers,
                           @Nullable T current, Apply<T> apply, @Nullable Action remove, Screen back) {
    }

    /**
     * Asks for a name and runs the command that loads or saves a brush or preset under it.
     */
    private enum NameRequest {
        LOAD_BRUSH("sbr loadbrush", "commands.brush.sessions.loadBrush", "commands.brush.dialog.loadHint", false, false, ""),
        SAVE_BRUSH("sbr savebrush", "commands.brush.sessions.saveBrush", "commands.brush.dialog.saveHint", true, false, Permissions.BrushPreset.GLOBAL),
        ADD_PRESET("sbr addpreset", "commands.brush.sessions.addPreset", "commands.brush.dialog.loadHint", false, true, ""),
        SAVE_PRESET("sbr savepreset", "commands.brush.sessions.savePreset", "commands.brush.dialog.saveHint", true, true, Permissions.Preset.GLOBAL);

        private final String command;
        private final String title;
        private final String hint;
        private final boolean saves;
        private final boolean fromSets;
        private final String globalPermission;

        NameRequest(String command, String title, String hint, boolean saves, boolean fromSets, String globalPermission) {
            this.command = command;
            this.title = title;
            this.hint = hint;
            this.saves = saves;
            this.fromSets = fromSets;
            this.globalPermission = globalPermission;
        }

        String command() {
            return command;
        }

        String title() {
            return title;
        }

        String hint() {
            return hint;
        }

        boolean saves() {
            return saves;
        }

        boolean fromSets() {
            return fromSets;
        }

        String globalPermission() {
            return globalPermission;
        }
    }
}
