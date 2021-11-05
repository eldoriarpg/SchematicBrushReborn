/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import org.bukkit.entity.Player;

/**
 * Represents a paste mutation, which will be applied when the brush is pasted.
 */
public interface PasteMutation {
    /**
     * Clipboard of next paste.
     *
     * @return clipboard
     */
    Clipboard clipboard();

    /**
     * Transform of next paste
     *
     * @return transform
     */
    AffineTransform transform();

    /**
     * session of next paste
     *
     * @return session
     */
    EditSession session();

    /**
     * paste offset of next paste
     *
     * @return offset
     */
    BlockVector3 pasteOffset();

    /**
     * Include air
     *
     * @return true if air should be included
     */
    boolean isIncludeAir();

    /**
     * Set the clipboard of the paste
     *
     * @param clipboard clipboard
     */
    void clipboard(Clipboard clipboard);

    /**
     * Set the transform of the paste
     *
     * @param transform transform
     */
    void transform(AffineTransform transform);

    /**
     * Set the paste offset
     *
     * @param pasteOffset paste offset
     */
    void pasteOffset(BlockVector3 pasteOffset);

    /**
     * Set the include air
     *
     * @param includeAir includeair
     */
    void includeAir(boolean includeAir);

    /**
     * Get the actor of the paste.
     *
     * @return actor
     */
    default Actor actor() {
        return BukkitAdapter.adapt(player());
    }

    /**
     * Get the owner of the brush
     *
     * @return the owner
     */
    Player player();

    /**
     * Gets a parser context for the paste action
     * @return a new parser action
     */
    default ParserContext parserContext() {
        var parserContext = new ParserContext();
        parserContext.setActor(actor());
        parserContext.setExtent(clipboard());
        parserContext.setRestricted(true);
        parserContext.setPreferringWildcard(false);
        parserContext.setTryLegacy(true);
        parserContext.setWorld(session().getWorld());
        return parserContext;
    }
}
