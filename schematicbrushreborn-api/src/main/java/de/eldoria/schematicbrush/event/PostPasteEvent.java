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
public class PostPasteEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final Player who;
    private final Schematic schematic;

    public PostPasteEvent(@NotNull Player who, Schematic schematic) {
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
