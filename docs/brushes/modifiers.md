---
title: Modifiers
sidebar_position: 3
---

# Modifiers

Modifiers change how a schematic is pasted. There are two kinds:

- **Brush modifiers** apply to every paste of the brush.
- **Set modifiers** apply only to the schematics of one [schematic set](./index.md#schematic-sets).

| Modifier | Brush | Set | Always on the brush | Default |
|---|---|---|---|---|
| [Placement](#placement) | yes | | yes | Drop |
| [Include Air](#include-air) | yes | | yes | false |
| [Replace All](#replace-all) | yes | | yes | false |
| [Offset](#offset) | yes | yes | | Fixed 0 when added |
| [Rotation](#rotation) | yes | yes | | Fixed 0 when added |
| [Flip](#flip) | yes | yes | | Fixed none when added |
| [Filter](#filter) | yes | | | no filter when added |

Modifiers that are not always on the brush can be added and removed in the [editor](./editor.md). When a set and
the brush both have an offset, rotation or flip, both apply. Offsets add up. Rotations and flips are combined.

## Fixed, List, Random and Range

Most modifiers can be filled in different ways. These are called providers.

| Provider | Meaning |
|---|---|
| **Fixed** | Always the same value. |
| **List** | You give several values. Each paste picks one of them at random. |
| **Random** | Each paste picks one of all possible values at random. |
| **Range** | Offsets only. Each paste picks a number between a lowest and a highest value, both included. |

List, Random and Range pick a new value after every paste. While you hold the brush you can also step through the
values by hand. See [Using a brush](./using-a-brush.md#change-the-next-paste).

## Placement

Placement decides which height of the schematic lands on the block you aim at. The schematic is always centered
on that block from the sides.

| Provider | The block you aim at gets |
|---|---|
| **Drop** | the lowest layer that contains a block other than air. This is the default. |
| **Bottom** | the lowest layer of the schematic, even if it is only air. |
| **Middle** | the middle height of the schematic. |
| **Raise** | the highest layer that contains a block other than air. |
| **Top** | the height just above the schematic. |
| **Original** | the point where the schematic was copied from. Nothing is centered. |

Drop is usually what you want for trees and rocks: they stand on the ground, even if the schematic has some empty
layers at the bottom.

## Include Air

`true` pastes the air blocks of the schematic too. `false` leaves them out.

Include Air only has an effect together with **Replace All**. Without Replace All, only air is replaced, so pasting
air changes nothing.

## Replace All

`false` only replaces air (including cave air and void air). Existing blocks stay where they are. This is the
default.

`true` replaces every block in the way.

## Offset

Moves the schematic up or down by a number of blocks. Positive numbers go up, negative numbers go down.

| Provider | Values | Example |
|---|---|---|
| **Fixed** | one number from -100 to 100 | `-2` sinks the schematic two blocks into the ground |
| **List** | several numbers from -100 to 100 | `0 -1 -2` picks one of the three |
| **Range** | a lowest and a highest number | `-3` and `0` picks a number from -3 to 0 |

A set offset and a brush offset add up.

## Rotation

Turns the schematic around its vertical axis.

| Provider | Values |
|---|---|
| **Fixed** | `0`, `90`, `180` or `270` |
| **List** | several of `0`, `90`, `180` and `270`. Each paste picks one. |
| **Random** | one of `0`, `90`, `180` and `270` |

## Flip

Mirrors the schematic.

| Provider | Values |
|---|---|
| **Fixed** | `none`, `north`, `east` or `up` |
| **List** | several of these. Each paste picks one. |
| **Random** | one of `none`, `north` and `east`. Random never flips upside down. |

- `north` mirrors along the north and south axis. You can also write `N`, `S`, `NS`, `SN` or `south`.
- `east` mirrors along the east and west axis. You can also write `E`, `W`, `EW`, `WE` or `west`.
- `up` turns the schematic upside down. You can also write `U`, `D`, `UD`, `DU` or `down`.

## Filter

Limits which blocks of the schematic are pasted. The filter is a WorldEdit mask, such as `stone,dirt`. Only blocks
of the schematic that match the mask are pasted.

Use WorldEdit's negation to leave blocks out. `!oak_leaves`, for example, pastes everything except oak leaves.
`none` removes the filter again.
