package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.HandSide;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.event.PasteEvent;
import de.eldoria.schematicbrush.rendering.BlockChangeCollector;
import de.eldoria.schematicbrush.rendering.CapturingExtent;
import de.eldoria.schematicbrush.rendering.FakeWorld;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the schematic brush as a {@link Brush} instance. A brush is immutable after creation and is always
 * assigned to only one player.
 */
public class SchematicBrush implements Brush {
    private final Plugin plugin;
    private final BrushSettings settings;
    private final Player brushOwner;
    private BrushPaste nextPaste;
    @Nullable
    private BrushBuilder builder;

    /**
     * Create a new schematic brush for a player.
     *
     * @param plugin   plugin instance
     * @param player   placer which owns this brush
     * @param settings settings of the brush
     */
    public SchematicBrush(Plugin plugin, Player player, BrushSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        brushOwner = player;
        buildNextPaste();
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) {
        paste(editSession, position);
    }

    private void paste(EditSession editSession, BlockVector3 position) {
        var paste = nextPaste.buildpaste(editSession, BukkitAdapter.adapt(brushOwner), position);

        Operations.completeBlindly(paste);
        if (editSession.getWorld() instanceof FakeWorld) return;
        plugin.getServer().getPluginManager().callEvent(new PasteEvent(brushOwner, nextPaste.schematic()));
        buildNextPaste();
    }

    private void performPasteFake(EditSession editSession, Extent targetExtent, BlockVector3 position) {
        var paste = nextPaste.buildpaste(editSession, targetExtent, BukkitAdapter.adapt(brushOwner), position);

        Operations.completeBlindly(paste);
    }

    /**
     * Paste the brush and capture changes.
     *
     * @return changes which will be made to the world
     */
    public BlockChangeCollector pasteFake() {
        var world = new FakeWorld(brushOwner.getWorld());
        CapturingExtent capturingExtent;
        try (var editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, 100000)) {
            var bukkitPlayer = BukkitAdapter.adapt(brushOwner);
            var localSession = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer);
            BrushTool brushTool;
            try {
                brushTool = localSession.getBrushTool(bukkitPlayer.getItemInHand(HandSide.MAIN_HAND).getType());
            } catch (InvalidToolBindException e) {
                return null;
            }
            if (!(brushTool.getBrush() instanceof SchematicBrush)) return null;
            capturingExtent = new CapturingExtent(editSession, world, settings);
            var target = bukkitPlayer.getBlockTrace(brushTool.getRange(), true, brushTool.getTraceMask());
            performPasteFake(editSession, capturingExtent, target.toVector().toBlockPoint());
        }
        return capturingExtent;
    }

    private void buildNextPaste() {
        var randomSchematicSet = settings.getRandomBrushConfig();
        var clipboard = randomSchematicSet.getRandomSchematic();
        if (clipboard == null) {
            MessageSender.getPluginMessageSender(plugin).sendError(brushOwner,
                    "No valid schematic was found for brush: ");
            return;
        }
        nextPaste = new BrushPaste(settings, randomSchematicSet, clipboard);
    }

    /**
     * Get the settings of the brush
     * @return settings
     */
    public BrushSettings getSettings() {
        return settings;
    }

    /**
     * Get the next paste which will be executed
     * @return next paste
     */
    public BrushPaste nextPaste() {
        return nextPaste;
    }

    /**
     * Convert the settings of the brush to a builder
     * @param settingsRegistry settings registry
     * @param schematicRegistry schematic registry
     * @return brush as builder
     */
    public BrushBuilder toBuilder(BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        if (builder == null) {
            builder = settings.toBuilder(brushOwner, settingsRegistry, schematicRegistry);
        }
        return builder;
    }
}
