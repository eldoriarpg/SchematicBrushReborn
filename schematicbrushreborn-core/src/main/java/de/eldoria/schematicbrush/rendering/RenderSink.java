/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.util.RollingQueue;
import de.eldoria.schematicbrush.util.Text;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RenderSink {
    private final UUID sinkOwner;
    private final Set<Player> subscribers = new HashSet<>();
    private final Set<Player> add = new HashSet<>();
    private final Set<Player> remove = new HashSet<>();
    private final Set<Player> received = new HashSet<>();
    @Nullable
    private Changes newChanges;
    @Nullable
    private Changes oldChanges;
    // Defines if the sink has changes yet to be sent.
    private boolean dirty;
    private final PacketWorker worker;
    private final Configuration configuration;
    private long flushed = System.currentTimeMillis();
    private final RollingQueue<Integer> batchSize = new RollingQueue<>(1200);

    public RenderSink(Player sinkOwner, PacketWorker worker, Configuration configuration) {
        this.sinkOwner = sinkOwner.getUniqueId();
        this.worker = worker;
        this.configuration = configuration;
        this.subscribers.add(sinkOwner);
    }

    public int sendChanges() {
        int changed = dispatchSend();
        if (changed != 0) {
            flushed = System.currentTimeMillis();
        }
        batchSize.add(changed);
        return changed;
    }

    private int dispatchSend() {
        int changed = 0;
        var general = configuration.general();
        // Check if new changes are present to be sent.
        for (Player player : add) {
            if (newChanges == null || general.isOutOfRenderRange(player.getLocation(), newChanges.location())) continue;
            changed += newChanges.show(player);
        }

        for (Player player : remove) {
            if (oldChanges != null) changed += oldChanges.hide(player);
        }

        if (!dirty) {
            applySubscriberChange();
            return changed;
        }
        if (oldChanges != null && newChanges != null) {
            return changed + update();
        }

        var lastReceived = new HashSet<>(this.received);
        this.received.clear();

        for (Player player : subscribers) {
            if (newChanges == null || general.isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                if (lastReceived.contains(player)) {
                    if (oldChanges != null) changed += oldChanges.hide(player);
                    continue;
                }
            }

            if (oldChanges != null) changed += oldChanges.hide(player);
            if (newChanges != null && !general.isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                changed += newChanges.show(player);
                received.add(player);
            }
        }

        dirty = false;
        applySubscriberChange();
        return changed;
    }

    private void applySubscriberChange() {
        subscribers.removeAll(remove);
        subscribers.addAll(add);
        if (newChanges != null) received.addAll(add);
        remove.clear();
        add.clear();
    }

    /**
     * Update the preview. Only send changed blocks and reuse already sent blocks.
     *
     * @return amount of send packets
     */
    @SuppressWarnings("ConstantConditions")
    private int update() {
        int changed = 0;
        var lastReceived = new HashSet<>(this.received);
        this.received.clear();

        for (Player player : subscribers) {
            // Check if player is outside of render distance
            if (configuration.general().isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                if (lastReceived.contains(player)) {
                    changed += oldChanges.hide(player);
                    continue;
                }
                continue;
            }

            if (lastReceived.contains(player)) {
                changed += oldChanges.hide(player, newChanges);
            }
            changed += newChanges.show(player, oldChanges);
            received.add(player);
        }
        dirty = false;
        return changed;
    }

    public int size() {
        return Optional.ofNullable(oldChanges).map(Changes::size).orElse(0)
                + Optional.ofNullable(newChanges).map(Changes::size).orElse(0);
    }

    public boolean isActive() {
        return isSubscribed() && System.currentTimeMillis() - flushed < 60000;
    }

    public void push(Changes newChanges) {
        oldChanges = this.newChanges;
        this.newChanges = newChanges;
        dirty = true;
    }

    public int pushAndSend(Changes newChanges) {
        push(newChanges);
        return sendChanges();
    }

    public void pushAndQueue(Changes newChanges) {
        push(newChanges);
        queueRender();
    }

    private void queueRender() {
        worker.queue(this);
    }

    public UUID sinkOwner() {
        return sinkOwner;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isSubscribed() {
        return !subscribers.isEmpty();
    }

    /**
     * Adds the player as a subscriber. Will send the latest changed.
     *
     * @param subscriber new subscriber
     */
    public void subscribe(Player subscriber) {
        if (subscribers.contains(subscriber)) return;
        add.add(subscriber);
        queueRender();
    }

    /**
     * Removed the player as a subscriber. Will resolve the latest changes.
     *
     * @param subscriber subscriber to remove
     */
    public void unsubscribe(Player subscriber) {
        if (!subscribers.contains(subscriber)) return;
        remove.add(subscriber);
        queueRender();
    }

    public void unsubscribeAll() {
        remove.addAll(subscribers);
        queueRender();
    }

    public void remove(Player player) {
        subscribers.remove(player);
    }

    public String info() {
        Optional<Player> player = Optional.of(Bukkit.getPlayer(sinkOwner));
        return """
                Owner: %s
                Size: %s
                Subscriber:
                %s
                Brush:
                %s
                Batch Size:
                %s
                """.stripIndent()
                .formatted(player.map(Player::getName).orElse("none"),
                        size(),
                        subscribers.stream().map(Player::getName).map("  %s"::formatted).collect(Collectors.joining("\n")),
                        player.flatMap(WorldEditBrush::getSchematicBrush).map(SchematicBrush::info).orElse("non").indent(2),
                        Text.inlineEntries(batchSize.values(), 20).indent(2));
    }
}
