package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.event.PasteEvent;
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
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class RenderService implements Runnable, Listener {
    private final Map<UUID, Changes> changes = new HashMap<>();
    private final PaketWorker worker;
    private final Queue<Player> players = new ArrayDeque<>();
    private final Config config;
    private double count = 1;
    private boolean active = false;

    public RenderService(Plugin plugin, Config config) {
        this.config = config;
        active = !plugin.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
        worker = new PaketWorker();
        if (active) {
            worker.runTaskTimerAsynchronously(plugin, 0, 1);
        }
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
        changes.remove(event.player().getUniqueId());
        var schematicBrush = WorldEditBrush.getSchematicBrush(event.player());
        if (schematicBrush.isEmpty()) return;
        var collector = schematicBrush.get().pasteFake();
        worker.queue(event.player(), null, collector.changes());
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
        var schematicBrush = WorldEditBrush.getSchematicBrush(player);
        if (schematicBrush.isEmpty()) {
            resolveChanges(player);
            return;
        }
        if (schematicBrush.get().nextPaste().clipboardSize() > config.general().maxRenderSize()) {
            resolveChanges(player);
            return;
        }
        var collector = schematicBrush.get().pasteFake();
        renderChanges(player, collector.changes());
    }

    private void resolveChanges(Player player) {
        getChanges(player).ifPresent(c -> worker.queue(player, c, null));
    }

    private void renderChanges(Player player, Changes newChanges) {
        worker.queue(player, changes.get(player.getUniqueId()), newChanges);
        putChanges(player, newChanges);
    }

    private void putChanges(Player player, Changes changes) {
        this.changes.put(player.getUniqueId(), changes);
    }

    private Optional<Changes> getChanges(Player player) {
        return Optional.ofNullable(this.changes.get(player.getUniqueId()));
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

    private static class PaketWorker extends BukkitRunnable {
        private final Queue<ChangeEntry> queue = new ArrayDeque<>();

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                var poll = queue.poll();
                poll.send();
            }
        }

        public void queue(Player player, Changes oldChanges, Changes newChanges) {
            queue.add(new ChangeEntry(player, oldChanges, newChanges));
        }

        public void remove(Player player) {
            queue.removeIf(e -> e.player.equals(player));
        }

        private static class ChangeEntry {
            private final Player player;
            private final Changes oldChanges;
            private final Changes newChanges;

            private ChangeEntry(Player player, Changes oldChanges, Changes newChanges) {
                this.player = player;
                this.oldChanges = oldChanges;
                this.newChanges = newChanges;
            }

            private void send() {
                if (oldChanges != null) oldChanges.hide(player);
                if (newChanges != null) newChanges.show(player);
            }
        }
    }
}
