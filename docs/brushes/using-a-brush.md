---
title: Using a brush
sidebar_position: 4
---

# Using a brush

Once a brush is bound, you paste with it like with any WorldEdit brush. This page shows what else you can do while
you hold it.

## Paste

Right click to paste the next schematic at the block you aim at. How far you can reach and which blocks you can
aim at follow the settings of the WorldEdit brush tool.

Using a bound brush needs the permission `schematicbrush.brush.use`.

The [preview](../preview.md) shows the next schematic before you click. Right after a paste it pauses for a short
moment. Server owners set that pause with `pasteBreak` in the [configuration](../configuration.md#general-settings).

## Change the next paste

With the brush in your main hand you can change the next paste without opening the editor:

| Action | Effect |
|---|---|
| Left click into the air | Next rotation |
| Sneak and left click into the air | Next flip |
| Press the swap hands key (`F` by default) | Skip to another schematic |
| Sneak and press the swap hands key | Next offset |

The brush stays in your main hand; the swap does not happen.

Stepping only works when the modifier has more than one value, which means a **List**, **Random** or **Range**
provider. With a **Fixed** value the action bar says that it cannot be changed. If both the set and the brush have
such a modifier, both step.

## Skip back and forth

These commands do the same from chat. They are handy for key binds and macros.

| Command | Effect |
|---|---|
| `/sbrm next` | Skip to another schematic |
| `/sbrm previous` | Go back to the schematic before |

The brush remembers the last 100 schematics for `/sbrm previous`.

## Selection modes

The selection mode decides how the brush picks the next schematic. Change it with `/sbrm selection <mode>` while
you hold the brush.

| Mode | Behaviour |
|---|---|
| `random` | Picks a new random schematic after each paste. This is the default. |
| `ordered` | Goes through the schematics in order, set by set. |
| `locked_random` | Keeps the same schematic until you skip. Skipping picks a random one. |
| `locked_ordered` | Keeps the same schematic until you skip. Skipping takes the next one in order. |

The locked modes are useful when you want to place the same schematic several times, then move on.

Within a set, the order is alphabetical, with numbers at the end of a name sorted as numbers: `tree_2` comes before
`tree_10`.

The selection mode belongs to the bound brush. It is not stored in brush presets.

:::warning
In version 3.0.0, `ordered` still picks at random, like `random`. `locked_ordered` does keep the order when you skip.
:::

## Show the name of each paste

`/sbrs showNames true` shows the name of each pasted schematic, its set and the values used.
`/sbrs showNames false` turns it off.

A second value picks where the text appears: `ACTION_BAR`, `CHAT`, `TITLE` or `SUB_TITLE`. Without it, the default
from the config is used.

The setting is not saved. Server owners can turn it on for everyone with `showNameDefault` in the
[configuration](../configuration.md#general-settings).

## Refresh the schematics

A bound brush remembers which schematics its selectors found. When you add new files, run
`/sbr refreshSchematics` while you hold the brush. Its selectors then run again.

## Edit the brush

Run `/sbr` while you hold the brush. The [editor](./editor.md) opens with this brush, and every change goes into it
right away.
