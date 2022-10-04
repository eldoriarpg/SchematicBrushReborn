/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Queue;

public class PaketWorker implements Runnable {
    private final Queue<RenderSink> queue = new ArrayDeque<>();
    private final Plugin plugin;
    private boolean active;

    public PaketWorker(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!claim()) return;
        while (!queue.isEmpty()) {
            var poll = queue.poll();
            poll.sendChanges();
        }
        active = false;
    }

    // There is a minimal chance of a race condition. That's why this method needs to be synchronized
    private synchronized boolean claim() {
        if (active) return false;
        active = true;
        return true;
    }

    /**
     * Queues the render sink to be sent. Will start a worker if now task is running.
     *
     * @param renderSink render sink to queue.
     */
    public void queue(RenderSink renderSink) {
        queue.add(renderSink);
        if (!active) plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
    }


    public void remove(Player player) {
        queue.removeIf(e -> e.sinkOwner().equals(player.getUniqueId()));
    }

    public void process(Player player) {
        queue.removeIf(e -> {
            if (e.sinkOwner().equals(player.getUniqueId())) {
                e.sendChanges();
                return true;
            }
            return false;
        });
    }

    public int packetQueuePacketCount() {
        return queue.stream().mapToInt(RenderSink::size).sum();
    }

    public int size() {
        return queue.size();
    }
}
