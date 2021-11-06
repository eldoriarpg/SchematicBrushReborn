/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.event;

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

    public PasteEvent(@NotNull Player who, Schematic schematic) {
        super(!Bukkit.isPrimaryThread());
        this.who = who;
        this.schematic = schematic;
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
