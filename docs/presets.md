---
title: Presets
sidebar_position: 4
---

# Presets

Presets save your work so you can use it again later. There are two kinds:

| Kind | Holds | Command | Use it to |
|---|---|---|---|
| **Preset** | schematic sets, with their selectors, set modifiers and weights | `/sbrp` | add sets to any brush |
| **Brush preset** | a whole brush: its sets and its brush modifiers | `/sbrbp` | load a complete brush |

Both kinds can be **private**, for you only, or **global**, for everyone on the server.

## Presets

A preset is a collection of schematic sets. Think of it as a building block: a "trees" preset, a "rocks" preset, a
"flowers" preset.

**Adding** a preset to a brush adds copies of its sets. The sets already on the brush stay. You can add several
presets to one brush. Changing the sets afterwards does not change the preset.

- Save: **Save Preset** in the sets screen of the [editor](./brushes/editor.md), or `/sbr savepreset <name>`.
- Add: **Add Preset** in the sets screen, or `/sbr addpreset <name>`.

A brush without sets cannot be saved as a preset.

## Brush presets

A brush preset stores the whole brush, including placement, offset, rotation and every other brush modifier.

**Loading** a brush preset replaces everything in the editor with the saved brush. If the editor is connected to a
bound brush, that brush changes too. Loading can be undone in the editor.

- Save: **Save Brush** in the brush screen of the editor, or `/sbr savebrush <name>`.
- Load: **Load Brush** in the brush screen, the **[Load]** button in the brush preset list, or
  `/sbr loadbrush <name>`.

## Private and global

Everything you save is private unless you ask for global.

- **Saving global:** tick **Global** in the dialog, or add `-g` to the command: `/sbr savepreset trees -g`.
- **Using global:** put `g:` in front of the name: `/sbr addpreset g:trees`, `/sbr loadbrush g:forest`.
- **Overwriting:** saving under a name that already exists fails. Tick **Overwrite** in the dialog or add `-f` to
  the command to replace it.

Names cannot contain spaces.

## Managing your presets

`/sbrp` manages presets and `/sbrbp` manages brush presets. Both work the same way.

| Command | Effect |
|---|---|
| `/sbrp` | Lists your private presets |
| `/sbrp list global` | Lists the global presets |
| `/sbrp list private 1` | Shows another page. Pages are counted from 0, so `1` is the second page. |
| `/sbrp info <name>` | Shows a preset with its description and sets |
| `/sbrp descr <name> <text>` | Changes the description |
| `/sbrp remove <name>` | Removes a private preset |
| `/sbrp remove <name> -g` | Removes a global preset |

For `info` and `descr`, put `g:` in front of the name to reach a global preset.

You rarely need to type these. The lists show 15 entries per page and have buttons:

- Hover over a name to see its description and sets.
- **[Info]** shows the details. There, **[Change]** edits the description.
- **[Remove]** deletes the entry. Global entries only show it if you may change global ones.
- Brush presets also have **[Load]**.
- The arrows at the bottom switch pages.

## Permissions

| Permission | Allows |
|---|---|
| `schematicbrush.preset.use` | Saving, adding and managing private presets, and adding global ones |
| `schematicbrush.preset.global` | Saving, changing and removing global presets |
| `schematicbrush.brushpreset.use` | Saving, loading and managing private brush presets, and loading global ones |
| `schematicbrush.brushpreset.global` | Saving and removing global brush presets |

The editor only shows the preset buttons to players with the matching `.use` permission.

## Where presets are stored

By default, presets live in `plugins/SchematicBrushReborn/presets.yml` and brush presets in `brushes.yml`. Both files
are written right after every change.

The [Schematic Brush Database](/schematic-brush-reborn/database) add-on stores them in a database instead, which
lets several servers share them. To move existing presets into another storage, see
[`/sbra migrate`](./reference/commands.md#admin-commands).
