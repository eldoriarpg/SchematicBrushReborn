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
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The preview of one brush owner and the players watching it.
 * <p>
 * Each viewer is updated against what it currently shows, see {@link PreviewDiff}, so a viewer that joins late, moves
 * out of range or lost chunks always ends up with the correct blocks. Only used on the main thread.
 */
public class RenderSink {
    private final UUID owner;
    private final Configuration configuration;
    private final Set<UUID> subscribers = new LinkedHashSet<>();
    private final Map<UUID, ViewerState> viewers = new HashMap<>();
    private final RollingQueue<Integer> blocksSent = new RollingQueue<>(1200);
    private final RollingQueue<Integer> sectionsSent = new RollingQueue<>(1200);
    private final RollingQueue<Integer> skipped = new RollingQueue<>(1200);
    private PreviewSnapshot preview = PreviewSnapshot.empty();
    private long lastSent = System.currentTimeMillis();

    /**
     * Creates the sink of a brush owner. The owner is its first subscriber.
     *
     * @param owner         owner of the brush
     * @param configuration plugin configuration
     */
    public RenderSink(Player owner, Configuration configuration) {
        this.owner = owner.getUniqueId();
        this.configuration = configuration;
        subscribers.add(this.owner);
    }

    /**
     * Shows a new preview to every subscriber.
     *
     * @param next the preview to show
     */
    public void show(PreviewSnapshot next) {
        preview = next;
        var sent = new Sent();
        for (var subscriber : subscribers) {
            var player = Bukkit.getPlayer(subscriber);
            if (player != null) sent.add(send(player, visibleFor(player)));
        }
        record(sent);
        skipped.add(0);
    }

    /**
     * Removes the preview from every subscriber.
     */
    public void hide() {
        show(PreviewSnapshot.empty());
    }

    /**
     * Adds a subscriber and shows the current preview to it.
     *
     * @param player new subscriber
     */
    public void subscribe(Player player) {
        if (!subscribers.add(player.getUniqueId())) return;
        record(new Sent().add(send(player, visibleFor(player))));
    }

    /**
     * Removes a subscriber and resets the blocks it was shown.
     *
     * @param player subscriber to remove
     */
    public void unsubscribe(Player player) {
        if (!subscribers.remove(player.getUniqueId())) return;
        reset(player);
    }

    /**
     * Resets the blocks of every subscriber and drops the preview. Subscribers are kept.
     */
    public void close() {
        preview = PreviewSnapshot.empty();
        for (var subscriber : subscribers) {
            var player = Bukkit.getPlayer(subscriber);
            if (player != null) reset(player);
        }
        viewers.clear();
    }

    /**
     * Drops a player without sending anything, for players that left the server.
     *
     * @param player id of the player
     */
    public void forget(UUID player) {
        subscribers.remove(player);
        viewers.remove(player);
    }

    /**
     * Marks every displayed section of all viewers in a world as lost.
     *
     * @param world id of the world
     */
    public void loseAll(UUID world) {
        viewers.values().stream()
               .filter(viewer -> world.equals(viewer.displayed().world()))
               .forEach(ViewerState::loseAll);
    }

    /**
     * Marks every displayed section of all viewers as lost.
     */
    public void loseAll() {
        viewers.values().forEach(ViewerState::loseAll);
    }

    /**
     * Marks a section as lost for all viewers in a world.
     *
     * @param world      id of the world
     * @param sectionKey packed section key
     */
    public void loseSection(UUID world, long sectionKey) {
        viewers.values().stream()
               .filter(viewer -> world.equals(viewer.displayed().world()))
               .forEach(viewer -> viewer.loseSection(sectionKey));
    }

    /**
     * The state of a viewer of this sink.
     *
     * @param player id of the viewer
     * @return the state, if the player was shown this preview
     */
    Optional<ViewerState> viewer(UUID player) {
        return Optional.ofNullable(viewers.get(player));
    }

    /**
     * Records a tick in which the preview was not rendered.
     */
    public void skipped() {
        skipped.add(1);
    }

    public UUID sinkOwner() {
        return owner;
    }

    public boolean isSubscribed() {
        return !subscribers.isEmpty();
    }

    public boolean isActive() {
        return isSubscribed() && System.currentTimeMillis() - lastSent < 60000;
    }

    /**
     * The amount of blocks in the current preview.
     *
     * @return block count
     */
    public int size() {
        return preview.size();
    }

    private PreviewSnapshot visibleFor(Player player) {
        var origin = preview.origin();
        if (origin == null || configuration.general().isOutOfRenderRange(player.getLocation(), origin)) {
            return PreviewSnapshot.empty();
        }
        return preview;
    }

    private void reset(Player player) {
        record(new Sent().add(send(player, PreviewSnapshot.empty())));
        viewers.remove(player.getUniqueId());
    }

    private Map<Long, BlockData> send(Player player, PreviewSnapshot target) {
        var viewer = viewers.computeIfAbsent(player.getUniqueId(), key -> new ViewerState());
        var world = player.getWorld();
        if (!viewer.displayed().isEmpty() && !world.getUID().equals(viewer.displayed().world())) {
            viewer.forget();
        }
        var changes = PreviewDiff.between(viewer.displayed(), target, viewer.lostSections(), live(world));
        BlockSender.send(player, changes);
        viewer.shows(target);
        return changes;
    }

    private static PreviewDiff.LiveBlocks live(World world) {
        return key -> {
            int x = BlockKey.x(key);
            int z = BlockKey.z(key);
            if (!world.isChunkLoaded(x >> 4, z >> 4)) return null;
            return world.getBlockData(x, BlockKey.y(key), z);
        };
    }

    private void record(Sent sent) {
        blocksSent.add(sent.blocks);
        sectionsSent.add(sent.sections);
        if (sent.blocks != 0) lastSent = System.currentTimeMillis();
    }

    public String info() {
        var player = Optional.ofNullable(Bukkit.getPlayer(owner));
        return """
                Owner: %s
                Size: %s
                Subscriber:
                %s
                Brush:
                %s
                Blocks sent:
                %s
                Section packets sent:
                %s
                Skipped ticks:
                %s
                """.stripIndent()
                   .formatted(player.map(Player::getName).orElse("none"),
                           size(),
                           subscribers.stream()
                                      .map(Bukkit::getPlayer)
                                      .filter(Objects::nonNull)
                                      .map(Player::getName)
                                      .map("  %s"::formatted)
                                      .collect(Collectors.joining("\n")),
                           player.flatMap(WorldEditBrush::getSchematicBrush).map(SchematicBrush::info).orElse("none").indent(2),
                           Text.inlineEntries(blocksSent.values(), 20).indent(2),
                           Text.inlineEntries(sectionsSent.values(), 20).indent(2),
                           Text.inlineEntries(skipped.values(), 20).indent(2));
    }

    /**
     * Counts what one update sent.
     */
    private static final class Sent {
        private int blocks;
        private int sections;

        private Sent add(Map<Long, BlockData> changes) {
            blocks += changes.size();
            sections += (int) changes.keySet().stream().map(BlockKey::section).distinct().count();
            return this;
        }
    }
}
