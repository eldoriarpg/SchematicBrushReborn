---
title: Selectors
sidebar_position: 2
---

# Selectors

A selector decides which schematics a [schematic set](./index.md#schematic-sets) uses. Every set has exactly one
selector. A new set starts with the Name selector `*`, which takes every schematic.

There are three selectors: **Name**, **Regex** and **Directory**. Add-ons can add more.

Selectors look at the **file name without its extension**. The file `oak_tree_1.schem` has the name `oak_tree_1`.

## Name

Picks schematics by their name. Use `*` as a wildcard for any number of characters.

| Selector | Picks |
|---|---|
| `oak_tree_1` | exactly `oak_tree_1` |
| `oak*` | every name that starts with `oak` |
| `*tree*` | every name that contains `tree` |
| `*` | every schematic |

- The whole name must match. `oak` does not pick `oak_tree_1`; `oak*` does.
- Upper and lower case count. `Oak*` does not pick `oak_tree_1`.
- The Name selector searches all folders. Two schematics with the same name in different folders are both picked.
- A value that starts with `^` is read as a regular expression, like the Regex selector.

## Regex

Picks schematics whose name matches a [regular expression](https://regex101.com). A `^` is added to the start if
you leave it out.

| Selector | Picks |
|---|---|
| `rock_\d+` | `rock_1`, `rock_27`, but not `rock_big` |
| `[ob].*_tree` | names that start with `o` or `b` and end with `_tree` |

- The whole name must match.
- Upper and lower case count.

## Directory

Picks the schematics in one folder. The folder path is written relative to the schematic folder it lives in, with
`/` between folder names. For example, the folder `plugins/FastAsyncWorldEdit/schematics/trees/oak` is `trees/oak`.

| Selector | Picks |
|---|---|
| `trees/oak` | everything directly in `trees/oak`, but not in folders below it |
| `trees*` | everything in `trees` and in all folders whose path starts with `trees`, so also `trees/oak` |
| `trees/*` | everything in the folders below `trees`, but not in `trees` itself |

- Folder names are compared without caring about upper and lower case.
- `*` at the end matches by the start of the path. `trees*` therefore also picks a folder named `trees_old`.
- Spaces in folder names become `_`. A folder `big trees` is selected as `big_trees`.

The Directory selector takes an optional second value, the **name filter**. It works like the Name selector, but
only inside the chosen folders. The default is `*`, which keeps everything.

| Directory | Name filter | Picks |
|---|---|---|
| `trees/oak` | `*` | everything in `trees/oak` |
| `trees*` | `*_big` | names ending in `_big`, in `trees` and below |

If several schematic folders contain a folder with the same path, their contents are merged. To keep them apart,
turn on `pathSourceAsPrefix` in the config. Paths then start with the prefix of their schematic folder, for example
`fawe/trees`. See [Schematic folders](../schematics.md).

## Folders of one player

A folder whose path contains a player's UUID belongs to that player. Only that player's selectors find the
schematics inside it. The UUID is left out of the folder path, so the player selects
`<uuid>/trees` simply as `trees`.

## Tips

- A selector must find at least one schematic. The editor refuses a selector that finds nothing.
- A set remembers the schematics its selector found. After adding new files, click **Refresh Schematics** in the
  editor, or run `/sbr refreshSchematics` while holding a bound brush.
- In the chat editor, hover over a set's selector to see a sample of the schematics it found.
