/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.eldoutilities.debug.DebugDataProvider;
import de.eldoria.eldoutilities.debug.HealthCheck;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.debug.data.chart.CategoryChart;
import de.eldoria.eldoutilities.debug.data.chart.ChartData;
import de.eldoria.eldoutilities.debug.data.chart.SeriesChart;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.RollingQueue;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Renders the brush previews of all players with an enabled preview and keeps their clients in sync.
 * <p>
 * Runs every tick on the main thread. Each tick renders as many previews as the refresh interval requires, within
 * {@code maxRenderMs}. Events that make a client lose preview blocks mark the affected sections, so the next update
 * sends them again. Every {@code previewResync} seconds all previews are sent again completely.
 */
public class RenderService implements Runnable, Listener, DebugDataProvider {
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
    private final Set<Schematic> measuring = ConcurrentHashMap.newKeySet();
    private final ExecutorService measurer = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "SBR schematic measurement");
        thread.setDaemon(true);
        return thread;
    });
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

        var schematic = brush.nextPaste().schematic();
        if (!schematic.isMeasured()) {
            measure(schematic);
            getSink(player).hide();
            return;
        }

        var includeAir = (boolean) brush.settings().getMutator(PlacementModifier.INCLUDE_AIR).value();
        var pasted = includeAir ? schematic.size() : schematic.effectiveSize();
        var limit = includeAir ? general.maxRenderSize() : Math.min(general.maxRenderSize(), general.maxEffectiveRenderSize());
        if (pasted > limit) {
            messageSender.sendErrorActionBar(brush.brushOwner(), "service.renderService.error.sizeExceeded",
                    Replacement.create("actual", "%,d".formatted(pasted)),
                    Replacement.create("max", "%,d".formatted(limit)));
            getSink(player).hide();
            return;
        }

        getSink(player).show(brush.pasteFake());
    }

    /**
     * Computes the sizes of a schematic on the measuring thread. Loading a large schematic takes long, so the preview
     * stays hidden until its size is known. A schematic that cannot be measured is not tried again.
     */
    private void measure(Schematic schematic) {
        if (!measuring.add(schematic)) return;
        measurer.execute(() -> {
            schematic.size();
            schematic.effectiveSize();
            if (schematic.isMeasured()) measuring.remove(schematic);
        });
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

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        var general = configuration.general();
        var rendering = new LinkedHashMap<String, String>();
        rendering.put("Active previews", players.stream().map(Player::getName).collect(Collectors.joining(", ")));
        rendering.put("Sinks", "%d (%d active)".formatted(sinks.size(), sinks.values().stream().filter(RenderSink::isActive).count()));
        rendering.put("Render time per tick", "%.2f ms of %d ms".formatted(renderTimeAverage(), general.maxRenderMs()));
        rendering.put("Refresh interval", "%d ticks".formatted(general.previewRefreshInterval()));
        rendering.put("Resync interval", general.previewResync() > 0 ? "%d s".formatted(general.previewResync()) : "disabled");
        rendering.put("Blocks sent (recent updates)", String.valueOf(sinks.values().stream().mapToInt(RenderSink::blocksSent).sum()));
        rendering.put("Section packets sent (recent updates)", String.valueOf(sinks.values().stream().mapToInt(RenderSink::sectionsSent).sum()));

        var entries = new ArrayList<EntryData>();
        entries.add(new EntryData("Rendering", rendering));
        sinks.values().stream()
             .filter(RenderSink::isActive)
             .map(RenderSink::debugEntries)
             .forEach(sink -> entries.add(new EntryData("Render sink " + sink.get("Owner"), sink)));
        return entries.toArray(EntryData[]::new);
    }

    @Override
    public @NotNull List<ChartData<?>> getCharts() {
        var charts = new ArrayList<ChartData<?>>();
        if (!timings.isEmpty()) {
            charts.add(SeriesChart.line("Render time per tick")
                                  .series("render time", timings.values())
                                  .unit("ms")
                                  .xLabel("tick")
                                  .yLabel("ms")
                                  .mark(configuration.general().maxRenderMs(), "budget"));
        }
        var active = sinks.values().stream().filter(RenderSink::isActive).toList();
        if (active.isEmpty()) return charts;
        var blocks = SeriesChart.line("Blocks sent per update").unit("blocks").xLabel("update");
        var sections = SeriesChart.line("Section packets per update").unit("packets").xLabel("update");
        var sizes = CategoryChart.bar("Preview size").unit("blocks");
        for (var sink : active) {
            blocks.series(sink.ownerName(), sink.blocksSentHistory());
            sections.series(sink.ownerName(), sink.sectionsSentHistory());
            sizes.value(sink.ownerName(), sink.size());
        }
        charts.add(blocks);
        charts.add(sections);
        charts.add(sizes);
        return charts;
    }

    @Override
    public @NotNull List<HealthCheck> getHealthChecks() {
        var budget = configuration.general().maxRenderMs();
        var average = renderTimeAverage();
        var message = "Rendering takes %.2f ms of %d ms per tick on average.".formatted(average, budget);
        if (average > budget * 0.8) {
            return List.of(HealthCheck.warn("Render budget", message + " Raise maxRenderMs or the refresh interval, or lower maxRenderSize."));
        }
        return List.of(HealthCheck.ok("Render budget", message));
    }

    /**
     * Resets the previews of every client and drops all sinks.
     */
    public void restart() {
        closeSinks();
        timings.clear();
    }

    /**
     * Resets the previews of every client, drops all sinks and stops measuring schematics. Used when the plugin is
     * disabled.
     */
    public void shutdown() {
        closeSinks();
        measurer.shutdownNow();
    }

    private void closeSinks() {
        sinks.values().forEach(RenderSink::close);
        sinks.clear();
        subscription.clear();
    }

    public Collection<RenderSink> sinks() {
        return sinks.values();
    }
}
