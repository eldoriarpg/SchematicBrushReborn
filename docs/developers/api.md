---
title: Developer API
sidebar_position: 1
---

# Developer API

Schematic Brush Reborn has an API for your own plugins. With it you can:

- add your own [selectors and modifiers](./extending.md),
- add your own [schematic sources and storages](./extending.md#schematic-caches),
- react to pastes with [events](./events.md),
- read and bind brushes from code.

The Javadocs of the API are at [eldoriarpg.github.io/SchematicBrushReborn](https://eldoriarpg.github.io/SchematicBrushReborn/).

## Add the dependency

The API is published to the EldoNexus repository.

**Gradle (Kotlin)**

```kotlin
repositories {
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    compileOnly("de.eldoria:schematicbrushreborn-api:3.0.0")
}
```

**Maven**

```xml
<repositories>
    <repository>
        <id>EldoNexus</id>
        <url>https://eldonexus.de/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.eldoria</groupId>
        <artifactId>schematicbrushreborn-api</artifactId>
        <version>3.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Schematic Brush Reborn 3.0.0 itself is built with:

| Library | Version |
|---|---|
| Java | 25 |
| Paper API | 1.21.11 |
| FastAsyncWorldEdit (core and bukkit) | 2.16.1 |
| eldo-util (`de.eldoria.util`) | 3.5.0 |
| Jackson (`tools.jackson`) | 3.2.4 |

Add Paper and FastAsyncWorldEdit as `compileOnly` too. The API uses types from eldo-util and Jackson, for example
`Arguments`, `CommandException` and the Jackson annotations. Add the eldo-util modules and the Jackson modules you
use as `compileOnly` as well, in the versions above.

## Relocate, do not shade

Schematic Brush Reborn ships eldo-util and Jackson inside its own jar, moved to the package
`de.eldoria.schematicbrush.libs`. Your plugin must use those copies. Do not put eldo-util or Jackson into your jar.
Instead, relocate your references to them, so they point at Schematic Brush Reborn's copies.

With the Gradle Shadow plugin:

```kotlin
tasks.shadowJar {
    val shadebase = "de.eldoria.schematicbrush.libs."
    relocate("com.fasterxml", shadebase + "fasterxml")
    relocate("tools.jackson", shadebase + "jackson")
    relocate("dev.chojo.ocular", shadebase + "ocular")
    relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
    relocate("de.eldoria.eldoutilities", shadebase + "utilities")
    mergeServiceFiles()
}
```

Keep the libraries `compileOnly`, so only the relocated references end up in your jar.

## Declare the dependency in your plugin

Your plugin must load after Schematic Brush Reborn. In `plugin.yml`:

```yaml
depend:
  - SchematicBrushReborn
  - FastAsyncWorldEdit
```

## Get the API

`SchematicBrushReborn.instance()` returns the running plugin. Call it in `onEnable`:

```java
import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.plugin.java.JavaPlugin;

public class MyAddon extends JavaPlugin {
    private SchematicBrushReborn sbr;

    @Override
    public void onEnable() {
        sbr = SchematicBrushReborn.instance();
    }
}
```

The plugin object gives you everything else:

| Method | Returns | Use it to |
|---|---|---|
| `brushSettingsRegistry()` | `BrushSettingsRegistry` | Register selectors, modifiers and selection modes. See [Extending brushes](./extending.md). |
| `schematics()` | `SchematicRegistry` | Register schematic caches. See [Schematic caches](./extending.md#schematic-caches). |
| `storageRegistry()` | `StorageRegistry` | Register storages for presets and brushes. See [Storages](./extending.md#storages). |
| `config()` | `Configuration` | Read the configuration and the active preset and brush storage |
| `configureMapper(MapperBuilder)` | `ObjectMapper` | Build a Jackson mapper that reads and writes brushes and presets like Schematic Brush Reborn does |
| `schematicBrushModule()` | `SimpleModule` | The Jackson module with Schematic Brush Reborn's serializers |
| `platformModule()` | `JacksonModule` | The Jackson module for Paper types |

## How the add-ons hook in

The add-ons in these docs use the API in different ways:

| Add-on | Uses |
|---|---|
| [Schematic Brush Database](/schematic-brush-reborn/database) | Registers database storages with `storageRegistry()` |
| [Schematic Tools](/schematic-brush-reborn/tools) | Loads saved brushes and binds them to tools; listens to `PrePasteEvent` and `PostPasteEvent` |
| [Survival Schematic Brush](/schematic-brush-reborn/survival) | Listens to `PrePasteEvent` to take the blocks from the player |

## Brushes from code

`WorldEditBrush` finds and binds schematic brushes:

```java
Optional<SchematicBrush> brush = WorldEditBrush.getSchematicBrush(player);
brush.ifPresent(b -> {
    BrushSettings settings = b.settings();
    BrushPaste next = b.nextPaste();
});
```

To bind a saved brush preset to the item in a player's hand, load its snapshot into a builder and build it:

```java
var sbr = SchematicBrushReborn.instance();
sbr.storageRegistry().activeStorage().brushes()
   .playerContainer(player)
   .get("forest")
   .thenAccept(saved -> saved.ifPresent(brush -> Bukkit.getScheduler().runTask(plugin, () -> {
       SchematicBrush built = brush.snapshot()
               .load(player, sbr.brushSettingsRegistry(), sbr.schematics())
               .build(plugin, player);
       WorldEditBrush.setBrush(player, built);
   })));
```

Storage lookups return `CompletableFuture`s and may complete on another thread, so switch back to the main thread
before you touch the player. `WorldEditBrush.setBrush(player, stack, brush, permission)` binds to a given item and
lets you choose the permission needed to use the brush. Without it, `schematicbrush.brush.use` is required.

## Version 3.0 changes for add-on authors

If your add-on was built for version 2, these are the main changes:

- Paper only, FastAsyncWorldEdit only, Java 25.
- eldo-util 3 and Jackson 3 (`tools.jackson`). The Jackson annotations stay in `com.fasterxml.jackson.annotation`.
- Settings are saved with Jackson only. Bukkit's `ConfigurationSerializable` is no longer used, and providers no
  longer take a serialization class. Provider constructors are `(name, localizedName, description)`.
- `SettingProvider#inputs` describes the inputs for the dialog editor. See [Dialog inputs](./extending.md#dialog-inputs).
- `SchematicBrush#pasteFake` returns a `PreviewSnapshot`. The old preview types `Changes`, `BlockChangeCollector`,
  `CapturingExtent` and `FakeWorld` are gone from the API.
- `SchematicBrush#settings(BrushSettings)` replaces the settings of a bound brush in place.
