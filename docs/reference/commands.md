---
title: Commands
sidebar_position: 1
---

# Commands

Every command of Schematic Brush Reborn. Most players only need `/sbr`; the editor runs the other brush commands
for you.

In this list, `<value>` must be given and `[value]` may be left out. Permissions are explained on the
[Permissions](./permissions.md) page.

## Base commands and aliases

| Command | Aliases | Permission | For |
|---|---|---|---|
| `/schematicbrush` | `/sbr`, `/schbr`, `/schembrush` | `schematicbrush.brush.use` | Building and binding brushes |
| `/schematicbrushsettings` | `/sbrs`, `/schbs` | `schematicbrush.brush.use` | Personal settings |
| `/schematicbrushmodify` | `/sbrm`, `/schbrm` | `schematicbrush.brush.use` | Changing the brush in your hand |
| `/schematicbrushpreset` | `/sbrp`, `/schbrp` | `schematicbrush.preset.use` | Managing presets |
| `/schematicbrushbrushpreset` | `/sbrbp`, `/schbrbp` | `schematicbrush.brushpreset.use` | Managing brush presets |
| `/schematicbrushadmin` | `/sbra`, `/schbra` | none, see each command | Server administration |

All commands except the admin commands can only be used by players.

## Brush commands

| Command | Permission | Effect |
|---|---|---|
| `/sbr` | | Opens the [brush editor](../brushes/editor.md). If you hold a schematic brush, the editor edits it. Same as `/sbr create`. |
| `/sbr parse <expression>` | | Builds a brush from the [original SchematicBrush syntax](../brushes/parse.md) and binds it |
| `/sbr loadbrush <name>` | | Loads a brush preset into the editor. Use `g:name` for a global one. |
| `/sbr refreshSchematics` | | Runs the selectors of the brush in your hand again |

All of them need `schematicbrush.brush.use`, like every `/sbr` command.

### Commands the editor uses

The editor runs these commands when you click its buttons. They are hidden from tab completion, but you can type
them, for example in macros. Set ids start at 0.

| Command | Permission | Effect |
|---|---|---|
| `/sbr show` | | Shows the brush screen |
| `/sbr showsets` | | Shows the schematic sets |
| `/sbr showset <id>` | | Shows one set |
| `/sbr bind` | | Binds the brush to the item in your main hand |
| `/sbr clear` | | Removes all sets and resets the brush modifiers |
| `/sbr undo` | | Undoes the last change |
| `/sbr redo` | | Repeats the last undone change |
| `/sbr addset` | | Adds a new set |
| `/sbr removeset <id>` | | Removes a set |
| `/sbr modifyset <id> selector <selector> <values>` | | Changes the selector of a set, for example `/sbr modifyset 0 selector Name oak*` |
| `/sbr modifyset <id> weight <weight>` | | Sets the weight, 1 to 100, or `-1` for auto |
| `/sbr modifyset <id> <modifier> <provider> [values]` | | Changes a set modifier, for example `/sbr modifyset 0 Rotation List 0 90` |
| `/sbr addsetmodifier <id> <modifier>` | | Adds a set modifier with its default value |
| `/sbr removesetmodifier <id> <modifier>` | | Removes a set modifier |
| `/sbr modify <modifier> <provider> [values]` | | Changes a brush modifier, for example `/sbr modify Placement Drop` |
| `/sbr addbrushmodifier <modifier>` | | Adds a brush modifier with its default value |
| `/sbr removebrushmodifier <modifier>` | | Removes a brush modifier. Placement, Include Air and Replace All cannot be removed. |
| `/sbr refreshSchematics session` | | Runs the selectors of the sets in the editor again |
| `/sbr addpreset <name>` | | Adds the sets of a preset. Use `g:name` for a global one. |
| `/sbr savepreset <name> [-g] [-f]` | `schematicbrush.preset.use` | Saves the sets as a preset. `-g` saves it globally and needs `schematicbrush.preset.global`. `-f` overwrites. |
| `/sbr savebrush <name> [-g] [-f]` | `schematicbrush.brushpreset.use` | Saves the brush as a brush preset. `-g` saves it globally and needs `schematicbrush.brushpreset.global`. `-f` overwrites. |

