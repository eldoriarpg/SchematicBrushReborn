/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import de.eldoria.schematicbrush.config.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private final PaketWorker worker;
    private final Configuration configuration;

    public RenderSink(Player sinkOwner, PaketWorker worker, Configuration configuration) {
        this.sinkOwner = sinkOwner.getUniqueId();
        this.worker = worker;
        this.configuration = configuration;
        this.subscribers.add(sinkOwner);
    }

    public void sendChanges() {
        var general = configuration.general();
        // Check if new changes are present to be sent.
        for (Player player : add) {
            if (newChanges == null || general.isOutOfRenderRange(player.getLocation(), newChanges.location())) continue;
            newChanges.show(player);
        }

        for (Player player : remove) {
            if (oldChanges != null) oldChanges.hide(player);
        }

        if (!dirty) {
            applySubscriberChange();
            return;
        }
        if (oldChanges != null && newChanges != null) {
            update();
            return;
        }

        var lastReceived = new HashSet<>(this.received);
        this.received.clear();

        for (Player player : subscribers) {
            if (newChanges == null || general.isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                if (lastReceived.contains(player)) {
                    if (oldChanges != null) oldChanges.hide(player);
                    continue;
                }
            }

            if (oldChanges != null) oldChanges.hide(player);
            if (newChanges != null && !general.isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                newChanges.show(player);
                received.add(player);
            }
        }

        dirty = false;
        applySubscriberChange();
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
     */
    @SuppressWarnings("ConstantConditions")
    private void update() {
        var lastReceived = new HashSet<>(this.received);
        this.received.clear();

        for (Player player : subscribers) {
            // Check if player is outside of render distance
            if (configuration.general().isOutOfRenderRange(player.getLocation(), newChanges.location())) {
                if (lastReceived.contains(player)) {
                    oldChanges.hide(player);
                    continue;
                }
                continue;
            }

            if (lastReceived.contains(player)) {
                oldChanges.hide(player, newChanges);
            }
            newChanges.show(player, oldChanges);
            received.add(player);
        }
        dirty = false;
    }

    public int size() {
        return Optional.ofNullable(oldChanges).map(Changes::size).orElse(0)
               + Optional.ofNullable(newChanges).map(Changes::size).orElse(0);
    }

    public void push(Changes newChanges) {
        oldChanges = this.newChanges;
        this.newChanges = newChanges;
        dirty = true;
    }

    public void pushAndSend(Changes newChanges) {
        push(newChanges);
        sendChanges();
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
}
