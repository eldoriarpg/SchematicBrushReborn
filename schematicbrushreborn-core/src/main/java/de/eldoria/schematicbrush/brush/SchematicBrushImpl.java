/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.history.BrushHistory;
import de.eldoria.schematicbrush.brush.history.BrushHistoryImpl;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import de.eldoria.schematicbrush.rendering.BlockChangeCollector;
import de.eldoria.schematicbrush.rendering.CapturingExtent;
import de.eldoria.schematicbrush.rendering.CapturingExtentImpl;
import de.eldoria.schematicbrush.rendering.FakeWorldImpl;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.FAWE;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the schematic brush as a {@link Brush} instance. A brush is immutable after creation and is always
 * assigned to only one player.
 */
public class SchematicBrushImpl implements SchematicBrush {
    private final Plugin plugin;
    private final BrushSettings settings;
    private final UUID brushOwner;
    @Nullable
    private BrushPaste nextPaste;
    @Nullable
    private BrushBuilder builder;
    private final BrushHistory history = new BrushHistoryImpl(100);

    /**
     * Create a new schematic brush for a player.
     *
     * @param plugin   plugin instance
     * @param player   placer which owns this brush
     * @param settings settings of the brush
     */
    public SchematicBrushImpl(Plugin plugin, Player player, BrushSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        brushOwner = player.getUniqueId();
        buildNextPaste();
    }

    @Override
    public Player brushOwner() {
        return plugin.getServer().getPlayer(brushOwner);
    }

    @Override
    public BukkitPlayer actor() {
        return BukkitAdapter.adapt(brushOwner());
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) {
        paste(editSession, position);
    }

    private void paste(EditSession editSession, BlockVector3 position) {
        var prePasteEvent = new PrePasteEvent(brushOwner(), nextPaste);
        plugin.getServer().getPluginManager().callEvent(prePasteEvent);
        if (prePasteEvent.isCancelled()) {
            return;
        }
        var paste = nextPaste.buildpaste(editSession, actor(), position);
        Operations.completeBlindly(paste);
        plugin.getServer().getPluginManager().callEvent(new PostPasteEvent(brushOwner(), nextPaste));
        buildNextPaste();
    }

    private void performPasteFake(EditSession editSession, Extent targetExtent, BlockVector3 position) {
        var paste = nextPaste.buildpaste(editSession, targetExtent, actor(), position);

        Operations.completeBlindly(paste);
    }

    /**
     * Paste the brush and capture changes.
     *
     * @return changes which will be made to the world
     */
    @Override
    public BlockChangeCollector pasteFake() {
        var world = new FakeWorldImpl(brushOwner().getWorld());
        world.location(new Location(world));
        CapturingExtent capturingExtent;
        try (var editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(100000).build()) {
            var bukkitPlayer = actor();
            if (bukkitPlayer == null) return new CapturingExtentImpl(editSession, world, settings, new Location(world));
            var brushTool = getBrushTool();
            if (brushTool.isEmpty()) return null;
            var target = bukkitPlayer.getBlockTrace(brushTool.get().getRange(), true, brushTool.get().getTraceMask());
            world.location(target);
            capturingExtent = new CapturingExtentImpl(editSession, world, settings, target);
            performPasteFake(editSession, capturingExtent, target.toVector().toBlockPoint());
        }
        return capturingExtent;
    }

    private Optional<BrushTool> getBrushTool() {
        var localSession = WorldEdit.getInstance().getSessionManager().get(actor());
        BrushTool brushTool;
        try {
            if (FAWE.isFawe()) {
                brushTool = localSession.getBrushTool(actor().getItemInHand(HandSide.MAIN_HAND).getType().getDefaultState(), actor(), false);
            } else {
                brushTool = localSession.getBrushTool(actor().getItemInHand(HandSide.MAIN_HAND).getType());
            }
        } catch (InvalidToolBindException e) {
            return Optional.empty();
        }
        if (brushTool == null || !(brushTool.getBrush() instanceof SchematicBrushImpl)) return Optional.empty();
        return Optional.of(brushTool);
    }

    @Override
    public Optional<Location> getBrushLocation() {
        var brushTool = getBrushTool();
        return brushTool.map(tool -> actor().getBlockTrace(tool.getRange(), true, tool.getTraceMask()));
    }

    private void buildNextPaste() {
        var next = settings.schematicSelection().nextSchematic(this, false);
        if (next.isEmpty()) {
            MessageSender.getPluginMessageSender(plugin).sendError(brushOwner(),
                    "No valid schematic was found for brush");
            return;
        }
        if (nextPaste != null && !nextPaste.schematic().equals(next.get().second)) {
            history.push(nextPaste.schematicSet(), nextPaste.schematic());
        }
        next.ifPresent(pair -> nextPaste = new BrushPasteImpl(this, settings, pair.first, pair.second));
    }

    /**
     * Get the settings of the brush
     *
     * @return settings
     */
    @Override
    public BrushSettings settings() {
        return settings;
    }

    /**
     * Get the next paste which will be executed
     *
     * @return next paste
     */
    @Override
    public BrushPaste nextPaste() {
        return nextPaste;
    }

    /**
     * Convert the settings of the brush to a builder
     *
     * @param settingsRegistry  settings registry
     * @param schematicRegistry schematic registry
     * @return brush as builder
     */
    @Override
    public BrushBuilder toBuilder(BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        if (builder == null) {
            builder = settings.toBuilder(brushOwner(), settingsRegistry, schematicRegistry);
        }
        return builder;
    }

    @Override
    public BrushHistory history() {
        return history;
    }

    @Override
    public String info() {
        Schematic schematic = nextPaste.schematic();
        return """
                Schematic Name: %s
                Schematic Path: %s
                Schematic Size: %,d
                Effective Schematic Size: %,d
                Schematic Format: %s
                """.stripIndent()
                .formatted(schematic.getFile().getName(),
                        schematic.getFile().getPath(),
                        schematic.size(),
                        schematic.effectiveSize(),
                        schematic.format().getName());
    }
}
