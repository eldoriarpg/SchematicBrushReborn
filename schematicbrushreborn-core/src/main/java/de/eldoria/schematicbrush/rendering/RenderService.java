/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import de.eldoria.schematicbrush.util.RollingQueue;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class RenderService implements Runnable, Listener {
    /**
     * The sinks of all active players. A sink does not require any subscribers by default.
     */
    private final Map<UUID, RenderSink> sinks = new HashMap<>();
    /**
     * The sink a player is subscribed to.
     */
    private final Map<UUID, RenderSink> subscription = new HashMap<>();
    /**
     * The paket worker which will process packet sending to players
     */
    private PacketWorker worker;
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
    private final RollingQueue<Long> timings = new RollingQueue<>(200);
    private double count = 1;

    public RenderService(SchematicBrushReborn plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        worker = PacketWorker.create(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("schematicbrush.brush.preview")) {
            if (configuration.general().isPreviewDefault()) {
                setState(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
        Optional.ofNullable(sinks.remove(event.getPlayer().getUniqueId())).ifPresent(RenderSink::unsubscribeAll);
        getSubscription(event.getPlayer()).ifPresent(sink -> sink.remove(event.getPlayer()));
        subscription.remove(event.getPlayer().getUniqueId());
        skip.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrePaste(PrePasteEvent event) {
        // Skip player if it has no active preview
        if (!players.contains(event.player())) return;
        // Skip rendering for some time to avoid conflicts
        skip.add(event.player().getUniqueId());
        // Resolve the last changes
        resolveBlocked(event.player());
    }

    /**
     * Removes a player from rendering by sending the current state of the world and negating all send changes.
     *
     * @param player player to resolve
     */
    private void resolveBlocked(Player player) {
        worker.process(player);
        RenderSink playerSink = getSink(player);
        // We push and send empty changes
        playerSink.pushAndSend(null);
        //getChanges(player).ifPresent(change -> new PaketWorker.ChangeEntry(player, change, null).sendChanges());
    }

    @EventHandler
    public void onPostPaste(PostPasteEvent event) {
        if (!players.contains(event.player())) return;

        // Remove the player from the block after a second aka 20 ticks
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> skip.remove(event.player().getUniqueId()), 20);
    }

    @Override
    public void run() {
        if (players.isEmpty()) return;
        count += players.size() / (double) configuration.general().previewRefreshInterval();
        var start = System.currentTimeMillis();
        while (count > 0 && !players.isEmpty()
                && System.currentTimeMillis() - start < configuration.general().maxRenderMs()) {
            count--;
            var player = nextPlayer();
            // No need to render dirty sinks or sinks without subscribers.
            if (getSink(player).isDirty() || !getSink(player).isSubscribed()) {
                continue;
            }
            if (!skip.contains(player.getUniqueId())) {
                render(player);
            } else if (sinks.containsKey(player.getUniqueId())) {
                resolveBlocked(player);
            }
        }
        timings.add(System.currentTimeMillis() - start);
    }

    private Player nextPlayer() {
        var player = players.remove();
        players.add(player);
        return player;
    }

    private void render(Player player) {
        var optBrush = WorldEditBrush.getSchematicBrush(player);
        if (optBrush.isEmpty()) {
            resolveChanges(player);
            return;
        }
        var brush = optBrush.get();
        var general = configuration.general();

        var outOfRange = brush.getBrushLocation()
                .map(loc -> loc.toVector().distanceSq(brush.actor().getLocation()
                        .toVector()) > Math.pow(general.renderDistance(), 2))
                .orElse(true);
        if (outOfRange) {
            resolveChanges(player);
            return;
        }

        var includeAir = (boolean) brush.settings().getMutator(PlacementModifier.INCLUDE_AIR).value();
        var replaceAll = (boolean) brush.settings().getMutator(PlacementModifier.REPLACE_ALL).value();

        if (includeAir && replaceAll && brush.nextPaste().schematic().size() > general.maxRenderSize()) {
            resolveChanges(player);
            return;
        }

        if (!includeAir && brush.nextPaste().schematic().effectiveSize() > general.maxRenderSize()) {
            resolveChanges(player);
            return;
        }

        if (!includeAir && brush.nextPaste().schematic().effectiveSize() > general.maxEffectiveRenderSize()) {
            resolveChanges(player);
            return;
        }

        var collector = brush.pasteFake();

        if (collector == null) {
            resolveChanges(player);
            return;
        }

        renderChanges(player, collector.changes());
    }

    private void resolveChanges(Player player) {
        RenderSink sink = getSink(player);
        // Push empty changes and queue the sink for sending.
        sink.pushAndQueue(null);
    }

    private void renderChanges(Player player, Changes newChanges) {
        var sink = getSink(player);
        sink.pushAndQueue(newChanges);
    }

    private RenderSink getSink(Player player) {
        return sinks.computeIfAbsent(player.getUniqueId(), k -> new RenderSink(player, worker, configuration));
    }

    public void setState(Player player, boolean state) {
        if (state) {
            if (players.contains(player)) {
                return;
            }
            players.add(player);
        } else {
            players.remove(player);
            resolveChanges(player);
        }
    }

    private Optional<RenderSink> getSubscription(Player player) {
        return Optional.ofNullable(subscription.get(player.getUniqueId()));
    }

    public boolean subscribe(Player target, Player subscriber) {
        // Remove last subscription
        getSubscription(subscriber).ifPresent(sink -> sink.unsubscribe(subscriber));
        // Schedule new subscription. Leave some time in order to let the sink refresh.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            getSink(target).subscribe(subscriber);
            subscription.put(subscriber.getUniqueId(), getSink(target));
        }, 10);
        return hasSink(target);
    }

    private boolean hasSink(Player target) {
        return sinks.containsKey(target.getUniqueId());
    }

    public void unsubscribe(Player player) {
        // subscripe player to its own sink
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
                Render Worker:
                %s
                """.stripIndent()
                .formatted(renderTimeAverage(),
                        Text.inlineEntries(timings.values(), 20).indent(2),
                        worker.info().indent(2));
    }

    public void restart() {
        sinks.clear();
        subscription.clear();
        worker = PacketWorker.create(this, plugin);
        timings.clear();
    }

    public Collection<RenderSink> sinks() {
        return sinks.values();
    }
}
