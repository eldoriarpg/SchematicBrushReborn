---
title: Extending brushes
sidebar_position: 2
---

# Extending brushes

Everything a player can choose in the editor comes from a registry. Your plugin can add to it. Set up the
dependency first, as described on the [Developer API](./api.md) page.

Each setting has two parts:

- the **setting** itself: a `Selector` or a `Mutator`. It is stored with the brush and does the actual work.
- a **provider**, which creates the setting from what the player entered, offers tab completion and describes the
  inputs of the dialog editor.

Register providers in your `onEnable`, through `SchematicBrushReborn.instance().brushSettingsRegistry()`.

## Selectors

A selector returns the schematics of a set. This one picks every schematic whose name starts with a text:

```java
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Escape;
import org.bukkit.entity.Player;

import java.util.Set;

public class PrefixSelector implements Selector {
    private final String prefix;

    @JsonCreator
    public PrefixSelector(@JsonProperty("prefix") String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.get(SchematicCache.STORAGE).getSchematicsByName(player, prefix + "*");
    }

    @Override
    public String name() {
        return "Prefix";
    }

    @Override
    public String descriptor() {
        return Escape.text(prefix);
    }
}
```

`name()` and `descriptor()` are shown in the editor. The descriptor is MiniMessage text, so escape anything a
player typed with `Escape.text`.

The provider turns command arguments into the selector:

```java
import de.eldoria.eldoutilities.commands.command.util.Argument;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.provider.SelectorProvider;
import de.eldoria.schematicbrush.brush.config.provider.SettingInput;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PrefixSelectorProvider extends SelectorProvider {
    private final Argument[] arguments = {Argument.unlocalizedInput("prefix", true)};

    public PrefixSelectorProvider(SchematicRegistry registry) {
        super("Prefix", "Prefix", "Select schematics whose name starts with a text.", registry);
    }

    @Override
    public Selector parse(Arguments args) throws CommandException {
        return new PrefixSelector(args.asString(0));
    }

    @Override
    public Argument[] arguments() {
        return arguments;
    }

    @Override
    public List<SettingInput> inputs(@Nullable Selector current) {
        var initial = current instanceof PrefixSelector selector ? selector.prefix() : "";
        return List.of(SettingInput.text("Prefix", initial).withSuggestions());
    }

    @Override
    public List<String> complete(Arguments args, Player player) {
        if (args.size() == 1) {
            return registry().get(SchematicCache.STORAGE).getMatchingSchematics(player, args.asString(0), 50);
        }
        return Collections.emptyList();
    }

    @Override
    public Selector defaultSetting() {
        return new PrefixSelector("");
    }
}
```

Register it:

```java
var sbr = SchematicBrushReborn.instance();
sbr.brushSettingsRegistry().registerSelector(new PrefixSelectorProvider(sbr.schematics()));
```

The provider's name must be unique among the selectors. Registering a name twice throws an
`AlreadyRegisteredException`.

## Modifiers

A modifier setting is a `Mutator<T>`. Its `invoke` method changes the paste through a `PasteMutation`, which gives
access to the clipboard, the transform, the paste offset, the edit session, Include Air and the source mask.

This mutator sinks the schematic into the ground by a fixed depth:

```java
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

public class SinkFixed implements Mutator<Integer> {
    private int depth;

    @JsonCreator
    public SinkFixed(@JsonProperty("depth") int depth) {
        this.depth = depth;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        mutation.pasteOffset(mutation.pasteOffset().add(0, -depth, 0));
    }

    @Override
    public Mutator<Integer> copy() {
        return new SinkFixed(depth);
    }

    @Override
    public void value(@NotNull Integer value) {
        depth = value;
    }

    @Override
    public @NotNull Integer value() {
        return depth;
    }

    @Override
    public Integer valueProvider() {
        return depth;
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public String descriptor() {
        return String.valueOf(depth);
    }
}
```

What the value methods mean:

| Method | Meaning |
|---|---|
| `value()` | The value used for the next paste |
| `valueProvider()` | A new value. It is called before each paste to refresh `value()`. Return a random value here for random modifiers. |
| `shiftable()` | `true` if the player can step to another value while holding the brush. Default `false`. |
| `shift()` | Steps to the next value. Default: takes a new value from `valueProvider()`. |
| `copy()` | A copy that shares no changeable state |

Stepping in hand (left click, swap hands) only reaches the built-in Rotation, Flip and Offset types.

The provider works like a selector provider, but extends `ModifierProvider`:

```java
public class SinkProvider extends ModifierProvider {
    private final Argument[] arguments = {Argument.unlocalizedInput("depth", true)};

    public SinkProvider() {
        super("Fixed", "Fixed", "Sink the schematic by a fixed number of blocks.");
    }

    @Override
    public Mutator<?> parse(Arguments args) throws CommandException {
        CommandAssertions.range(args.asInt(0), 0, 50);
        return new SinkFixed(args.asInt(0));
    }

    @Override
    public Argument[] arguments() {
        return arguments;
    }

    @Override
    public List<SettingInput> inputs(@Nullable Mutator<?> current) {
        var initial = current instanceof SinkFixed sink ? sink.value() : 0;
        return List.of(SettingInput.numeric("Depth", 0, 50, initial));
    }

    @Override
    public List<String> complete(Arguments args, Player player) throws CommandException {
        return Completion.completeInt(args.asString(0), 0, 50);
    }

    @Override
    public Mutator<?> defaultSetting() {
        return new SinkFixed(0);
    }
}
```

