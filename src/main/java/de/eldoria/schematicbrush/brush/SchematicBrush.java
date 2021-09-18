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
import de.eldoria.schematicbrush.brush.config.BrushPaste;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.event.PasteEvent;
import de.eldoria.schematicbrush.rendering.BlockChangeCollecter;
import de.eldoria.schematicbrush.rendering.CapturingExtent;
import de.eldoria.schematicbrush.rendering.FakeWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents the schematic brush as a {@link Brush} instance. A brush is immutable after creation and is always
 * assigned to only one player.
 */
public class SchematicBrush implements Brush {
    private final Plugin plugin;
    private final BrushSettings settings;
    private final Player brushOwner;
    private BrushPaste nextPaste;

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

    public BlockChangeCollecter pasteFake() {
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
                    "No valid schematic was found for brush: " + randomSchematicSet.arguments());
            return;
        }
        nextPaste = new BrushPaste(settings, randomSchematicSet, clipboard);
    }

    /**
     * Combine the current configuration with a new brush configuration to get a new brush
     *
     * @param brush Brush to combine. Only the {@link SchematicSet} list is updated.
     * @return a new schematic brush with the sub brushes of both brush configurations.
     */
    public SchematicBrush combineBrush(BrushSettings brush) {
        return new SchematicBrush(plugin, brushOwner, settings.combine(brush));
    }

    public BrushSettings getSettings() {
        return settings;
    }

    public BrushPaste nextPaste() {
        return nextPaste;
    }
}
