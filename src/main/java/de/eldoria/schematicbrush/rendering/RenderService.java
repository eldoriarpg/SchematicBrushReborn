package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.util.HandSide;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
    private final Map<UUID, Changes> changes = new HashMap<>();
    private final PaketWorker worker;
    private final Set<Player> players = new HashSet<>();

    public RenderService(Plugin plugin) {
        worker = new PaketWorker();
        worker.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            render(player);
        }
    }

    private void render(Player player) {
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer);
        BrushTool brushTool;
        bukkitPlayer.getItemInHand(HandSide.MAIN_HAND).getType().getBlockType();
        try {
            brushTool = localSession.getBrushTool(bukkitPlayer.getItemInHand(HandSide.MAIN_HAND).getType());
        } catch (InvalidToolBindException e) {
            resolveChanges(player);
            return;
        }

        if (!(brushTool.getBrush() instanceof SchematicBrush)) {
            resolveChanges(player);
            return;
        }
        BlockChangeCollecter collector = ((SchematicBrush) brushTool.getBrush()).pasteFake();
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
        if (state) {
            players.add(player);
        } else {
            players.remove(player);
            resolveChanges(player);
        }
    }

    private static class PaketWorker extends BukkitRunnable {
        private Queue<ChangeEntry> queue = new ArrayDeque<>();

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                ChangeEntry poll = queue.poll();
                poll.send();
            }
        }

        public void queue(Player player, Changes oldChanges, Changes newChanges) {
            queue.add(new ChangeEntry(player, oldChanges, newChanges));
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
