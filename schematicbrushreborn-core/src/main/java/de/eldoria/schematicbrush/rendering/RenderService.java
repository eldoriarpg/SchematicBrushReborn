/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.event.PrePasteEvent;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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
     * The changes which were send to the player lately
     */
    private final Map<UUID, Changes> changes = new HashMap<>();
    /**
     * The paket worker which will process packet sending to players
     */
    private final PaketWorker worker;
    /**
     * The players which should receive render preview packets
     */
    private final Queue<Player> players = new ArrayDeque<>();
    /**
     * Players which should be excluded from receiving render packets.
     */
    private final Set<UUID> skip = new HashSet<>();
    private final Plugin plugin;
    private final Configuration configuration;
    private double count = 1;

    public RenderService(Plugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        worker = new PaketWorker();
        worker.runTaskTimerAsynchronously(plugin, 0, 1);
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
        changes.remove(event.getPlayer().getUniqueId());
        worker.remove(event.getPlayer());
        skip.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPrePaste(PrePasteEvent event) {
        if (!players.contains(event.player())) return;
        skip.add(event.player().getUniqueId());
        if (event.isAsynchronous()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                resolveChanges(event.player());
            });
        } else {
            resolveChanges(event.player());
        }
    }

    private void resolveBlocked(Player player) {
        resolveChanges(player);
        changes.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPostPaste(PostPasteEvent event) {
        if (!players.contains(event.player())) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> skip.remove(event.player().getUniqueId()), 20);
    }

    @Override
    public void run() {
        if (players.isEmpty()) return;
        count += players.size() / (double) configuration.general().previewRefreshInterval();
        var start = System.currentTimeMillis();
        while (count > 0 && !players.isEmpty() && System.currentTimeMillis() - start < configuration.general().maxRenderMs()) {
            count--;
            var player = players.remove();
            if (!skip.contains(player.getUniqueId())) {
                render(player);
            } else if (changes.containsKey(player.getUniqueId())) {
                resolveBlocked(player);
            }
            players.add(player);
        }
    }

    private void render(Player player) {
        var schematicBrush = WorldEditBrush.getSchematicBrush(player);
        if (schematicBrush.isEmpty()) {
            resolveChanges(player);
            return;
        }
        if (schematicBrush.get().nextPaste().clipboardSize() > configuration.general().maxRenderSize()) {
            resolveChanges(player);
            return;
        }
        var collector = schematicBrush.get().pasteFake();
        renderChanges(player, collector.changes());
    }

    private void resolveChanges(Player player) {
        getChanges(player).ifPresent(change -> worker.queue(player, change, null));
    }

    private void renderChanges(Player player, Changes newChanges) {
        worker.queue(player, changes.get(player.getUniqueId()), newChanges);
        putChanges(player, newChanges);
    }

    private void putChanges(Player player, Changes changes) {
        this.changes.put(player.getUniqueId(), changes);
    }

    private Optional<Changes> getChanges(Player player) {
        return Optional.ofNullable(changes.get(player.getUniqueId()));
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

    private static class PaketWorker extends BukkitRunnable {
        private final Queue<ChangeEntry> queue = new ArrayDeque<>();

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                var poll = queue.poll();
                poll.sendChanges();
            }
        }

        public void queue(Player player, Changes oldChanges, Changes newChanges) {
            queue.add(new ChangeEntry(player, oldChanges, newChanges));
        }

        public void remove(Player player) {
            queue.removeIf(e -> e.player.equals(player));
        }

        private record ChangeEntry(Player player, Changes oldChanges,
                                   Changes newChanges) {

            private void sendChanges() {
                if (oldChanges != null) oldChanges.hide(player);
                if (newChanges != null) newChanges.show(player);
            }
        }
    }
}
