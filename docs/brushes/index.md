---
title: How brushes work
---

# How brushes work

A schematic brush has two parts:

- one or more **schematic sets**, which decide *which* schematics can be pasted, and
- the **brush modifiers**, which decide *how* every paste of the brush is placed.

## Schematic sets

A schematic set is a group of schematics with its own settings. It has:

- a **selector**, which picks the schematics. For example "every schematic whose name starts with `tree`", or
  "everything in the folder `rocks`". See [Selectors](./selectors.md).
- **set modifiers**, which change the schematics of this set only: rotation, flip and offset. See
  [Modifiers](./modifiers.md).
- a **weight**, which decides how often this set is used compared to the other sets.

A brush with one set simply pastes schematics from that set. With several sets you can mix things. For example,
a forest brush could have one set for big trees, one for small trees and one for bushes.

### Weight

The weight is a number from 1 to 100. A set with weight 20 is picked twice as often as a set with weight 10.

A set can also leave its weight on **auto**. Auto sets get the average weight of all sets that have one. If no set
has a weight, every set counts the same.

| Set | Weight | Used for the paste |
|---|---|---|
| Big trees | 10 | 10 |
| Small trees | 30 | 30 |
| Bushes | auto | 20, the average of 10 and 30 |

In the dialog editor, a weight of `0` means auto. In commands and in the chat editor, auto is `-1`.

## Brush modifiers

Brush modifiers apply to every paste, whatever set the schematic comes from:

- **Placement**, **Include Air** and **Replace All** are always there.
- **Offset**, **Rotation**, **Flip** and **Filter** can be added when you need them.

Rotation, flip and offset exist on both levels. When a set and the brush both have one, both apply: offsets add
up, and rotations and flips are combined. All values are explained on the [Modifiers](./modifiers.md) page.

## What happens on a paste

1. The brush picks a set. Sets with a higher weight are picked more often.
2. It picks a schematic from that set. By default the pick is random. Other selection modes are on
   [Using a brush](./using-a-brush.md#selection-modes).
3. Modifiers that have several possible values pick one. A random rotation, for example, picks one of the four
   directions.
4. The brush modifiers and then the set modifiers are applied.
5. The schematic is pasted at the block you aim at.

The next schematic is chosen right after a paste. That is what the [Preview](../preview.md) shows.

## Brushes belong to item types

WorldEdit binds tools to an item *type*, not to one item. When you bind a brush to a stick, every stick in your
inventory carries that brush. Binding another brush to a stick replaces it. Brushes cannot be bound to blocks.

A brush is bound for you only. Other players need their own brush. To hand out ready made brushes, look at the
[Schematic Tools](/schematic-brush-reborn/tools) add-on.

## Building a brush

You build brushes in the [brush editor](./editor.md), which opens with `/sbr`. If you know the syntax of the
original SchematicBrush, you can also write a brush in one line with [`/sbr parse`](./parse.md).
