/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.RollingQueue;
import de.eldoria.schematicbrush.util.Text;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Renders the brush previews of all players with an enabled preview and keeps their clients in sync.
 * <p>
 * Runs every tick on the main thread. Each tick renders as many previews as the refresh interval requires, within
 * {@code maxRenderMs}. Events that make a client lose preview blocks mark the affected sections, so the next update
 * sends them again. Every {@code previewResync} seconds all previews are sent again completely.
 */
public class RenderService implements Runnable, Listener {
    /**
     * The sinks of all active players. A sink does not require any subscribers by default.
     */
    private final Map<UUID, RenderSink> sinks = new HashMap<>();
    /**
     * The sink a player is subscribed to.
     */
    private final Map<UUID, RenderSink> subscription = new HashMap<>();
    private final MessageSender messageSender;
    /**
     * The players which should receive render preview packets
     */
    private final Queue<Player> players = new ArrayDeque<>();
    /**
     * Players which should be excluded from receiving render packets.
     */
    private final Set<UUID> skip = new HashSet<>();
    private final SchematicBrushReborn plugin;
    private final Configuration configuration;
    private final RollingQueue<Long> timings = new RollingQueue<>(1200);
    private double credit;
    private long ticks;

    public RenderService(SchematicBrushReborn plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(Permissions.Brush.PREVIEW)) {
            if (configuration.general().isPreviewDefault()) {
                setState(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        var player = event.getPlayer().getUniqueId();
        players.remove(event.getPlayer());
        Optional.ofNullable(sinks.remove(player)).ifPresent(sink -> {
            sink.forget(player);
            sink.close();
        });
        sinks.values().forEach(sink -> sink.forget(player));
        subscription.remove(player);
        skip.remove(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrePaste(PrePasteEvent event) {
        if (!players.contains(event.player())) return;
        skip.add(event.player().getUniqueId());
        getSink(event.player()).hide();
    }

    @EventHandler
    public void onPostPaste(PostPasteEvent event) {
        var world = event.player().getWorld().getUID();
        sinks.values().forEach(sink -> sink.loseAll(world));
        if (!players.contains(event.player())) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> skip.remove(event.player().getUniqueId()), configuration.general().pasteBreak());
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        loseChunk(event.getPlayer(), event.getChunk().getX(), event.getChunk().getZ());
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        loseChunk(event.getPlayer(), event.getChunk().getX(), event.getChunk().getZ());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        var player = event.getPlayer().getUniqueId();
        sinks.values().forEach(sink -> sink.viewer(player).ifPresent(ViewerState::forget));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        loseSection(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        loseSection(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null) return;
        var player = event.getPlayer().getUniqueId();
        var clicked = sectionKey(block);
        var face = sectionKey(block.getRelative(event.getBlockFace()));
        sinks.values().forEach(sink -> sink.viewer(player).ifPresent(viewer -> {
            viewer.loseSection(clicked);
            viewer.loseSection(face);
        }));
    }

    private void loseChunk(Player player, int chunkX, int chunkZ) {
        sinks.values().forEach(sink -> sink.viewer(player.getUniqueId()).ifPresent(viewer -> viewer.loseChunk(chunkX, chunkZ)));
    }

    private void loseSection(Block block) {
        var world = block.getWorld().getUID();
        var section = sectionKey(block);
        sinks.values().forEach(sink -> sink.loseSection(world, section));
    }

    private static long sectionKey(Block block) {
        return BlockKey.section(block.getX(), block.getY(), block.getZ());
    }

    @Override
    public void run() {
        ticks++;
        resyncIfDue();
        if (players.isEmpty()) return;
        try {
            tick();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred during rendering", e);
        }
    }

    private void resyncIfDue() {
        var interval = configuration.general().previewResync() * 20L;
        if (interval > 0 && ticks % interval == 0) {
            sinks.values().forEach(RenderSink::loseAll);
        }
    }

    private void tick() {
        var start = System.currentTimeMillis();
        credit = Math.min(credit + players.size() / (double) configuration.general().previewRefreshInterval(), players.size());
        while (credit >= 1 && System.currentTimeMillis() - start < configuration.general().maxRenderMs()) {
            credit--;
            try {
                handlePlayerTick(nextPlayer());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred during player rendering", e);
            }
        }
        timings.add(System.currentTimeMillis() - start);
    }

    private void handlePlayerTick(Player player) {
        var sink = getSink(player);
        if (!sink.isSubscribed()) return;
        if (skip.contains(player.getUniqueId())) {
            sink.skipped();
            return;
        }
        render(player);
    }

    private Player nextPlayer() {
        var player = players.remove();
        players.add(player);
        return player;
    }

    private void render(Player player) {
        var optBrush = WorldEditBrush.getSchematicBrush(player);
        if (optBrush.isEmpty() || optBrush.get().nextPaste() == null) {
            getSink(player).hide();
            return;
        }
        var brush = optBrush.get();
        var general = configuration.general();

        var outOfRange = brush.getBrushLocation()
                .map(loc -> loc.toVector().distanceSq(brush.actor().getLocation()
                        .toVector()) > Math.pow(general.renderDistance(), 2))
                .orElse(true);
        if (outOfRange) {
            getSink(player).hide();
            return;
        }

        var includeAir = (boolean) brush.settings().getMutator(PlacementModifier.INCLUDE_AIR).value();
        var replaceAll = (boolean) brush.settings().getMutator(PlacementModifier.REPLACE_ALL).value();

        if (includeAir && replaceAll && brush.nextPaste().schematic().size() > general.maxRenderSize()) {
            messageSender.sendErrorActionBar(brush.brushOwner(), "service.renderService.error.sizeExceeded",
                    Replacement.create("actual", "%,d".formatted(brush.nextPaste().schematic().size())),
                    Replacement.create("max", "%,d".formatted(general.maxRenderSize())));
            getSink(player).hide();
            return;
        }

        if (!includeAir && brush.nextPaste().schematic().effectiveSize() > general.maxRenderSize()) {
            messageSender.sendErrorActionBar(brush.brushOwner(), "service.renderService.error.sizeExceeded",
                    Replacement.create("actual", "%,d".formatted(brush.nextPaste().schematic().effectiveSize())),
                    Replacement.create("max", "%,d".formatted(general.maxRenderSize())));
            getSink(player).hide();
            return;
        }

        if (!includeAir && brush.nextPaste().schematic().effectiveSize() > general.maxEffectiveRenderSize()) {
            messageSender.sendErrorActionBar(brush.brushOwner(), "service.renderService.error.sizeExceeded",
                    Replacement.create("actual", "%,d".formatted(brush.nextPaste().schematic().effectiveSize())),
                    Replacement.create("max", "%,d".formatted(general.maxEffectiveRenderSize())));
            getSink(player).hide();
            return;
        }

        getSink(player).show(brush.pasteFake());
    }

    private RenderSink getSink(Player player) {
        return sinks.computeIfAbsent(player.getUniqueId(), k -> new RenderSink(player, configuration));
    }

    public void setState(Player player, boolean state) {
        if (state) {
            if (players.contains(player)) {
                return;
            }
            players.add(player);
        } else {
            players.remove(player);
            getSink(player).hide();
        }
    }

    /**
     * Returns the state of the player
     *
     * @param player player to check
     * @return true if preview is active
     */
    public boolean getState(Player player) {
        return players.contains(player);
    }

    private Optional<RenderSink> getSubscription(Player player) {
        return Optional.ofNullable(subscription.get(player.getUniqueId()));
    }

    /**
     * Moves a subscriber to the sink of a target player.
     *
     * @param target     owner of the preview to watch
     * @param subscriber player who watches
     * @return true if the target has an enabled preview
     */
    public boolean subscribe(Player target, Player subscriber) {
        getSubscription(subscriber).ifPresent(sink -> sink.unsubscribe(subscriber));
        var sink = getSink(target);
        sink.subscribe(subscriber);
        subscription.put(subscriber.getUniqueId(), sink);
        return players.contains(target);
    }

    public void unsubscribe(Player player) {
        subscribe(player, player);
    }

    public double renderTimeAverage() {
        return timings.values().stream().mapToLong(value -> value).average().orElse(0);
    }

    public String renderInfo() {
        return """
                Average Tick Render Time: %s
                Last Tick Render Times:
                %s
                Total Sinks Count: %s
                Active Sinks Count: %s
                Active Sinks:
                %s
                Active previews:
                %s
                """.stripIndent()
                   .formatted(renderTimeAverage(),
                           Text.inlineEntries(timings.values(), 20).indent(2),
                           sinks.size(),
                           sinks.values().stream().filter(RenderSink::isActive).count(),
                           sinks.values().stream()
                                .filter(RenderSink::isActive)
                                .map(RenderSink::info)
                                .map(t -> t.indent(2))
                                .collect(Collectors.joining("\n======")),
                           players.stream().map(Player::getName).collect(Collectors.joining("\n")).indent(2));
    }

    /**
     * Resets the previews of every client and drops all sinks.
     */
    public void restart() {
        shutdown();
        timings.clear();
    }

    /**
     * Resets the previews of every client and drops all sinks. Used when the plugin is disabled.
     */
    public void shutdown() {
        sinks.values().forEach(RenderSink::close);
        sinks.clear();
        subscription.clear();
    }

    public Collection<RenderSink> sinks() {
        return sinks.values();
    }
}
