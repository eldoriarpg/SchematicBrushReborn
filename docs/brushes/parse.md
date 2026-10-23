---
title: Original SchematicBrush syntax
sidebar_position: 5
---

# Original SchematicBrush syntax

The original SchematicBrush plugin built brushes from one line of text. `/sbr parse` reads that syntax, so your old
brushes keep working:

```text
/sbr parse tree*@*:5 rock@90N^-1 -place:drop -incair
```

The command builds the brush, binds it to the item in your main hand and connects the [editor](./editor.md) to it.
Run `/sbr` afterwards to fine tune it in the editor. Tab completion helps with every part of the line.

`/sbr parse` understands a few things the original did not. They are marked as **new** below.

## How a line is built

A line is a list of items, separated by spaces. Each item is one of:

- a **definition**, which becomes one schematic set,
- a **preset**, written `&name`, which adds the sets of a saved [preset](../presets.md),
- an **option**, written `-name` or `-name:value`, which sets something for the whole brush.

You need at least one definition or one preset.

## Definitions

A definition starts with the schematics to use, followed by optional parts in any order. Each part may appear
once.

```text
name @rotation-and-or-flip :weight ^offset #format
```

Written without the spaces, for example `oak*@90N:5^-1`. Everything after the name is optional.

### Which schematics

| You write | Schematic Brush Reborn uses |
|---|---|
| `oak_tree` | the Name selector `oak_tree` |
| `oak*` | the Name selector `oak*`. `*` stands for any characters. |
| `oak_tree_?` | a Regex selector. `?` stands for exactly one character. |
| `^oak_tree_\d+` | the Regex selector `^oak_tree_\d+` |
| `trees/oak` | the Directory selector `trees`, with the name filter `oak` |
| `trees/` | the Directory selector `trees` |
| `trees/**` | **new:** the Directory selector `trees*`, which includes all folders below |

A definition that finds no schematic stops the command with an error that marks it.

See [Selectors](./selectors.md) for how names and folders are matched.

### Rotation and flip

Rotation and flip both follow an `@`. You can give a rotation, a flip, or a rotation followed by a flip.

| Part | Meaning |
|---|---|
| `@90` | Fixed rotation. Allowed are multiples of 90. |
| `@*` | Random rotation |
| `@90,180` | **new:** a list of rotations. Each paste picks one. |
| `@N` or `@S` | Flip along the north and south axis |
| `@E` or `@W` | Flip along the east and west axis |
| `@U` or `@D` | **new:** flip upside down |
| `@NE` | **new:** several flip letters make a list. Each paste picks one. |
| `@*` after a rotation, such as `@90*` | Random flip: none, north and south, or east and west |

A lone `@*` is a random rotation. To get a random flip, put it after a rotation, as in `@0*`. `@**` gives a random
rotation and a random flip.

Do not put spaces inside a definition. A space starts the next item.

### Weight

`:5` gives the set a weight of 5. Weights go from 1 to 100. Without a weight the set uses auto. See
[Weight](./index.md#weight).

### Offset

| Part | Meaning |
|---|---|
| `^-1` | Fixed offset of -1 |
| `^-2..1` | **new:** a range. Each paste picks a number from -2 to 1. |
| `^0,-1,-2` | **new:** a list. Each paste picks one. |

Offsets go from -100 to 100.

### Format

`#schematic` named the file format in the original plugin. Schematic Brush Reborn finds schematics in every format
FastAsyncWorldEdit can read, so the format is ignored. The chat tells you so.

## Presets

`&forest` adds the schematic sets of your preset `forest`. Use `&g:forest` for a global preset. You can mix presets
and definitions in one line.

## Options

| Option | Meaning |
|---|---|
| `-incair` | Paste the air of the schematics. Sets [Include Air](./modifiers.md#include-air) to true. |
| `-replaceall` | Replace existing blocks. Sets [Replace All](./modifiers.md#replace-all) to true. |
| `-place:drop` | The [placement](./modifiers.md#placement). See the table below. |
| `-yoff:2` | Offset of the whole brush. Takes the same forms as `^`: **new** ranges and lists. |
| `-rot:90` | **new:** rotation of the whole brush, same forms as after `@` |
| `-flip:N` | **new:** flip of the whole brush, same forms as after `@` |
| `-filter:stone,dirt` | **new:** a WorldEdit mask as [filter](./modifiers.md#filter). The mask cannot contain spaces. |

Placements for `-place:`

| Value | Placement |
|---|---|
| `center` or `middle` | Middle |
| `bottom` | Bottom |
| `drop` | Drop |
| `raise` | Raise, **new** |
| `top` | Top, **new** |
| `original` | Original, **new** |

Without `-place`, the brush uses **Middle**, as the original plugin did. This differs from brushes built in the
editor, which start with Drop.

Without `-incair` and `-replaceall`, air is skipped and only air is replaced, as in the original.

## Errors

When something is wrong, the reply explains the problem and underlines the part of the line that caused it. For
example, `stone@45` is refused because 45 is not a multiple of 90.

## Examples

| Line | Brush |
|---|---|
| `/sbr parse oak*@*` | Every schematic starting with `oak`, randomly rotated |
| `/sbr parse tree*:3 bush*:1 -place:drop` | Trees three times as often as bushes, dropped onto the ground |
| `/sbr parse rocks/**^-2..0 -replaceall` | Everything in `rocks` and below, sunk up to two blocks, replacing blocks |
| `/sbr parse &forest -rot:*` | The sets of your preset `forest`, randomly rotated |
