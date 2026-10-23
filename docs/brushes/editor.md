---
title: The brush editor
sidebar_position: 1
---

# The brush editor

The brush editor is where you put a brush together. Open it with `/sbr`.

- If you hold an item with a schematic brush, the editor opens **that brush**. Every change goes into the brush in
  your hand right away.
- If you hold anything else, the editor starts a new, empty brush.

The editor comes in two forms: **dialogs** and **chat**. Both can do the same things.

## Dialog or chat

Dialogs are Minecraft menus with buttons, text fields, toggles and sliders. They are the default. The chat editor
shows clickable text in chat, as earlier versions did.

Switch with `/sbrs editor`:

| Command | Effect |
|---|---|
| `/sbrs editor dialog` | Use dialogs |
| `/sbrs editor chat` | Use chat |
| `/sbrs editor` | Switch to the other one |

Your choice lasts until you leave the server. After that the default from the config applies again. Server owners
set that default with `editor` in the [configuration](../configuration.md#general-settings).

Bedrock players who join through Floodgate always get the chat editor, because their client cannot show dialogs.

## The screens

The editor has three main screens. The dialog and the chat editor use the same ones.

### Brush

The first screen shows the whole brush:

- the schematic sets, with a button to open them,
- each brush modifier with its current value. Click one to change it.
- a **+** button for each brush modifier that is not on the brush yet,
- **Bind**, **Clear**, **Undo** and **Redo**,
- **Load Brush** and **Save Brush**, if you may use [brush presets](../presets.md).

**Bind** puts the brush on the item in your main hand. **Clear** removes all sets and resets the modifiers. Clear
can be undone.

### Schematic sets

This screen lists your sets. Each line shows the selector. Dialogs also show how many schematics it found; in chat,
hover over the line to see the set's modifiers. From here you can:

- open a set to edit it,
- **Add** a new set,
- **Refresh Schematics**, which runs every selector again. Use it after you added new schematic files.
- **Add Preset** and **Save Preset**, if you may use [presets](../presets.md).

In chat, a set is removed with its **Remove** button here. In the dialog editor, the **Remove** button is inside the
set.

### One set

This screen shows a single set: its selector, how many schematics it found, its set modifiers and its weight.
You can change the selector, change or add modifiers, set the weight or remove the set.

A new selector must find at least one schematic, or the editor refuses it.

## Changing a value

Every modifier and the selector can be filled by different **providers**. Rotation, for example, has the providers
Fixed, List and Random. When you click a modifier, the editor lists its providers:

- A provider without values, like **Random**, is applied as soon as you click it.
- A provider with values, like **Fixed**, opens a form. In dialogs these buttons end with `…`. Fill in the form and
  click **Apply**.
- Modifiers that a brush can do without also have a **Remove** button.

The form starts with the current values where that makes sense. It checks your input the same way the commands
do. If something is wrong, the dialog opens again with the error on top.

### Suggestions

Text fields in dialogs cannot complete names while you type. The Name selector and the Directory selector
therefore have a **Suggestions** button. It lists up to 30 schematic or folder names that start with what you
typed. Click one to put it into the field. Type more letters if the list is too long.

In the chat editor, buttons that need a value put a command into your chat line instead. Finish it there; tab
completion helps you.

## Undo and redo

Every change in the editor can be undone with **Undo** and repeated with **Redo**. The editor keeps the last 50
changes. A new change clears the redo list.

The history belongs to the editor session. Running `/sbr` again starts a new session with an empty history.

## Live editing

After you click **Bind**, the editor stays connected to the brush on that item. Every later change, undo or redo
goes straight into the bound brush, and the preview follows. You do not need to bind again.

The connection ends quietly when that item type gets another brush or loses its brush. Your next change then only
affects the editor.

Picking up a brush and running `/sbr` connects the editor to that brush in the same way.

## Loading and saving

- **Save Brush** stores the whole brush as a brush preset. **Load Brush** replaces the brush in the editor with a
  saved one.
- **Save Preset** stores the schematic sets as a preset. **Add Preset** adds the sets of a saved preset to the
  brush.

In dialogs you type the name into a field. Saving also offers **Global** (if you have the permission) and
**Overwrite**. Names cannot contain spaces. To load a global preset or brush, put `g:` in front of its name.

Loading a brush and adding a preset can be undone. See [Presets](../presets.md) for how presets work.
