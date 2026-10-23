---
title: Preview
sidebar_position: 5
---

# Preview

The preview shows the next schematic as ghost blocks at the spot you aim at, before you paste. The blocks only
exist on your screen. The world does not change until you click.

The preview follows everything: where you look, the next rotation or flip you step to, and every change you make in
the [editor](./brushes/editor.md).

## Turn it on and off

| Command | Effect |
|---|---|
| `/sbrs preview enable` | Switches the preview on or off |
| `/sbrs preview enable true` | Turns it on |
| `/sbrs preview enable false` | Turns it off |

You need the permission `schematicbrush.brush.preview`.

When `previewDefault` is `true` in the config, the preview turns on by itself when a player with the permission
joins. That is the default. The setting is not saved per player; it starts fresh on each join.

## Watch someone else's preview

Building together? You can see another player's preview too:

| Command | Effect |
|---|---|
| `/sbrs preview subscribe <player>` | Shows you that player's preview |
| `/sbrs preview unsubscribe` | Stops it |

You keep seeing your own preview while you watch someone else. You can watch one other player at a time.
If that player has the preview turned off, the chat tells you so. You see their preview as soon as they turn it on.

## When there is no preview

The preview hides itself when:

- you hold something that is not a schematic brush,
- the spot you aim at is farther away than `renderDistance` blocks (100 by default),
- the schematic is larger than the configured limit. The action bar then shows the size and the limit.
- you just pasted. The preview pauses for `pasteBreak` ticks (20, one second, by default).

A large schematic may also take a moment to appear the first time. Its size is measured in the background first.

Players watching your preview only see it while they are within `renderDistance` blocks of it and in the same
world.

## Ghost blocks that stay or vanish

The client can lose preview blocks, for example when chunks reload or when you click a ghost block. The preview
notices most of these cases and sends the blocks again. On top of that, every preview is sent again completely
every `previewResync` seconds (10 by default).

If previews still look wrong for everyone, an admin can run `/sbra restartRendering`. It clears every preview and
starts the rendering anew.

## Settings for server owners

All preview settings are in the `generalConfig` section of `config.yml`:

| Key | Default | Meaning |
|---|---|---|
| `previewDefault` | `true` | Turn the preview on when a player with the permission joins |
| `previewRefreshInterval` | `1` | How often each preview is updated, in ticks |
| `maxRenderMs` | `25` | Time per tick the preview may use, in milliseconds |
| `maxRenderSize` | `2500` | Largest schematic shown, in blocks. When the brush pastes air, air blocks count too. |
| `maxEffectiveRenderSize` | `2500` | When the brush does not paste air, only blocks other than air count, and the schematic must stay within this and `maxRenderSize` |
| `renderDistance` | `100` | Largest distance between the player and the previewed spot, in blocks |
| `pasteBreak` | `20` | Pause after a paste, in ticks |
| `previewResync` | `10` | Interval for sending every preview again completely, in seconds. `0` turns it off. |

A server tick has 50 milliseconds. If previews make the server lag, lower `maxRenderSize`, lower `maxRenderMs` or
raise `previewRefreshInterval`. `/sbra debug` includes the time the preview needs per tick.

See [Configuration](./configuration.md) for all other settings.
