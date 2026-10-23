---
title: Events
sidebar_position: 3
---

# Events

Schematic Brush Reborn fires two Bukkit events around every paste of a schematic brush. Both are in the package
`de.eldoria.schematicbrush.event`.

| Event | When | Cancellable |
|---|---|---|
| `PrePasteEvent` | Before the schematic is pasted | yes |
| `PostPasteEvent` | After the schematic was pasted | no |

Cancelling a `PrePasteEvent` stops the paste. The brush keeps the same schematic for the next try.

Previews do not fire these events. Only real pastes do.

## What the events offer

Both events extend `PasteEvent`:

| Method | Returns |
|---|---|
| `player()` | The player who pasted |
| `schematic()` | The pasted `Schematic`: its name, file, format and sizes |
| `schematicSet()` | The `SchematicSet` the schematic came from, with its selector and modifiers |
| `paste()` | The `BrushPaste` with everything about this paste |

`BrushPaste` gives you, among others:

| Method | Returns |
|---|---|
| `clipboard()` | The loaded WorldEdit clipboard of the schematic |
| `rotation()`, `flip()`, `offsetSet()` | The set's rotation, flip and offset modifiers, or `null` if the set has none |
| `offsetBrush()` | The brush's offset modifier, or `null` |
| `settings()` | The `BrushSettings` of the brush |
| `brush()` | The `SchematicBrush` that pastes |

## Threads

The events may be fired off the main thread. Check `event.isAsynchronous()` and switch to the main thread before you
use Bukkit methods that need it.

## Listening

Listen to `PrePasteEvent` or `PostPasteEvent` directly. Each has its own handler list, so a listener for the base
class `PasteEvent` does not receive them.

```java
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PasteListener implements Listener {
    @EventHandler
    public void onPrePaste(PrePasteEvent event) {
        if (event.schematic().name().startsWith("forbidden_")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPostPaste(PostPasteEvent event) {
        var name = event.schematic().name();
        var player = event.player().getName();
        event.player().getServer().getLogger().info(player + " pasted " + name);
    }
}
```

The [Survival Schematic Brush](/schematic-brush-reborn/survival) add-on uses `PrePasteEvent` to take the blocks of
each paste from the player, and [Schematic Tools](/schematic-brush-reborn/tools) listens to both events.
