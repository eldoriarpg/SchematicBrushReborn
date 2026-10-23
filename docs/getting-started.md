---
title: Getting started
sidebar_position: 2
---

# Getting started

This page takes you from an empty plugin folder to your first pasted schematic.

## Install the plugin

1. Check that your server runs **Paper 1.21.11 or newer** on **Java 25 or newer**.
2. Install **FastAsyncWorldEdit**. Schematic Brush Reborn does not start without it.
3. Download the Schematic Brush Reborn jar from [Lyna](https://lyna.eldoria.de/products/1) and put it into the `plugins` folder.
4. Restart the server. Do not use `/reload`.

Floodgate is optional. When it is installed, Bedrock players always get the chat version of the brush editor,
because their client cannot show dialogs.

After the first start you find these files in `plugins/SchematicBrushReborn`:

| File or folder | What it holds |
|---|---|
| `config.yml` | The main settings. See [Configuration](./configuration.md). |
| `base_configuration.yml` | The config version and the log level. |
| `presets.yml`, `brushes.yml` | Saved presets and brush presets. |
| `messages/` | The message files, one per language. |
| `logs/` | A log file with only the messages of this plugin. |

### Coming from version 2

Data from 2.7 loads as it is: your config, presets and brush presets keep working.

- A config written by a version **older than 2.5.0** is refused, and the plugin does not start. Update to the
  latest 2.7 release first, start the server once, then update to 3.0.
- Add-ons built for version 2 do not load on 3.0. Use the add-on versions made for Schematic Brush Reborn 3.

## Add your schematics

Schematic Brush Reborn reads schematics from three folders by default. All paths are relative to the `plugins`
folder:

- `plugins/SchematicBrushReborn/schematics`
- `plugins/FastAsyncWorldEdit/schematics`
- `plugins/WorldEdit/schematics`

Folders that do not exist are skipped. Create `plugins/SchematicBrushReborn/schematics` yourself if you want to
use it. You can sort schematics into sub folders; the brush can pick them by folder later.

New schematic files are noticed while the server runs. They show up after a few seconds. You can add your own
folders in the config. See [Schematic folders](./schematics.md).

## Give permissions

Operators can use everything right away. Other players need at least:

| Permission | For |
|---|---|
| `schematicbrush.brush.use` | Creating and using brushes |
| `schematicbrush.brush.preview` | The preview |
| `schematicbrush.preset.use` | Their own presets, and reading global ones |
| `schematicbrush.brushpreset.use` | Their own brush presets, and reading global ones |

The full list is on the [Permissions](./reference/permissions.md) page.

## Build your first brush

1. Hold an item that is not a block, for example a stick. Brushes cannot be bound to blocks.
2. Type `/sbr`. The brush editor opens.
3. Click **Schematic Sets**, then **Add**. A new set starts with the Name selector `*`, which takes every schematic.
4. Click **Selector** to narrow it down. Choose **Name**, **Regex** or **Directory**, fill in the value and click
   **Apply**. For example, the Name selector `tree*` takes every schematic whose name starts with `tree`.
5. Want variety? Click **+ Rotation** to add a rotation, then click **Rotation** and choose **Random**. Each paste
   is now turned by 0, 90, 180 or 270 degrees.
6. Click **Back** twice to get to the brush menu.
7. Click **Bind**. The chat says how many schematics and sets the brush uses.
8. Right click a block. The schematic is pasted there.

If the preview is on, you see the next schematic as ghost blocks before you click. WorldEdit's `//undo` takes a
paste back, like with any other brush.

:::tip Chat editor
Prefer the old chat menu? Type `/sbrs editor chat`. The editor then shows clickable text in chat instead of a
dialog. See [The brush editor](./brushes/editor.md).
:::

## Next steps

- Learn what a brush is made of: [How brushes work](./brushes/index.md).
- Change the brush while you hold it: [Using a brush](./brushes/using-a-brush.md).
- Save your work: [Presets](./presets.md).