`CommandAssertions` and `Completion` come from eldo-util (`de.eldoria.eldoutilities.commands`).

### Register a modifier

Modifiers are registered under a **type**. A type is a slot on the brush or on a set, like Rotation or Offset.
Each type can have several providers, such as Fixed, List and Random.

You can add your provider to a built-in type:

```java
var registry = SchematicBrushReborn.instance().brushSettingsRegistry();
registry.registerSchematicModifier(SchematicModifier.OFFSET, new MyOffsetProvider());
registry.registerPlacementModifier(PlacementModifier.OFFSET, new MyOffsetProvider());
```

Or create your own type. It then shows up in the editor as a modifier of its own:

```java
public static final PlacementModifier SINK = PlacementModifier.of(
        "Sink", "Sink", "Sink the schematic into the ground.", false);

registry.registerPlacementModifier(SINK, new SinkProvider());
```

`SchematicModifier.of(...)` creates a type for sets in the same way. The arguments are the name, the localized
name, the description and whether the type is required.

- A **placement modifier** applies to the whole brush. A **schematic modifier** applies to one set.
- The first provider registered for a type gives the default value when a player adds the type in the editor.
- A **required** type is put on every new brush or set with that default, and players cannot remove it.
- A provider name may only be registered once per type.

The type name is stored with the brush. Keep it stable.

## Dialog inputs

`SettingProvider#inputs(current)` tells the dialog editor which inputs to show. The values the player enters are
turned back into command arguments, in the order of the inputs, and passed to your `parse` method. So the same
checks apply as for commands.

| Input | Shown as | Produces |
|---|---|---|
| `SettingInput.choice(label, options, initial)` | one of several options | the chosen option |
| `SettingInput.toggle(label, initial)` | an on and off switch | `true` or `false` |
| `SettingInput.numeric(label, min, max, initial)` | a whole number within bounds | the number |
| `SettingInput.text(label, initial)` | a text field | one argument per word, none if empty |
| `SettingInput.selection(label, options, initial)` | any number of the options | one argument per chosen option, in the order of the options |

`current` is the setting the player has now, possibly from another provider of the same type, or `null`. Use it to
start from the current value.

Text inputs can offer suggestions with `SettingInput.text(...).withSuggestions()`. The dialog then shows a
**Suggestions** button that lists the results of your `complete` method. Only use it where `complete` returns real
values, not placeholders like `<name>`.

If you do not override `inputs`, the dialog shows one empty text field per argument from `arguments()`. A provider
without arguments is applied as soon as the player clicks it. Whether a provider counts as having arguments comes
from `hasArguments()`, which by default checks `arguments()`.

Labels, names and descriptions can be plain text or keys of Schematic Brush Reborn's message files.

## Permissions

Override `permission()` in a provider to require a permission for it:

```java
@Override
public String permission() {
    return "myaddon.selector.prefix";
}
```

The dialog editor and tab completion hide providers the player may not use. The permission is checked whenever a
value is parsed, so `/sbr modify`, `/sbr modifyset`, the chat editor and the dialog editor all enforce it.

## How settings are stored

Brushes and presets are saved with Jackson. Every selector and mutator is stored with its full class name under
the key `@clazz`, next to its fields:

```yaml
selector:
  '@clazz': "com.example.myaddon.PrefixSelector"
  prefix: "oak"
```

- Fields are saved directly, whatever their visibility. Getters are ignored.
- Give the class a constructor marked `@JsonCreator`, with `@JsonProperty` on every parameter.
- Keep the class name and package stable once players have saved brushes with it.

## Selection modes

Selection modes decide which schematic comes next. Implement `SchematicSelection`, create a
`SchematicSelectionProvider` and register it with `registerSchematicSelection`. Players then choose it with
`/sbrm selection`.

## Schematic caches

A schematic cache provides schematics. Schematic Brush Reborn's own cache, registered as `SchematicCache.STORAGE`,
reads the [schematic folders](../schematics.md). Your plugin can add another one, for example for schematics from a
web service:

```java
var sbr = SchematicBrushReborn.instance();
sbr.schematics().register(Nameable.of("my_cache"), new MySchematicCache());
```

- `init()` runs when the cache is registered. `reload()` runs on `/sbra reloadCache`.
- `isReady()` returns `false` while the cache is still loading.
- `shutdown()` runs when Schematic Brush Reborn shuts down.
- The built-in selectors only read `SchematicCache.STORAGE`. To use your cache, add a selector that reads it with
  `registry.get(Nameable.of("my_cache"))`.

A `Schematic` is created from a file with `Schematic.of(path)`. The format is detected by FastAsyncWorldEdit.

## Storages

A storage keeps presets and brush presets. Implement `Storage` with its `presets()` and `brushes()` containers and
register it:

```java
sbr.storageRegistry().register(Nameable.of("my_storage"), new MyStorage());
```

Server owners switch to it by setting `storageType: "my_storage"` in `config.yml`. `/sbra migrate` copies data
between storages. The containers work with `CompletableFuture`s, so they can talk to a database without blocking
the server.

To read and write `Brush` and `Preset` objects the same way Schematic Brush Reborn does, build your mapper with
`sbr.configureMapper(...)`. The [Schematic Brush Database](/schematic-brush-reborn/database) add-on is a complete
example of a storage.
