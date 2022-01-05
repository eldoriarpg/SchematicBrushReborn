/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.event;

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
public class PasteEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final Player who;
    private final Schematic schematic;
    private final SchematicSet schematicSet;

    public PasteEvent(@NotNull Player who, Schematic schematic, SchematicSet schematicSet) {
        super(!Bukkit.isPrimaryThread());
        this.who = who;
        this.schematic = schematic;
        this.schematicSet = schematicSet;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Player who pasted
     *
     * @return player
     */
    public Player player() {
        return who;
    }

    /**
     * The pasted schematic
     *
     * @return schematic
     */
    public Schematic schematic() {
        return schematic;
    }

    /**
     * The schematic set which contains the schematic
     *
     * @return schematic set
     * @since 2.0.2
     */
    public SchematicSet schematicSet() {
        return schematicSet;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
