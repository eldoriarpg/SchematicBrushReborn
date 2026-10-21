/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.Location;

public interface GeneralConfig {
    /**
     * Defined the used storage type.
     *
     * @return name of storage type
     */
    Nameable storageType();

    /**
     * Defined if the plugin should check for updates
     *
     * @return true when update checks should be performed
     */
    boolean isCheckUpdates();

    /**
     * Defines if the preview should be enabled by default.
     *
     * @return true when preview should be enabled
     */
    boolean isPreviewDefault();

    /**
     * Defined if names should be always shown by default
     *
     * @return true when names should be shown by default
     */
    boolean isShowNameDefault();

    /**
     * The default channel for brush messages. One of {@link MessageChannel}
     *
     * @return the message channel implementation
     */
    de.eldoria.schematicbrush.config.sections.MessageChannel defaultNameChannel();

    /**
     * The refresh interval for each player of the rendering preview.
     *
     * @return the refresh interval in ticks
     */
    int previewRefreshInterval();

    /**
     * The max amount of ms, which can be used by the renderer in one tick (50 ms)
     *
     * @return the amount in ms
     */
    int maxRenderMs();

    /**
     * The max distance of the render origin and the brush owner
     *
     * @return the distance in blocks
     */
    int renderDistance();

    /**
     * The max distance of the render origin and the brush owner
     *
     * @return the distance in blocks
     */
    double renderDistanceSquared();

    /**
     * The max render size. This is the size of the schematic based on the bounds including air blocks.
     * <p>
     * See {@link #maxEffectiveRenderSize()} for the size without air blocks
     *
     * @return the max render size in blocks
     */
    int maxRenderSize();

    /**
     * Checks whether two locations are too far apart to render a preview. Locations in different worlds are always out of
     * range.
     *
     * @param origin the first location
     * @param other  the second location
     * @return true if the locations are in different worlds or further apart than {@link #renderDistance()}
     */
    default boolean isOutOfRenderRange(Location origin, Location other) {
        if (origin.getWorld() != other.getWorld()) return true;
        return origin.distanceSquared(other) > renderDistanceSquared();
    }

    /**
     * The max effective render size. This is the size of a schematic excluding air blocks
     *
     * @return the effective render size in blocks
     */
    int maxEffectiveRenderSize();

    int pasteBreak();

    /**
     * The interval in which every preview is sent again completely, to repair blocks the client lost without notice.
     *
     * @return the interval in seconds, 0 if previews are never resent completely
     */
    int previewResync();

    String language();
}
