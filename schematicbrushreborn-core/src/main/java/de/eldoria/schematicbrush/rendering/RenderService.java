package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.event.PasteEvent;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class RenderService implements Runnable, Listener {
    private final Set<UUID> locks = new HashSet<>();
    private final Map<UUID, Changes> changes = new HashMap<>();
    private final PaketWorker worker;
    private final Queue<Player> players = new ArrayDeque<>();
    private final Config config;
    private double count = 1;
    private boolean active = false;
    private ScheduledExecutorService executorService;

    public RenderService(Plugin plugin, Config config) {
        var renderer = new ThreadGroup("Brush Renderer");
        executorService = Executors.newScheduledThreadPool(5, r -> {
            var thread = new Thread(renderer, r);
            return thread;
        });
        this.config = config;
        active = !plugin.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
        worker = new PaketWorker(executorService);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!active) return;
        if (event.getPlayer().hasPermission("schematicbrush.brush.preview")) {
            if (config.general().isPreviewDefault()) {
                setState(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    @EventHandler
    public void onPaste(PasteEvent event) {
        if (!active) return;
        worker.remove(event.player());
        resolveChanges(event.player());
    }

    @Override
    public void run() {
        if (!active) return;
        count += players.size() / (double) config.general().previewRefreshInterval();
        var start = System.currentTimeMillis();
        while (count > 0 && !players.isEmpty() && System.currentTimeMillis() - start < config.general().maxRenderMs()) {
            count--;
            var player = players.poll();
            render(player);
            players.add(player);
        }
    }

    private void render(Player player) {
        if (locks.contains(player.getUniqueId())) return;
        locks.add(player.getUniqueId());
        var schematicBrush = WorldEditBrush.getSchematicBrush(player);
        if (schematicBrush.isEmpty()) {
            resolveChanges(player)
                    .exceptionally(err -> {
                        SchematicBrushReborn.logger().log(Level.WARNING, "Error in renderer", err);
                        return null;
                    })
                    .thenRun(() -> locks.remove(player.getUniqueId()));
            return;
        }
        if (schematicBrush.get().nextPaste().clipboardSize() > config.general().maxRenderSize()) {
            resolveChanges(player)
                    .exceptionally(err -> {
                        SchematicBrushReborn.logger().log(Level.WARNING, "Error in renderer", err);
                        return null;
                    })
                    .thenRun(() -> locks.remove(player.getUniqueId()));
            return;
        }
        var collector = schematicBrush.get().pasteFake();
        renderChanges(player, collector.changes())
                .exceptionally(err -> {
                    SchematicBrushReborn.logger().log(Level.WARNING, "Error in renderer", err);
                    return null;
                })
                .thenRun(() -> locks.remove(player.getUniqueId()));
    }

    private CompletableFuture<Void> resolveChanges(Player player) {
        var optChanges = getAndRemoveChanges(player);
        if (optChanges.isPresent()) {
            var changes = optChanges.get();
            var snapshots = SnapshotContainer.fromLocations(player.getWorld(), changes.changedLocations());
            return CompletableFuture.runAsync(() -> {
                var blocks = snapshots.getBlocks(changes.changedLocations());
                for (var entry : blocks.entrySet()) {
                    worker.queue(player, entry.getKey(), entry.getValue());
                }
            }, executorService);
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> renderChanges(Player player, Changes newChanges) {
        return resolveChanges(player)
                .thenRun(() -> {
                    putChanges(player, newChanges);
                    for (var entry : newChanges.changed().entrySet()) {
                        worker.queue(player, entry.getKey(), entry.getValue());
                    }
                });
    }

    private void putChanges(Player player, Changes changes) {
        this.changes.put(player.getUniqueId(), changes);
    }

    private Optional<Changes> getAndRemoveChanges(Player player) {
        return Optional.ofNullable(this.changes.remove(player.getUniqueId()));
    }

    public void setState(Player player, boolean state) {
        if (!active) return;
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

    public boolean isActive() {
        return active;
    }

    private static class PaketWorker implements Runnable {
        private boolean active = false;
        private final Queue<ChangeEntry> queue = new ArrayDeque<>();
        private final ExecutorService service;

        private PaketWorker(ExecutorService service) {
            this.service = service;
        }

        @Override
        public void run() {
            active = true;
            while (!queue.isEmpty()) {
                var poll = queue.poll();
                if (poll == null) return;
                poll.run();
            }
            active = false;
        }

        public void queue(Player player, Location location, BlockData blockData) {
            queue.add(new ChangeEntry(player, () -> player.sendBlockChange(location, blockData)));
            if (!active) {
                service.submit(this);
            }
        }

        public void remove(Player player) {
            queue.removeIf(e -> e.player.equals(player));
        }

        private static class ChangeEntry {
            private final Player player;
            private final Runnable runnable;

            private ChangeEntry(Player player, Runnable runnable) {
                this.player = player;
                this.runnable = runnable;
            }

            public void run() {
                runnable.run();
            }
        }
    }
}
