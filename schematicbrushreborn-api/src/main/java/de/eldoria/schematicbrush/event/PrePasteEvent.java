/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.event;

import de.eldoria.schematicbrush.brush.BrushPaste;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * A paste event.
 * The event may be asynchronous.
 */
@SuppressWarnings({"unused", "SameReturnValue"})
public class PrePasteEvent extends PasteEvent {
    public static final HandlerList HANDLERS = new HandlerList();

    public PrePasteEvent(@NotNull Player who, BrushPaste paste) {
        super(who, paste);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
