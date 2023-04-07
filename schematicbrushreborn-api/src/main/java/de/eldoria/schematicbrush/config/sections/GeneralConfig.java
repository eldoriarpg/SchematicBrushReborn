/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface GeneralConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

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

    default boolean isOutOfRenderRange(Location origin, Location other) {
        if (origin.getWorld() != other.getWorld()) return false;
        return origin.distanceSquared(other) > renderDistanceSquared();
    }

    /**
     * The max effective render size. This is the size of a schematic excluding air blocks
     *
     * @return the effective render size in blocks
     */
    int maxEffectiveRenderSize();
}
