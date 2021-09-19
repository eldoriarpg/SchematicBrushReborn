package de.eldoria.schematicbrush.event;

import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PasteEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final Player who;
    private final Schematic schematic;

    public PasteEvent(@NotNull Player who, Schematic schematic) {
        super(true);
        this.who = who;
        this.schematic = schematic;
    }

    public Player player() {
        return who;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Schematic schematic() {
        return schematic;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
