---
title: Schematic folders
sidebar_position: 6
---

# Schematic folders

Schematic Brush Reborn reads schematics from folders called **sources**. You set them up in the `schematicConfig`
section of `config.yml`.

## The default sources

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
```

So out of the box, schematics saved with FastAsyncWorldEdit can be used right away. A source whose folder does not
exist is skipped without an error.

## A source

| Key | Meaning |
|---|---|
| `path` | The folder to read. Use `/` between folder names. |
| `relative` | `true`: `path` starts in the server's `plugins` folder. `false`: `path` is a full path anywhere on the machine. |
| `prefix` | A short name for the source. Only used when `pathSourceAsPrefix` is `true`. Keep it unique. |
| `excludedPath` | Folders inside this source whose schematics are skipped. |

Every source is read with all its sub folders. Each file is checked for its format. Every format that
FastAsyncWorldEdit can read works. Files it cannot read are skipped with a warning in the log.

To add a folder, add an entry to the list:

```yaml
  - path: "/srv/shared/schematics"
    prefix: "shared"
    relative: false
    excludedPath: []
```

### Excluding folders

`excludedPath` lists folders inside the source, written relative to the source's `path`. The schematics directly in
such a folder are skipped. Folders below it are still read, so list them too if you want to skip them.

```yaml
    excludedPath:
    - "old"
    - "old/unused"
```

## Folder names in selectors

The [Directory selector](./brushes/selectors.md#directory) picks schematics by folder. Folder paths are written
relative to the source, so `plugins/FastAsyncWorldEdit/schematics/trees/oak` becomes `trees/oak`.

- `pathSeparator` is the character between folder names in selectors and tab completion. The default is `/`. Only
  the first character counts.
- Spaces in folder names become `_`.
- If two sources have a folder with the same path, for example `trees`, their contents are merged.

Set `pathSourceAsPrefix: true` to keep sources apart. Folder paths then start with the source prefix:
`fawe/trees` and `we/trees` are two different folders.

## Folders of one player

A folder whose path contains a player's UUID belongs to that player. Only that player's selectors find the
schematics in it, and the UUID is left out of the folder path.

## New and deleted files

Schematic Brush Reborn watches the source folders while the server runs:

- A new file is added about five seconds after it appears.
- A new folder is watched from then on.
- A deleted file is removed.

Brushes and schematic sets keep the schematics their selectors found. To pick up new files in a brush, use
**Refresh Schematics** in the editor or `/sbr refreshSchematics` while you hold it.

After you change the sources in `config.yml`, restart the server.

## Checking what was loaded

The log shows each source as it is loaded. For more detail, set `logLevel: DEBUG` in `base_configuration.yml`. The
log then also names every folder that is read. Files in a format that cannot be read are always logged as a
warning.

`/sbra debug` uploads a report that lists every source as found or missing, and the number of schematics and
folders.
