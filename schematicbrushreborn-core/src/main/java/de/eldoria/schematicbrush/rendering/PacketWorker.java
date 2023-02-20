/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.util.RollingQueue;
import de.eldoria.schematicbrush.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PacketWorker implements Runnable {
    private final Queue<RenderSink> queue = new ArrayDeque<>();
    private final RenderService renderService;
    private boolean active;
    private final AtomicInteger tickChanges = new AtomicInteger();
    private final RollingQueue<Integer> tickUpdates = new RollingQueue<>(200);
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("SBR-Packet worker");
        return thread;
    });

    public static PacketWorker create(RenderService renderService,SchematicBrushReborn plugin) {
        PacketWorker packetWorker = new PacketWorker(renderService);
        plugin.scheduleRepeatingTask(packetWorker::flushTickMetrics, 1, 1);
        return packetWorker;
    }

    private PacketWorker(RenderService renderService) {
        this.renderService = renderService;
    }

    public void flushTickMetrics() {
        tickUpdates.add(tickChanges.getAndSet(0));
    }

    @Override
    public void run() {
        if (!claim()) return;
        while (!queue.isEmpty()) {
            var poll = queue.poll();
            tickChanges.addAndGet(poll.sendChanges());
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
        if (!active) worker.submit(this);
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

    public String info() {
        return """
                Total Sinks Count: %s
                Active Sinks Count: %s
                Active Sinks:
                %s
                Packets queued: %s
                Average updates last %s ticks: %s
                Updates per tick:
                %s
                """.stripIndent()
                .formatted(renderService.sinks().size(),
                        renderService.sinks().stream()
                                .filter(RenderSink::isActive)
                                .count(),
                        renderService.sinks().stream()
                                .filter(RenderSink::isActive)
                                .map(RenderSink::info)
                                .map(t -> t.indent(2))
                                .collect(Collectors.joining("\n======")),
                        packetQueuePacketCount(),
                        tickUpdates.values().size(), tickUpdates.values().stream()
                                .mapToInt(Integer::intValue)
                                .average().orElse(0),
                        Text.inlineEntries(tickUpdates.values(), 20).indent(2));
    }
}