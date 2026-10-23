---
title: Configuration
sidebar_position: 7
---

# Configuration

Schematic Brush Reborn works without any changes. This page lists every file and setting in case you want to adjust
something.

All files are in `plugins/SchematicBrushReborn`:

| File | Content |
|---|---|
| `config.yml` | The main settings, explained below |
| `base_configuration.yml` | Config version and log level |
| `messages/` | The message files, one per language |
| `presets.yml` | Saved presets, when the YAML storage is used |
| `brushes.yml` | Saved brush presets, when the YAML storage is used |
| `logs/` | A log file with only the messages of this plugin |

## config.yml

A fresh `config.yml` looks like this:

```yaml
schematicConfig:
  sources:
  - path: "SchematicBrushReborn/schematics"
    prefix: "sbr"
    relative: true
    excludedPath: []
  - path: "FastAsyncWorldEdit/schematics"
    prefix: "fawe"
    relative: true
    excludedPath: []
  - path: "WorldEdit/schematics"
    prefix: "we"
    relative: true
    excludedPath: []
  pathSeparator: "/"
  pathSourceAsPrefix: false
generalConfig:
  storageType: "yaml"
  previewDefault: true
  showNameDefault: false
  defaultNameChannel: "ACTION_BAR"
  previewRefreshInterval: 1
  maxRenderMs: 25
  maxRenderSize: 2500
  maxEffectiveRenderSize: 2500
  renderDistance: 100
  pasteBreak: 20
  previewResync: 10
  language: "en_US"
  editor: "DIALOG"
updates:
  notice: true
  autoUpdate: false
  askForAutoUpdate: true
```

### Schematic sources

The `schematicConfig` section says where schematics are read from.

| Key | Default | Meaning |
|---|---|---|
| `sources` | three sources, see above | The folders schematics are read from |
| `sources[].path` | | The folder of one source |
| `sources[].relative` | `true` | `true`: `path` starts in the `plugins` folder. `false`: `path` is a full path. |
| `sources[].prefix` | | Short name of the source, used by `pathSourceAsPrefix` |
| `sources[].excludedPath` | `[]` | Folders inside the source to skip |
| `pathSeparator` | `/` | Character between folder names in selectors. Only the first character counts. |
| `pathSourceAsPrefix` | `false` | Put the source prefix in front of every folder path, such as `fawe/trees` |

[Schematic folders](./schematics.md) explains these settings with examples.

### General settings

The `generalConfig` section holds everything else.

| Key | Default | Meaning |
|---|---|---|
| `storageType` | `yaml` | Where presets and brush presets are stored. See [Storage](#storage). |
| `previewDefault` | `true` | Turn the preview on when a player with `schematicbrush.brush.preview` joins |
| `showNameDefault` | `false` | Show the name of each pasted schematic to players with `schematicbrush.brush.use` when they join |
| `defaultNameChannel` | `ACTION_BAR` | Where names are shown: `ACTION_BAR`, `CHAT`, `TITLE` or `SUB_TITLE` |
| `previewRefreshInterval` | `1` | How often each preview is updated, in ticks |
| `maxRenderMs` | `25` | Time per tick the preview may use, in milliseconds |
| `maxRenderSize` | `2500` | Largest schematic the preview shows, in blocks. Air counts when the brush pastes air. |
| `maxEffectiveRenderSize` | `2500` | Largest schematic the preview shows when the brush does not paste air, counting only blocks other than air |
| `renderDistance` | `100` | Largest distance between a player and the previewed spot, in blocks |
| `pasteBreak` | `20` | Pause of the preview after a paste, in ticks |
| `previewResync` | `10` | Send every preview again completely after this many seconds. `0` turns it off. |
| `language` | `en_US` | Language of the messages: `en_US`, `de_DE` or `zh_TW` |
| `editor` | `DIALOG` | Brush editor for players who did not choose one: `DIALOG` or `CHAT` |

The [Preview](./preview.md) page explains the preview settings in more detail.

### Updates

The `updates` section controls the update check. The plugin looks for new versions when it starts and every six
hours after that.

| Key | Default | Meaning |
|---|---|---|
| `notice` | `true` | Tell the console and players with `schematicbrush.updates` about a new version |
| `autoUpdate` | `false` | Premium builds only: download new versions by themselves. They take effect on the next start. |
| `askForAutoUpdate` | `true` | Premium builds only: ask admins in chat whether to turn on `autoUpdate` |

- A new major version is never installed automatically. You confirm it yourself.
- The buttons in the update message run `/schematicbrushreborn:eldo-update`. Your choice is written to the config.
- `/sbra info` shows whether your build is a premium build.

Configs from version 2 had a `checkUpdates` switch. It is read once as the starting value of `notice` and then
dropped.

## base_configuration.yml

On a fresh install of 3.0.0 the file looks like this:

```yaml
version: 2
lastInstalledVersion: "3.0.0"
logLevel: "INFO"
```

| Key | Meaning |
|---|---|
| `version` | Version of the config layout. Do not change it. |
| `lastInstalledVersion` | The plugin version that created the file |
| `logLevel` | How much the plugin logs. `INFO` is normal. Set `DEBUG` when you need more detail, for example when a support member asks for it. |

Other accepted log levels are `OFF`, `SEVERE`, `WARNING`, `FINE`, `FINER`, `FINEST` and `ALL`. Unknown values count as
`INFO`.

## Messages

The `messages` folder holds one file per language: `messages_en_US.properties`, `messages_de_DE.properties` and
`messages_zh_TW.properties`. Pick the language with `language` in `config.yml`. The Traditional Chinese file only
covers part of the messages.

You can edit the texts in these files. When an update brings new messages, the missing keys are added to your files
on the next start. Your own changes stay.

Restart the server after you change the language or a message file.

## Storage

`storageType` decides where presets and brush presets are kept.

- `yaml`, the default, uses `presets.yml` and `brushes.yml`. They are written after every change. Edit them only
  while the server is stopped.
- Add-ons can add more storage types. The [Schematic Brush Database](/schematic-brush-reborn/database) add-on adds
  database storages.

If `storageType` names a storage that does not exist, the plugin warns in the log and uses `yaml`.

To copy everything from one storage into another, use `/sbra migrate <source> <target>`, for example
`/sbra migrate yaml mysql`. Entries with the same name are overwritten in the target; nothing is deleted. Then set
`storageType` to the new storage. Storage type names are case sensitive.

## Reloading

`/sbra reload` reads the configuration again, loads the schematics from the configured sources, restarts the preview
and applies the update settings. The language and message files still need a restart. Reloading can also upset
add-ons, so a restart is the safer choice.

## Metrics

Schematic Brush Reborn sends anonymous usage numbers to bStats, such as the number of schematics and which
add-ons are installed. You can turn bStats off for all plugins in `plugins/bStats/config.yml`.