Modifier and provider names are the ones on the [Modifiers](../brushes/modifiers.md) page: `Placement`,
`IncludeAir`, `ReplaceAll`, `Offset`, `Rotation`, `Flip` and `Filter`. The provider of `IncludeAir` and `ReplaceAll`
is `Fixed`, as in `/sbr modify ReplaceAll Fixed true`. The provider of `Filter` is `BlockFilter`.
Modifier names in `modify` and `modifyset` must be written with this exact case.

## Settings

| Command | Permission | Effect |
|---|---|---|
| `/sbrs editor [dialog\|chat]` | | Chooses the [editor](../brushes/editor.md). Without a value it switches to the other one. |
| `/sbrs showNames <true\|false> [channel]` | | Shows the name of every pasted schematic. Channels: `ACTION_BAR`, `CHAT`, `TITLE`, `SUB_TITLE`. |
| `/sbrs preview enable [true\|false]` | `schematicbrush.brush.preview` | Turns the [preview](../preview.md) on or off. Without a value it switches. |
| `/sbrs preview subscribe <player>` | `schematicbrush.brush.preview` | Shows you another player's preview |
| `/sbrs preview unsubscribe` | `schematicbrush.brush.preview` | Stops showing another player's preview |

## Changing the brush in your hand

These commands work on the schematic brush in your main hand. See [Using a brush](../brushes/using-a-brush.md).

| Command | Effect |
|---|---|
| `/sbrm next` | Skips to another schematic |
| `/sbrm previous` | Goes back to the schematic before |
| `/sbrm selection <mode>` | Changes the selection mode: `random`, `ordered`, `locked_random` or `locked_ordered` |

## Presets

See [Presets](../presets.md). Names of global presets start with `g:` where noted.

| Command | Permission | Effect |
|---|---|---|
| `/sbrp` | | Lists your private presets. Same as `/sbrp list private`. |
| `/sbrp list private [page]` | | Lists your private presets |
| `/sbrp list global [page]` | | Lists the global presets |
| `/sbrp info <name>` | | Shows a preset. Use `g:name` for a global one. |
| `/sbrp descr <name> <description>` | | Changes the description. `g:name` needs `schematicbrush.preset.global`. |
| `/sbrp remove <name> [-g]` | | Removes a preset. `-g` removes a global one and needs `schematicbrush.preset.global`. |

Page numbers start at 0. All of them need `schematicbrush.preset.use`.

## Brush presets

| Command | Permission | Effect |
|---|---|---|
| `/sbrbp` | | Lists your private brush presets. Same as `/sbrbp list private`. |
| `/sbrbp list private [page]` | | Lists your private brush presets |
| `/sbrbp list global [page]` | | Lists the global brush presets |
| `/sbrbp info <name>` | | Shows a brush preset. Use `g:name` for a global one. |
| `/sbrbp descr <name> <description>` | | Changes the description. `g:name` needs `schematicbrush.preset.global`. |
| `/sbrbp remove <name> [-g]` | | Removes a brush preset. `-g` removes a global one and needs `schematicbrush.brushpreset.global`. |

Page numbers start at 0. All of them need `schematicbrush.brushpreset.use`. Loading a brush preset is done with
`/sbr loadbrush`.

## Admin commands

| Command | Permission | Effect |
|---|---|---|
| `/sbra` | | Shows the version, whether the build is premium, and support links. Same as `/sbra info`. |
| `/sbra reload` | `schematicbrush.admin.reload` | Reloads the configuration and restarts the preview. See [Reloading](../configuration.md#reloading). |
| `/sbra reloadCache` | `schematicbrush.admin.reloadcache` | Reads all schematic folders again |
| `/sbra restartRendering` | `schematicbrush.admin.restartrendering` | Clears every preview and restarts the rendering |
| `/sbra migrate <source> <target>` | `schematicbrush.admin.migrate` | Copies all presets and brush presets from one storage into another, for example `yaml` into `mysql` |
| `/sbra debug` | `schematicbrush.admin.debug` | Uploads a debug report and returns a link to share with support |

The first time `/sbra debug` is used, it explains what is uploaded and asks you to run it again.

## Updates

| Command | Who | Effect |
|---|---|---|
| `/schematicbrushreborn:eldo-update [download\|enable-auto\|later\|never]` | Console, operators and holders of `schematicbrush.updates` | Handles the buttons of the update message |

You do not need to type it; the update message has buttons for it. See [Updates](../configuration.md#updates).
