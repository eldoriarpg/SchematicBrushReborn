package de.eldoria.schematicbrush.event;

import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PasteEvent extends PlayerEvent {
    public static HandlerList HANDLERS = new HandlerList();
    private final Schematic schematic;

    public PasteEvent(@NotNull Player who, Schematic schematic) {
        super(who);
        this.schematic = schematic;
    }

    public Schematic schematic() {
        return schematic;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
