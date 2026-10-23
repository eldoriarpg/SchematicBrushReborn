---
title: Permissions
sidebar_position: 2
---

# Permissions

Operators have every permission by default. Other players get nothing until you grant it.

## Players

| Permission | Allows |
|---|---|
| `schematicbrush.brush.use` | Creating, editing and using schematic brushes: `/sbr`, `/sbrs` and `/sbrm`. Also needed to paste with a bound brush. |
| `schematicbrush.brush.preview` | The [preview](../preview.md): `/sbrs preview`. With `previewDefault` on, the preview starts on join for players with this permission. |
| `schematicbrush.preset.use` | Private [presets](../presets.md): saving, adding and managing them with `/sbrp`. Also reading and adding global presets. |
| `schematicbrush.preset.global` | Saving global presets, changing their description and removing them |
| `schematicbrush.brushpreset.use` | Private brush presets: saving, loading and managing them with `/sbrbp`. Also reading and loading global brush presets. |
| `schematicbrush.brushpreset.global` | Saving and removing global brush presets |

A good start for builders is `schematicbrush.brush.use`, `schematicbrush.brush.preview`,
`schematicbrush.preset.use` and `schematicbrush.brushpreset.use`.

## Admins

| Permission | Allows |
|---|---|
| `schematicbrush.admin.reload` | `/sbra reload`. Reloading may upset add-ons. |
| `schematicbrush.admin.reloadcache` | `/sbra reloadCache` |
| `schematicbrush.admin.restartrendering` | `/sbra restartRendering` |
| `schematicbrush.admin.migrate` | `/sbra migrate` |
| `schematicbrush.admin.debug` | `/sbra debug` |
| `schematicbrush.updates` | Hearing about new versions on join and using the buttons of the update message |

`/sbra info` needs no permission.

## Wildcard

`schematicbrush.*` grants every permission on this page.

## Permissions of add-ons

Selectors and modifiers from add-ons can have their own permission. Both editors and tab completion hide those you
may not use, and setting one without the permission fails in every command.
See the add-on pages for their permissions:

- [Schematic Brush Database](/schematic-brush-reborn/database)
- [Schematic Tools](/schematic-brush-reborn/tools)
- [Survival Schematic Brush](/schematic-brush-reborn/survival)
