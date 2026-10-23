---
title: Schematic Brush Reborn
sidebar_position: 1
---

# Schematic Brush Reborn

Schematic Brush Reborn turns your schematics into a WorldEdit brush. You bind the brush to an item and click.
Each click places a schematic where you look. The next click can pick another schematic, turn it, flip it or
move it up or down. Forests, rock fields and ruins stop looking copied and pasted.

The plugin is a remake of the original [SchematicBrush](https://github.com/mikeprimm/SchematicBrush). It keeps the
idea and makes it much easier to use. You click your brush together in a menu instead of typing long commands.

## What it can do

- **Pick schematics your way.** Choose them by name, by folder or with a regular expression. See
  [Selectors](./brushes/selectors.md).
- **Mix several groups of schematics.** A brush holds one or more schematic sets. Weights decide how often each set
  is used. See [How brushes work](./brushes/index.md).
- **Control every paste.** Set the placement, an offset, rotation, flip, whether air is pasted, whether blocks are
  replaced, and a block filter. See [Modifiers](./brushes/modifiers.md).
- **Edit in a menu.** The brush editor opens as a Minecraft dialog or as clickable chat. Changes reach the brush in
  your hand right away, and you can undo and redo them. See [The brush editor](./brushes/editor.md).
- **Save what you like.** Keep schematic sets as presets and whole brushes as brush presets, for yourself or for
  everyone. See [Presets](./presets.md).
- **See before you paste.** A preview shows the next schematic at the spot you aim at. See [Preview](./preview.md).
- **Bring your old brushes.** `/sbr parse` reads the syntax of the original SchematicBrush. See
  [Original SchematicBrush syntax](./brushes/parse.md).
- **Build on it.** Add your own selectors, modifiers, schematic sources and storages through the API. See
  [Developer API](./developers/api.md).

## Requirements

| What | Version |
|---|---|
| Server | Paper 1.21.11 or newer |
| Java | 25 or newer |
| FastAsyncWorldEdit | required |
| Floodgate | optional, so Bedrock players get the chat editor |

:::note Paper and FastAsyncWorldEdit only
Version 3.0 runs on Paper and needs FastAsyncWorldEdit. Spigot and plain WorldEdit are not supported anymore.
:::

## Add-ons

Four add-ons extend Schematic Brush Reborn. Each one has its own section in these docs.

- **[Schematic Brush Database](/schematic-brush-reborn/database)** stores presets and brush presets in a MariaDB,
  MySQL or PostgreSQL database instead of files, so several servers can share them.
- **[Schematic Tools](/schematic-brush-reborn/tools)** binds saved brushes to tools that other players can use, with
  their own permission and an optional usage limit.
- **[Survival Schematic Brush](/schematic-brush-reborn/survival)** takes the blocks for each paste from the player,
  so the brush can be used in survival.
- **[Grid Selector](/schematic-brush-reborn/grid-selector)** turns builds on a grid of plots into the schematics of a
  brush. The add-on lays out the plots, or uses the plots of a PlotSquared world.

## Where to go next

- New here? Start with [Getting started](./getting-started.md).
- Setting up a server? Read [Configuration](./configuration.md) and [Permissions](./reference/permissions.md).
- Looking for a command? See [Commands](./reference/commands.md).

## Help

Questions, bugs and ideas are welcome on the support Discord: [sbr.discord.eldoria.de](https://sbr.discord.eldoria.de).
